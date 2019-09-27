package edu.temple.sp_res_lib.utils;

import android.app.Activity;
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
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static edu.temple.sp_res_lib.utils.Constants.LOG_TAG;

public class CameraUtil {

    public interface ImageCaptureListener {
        void onImageCaptured(String alarmGUID, byte[] imageBytes);
    }

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private static String cameraId;
    private static CameraDevice cameraDevice;

    private static CameraCaptureSession cameraCaptureSession;
    private static CaptureRequest.Builder captureRequestBuilder;
    private static Handler mBackgroundHandler;

    private static Size textureDimensions, jpegDimensions, previewDimensions;
    private static ImageReader imageReader;

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private static CameraDevice.StateCallback stateCallback;
    private static final CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            Log.i(LOG_TAG, "Capture session callback listener engaged!  "
                    + "Returning control to parent activity.");
            super.onCaptureCompleted(session, request, result);
            // createCameraPreview();
        }
    };

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public static void open(final Context context, final TextureView previewTexture) {
        Log.i(LOG_TAG, "Attempting to open camera!");
        CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        assert manager != null;

        try {
            cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics cc = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;

            textureDimensions = map.getOutputSizes(SurfaceTexture.class)[0];
            jpegDimensions = map.getOutputSizes(ImageFormat.JPEG)[0];
            Log.i(LOG_TAG, "Are  default texture, JPEG dimensions equal?  \t\t " +
                    (textureDimensions.getWidth() == jpegDimensions.getWidth() &&
                        textureDimensions.getHeight() == jpegDimensions.getHeight()));

            boolean textureDimMatch = (textureDimensions.getWidth() == previewTexture.getWidth() &&
                    textureDimensions.getHeight() == previewTexture.getHeight());
            Log.i(LOG_TAG, "Are default texture, preview texture dimensions "
                    + "equal? \t\t " + textureDimMatch);

            if (!textureDimMatch) {
                textureDimensions = new Size(previewTexture.getWidth(), previewTexture.getHeight());
                jpegDimensions = new Size(previewTexture.getWidth(), previewTexture.getHeight());
            }

            stateCallback = new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice camera) {
                    Log.e(LOG_TAG, "onOpened");
                    cameraDevice = camera;
                    createPreview(context, previewTexture);
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

            manager.openCamera(cameraId, stateCallback, null);
            Log.i(LOG_TAG, "Camera is open!");
        } catch (CameraAccessException | SecurityException e) {
            Log.e(LOG_TAG, "Something went wrong while trying to open camera with ID: "
                    + cameraId, e);
        }
    }

    public static void close() {
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }

        if (imageReader != null) {
            imageReader.close();
            imageReader = null;
        }
    }

    public static void takePicture(Activity activity, TextureView previewTexture,
                                   final ImageCaptureListener icListener, final String alarmID) {
        Log.i(LOG_TAG, "User has taken a picture!  Ready to capture picture "
                + "from camera preview!");

        if (cameraDevice == null) {
            Log.e(LOG_TAG, "cameraDevice is null");
            return;
        }

        ImageReader reader = ImageReader.newInstance(jpegDimensions.getWidth(),
                jpegDimensions.getHeight(), ImageFormat.JPEG, 1);
        List<Surface> outputSurfaces = new ArrayList<>(2);
        outputSurfaces.add(reader.getSurface());
        outputSurfaces.add(new Surface(previewTexture.getSurfaceTexture()));

        Log.e(LOG_TAG, "Attempting to create capture session with ImageReader width: "
                + reader.getWidth() + " \t and height: " + reader.getHeight() +
                " \n \t ... Compared against CameraPreviewTexture width: "
                + previewTexture.getWidth() + " \t and height: " + previewTexture.getHeight());

        setCaptureBuilder(false, reader.getSurface());
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

        ImageReader.OnImageAvailableListener readerListener =
                new ImageReader.OnImageAvailableListener() {
                    @Override
                    public void onImageAvailable(ImageReader reader) {
                        Log.i(LOG_TAG, "A new camera image is available!");
                        Image image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        icListener.onImageCaptured(alarmID, bytes);
                    }
                };
        reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);
        initCaptureSession(activity, outputSurfaces, false);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private static void createPreview(final Context context, TextureView previewTexture) {
        SurfaceTexture texture = previewTexture.getSurfaceTexture();
        assert texture != null;
        texture.setDefaultBufferSize(textureDimensions.getWidth(), textureDimensions.getHeight());
        Surface surface = new Surface(texture);

        Log.e(LOG_TAG, "Attempting to create camera capture session with width: "
                + textureDimensions.getWidth() + " \t and height: " + textureDimensions.getHeight());
        Log.e(LOG_TAG, "... Compared against CameraPreviewTexture width: "
                + previewTexture.getWidth() + " \t and height: " + previewTexture.getHeight());

        setCaptureBuilder(true, surface);
        initCaptureSession(context, Collections.singletonList(surface), true);
    }

    private static void initCaptureSession(final Context context, List<Surface> outputSurfaces, final boolean isPreview) {
        try {
            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        Log.i(LOG_TAG, "Camera capture session configured!");
                        if (cameraDevice == null) {
                            // The camera is already closed
                            return;
                        }

                        if (isPreview) {
                            // When the session is ready, we start displaying the preview.
                            cameraCaptureSession = session;
                            updatePreview();
                        } else {
                            try {
                                Log.i(LOG_TAG, "Attempting to create a new capture session image.");
                                session.capture(captureRequestBuilder.build(), captureCallback, mBackgroundHandler);
                            } catch (CameraAccessException e) {
                                Log.e(LOG_TAG, "Something went wrong while trying to "
                                        + "capture the camera session contents.", e);
                            }
                        }
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                        Log.e(LOG_TAG, "Failed to create a new camera capture session!");
                        Toast.makeText(context, "Configuration change", Toast.LENGTH_SHORT).show();
                    }

                }, mBackgroundHandler);
        } catch (CameraAccessException ex) {
            Log.e(LOG_TAG, "Something went wrong while trying to initialize a "
                    + "new camera session.", ex);
        }
    }

    private static void updatePreview() {
        if (cameraDevice == null) {
            Log.e(LOG_TAG, "updatePreview error, return");
            return;
        }

        try {
            Log.i(LOG_TAG, "Attempting to set repeating request for camera preview.");
            captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(),
                    null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            Log.e(LOG_TAG, "Something went wrong while attempting to update the camera preview!", e);
        }
    }

    private static void setCaptureBuilder(boolean isPreview, Surface target) {
        try {
            if (isPreview)
                captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            else {
                captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            }
            captureRequestBuilder.addTarget(target);
        } catch (CameraAccessException ex) {
            Log.e(LOG_TAG, "Something went wrong while trying to initialize the "
                    + "capture request builder!", ex);
        }
    }

}
