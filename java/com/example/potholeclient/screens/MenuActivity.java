package com.example.potholeclient.screens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.potholeclient.R;
import com.example.potholeclient.utils.Costants;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        TextView greetingTextView = findViewById(R.id.greetingTextView);
        String greetingMessage = "Ciao " + Costants.nickname;
        greetingTextView.setText(greetingMessage);

        TextView logoutTextView = findViewById(R.id.logoutTextView);
        logoutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Costants.nickname = "";
                Intent goToLogin = new Intent(MenuActivity.this, LoginActivity.class);
                startActivity(goToLogin);
            }
        });

        Button registerPotholesBTN = findViewById(R.id.registerPotholesBTN);
        registerPotholesBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        Button listPotholesBTN = findViewById(R.id.listPotholesBTN);
        listPotholesBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, PotholesListScreen.class);
                startActivity(intent);
            }
        });

    }
}