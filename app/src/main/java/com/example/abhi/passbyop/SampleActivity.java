package com.example.abhi.passbyop;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class SampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        TextView tv = findViewById(R.id.sampletext);
        String Username = getIntent().getStringExtra("User");
        tv.setText(Username + "logged in !!");
    }
}
