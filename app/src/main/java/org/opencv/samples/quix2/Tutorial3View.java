package org.opencv.samples.quix2;
import java.util.ArrayList;
import java.util.List;
import org.opencv.android.JavaCameraView;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.camera2.*;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;


public class Tutorial3View extends JavaCameraView {
    public Tutorial3View(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public List<String> getEffectList() {
        return mCamera.getParameters().getSupportedFlashModes();
    }

    public boolean isEffectSupported() {
        return (mCamera.getParameters().getFlashMode() != null);
    }

    public String getEffect() {
        return mCamera.getParameters().getFlashMode();
    }

    public void setEffect(String effect) {
        if (mCamera != null) {
            mCamera.getParameters();
            Camera.Parameters params = mCamera.getParameters();
            CaptureRequest.Builder builder= null;

//            builder.set(CaptureRequest.CONTROL_AWB_MODE,CaptureRequest.CONTROL_AWB_MODE_OFF);

            params.setFlashMode(effect);
            mCamera.setParameters(params);
        }
    }

    public void cameraRelease() {
        if (mCamera != null) {
            mCamera.release();
        }
    }


    protected SeekBar seekBar;

    public void setZoomControl(SeekBar _seekBar) {
        seekBar = _seekBar;
    }

    protected void enableZoomControls(Camera.Parameters params) {

        final int maxZoom = params.getMaxZoom();
        seekBar.setMax(maxZoom);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                               int progressvalue = 0;

                                               @Override
                                               public void onProgressChanged(SeekBar seekBar, int progress,
                                                                             boolean fromUser) {
                                                   // TODO Auto-generated method stub
                                                   progressvalue = progress;
                                                   Camera.Parameters params = mCamera.getParameters();
                                                   params.setZoom(progress);
                                                   mCamera.setParameters(params);


                                               }

                                               @Override
                                               public void onStartTrackingTouch(SeekBar seekBar) {
                                                   // TODO Auto-generated method stub

                                               }

                                               @Override
                                               public void onStopTrackingTouch(SeekBar seekBar) {
                                                   // TODO Auto-generated method stub

                                               }


                                           }

        );
//    public void macroFocus(Rect rect){
//    	params = mCamera.getParameters();
//    	params.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO); //������
//    	ArrayList<Camera.Area> arraylist = new ArrayList<Camera.Area>();
//    	arraylist.add(new Camera.Area(rect, 1000));
//
//    	params.setFocusAreas(arraylist) ;
//    	mCamera.setParameters(params);
//    }
//
    }

    protected boolean initializeCamera(int width, int height)
    {

        boolean ret = super.initializeCamera(width, height);


        Camera.Parameters params = mCamera.getParameters();

        if(params.isZoomSupported())
            enableZoomControls(params);

        mCamera.setParameters(params);

        return ret;
    }


}