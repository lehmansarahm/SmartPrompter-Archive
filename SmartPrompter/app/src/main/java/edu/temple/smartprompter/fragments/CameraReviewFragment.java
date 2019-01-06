package edu.temple.smartprompter.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import edu.temple.smartprompter.R;
import edu.temple.smartprompter.utils.Constants;
import edu.temple.sp_res_lib.utils.MediaUtil;

public class CameraReviewFragment extends Fragment {

    public interface ImageReviewListener {
        void onImageAccepted(int alarmID, byte[] bytes);
        void onImageRejected(int alarmID);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private ImageReviewListener mListener;
    private int mAlarmID;
    private byte[] mImageBytes;

    public CameraReviewFragment() {
        // required empty constructor
    }

    public static CameraReviewFragment newInstance(int alarmID, byte[] bytes) {
        CameraReviewFragment fragment = new CameraReviewFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.BUNDLE_ARG_ALARM_ID, alarmID);
        args.putByteArray(Constants.BUNDLE_ARG_IMAGE_BYTES, bytes);
        fragment.setArguments(args);
        return fragment;
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (ImageReviewListener) context;
        } catch (ClassCastException e) {
            String error = context.toString() + " must implement ImageReviewListener";
            Log.e(edu.temple.sp_res_lib.utils.Constants.LOG_TAG, error, e);
            throw new ClassCastException();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAlarmID = getArguments().getInt(Constants.BUNDLE_ARG_ALARM_ID);
            mImageBytes = getArguments().getByteArray(Constants.BUNDLE_ARG_IMAGE_BYTES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_camera_review,
                container, false);

        initReviewImage(rootView);

        Button acceptButton = rootView.findViewById(R.id.accept_button);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onImageAccepted(mAlarmID, mImageBytes);
            }
        });

        Button rejectButton = rootView.findViewById(R.id.reject_button);
        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onImageRejected(mAlarmID);
            }
        });

        return rootView;
    }

    private void initReviewImage(final View rootView) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        int height = 480, width = 640;

        Bitmap bmpOrig = MediaUtil.convertToBitmap(mImageBytes);
        Bitmap bmpScaled = Bitmap.createScaledBitmap(bmpOrig, width, height, true);
        Bitmap bmpRotated = Bitmap.createBitmap(bmpScaled, 0, 0,
                bmpScaled.getWidth(), bmpScaled.getHeight(), matrix, true);

        ImageView reviewImageView = rootView.findViewById(R.id.camera_review_image);
        reviewImageView.setImageBitmap(bmpRotated);
    }

}