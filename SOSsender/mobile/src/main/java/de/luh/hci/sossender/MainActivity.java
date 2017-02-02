package de.luh.hci.sossender;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;



public class MainActivity extends AppCompatActivity implements
        DataApi.DataListener,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{
    private TextView show;
//    private static final String wear_path = "/only_wear";
    private GoogleApiClient mGoogleApiClient;
    public int count = 0,danger = 65535;
    public double time1=0.0,time2=0.0;
    public boolean Send_SMS_Permission = true;
    //------------Socket--------------------
    Socket socket = null;
    BufferedWriter writer = null;
    BufferedReader reader = null;
    public String ip_address = "192.168.137.1";
    public int port = 5000;
    //--------------GPS setting-------------
    public double longitude = 0.0;
    public double latitude = 0.0;
    String locationProvider = LocationManager.NETWORK_PROVIDER;
    //---------accSensor--------------------
    public Sensor accSensor;
    public MySensorListener mySensorListener;
    public SensorManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //--------button bind---------------------
        show = (TextView)findViewById(R.id.show);
        //-----------Google service initialize-----------------
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        //-------------Sensor register and initialize------------------
        MySensorListener mySensorListener = new MySensorListener();
        sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        accSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(mySensorListener,accSensor,SensorManager.SENSOR_DELAY_NORMAL);
        //---------connect to server(computer)---------------------------------
        connect();
    }
//    private void sendTextToWear(String content){
//        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(Wearable.API)
//                .build();

//        PutDataMapRequest dataMap = PutDataMapRequest.create(wear_path);
//        dataMap.getDataMap().putString("content",content);
//        PutDataRequest request = dataMap.asPutDataRequest();
//        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi
//                .putDataItem(mGoogleApiClient,request);
//    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
//        connect();
        super.onStart();
    }
    @Override
    protected void onPause() {
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            Wearable.DataApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
            close();
        }
        super.onPause();
    }
    @Override
    protected void onStop() {
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            Wearable.DataApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
            close();
        }
        super.onStop();
    }
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.e("MainActivity","-----Data Changed-------"+String.valueOf(count));
        int content;

        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_DELETED) {
            }
            else if (event.getType() == DataEvent.TYPE_CHANGED) {
                DataMap dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                if(event.getDataItem().getUri().getPath().equals("/only_phone")){
                    content = dataMap.get("content");
                    if(content == danger){
                        getGPSandSend();
                        Log.e("GETGPS","-------------GPS---------------");
                    }
                    else{
                        show.append("\n" + content);
                        send("Rate:"+ content);
                    }
                    Log.e("MainActivity","-------receive-content-----"+content);
                }
//                if(event.getDataItem().getUri().getPath().equals("/only_phone2")){
//                    falldown_message = dataMap.get("falldown_message");
//                    if(falldown_message){
//                        Log.e("MainActivity","-------receive-falldown-----");
//                        show.append("\n" + "falldown!");
//                        send("falldown");
//                    }
//                }
            }
        }
//        sendTextToWear(String.valueOf(count));
        count++;
    }
//----------------------------------------
//    public boolean data_trigger(String data, int high, int low) {
//        int herz_data = Integer.parseInt(data);
//        if (herz_data > high && herz_data < low) {
//        new AlertDialog.Builder(this).setTitle("Dialog").setMessage("Do you want to send SMS?")
//                .setPositiveButton("Ja",null)
//                .setNegativeButton("Nein",null).show();
//        return true;
//        } else {
//            send(data);
//            return false;
//        }
//}
    @Override
    public void onConnected(Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }
    @Override
    public void onConnectionSuspended(int i) {

    }
    @Override
    public void onConnectionFailed(@Nullable ConnectionResult connectionResult) {

    }
    //---------------GPS--------------------------
    public void getGPSandSend() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                makeUseOfNewLocation(location);
            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }
            @Override
            public void onProviderEnabled(String s) {
            }
            @Override
            public void onProviderDisabled(String s) {
            }
        };
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission refused!", Toast.LENGTH_SHORT).show();
            return;
        }
        locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
    }
    public void makeUseOfNewLocation(Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        show.append("\n" + "Latitude:" + latitude + "\n" + "Longitude:" + longitude);
        send(latitude + "*" + longitude);
    }
    //--------------sensor listner----------------
    public class MySensorListener implements SensorEventListener{
        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
            Log.d("MainActivity","onAccuracyChanged:" + sensor + "," + i);
        }
        @Override
        public void onSensorChanged(SensorEvent event) {
            if(fall_down(event.values[0],event.values[1],event.values[2]) && Send_SMS_Permission){
                send("falldown");
                show.append("\n"+"falldown");
                Send_SMS_Permission = false;
            }
        }
    }
    //------------fall down------------------------
    public boolean fall_down(double a,double b,double c){
        double acc;
        acc = Math.pow(a,2)+Math.pow(b,2)+Math.pow(c,2);
        acc = Math.sqrt(acc);
        if(acc>20 ){
            time1 = System.currentTimeMillis();
            Log.e("Sensor","--------" + a +"-----------");
            Log.e("Sensor","--------" + b +"-----------");
            Log.e("Sensor","--------" + c +"-----------");
            Log.e("Sensor","---------------------------");
            return true;
        }
        return false;
//        if(acc<10){
//            time2 = System.currentTimeMillis();
//            if(time2-time1 == 5){
//                Log.e("Sensor","***********" + "摔倒！" +"***********");
//            }
//        }
    }
    //--------------Socket------------------------
    public void connect(){
        AsyncTask<Void,String,Void> read = new AsyncTask<Void, String, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    socket = new Socket(ip_address,port);
                    writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    publishProgress("@success");
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                String line = null;
//                try {
//                    while((line = reader.readLine())!=null){
//                        publishProgress(line);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                return null;
            }
            @Override
            protected void onProgressUpdate(String... values) {
                if(values[0].equals("@success")){
                    Toast.makeText(MainActivity.this,"connect Success!",Toast.LENGTH_SHORT).show();
                }
                super.onProgressUpdate(values);
            }
        };
        read.execute();
    }
    public void send(String str){
        try {
            writer.write(str);
            writer.flush();
            Log.e("MainActivity","-------" + str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void close(){
        try {
            writer.write("exit");
            writer.flush();
            reader.close();
            writer.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

