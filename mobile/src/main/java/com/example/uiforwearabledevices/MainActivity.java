package com.example.uiforwearabledevices;


import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.text.IDNA;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EventListener;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {

    private Button btn_StartAndStop;
    private Chronometer chronometer;
    private TextView tv_accX;
    private TextView tv_accY;
    private TextView tv_accZ;
    private TextView tv_gyrX;
    private TextView tv_gyrY;
    private TextView tv_gyrZ;
    private TextView tv_heart;
    private Toast toast;
    private SensorManager mSensorMgr;
    public List<String> LS1;
    public List<String> LS2;
    public String acc;
    public String gyr;

    private EditText etServerIP;

    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_accept = (Button) findViewById(R.id.btn_accept);
        btn_accept.setOnClickListener(this);
        if (checkCallingPermission(Manifest.permission.BODY_SENSORS) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BODY_SENSORS}, 100);
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        btn_StartAndStop = findViewById(R.id.btn_StartAndStop);
        chronometer = findViewById(R.id.chronometer);
        tv_accX = findViewById(R.id.tv_accX);
        tv_accY = findViewById(R.id.tv_accY);
        tv_accZ = findViewById(R.id.tv_accZ);
        tv_gyrX = findViewById(R.id.tv_gyrX);
        tv_gyrY = findViewById(R.id.tv_gyrY);
        tv_gyrZ = findViewById(R.id.tv_gyrZ);
        tv_heart = findViewById(R.id.tv_heart);
        btn_StartAndStop.setOnClickListener(this);
        mSensorMgr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        LS1 = new ArrayList<String>();
        LS2 = new ArrayList<String>();

        etServerIP = findViewById(R.id.et_server_ip);

        Button btnAccept = findViewById(R.id.btn_accept);
        btnAccept.setOnClickListener(this);



    }



    protected void onPause() {
        super.onPause();
        mSensorMgr.unregisterListener(this);
    }

    protected void onResume() {
        super.onResume();

    }

    protected void onStop() {
        super.onStop();
        mSensorMgr.unregisterListener(this);

    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();
        float[] values = event.values;
        switch (type) {
            case Sensor.TYPE_ACCELEROMETER:
                tv_accX.setText(String.valueOf(values[0]));
                tv_accY.setText(String.valueOf(values[1]));
                tv_accZ.setText(String.valueOf(values[2]));
                acc = Float.toString(values[0]) + "/" + Float.toString(values[1]) + "/" + Float.toString(values[2]);
                LS1.add(acc);

                break;
            case Sensor.TYPE_GYROSCOPE:
                tv_gyrX.setText(String.valueOf(values[0]));
                tv_gyrY.setText(String.valueOf(values[1]));
                tv_gyrZ.setText(String.valueOf(values[2]));
                gyr = Float.toString(values[0]) + "/" + Float.toString(values[1]) + "/" + Float.toString(values[2]);
                LS2.add(gyr);
                break;
            case Sensor.TYPE_HEART_RATE:
                break;

        }

    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

        return;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_accept) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        acceptServer();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } else if (v.getId() == R.id.btn_StartAndStop) {
            // Rest of your existing code for the "Start/Stop" button click
            // ...

        if (btn_StartAndStop.getText().equals("Start")) {
            toast = Toast.makeText(this, "START", Toast.LENGTH_SHORT);
            toast.show();
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
            btn_StartAndStop.setText("Stop");
            btn_StartAndStop.setTextColor(Color.RED);
            btn_StartAndStop.setBackgroundColor(Color.GRAY);
            mSensorMgr.unregisterListener(this);
            mSensorMgr.unregisterListener(this, mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
            mSensorMgr.registerListener(this, mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
            mSensorMgr.unregisterListener(this, mSensorMgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
            mSensorMgr.registerListener(this, mSensorMgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_GAME);
            mSensorMgr.unregisterListener(this, mSensorMgr.getDefaultSensor(Sensor.TYPE_HEART_RATE));
            mSensorMgr.registerListener(this, mSensorMgr.getDefaultSensor(Sensor.TYPE_HEART_RATE), SensorManager.SENSOR_DELAY_GAME);


        } else if (btn_StartAndStop.getText().equals("Stop")) {
            toast = Toast.makeText(this, "STOP", Toast.LENGTH_SHORT);
            toast.show();
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.stop();
            mSensorMgr.unregisterListener(this);
            tv_accX.setText("------");
            tv_accY.setText("------");
            tv_accZ.setText("------");
            tv_gyrX.setText("------");
            tv_gyrY.setText("------");
            tv_gyrZ.setText("------");
            tv_heart.setText("------");
            btn_StartAndStop.setText("Start");
            btn_StartAndStop.setTextColor(Color.BLACK);
            btn_StartAndStop.setBackgroundColor(Color.rgb(66, 204, 255));

            File filepath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Acc");
            //如果目录不存在，则创建该目录
            if (!filepath.exists()) {
                try {
                    filepath.mkdir();
                } catch (Exception e) {
                    toast = Toast.makeText(this, "ERROR1", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            int k = 0;
            while (true) {
                String filename = filepath + "/Acc" + k + ".txt";
                File file = new File(filename);
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (Exception e) {
                        toast = Toast.makeText(this, "ERROR2", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    try {
                        FileOutputStream fos = new FileOutputStream(file);
                        for (int i = 0; i < LS1.size(); i++) {////在循环中逐个尝试创建文件，并将列表 LS1 中的数据写入文件。写入完成后，将 LS1 清空
                            String content = LS1.get(i) + "\n";
                            fos.write(content.getBytes(StandardCharsets.UTF_8));
                        }
                        fos.close();
                        LS1 = new ArrayList<String>();
                    } catch (FileNotFoundException e) {
                        toast = Toast.makeText(this, "ERROR3", Toast.LENGTH_SHORT);
                        toast.show();
                    } catch (IOException e) {
                        toast = Toast.makeText(this, "ERROR4", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    break;
                }
                k++;
            }
            //创建另一个目录对象 filepath，表示存储文件的路径。路径为外部存储器的下载目录下的 "Gyr" 文件夹
            filepath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Gyr");

            if (!filepath.exists()) {
                try {
                    filepath.mkdir();
                } catch (Exception e) {
                    toast = Toast.makeText(this, "ERROR1", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            k = 0;
            while (true) {
                String filename = filepath + "/Gyr" + k + ".txt";
                File file = new File(filename);
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (Exception e) {
                        toast = Toast.makeText(this, "ERROR2", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    try {
                        FileOutputStream fos = new FileOutputStream(file);
                        for (int i = 0; i < LS2.size(); i++) {
                            String content = LS2.get(i) + "\n";
                            fos.write(content.getBytes(StandardCharsets.UTF_8));
                        }
                        fos.close();
                        LS2 = new ArrayList<String>();
                    } catch (FileNotFoundException e) {
                        toast = Toast.makeText(this, "ERROR3", Toast.LENGTH_SHORT);
                        toast.show();
                    } catch (IOException e) {
                        toast = Toast.makeText(this, "ERROR4", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    break;
                }
                k++;
            }


        }
    }
    }



    private void acceptServer() throws IOException, SocketException{
        String serverIP = etServerIP.getText().toString();
        int serverPort = 3490;

        try {
            // Create a socket connection to the server
            Socket socket = new Socket(serverIP, serverPort);

            // Get the output stream to send data to the server
            OutputStream os = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(os);

            // Get the client's IP address
            InetAddress address = InetAddress.getLocalHost();
            String clientIP = address.getHostAddress();

            // Send a message to the server
            pw.write("客户端：~" + clientIP + "~ 接入服务器！！");
            pw.flush();

            // Close the socket and streams
            pw.close();
            os.close();
            socket.close();

            // Connection successful
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "已成功连接到服务器", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();

            // Connection failed
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "无法连接到服务器", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}


