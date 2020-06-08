package com.example.android.pitchpad

import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


//implement a view model to allow the midi controller to persist across changes in state
class MidiControllerViewModel(application: Application) : AndroidViewModel(application) {

    val customMidiController = CustomMidiController(application)

    //give a method of communicating to the UI whether MIDI is available
    private val _midiEnabledSuccessfully = MutableLiveData<Boolean>()
    val midiEnabledSuccessfully : LiveData<Boolean>
        get() = _midiEnabledSuccessfully

    private val _attachedDevicesExist = MutableLiveData<Boolean>()
    val attachedDevicesExist : LiveData<Boolean>
        get() = _attachedDevicesExist

    init {
        Log.v("MidiControllerViewModel", "initializing MidiControllerViewModel")

        if(!application.packageManager.hasSystemFeature(PackageManager.FEATURE_MIDI)){
            throw NoMidiSupportException()
        }

        _midiEnabledSuccessfully.value = false
        try {
            customMidiController.open()
            _midiEnabledSuccessfully.value = true
            _attachedDevicesExist.value = true
        }
        catch (e: AttachedDeviceException){
            _midiEnabledSuccessfully.value = false
            _attachedDevicesExist.value = false
        }


    }






}