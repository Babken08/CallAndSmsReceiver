package com.example.armen.callsmsinfo;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver broadcastReceiverCall;
    private BroadcastReceiver broadcastReceiverSms;
    private SMSBReceiver recelver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView textView = findViewById(R.id.text);
        recelver = new SMSBReceiver();
        callPermissons();
        broadcastReceiverSms = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                textView.setText(intent.getStringExtra("key_sms_detail"));
            }
        };
        broadcastReceiverCall = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String data = intent.getStringExtra("key_call_detail");
                textView.setText(data);
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction("from_my_intent_service");
        intentFilter.addAction("response_from_my_intent_service");
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiverCall, intentFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiverSms, intentFilter1);
    }

    private void callPermissons() {

        if(ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_SMS") == PackageManager.PERMISSION_GRANTED) {

            // Todo : If Permission Granted Then Show SMS
            refreshSmsInbox();

        } else {
            // Todo : Then Set Permission
            final int REQUEST_CODE_ASK_PERMISSIONS = 123;
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);
        }


        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_PHONE_STATE)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            }
        } else {
//            TextView textView = findViewById(R.id.text);
//            textView.setText(getCallDataList());
        }

    }

    public void refreshSmsInbox() {
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;
//        arrayAdapter.clear();
        do {
            String str = "SMS From: " + smsInboxCursor.getString(indexAddress) +
                    "\n" + smsInboxCursor.getString(indexBody) + "\n";
//            arrayAdapter.add(str);
        } while (smsInboxCursor.moveToNext());
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "no permission granted", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private String getCallDataList() {
        StringBuffer sb = new StringBuffer();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return "";
        }
        Cursor managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        sb.append("Call Details:\n\n");
        while (managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            String callDuration = managedCursor.getString(duration);
            Date callDateTime = new Date(Long.valueOf(callDate));
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy HH:mm");
            String dateString = format.format(callDateTime);
            String dir = null;
            int dirCode = Integer.parseInt(callType);
            switch (dirCode) {
                case CallLog.Calls.OUTGOING_TYPE: {
                    dir  = "OUTGOING";
                    break;
                }
                case CallLog.Calls.INCOMING_TYPE: {
                    dir  = "INCOMING";
                    break;
                }
                case CallLog.Calls.MISSED_TYPE: {
                    dir  = "MISSED";
                    break;
                }
            }
            sb.append("\nPhoneNumber " + phNumber + " \nCallType " + dir + " \n CallDate " + dateString + " \nCallDuration " + callDuration);
            sb.append("\n***************");
        }
        managedCursor.close();
        return sb.toString();

    }
}
