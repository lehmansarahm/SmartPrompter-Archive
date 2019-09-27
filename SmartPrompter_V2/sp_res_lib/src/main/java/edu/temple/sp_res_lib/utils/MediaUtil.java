package edu.temple.sp_res_lib.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static edu.temple.sp_res_lib.utils.Constants.LOG_TAG;

public class MediaUtil {

    public enum AUDIO_TYPE { Alarm, Reminder, Reward, None }

    private static final String DEFAULT_AUDIO_FILE = "sp_default";
    private static final String DEFAULT_REMINDER_FILE = "sp_default_reminder";
    private static final String DEFAULT_REWARD_FILE = "sp_default_reward";
    private static final String DEFAULT_AUDIO_TYPE = "raw";

    private static final String ALARM_AUDIO_FILE = "sp_audio_alarm.mpeg4";
    private static final String REMINDER_AUDIO_FILE = "sp_audio_reminder.mpeg4";
    private static final String REWARD_AUDIO_FILE = "sp_audio_reward.mpeg4";

    private static MediaPlayer mediaPlayer;
    private static MediaRecorder mediaRecorder;


    // ========================================================================================
    // ========================================================================================


    public static Bitmap convertToBitmap(byte[] bytes) {
        try {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            return bitmap;
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Something went wrong while attempting to "
                    + "converting byte array to bitmap.", ex);
            return null;
        }
    }

    public static byte[] convertToByteArray(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }


    // ========================================================================================
    // ========================================================================================


    public interface MediaListener {
        void audioPlayComplete();
    }

    public static void playAlarmAlerts(Activity context, MediaUtil.AUDIO_TYPE audioType, MediaListener listener) {
        if (Constants.PLAY_ALARM_TONE)
            MediaUtil.playAudio(context, audioType, listener);
        if (Constants.PLAY_ALARM_VIBRATE) {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                Log.i(LOG_TAG, "Starting alarm alert vibrate!");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    vibrator.vibrate(VibrationEffect.createOneShot(Constants.ALARM_ALERT_DURATION,
                            VibrationEffect.DEFAULT_AMPLITUDE));
                else vibrator.vibrate(Constants.ALARM_ALERT_DURATION);
            }
        }
    }

    public static void stopAlarmAlerts(Context context) {
        MediaUtil.stopAudio();
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            Log.i(LOG_TAG, "Stopping vibrate!");
            vibrator.cancel();
        }
    }


    // ========================================================================================
    // ========================================================================================


    public static boolean doesCustomAudioExist(Context context, AUDIO_TYPE audioType) {
        File audioFile = getAudioFile(context, audioType);
        boolean fileExists = (audioFile != null && audioFile.exists());
        if (fileExists) Log.i(LOG_TAG, "Found audio file: " + audioFile.getAbsolutePath());
        return fileExists;
    }

    public static void playAudio(Context context, AUDIO_TYPE audioType, final MediaListener listener) {
        File audioFile = getAudioFile(context, audioType);
        if (audioFile == null || !audioFile.exists()) {
            Log.e(LOG_TAG, "Can't play non-existent file: "
                    + audioFile.getAbsolutePath() + " \t\t Playing default audio.");
            playDefaultAudio(context, audioType);
        } else {
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(audioFile.getAbsolutePath());
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        listener.audioPlayComplete();
                    }
                });
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (Exception e) {
                Log.e(LOG_TAG, "Something went wrong while trying to play audio file: "
                        + audioFile.getAbsolutePath(), e);
            }
        }
    }

    public static void stopAudio() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
            } catch (Exception ex) {
                Log.e(LOG_TAG, "Something went wrong while trying to stop the "
                        + "media player", ex);
            }
        }
    }

    public static void recordAudio(Context context, AUDIO_TYPE audioType) {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

        String outputFilePath = getAudioFile(context, audioType).getAbsolutePath();
        mediaRecorder.setOutputFile(outputFilePath);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException ex) {
            Log.e(LOG_TAG, "Something went wrong while trying to initialize the "
                    + "audio recorder.", ex);
        }
    }

    public static void stopRecord() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
            } catch (Exception ex) {
                Log.e(LOG_TAG, "Something went wrong when trying to stop the "
                        + "media recorder.", ex);
            }
        }
    }

    public static void deleteAudio(Context context, AUDIO_TYPE audioType) {
        File audioFile = getAudioFile(context, audioType);
        if (audioFile == null || !audioFile.exists()) {
            Log.e(LOG_TAG, "Can't delete non-existent file: "
                    + audioFile.getAbsolutePath());
        } else {
            if (audioFile.delete())
                Log.i(LOG_TAG, "Audio file deleted: " + audioFile.getAbsolutePath());
            else
                Log.e(LOG_TAG, "Something went wrong while attempting to delete file: "
                        + audioFile.getAbsolutePath());
        }
    }


    // ========================================================================================
    // ========================================================================================


    private static File getAudioFile(Context context, AUDIO_TYPE audioType) {
        String audioFilename = "";
        switch (audioType) {
            case Alarm:
                audioFilename = ALARM_AUDIO_FILE;
                break;
            case Reminder:
                audioFilename = REMINDER_AUDIO_FILE;
                break;
            case Reward:
                audioFilename = REWARD_AUDIO_FILE;
                break;
        }

        File audioFile = StorageUtil.getAudioFile(context, audioFilename);
        return audioFile;
    }

    private static void playDefaultAudio(Context context, AUDIO_TYPE audioType) {
        String filename;
        switch (audioType) {
            case Reminder:
                filename = DEFAULT_REMINDER_FILE;
                break;
            case Reward:
                filename = DEFAULT_REWARD_FILE;
                break;
            default:
                filename = DEFAULT_AUDIO_FILE;
                break;
        }

        int resID = context.getResources().getIdentifier(filename,
                DEFAULT_AUDIO_TYPE, context.getPackageName());
        mediaPlayer = MediaPlayer.create(context, resID);
        mediaPlayer.start();
    }

}