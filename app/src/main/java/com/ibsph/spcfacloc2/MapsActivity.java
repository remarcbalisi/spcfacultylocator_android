package com.ibsph.spcfacloc2;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String LOCATE_URL = "http://www.spcfl.ibsph.com/android/locate/";
    private RequestQueue locate_request_que;
    private StringRequest locate_request;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        locate_request_que = Volley.newRequestQueue(this);
        Bundle username = getIntent().getExtras();
        String username_string = username.getString("username");

        locate_request = new StringRequest(Request.Method.GET, LOCATE_URL+username_string, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if( jsonObject.getString("status").equals("OK") ){
//                        Intent i = new Intent(MainActivity.this, LoginActivity.class);
//                        startActivity(i);
                        JSONObject location_object = new JSONObject( jsonObject.getString("location") );
                        latitude = Double.parseDouble(location_object.getString("latitude"));
                        longitude = Double.parseDouble(location_object.getString("longitude"));
                        Toast.makeText(getApplicationContext(), "STATUS OK \n latitude: " + location_object.getString("latitude") + "\n longitude: " +location_object.getString("longitude"), Toast.LENGTH_SHORT).show();
                        LatLng spc = new LatLng(latitude, longitude);
                        mMap.addMarker(new MarkerOptions().position(spc).title("Marker in SPC"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(spc, 19.0f));
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "STATUS ERROR", Toast.LENGTH_SHORT).show();
                }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "VOLLEY ERROR", Toast.LENGTH_SHORT).show();
            }
        }
        );

        locate_request_que.add(locate_request);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // Add a marker in Sydney and move the camera
//        LatLng spc = new LatLng(8.231809, 124.236440);
//        LatLng spc = new LatLng(latitude, longitude);
//        mMap.addMarker(new MarkerOptions().position(spc).title("Marker in SPC"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(spc, 19.0f));
    }
}
