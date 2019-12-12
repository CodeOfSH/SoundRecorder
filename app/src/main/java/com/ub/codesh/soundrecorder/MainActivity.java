package com.ub.codesh.soundrecorder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MicrophoneInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };
    public static String[] PERMISSIONS_MICROPHONE = {
            Manifest.permission.RECORD_AUDIO};

    private static String Main_TAG="MainActivity";
    private static String AudioReading_TAG = "AudioReading";

    private AudioRecorder recorder;
    private Button btn_start;
    private TextView countText;

    private Boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        btn_start = findViewById(R.id.button);
        countText = findViewById(R.id.counttext);
        verifyStoragePermissions(this);
        verifyAudioPermission(this);
        recorder = AudioRecorder.getInstance();
        recorder.createDefaultAudio("Recorded_audio",44100);
    }

    public static void verifyStoragePermissions(Activity activity) {

        try {
            //check if have the permission
            int permission;
            permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // if no permission, ask for permission
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void verifyAudioPermission(Activity activity){
        try {
            //check if have the permission
            int permission;
            permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.RECORD_AUDIO");
            if (permission != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(activity,PERMISSIONS_MICROPHONE,2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clickButton(View view){
        if(recorder.getState()==Status.STATUS_NO_READY)
        {
            Log.d(Main_TAG, "=====CreateInstance======");
            recorder.createDefaultAudio("Recorded_audio",44100);

        }
        if(recorder.getState()==Status.STATUS_READY)
        {
            Log.d(Main_TAG, "=====StartRecordAudio======");
            recorder.startRecord();
            isRecording = true;
            btn_start.setText("Stop");
        }
        else if(recorder.getState() == Status.STATUS_START)
        {
            stopAll();
        }
    }

    private void stopAll(){
        if(isRecording)
        {
            Log.d(Main_TAG, "=====StopRecordAudio======");
            recorder.stopRecord();
            isRecording = false;
            btn_start.setText("Start");
        }
    }


    @Override
    protected void onStop(){
        super.onStop();
        stopAll();
    }
}
