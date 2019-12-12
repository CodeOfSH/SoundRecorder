package com.ub.codesh.soundrecorder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

enum Status {
    STATUS_NO_READY,
    STATUS_READY,
    STATUS_START,
    STATUS_STOP
}

public class AudioRecorder {
    private static AudioRecorder audioRecorder;
    // Audio source：mic
    private final static int AUDIO_INPUT = MediaRecorder.AudioSource.UNPROCESSED;
    // Sample rate
    // Here we choose 44100
    private final static int AUDIO_SAMPLE_RATE = 44100;
    // Audio channel: single channel
    private final static int AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_STEREO;
    // Audio format：PCM_16BIT
    private final static int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    // size of buffer bytes
    private int bufferSizeInBytes = 0;
    // system audio record class
    private AudioRecord audioRecord;
    // record status
    private Status status = Status.STATUS_NO_READY;
    // filename
    private String fileName;
    // system time format
    private SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
    // class to change format
    private PcmToWavUtil pcm2wav;

    private AudioRecorder() {
    }

    //one instance mode
    public static AudioRecorder getInstance() {
        if (audioRecorder == null) {
            audioRecorder = new AudioRecorder();
        }
        return audioRecorder;
    }

    public Status getState(){
        return status;
    }

    /**
     * create record object
     * @param inputfileName the filename to be stored
     */
    public void createDefaultAudio(String inputfileName, int sample_rate) {
        // calculate the size for the buffer size
        int audioSource = AUDIO_INPUT;
        int sampleRateInHz = sample_rate;
        int channelConfig = AUDIO_CHANNEL;
        int audioFormat = AUDIO_ENCODING;
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,
                channelConfig, audioFormat);
        audioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);
        pcm2wav = new PcmToWavUtil(sampleRateInHz,channelConfig,audioFormat);

        // get record
        String folderName = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            if (path != null) {
                folderName = path + "/AudioRecord/";
            }
        }
        File fileRobo = new File(folderName);
        if (!fileRobo.exists()) {
            fileRobo.mkdir();
        }
        String deviceModel = Build.MODEL;
        this.fileName = folderName +deviceModel+"-"+ inputfileName +"-" + format.format(System.currentTimeMillis()) + ".pcm";
        status = Status.STATUS_READY;
    }

    /**
     * Start recording
     */
    public void startRecord() {
        if (status == Status.STATUS_NO_READY || TextUtils.isEmpty(fileName)) {
            throw new IllegalStateException("Cannot initialize record, check the record permission");
        }
        if (status == Status.STATUS_START) {
            throw new IllegalStateException("Now recording");
        }
        Log.d("AudioRecorder","===StartRecord==="+audioRecord.getState());
        audioRecord.startRecording();

        new Thread(new Runnable() {
            @Override
            public void run() {
                writeDataTOFile();
            }
        }).start();
    }

    /**
     * stop record
     */
    public void stopRecord() {
        Log.d("AudioRecorder","===StopRecord===");
        if (status == Status.STATUS_NO_READY || status == Status.STATUS_READY) {
            throw new IllegalStateException("Record has not start");
        } else {
            audioRecord.stop();
            status = Status.STATUS_STOP;
            release();
        }
    }

    /**
     * cancel record
     */
//    public void canel() {
//        filesName.clear();
//        fileName = null;
//        if (audioRecord != null) {
//            audioRecord.release();
//            audioRecord = null;
//        }
//        status = Status.STATUS_NO_READY;
//    }

    /**
     * release resources
     */
    public void release() {
        Log.d("AudioRecorder","===Release===");
        try {
            Log.d("AudioRecorder", "=====PCMFileToWAVFile======");
            //makePCMFileToWAVFile();
            String newfilename = fileName.substring(0,fileName.length()-4);
            newfilename += ".wav";
            pcm2wav.pcmToWav(fileName,newfilename);
        } catch (IllegalStateException e) {
            throw new IllegalStateException(e.getMessage());
        }
        status = Status.STATUS_NO_READY;
    }

    /**
     * write data to file
     */
    private void writeDataTOFile() {
        // array of bytes to store data
        byte[] audiodata = new byte[bufferSizeInBytes];

        FileOutputStream fos = null;
        int readsize = 0;
        try {
            String currentFileName = this.fileName;
            File file = new File(currentFileName);
            // setup output stream
            fos = new FileOutputStream(file);
        } catch (Exception e) {
            Log.e("AudioRecorder", e.getMessage());
            throw new IllegalStateException(e.getMessage());
        }
        //change to record state
        status = Status.STATUS_START;
        while (status == Status.STATUS_START) {
            readsize = audioRecord.read(audiodata, 0, bufferSizeInBytes);
            if (AudioRecord.ERROR_INVALID_OPERATION != readsize && fos != null) {
                try {
                    fos.write(audiodata);
                } catch (IOException e) {
                    Log.e("AudioRecorder", e.getMessage());
                }
            }
        }
        try {
            if (fos != null) {
                fos.close();// close stream
            }
        } catch (IOException e) {
            Log.e("AudioRecorder", e.getMessage());
        }
    }
}