package com.intersofteagles.tictactoe.Adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.intersofteagles.tictactoe.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Orion Technologies on 6/20/2017.
 */

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceHolder> {

    Context context;
    LayoutInflater inflater;
    List<BluetoothDevice> devices = new ArrayList<>();
    DeviceListener listener;

    public DeviceAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        listener = (DeviceListener)context;
    }


    public void addDevice(BluetoothDevice device){
        devices.add(device);
        notifyItemInserted(devices.size()-1);
    }

    public void addDevice(int i,BluetoothDevice device){
        for (int a = 0;a<devices.size();a++){
            BluetoothDevice d = devices.get(a);
            if (d.getAddress().equals(device.getAddress())){
                if (!d.getName().isEmpty()) {
                    return;
                }
                devices.remove(a);
                notifyItemRemoved(a);
                break;
            }
        }
        devices.add(i,device);
        notifyItemInserted(i);
    }


    @Override
    public DeviceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DeviceHolder(inflater.inflate(R.layout.row_device,parent,false));
    }

    @Override
    public void onBindViewHolder(DeviceHolder holder, int position) {
        final BluetoothDevice device = devices.get(position);
        holder.name.setText(device.getName());
        holder.address.setText(device.getAddress());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDeviceClicked(device);
            }
        });
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    class DeviceHolder extends RecyclerView.ViewHolder{
        TextView name,address;
        public DeviceHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.name);
            address = (TextView)itemView.findViewById(R.id.address);
        }
    }

    public interface DeviceListener{
        void onDeviceClicked(BluetoothDevice device);
    }

}
