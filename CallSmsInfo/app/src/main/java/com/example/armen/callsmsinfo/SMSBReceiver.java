package com.example.armen.callsmsinfo;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SMSBReceiver extends BroadcastReceiver {
    private List<ModelSms> list = new ArrayList<>();
    public static final String SMS_BUNDLE = "pdus";


    public static final String SMS_CONTENT = "sms_content";


    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";
            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);
                String smsBody = smsMessage.getMessageBody().toString();
                String address = smsMessage.getOriginatingAddress();

                ModelSms modelSms = new ModelSms();
                modelSms.setSmsBody(smsBody);
                modelSms.setSmsAddress(address);
                list.add(modelSms);
                smsMessageStr += "SMS From: " + address + "\n";
                smsMessageStr += "SMS Body"+smsBody + "\n";
            }


            Intent serviceIntent = new Intent(context, CallInfoSmsInfo.class);
            serviceIntent.putExtra("keySMS", smsMessageStr);
            context.startService(serviceIntent);
        }
    }
}