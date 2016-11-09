package com.utis.patient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class CameraActivity extends Activity {
	private Camera mCamera;
    private CameraPreview mCameraPreview;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    
    PictureCallback mPicture = new PictureCallback() {
    	  
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
             File pictureFile = getOutputMediaFile(/*MEDIA_TYPE_IMAGE*/);
                if (pictureFile == null) {
                    return;
                }
                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                } catch (FileNotFoundException e) {
  
                } catch (IOException e) {
                  
                }              
        }          
    };    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		
		mCamera = getCameraInstance();
	    mCameraPreview = new CameraPreview(this, mCamera);
	          
	    FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
	    preview.addView(mCameraPreview);
	    
	    Button captureButton = (Button) findViewById(R.id.button_capture);
	    captureButton.setOnClickListener(new View.OnClickListener() {	                     
	    	@Override
            public void onClick(View v) {
	    		mCamera.takePicture(null, null, mPicture);                     
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.camera, menu);
		return true;
	}
	
	/**
     * Helper method to access the camera returns null if
     * it cannot get the camera or does not exist
     * @return
     */
    private Camera getCameraInstance() {
        Camera camera = null;
  
        try {
            camera = Camera.open();
        } catch (Exception e) {
            // cannot get camera or does not exist
        }
        return camera;
    }

    /** Создаем файл для сохранения изображения */
    private static File getOutputMediaFile() {
  
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                  Environment.DIRECTORY_PICTURES), "MyCameraApp");
  
        if (! mediaStorageDir.exists()) {
            if (! mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
  
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "IMG_"+ timeStamp + ".jpg");
  
        return mediaFile;
    }    

}
