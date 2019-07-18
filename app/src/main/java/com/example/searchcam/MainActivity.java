package com.example.searchcam;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static Button startBtn;
    String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        OnClickStartListner();
    }

    public void OnClickStartListner(){
        startBtn = findViewById(R.id.startBtn);
        startBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("AppLogs", "in onCreate() at " + time);
                        Intent camCaptureIntent = new Intent("com.example.searchcam.CamCaptureActivity");
                        startActivity(camCaptureIntent);
                    }
                }
        );
    }
}
