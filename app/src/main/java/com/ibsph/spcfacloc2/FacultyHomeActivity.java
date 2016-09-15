package com.ibsph.spcfacloc2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class FacultyHomeActivity extends AppCompatActivity {

    private TextView title_tv;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onResume(){
        super.onResume();
        if( broadcastReceiver == null ){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    title_tv.append( "\n" + intent.getExtras().get("coordinates") );
                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if( broadcastReceiver != null ){
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_home);

        title_tv = (TextView) findViewById(R.id.title_tv);

        if( !runtime_permissions() )
            open_gps();
    }

    private void open_gps() {
        Intent i = new Intent(getApplicationContext(), GPSService.class);
        startService(i);
    }

    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            title_tv.setText("runtime permission is true");
            return true;
        }
        title_tv.setText("runtime permission is false");
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if( requestCode == 100 ){
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED ){
                open_gps();
            }
            else{
                runtime_permissions();
            }
        }

    }
}
