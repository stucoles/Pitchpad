package com.example.android.pitchpad

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //assign switch to a value to later give it an action
        val customSwitch = findViewById<Switch>(R.id.customC)

        val pitchBar = findViewById<SeekBar>(R.id.seekBar)

        val pitchText = findViewById<TextView>(R.id.pitchText)

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
                customSwitch.isClickable = false
                pitchBar.isClickable = false
            }

            //assign actions to switches
            customSwitch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    customMidiController.sendNoteOn(60, 127)
                } else {
                    customMidiController.sendNoteOff(60)
                }
            }

            //assign pitch send
            pitchBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(seekBar: SeekBar, amount: Int, fromUser: Boolean) {
                    // set pitch bend based on the seek bar position, given by i
                    customMidiController.sendPitchBend(amount)
                    pitchText.text = "Pitch bend: $amount"

                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    //do nothing
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    //reset pitch bend to zero
                    customMidiController.sendPitchBend(0)
                    pitchBar.progress = 0
                    pitchText.text = "Pitch bend"
                    //Toast.makeText(applicationContext,"stop tracking",Toast.LENGTH_SHORT).show()
                }
            })

        } else {
            Snackbar.make(
                findViewById(R.id.rootLayout),
                "Your device does not seem to support MIDI. Sorry :(",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }
}
