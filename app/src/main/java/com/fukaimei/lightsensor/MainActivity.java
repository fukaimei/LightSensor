package com.fukaimei.lightsensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.fukaimei.lightsensor.util.SwitchUtil;
import com.fukaimei.lightsensor.util.Utils;

public class MainActivity extends AppCompatActivity implements
        OnCheckedChangeListener, SensorEventListener {
    private CheckBox ck_bright;
    private TextView tv_light;
    private SensorManager mSensroMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ck_bright = (CheckBox) findViewById(R.id.ck_bright);
        if (SwitchUtil.getAutoBrightStatus(this) == true) {
            ck_bright.setChecked(true);
        }
        ck_bright.setOnCheckedChangeListener(this);
        tv_light = (TextView) findViewById(R.id.tv_light);
        mSensroMgr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.ck_bright) {
            SwitchUtil.setAutoBrightStatus(this, isChecked);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensroMgr.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensroMgr.registerListener(this, mSensroMgr.getDefaultSensor(Sensor.TYPE_LIGHT),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float light_strength = event.values[0];
            tv_light.setText(Utils.getNowDateTimeFormat() + "\n当前光线强度为" + light_strength);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //当传感器精度改变时回调该方法，一般无需处理
    }

}