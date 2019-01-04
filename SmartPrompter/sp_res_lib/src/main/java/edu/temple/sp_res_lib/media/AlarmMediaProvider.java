package edu.temple.sp_res_lib.media;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;

import edu.temple.sp_res_lib.utils.Constants;
import edu.temple.sp_res_lib.utils.MediaUtil;
import edu.temple.sp_res_lib.utils.StorageUtil;

import edu.temple.sp_res_lib.media.AlarmMediaContract.MEDIA_TYPE;

public class AlarmMediaProvider extends ContentProvider {

    public static final int CODE_IMAGE = 100;
    public static final int CODE_IMAGE_WITH_ID = 101;

    public static final int CODE_RINGTONE = 200;
    public static final int CODE_RINGTONE_WITH_ID = 201;

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = AlarmMediaContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MEDIA_TYPE.Image.toString(), CODE_IMAGE);
        matcher.addURI(authority, MEDIA_TYPE.Image.toString() + "/#", CODE_IMAGE_WITH_ID);

        matcher.addURI(authority, MEDIA_TYPE.Ringtone.toString(), CODE_RINGTONE);
        matcher.addURI(authority, MEDIA_TYPE.Ringtone.toString() + "/#", CODE_RINGTONE_WITH_ID);

        return matcher;
    }

    // ------------------------------------------------------------------------------------
    // ------------------------------------------------------------------------------------

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_MEDIA_TYPE = "mediaType";
    private static final String COLUMN_MEDIA = "media";

    private static final String[] ALL_COLUMNS = new String[] {
            COLUMN_ID,
            COLUMN_MEDIA_TYPE,
            COLUMN_MEDIA
    };

    private static final UriMatcher uriMatcher = buildUriMatcher();

    private File rootDir, imageDir, ringtoneDir;

    @Override
    public boolean onCreate() {
        if (!StorageUtil.isExternalStorageWritable())
            return false;

        rootDir = StorageUtil.getPublicRootDir();
        imageDir = getPublicDir(rootDir, Constants.PUBLIC_DIR_IMAGES);
        ringtoneDir = getPublicDir(rootDir, Constants.PUBLIC_DIR_RINGTONES);
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        MatrixCursor cursor = new MatrixCursor(ALL_COLUMNS);

        switch (uriMatcher.match(uri)) {

            // ------------------------------------------------------------------------------------
            // ------------------------------------------------------------------------------------

            case CODE_IMAGE_WITH_ID: {
                String _ID = uri.getLastPathSegment();
                String filepath = (imageDir.getAbsolutePath() + "/" + _ID);
                Log.i(Constants.LOG_TAG, "Attempting to retrieve image file by ID: " + _ID
                        + " \t\t using file path: " + filepath);

                MatrixCursor.RowBuilder builder = cursor.newRow();
                builder.add(COLUMN_ID, _ID);
                builder.add(COLUMN_MEDIA_TYPE, MEDIA_TYPE.Image.toString());
                builder.add(COLUMN_MEDIA, loadBitmap(filepath));
                break;
            }

            case CODE_IMAGE: {
                Log.i(Constants.LOG_TAG, "Attempting to retrieve all image media files.");
                File[] taskMediaFiles = imageDir.listFiles();
                for (File taskMediaFile : taskMediaFiles) {
                    String filepath = taskMediaFile.getAbsolutePath();
                    Log.i(Constants.LOG_TAG, "Attempting to retrieve image file "
                            + "using file path: " + filepath);

                    MatrixCursor.RowBuilder builder = cursor.newRow();
                    builder.add(COLUMN_ID, taskMediaFile.getName());
                    builder.add(COLUMN_MEDIA_TYPE, MEDIA_TYPE.Image.toString());
                    builder.add(COLUMN_MEDIA, loadBitmap(filepath));
                }
                break;
            }

            // ------------------------------------------------------------------------------------
            // ------------------------------------------------------------------------------------

            case CODE_RINGTONE_WITH_ID: {
                String _ID = uri.getLastPathSegment();
                String filepath = (ringtoneDir.getAbsolutePath() + "/" + _ID);
                Log.i(Constants.LOG_TAG, "Attempting to retrieve ringtone file by ID: " + _ID
                        + " \t\t using file path: " + filepath);

                MatrixCursor.RowBuilder builder = cursor.newRow();
                builder.add(COLUMN_ID, _ID);
                builder.add(COLUMN_MEDIA_TYPE, MEDIA_TYPE.Ringtone.toString());
                builder.add(COLUMN_MEDIA, loadRingtone(filepath));
                break;
            }

            case CODE_RINGTONE: {
                Log.i(Constants.LOG_TAG, "Attempting to retrieve all ringtone media files.");
                File[] files = ringtoneDir.listFiles();
                for (File file : files) {
                    String filepath = file.getAbsolutePath();
                    Log.i(Constants.LOG_TAG, "Attempting to retrieve ringtone file "
                            + "using file path: " + filepath);

                    MatrixCursor.RowBuilder builder = cursor.newRow();
                    builder.add(COLUMN_ID, file.getName());
                    builder.add(COLUMN_MEDIA_TYPE, MEDIA_TYPE.Ringtone.toString());
                    builder.add(COLUMN_MEDIA, loadRingtone(filepath));
                }
                break;
            }

            // ------------------------------------------------------------------------------------
            // ------------------------------------------------------------------------------------

            default:
                Log.e(Constants.LOG_TAG, "Unexpected URI: " + uri);
                return null;
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        switch (uriMatcher.match(uri)) {
            case CODE_IMAGE:
                String id = values.getAsString(COLUMN_ID);
                Bitmap media = MediaUtil.convertToBitmap(values.getAsByteArray(COLUMN_MEDIA));
                StorageUtil.writeToFile(imageDir, id, media);
                media.recycle();

                getContext().getContentResolver().notifyChange(uri, null);
                return AlarmMediaContract.getContentUriWithID(MEDIA_TYPE.Image, id);
            case CODE_RINGTONE:
                Log.e(Constants.LOG_TAG, "Programmatically inserting ringtones is "
                        + "not supported at this time.  Please utilize the Android File "
                        + "Transfer program to load ringtones manually onto your device.");
                return null;
            default:
                Log.e(Constants.LOG_TAG, "Unexpected URI: " + uri);
                return null;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case CODE_IMAGE_WITH_ID:
                int updatedFileCount = 0;
                try {
                    String _ID = uri.getLastPathSegment();
                    String filepath = (imageDir.getAbsolutePath() + "/" + _ID);
                    if ((new File(filepath)).delete())
                        updatedFileCount = 1;
                    getContext().getContentResolver().notifyChange(uri, null);
                } catch (NullPointerException ex) {
                    Log.e(Constants.LOG_TAG, "Something went wrong while trying to "
                            + "delete image file with provided URI: " + uri.toString(), ex);
                }
                return updatedFileCount;
            case CODE_RINGTONE_WITH_ID:
                Log.e(Constants.LOG_TAG, "Programmatically deleting ringtones is "
                        + "not supported at this time.  Please utilize the Android File "
                        + "Transfer program to load ringtones manually onto your device.");
                return -1;
            default:
                Log.e(Constants.LOG_TAG, "Unexpected URI: " + uri);
                return -1;
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case CODE_IMAGE:
                int updatedFileCount = 0;
                try {
                    String id = values.getAsString(COLUMN_ID);
                    Bitmap media = MediaUtil.convertToBitmap(values.getAsByteArray(COLUMN_MEDIA));
                    StorageUtil.writeToFile(imageDir, id, media);
                    media.recycle();
                    getContext().getContentResolver().notifyChange(uri, null);
                    updatedFileCount = 1;
                } catch (NullPointerException ex) {
                    Log.e(Constants.LOG_TAG, "Something went wrong while trying to "
                            + "update image file with provided URI: " + uri.toString(), ex);
                }
                return updatedFileCount;
            case CODE_RINGTONE:
                Log.e(Constants.LOG_TAG, "Programmatically updating ringtones is "
                        + "not supported at this time.  Please utilize the Android File "
                        + "Transfer program to load ringtones manually onto your device.");
                return -1;
            default:
                Log.e(Constants.LOG_TAG, "Unexpected URI: " + uri);
                return -1;
        }
    }

    // ------------------------------------------------------------------------------------
    // ------------------------------------------------------------------------------------

    private File getPublicDir(File rootDir, String subdirName) {
        File taskMediaDir = new File(rootDir, subdirName);
        if (!taskMediaDir.exists()) {
            Log.e(Constants.LOG_TAG, "Media directory: " + subdirName
                    + " does not exist!  Attempting to create.");
            if (!taskMediaDir.mkdirs())
                Log.e(Constants.LOG_TAG, "Failed to create media directory: "
                        + subdirName);
        }

        Log.i(Constants.LOG_TAG, "Retrieved public directory: "
                + taskMediaDir.getAbsolutePath());
        return taskMediaDir;
    }

    private Bitmap loadBitmap(String filepath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(filepath, options);
    }

    private Ringtone loadRingtone(String filepath) {
        // TODO - figure out how to load a ringtone from file
        return null;
    }

}