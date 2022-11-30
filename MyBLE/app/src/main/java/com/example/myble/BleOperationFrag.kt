package com.example.myble

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.myble.ble.*
import com.example.myble.databinding.EdittextHexPayloadBinding
import com.example.myble.databinding.FragmentBleOperationBinding
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "gattDevice"
// private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BleOperationFrag.newInstance] factory method to
 * create an instance of this fragment.
 */
class BleOperationFrag : Fragment() {
    // TODO: Rename and change types of parameters
//     private var param1: String? = null
//    private var param2: String? = null

    private var _binding:FragmentBleOperationBinding?=null
    private val binding get() = _binding!!
    private var mainActivity:MainActivity?=null

    private var _hexField : EdittextHexPayloadBinding?=null
    private val hexField get() = _hexField!!

    private var device: BluetoothDevice?=null

    private val dateFormatter = SimpleDateFormat("MMM d, HH:mm:ss", Locale.US)

    private var notifyingCharacteristics = mutableListOf<UUID>()


    private var characteristics :List<BluetoothGattCharacteristic> ?= listOf()

    private var characteristicProperties: Map<BluetoothGattCharacteristic, List<BleOperationFrag.CharacteristicProperty>> ?= mapOf()

    private var characteristicAdapter: CharacteristicAdapter ?= null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            device = it.getParcelable(BluetoothDevice.EXTRA_DEVICE)
            println("onCreate")
            println(it)
//            _device = it.getString(ARG_PARAM1)
//             param1 = it.getString(ARG_PARAM1)
//             param2 = it.getString(ARG_PARAM2)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_ble_operation, container, false)
        _binding = FragmentBleOperationBinding.inflate(inflater, container, false)

        ConnectionManager.registerListener(connectionEventListener)

        characteristics = ConnectionManager.servicesOnDevice(device!!)?.flatMap { service ->
            service.characteristics ?: listOf()
        }

        characteristicProperties = characteristics!!.map { characteristic ->
            characteristic to mutableListOf<CharacteristicProperty>().apply {
                if (characteristic.isNotifiable()) add(CharacteristicProperty.Notifiable)
                if (characteristic.isIndicatable()) add(CharacteristicProperty.Indicatable)
                if (characteristic.isReadable()) add(CharacteristicProperty.Readable)
                if (characteristic.isWritable()) add(CharacteristicProperty.Writable)
                if (characteristic.isWritableWithoutResponse()) {
                    add(CharacteristicProperty.WritableWithoutResponse)
                }
            }.toList()
        }.toMap()

        characteristicAdapter = CharacteristicAdapter(characteristics!!) { characteristic ->
            showCharacteristicOptions(characteristic)
        }

        setupRecyclerView()
        binding.requestMtuButton.setOnClickListener {
            if (binding.mtuField.text.isNotEmpty() && binding.mtuField.text.isNotBlank()) {
                binding.mtuField.text.toString().toIntOrNull()?.let { mtu ->
                    log("Requesting for MTU value of $mtu")
                    ConnectionManager.requestMtu(device!!, mtu)
                } ?: log("Invalid MTU value: ${binding.mtuField.text}")
            } else {
                log("Please specify a numeric value for desired ATT MTU (23-517)")
            }
            mainActivity!!.hideKeyboard()
        }

        binding.disconnectionBtn.setOnClickListener {
            mainActivity!!.removeBleOperationLayout()
        }

        return binding.root
    }

    override fun onDestroyView() {
        println("onDestroyView")
        ConnectionManager.unregisterListener(connectionEventListener)
        ConnectionManager.teardownConnection(device!!)

        super.onDestroyView()
        _binding = null
    }


    private fun setupRecyclerView() {
        binding.characteristicsRecyclerView.apply {
            adapter = characteristicAdapter
            layoutManager = LinearLayoutManager(
                mainActivity,
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

    @SuppressLint("SetTextI18n")
    private fun log(message: String) {
        val formattedMessage = String.format("%s: %s", dateFormatter.format(Date()), message)
        mainActivity!!.runOnUiThread {
            val currentLogText = if (binding.logTextView.text.isEmpty()) {
                "Beginning of log."
            } else {
                binding.logTextView.text
            }
            binding.logTextView.text = "$currentLogText\n$formattedMessage"
            binding.logScrollView.post { binding.logScrollView.fullScroll(View.FOCUS_DOWN) }
        }
    }

    private fun showCharacteristicOptions(characteristic: BluetoothGattCharacteristic) {
        characteristicProperties!![characteristic]?.let { properties ->
            selector("Select an action to perform", properties.map { it.action }) { _, i ->
                when (properties[i]) {
                    CharacteristicProperty.Readable -> {
                        log("Reading from ${characteristic.uuid}")
                        ConnectionManager.readCharacteristic(device!!, characteristic)
                    }
                    CharacteristicProperty.Writable, CharacteristicProperty.WritableWithoutResponse -> {
                        showWritePayloadDialog(characteristic)
                    }
                    CharacteristicProperty.Notifiable, CharacteristicProperty.Indicatable -> {
                        if (notifyingCharacteristics.contains(characteristic.uuid)) {
                            log("Disabling notifications on ${characteristic.uuid}")
                            ConnectionManager.disableNotifications(device!!, characteristic)
                        } else {
                            log("Enabling notifications on ${characteristic.uuid}")
                            ConnectionManager.enableNotifications(device!!, characteristic)
                        }
                    }
                }
            }
        }
    }

    private fun selector(
        title: CharSequence? = null,
        items: List<CharSequence>,
        onClick: (DialogInterface, Int) -> Unit
    ) {
        println("================selector===================")
        println(onClick)
        val builder = AlertDialog.Builder(mainActivity)
            .setTitle(title)
            .setItems(Array(items.size) { i -> items[i].toString() }, onClick)
            .show()
//                                    {
//        with(AlertDialog.Builder(this)) {
//            if (title != null) {
//                this.setTitle(title)
//            }
//            this.setItems(items, onClick)
//            this.show()
//        }
    }

    @SuppressLint("InflateParams")
    private fun showWritePayloadDialog(characteristic: BluetoothGattCharacteristic) {
//        hexField = EdittextHexPayloadBinding.inflate(layoutInflater, null, false)
        _hexField = EdittextHexPayloadBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(mainActivity)
            .setTitle("Write payload")
            .setCancelable(false)
            .setPositiveButton("Write") { _, _ ->
                with(hexField.writePayload.text.toString()) {
                    if (isNotBlank() && isNotEmpty()) {
                        val bytes = hexToBytes()
                        log("Writing to ${characteristic.uuid}: ${bytes.toHexString()}")
                        ConnectionManager.writeCharacteristic(device!!, characteristic, bytes)
                    } else {
                        log("Please enter a hex payload to write to ${characteristic.uuid}")
                    }
                }
            }
            .setNegativeButton("cancel") { _ , _ -> println(1111)}
            .setView(hexField.root)
//            .show()
        hexField.writePayload.showKeyboard()
        builder.show()
    }

    private val connectionEventListener:ConnectionEventListener =
        ConnectionEventListener().apply {
            onDisconnect = {
                mainActivity!!.runOnUiThread {
                    val builder = AlertDialog.Builder(mainActivity)  // ?
                        .setTitle("Disconnected")
                        .setMessage("Disconnected from device.")
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok, {DialogInterface, Int -> mainActivity!!.onBackPressed()})
                        .show()
                }
            }

            onCharacteristicRead = { _, characteristic ->
                log("Read from ${characteristic.uuid}: ${characteristic.value.toHexString()}")
            }

            onCharacteristicWrite = { _, characteristic ->
                log("Wrote to ${characteristic.uuid}")
            }

            onMtuChanged = { _, mtu ->
                log("MTU updated to $mtu")
            }

            onCharacteristicChanged = { _, characteristic ->
                log("Value changed on ${characteristic.uuid}: ${characteristic.value.toHexString()}")
            }

            onNotificationsEnabled = { _, characteristic ->
                log("Enabled notifications on ${characteristic.uuid}")
                notifyingCharacteristics.add(characteristic.uuid)
            }

            onNotificationsDisabled = { _, characteristic ->
                log("Disabled notifications on ${characteristic.uuid}")
                notifyingCharacteristics.remove(characteristic.uuid)
            }
        }


    private enum class CharacteristicProperty {
        Readable,
        Writable,
        WritableWithoutResponse,
        Notifiable,
        Indicatable;

        val action
            get() = when (this) {
                Readable -> "Read"
                Writable -> "Write"
                WritableWithoutResponse -> "Write Without Response"
                Notifiable -> "Toggle Notifications"
                Indicatable -> "Toggle Indications"
            }
    }

    private fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = mainActivity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun EditText.showKeyboard() {
        val inputMethodManager = mainActivity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        requestFocus()
        inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun String.hexToBytes() =
        this.chunked(2).map { it.toUpperCase(Locale.US).toInt(16).toByte() }.toByteArray()


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BleOperationFrag.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
            BleOperationFrag().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }
}