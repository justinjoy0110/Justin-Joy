package com.example.justin.finalsocket;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toolbar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
/**
 * Created by justin on 13-04-2018.
 */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import static java.lang.Character.isDigit;

public class MainActivity2 extends AppCompatActivity {

    boolean tempClick = false;
    boolean humidClick = false;
    String IPAdress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Button temp = (Button) findViewById(R.id.temperature);
        Button humid = (Button) findViewById(R.id.humidity);
        Intent intent = getIntent();
        IPAdress = intent.getStringExtra("ipAddress");
        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempClick = true;
                toastMsg("Temperature!");
                Intent intent1 = new Intent(MainActivity2.this, MainActivity3.class);
                intent1.putExtra("ipAdd", IPAdress);
                startActivity(intent1);

            }


            public void toastMsg(String msg) {

                Toast.makeText(MainActivity2.this, msg, Toast.LENGTH_SHORT).show();

            }

        });
        humid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                humidClick = true;
                toastMsg("Humidity!");
            }

            public void toastMsg(String msg) {

                Toast.makeText(MainActivity2.this, msg, Toast.LENGTH_SHORT).show();

            }


        });

    }
}







