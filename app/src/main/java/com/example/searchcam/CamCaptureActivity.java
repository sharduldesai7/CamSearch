package com.example.searchcam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class CamCaptureActivity extends AppCompatActivity {

    Button btnTakePic;
    ImageView imageView;
    String pathToFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cam_capture_activity);
        btnTakePic = findViewById(R.id.btnTakePic);
        if(Build.VERSION.SDK_INT >= 23){
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }
        btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CaptureActionHandler();
            }
        });

        imageView = findViewById(R.id.image);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 1){
                Bitmap bitmap = BitmapFactory.decodeFile(pathToFile);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    private void CaptureActionHandler() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePicture.resolveActivity(getPackageManager()) != null){
            File capturedPic = createPhotoFile();
            if(capturedPic != null){
                pathToFile = capturedPic.getAbsolutePath();
                Uri picURI = FileProvider.getUriForFile(CamCaptureActivity.this, "com.example.searchcam", capturedPic);
                takePicture.putExtra(MediaStore.EXTRA_OUTPUT, picURI);
                startActivityForResult(takePicture, 1);
            }
        }
    }

    private File createPhotoFile() {
        String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(name, ".jpg", storageDir);
        } catch (IOException e) {
            Log.d("AppLogs", "Exception" + e.toString());
        }
        return image;
    }
}
