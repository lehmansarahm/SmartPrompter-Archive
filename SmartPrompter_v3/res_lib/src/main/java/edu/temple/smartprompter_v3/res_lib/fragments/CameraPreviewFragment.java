package edu.temple.smartprompter_v3.res_lib.fragments;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import edu.temple.smartprompter_v3.res_lib.R;
import edu.temple.smartprompter_v3.res_lib.utils.CameraUtil;
import edu.temple.smartprompter_v3.res_lib.utils.Constants;

import static edu.temple.smartprompter_v3.res_lib.utils.Constants.LOG_TAG;

public class CameraPreviewFragment extends Fragment {

    private CameraUtil.ImageCaptureListener mListener;
    private String mAlarmID;

    private Button takePictureButton;
    private TextureView cameraPreviewTexture;

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            CameraUtil.open(CameraPreviewFragment.this.getActivity(), cameraPreviewTexture);
        }
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // TODO - Transform you image captured size according to the surface width and height
        }
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public CameraPreviewFragment() {
        // required empty constructor
    }

    public static CameraPreviewFragment newInstance(String alarmGUID) {
        CameraPreviewFragment fragment = new CameraPreviewFragment();
        Bundle args = new Bundle();
        args.putString(Constants.BUNDLE_ARG_ALARM_GUID, alarmGUID);
        fragment.setArguments(args);
        return fragment;
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (CameraUtil.ImageCaptureListener) context;
        } catch (ClassCastException e) {
            String error = context.toString() + " must implement ImageCaptureListener";
            Log.e(LOG_TAG, error, e);
            throw new ClassCastException();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        CameraUtil.close();
        mListener = null;
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAlarmID = getArguments().getString(Constants.BUNDLE_ARG_ALARM_GUID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_camera_preview,
                container, false);

        cameraPreviewTexture = (TextureView)rootView.findViewById(R.id.camera_preview_texture);
        assert cameraPreviewTexture != null;
        cameraPreviewTexture.setSurfaceTextureListener(textureListener);

        takePictureButton = (Button)rootView.findViewById(R.id.take_picture_button);
        assert takePictureButton != null;
        takePictureButton.setOnClickListener(v -> {
            Log.i(LOG_TAG, // CameraPreviewFragment.this.getActivity(),
                    "Camera button clicked!");
            CameraUtil.takePicture(CameraPreviewFragment.this.getActivity(),
                    cameraPreviewTexture, mListener, mAlarmID);
        });

        return rootView;
    }

}