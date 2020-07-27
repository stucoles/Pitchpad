package com.example.android.pitchpad

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.android.pitchpad.databinding.FragmentGenericParameterBinding


class GenericParameterFragment : Fragment() {

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }

    val viewModel : MidiControllerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //get values from bundle
        val parameterName = this.requireArguments().getString("name", "undefined parameter")
        val controlNumber = this.requireArguments().getInt("controlNum", 1)
        val highResolution = this.requireArguments().getBoolean("highResolution", false)
        val startsAtHalf = this.requireArguments().getBoolean("startsAtHalf", false)
        val channel= this.requireArguments().getInt("channel", 0)

        Log.i("GenericFragment", "Loading arguments from bundle: $parameterName, $controlNumber, $highResolution, $startsAtHalf, $channel")

        //set up the dataBinding and the viewModel
        val binding = FragmentGenericParameterBinding.inflate(layoutInflater, container, false)
        //val viewModel : MidiControllerViewModel by activityViewModels()
        binding.midiControllerModel = viewModel
        binding.executePendingBindings()

        val parameterText = binding.parameterText
        val genericWheel = binding.genericWheel

        if(viewModel.midiEnabledSuccessfully.value!!){

            //this library doesn't support negative progress, so we'll just split the value
            if(highResolution) {
                genericWheel.maxValue = 16384
            }
            else genericWheel.maxValue = 128
            val halfValue : Int = genericWheel.maxValue / 2

            if(startsAtHalf) {
                genericWheel.setOnProgressChangeListener { amount ->
                    val realAmount = amount - halfValue
                    viewModel.sendControlChange(controlNumber, realAmount, channel, highResolution)
                    parameterText.text = "$parameterName: $realAmount"
                }
                genericWheel.setOnReleaseListener {
                    genericWheel.progress = halfValue
                }
                //set the listeners first, then set the beginning value (0)
                genericWheel.progress = halfValue
            }
            else{
                genericWheel.setOnProgressChangeListener { amount ->
                    viewModel.sendControlChange(controlNumber, amount, channel, highResolution)
                    parameterText.text = "$parameterName: $amount"
                }
                genericWheel.setOnReleaseListener {
                    genericWheel.progress = 0
                }
                //set the listeners first, then set the beginning value (0)
                genericWheel.progress = 0
            }

        }
        // Inflate the layout for this fragment
        return binding.root
    }

    fun makeGenericBundle(
        name: String,
        controlNumber: Int,
        highResolution: Boolean = false,
        startsAtHalf: Boolean = false,
        channel: Int = 0
    ): Bundle {
        val bundleToReturn = Bundle()
        bundleToReturn.putString("name", name)
        bundleToReturn.putInt("controlNum", controlNumber)
        bundleToReturn.putBoolean("highResolution", highResolution)
        bundleToReturn.putBoolean("startsAtHalf", startsAtHalf)
        bundleToReturn.putInt("channel", channel)
        return bundleToReturn
    }

    fun makeGenericBundle(midiControlType: MidiControlType) : Bundle {
        val bundleToReturn = Bundle()
        bundleToReturn.putString("name", midiControlType.name)
        bundleToReturn.putInt("controlNum", midiControlType.controlNumber)
        bundleToReturn.putBoolean("highResolution", midiControlType.highResolution)
        bundleToReturn.putBoolean("startsAtHalf", midiControlType.startsAtHalf)
        bundleToReturn.putInt("channel", midiControlType.channel)
        return bundleToReturn
    }
}
