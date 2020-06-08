package com.example.android.pitchpad

open class MidiException(override val message: String = "generic Midi failure") : Exception()

class AttachedDeviceException(override val message: String = "there are currently no attached devices") : MidiException()

class NoMidiSupportException(override val message: String = "The current device does not support android MIDI. Sorry.") : MidiException()