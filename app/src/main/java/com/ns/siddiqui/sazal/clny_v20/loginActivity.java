/*
 * Copyright (c) 2016.
 */

package com.ns.siddiqui.sazal.clny_v20;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class loginActivity extends AppCompatActivity {

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

        init();

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(loginActivity.this, MainActivity.class);
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

    private void init() {
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.SingUpButton:
                    startActivity(new Intent(loginActivity.this, singUpActivity.class));
                    finish();
                    break;
                case R.id.singInButton:
                    doLogin(userNameEditText.getText().toString().trim(), passwordEditText.getText().toString().trim());
            }
        }
    };

    private void doLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("check login", "Login Response: " + response.toString());
                hideDialog();
                if (response.contains("false")){
                    Intent intent = new Intent(loginActivity.this, MainActivity.class);
                    finish();
                    startActivity(intent);
                }
                else{
                    dialogShow(response);
                }
                /*try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session
                        session.setLogin(true);

                        // Now store the user in SQLite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String created_at = user
                                .getString("created_at");

                        // Inserting row in users table
                        db.addUser(name, email, uid, created_at);

                        // Launch main activity
                        Intent intent = new Intent(LoginActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
*/
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("chekc login", "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
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

    private void dialogShow(String s) {
        Drawable error_icon = getResources().getDrawable(R.mipmap.ic_alert);

        MaterialDialog.Builder builder = new MaterialDialog.Builder(loginActivity.this)
                .title(R.string.error)
                .content(s)
                .icon(error_icon);
        MaterialDialog dialog = builder.build();
        dialog.show();
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
