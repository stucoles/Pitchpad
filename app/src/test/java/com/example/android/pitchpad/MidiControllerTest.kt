package com.example.android.pitchpad

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class MidiSendTest {
    @Test
    fun middleC_on_fullVelocity_isCorrect() {
        val correctArray = ByteArray(3)
        correctArray[0] = (-112).toByte()
        correctArray[1] = 60.toByte()
        correctArray[2] = 127.toByte()

        val receivedArray = MidiEvent(MidiEvent.TYPE_NOTE_ON,0,60,127).sendable

        assertEquals(receivedArray.size, correctArray.size)
        for(i in correctArray.indices){
            assert(receivedArray[i] == correctArray[i])
        }

    }

    @Test
    fun middleC_on_medVelocity_isCorrect() {
        val correctArray = ByteArray(3)
        correctArray[0] = (-112).toByte()
        correctArray[1] = 60.toByte()
        correctArray[2] = 64.toByte()

        val receivedArray = MidiEvent(MidiEvent.TYPE_NOTE_ON,0,60,64).sendable

        assertEquals(receivedArray.size, correctArray.size)
        for(i in correctArray.indices){
            assert(receivedArray[i] == correctArray[i])
        }
    }

    @Test
    fun middleC_on_zeroVelocity_isCorrect() {
        val correctArray = ByteArray(3)
        correctArray[0] = (-112).toByte()
        correctArray[1] = 60.toByte()
        correctArray[2] = 0.toByte()

        val receivedArray = MidiEvent(MidiEvent.TYPE_NOTE_ON,0,60,0).sendable

        assertEquals(receivedArray.size, correctArray.size)
        for(i in correctArray.indices){
            assert(receivedArray[i] == correctArray[i])
        }
    }

    @Test
    fun middleC_on_channel1_isCorrect() {
        val correctArray = ByteArray(3)
        correctArray[0] = (-111).toByte()
        correctArray[1] = 60.toByte()
        correctArray[2] = 127.toByte()

        val receivedArray = MidiEvent(MidiEvent.TYPE_NOTE_ON,1,60,127).sendable

        assertEquals(receivedArray.size, correctArray.size)
        for(i in correctArray.indices){
            assert(receivedArray[i] == correctArray[i])
        }
    }

    @Test
    fun middleC_on_channel15_isCorrect() {
        val correctArray = ByteArray(3)
        correctArray[0] = (-97).toByte()
        correctArray[1] = 60.toByte()
        correctArray[2] = 127.toByte()

        val receivedArray = MidiEvent(MidiEvent.TYPE_NOTE_ON,15,60,127).sendable

        assertEquals(receivedArray.size, correctArray.size)
        for(i in correctArray.indices){
            assert(receivedArray[i] == correctArray[i])
        }
    }


    @Test
    fun pitch_bend_zero_is_correct() {
        val correctArray = ByteArray(3)
        correctArray[0] = (-32).toByte()
        correctArray[1] = 0.toByte()
        correctArray[2] = 0x40.toByte()

        val receivedArray = MidiEvent(MidiEvent.TYPE_PITCH_BEND, 0, 0, 64).sendable

        assertEquals(receivedArray.size, correctArray.size)
        for(i in correctArray.indices){
            assert(receivedArray[i] == correctArray[i])
        }
    }

    @Test
    fun pitch_bend_max_is_correct() {
        val correctArray = ByteArray(3)
        correctArray[0] = (-32).toByte()
        correctArray[1] = 127.toByte()
        correctArray[2] = 127.toByte()

        val receivedArray = MidiEvent(MidiEvent.TYPE_PITCH_BEND, 0, 127, 127).sendable

        assertEquals(receivedArray.size, correctArray.size)
        for(i in correctArray.indices){
            assert(receivedArray[i] == correctArray[i])
        }
    }

    @Test
    fun pitch_bend_min_is_correct() {
        val correctArray = ByteArray(3)
        correctArray[0] = (-32).toByte()
        correctArray[1] = 127.toByte()
        correctArray[2] = 127.toByte()

        val receivedArray = MidiEvent(MidiEvent.TYPE_PITCH_BEND, 0, 0, 0).sendable

        assertEquals(receivedArray.size, correctArray.size)
        for(i in correctArray.indices){
            assert(receivedArray[i] == correctArray[i])
        }
    }


}