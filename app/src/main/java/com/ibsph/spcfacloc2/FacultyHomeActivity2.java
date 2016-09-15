package com.ibsph.spcfacloc2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class FacultyHomeActivity2 extends AppCompatActivity {

    private TextView latitude_tv, longitude_tv;
    public static final String TAG = "WEAVER_";
    private Button logout_btn;
    private RequestQueue updatelocation_request_que;
    private StringRequest updatelocation_request;

    private static final String LOCATION_UPDATE_URL = "http://www.spcfl.ibsph.com/android/";
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    public LocationManager mLocationManager;

    int updates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_home2);

        logout_btn = (Button) findViewById(R.id.logout_btn);

        updates = 0;
        handlePermissionsAndGetLocation();

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                System.exit(0);
            }
        });
    }

    public void uploadLocation(final double latitude, final double longitude ){

        //getting unique id for device
        String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        updatelocation_request_que = Volley.newRequestQueue(this);

        updatelocation_request = new StringRequest(Request.Method.POST, LOCATION_UPDATE_URL+id+"/"+latitude+"/"+longitude, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    if ( jsonObject.names().get(1).equals("status") ){
                        if( jsonObject.getString("status").equals("OK") ){
                            Toast.makeText( getApplicationContext(), "Status: OK", Toast.LENGTH_SHORT ).show();
                        }
                        else{
                            Toast.makeText( getApplicationContext(), "Status: FAILED", Toast.LENGTH_SHORT ).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText( getApplicationContext(), "Status: FAILED" + e , Toast.LENGTH_SHORT ).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText( getApplicationContext(), "Status: FAILED VOLLEY" , Toast.LENGTH_SHORT ).show();
            }
        }
        );

        updatelocation_request_que.add(updatelocation_request);

    }

    public void logout(){
        //getting unique id for device
        String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        updatelocation_request_que = Volley.newRequestQueue(this);

        updatelocation_request = new StringRequest(Request.Method.GET, LOCATION_UPDATE_URL+"logout"+"/"+id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    if( jsonObject.getString("status").equals("OK") ){
                        Toast.makeText( getApplicationContext(), "Status: SUCCESSFULLY LOGGED OUT", Toast.LENGTH_SHORT ).show();
                    }
                    else{
                        Toast.makeText( getApplicationContext(), "Status: FAILED", Toast.LENGTH_SHORT ).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText( getApplicationContext(), "Status: FAILED" + e , Toast.LENGTH_SHORT ).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText( getApplicationContext(), "Status: FAILED VOLLEY" , Toast.LENGTH_SHORT ).show();
            }
        }
        );

        updatelocation_request_que.add(updatelocation_request);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Accepted
                    getLocation();
                } else {
                    // Denied
                    Toast.makeText(FacultyHomeActivity2.this, "LOCATION Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void handlePermissionsAndGetLocation() {
        Log.v(TAG, "handlePermissionsAndGetLocation");
        int hasWriteContactsPermission = checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
        getLocation();//if already has permission
    }

    protected void getLocation() {
        Log.v(TAG, "GetLocation");
        int LOCATION_REFRESH_TIME = 1000;
        int LOCATION_REFRESH_DISTANCE = 5;

        if (!(checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            Log.v("WEAVER_", "Has permission");
            mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                    LOCATION_REFRESH_DISTANCE, mLocationListener);
        } else {
            Log.v("WEAVER_", "Does not have permission");
        }

    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.v("WEAVER_", "Location Change");
//            latitude_tv.setText(String.valueOf(updates) + " updates");
//            latitude_tv.append("\nLatitude: " + location.getLatitude()+ "\nLongitude: "+location.getLongitude() );
            uploadLocation( location.getLatitude(), location.getLongitude() );
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

}
