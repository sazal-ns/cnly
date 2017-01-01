/*
 * Copyright (c) 2016.
 */

package com.ns.siddiqui.sazal.clny_v20;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ns.siddiqui.sazal.clny_v20.AppConfig.AppConfig;
import com.ns.siddiqui.sazal.clny_v20.helpingHand.DialogShow;
import com.ns.siddiqui.sazal.clny_v20.helpingHand.SQLiteHandler;
import com.ns.siddiqui.sazal.clny_v20.helpingHand.SessionManager;
import com.ns.siddiqui.sazal.clny_v20.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

   private TextView singinTextView,login_singup_textView,aggreeTextView;
   private EditText userNameEditText,passwordEditText;
   private Button singInButton,fbLoginButton,googleButton,phoneButton,SingUpButton,ToUButton;
   private Typeface roboto;
    Intent intent;

    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preInit();

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if User is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void preInit() {
        roboto = Typeface.createFromAsset(getAssets(),"fonts/Roboto.ttf");
        intent = new Intent();

        singinTextView = (TextView) findViewById(R.id.singinTextView);
        singinTextView.setTypeface(roboto);
        login_singup_textView = (TextView) findViewById(R.id.login_singup_textView);
        login_singup_textView.setTypeface(roboto);
        aggreeTextView = (TextView) findViewById(R.id.aggreeTextView);
        aggreeTextView.setTypeface(roboto);

        userNameEditText = (EditText) findViewById(R.id.userNameEditText);
        userNameEditText.setTypeface(roboto);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        passwordEditText.setTypeface(roboto);

        singInButton = (Button) findViewById(R.id.singInButton);
        singInButton.setTypeface(roboto);
        singInButton.setOnClickListener(onClickListener);
        fbLoginButton = (Button) findViewById(R.id.fbLoginButton);
        fbLoginButton.setTypeface(roboto);
        googleButton = (Button) findViewById(R.id.googleButton);
        googleButton.setTypeface(roboto);
        phoneButton = (Button) findViewById(R.id.phoneButton);
        phoneButton.setTypeface(roboto);
        SingUpButton = (Button) findViewById(R.id.SingUpButton);
        SingUpButton.setTypeface(roboto);
        SingUpButton.setOnClickListener(onClickListener);
        ToUButton = (Button) findViewById(R.id.ToUButton);
        ToUButton.setTypeface(roboto);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.SingUpButton:
                    startActivity(new Intent(LoginActivity.this, SingUpActivity.class));
                    finish();
                    break;
                case R.id.singInButton:
                    doLogin(userNameEditText.getText().toString().trim(), passwordEditText.getText().toString().trim());
            }
        }
    };

    private void doLogin(final String email, final String password) {

        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("check login", "Login Response: " + response);
                hideDialog();
                JSONObject object1 = null;
                try {
                    object1 = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (response.contains("false")){
                    try {

                        JSONObject object = new JSONObject(object1.getString("user"));

                            User.setUserName(object.getString("user"));
                            User.setEmailAddress(object.getString("email"));
                            User.setId(object.getString("id"));
                            User.setCreatedOn(object.getString("createdOn"));
                            User.setBio(object.getString("bio"));
                            User.setFavMusic(object.getString("favMusic"));
                            User.setFavPet(object.getString("favPet"));
                            User.setFirstName(object.getString("firstName"));
                            User.setFullAddress(object.getString("fullAddress"));
                            User.setImageLink(object.getString("imageLink"));
                            User.setLastName(object.getString("lastName"));
                            User.setUnique_id(object.getString("u_id"));
                            User.setUpdatedOn(object.getString("updatedOn"));

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            finish();
                            startActivity(intent);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    try {
                        /*dialogShow(object1.getString("error_msg"));*/
                        assert object1 != null;
                        new DialogShow(LoginActivity.this,"Login Failed !!!",object1.getString("error_msg"),getResources().getDrawable(R.mipmap.ic_alert));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("chekc login", "Login Error: " + error.getMessage());
                /*dialogShow(error.getMessage()+"\n Please Try Again");*/
                new DialogShow( LoginActivity.this,"Server Error","Please Try Again",getResources().getDrawable(R.mipmap.ic_alert));
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);

                return params;
            }
        };

        // Adding request to request queue
       /* AppController.getInstance().addToRequestQueue(strReq, tag_string_req);*/
        Volley.newRequestQueue(this).add(request);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
