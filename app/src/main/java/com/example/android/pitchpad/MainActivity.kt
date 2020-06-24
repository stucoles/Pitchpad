package com.example.android.pitchpad

import android.media.midi.MidiDeviceInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

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

            val sourcesSpinner = findViewById<Spinner>(R.id.midiInputSpinner)


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


                        val sourcesAdapter =
                            ArrayAdapter<MidiDeviceInfo>(this, android.R.layout.simple_spinner_item)
                        for (device in model.attachedDevices.value!!) {
                            //TODO: find a way to clean up the textt shown in the spinner to make it readable
                            sourcesAdapter.add(device)
                        }
                        sourcesSpinner.adapter = sourcesAdapter
                        sourcesSpinner.onItemSelectedListener = this@MainActivity


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

    //TODO: make these actually select the correct MIDI item
    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        Log.i("spinner", "selected ${parent.getItemAtPosition(pos)}")
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
        Log.i("spinner", "nothing selected")
    }
}
