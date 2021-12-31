package cn.pashiguoke.subscreen;

import static android.hardware.camera2.CameraCaptureSession.*;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

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
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

import java.util.Arrays;

public class MirrorActivity extends SubBaseActivity{

    private CameraDevice device;
    private int width,height;
    private Size cameraSize;
    private String cameraId;
    private CameraManager cm;
    private TextureView mirrorView;
    private Surface surface;

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mirror);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED|WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        // 左滑返回
        mirrorView = findViewById(R.id.mirror);
        mirrorView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return myGLister.onTouchEvent(motionEvent);
            }
        });

        // 获取屏幕宽高
        width = getResources().getDisplayMetrics().widthPixels;
        height = getResources().getDisplayMetrics().heightPixels;


        cm = (CameraManager)getSystemService(CAMERA_SERVICE);
        try {
            String[] cmids = cm.getCameraIdList();

            for(String id : cmids){
                cameraId = id;
                CameraCharacteristics cameraCharacteristics = cm.getCameraCharacteristics(id);
                Integer facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
                // 获取后置摄像头
                if(facing!=null && facing==CameraCharacteristics.LENS_FACING_BACK){
                    StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    Size[] sizes = streamConfigurationMap.getOutputSizes(ImageFormat.JPEG);
                    // 计算最佳宽高比
                    float screen_proportion = (float) width/(float) height;
                    float select_view_proportion = 0;
                    Size select_size = null;
                    for(int i=0;i<sizes.length;i++){
                        Size s = sizes[i];
                        float itP = (float)s.getHeight()/(float) s.getWidth();
                        float diffP = Math.abs(screen_proportion-itP);
                        if(i==0){
                            select_view_proportion = diffP;
                            select_size = s;
                        }
                        if(diffP<select_view_proportion){
                            select_view_proportion = diffP;
                            select_size = s;

                        }else if(diffP==select_view_proportion){
                            if(select_size.getHeight()-width>s.getHeight()-width){
                                select_view_proportion = diffP;
                                select_size = s;
                            }
                        }
                    }
                    cameraSize = select_size;
                    // 重置MirrorView的宽高
                    ViewGroup.LayoutParams layoutParams = mirrorView.getLayoutParams();

                    float scaleN = (float) width / (float)select_size.getHeight();
                    Size FinalSize = new Size((int) (select_size.getHeight() * scaleN),(int) (select_size.getWidth() * scaleN));
                    layoutParams.width = FinalSize.getWidth();
                    layoutParams.height = FinalSize.getHeight();
                    mirrorView.setLayoutParams(layoutParams);


                    // 反转MirrorView
                    Animation animation = new ZF(FinalSize.getWidth(),FinalSize.getHeight());
                    animation.setFillAfter(true);
                    mirrorView.startAnimation(animation);

                    // 设定Texture
                    SurfaceTexture texture = new SurfaceTexture(false);
                    assert texture!=null;
                    texture.setDefaultBufferSize(cameraSize.getWidth(),cameraSize.getHeight());
                    surface = new Surface(texture);
                    mirrorView.setSurfaceTexture(texture);

                    cm.openCamera(cameraId, new CameraDevice.StateCallback() {
                        @Override
                        public void onOpened(@NonNull CameraDevice cameraDevice) {
                            try {
                                device = cameraDevice;
                                CaptureRequest.Builder builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {


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
                    break;

                }
            }

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void finish() {
        super.finish();
        if(device!=null)
            device.close();

    }

    // 反转显示用的
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