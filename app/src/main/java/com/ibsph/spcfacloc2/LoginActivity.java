package com.ibsph.spcfacloc2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private static final String LOGIN_URL = "http://www.spcfl.ibsph.com/android/login/";
    private EditText username, password;
    private Button login_btn;
    private RequestQueue login_request_que;
    private StringRequest login_request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText) findViewById(R.id.username_tf);
        password = (EditText) findViewById(R.id.password_tf);

        login_request_que = Volley.newRequestQueue(this);

        login_btn = (Button) findViewById(R.id.login_btn);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                login_request = new StringRequest(Request.Method.GET, LOGIN_URL+username.getText().toString()+"/"+password.getText().toString(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if ( jsonObject.getString("status").equals("OK") ){
                                Intent intent = new Intent(LoginActivity.this, StudentHomeActivity.class);
                                intent.putExtra("online_faculties", jsonObject.getString("online_faculties"));
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText( getApplicationContext(), "FAILED: "+jsonObject.getString("message"), Toast.LENGTH_SHORT ).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
                );

                login_request_que.add(login_request);

            }
        });
    }
}
