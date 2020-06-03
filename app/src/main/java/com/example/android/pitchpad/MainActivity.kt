package com.example.android.pitchpad

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        //assign switch to a value to later give it an action
        val customSwitch = findViewById<Switch>(R.id.customC)

        if (packageManager.hasSystemFeature(PackageManager.FEATURE_MIDI)) {
            // do MIDI stuff only if the device won't immediately reject it
            val customMidiController = CustomMidiController(this.application)

            //Open up the MIDI peripheral controller
            try {
                customMidiController.open()
            } catch (e: AttachedDeviceException) {
                Snackbar.make(
                    findViewById(R.id.rootLayout),
                    "No MIDI devices attached",
                    Snackbar.LENGTH_LONG
                ).show()
                customSwitch.isChecked = false
            }

            //assign actions to switches
            customSwitch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    customMidiController.sendNoteOn(60, 127)
                } else {
                    customMidiController.sendNoteOff(60)
                }
            }
        }

        else{
            Snackbar.make(
                findViewById(R.id.rootLayout),
                "Your device does not seem to support MIDI. Sorry :(",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }
}
