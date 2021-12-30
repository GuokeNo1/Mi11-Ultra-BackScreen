package cn.pashiguoke.subscreen;

import static android.hardware.camera2.CameraCaptureSession.*;

import androidx.annotation.NonNull;

import android.annotation.SuppressLint;
import android.graphics.Camera;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

import java.util.Arrays;

public class MirrorActivity extends SubBaseActivity {

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mirror);
        findViewById(R.id.mirror).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return myGLister.onTouchEvent(motionEvent);
            }
        });
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        CameraManager cm = (CameraManager)getSystemService(CAMERA_SERVICE);
        try {
            String[] cmids = cm.getCameraIdList();

            for(String id : cmids){
                CameraCharacteristics cameraCharacteristics = cm.getCameraCharacteristics(id);
                Integer facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
                if(facing!=null && facing==CameraCharacteristics.LENS_FACING_BACK){
                    StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    Size[] sizes = streamConfigurationMap.getOutputSizes(ImageFormat.JPEG);
                    float vP = (float) width/(float) height;
                    float sP = 0;
                    Size ss = null;
                    for(int i=0;i<sizes.length;i++){
                        Size s = sizes[i];
                        float itP = (float)s.getWidth()/(float) s.getHeight();
                        float diffP = Math.abs(vP-itP);
                        if(i==0){
                            sP = diffP;
                            ss = s;
                        }
                        if(diffP<sP){
                            sP = diffP;
                            ss = s;

                        }else if(diffP==sP){
                            if(ss.getWidth()-width>s.getWidth()-width){
                                sP = diffP;
                                ss = s;
                            }
                        }
                    }
                    final Size size = ss;
                    cm.openCamera(id, new CameraDevice.StateCallback() {
                        @Override
                        public void onOpened(@NonNull CameraDevice cameraDevice) {
                            try {
                                TextureView sv = findViewById(R.id.mirror);

                                ViewGroup.LayoutParams layoutParams = sv.getLayoutParams();
                                float scaleN = (float) width / (float)size.getWidth();
                                layoutParams.width = (int) (size.getWidth() * scaleN);
                                layoutParams.height = (int) (size.getHeight() * scaleN);
                                sv.setLayoutParams(layoutParams);

                                Animation animation = new ZF((int) (size.getWidth() * scaleN),(int) (size.getHeight() * scaleN));
                                animation.setFillAfter(true);
                                sv.startAnimation(animation);


                                CaptureRequest.Builder builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                                    SurfaceTexture texture = new SurfaceTexture(false);
                                    assert texture!=null;
                                    texture.setDefaultBufferSize(size.getWidth(),size.getHeight());


                                    sv.setSurfaceTexture(texture);
                                    Surface surface = new Surface(texture);


                                    builder.addTarget(surface);
                                    builder.set(CaptureRequest.CONTROL_AF_MODE,CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

                                    cameraDevice.createCaptureSession(Arrays.asList(surface), new StateCallback() {
                                        @Override
                                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                                            try {
                                                cameraCaptureSession.setRepeatingRequest(builder.build(), new CaptureCallback() {
                                                    @Override
                                                    public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                                                        super.onCaptureCompleted(session, request, result);
                                                    }
                                                }, null);
                                            } catch (CameraAccessException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                                        }
                                    },null);
                                }
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
                            cameraDevice.close();
                        }

                        @Override
                        public void onError(@NonNull CameraDevice cameraDevice, int i) {
                            cameraDevice.close();
                        }
                    },null);
                }
            }

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    class ZF extends Animation{
        private Camera mCamera = new Camera();
        private float cx,cy;
        public ZF(float x,float y){
            cx = x/2.0f;
            cy = y/2.0f;
        }
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            Matrix matrix = t.getMatrix();
            mCamera.save();
            mCamera.rotateY(180);
            mCamera.getMatrix(matrix);
            mCamera.restore();
            matrix.preTranslate(-cx,-cy) ;
            matrix.postTranslate(cx,cy);
        }
    }
}