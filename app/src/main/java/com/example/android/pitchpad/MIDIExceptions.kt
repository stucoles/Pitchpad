package com.example.android.pitchpad

class GenericMidiException(override val message: String = "generic Midi failure") : Exception()

class AttachedDeviceException(override val message: String = "there are currently no attached devices") : Exception()