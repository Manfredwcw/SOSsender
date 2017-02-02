package de.luh.hci.sossender;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Dell on 2016/12/15.
 */

public class losgehen extends WearableActivity implements
        DataApi.DataListener,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private BoxInsetLayout mContainerView;
    private TextView mTextView;
    private TextView mClockView;
    private TextView showrate;
    //--------------------------------------------
    public int count1 = 0;
    private static final String phone_path = "/only_phone";
    private GoogleApiClient mGoogleApiClient;
    //----------share preference-----------
    private SharedPreferences d_herz,d_m;
    private int high,low;
    //----------Double click---------------
    public int count_click = 0;
    public long firClick = 0;
    public long secClick = 0;
    public long trdClick = 0;
    //--------------Modul-------------------
    boolean schlaf;
    int danger = 65535;
    int danger_count = 0;
    //------------Others---------------
    double x,y,z,time1=0.0,time2=0.0,a=0.0;
    public int datass = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.los_gehen);
        setAmbientEnabled();

        d_herz = getSharedPreferences("data_hs", MODE_WORLD_READABLE);
        d_m = getSharedPreferences("data_m", MODE_WORLD_READABLE);
        high = d_herz.getInt("high",140);
        low = d_herz.getInt("low",70);
        schlaf = d_m.getBoolean("schlaf",false);
        if(schlaf)
            danger_count = 1;
        else
            danger_count = 0;
        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        mTextView = (TextView) findViewById(R.id.text);
        mClockView = (TextView) findViewById(R.id.clock);
        showrate = (TextView)findViewById(R.id.rate);
//        send = (Button) findViewById(R.id.button_send);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

//        losgehen.MySensorListener mySensorListener1 = new losgehen.MySensorListener();
        losgehen.MySensorListener mySensorListener2 = new losgehen.MySensorListener();
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

//        Sensor accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor heartSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);

//        sensorManager.registerListener(mySensorListener1,accSensor,SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(mySensorListener2,heartSensor,SensorManager.SENSOR_DELAY_UI);

//        send.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendDataToPhone(String.valueOf(count));
//                count++;
//            }
//        });
        //-------------Double click event----------------------
        mContainerView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!schlaf){
                    if(MotionEvent.ACTION_DOWN == event.getAction()){
                        count_click++;
                        if(count_click == 1){
                            firClick = System.currentTimeMillis();
                        }else if (count_click == 2) {
                            secClick = System.currentTimeMillis();
                            if(secClick - firClick >= 500){
                                count_click = 0;
                                firClick = 0;
                                secClick = 0;
                                trdClick = 0;
                            }
                        } else if(count_click == 3) {
                            trdClick = System.currentTimeMillis();
                            if (trdClick - secClick < 500 && secClick - firClick < 500) {
                                Log.e("losgehen", "---------triple click------------");
                                data_trigger(danger);
//                                send_Data_ToPhone(danger);
//                                Toast.makeText(getApplicationContext(), "Signal schon gesendet !", Toast.LENGTH_SHORT).show();
                            }
                            count_click = 0;
                            firClick = 0;
                            secClick = 0;
                            trdClick = 0;
                        }
                    }
                    return true;
                }
                else
                    return false;
            }
        });
    }
    //-------------------------------------------------------
    public class MySensorListener implements SensorEventListener {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.d("MainActivity","onAccuracyChanged:" + sensor + "," + accuracy);
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType()==Sensor.TYPE_HEART_RATE){
                int heartInfo = (int)event.values[0];
                showrate.setText(String.valueOf(heartInfo));
                if((heartInfo > high || heartInfo < low) && heartInfo !=0){
//                    data_trigger(heartInfo);
                    send_Data_ToPhone(heartInfo);
                    if(schlaf && danger_count==1){
                        send_Data_ToPhone(danger);
                        danger_count--;
                    }
                }
                count1++;
            }
        }
    }
    //----------------------------------------
    public void data_trigger(int data) {
        datass = data;
        new AlertDialog.Builder(this).setMessage("Send SOS to Hospital?")
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        send_Data_ToPhone(datass);
                        Toast.makeText(getApplicationContext(), "SOS schon gesendet !", Toast.LENGTH_SHORT).show();
                    }

                })
                .setNegativeButton("Nein", null).show();
    }
    //---------send message to phone-------------------------------------------------
    public void send_Data_ToPhone(int content){
//        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(Wearable.API)
//                .build();
        Log.e("MainActivity","-------send-data----------"+String.valueOf(count1));
        PutDataMapRequest dataMap = PutDataMapRequest.create(phone_path);
//        dataMap.getDataMap().putString("content",content);
        dataMap.getDataMap().putInt("content",content);
        PutDataRequest request = dataMap.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi
                .putDataItem(mGoogleApiClient,request);
    }
//    public void send_falldown_ToPhone(){
//        Log.e("MainActivity","-------send-falldown-message----------"+String.valueOf(count1));
//        PutDataMapRequest dataMap = PutDataMapRequest.create(phone_path1);
//        dataMap.getDataMap().putBoolean("falldown_message",true);
//        PutDataRequest request = dataMap.asPutDataRequest();
//        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi
//                .putDataItem(mGoogleApiClient,request);
//    }
    //-------------------------------------------------------------------------------
    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        d_herz = getSharedPreferences("data_hs", MODE_WORLD_READABLE);
        d_m = getSharedPreferences("data_m", MODE_WORLD_READABLE);
        high = d_herz.getInt("high",140);
        low = d_herz.getInt("low",70);
        schlaf = d_m.getBoolean("schlaf",false);
        if(schlaf)
            danger_count = 1;
        else
            danger_count = 0;
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            Wearable.DataApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    //------------------------------------------------------------------------
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_DELETED) {
            } else if (event.getType() == DataEvent.TYPE_CHANGED) {
                DataMap dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                if(event.getDataItem().getUri().getPath().equals("/only_wear")){
                    String content = dataMap.get("content");
                    Toast.makeText(this,content,Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    @Override
    public void onConnected(Bundle bundle) {
//        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }
    @Override
    public void onConnectionSuspended(int i) {

    }
    @Override
    public void onConnectionFailed(@Nullable ConnectionResult connectionResult) {

    }
    //----------------------------------------------------------------------
    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }
    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }
    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }
    private void updateDisplay() {
        if (isAmbient()) {
            mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
            mTextView.setTextColor(getResources().getColor(android.R.color.white));
            mClockView.setVisibility(View.VISIBLE);

            mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
        } else {
            mContainerView.setBackground(null);
            mTextView.setTextColor(getResources().getColor(android.R.color.black));
            mClockView.setVisibility(View.GONE);
        }
    }
}
