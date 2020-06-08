package com.example.android.pitchpad

import android.content.Context
import android.media.midi.MidiDevice
import android.media.midi.MidiDeviceInfo
import android.media.midi.MidiInputPort
import android.media.midi.MidiManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlin.math.max
import kotlin.math.min

class CustomMidiController(context: Context) {

    //this class needs a Handler
    private val handler: Handler by lazy { Handler(Looper.getMainLooper()) }

    //we need a midiManager object and some variables to store the data it creates
    private val midiManager = context.getSystemService(Context.MIDI_SERVICE) as MidiManager

    //we store the MIDI devices attached to allow the user to select them
    private val _attachedDevices = MutableLiveData<List<MidiDeviceInfo>>()
    val attachedDevices: LiveData<List<MidiDeviceInfo>>
        get() = _attachedDevices

    //the inputPort is actually the port that we send data down. The thing at the other end receives
    //the input.
    private var inputPort: MidiInputPort? = null

    //TODO: get an output port going once this setup works to make the whole thing controllable from the outside

    //the midiDevice assists in sending data. it has the ports
    private var midiDevice: MidiDevice? = null

    //on creation of this object, we must find attached midi devices
    init {
        _attachedDevices.value = midiManager.devices.toList()
    }

    fun open() {
        //first of all, we want the app to seize control, so we close any open midi connections on the ports we hope to use
        close()

        //check for attached devices
        if (attachedDevices.value.isNullOrEmpty()) {
            throw AttachedDeviceException()
        }

        //if there are attached devices, we should be able to choose one and open up communication.
        //for now, it will just use the first one for simplicity of code while I get a proof of concept
        //up and running.
        //TODO: allow for choosing input device

        val deviceInfo = _attachedDevices.value!!.first()
        val properties = deviceInfo.properties

        //TODO: convert all log statements to Timber. this is untenable.
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
            midiDevice = it
            inputPort = it.openInputPort(deviceInfo.ports.first().portNumber)
        }, handler)

    }


    //This function closes all connections. Make sure to use this when you're done w/ MIDI
    fun close() {
        inputPort?.close()
        midiDevice?.close()
        inputPort = null
        midiDevice = null
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

        Log.i("sendPitchBend", "pitch bend intensity set to $intensity")

    }

    //TODO: implement sendModBend
    fun sendModBend(intensity: Int, channel: Int = 0) {
        TODO("sendModBend should be similar to pitch bending, but the MIDI packet structure is different")
    }

    //TODO: implement generic controller send
    fun sendValueChange(controlNumber: Int, intensity: Int, channel: Int = 0) {
        TODO("This would sort of be the holy grail")
    }

}