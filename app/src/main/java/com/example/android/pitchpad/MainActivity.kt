package com.example.android.pitchpad

import android.media.midi.MidiDeviceInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    var modHighResolution = false

    lateinit var model: MidiControllerViewModel;
    private lateinit var linearLayoutManager: GridLayoutManager
    private lateinit var adapter: GenericParameterFragmentRecyclerAdapter

    private var MidiList = arrayListOf<MidiControlType>(
        MidiControlType("Modulation", 1),
        MidiControlType("Breath", 2),
        MidiControlType("Portamento", 5),
        MidiControlType("Pan", 10, startsAtHalf = true)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.v("MainActivity.onCreate", "onCreate run")



        try {
            //assign switch to a value to later give it an action
            val customSwitch = findViewById<Switch>(R.id.customC)

            model = ViewModelProvider(this).get(MidiControllerViewModel::class.java)

            val sourcesSpinner = findViewById<Spinner>(R.id.midiInputSpinner)

            linearLayoutManager = GridLayoutManager(this, 2)
            recyclerView.layoutManager = linearLayoutManager
            adapter = GenericParameterFragmentRecyclerAdapter(MidiList, model)
            recyclerView.adapter = adapter


            model.midiEnabledSuccessfully.observe(
                this,
                Observer { midiEnabledSuccessfully ->
                    if (midiEnabledSuccessfully) {
                        customSwitch.setOnCheckedChangeListener { _, isChecked ->
                            if (isChecked) {
                                model.sendNoteOn(60, 127)
                            } else {
                                model.sendNoteOff(60)
                            }
                        }


                        val sourcesAdapter = MidiSpinnerAdapter(
                            this,
                            model.attachedDevices.value!!
                        )

                        //sourcesAdapter.addAll(model.customMidiController.attachedDevices.value!!)
                        sourcesSpinner.adapter = sourcesAdapter
                        sourcesSpinner.onItemSelectedListener = this@MainActivity

                        //set up listeners for connected midi devices to change
                        model.devicesChanged.observe(this, Observer { changed ->
                            if (changed) {
                                sourcesAdapter.clear()
                                sourcesAdapter.addAll(model.attachedDevices.value!!)
                                model.finishChangingDevices()
                            }
                        })


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

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        Log.i("spinner", "selected ${parent.getItemAtPosition(pos)}")
        model.openDevice(parent.getItemAtPosition(pos) as MidiDeviceInfo);
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
        Log.i("spinner", "nothing selected")
    }
}
