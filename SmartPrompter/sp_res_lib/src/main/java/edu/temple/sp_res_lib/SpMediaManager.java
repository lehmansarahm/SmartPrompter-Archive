package edu.temple.sp_res_lib;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;

import java.util.List;

import edu.temple.sp_res_lib.media.AlarmMediaContract;

public class SpMediaManager {

    private Context context;

    public SpMediaManager(Context context) {
        this.context = context;
    }

    public Bitmap getImage(String ID) {
        String whereClause = (AlarmMediaContract.COLUMN_ID + "=?");
        String[] args = new String[] { ID };

        Cursor cursor = context.getContentResolver().query(
                AlarmMediaContract.getContentUri(AlarmMediaContract.MEDIA_TYPE.Image),
                AlarmMediaContract.ALL_COLUMNS,
                whereClause, args, null);

        List<Bitmap> images = AlarmMediaContract.populateImages(cursor);
        if (images == null || images.size() == 0) return null;
        else return images.get(0);
    }

}