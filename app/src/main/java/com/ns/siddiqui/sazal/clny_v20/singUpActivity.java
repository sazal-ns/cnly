/*
 * Copyright (c) 2016. By Noor Nabiul Alam Siddiqui
 */

package com.ns.siddiqui.sazal.clny_v20;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ns.siddiqui.sazal.clny_v20.AppConfig.AppConfig;
import com.ns.siddiqui.sazal.clny_v20.helpingHand.SQLiteHandler;
import com.ns.siddiqui.sazal.clny_v20.helpingHand.SessionManager;

import java.util.HashMap;
import java.util.Map;

public class SingUpActivity extends AppCompatActivity {

    private EditText singUpUserNameEditText,singUpEmailEditText,singUpPasswordEditText,singUpRepeatPasswordEditText;
    private Button singUpButton;

    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    private boolean isOK= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);

        singUpButton = (Button) findViewById(R.id.singUpButton);
        singUpEmailEditText = (EditText) findViewById(R.id.singUpEmailEditText);
        singUpUserNameEditText = (EditText) findViewById(R.id.singUpUserNameEditText);
        singUpPasswordEditText = (EditText) findViewById(R.id.singUpPasswordEditText);
        singUpRepeatPasswordEditText = (EditText) findViewById(R.id.singUpRepeatPasswordEditText);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Check if User is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(SingUpActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }

        singUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = singUpUserNameEditText.getText().toString().trim();
                String email = singUpEmailEditText.getText().toString().trim();
                String password = singUpPasswordEditText.getText().toString().trim();
                String passwordRepeat = singUpRepeatPasswordEditText.getText().toString().trim();

                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {

                    if (password.contains(passwordRepeat)){
                        registerUser(name, email, password);
                    }else{
                        dialogShow("Passwords are not same!!!");
                    }
                } else {
                    dialogShow("Please enter your details!!!");
                }
            }
        });


    }

    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }

    private void registerUser(final String name, final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("OnResponse", "Register Response: " + response);
                hideDialog();
                if (response.contains("false")){
                    Intent intent = new Intent(SingUpActivity.this, LoginActivity.class);
                    finish();
                    startActivity(intent);
                }
                else{
                    dialogShow(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("OnErrorResponse", "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }){

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("userName", name);
                params.put("emailAddress", email);
                params.put("password", password);

                return params;
            }
        };
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

    private void dialogShow(String s){
        Drawable error_icon = getResources().getDrawable(R.mipmap.ic_alert);

        MaterialDialog.Builder builder = new MaterialDialog.Builder(SingUpActivity.this)
                .title(R.string.error)
                .content(s)
                .icon(error_icon);
        MaterialDialog dialog = builder.build();
        dialog.show();
    }
}
