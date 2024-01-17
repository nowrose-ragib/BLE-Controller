package com.vivasoftltd.blect.features.discovery.presentation.adapters

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vivasoftltd.blect.R

class DeviceListAdapter(
  private val context: Context,
  private val deviceList: List<BluetoothDevice>,
  private val itemClickListener: ItemClickListener,
) : RecyclerView.Adapter<DeviceListAdapter.DeviceListCellViewHolder>() {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceListCellViewHolder {
    return DeviceListCellViewHolder(
      LayoutInflater.from(context)
        .inflate(
          R.layout.device_list_cell,
          parent,
          false
        )
    )
  }

  @SuppressLint("MissingPermission")
  override fun onBindViewHolder(holder: DeviceListCellViewHolder, position: Int) {
    val item: BluetoothDevice = deviceList[position]

    holder.deviceName.text = item.name ?: item.address
    holder.itemView.setOnClickListener {
      itemClickListener.onItemClick(this, deviceList[position], position)
    }
  }

  override fun getItemCount(): Int {
    return deviceList.size
  }

  class DeviceListCellViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val deviceIcon: ImageView = itemView.findViewById(R.id.deviceIcon)
    val deviceName: TextView = itemView.findViewById(R.id.deviceName)
    val exploreIndicator: ImageView = itemView.findViewById(R.id.exploreIndicator)
  }

  interface ItemClickListener {
    fun onItemClick(adapter: DeviceListAdapter, device: BluetoothDevice, index: Int)
  }
}