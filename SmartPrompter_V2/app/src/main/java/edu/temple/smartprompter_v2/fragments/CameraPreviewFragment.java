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
import edu.temple.sp_res_lib.utils.Constants;
import edu.temple.sp_res_lib.utils.Log;

import static edu.temple.smartprompter_v2.SmartPrompter.LOG_TAG;

public class CameraPreviewFragment extends Fragment {

    public interface ImageCaptureListener {
        void onImageCaptured(String alarmGUID, byte[] imageBytes);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private ImageCaptureListener mListener;
    private String mAlarmID;

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

    protected CameraCaptureSession cameraCaptureSession;
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
            Log.e(LOG_TAG, "onOpened");
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
            mListener = (ImageCaptureListener) context;
        } catch (ClassCastException e) {
            String error = context.toString() + " must implement ImageCaptureListener";
            Log.e(LOG_TAG, error, e);
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
            Log.e(LOG_TAG, "cameraDevice is null");
            return;
        }

        Log.e(LOG_TAG, "User has taken a picture!  Ready to capture picture from camera preview!");
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
            Log.e(LOG_TAG, "Attempting to create camera capture session with width: "
                    + reader.getWidth() + " \t and height: " + reader.getHeight());

            SurfaceTexture texture = cameraPreviewTexture.getSurfaceTexture();
            outputSurfaces.add(new Surface(texture));
            Log.e(LOG_TAG, "... Compared against CameraPreviewTexture width: "
                    + cameraPreviewTexture.getWidth() + " \t and height: " + cameraPreviewTexture.getHeight());

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
                            Log.i(LOG_TAG, "A new camera image is available!");
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
                            Log.i(LOG_TAG, "Capture session callback listener "
                                    + "engaged!  Returning control to parent activity.");
                            super.onCaptureCompleted(session, request, result);
                            // createCameraPreview();
                        }

                    };

            cameraDevice.createCaptureSession(outputSurfaces,
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(CameraCaptureSession session) {
                            Log.e(LOG_TAG, "Camera capture session configured!");

                            try {
                                Log.i(LOG_TAG, "Attempting to create a new "
                                        + "capture session.");
                                session.capture(captureBuilder.build(),
                                        captureListener, mBackgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession session) {
                            // TODO - debug session configuration error for Google Pixel 2
                            Log.e(LOG_TAG, "Failed to create a new camera capture session!");
                        }

                    }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            Log.e(LOG_TAG, "Something went wrong while trying to capture "
                    + "an image from the camera preview.", e);
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

            Log.e(LOG_TAG, "Attempting to create camera capture session with width: "
                    + imageDimension.getWidth() + " \t and height: " + imageDimension.getHeight());

            cameraDevice.createCaptureSession(Arrays.asList(surface),
                    new CameraCaptureSession.StateCallback(){

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            Log.e(LOG_TAG, "Camera capture session configured!");
                            if (cameraDevice == null) {
                                // The camera is already closed
                                return;
                            }

                            // When the session is ready, we start displaying the preview.
                            cameraCaptureSession = session;
                            updatePreview();
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            Log.e(LOG_TAG, "Failed to create a new camera capture session!");
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
            Log.e(LOG_TAG, "updatePreview error, return");
            return;
        }

        try {
            Log.i(LOG_TAG, "Attempting to set repeating request for camera preview.");
            captureRequestBuilder.set(CaptureRequest.CONTROL_MODE,
                    CameraMetadata.CONTROL_MODE_AUTO);
            cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(),
                    null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            Log.e(LOG_TAG, "Something went wrong while attempting to update the camera preview!", e);
        }
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private void openCamera() {
        CameraManager manager = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);
        Log.e(LOG_TAG, "is camera open");

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

        Log.e(LOG_TAG, "openCamera X");
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