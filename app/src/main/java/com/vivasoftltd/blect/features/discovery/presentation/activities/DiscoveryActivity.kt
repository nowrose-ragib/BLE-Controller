package com.vivasoftltd.blect.features.discovery.presentation.activities

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vivasoftltd.blect.R
import com.vivasoftltd.blect.core.contracts.InitializerContract
import com.vivasoftltd.blect.core.contracts.RecyclerContract
import com.vivasoftltd.blect.features.discovery.presentation.adapters.DeviceListAdapter
import com.vivasoftltd.blect.features.discovery.presentation.viewmodels.BleScanViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiscoveryActivity : AppCompatActivity(), InitializerContract, RecyclerContract {
  // dialog ref
  private var alertDialog: AlertDialog? = null

  // ui components
  private lateinit var toolbar: Toolbar
  private lateinit var deviceListView: RecyclerView

  // ui components helpers
  private lateinit var layoutManager: LinearLayoutManager

  // view models
  private lateinit var bleScanViewModel: BleScanViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_discovery)

    // initialize activity components
    initialize()

    // start flow
    checkPermissionsAndStartScan()
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    if (areAllPermissionsGranted()) {
      startListeningForDevices()
    } else {
      Toast.makeText(
        this,
        "Please grant all the permissions to continue",
        Toast.LENGTH_SHORT
      ).show()
    }
  }

  override fun initialize() {
    initializeActivityData()
    initializeToolBar()
    initializeUiComponents()
  }

  override fun recycle() {
    alertDialog?.dismiss()
  }

  private fun initializeActivityData() {
    bleScanViewModel = ViewModelProvider(this)[BleScanViewModel::class.java]
  }

  private fun initializeToolBar() {
    toolbar = findViewById(R.id.toolbar)
    setSupportActionBar(toolbar)
  }

  private fun initializeUiComponents() {
    // initialize
    deviceListView = findViewById(R.id.deviceList)

    // init props
    layoutManager = LinearLayoutManager(this)

    // set items/props
    deviceListView.layoutManager = layoutManager
    deviceListView.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))
  }

  private fun checkPermissionsAndStartScan() {
    if (!areAllPermissionsGranted()) {
      requestPermissions(buildPermissionList(), 1001)
    } else {
      startListeningForDevices()
    }
  }

  private fun startListeningForDevices() {
    bleScanViewModel.immutableState.observe(this) { applyNewList(it) }
    bleScanViewModel.startScan(this@DiscoveryActivity)
  }

  private fun applyNewList(deviceList: List<BluetoothDevice>) {
    deviceListView.adapter = DeviceListAdapter(
      this,
      deviceList,
      object : DeviceListAdapter.ItemClickListener {
        override fun onItemClick(
          adapter: DeviceListAdapter,
          device: BluetoothDevice,
          index: Int
        ) {
          // TODO - add item click logic here
          Log.d(javaClass.simpleName, device.toString())
        }
      }
    )
  }

  private fun areAllPermissionsGranted(): Boolean {
    val permissionList = buildPermissionList()
    for (element in permissionList) {
      if (checkSelfPermission(element) != PackageManager.PERMISSION_GRANTED) {
        return false
      }
    }
    return true
  }

  private fun buildPermissionList(): Array<String> {
    val permissionList = mutableListOf<String>()

    // for nearby devices
    permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION)
    permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION)

    // legacy bluetooth permissions
    permissionList.add(Manifest.permission.BLUETOOTH)
    permissionList.add(Manifest.permission.BLUETOOTH_ADMIN)

    // new bluetooth permissions
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      permissionList.add(Manifest.permission.BLUETOOTH_SCAN)
      permissionList.add(Manifest.permission.BLUETOOTH_CONNECT)
    }

    return permissionList.toTypedArray()
  }
}