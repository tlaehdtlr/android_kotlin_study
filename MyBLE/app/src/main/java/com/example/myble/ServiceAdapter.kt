package com.example.myble

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.myble.ble.*
import com.example.myble.databinding.RowServiceBinding

class ServiceAdapter (
    private val items: List<BluetoothGattService>,
    private val onClickListenerSvc: ((service: BluetoothGattService) -> Unit),
    private val onClickListenerChar: ((characteristic: BluetoothGattCharacteristic) -> Unit)
    ) : RecyclerView.Adapter<ServiceAdapter.ViewHolder>() {

    private var _binding:RowServiceBinding?=null
    private val binding get() = _binding!!
    private var characteristics :List<BluetoothGattCharacteristic> ?= listOf()
    private var characteristicAdapter: CharacteristicAdapter ?= null


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        _binding = RowServiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        println("onCreateViewHolderonCreateViewHolder")

        return ViewHolder(binding, onClickListenerSvc)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        characteristics = item.characteristics
        characteristicAdapter = CharacteristicAdapter(characteristics!!) { characteristic ->
            onClickListenerChar(characteristic)
        }

        setupRecyclerView(binding)

        holder.bind(item)
    }

    fun setupRecyclerView(binding:RowServiceBinding) {
        binding.characteristicsRecyclerView.apply {
            adapter = characteristicAdapter
            layoutManager = LinearLayoutManager(
                binding.root.context,
                RecyclerView.VERTICAL,
                false
            )
            isNestedScrollingEnabled = false
        }

        val animator = binding.characteristicsRecyclerView.itemAnimator
        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }
    }


    class ViewHolder(
        val binding: RowServiceBinding,
        private val onClickListener: (service: BluetoothGattService) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(service:BluetoothGattService) {
            val Uuid = service.uuid.toString().uppercase()
            binding.serviceUuid.text = Uuid

            val listUuid = Uuid.split("-")
            var BaseUuid = listUuid[0].substring(0,4) + "XXXX"
            for (idx in 1 until listUuid.size) {
                BaseUuid += listUuid[idx]
            }

            val SvcUuid = listUuid[0].substring(4)
            when (BaseUuid){
                BASE_BASIC->{
                    when (SvcUuid) {
                        BASIC_SVC_GENERIC_ACCESS -> {binding.serviceUuid.text = "[BASIC SVC] GENERIC ACCESS"}
                        BASIC_SVC_GENERIC_ATTRIBUTE -> {binding.serviceUuid.text = "[BASIC SVC] GENERIC ATTRIBUTE"}
                        BASIC_SVC_DEVICE_INFORMATION -> {binding.serviceUuid.text = "[BASIC SVC] DEVICE INFORMATION"}
                        BASIC_SVC_NORDIC_4 -> {binding.serviceUuid.text = "[BASIC SVC] NORDIC 4"}
                        else -> {binding.serviceUuid.text = "Unregistered BASIC SVC " + Uuid}
                    }
                }
                else -> {
                    binding.serviceUuid.text = "[CUSTOM SVC] " + Uuid
                }
            }
            binding.root.setOnClickListener { onClickListener.invoke(service) }
        }

    }
}