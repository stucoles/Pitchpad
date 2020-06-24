package com.example.android.pitchpad

import android.app.Application
import android.content.pm.PackageManager
import android.media.midi.MidiDeviceInfo
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


//implement a view model to allow the midi controller to persist across changes in state
class MidiControllerViewModel(application: Application) : AndroidViewModel(application) {

    val customMidiController = CustomMidiController(application)

    private val _attachedDevices = MutableLiveData<List<MidiDeviceInfo>>()

    val attachedDevices
        get(): LiveData<List<MidiDeviceInfo>> {
            _attachedDevices.value = customMidiController.attachedDevices
            return _attachedDevices
        }

    //enable quick monitoring of the number of devices available
    private val _numAttachedDevices = MutableLiveData<Int>();
    val numAttachedDevices
        get(): LiveData<Int>{
            _numAttachedDevices.value = customMidiController.attachedDevices.size;
            return _numAttachedDevices
        }

    //give a method of communicating to the UI whether MIDI is available
    private val _midiEnabledSuccessfully = MutableLiveData<Boolean>()
    val midiEnabledSuccessfully: LiveData<Boolean>
        get() = _midiEnabledSuccessfully

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
}
