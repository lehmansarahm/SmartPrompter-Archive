package edu.temple.smartprompter_v2.fragments;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.temple.smartprompter_v2.R;
import edu.temple.sp_res_lib.utils.CameraUtil;
import edu.temple.sp_res_lib.utils.Constants;
import edu.temple.sp_res_lib.utils.Log;

import static edu.temple.smartprompter_v2.SmartPrompter.LOG_TAG;

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

        cameraPreviewTexture = rootView.findViewById(R.id.camera_preview_texture);
        assert cameraPreviewTexture != null;
        cameraPreviewTexture.setSurfaceTextureListener(textureListener);

        takePictureButton = rootView.findViewById(R.id.take_picture_button);
        assert takePictureButton != null;
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.ui(LOG_TAG, CameraPreviewFragment.this.getActivity(),
                        "Camera button clicked!");
                CameraUtil.takePicture(CameraPreviewFragment.this.getActivity(),
                        cameraPreviewTexture, mListener, mAlarmID);
            }
        });

        return rootView;
    }

}