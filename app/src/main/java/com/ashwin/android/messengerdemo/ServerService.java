package com.ashwin.android.messengerdemo;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

public class ServerService extends Service {
    static final int MSG_GET_VALUE = 1;
    public static final int MSG_SET_VALUE = 2;

    private int count = -1;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.w("messenger-demo", "server-service: on-create");
    }

    private int getCount() {
        count = count + 1;
        return count;
    }

    Messenger messenger = new Messenger(new ServerHandler());

    class ServerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_GET_VALUE:
                    try {
                        Messenger clientMessenger = msg.replyTo;
                        Message reply = Message.obtain(null, MSG_SET_VALUE, getCount(), 0);
                        clientMessenger.send(reply);
                    } catch (RemoteException e) {
                        Log.e("messenger-demo", "Exception while sending message to client", e);
                    }
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.w("messenger-demo", "server-service: on-start");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.w("messenger-demo", "server-service: on-bind");
        return messenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.w("messenger-demo", "server-service: on-unbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.w("messenger-demo", "server-service: on-rebind");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.w("messenger-demo", "server-service: on-destroy");
    }
}
