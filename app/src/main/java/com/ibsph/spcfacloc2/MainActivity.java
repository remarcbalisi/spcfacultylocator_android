package com.ibsph.spcfacloc2;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
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

public class MainActivity extends AppCompatActivity {

    private static final String LOGIN_URL = "http://www.spcfl.ibsph.com/android/";
    private TextView device_id;
    private RequestQueue login_request_que;
    private StringRequest login_request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getting unique id for device
        final String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        device_id = (TextView) findViewById(R.id.android_id_tv);
        device_id.setText(id);
        device_id.setText("Welcome To SPC-Faculty Locator\n Loading ...");

        login_request_que = Volley.newRequestQueue(this);

        login_request = new StringRequest(Request.Method.GET, LOGIN_URL+id+"1", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Toast.makeText( getApplicationContext(), jsonObject.getString("user_type"), Toast.LENGTH_SHORT ).show();
                    if( jsonObject.getString("user_type").equals("student") ){
                        Intent i = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(i);
//                            device_id.setText("json response ON ERROR RESPONSE: " + id);
                    }
                    else{
                        Intent i = new Intent(MainActivity.this, FacultyHomeActivity2.class);
                        startActivity(i);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    device_id.setText("json response ERROR");
                }
            }

            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                device_id.setText("json response ON ERROR RESPONSE: " + error);
            }
        }
        );

        login_request_que.add(login_request);
    }
}
