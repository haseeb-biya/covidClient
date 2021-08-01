package com.example.covidclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;

import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    Button start,stop;
    public static TextView status,availability;


    private NotificationManager notificationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        start = (Button)findViewById(R.id.start_service);
        stop = (Button)findViewById(R.id.stop_service);
        status =(TextView)findViewById(R.id.status);
        availability =(TextView)findViewById(R.id.slot_availability);
        Intent intent = new Intent(this,ExecutableService.class);
        requestSMSPermission();
        new ReadOTP().setViewText(availability);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Service Started",Toast.LENGTH_LONG).show();
                stop.setEnabled(true);
                start.setEnabled(false);
                status.setText("Server Status: Started");
               //MainActivity.this.showNotification("Okay new Notification");
                startForegroundService(intent);
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Service Stopped",Toast.LENGTH_LONG).show();
                stop.setEnabled(false);
                start.setEnabled(true);
                status.setText("Server Status: Stopped");
                stopService(intent);
                // notifications.showNotification("Okay new Notification");
            }
        });

    }
    private void requestSMSPermission(){
        String permission = Manifest.permission.RECEIVE_SMS;
        int grant = ContextCompat.checkSelfPermission(this,permission);
        if(grant != PackageManager.PERMISSION_GRANTED){
            String[] permissionList = new String[1];
            permissionList[0]= permission;
            ActivityCompat.requestPermissions(this,permissionList,1);
        }
    }

}