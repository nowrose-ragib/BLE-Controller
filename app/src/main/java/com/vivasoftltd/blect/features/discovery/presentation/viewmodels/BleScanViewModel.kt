package com.vivasoftltd.blect.features.discovery.presentation.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.vivasoftltd.blect.core.contracts.AndroidViewModelContract
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BleScanViewModel @Inject constructor(application: Application) :
  AndroidViewModelContract<List<BluetoothDevice>>(application) {

  @SuppressLint("MissingPermission")
  fun startScan(context: Context) {
    val bluetoothManager: BluetoothManager = createBluetoothManager(context)
    bluetoothManager.adapter.bluetoothLeScanner.startScan(
      object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
          // super.onScanResult(callbackType, result)
          val device: BluetoothDevice? = result?.device
          if (device != null) {
            mutableState.postValue(
              createDeviceList(device, immutableState.value ?: listOf())
            )
          }
        }
      }
    )
  }

  private fun createBluetoothManager(context: Context): BluetoothManager {
    return context.getSystemService(AppCompatActivity.BLUETOOTH_SERVICE) as BluetoothManager
  }

  private fun createDeviceList(
    newDevice: BluetoothDevice,
    previousList: List<BluetoothDevice>,
  ): List<BluetoothDevice> {
    val deviceMap: MutableMap<String, BluetoothDevice> = mutableMapOf()
    for (device in previousList) {
      deviceMap[device.address] = device
    }

    deviceMap[newDevice.address] = newDevice

    return deviceMap.toList().sortedBy { it.first }.map { it.second }
  }
}