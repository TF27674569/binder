package com.cabinet.binder;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.binder.ITestAidlInterface;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ITestAidlInterface asInterface = ITestAidlInterface.Stub.asInterface(service);
            try {
                Toast.makeText(MainActivity.this, asInterface.getName()+"  " + asInterface.getAge(), Toast.LENGTH_SHORT).show();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public void startService(View view) {
        Intent intent = new Intent();
        intent.setAction("TestAidlServices");
        // 需要加上包名
        intent.setPackage(getPackageName());
        bindService(intent,connection,BIND_AUTO_CREATE);
    }
}
