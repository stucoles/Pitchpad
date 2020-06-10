package com.example.android.pitchpad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.android.pitchpad.databinding.FragmentPitchBendBinding

/**
 * A simple [Fragment] subclass.
 * Use the [PitchBendFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PitchBendFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //set up the dataBinding and the viewModel
        val binding = FragmentPitchBendBinding.inflate(layoutInflater, container, false)
        val viewModel : MidiControllerViewModel by activityViewModels();
        binding.midiControllerModel = viewModel
        binding.executePendingBindings()

        val pitchBar = binding.pitchBendSeekBar
        val pitchText = binding.pitchBendText

        if(viewModel.midiEnabledSuccessfully.value!!){
            pitchBar.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(
                    seekBar: SeekBar,
                    amount: Int,
                    fromUser: Boolean
                ) {
                    // set pitch bend based on the seek bar position, given by amount
                    viewModel.customMidiController.sendPitchBend(amount)
                    pitchText.text = "Pitch bend: $amount"

                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    //do nothing
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    //reset pitch bend to zero
                    viewModel.customMidiController.sendPitchBend(0)
                    pitchBar.progress = 0
                    pitchText.text = "Pitch bend"
                }
            })
        }
        // Inflate the layout for this fragment
        return binding.root
    }
}
