/*
 * Copyright 2019 Punch Through Design LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.myble

import android.bluetooth.BluetoothGattCharacteristic
import android.view.LayoutInflater
import android.view.View
//import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myble.ble.*
import com.example.myble.databinding.RowCharacteristicBinding


class CharacteristicAdapter(
    private val items: List<BluetoothGattCharacteristic>,
    private val onClickListener: ((characteristic: BluetoothGattCharacteristic) -> Unit)
) : RecyclerView.Adapter<CharacteristicAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = parent.context.layoutInflater.inflate(
//            R.layout.row_characteristic,
//            parent,
//            false
//        )
        val binding = RowCharacteristicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        val view = binding.root

        return ViewHolder(binding, onClickListener)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    class ViewHolder(
    //    private val view: View,
        val binding: RowCharacteristicBinding,
        private val onClickListener: ((characteristic: BluetoothGattCharacteristic) -> Unit)
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(characteristic: BluetoothGattCharacteristic) {
            val Uuid =  characteristic.uuid.toString().uppercase()
            binding.characteristicUuid.text = Uuid

            val listUuid = Uuid.split("-")
            var BaseUuid = listUuid[0].substring(0,4) + "XXXX"
            for (idx in 1 until listUuid.size) {
                BaseUuid += listUuid[idx]
            }

            val CharUuid = listUuid[0].substring(4)
            when (BaseUuid){
                BASE_BASIC ->{
                    when (CharUuid) {
                        BASIC_CHAR_DEVICE_NAME -> {binding.characteristicUuid.text = "DEVICE NAME"}
                        BASIC_CHAR_APPEARANCE -> {binding.characteristicUuid.text = "APPEARANCE"}
                        BASIC_CHAR_PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS -> {binding.characteristicUuid.text = "PERIPHERAL PREFERRED CONNECTION PARAMETERS"}
                        BASIC_CHAR_CENTRAL_ADDRESS_RESOLUTION -> {binding.characteristicUuid.text = "CENTRAL ADDRESS RESOLUTION"}
                        BASIC_CHAR_MANUFACTURER_NAME_STRING -> {binding.characteristicUuid.text = "MANUFACTURER NAME STRING"}
                        BASIC_CHAR_SOFTWARE_REVISION_STRING -> {binding.characteristicUuid.text = "SOFTWARE REVISION STRING"}
                        BASIC_CHAR_NORDIC_BUTTONLESS_DFU -> {binding.characteristicUuid.text = "NORDIC BUTTONLESS DFU"}
                        else -> {binding.characteristicUuid.text = "Unregistered BASIC CHAR " + Uuid}
                    }
                }
                else -> {
                    println("Custom characteristic")
                }
            }


            binding.characteristicProperties.text = characteristic.printProperties()
            binding.root.setOnClickListener { onClickListener.invoke(characteristic) }
        }
    }
}
