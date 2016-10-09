package com.ekg.www.inventathon.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import com.bitalino.comm.BITalinoDevice;
import com.bitalino.comm.BITalinoFrame;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Created by cbono on 5/5/16.
 * Bluetooth Class used for spirometer and airbeam - regular dust sensor uses the other non-socket  classes in this package,
 * as the dust sensor uses an older version of Bluetooth.
 *
 */
public class BTSocket {
    private static final String TAG = "BTSocket";
    private static final Integer BUFFER_SIZE = 1024*2;

    private BluetoothSocket mmSocket = null;
    private static int mIndex = 0;
    private boolean stopWorker;
    private BluetoothDevice mmDevice = null;
    private InputStream mmInputStream = null;
    // context used for spirometer only.
    private Context context;
    private UUID uuid;

    private int readBufferPosition;
    private byte[] readBuffer = new byte[BUFFER_SIZE];
    private Thread workerThread = null;

    public BTSocket(int type, UUID u, Context c) {
        context = c;
        uuid = u;
        stopWorker = false;
        setDevice(findConn());
    }

    public BTSocket(int type, UUID u, Context c, BluetoothDevice device) {
        context = c;
        uuid = u;
        stopWorker = false;
        setDevice(device);
    }

    public BTSocket(Context c) {
        context = c;
        uuid =  UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        stopWorker = false;
        setDevice(findConn());

    }

    public boolean hasDevice() {
        return mmDevice != null;
    }

    public void setDevice(BluetoothDevice device) {
        mmDevice = device;
    }

    public BluetoothDevice getDevice() {
        return mmDevice;
    }

    // Attempt to connect to non-null device, if failure reset value of mmDevice.
    public boolean openConn() {
        try {
//            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmInputStream = mmSocket.getInputStream();

        } catch (Exception e) {
//            e.printStackTrace();
            if (mmDevice != null) {
                Log.e(TAG, "[Handled] openConn unsuccessful: "
                        + e.getMessage() + ", device:" + mmDevice.toString());

                try {
                    Log.e(TAG,"initial BTSocket failed, trying fallback...");
                    mmSocket = (BluetoothSocket) mmDevice.getClass().getMethod(
                            "createRfcommSocket", new Class[] {int.class}).invoke(mmDevice,1);
                    mmSocket.connect();
                    mmInputStream = mmSocket.getInputStream();
                    Log.d(TAG,"Connected");
                }
                catch (Exception e2) {
                    Log.e(TAG, "Couldn't establish Bluetooth connection! Setting mmDevice to null");
                    // broadcast a custom intent.

                    System.out.println("intent Received");
                    mmDevice = null;
                }
            } else {
                Log.e(TAG, "[Handled] Failure: mmDevice was null in openConn");
            }
            return false;
        }
        // Able to connect and retrieve inputStream to bluetooth device.
        Log.d(TAG, "Bluetooth Opened with device: " + mmDevice.toString());
        return true;
    }

    public void closeConn() {
        mmDevice = null;
        stopWorker = true;

        try {
            if (workerThread != null)
                workerThread.join();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error joining workerThread");
        }
        try {
            if (mmInputStream != null)
                mmInputStream.close();
        } catch (Exception e) {
            Log.e(TAG, "Error closing mmInputStream");
        }

        try {
            if (mmSocket!=null)
                mmSocket.close();
        } catch (Exception e) {
            Log.e(TAG, "Error closing mmSocket");
        }

        Log.d(TAG, "conn bluetooth closed");
    }

    private BITalinoDevice bitalino;

    public void beginListen() {
        Log.d(TAG, "beginListen");

        try {
            bitalino = new BITalinoDevice(1000, new int[]{0, 1, 2, 3, 4, 5});
            Log.d(TAG, "Connecting to BITalino [" + mmDevice.toString() + "]..");
            bitalino.open(mmSocket.getInputStream(), mmSocket.getOutputStream());
            Log.d(TAG, "Connected.");

            // get BITalino version
            Log.d(TAG, "Version: " + bitalino.version());

            // start acquisition on predefined analog channels
            bitalino.start();

            // read until task is stopped
            int counter = 0;
            while (counter < 100) {
                final int numberOfSamplesToRead = 1000;
                Log.d(TAG, "Reading " + numberOfSamplesToRead + " samples..");
                BITalinoFrame[] frames = bitalino.read(numberOfSamplesToRead);
//
//                if (UPLOAD) {
//                    // prepare reading for upload
//                    BITalinoReading reading = new BITalinoReading();
//                    reading.setTimestamp(System.currentTimeMillis());
//                    reading.setFrames(frames);
//                    // instantiate reading service client
//                    RestAdapter restAdapter = new RestAdapter.Builder()
//                            .setEndpoint("http://server_ip:8080/bitalino")
//                            .build();
//                    ReadingService service = restAdapter.create(ReadingService.class);
//                    // upload reading
//                    Response response = service.uploadReading(reading);
//                    assert response.getStatus() == 200;
//                }

                // present data in screen
                for (BITalinoFrame frame : frames)
                    Log.d(TAG, frame.toString());

                counter++;
            }

            // trigger digital outputs
            // int[] digital = { 1, 1, 1, 1 };
            // device.trigger(digital);
        } catch (Exception e) {
            Log.e(TAG, "There was an error.", e);
        }
        // beginDuinoListen();
    }

    private static float[] airBeamVals = new float[3];
    // callback when dust data receivable
    // Example data: B:0.84ET:77EH:59E
    private void processBTData(String receiveBuffer) {
        Log.d(TAG, "bitalinoData: " + receiveBuffer);
    }

    private void beginDuinoListen() {
        Log.d(TAG, "beginDuinoListen");
        readBufferPosition = 0;
        stopWorker = false;
        //listener worker thread
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                boolean readActive = true;

                while(!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try  {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            // Process AirBeam packetBytes and add to server.
                            processBTData(new String(packetBytes));
                        }
                    }
                    catch (IOException ex) {
                        Log.e(TAG, "IO exception in BTSocket run");
                        stopWorker = true;
                    }
                }
            }
        });
        workerThread.start();
    }

    //Bluetooth socket connection - only check paired devices
    public static BluetoothDevice findConn() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // identifier for locating the bluetooth device under paired devices.
        final String deviceString = "bit";

        if(bluetoothAdapter == null) {
            Log.e(TAG, "No bluetooth adapter available");
            return null;
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            String deviceName;
            for (BluetoothDevice device : pairedDevices) {
                deviceName = device.getName();
                try {
                    Log.d(TAG, "Device: " + deviceName);
                    if (deviceName.contains(deviceString)) {
                        Log.d(TAG, "findConn Detected device: " + deviceName);
                        return device;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Handled exception in device name");

                }
            }
        }
        Log.d(TAG, "findConn did not find paired device matching " + deviceString);
        return null;
    }


}
