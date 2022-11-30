package com.example.myble

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.myble.ble.ConnectionManager
import com.example.myble.ble.ConnectionEventListener
import com.example.myble.databinding.ActivityMainBinding
import com.example.myble.databinding.FragmentBleOperationBinding

private const val ENABLE_BLUETOOTH_REQUEST_CODE = 1
private const val LOCATION_PERMISSION_REQUEST_CODE = 2

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
//    private lateinit var getResult: ActivityResultLauncher<Intent>

    private val fragBleOperation = BleOperationFrag()

    /*******************************************
     * Properties
     *******************************************/

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    private var isScanning = false
        set(value) {
            field = value
            runOnUiThread { binding.scanButton.text = if (value) "Stop Scan" else "Start Scan" }
        }

    private val scanResults = mutableListOf<ScanResult>()
    private val scanResultAdapter: ScanResultAdapter by lazy {
        ScanResultAdapter(scanResults) { result ->
            if (isScanning) {
                stopBleScan()
            }
            with(result.device) {
                println("Connecting to $address")
                ConnectionManager.connect(this, this@MainActivity)
            }
        }
    }

    private val isLocationPermissionGranted
        get() = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)

    /*******************************************
     * Activity function overrides
     *******************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.scanButton.setOnClickListener {
            if(isScanning) {
                stopBleScan()
            }
            else {
                startBleScan()
            }
            setupRecyclerView()
        }

        binding.exitBtn.setOnClickListener{
            println("Finish app")
            finish()
        }

    }

    override fun onResume() {
        super.onResume()
//        checkSupportBLE()
        ConnectionManager.registerListener(connectionEventListener)
        if (!bluetoothAdapter.isEnabled) {
            promptEnableBluetooth()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ENABLE_BLUETOOTH_REQUEST_CODE -> {
                if (resultCode != Activity.RESULT_OK) {
                    promptEnableBluetooth()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.firstOrNull() == PackageManager.PERMISSION_DENIED) {
                    requestLocationPermission()
                } else {
                    startBleScan()
                }
            }
        }
    }

    /*******************************************
     * Private functions
     *******************************************/

//    private fun checkSupportBLE() {
//        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
//            Toast.makeText(this, "Not support BLE", Toast.LENGTH_LONG).show()
//        }
//        else {
//            Toast.makeText(this, "Support BLE", Toast.LENGTH_LONG).show()
//        }
//    }

    private fun promptEnableBluetooth() {
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH_REQUEST_CODE)
        }
    }

    private fun startBleScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isLocationPermissionGranted) {
            requestLocationPermission()
        } else {
            scanResults.clear()
            scanResultAdapter.notifyDataSetChanged()
            bleScanner.startScan(null, scanSettings, scanCallback)
            isScanning = true
        }
    }

    private fun stopBleScan() {
        bleScanner.stopScan(scanCallback)
        isScanning = false
    }

    private fun requestLocationPermission() {
        if (isLocationPermissionGranted) {
            return
        }
        runOnUiThread {
            val builder = AlertDialog.Builder(this)
                .setTitle("Location permission required")
                .setMessage("Starting from Android M (6.0), the system requires apps to be granted" +
                        "location access in order to scan for BLE devices.")
                .setCancelable(false)
//                .setPositiveButton(android.R.string.ok, locationPermissionReqPositive)
                .setPositiveButton(android.R.string.ok) {_, i ->
                    println("===================================")
                    println("locationPermissionReqPositive")
//                    requestPermission(Manifest.permission.BLUETOOTH_SCAN, 2)
//                    requestPermission(Manifest.permission.BLUETOOTH_CONNECT, 3)
                    requestPermission(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        LOCATION_PERMISSION_REQUEST_CODE
                    )
                }
                .show()



//            alert {
//                title = "Location permission required"
//                message = "Starting from Android M (6.0), the system requires apps to be granted " +
//                        "location access in order to scan for BLE devices."
//                isCancelable = false
//                positiveButton(android.R.string.ok) {
//                    requestPermission(
//                        Manifest.permission.ACCESS_FINE_LOCATION,
//                        LOCATION_PERMISSION_REQUEST_CODE
//                    )
//                }
//            }.show()
        }
    }

    val locationPermissionReqPositive = { dialoginterface: DialogInterface, i: Int ->
        println("===================================")
        println("locationPermissionReqPositive")
        requestPermission(
            Manifest.permission.ACCESS_FINE_LOCATION,
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun setupRecyclerView() {
        binding.scanResultsRecyclerView.apply {
            adapter = scanResultAdapter
            layoutManager = LinearLayoutManager(
                this@MainActivity,
                RecyclerView.VERTICAL,
                false
            )
            isNestedScrollingEnabled = false
        }

        val animator = binding.scanResultsRecyclerView.itemAnimator
        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }
    }

    /*******************************************
     * Callback bodies
     *******************************************/

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val indexQuery = scanResults.indexOfFirst { it.device.address == result.device.address }
//            println("======================================")
//            println(scanResults)
//            println(indexQuery)

            if (indexQuery != -1) { // A scan result already exists with the same address
                scanResults[indexQuery] = result
                scanResultAdapter.notifyItemChanged(indexQuery)
            } else {
                with(result.device) {
                    println("Found BLE device! Name: ${name ?: "Unnamed"}, address: $address")

                }
                if (binding.scanDeviceName.text.isNullOrEmpty()) {
                    scanResults.add(result)
                    scanResultAdapter.notifyItemInserted(scanResults.size - 1)
                } else {
                    if (isLocationPermissionGranted && result.device.name != null) {
                        if (result.device.name.contains(binding.scanDeviceName.text, ignoreCase = true))
                        {
                            scanResults.add(result)
                            scanResultAdapter.notifyItemInserted(scanResults.size - 1)
                        }
                    }
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            println("onScanFailed: code $errorCode")
        }
    }

    fun removeBleOperationLayout() {
        val transaction = supportFragmentManager.beginTransaction()
//        val frameLayout = supportFragmentManager.findFragmentById(R.id.frameBleOperation)
//        transaction.remove(frameLayout!!)
        transaction.remove(fragBleOperation)
        transaction.commit()
        ConnectionManager.registerListener(connectionEventListener)
    }

    private val connectionEventListener by lazy {
        ConnectionEventListener().apply {
            onConnectionSetupComplete = { gatt ->
                println("connectionEventListener : onConnectionSetupComplete ")
//                 Intent(this@MainActivity, BleOperationsActivity::class.java).also {
//                     it.putExtra(BluetoothDevice.EXTRA_DEVICE, gatt.device)
//                     startActivity(it)
//                 }

                val bundle = Bundle()
                bundle.putParcelable(BluetoothDevice.EXTRA_DEVICE, gatt.device)
                fragBleOperation.arguments = bundle

                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.frameBleOperation, fragBleOperation)

                transaction.addToBackStack(null)
                transaction.commit()

                ConnectionManager.unregisterListener(this)
            }
            onDisconnect = {
                runOnUiThread {
                    val builder = AlertDialog.Builder(this@MainActivity)
                        .setTitle("Disconnected")
                        .setMessage("Disconnected or unable to connect to device.")
                        .setCancelable(false)
                        .setPositiveButton("OK") {_, i ->
                            println("connectionEventListener : onDisconnect ")
                        }
                        .show()

//                    alert {
//                        title = "Disconnected"
//                        message = "Disconnected or unable to connect to device."
//                        positiveButton("OK") {}
//                    }.show()
                }
            }
        }
    }

    /*******************************************
     * Extension functions
     *******************************************/

    private fun Context.hasPermission(permissionType: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permissionType) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun Activity.requestPermission(permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
    }


}