package com.example.covidclient;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.util.Timer;
import java.util.TimerTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class ExecutableService extends Service {

    private NotificationManager notificationManager;
    private NotificationManager notificationManager2;
    private Timer timer;
    private TimerTask timerTask;
    private RequestQueue mRequestQueue;
    private JsonArrayRequest mArrayRequest;
    private String url = "http://192.168.43.150:3001/newData";
    private AudioAttributes audioAttributes;
    private Uri sound;
    private static final String TAG = MainActivity.class.getName();
    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        createChannelTwoNotification();
        timerTask =  new TimerTask() {
            @Override
            public void run() {
                every30SecondTask(intent);
            }
        };

        timer.scheduleAtFixedRate(timerTask,15000,15000);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        Notification notification = new NotificationCompat.Builder(this,"Server Started Notification")
                .setContentTitle("Covid App")
                .setContentText("Looking for Available Slots")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent).build();
        startForeground(1,notification);
        return START_STICKY;
    }
    public void every30SecondTask(Intent intent){
        ContextCompat.getMainExecutor(this).execute(()->{
            mRequestQueue=Volley.newRequestQueue(this);
            mArrayRequest = new JsonArrayRequest(Request.Method.GET, url,null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    //extract JSON data from response
                    String responseForUI="";
                    for(int i=0;i<response.length();i++){
                        try {
                            JSONObject object = response.getJSONObject(i);
                            responseForUI +="Name: "+object.optString("name")+" Doses: "+object.optString("dose1")+"\n";
                        }catch(Exception e) {
                            Log.i(TAG, "Error in parse " + e.getMessage());
                        }
                    }
                    MainActivity.availability.setText(responseForUI);
                    Log.i(TAG,"Res :" + responseForUI);
                    //Generate a Notification when new data is available
                    if(!responseForUI.isEmpty()) {
                    showNotification();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i(TAG,"Error :" + error.toString());
                }
            });
            mRequestQueue.add(mArrayRequest);
        });
    }
    public void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel("Server Started Notification","Server Started Notification", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("This channel is for started started notification");
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);

        }
    }
    public void showNotification(){
        NotificationCompat.Builder notification2 = new NotificationCompat.Builder(this, "Slot Availability")
                .setContentTitle("Covid App")
                .setContentText("Slots are available.Please check server logs.")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(sound);
        notificationManager2.notify(2, notification2.build());
    }
    public void createChannelTwoNotification(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/raw/siren.wav" ) ;
//            audioAttributes = new AudioAttributes.Builder()
//                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION )
//                    .setUsage(AudioAttributes.USAGE_ALARM)
//                    .build();
            NotificationChannel notificationChannel2 = new NotificationChannel("Slot Availability","Slot Availability", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel2.setDescription("This Channel for slot availability");
            notificationManager2 = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager2.createNotificationChannel(notificationChannel2);
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onDestroy() {
        stopForeground(true);
        stopSelf();
        timer.cancel();
        timerTask.cancel();
        super.onDestroy();
    }
}
