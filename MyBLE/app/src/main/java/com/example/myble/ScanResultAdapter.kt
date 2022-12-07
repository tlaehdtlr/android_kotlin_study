package com.example.myble

import android.bluetooth.le.ScanResult
import android.view.LayoutInflater
//import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myble.databinding.RowScanResultBinding

class ScanResultAdapter (
    private val items: List<ScanResult>,
    private val onClickListener: ((device: ScanResult) -> Unit)
    ) : RecyclerView.Adapter<ScanResultAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = parent.context.layoutInflater.inflate(
//            R.layout.row_scan_result,
//            parent,
//            false
//        )
        val binding = RowScanResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onClickListener)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    class ViewHolder(
//        private val view: View,
        val binding: RowScanResultBinding,
        private val onClickListener: ((device: ScanResult) -> Unit)
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(result: ScanResult) {
            binding.deviceName.text = result.device.name ?: "Unnamed"
            binding.macAddress.text = result.device.address
            binding.signalStrength.text = "${result.rssi} dBm"
            binding.root.setOnClickListener { onClickListener.invoke(result) }
        }
    }
}
