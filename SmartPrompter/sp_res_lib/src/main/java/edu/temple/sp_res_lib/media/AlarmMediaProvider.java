package edu.temple.sp_res_lib.media;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;

import edu.temple.sp_res_lib.utils.Constants;
import edu.temple.sp_res_lib.utils.MediaUtil;
import edu.temple.sp_res_lib.utils.StorageUtil;

public class AlarmMediaProvider extends ContentProvider {

    public static final int CODE_IMAGE = 100;
    public static final int CODE_RINGTONE = 200;

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = AlarmMediaContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, AlarmMediaContract.ImageEntry.TABLE_NAME, CODE_IMAGE);
        matcher.addURI(authority, AlarmMediaContract.RingtoneEntry.TABLE_NAME, CODE_RINGTONE);

        return matcher;
    }

    // ------------------------------------------------------------------------------------
    // ------------------------------------------------------------------------------------

    private static final UriMatcher uriMatcher = buildUriMatcher();

    private File rootDir, imageDir, ringtoneDir;

    @Override
    public boolean onCreate() {
        if (!StorageUtil.isExternalStorageWritable())
            return false;

        rootDir = StorageUtil.getPublicRootDir();
        imageDir = StorageUtil.getPublicDir(rootDir, Constants.PUBLIC_DIR_IMAGES);
        ringtoneDir = StorageUtil.getPublicDir(rootDir, Constants.PUBLIC_DIR_RINGTONES);
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

        Log.i(Constants.LOG_TAG, "Received QUERY request with URI: " + uri);
        MatrixCursor cursor = null;

        switch (uriMatcher.match(uri)) {

            // ------------------------------------------------------------------------------------
            // ------------------------------------------------------------------------------------

            case CODE_IMAGE: { // case CODE_IMAGE_WITH_ID: {
                String _ID = selectionArgs[0];
                String filepath = (imageDir.getAbsolutePath() + "/" + _ID);
                Log.i(Constants.LOG_TAG, "Attempting to retrieve image file by ID: " + _ID
                        + " \t\t using file path: " + filepath);

                Bitmap media = MediaUtil.loadBitmap(filepath);
                if (media == null) {
                    Log.e(Constants.LOG_TAG, "UNABLE TO LOAD IMAGE FROM PATH: \t\t"
                            + filepath);
                    break;
                }

                cursor = new MatrixCursor(AlarmMediaContract.ImageEntry.ALL_COLUMNS);
                MatrixCursor.RowBuilder builder = cursor.newRow();

                builder.add(AlarmMediaContract.ImageEntry.COLUMN_ID, _ID);
                builder.add(AlarmMediaContract.ImageEntry.COLUMN_MEDIA_TYPE,
                        AlarmMediaContract.ImageEntry.TABLE_NAME);
                builder.add(AlarmMediaContract.ImageEntry.COLUMN_MEDIA,
                        MediaUtil.convertToByteArray(media));
                break;
            }

            // ------------------------------------------------------------------------------------
            // ------------------------------------------------------------------------------------

            case CODE_RINGTONE: { // case CODE_RINGTONE_WITH_ID: {
                String _ID = uri.getLastPathSegment();
                String filepath = (ringtoneDir.getAbsolutePath() + "/" + _ID);
                Log.i(Constants.LOG_TAG, "Attempting to retrieve ringtone file by ID: " + _ID
                        + " \t\t using file path: " + filepath);

                cursor = new MatrixCursor(AlarmMediaContract.RingtoneEntry.ALL_COLUMNS);
                MatrixCursor.RowBuilder builder = cursor.newRow();

                builder.add(AlarmMediaContract.RingtoneEntry.COLUMN_ID, _ID);
                builder.add(AlarmMediaContract.RingtoneEntry.COLUMN_MEDIA_TYPE,
                        AlarmMediaContract.RingtoneEntry.TABLE_NAME);
                builder.add(AlarmMediaContract.RingtoneEntry.COLUMN_MEDIA,
                        MediaUtil.loadRingtone(filepath));
                break;
            }

            // ------------------------------------------------------------------------------------
            // ------------------------------------------------------------------------------------

            default:
                Log.e(Constants.LOG_TAG, "Unexpected URI: " + uri);
                return null;
        }

        if (cursor != null)
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Log.e(Constants.LOG_TAG, "Received INSERT query with URI: " + uri);
        switch (uriMatcher.match(uri)) {
            case CODE_IMAGE:
                try {
                    String id = values.getAsString(AlarmMediaContract.ImageEntry.COLUMN_ID);
                    byte[] rawMedia = values.getAsByteArray(AlarmMediaContract.ImageEntry.COLUMN_MEDIA);
                    Log.i(Constants.LOG_TAG, "Attempting to write image to file at location: "
                            + imageDir.getAbsolutePath() + "/" + id);

                    Bitmap media = MediaUtil.convertToBitmap(rawMedia);
                    StorageUtil.writeToFile(imageDir, id, media);
                    // media.recycle();

                    getContext().getContentResolver().notifyChange(uri, null);
                    return AlarmMediaContract.ImageEntry.CONTENT_URI;
                } catch (Exception ex) {
                    Log.e(Constants.LOG_TAG, "Something went wrong while trying to "
                            + "insert new alarm image.", ex);
                    return null;
                }
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
        Log.i(Constants.LOG_TAG, "Received DELETE request with URI: " + uri);
        switch (uriMatcher.match(uri)) {
            case CODE_IMAGE:
                Log.e(Constants.LOG_TAG, "Programmatically deleting images is "
                        + "not supported at this time.  Please utilize the Android File "
                        + "Transfer program to remove images manually from your device.");
                return -1;
            case CODE_RINGTONE:
                Log.e(Constants.LOG_TAG, "Programmatically deleting ringtones is "
                        + "not supported at this time.  Please utilize the Android File "
                        + "Transfer program to remove ringtones manually from your device.");
                return -1;
            default:
                Log.e(Constants.LOG_TAG, "Unexpected URI: " + uri);
                return -1;
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.i(Constants.LOG_TAG, "Received UPDATE request with URI: " + uri);
        switch (uriMatcher.match(uri)) {
            case CODE_IMAGE:
                int updatedFileCount = 0;
                try {
                    String id = values.getAsString(AlarmMediaContract.ImageEntry.COLUMN_ID);
                    byte[] rawMedia = values.getAsByteArray(AlarmMediaContract.ImageEntry.COLUMN_MEDIA);
                    Bitmap media = MediaUtil.convertToBitmap(rawMedia);

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
                        + "Transfer program to update ringtones manually on your device.");
                return -1;
            default:
                Log.e(Constants.LOG_TAG, "Unexpected URI: " + uri);
                return -1;
        }
    }

}