package com.example.android.pitchpad

class MidiEvent(
    private val eventType: Byte,
    private val eventChannel: Byte,
    private vararg val dataToSend: Byte
) {

    //contains all ya need to get a bit of data you can actually send out of the object.
    val sendable: ByteArray
        get(): ByteArray {
            //our array must be one bigger than the data we intend to send, as we need to append the
            //event type to the front of the data
            val toReturn = ByteArray(dataToSend.size + 1)
            //this is code to combine the event type and channel number according to the MIDI spec
            //recall that a channel voice message is encoded as (for example) 10010000 for a
            //note on (1001) on channel 1 (encoded 0000). Assuming that whoever called this function
            //uses my conveniently-provided types, this should work.
            toReturn[0] = (eventType + eventChannel).toByte()

            for (index in 1 until toReturn.size) {
                toReturn[index] = dataToSend[index - 1]
            }
            return toReturn
        }

    companion object {
        const val TYPE_NOTE_ON: Byte = 0x90.toByte()
        const val TYPE_NOTE_OFF: Byte = 0x80.toByte()
        const val TYPE_POLYPHONIC_AFTERTOUCH: Byte = 0xA0.toByte()
        const val TYPE_CONTROL_CHANGE: Byte = 0xB0.toByte()
        const val TYPE_PROGRAM_CHANGE: Byte = 0xC0.toByte()
        const val TYPE_CHANNEL_AFTERTOUCH = 0xD0.toByte()
        const val TYPE_PITCH_BEND = 0xE0.toByte()

    }
}