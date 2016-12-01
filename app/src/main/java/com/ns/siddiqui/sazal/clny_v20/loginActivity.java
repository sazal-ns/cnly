/*
 * Copyright (c) 2016.
 */

package com.ns.siddiqui.sazal.clny_v20;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class loginActivity extends AppCompatActivity {

   private TextView singinTextView,login_singup_textView,aggreeTextView;
   private EditText userNameEditText,passwordEditText;
   private Button singInButton,fbLoginButton,googleButton,phoneButton,SingUpButton,ToUButton;
   private Typeface roboto;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preInit();

        init();
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
                    break;
                case R.id.singInButton:
                    startActivity(new Intent(loginActivity.this, MapsActivity.class));
                    finish();
                    break;
            }
        }
    };
}
