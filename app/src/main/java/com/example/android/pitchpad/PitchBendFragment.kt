package com.example.android.pitchpad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.android.pitchpad.databinding.FragmentPitchBendBinding


class PitchBendFragment : Fragment() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //set up the dataBinding and the viewModel
        val binding = FragmentPitchBendBinding.inflate(layoutInflater, container, false)
        val viewModel : MidiControllerViewModel by activityViewModels()
        binding.midiControllerModel = viewModel
        binding.executePendingBindings()

        val pitchText = binding.pitchBendText
        val pitchWheel = binding.pitchBendWheel

        if(viewModel.midiEnabledSuccessfully.value!!){

            //this library doesn't support negative progress, so we'll just split the value
            pitchWheel.maxValue = 16384
            val halfValue : Int = pitchWheel.maxValue / 2

            pitchWheel.setOnProgressChangeListener { amount ->
                val realAmount = amount - 8192
                viewModel.sendPitchBend(realAmount)
                pitchText.text = "Pitch bend: $realAmount"
            }

            pitchWheel.setOnReleaseListener {
                pitchWheel.progress = halfValue
            }

            //set the listeners first, then set the beginning value (0)
            pitchWheel.progress = halfValue

        }
        // Inflate the layout for this fragment
        return binding.root
    }
}
