package com.example.android.pitchpad

import android.os.Bundle
import android.util.Log
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {

    var modHighResolution = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.v("MainActivity.onCreate", "onCreate run")

        try {
            //assign switch to a value to later give it an action
            val customSwitch = findViewById<Switch>(R.id.customC)
//
//            val modBar = findViewById<SeekBar>(R.id.modBar)
//
//            val modButton = findViewById<ToggleButton>(R.id.modToggle)

            val model = ViewModelProvider(this).get(MidiControllerViewModel::class.java)

            val modWheel = GenericParameterFragment()

//            modButton.setOnCheckedChangeListener { _, isChecked ->
//                if (isChecked) {
//                    modBar.max = 16384
//                    modHighResolution = true;
//                } else {
//                    modBar.max = 128
//                    modHighResolution = false;
//                }
//            }

            modWheel.arguments = modWheel.makeGenericBundle("Modulation", 1)

            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.add(R.id.frameToHoldModulation, modWheel)
            fragmentTransaction.commit()

            model.midiEnabledSuccessfully.observe(
                this,
                Observer { midiEnabledSuccessfully ->
                    if (midiEnabledSuccessfully) {
                        customSwitch.setOnCheckedChangeListener { _, isChecked ->
                            if (isChecked) {
                                model.customMidiController.sendNoteOn(60, 127)
                            } else {
                                model.customMidiController.sendNoteOff(60)
                            }
                        }

//                        modBar.setOnSeekBarChangeListener(object :
//                            SeekBar.OnSeekBarChangeListener {
//
//                            override fun onProgressChanged(
//                                seekBar: SeekBar,
//                                amount: Int,
//                                fromUser: Boolean
//                            ) {
//                                // set pitch bend based on the seek bar position, given by amount
//                                model.customMidiController.sendModBend(modHighResolution, amount)
//                                modText.text = "Modulation: $amount"
//
//                            }
//
//                            override fun onStartTrackingTouch(seekBar: SeekBar) {
//                                //do nothing
//                            }
//
//                            override fun onStopTrackingTouch(seekBar: SeekBar) {
//                                //reset pitch bend to zero
//                                model.customMidiController.sendModBend(modHighResolution, 0)
//                                modBar.progress = 0
//                                modText.text = "Modulation"
//                            }
//                        })

                    } else {
                        Snackbar.make(
                            findViewById(R.id.rootLayout),
                            "There are currently no MIDI devices connected",
                            Snackbar.LENGTH_LONG
                        ).show()
                        customSwitch.isClickable = false
                    }

                })

        } catch (e: Exception) {
            Log.i("init", e.message)
        }

    }


}
