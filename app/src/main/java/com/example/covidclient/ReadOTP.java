package com.example.covidclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.widget.TextView;

public class ReadOTP extends BroadcastReceiver {
    private static TextView availability;

    public void setViewText(TextView avail){
        ReadOTP.availability = avail;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        for(SmsMessage sms: messages){
            String message = sms.getMessageBody();
            if(message.contains("CoWIN")) {
                String msg = message.split(".")[0];
                String OTP = msg.split("is")[1];
                availability.setText("Your OTP to access coWin is " + OTP);
            }
        }
    }
}
