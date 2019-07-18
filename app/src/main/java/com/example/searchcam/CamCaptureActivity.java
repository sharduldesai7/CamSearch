package com.example.searchcam;

import androidx.annotation.NonNull;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceImageLabelerOptions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class CamCaptureActivity extends AppCompatActivity {

    Button btnTakePic;
    ImageView imageView;
    String pathToFile;
    String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    //private FirebaseAnalytics imageAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cam_capture_activity);
        btnTakePic = findViewById(R.id.btnTakePic);
        if(Build.VERSION.SDK_INT >= 23){
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }
        final EditText imageName = (EditText) findViewById(R.id.imageDes);
        btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CaptureActionHandler();
            }
        });
        Log.d("AppLogs", "in onCreate() at " + time);
        imageView = findViewById(R.id.image);
        //imageAnalytics = FirebaseAnalytics.getInstance(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 1){
                Bitmap bitmap = BitmapFactory.decodeFile(pathToFile);
                imageView.setImageBitmap(bitmap);
                Log.d("AppLogs", "before analysis " + time );
                analyseImage(bitmap);
                Log.d("AppLogs", "after analysis " + time );
            }
        }
    }

    private void analyseImage(Bitmap bitmap) {

        FirebaseVisionImage inputImage = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionOnDeviceImageLabelerOptions options =
        new FirebaseVisionOnDeviceImageLabelerOptions.Builder()
            .setConfidenceThreshold(0.9f)
            .build();
        FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance().getOnDeviceImageLabeler(options);
       String imageDes =null;
       labeler.processImage(inputImage)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                        for(FirebaseVisionImageLabel label: labels){
                            String imageDescription = label.getText();
                            float confidence = label.getConfidence();
                            Log.d("AppLogs", "confidence for the image " + imageDescription + " at " + time + " is: " + Float.toString(confidence));
                            EditText imageDes = null;
                            imageDes = findViewById(R.id.imageDes);
                            imageDes.setText(imageDescription, TextView.BufferType.EDITABLE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("AppLogs", "Image Analysis Failed " + time );
                        EditText imageDes = findViewById(R.id.imageDes);
                        imageDes.setText("Failed", TextView.BufferType.EDITABLE);
                    }
                });

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

        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(time, ".jpg", storageDir);
        } catch (IOException e) {
            Log.d("AppLogs", "Exception" + e.toString());
        }
        return image;
    }
}
