package com.ashwin.android.messengerdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private TextView countTextView;

    private ServiceConnection serviceConnection;
    private boolean isBound = false;

    private Intent serverIntent;

    private Messenger clientMessenger;
    private Messenger serverMessenger;

    class ClientHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case ServerService.MSG_SET_VALUE:
                    int count = msg.arg1;
                    if (countTextView != null) {
                        countTextView.setText("count: " + count);
                    }
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.w("messenger-demo", "activity: on-create");

        clientMessenger = new Messenger(new ClientHandler());

        serverIntent = new Intent(this, ServerService.class);

        countTextView = (TextView) findViewById(R.id.count_textview);
    }

    public void startServer(View v) {
        startService(serverIntent);
    }

    public void bind(View v) {
        if (serviceConnection == null) {
            serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder binder) {
                    Log.w("messenger-demo", "activity: on-service-connected");
                    serverMessenger = new Messenger(binder);
                    isBound = true;
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    serverMessenger = null;
                    isBound = false;
                }
            };
        }

        bindService(serverIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void getCount(View v) {
        if (isBound) {
            Message msg = Message.obtain(null, ServerService.MSG_GET_VALUE, 0, 0);
            msg.replyTo = clientMessenger;
            try {
                serverMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Service not bound", Toast.LENGTH_LONG).show();
        }
    }

    public void unbind(View v) {
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
            Log.w("messenger-demo", "activity: unbind service");
        }
    }

    public void stopServer(View v) {
        stopService(serverIntent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.w("messenger-demo", "activity: on-stop");
        unbind(null);
    }
}
