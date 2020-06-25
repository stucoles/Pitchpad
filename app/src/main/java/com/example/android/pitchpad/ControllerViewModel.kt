package com.example.android.pitchpad

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.media.midi.MidiDevice
import android.media.midi.MidiDeviceInfo
import android.media.midi.MidiInputPort
import android.media.midi.MidiManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlin.math.max
import kotlin.math.min


//implement a view model to allow the midi controller to persist across changes in state
class MidiControllerViewModel(application: Application) : AndroidViewModel(application) {

    val customMidiController = CustomMidiController(application)

    //give a method of communicating to the UI whether MIDI is available
    private val _midiEnabledSuccessfully = MutableLiveData<Boolean>()
    val midiEnabledSuccessfully: LiveData<Boolean>
        get() = _midiEnabledSuccessfully

    //listen for a change in available devices
    private val _devicesChanged = MutableLiveData<Boolean>();
    val devicesChanged
        get(): LiveData<Boolean> = _devicesChanged

    fun finishChangingDevices(){
        _devicesChanged.value = false;
    }

    init {
        Log.v("MidiControllerViewModel", "initializing MidiControllerViewModel")

        if (!application.packageManager.hasSystemFeature(PackageManager.FEATURE_MIDI)) {
            throw NoMidiSupportException()
        }

        _midiEnabledSuccessfully.value = false
        try {
            customMidiController.open()
            _midiEnabledSuccessfully.value = true
        } catch (e: Exception) {
            Log.e("controllerViewModelinit", e.toString())
            _midiEnabledSuccessfully.value = false
        }


    }

    override fun onCleared() {
        customMidiController.close()
        super.onCleared()
    }

    //we'll do most of our work through this customMidiController object
    //TODO: refactor to make this class private. There's no need for it to be exposed to the other classes
    inner class CustomMidiController(context: Context) {

        //this class needs a Handler
        private val handler: Handler by lazy { Handler(Looper.getMainLooper()) }

        private val deviceCallback = object : MidiManager.DeviceCallback(){
            override fun onDeviceAdded(device: MidiDeviceInfo?) {
                _attachedDevices.value = midiManager.devices.toList()
                _devicesChanged.value = true;
            }

            override fun onDeviceRemoved(device: MidiDeviceInfo?) {
                _devicesChanged.value = true;
                if (device === activeMidiDevice?.info){
                    close()
                }
                _attachedDevices.value = midiManager.devices.toList()
            }
        }

        //we need a midiManager object and some variables to store the data it creates
        private val midiManager = context.getSystemService(Context.MIDI_SERVICE) as MidiManager

        private val _attachedDevices = MutableLiveData<List<MidiDeviceInfo>>();

        //we store the MIDI devices attached to allow the user to select them
        val attachedDevices
            get(): LiveData<List<MidiDeviceInfo>> = _attachedDevices;

        //enable quick monitoring of the number of devices available
        private val _numAttachedDevices = MutableLiveData<Int>();
        val numAttachedDevices
            get(): LiveData<Int>{
                _numAttachedDevices.value = _attachedDevices.value!!.size;
                return _numAttachedDevices
            }

        //the inputPort is actually the port that we send data down. The thing at the other end receives
        //the input.
        private var inputPort: MidiInputPort? = null

        //TODO: get an output port going once this setup works to make the whole thing controllable from the outside

        //the midiDevice assists in sending data. it has the ports
        private var activeMidiDevice: MidiDevice? = null

        //on creation of this object, we must find attached midi devices
        init {
            _attachedDevices.value = midiManager.devices.toList()

            //set it up so that it handles when devices are plugged and unplugged somewhat gracefully
            midiManager.registerDeviceCallback(deviceCallback, handler)
        }

        //if no device is given, just use the first device in the list of attached devices
        fun open(){
            //check for attached devices
            if (attachedDevices.value.isNullOrEmpty()) {
                throw AttachedDeviceException()
            }

            //we know it's non-null because we checked above, so this is fine
            open(attachedDevices.value!!.first())
        }

        fun open(deviceInfo: MidiDeviceInfo) {
            //first of all, we want the app to seize control, so we close any open midi connections on the ports we hope to use
            close()
            //if there are attached devices, we should be able to choose one and open up communication.
            //for now, it will just use the first one for simplicity of code while I get a proof of concept
            //up and running.
            val properties = deviceInfo.properties

            Log.i(
                "midiHandler init",
                "there are " + deviceInfo.inputPortCount + " ports available for input"
                        + "\n The device of choice is a ${properties.getString(MidiDeviceInfo.PROPERTY_PRODUCT)} from ${properties.getString(
                    MidiDeviceInfo.PROPERTY_MANUFACTURER
                )}"
            )


            //now we will actually open up the midi device
            //TODO: update for case where the first port is not an input port
            midiManager.openDevice(deviceInfo, {
                activeMidiDevice = it
                inputPort = it.openInputPort(deviceInfo.ports.first().portNumber)
            }, handler)
        }


        //This function closes all connections. Make sure to use this when you're done w/ MIDI
        fun close() {
            inputPort?.close()
            activeMidiDevice?.close()
            inputPort = null
            activeMidiDevice = null
        }


        private fun send(data: MidiEvent) {
            inputPort?.send(data.sendable, 0, data.sendable.size)
        }

        fun sendNoteOn(noteNumber: Int, velocity: Int, channel: Int = 0) {
            send(
                MidiEvent(
                    MidiEvent.TYPE_NOTE_ON,
                    channel.toByte(),
                    noteNumber.toByte(),
                    velocity.toByte()
                )
            )
        }

        fun sendNoteOff(noteNumber: Int, channel: Int = 0) {
            //I am using a velocity of 0 for note-off so that running state can be implemented in the future
            send(MidiEvent(MidiEvent.TYPE_NOTE_ON, channel.toByte(), noteNumber.toByte(), 0.toByte()))
        }

        //Requires: nothing
        //Modifies: midimanager
        //Effects: changes the pitch bend intensity. Takes in an int between -8192 and 8192 and sends
        //the matching pitch bend value in binary. If the int passed in is outside the range, it defaults
        //to the maximum or minimum, depending.
        fun sendPitchBend(intensity: Int, channel: Int = 0) {

            //Per the official spec, MIDI pitch bend messages are sent as two seven-bit numbers
            // prefixed with zeros, together forming 14 bits of resolution. This means that the first
            // data byte, the "most significant bit", contains the fine-detail information,
            // and its the second data byte that handles more coarse pitch control.

            var unsignedIntensity = intensity + 8192
            unsignedIntensity = min(unsignedIntensity, 16383)
            unsignedIntensity = max(unsignedIntensity, 0)

            val smallPart = unsignedIntensity % 128
            val bigPart = unsignedIntensity - smallPart

            //the small part will be some number under 128, so we can write that directly to a seven-bit
            //digit
            val firstDataByte = smallPart.toByte()

            //this part takes the rest of the data and right shifts the binary representation to fit
            //in our seven-bit representation. i really don't know who decided seven-bit was a good idea,
            //but whatever.
            val secondDataByte = bigPart.shr(7).toByte()

            //actually send the bytes!
            send(
                MidiEvent(
                    MidiEvent.TYPE_PITCH_BEND,
                    channel.toByte(),
                    firstDataByte,
                    secondDataByte
                )
            )

            //Log.i("sendPitchBend", "pitch bend intensity set to $intensity")

        }

        //Sends a control change message
        fun sendControlChange(
            controlNumber: Int,
            intensity: Int,
            channel: Int = 0,
            highResolution: Boolean = false
        ) {
            //if the value is guaranteed between 0 and 128, this works
            if (!highResolution) {
                send(
                    MidiEvent(
                        MidiEvent.TYPE_CONTROL_CHANGE,
                        channel.toByte(),
                        controlNumber.toByte(),
                        intensity.toByte()
                    )
                )
            } else {
                //break the value into two bits
                val smallPart = intensity % 128
                val bigPart = intensity - smallPart

                //the small part will be some number under 128, so we can write that directly to a seven-bit
                //digit
                val leastSignificantByte = smallPart.toByte()

                //this part takes the rest of the data and right shifts the binary representation to fit
                //in our seven-bit representation. i really don't know who decided seven-bit was a good idea,
                //but whatever.
                val mostSignificantByte = bigPart.shr(7).toByte()

                //most significant bit
                send(
                    MidiEvent(
                        MidiEvent.TYPE_CONTROL_CHANGE,
                        channel.toByte(),
                        controlNumber.toByte(),
                        mostSignificantByte
                    )
                )

                //least significant bit, which, for the low level bits where these are assigned, is is 32 higher on the list of value
                //than the most significant bit
                send(
                    MidiEvent(
                        MidiEvent.TYPE_CONTROL_CHANGE,
                        channel.toByte(),
                        (controlNumber + 32).toByte(),
                        leastSignificantByte
                    )
                )

            }

        }

        //Mod bend is a pretty common parameter, so I felt it deserved its own function, but this is
        //really just a wrapper for sendValueChange
        fun sendModBend(highResolution: Boolean, intensity: Int, channel: Int = 0) {
            sendControlChange(1, intensity, channel, highResolution)
        }
    }

}
