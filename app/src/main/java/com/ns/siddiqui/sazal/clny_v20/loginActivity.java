/*
 * Copyright (c) 2016.
 */

package com.ns.siddiqui.sazal.clny_v20;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.ns.siddiqui.sazal.clny_v20.AppConfig.AppConfig;
import com.ns.siddiqui.sazal.clny_v20.helpingHand.DialogShow;
import com.ns.siddiqui.sazal.clny_v20.helpingHand.PrefUtils;
import com.ns.siddiqui.sazal.clny_v20.helpingHand.SQLiteHandler;
import com.ns.siddiqui.sazal.clny_v20.helpingHand.SessionManager;
import com.ns.siddiqui.sazal.clny_v20.model.FbUser;
import com.ns.siddiqui.sazal.clny_v20.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
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

    private CallbackManager callbackManager;
    private LoginButton loginButton;
    FbUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();

        if(PrefUtils.getCurrentUser(LoginActivity.this) != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("fb","itIs");
            startActivity(intent);
            finish();
        }

/*
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.ns.siddiqui.sazal.clny_v20",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }*/

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
        fbLoginButton.setOnClickListener(onClickListener);
        googleButton = (Button) findViewById(R.id.googleButton);
        googleButton.setTypeface(roboto);
        phoneButton = (Button) findViewById(R.id.phoneButton);
        phoneButton.setTypeface(roboto);
        SingUpButton = (Button) findViewById(R.id.SingUpButton);
        SingUpButton.setTypeface(roboto);
        SingUpButton.setOnClickListener(onClickListener);
        ToUButton = (Button) findViewById(R.id.ToUButton);
        ToUButton.setTypeface(roboto);

        loginButton= (LoginButton) findViewById(R.id.login_button);
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
                    break;
            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();

        callbackManager=CallbackManager.Factory.create();

        loginButton.setReadPermissions("public_profile", "email","user_friends");

        fbLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pDialog.setMessage("Loading...");
                pDialog.show();

                loginButton.performClick();

                loginButton.setPressed(true);

                loginButton.invalidate();

                loginButton.registerCallback(callbackManager, mCallBack);

                loginButton.setPressed(false);

                loginButton.invalidate();

            }
        });

    }

    private FacebookCallback<LoginResult> mCallBack = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            pDialog.dismiss();

            // App code
            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {

                            Log.e("response: ", response + "");
                            try {
                                user = new FbUser();
                                user.facebookID = object.getString("id");
                                user.email = object.getString("email");
                                user.name = object.getString("name");
                                user.gender = object.getString("gender");
                                PrefUtils.setCurrentUser(user,LoginActivity.this);

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("fb","itIs");
                            startActivity(intent);
                            finish();
                        }

                    });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender, birthday");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
            pDialog.dismiss();
            Log.d("*****Cancel****","On cancel");
        }

        @Override
        public void onError(FacebookException error) {
            pDialog.dismiss();
            Log.d("****Error****",error.toString());
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
                            startActivity(intent);
                            finish();


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
