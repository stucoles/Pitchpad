package com.example.android.pitchpad

import android.content.Context
import android.media.midi.MidiDeviceInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.midi_device_list_item.view.*


class MidiSpinnerAdapter(context: Context, data: List<MidiDeviceInfo>) :
    ArrayAdapter<MidiDeviceInfo>(context, 0, data) {
    override fun getView(position: Int, recycledView: View?, parent: ViewGroup): View {
        return this.createView(position, recycledView, parent)
    }

    override fun getDropDownView(position: Int, recycledView: View?, parent: ViewGroup): View {
        return this.createView(position, recycledView, parent)
    }

    private fun createView(position: Int, recycledView: View?, parent: ViewGroup): View {
        val entry = getItem(position)
        val view = recycledView ?: LayoutInflater.from(context).inflate(
            R.layout.midi_device_list_item,
            parent,
            false
        )

        val properties = entry?.properties;

        val manufacturer = properties?.getString(MidiDeviceInfo.PROPERTY_MANUFACTURER);
        val product = properties?.getString(MidiDeviceInfo.PROPERTY_PRODUCT);

        view.midiDeviceProperties.text = "$manufacturer - $product"
        return view
    }
}