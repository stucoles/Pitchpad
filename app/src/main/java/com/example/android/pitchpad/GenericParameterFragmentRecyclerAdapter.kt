package com.example.android.pitchpad

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_generic_parameter.view.*

//TODO: refactor this? or remove it entirely
fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

class GenericParameterFragmentRecyclerAdapter(
    private val controls: ArrayList<MidiControlType>,
    private val midiControllerViewModel: MidiControllerViewModel
) : RecyclerView.Adapter<GenericParameterFragmentRecyclerAdapter.MidiControlHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GenericParameterFragmentRecyclerAdapter.MidiControlHolder {
        val inflatedView = parent.inflate(R.layout.fragment_generic_parameter, false)
        return MidiControlHolder(inflatedView, midiControllerViewModel)
    }

    override fun getItemCount() = controls.size

    override fun onBindViewHolder(
        holder: GenericParameterFragmentRecyclerAdapter.MidiControlHolder,
        position: Int
    ) {
        val itemParameter = controls[position]
        holder.bindMidiControl(itemParameter, midiControllerViewModel)
    }


    class MidiControlHolder(v: View, midiControllerViewModel: MidiControllerViewModel) :
        RecyclerView.ViewHolder(v), View.OnClickListener {
        //2
        private var view: View = v
        private var midiControlType: MidiControlType? = null

        private fun halfValue(midiControlType: MidiControlType): Int {
            return 50
        }

        fun bindMidiControl(
            midiControlType: MidiControlType,
            midiControllerViewModel: MidiControllerViewModel
        ) {
            //set the midi control type using the one passed in
            this.midiControlType = midiControlType;
            view.parameterText.text = midiControlType.name;
            view.genericWheel.setOnProgressChangeListener {
                //some log data
                Log.i("midiControlChange", "${midiControlType.name} set to: $it")
                //actually change the values
                midiControllerViewModel.sendControlChange(
                    midiControlType.controlNumber,
                    it,
                    midiControlType.channel,
                    midiControlType.highResolution
                )
            }

            view.genericWheel.setOnReleaseListener {
                if (midiControlType.startsAtHalf) {
                    view.genericWheel.progress = halfValue(midiControlType)
                } else {
                    view.genericWheel.progress = 0
                }
            }

            if (midiControlType.startsAtHalf) {
                view.genericWheel.progress = halfValue(midiControlType)
            }
            else{
                view.genericWheel.progress = 0
            }

        }

        //3
        init {
            //TODO: figure out what this code actually does
            v.setOnClickListener(this)
        }

        //4
        override fun onClick(v: View) {
            //this shouldn't really do anything
        }

        companion object {
            //5
            private val CONTROL_KEY = "MIDI_CONTROL"
        }
    }
}