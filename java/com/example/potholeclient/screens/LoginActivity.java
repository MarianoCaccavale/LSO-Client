package com.example.potholeclient.screens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.potholeclient.R;
import com.example.potholeclient.utils.Costants;
import com.example.potholeclient.utils.Network;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText editText = findViewById(R.id.editTextNickname);

        Button loginBtn = findViewById(R.id.loginBTN);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(editText.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Devi inserire un nome prima di continuare", Toast.LENGTH_LONG).show();
                    return;
                }

                Costants.nickname = editText.getText().toString();

                Thread getToleranceThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Costants.toleranceThreshold = Network.getTolerance();
                    }
                });

                getToleranceThread.setPriority(10);
                getToleranceThread.start();

                Intent mainActivity = new Intent(LoginActivity.this, MenuActivity.class);
                startActivity(mainActivity);
            }
        });
    }
}