package edu.temple.smartprompter.fragments;

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
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.temple.smartprompter.R;
import edu.temple.smartprompter.utils.Constants;
import edu.temple.sp_res_lib.Alarm;
import edu.temple.sp_res_lib.SpAlarmManager;

public class CameraPreviewFragment extends Fragment {

    public interface ImageCaptureListener {
        void onImageCaptured(int alarmID, byte[] imageBytes);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private ImageCaptureListener mListener;
    private int mAlarmID;

    private Button takePictureButton;
    private TextureView cameraPreviewTexture;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private String cameraId;
    protected CameraDevice cameraDevice;

    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest.Builder captureRequestBuilder;

    private Size imageDimension;
    private ImageReader imageReader;

    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            openCamera();
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

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            //This is called when the camera is open
            Log.e(Constants.LOG_TAG, "onOpened");
            cameraDevice = camera;
            createCameraPreview();
        }
        @Override
        public void onDisconnected(CameraDevice camera) {
            cameraDevice.close();
        }
        @Override
        public void onError(CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public CameraPreviewFragment() {
        // required empty constructor
    }

    public static CameraPreviewFragment newInstance(int alarmID) {
        CameraPreviewFragment fragment = new CameraPreviewFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.BUNDLE_ARG_ALARM_ID, alarmID);
        fragment.setArguments(args);
        return fragment;
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (ImageCaptureListener) context;
        } catch (ClassCastException e) {
            String error = context.toString() + " must implement ImageCaptureListener";
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
                takePicture();
            }
        });

        return rootView;
    }

    public void pausePreview() {
        closeCamera();
        stopBackgroundThread();
    }

    public void resumePreview() {
        startBackgroundThread();
        if (cameraPreviewTexture.isAvailable()) {
            openCamera();
        } else {
            cameraPreviewTexture.setSurfaceTextureListener(textureListener);
        }
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        if (mBackgroundThread != null) {
            mBackgroundThread.quitSafely();
            try {
                mBackgroundThread.join();
                mBackgroundThread = null;
                mBackgroundHandler = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private void takePicture() {
        if (cameraDevice == null) {
            Log.e(Constants.LOG_TAG, "cameraDevice is null");
            return;
        }

        CameraManager manager =
                (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);

        try {
            CameraCharacteristics characteristics =
                    manager.getCameraCharacteristics(cameraDevice.getId());

            Size[] jpegSizes = null;
            if (characteristics != null) {
                jpegSizes =
                        characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                                .getOutputSizes(ImageFormat.JPEG);
            }

            int width = 640;
            int height = 480;
            if (jpegSizes != null && 0 < jpegSizes.length) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }

            ImageReader reader = ImageReader.newInstance(width, height,
                    ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(cameraPreviewTexture.getSurfaceTexture()));

            final CaptureRequest.Builder captureBuilder =
                    cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            // Orientation
            int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

            ImageReader.OnImageAvailableListener readerListener =
                    new ImageReader.OnImageAvailableListener() {

                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = reader.acquireLatestImage();
                    ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                    byte[] bytes = new byte[buffer.capacity()];
                    buffer.get(bytes);
                    mListener.onImageCaptured(mAlarmID, bytes);
                }

            };

            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);

            final CameraCaptureSession.CaptureCallback captureListener =
                    new CameraCaptureSession.CaptureCallback() {

                @Override
                public void onCaptureCompleted(CameraCaptureSession session,
                                               CaptureRequest request,
                                               TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    // createCameraPreview();
                }

            };

            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(),
                                captureListener, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                    // do nothing
                }

            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private void createCameraPreview() {
        try {
            SurfaceTexture texture = cameraPreviewTexture.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);

            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);

            cameraDevice.createCaptureSession(Arrays.asList(surface),
                    new CameraCaptureSession.StateCallback(){

                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    if (cameraDevice == null) {
                        // The camera is already closed
                        return;
                    }

                    // When the session is ready, we start displaying the preview.
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(CameraPreviewFragment.this.getContext(),
                            "Configuration change", Toast.LENGTH_SHORT).show();
                }

            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void updatePreview() {
        if (cameraDevice == null) {
            Log.e(Constants.LOG_TAG, "updatePreview error, return");
            return;
        }

        try {
            captureRequestBuilder.set(CaptureRequest.CONTROL_MODE,
                    CameraMetadata.CONTROL_MODE_AUTO);
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(),
                    null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private void openCamera() {
        CameraManager manager = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);
        Log.e(Constants.LOG_TAG, "is camera open");

        try {
            cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics =
                    manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map =
                    characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;

            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            manager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        Log.e(Constants.LOG_TAG, "openCamera X");
    }

    private void closeCamera() {
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }

        if (imageReader != null) {
            imageReader.close();
            imageReader = null;
        }
    }

}