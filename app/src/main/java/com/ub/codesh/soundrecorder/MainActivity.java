package com.ub.codesh.soundrecorder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
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
    private RadioGroup channel_group;

    private int audio_channel =  AudioFormat.CHANNEL_IN_MONO;
    private String filename = "Recorded_audio_MONO";

    private Boolean isRecording = false;
    private static int sample_rate = 44100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        btn_start = findViewById(R.id.button);
        verifyStoragePermissions(this);
        verifyAudioPermission(this);
        recorder = AudioRecorder.getInstance();

        channel_group=findViewById(R.id.channel_group);
        channel_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.channel_btn1:
                        audio_channel = AudioFormat.CHANNEL_IN_MONO;
                        filename = "Recorded_audio_Mono";
//                        System.out.println("channel set to mono");
                        break;
                    case R.id.channel_btn2:
                        audio_channel = AudioFormat.CHANNEL_IN_STEREO;
                        filename = "Recorded_audio_Stereo";
//                        System.out.println("channel set to stereo");
                        break;
                    default:
                        break;
                }
            }
        });
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
            recorder.createDefaultAudio(filename, sample_rate, audio_channel);

        }
        if(recorder.getState()==Status.STATUS_READY)
        {
            Log.d(Main_TAG, "=====StartRecordAudio======");
            recorder.startRecord();
            isRecording = true;
            btn_start.setText(R.string.btn_stop);
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
            btn_start.setText(R.string.btn_start);
        }
    }


    @Override
    protected void onStop(){
        super.onStop();
        stopAll();
    }
}
