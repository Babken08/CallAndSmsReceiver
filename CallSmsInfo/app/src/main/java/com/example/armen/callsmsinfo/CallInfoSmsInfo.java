package com.example.armen.callsmsinfo;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import java.util.ArrayList;
import java.util.List;

public class CallInfoSmsInfo extends IntentService {
    private List<ModelCall> list = new ArrayList<>();
    Handler handler;
    public CallInfoSmsInfo() {
        super("CallInfoSmsInfo");
    }
    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        handler = new Handler();
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent != null && intent.hasExtra("keyCall")){
            final String state  = intent.getStringExtra("keyCall");
            Intent i = new Intent("response_from_my_intent_service");
            i.putExtra("key_call_detail", state);
            LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        }

        if(intent != null && intent.hasExtra("keySMS")){
            final String state  = intent.getStringExtra("keySMS");
            Intent i = new Intent("from_my_intent_service");
            i.putExtra("key_sms_detail", state);
            LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        }

    }
}
