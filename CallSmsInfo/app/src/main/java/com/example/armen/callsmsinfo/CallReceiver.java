package com.example.armen.callsmsinfo;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;

import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CallReceiver extends BroadcastReceiver {
    private List<ModelCall> list = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String state  = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
//            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)){
//                Toast.makeText(context, "Ringing", Toast.LENGTH_SHORT).show();
//
//            }
//            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_OFFHOOK)){
//                Toast.makeText(context, "OFFHOOK", Toast.LENGTH_SHORT).show();
//            }
            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE)){
                Intent serviceIntent = new Intent(context, CallInfoSmsInfo.class);
                serviceIntent.putExtra("keyCall", getCallDataList(context));
                context.startService(serviceIntent);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getCallDataList(Context context) {
        StringBuffer sb = new StringBuffer();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return "";
        }
        Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        sb.append("Call Details:\n\n");
        if (managedCursor.moveToFirst()) {
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
                ModelCall modelCall = new ModelCall();
                modelCall.setPhNumber(phNumber);
                modelCall.setDir(dir);
                modelCall.setCallDuration(callDuration);
                modelCall.setDateString(dateString);
                list.add(modelCall);

//                sb.append("\nPhoneNumber " + phNumber + " \nCallType " + dir + " \n CallDate " + dateString + " \nCallDuration " + callDuration);
//                sb.append("\n***************");
            }

            ModelCall m = list.get(0);
        sb.append("\nPhoneNumber " + m.getPhNumber() + " \nCallType " + m.getDir() + " \n CallDate " + m.getDateString() + " \nCallDuration " + m.getCallDuration());
        managedCursor.close();
        return sb.toString();

    }
}
