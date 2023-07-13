package com.example.uiforwearabledevices;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uiforwearabledevices.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener, SensorEventListener {


    private ActivityMainBinding binding;
    private Button btn_StartAndStop;
    private TextView tv_data1;
    private TextView tv_data2;
    private TextView tv_data3;
    private SensorManager mSensorMgr;
    private Chronometer chronometer;
    private Toast toast;
    private static final String TAG = "MY_APP_DEBUG_TAG";
    static String acc;
    static String gyr;
    public List<String> LS1;
    public List<String> LS2;
    Context context = this;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        btn_StartAndStop = findViewById(R.id.btn_StartAndStop);
        btn_StartAndStop.setOnClickListener(this);
        tv_data1 = findViewById(R.id.tv_data1);
        tv_data2 = findViewById(R.id.tv_data2);
        tv_data3 = findViewById(R.id.tv_data3);
        mSensorMgr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        chronometer = findViewById(R.id.chronometer);
        LS1 = new ArrayList<String>();
        LS2 = new ArrayList<String>();



    }
    //暂停活动。此时活动页面进入暂停状态（也就是退回就绪状态），无法与用户正常交互
    protected void onPause() {//Another activity comes in front of this activity,要准备让这个页面不可见，也就是onResume-->到onStop的过渡
        super.onPause();
        mSensorMgr.unregisterListener(this);
    }
    //恢复活动。此时活动页面进入活跃状态，能够与用户正常交互，例如允许响应用户的点击动作、允许用户输入文字等
    protected void onResume() {//动画可见
        super.onResume();

    }

    protected void onStop() {//this activity is no longer visible
        super.onStop();
        mSensorMgr.unregisterListener(this);

    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public void onSensorChanged(SensorEvent event) {

        int type = event.sensor.getType();
        float[] values = event.values;
        switch (type) {
            case Sensor.TYPE_ACCELEROMETER:
                acc =  Float.toString(values[0]) + "/" + Float.toString(values[1]) + "/" + Float.toString(values[2]);
                tv_data1.setText("X=" + String.format("%.2f", values[0]) + " Y=" + String.format("%.2f", values[1]) + " Z=" + String.format("%.2f", values[2]));
                LS1.add(acc);
                break;
            case Sensor.TYPE_GYROSCOPE:
                gyr =  Float.toString(values[0]) + "/" + Float.toString(values[1]) + "/" + Float.toString(values[2]);
                tv_data2.setText("X=" + String.format("%.2f", values[0]) + " Y=" + String.format("%.2f", values[1]) + " Z=" + String.format("%.2f", values[2]));

                LS2.add(gyr);
                break;
            case Sensor.TYPE_HEART_RATE:
                tv_data3.setText(Float.toString(values[0]));
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

        return;
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        if (btn_StartAndStop.getText().equals("Start")) {
            toast = Toast.makeText(this, "START", Toast.LENGTH_SHORT);
            toast.show();
            chronometer.setVisibility(View.VISIBLE);
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
            btn_StartAndStop.setText("Stop");
            btn_StartAndStop.setTextColor(Color.RED);
            btn_StartAndStop.setBackgroundColor(Color.GRAY);
            mSensorMgr.unregisterListener(this, mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
            mSensorMgr.registerListener(this, mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
            mSensorMgr.unregisterListener(this, mSensorMgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
            mSensorMgr.registerListener(this, mSensorMgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_UI);
            mSensorMgr.unregisterListener(this, mSensorMgr.getDefaultSensor(Sensor.TYPE_HEART_RATE));
            mSensorMgr.registerListener(this, mSensorMgr.getDefaultSensor(Sensor.TYPE_HEART_RATE), SensorManager.SENSOR_DELAY_UI);


        }
        else if(btn_StartAndStop.getText().equals("Stop")){
            toast = Toast.makeText(this, "STOP", Toast.LENGTH_SHORT);
            toast.show();
            chronometer.setVisibility(View.INVISIBLE);
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.stop();
            mSensorMgr.unregisterListener(this);
            tv_data1.setText("------");
            tv_data2.setText("------");
            tv_data3.setText("------");
            btn_StartAndStop.setText("Start");
            btn_StartAndStop.setTextColor(Color.BLACK);
            btn_StartAndStop.setBackgroundColor(Color.rgb(66,204,255));
            File folder = context.getExternalFilesDir("/storage/Acc");

            if(!folder.exists()){
                try{
                    folder.mkdir();
                }catch (Exception e){
                    toast = Toast.makeText(this, "ERROR1", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            int k = 0;
            while(true){
                String filename = folder + "/Acc" + k +".txt";
                File file = new File(filename);
                if(!file.exists()){
                    try{
                        file.createNewFile();
                    }catch (Exception e){
                        toast = Toast.makeText(this, "ERROR2", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    try {
                        FileOutputStream fos = new FileOutputStream(file);
                        for(int i = 0;i< LS1.size();i++){
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
            folder = context.getExternalFilesDir("/storage/Gyr");

            if(!folder.exists()){
                try{
                    folder.mkdir();
                }catch (Exception e){
                    toast = Toast.makeText(this, "ERROR1", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            k = 0;
            while(true){
                String filename = folder + "/Gyr" + k +".txt";
                File file = new File(filename);
                if(!file.exists()){
                    try{
                        file.createNewFile();
                    }catch (Exception e){
                        toast = Toast.makeText(this, "ERROR2", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    try {
                        FileOutputStream fos = new FileOutputStream(file);
                        for(int i = 0;i< LS2.size();i++){
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