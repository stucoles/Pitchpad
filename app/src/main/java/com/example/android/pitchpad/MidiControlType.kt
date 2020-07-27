package com.example.android.pitchpad

//A data class representing a Midi control we may want to modify
data class MidiControlType(val name: String, val controlNumber: Int, val channel: Int = 0, val highResolution: Boolean = false, val startsAtHalf: Boolean = false)