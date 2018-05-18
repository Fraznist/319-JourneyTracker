package com.example.eakgun14.journeytracker.RouteService;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class AudioManager {

    private Context c;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;

    private boolean mStartRecording = true;
    private boolean mStartPlaying = true;

    public AudioManager(Context context) {
        c = context;
    }

    public void onRecord() {
        if (mStartRecording) {
            startRecording();
        } else {
            stopRecording();
        }
        mStartRecording = !mStartRecording;
    }

    public void onPlay(String fileName) {
        if (mStartPlaying) {
            startPlaying(fileName);
        } else {
            stopPlaying();
        }
        mStartPlaying = !mStartPlaying;
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(c.getExternalCacheDir()+"/tempAudio");
        Log.d("audio", "" + c.getExternalCacheDir());
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e("audio", "fail: " + e.toString());
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    private void startPlaying(String fileName) {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(c.getExternalFilesDir(Environment.DIRECTORY_MUSIC)+"/"+fileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e("audio", "prepare() failed: " + e.toString());
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    public void changeAudioFileName(String newJourneyName) {
        String sourceName = "tempAudio";
        File musicDir = c.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File cacheDir = c.getExternalCacheDir();

        File sourceAudio = new File(cacheDir, sourceName);

        if(!sourceAudio.exists())
            return;

        if (sourceAudio.renameTo(new File(musicDir, newJourneyName)))
            Log.d("rename", "YE BOIII");
        else
            Log.d("rename", "no boi :(");
    }
}
