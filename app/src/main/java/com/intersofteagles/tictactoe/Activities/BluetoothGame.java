package com.intersofteagles.tictactoe.Activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.intersofteagles.tictactoe.Adapters.DeviceAdapter;
import com.intersofteagles.tictactoe.R;

import java.util.Set;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BluetoothGame extends AppCompatActivity implements DeviceAdapter.DeviceListener{

    @BindView(R.id.recycler) RecyclerView recycler;

    DeviceAdapter deviceAdapter;
    BluetoothAdapter mBluetoothAdapter;

    private final int REQUEST_ENABLE_BT = 99;
    private final String UUID_STRING_WELL_KNOWN_SPP =
            "00001101-0000-1000-8000-00805F9B34FB";
    private UUID myUUID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_game);
        ButterKnife.bind(this);
        deviceAdapter = new DeviceAdapter(this);

        recycler.setHasFixedSize(true);
        recycler.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(deviceAdapter);

        myUUID = UUID.fromString(UUID_STRING_WELL_KNOWN_SPP);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (!mBluetoothAdapter.isEnabled()) {  // TODO enable discoverability to automatically start bluetooth
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }else {
            initBluetoothOn();
        }

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
    }


    @Override
    public void onDeviceClicked(BluetoothDevice device) {
        startActivity(new Intent(this,BluetoothActivity.class).putExtra("bd",device));
    }



    public void createGame(View v){
        startActivity(new Intent(this,BluetoothActivity.class).putExtra("server",true));
    }






    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_ENABLE_BT:
                if (!mBluetoothAdapter.isEnabled()) {
                    Toast.makeText(this, "Bluetooth is required", Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    Toast.makeText(this, "Bluetooth connected", Toast.LENGTH_SHORT).show();
                    initBluetoothOn();
                }
                break;
        }
        switch (resultCode){
            case 300:
                Toast.makeText(this, "Device discoverable", Toast.LENGTH_SHORT).show();
                break;
            case RESULT_CANCELED:
                Toast.makeText(this, "Discoverability cancelled", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                deviceAdapter.addDevice(0,device);
            }
        }
    };


    public void initBluetoothOn(){
        loadPairedDevices();

        if (mBluetoothAdapter.isDiscovering())return;
        if (!mBluetoothAdapter.startDiscovery()){
            Toast.makeText(this, "Unable to scan", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Searching for games ...", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mBluetoothAdapter.isDiscovering())mBluetoothAdapter.cancelDiscovery();
    }

    public void loadPairedDevices(){
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                deviceAdapter.addDevice(device);
            }
        }
    }



}
