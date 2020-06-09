package com.example.android.pitchpad

import android.app.Application
import android.content.pm.PackageManager
import android.media.midi.MidiDeviceInfo
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


//implement a view model to allow the midi controller to persist across changes in state
class MidiControllerViewModel(application: Application) : AndroidViewModel(application) {

    val customMidiController = CustomMidiController(application)

    private val _attachedDevices = MutableLiveData<List<MidiDeviceInfo>>()

    private val _message = MutableLiveData<String>()

    val message : LiveData<String>
        get() = _message

    val attachedDevices
        get(): LiveData<List<MidiDeviceInfo>> {
            _attachedDevices.value = customMidiController.attachedDevices
            return _attachedDevices
        }

    //give a method of communicating to the UI whether MIDI is available
    private val _midiEnabledSuccessfully = MutableLiveData<Boolean>()
    val midiEnabledSuccessfully: LiveData<Boolean>
        get() = _midiEnabledSuccessfully

    private val _attachedDevicesExist = MutableLiveData<Boolean>()
    val attachedDevicesExist: LiveData<Boolean>
        get() = _attachedDevicesExist

    init {
        Log.v("MidiControllerViewModel", "initializing MidiControllerViewModel")
        _message.value = "enabled midi"

        if (!application.packageManager.hasSystemFeature(PackageManager.FEATURE_MIDI)) {
            throw NoMidiSupportException()
        }

        _midiEnabledSuccessfully.value = false
        try {
            customMidiController.open()
            _midiEnabledSuccessfully.value = true
            _attachedDevicesExist.value = true
        } catch (e: AttachedDeviceException) {
            _midiEnabledSuccessfully.value = false
            _attachedDevicesExist.value = false
        }


    }


    override fun onCleared() {
        customMidiController.close()
        super.onCleared()
    }
}

class testViewModel : ViewModel(){
    var message = "ugh"
}