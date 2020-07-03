package org.opencv.samples.quix2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TimeUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class Tutorial2Activity extends Activity implements
		CvCameraViewListener2,View.OnTouchListener {

	Zoomcameraview zoomcameraview;

	Thread threadDelay;
	TimerHandler timerHandler = new TimerHandler();


	private int RGB_data_array[]=new int[3];
	private int RGB_data_array_2[]=new int[3];
	private int RGB_avg_per_time[]=new int[10];
	private int frame_num=0;

	public boolean should_motor_on =false;
	public boolean FLAG_ENTER=false;
	public boolean FLAG_FILLED=true;
	public boolean FLAG_LEAVE=false		;
	public boolean FLAG_EMPTY=true;
	public boolean FLAG_INSUFF=false;
	public boolean FLAG_BUBBLE=false;



	public boolean is_in=true;
	public boolean is_empty=false;


	public int updated_count=0;
	public int FLAG_INI_VALUE=0;
	public double scaleFactor;
	public int minNeighbors,minNeighborsfill,minNeighborsEmpty;
	public boolean ini_F = false;
	public int ini_R=0, ini_G=0, ini_B=0, ini_GC=0;

	private static final String TAG = "Tutorial2Activity.java";

	private static final int VIEW_MODE_RGBA = 0;
	private static final int VIEW_MODE_START = 1;
	private static final int VIEW_MODE_RESULT = 2;
	private static final int VIEW_MODE_STOP = 3;
	private static final int VIEW_MODE_INIT = 4;
	private static final int VIEW_MODE_CHECK =5;
	private static final int VIEW_MODE_THRESH =6;




	private static final int MESSAGE_TIMER_START=100;
	private static final int MESSAGE_TIMER_REPEAT=101;
	private static final int MESSAGE_TIMER_STOP=102;
	int timer_counter;


	private static final int COLOR_RED = 0;
	private static final int COLOR_GREEN=1;
	private  static  final  int COLOR_YELLOW=2;


	Rect[] DetectCircle_array =new Rect[50];
	Rect[] DetectEnter_array =new Rect[20];
	Rect[] DetectFilled_array =new Rect[20];
	Rect[] DetectLeave_array =new Rect[20];
	Rect[] DetectEmpty_array =new Rect[20];
	Rect[] DetectInsuff_array =new Rect[20];
	Rect[] DetectBubble_array =new Rect[20];
	public double tl_x=0.0;
	public double tl_y=0.0;

	public double br_x=0.0;
	public double br_y=0.0;

	public String detectMessage="";
	public String BTMessage="";

	public int seekBarProgress;
	public int thresholdProgress;
	public int start_x=1340;
	public int start_y=720;
	public int x_width=25;
	public int y_height=30;


	public int start_x_2=1360;
	public int start_y_2=520;
	public int cropped_x =820;//1070
	public int cropped_y = 250;

	public int cropped_w = 600;
	public int cropped_h =600;


	public int detectzone_x =0;//1070
	public int detectzone_y = 0;//710

	public int detectzone_w = 0;//160
	public int detectzone_h =0;//100

	public int thickness =0;


	public int x_width_2=15;
	public int y_height_2=15;

	private int mViewMode;
	private Mat mRgba,mask;
	private Mat mIntermediateMat;
	private Mat mGray, cropped_img,detection_zone;
	private File mCascadeFile, mModelFile2, mModelFile3, mModelFile4,mModelFile5,mModelFile6,mModelFile7;
	private CascadeClassifier	mJavaDetector,mJavaDetector2,mJavaDetector3,mJavaDetector4,mJavaDetector5,mJavaDetector6,mJavaDetector7;

	private MenuItem mItemPreviewRGBA;
	private MenuItem mItemPreviewGray;
	private MenuItem mItemPreviewCanny;
	private MenuItem mItemPreviewFeatures;

	private TextView mTitle;
	private TextView mText;
	private Button mFlashButton;

	private Tutorial3View mOpenCvCameraView;

	private String text;
	private String sendMsg="";

	private FileOutputStream fos = null;
	String path;
	// Debugging
	private static final boolean D = true;

	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;
	public int mN1,mN2,mN3,mN4,mN5,mN6,mN7;
	public double sF =1.1;



	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;
	// Name of the connected device
	private String mConnectedDeviceName = null;
	// Array adapter for the conversation thread
	private ArrayAdapter<String> mConversationArrayAdapter;
	// String buffer for outgoing messages
	private StringBuffer mOutStringBuffer;
	// Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter = null;

	private BluetoothChatService mChatService = null;
	Point p = new Point(0,0);
	Point p_to = new Point(0,0);


	Button InitBtn;
	Button StartBtn;
	Button StopBtn;
	Button ReturnBtn,MeasureBtn;
	Button thPlsBtn1,thPlsBtn2,thPlsBtn3,thPlsBtn4,thPlsBtn5,thPlsBtn6,thPlsBtn7;
	Button thmnsBtn1,thmnsBtn2,thmnsBtn3,thmnsBtn4,thmnsBtn5,thmnsBtn6,thmnsBtn7;

    int count=0;
    int enter_count=0,filled_count=0,leave_count=0,empty_count=0,insuff_count=0,bubble_count=0;
    long fpsStratTime=0L;
    int frameCnt=0;
    double timeElapsed=0.0f;

//	TimerHandler timerHandler = new TimerHandler();

	CheckBox flashCheckbox;
	SeekBar seekBar,thresholdBar;
	TextView textView_for_th;

	File file;
	String Text_for_R="R: ";
	String Text_for_G="G: ";
	String Text_for_B="B: ";
	Scalar colorSelect = new Scalar(0, 0, 0);

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
				case LoaderCallbackInterface.SUCCESS: {
					Log.i(TAG, "OpenCV loaded successfully");

					// Load native library after(!) OpenCV initialization
					System.loadLibrary("native-lib");
//					System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

					try {
						// load cascade file from application resources
						InputStream is = getResources().openRawResource(R.raw.circle); //cascade_filled_6f
						InputStream is_enter=getResources().openRawResource(R.raw.enter20191226); //maniscus 13
                        InputStream is_filled=getResources().openRawResource(R.raw.filled20200110);
                        InputStream is_leave=getResources().openRawResource(R.raw.leave20191226); //cascade_empty_6
                        InputStream is_empty=getResources().openRawResource(R.raw.empty20191226); //cempty_4 ok
						InputStream is_insuff=getResources().openRawResource(R.raw.insuff20191226); //cascade_moved
						InputStream is_bubble=getResources().openRawResource(R.raw.bubble20200108); //cascade_moved


						scaleFactor=1.11;


						File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
						File manisDir=getDir("cascade",Context.MODE_PRIVATE);
						File model3Dir=getDir("cascade",Context.MODE_PRIVATE);
						File model4Dir=getDir("cascade",Context.MODE_PRIVATE);
						File model5Dir=getDir("cascade",Context.MODE_PRIVATE);
						File modelempDir=getDir("cascade",Context.MODE_PRIVATE);
						File model6Dir=getDir("cascade",Context.MODE_PRIVATE);


						Log.i(TAG,"directory_cascade :"+cascadeDir.getAbsolutePath());
						Log.i(TAG,"directory_maniscus :"+cascadeDir.getAbsolutePath());

						mCascadeFile = new File(cascadeDir, "enter2233.xml");
						mModelFile2 = new File(manisDir,"cascade_out4.xml");
						mModelFile3= new File(model3Dir,"cascade_empty_6.xml");
						mModelFile4= new File(model4Dir,"cascade_empty_7.xml");
						mModelFile5= new File(model5Dir,"cascade_empty_8.xml");
						mModelFile6= new File(modelempDir,"cascade_empty_9.xml");
						mModelFile7= new File(modelempDir,"cascade_empty_10.xml");


						FileOutputStream os = new FileOutputStream(mCascadeFile);
						FileOutputStream os_mains = new FileOutputStream(mModelFile2);
						FileOutputStream os_3=new FileOutputStream(mModelFile3);
						FileOutputStream os_4=new FileOutputStream(mModelFile4);
						FileOutputStream os_5=new FileOutputStream(mModelFile5);
						FileOutputStream os_6=new FileOutputStream(mModelFile6);
						FileOutputStream os_7=new FileOutputStream(mModelFile7);


						byte[] buffer = new byte[4096];
						byte[] buffer2 = new byte[4096];
						byte[] buf3 = new byte[4096];
						byte[] buf4 = new byte[4096];
						byte[] buf5 = new byte[4096];
						byte[] buf6 = new byte[4096];
						byte[] buf7 = new byte[4096];

						int bytesRead,bytesRead2,bytesRead3,bytesRead4,bytesRead5,bytesRead6,bytesRead7;
						while ((bytesRead = is.read(buffer)) != -1) {
							os.write(buffer, 0, bytesRead);
						}
						while ((bytesRead2 = is_enter.read(buffer2)) != -1) {
							os_mains.write(buffer2, 0, bytesRead2);
						}
						while((bytesRead3 = is_filled.read(buf3))!=-1){
							os_3.write(buf3,0,bytesRead3);
						}
						while((bytesRead4 = is_leave.read(buf4))!=-1){
							os_4.write(buf4,0,bytesRead4);
						}
						while((bytesRead5 = is_empty.read(buf5))!=-1){
							os_5.write(buf5,0,bytesRead5);
						}
						while((bytesRead6 = is_insuff.read(buf6))!=-1){
							os_6.write(buf6,0,bytesRead6);
						}
						while((bytesRead7 = is_bubble.read(buf7))!=-1){
							os_7.write(buf7,0,bytesRead7);
						}
						is.close();
						is_enter.close();
						is_empty.close();
						is_leave.close();
						is_insuff.close();
						is_bubble.close();
						is_filled.close();
						os.close();
						os_mains.close();
						os_3.close();
						os_4.close();
						os_5.close();
						os_6.close();
						os_7.close();
						mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
						mJavaDetector2 =new CascadeClassifier(mModelFile2.getAbsolutePath());
						mJavaDetector3= new CascadeClassifier(mModelFile3.getAbsolutePath());
						mJavaDetector4= new CascadeClassifier(mModelFile4.getAbsolutePath());
						mJavaDetector5= new CascadeClassifier(mModelFile5.getAbsolutePath());
						mJavaDetector6= new CascadeClassifier(mModelFile6.getAbsolutePath());
						mJavaDetector7= new CascadeClassifier(mModelFile7.getAbsolutePath());


						if (mJavaDetector.empty() || mJavaDetector2.empty()) {
							Log.e(TAG, "Failed to load cascade classifier");
							mJavaDetector = null;
						} else
							Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

//						mNativeDetector = new DetectionBasedTracker(mCascadeFile.getAbsolutePath(), 0);

						cascadeDir.delete();

					} catch (IOException e) {
						e.printStackTrace();
						Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
					}

					mRgba=new Mat();
					mOpenCvCameraView.enableView();
				}
				break;
				default: {
					super.onManagerConnected(status);
				}
				break;
			}
		}
	};

	public Tutorial2Activity() {
		Log.i(TAG, "Instantiated new " + this.getClass());
	}

	Rect sel=new Rect();


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.tutorial2_surface_view);

		fpsStratTime=System.currentTimeMillis();

		InitBtn = (Button) findViewById(R.id.button1);
		StartBtn = (Button) findViewById(R.id.button2);
		StopBtn = (Button) findViewById(R.id.button3);
		ReturnBtn = (Button) findViewById(R.id.button6);
		MeasureBtn=(Button)findViewById(R.id.button7);
		seekBar=(SeekBar)findViewById(R.id.CameraZoomControls);


		seekBarProgress=seekBar.getProgress();
		mOpenCvCameraView = (Tutorial3View) findViewById(R.id.tutorial2_activity_surface_view);
		mOpenCvCameraView.setCvCameraViewListener(this);
		mOpenCvCameraView.setEnabled(true);
		mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
		mOpenCvCameraView.setZoomControl((SeekBar) findViewById(R.id.CameraZoomControls));

		mTitle = (TextView) findViewById(R.id.textView1);
		mText = (TextView) findViewById(R.id.textView2);
		textView_for_th = findViewById(R.id.textView4);

		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


		if (!hasPermissions(PERMISSIONS)) {

			//퍼미션 허가 안되어있다면 사용자에게 요청
			requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
		}




		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			// Otherwise, setup the chat session
		} else {
			if (mChatService == null)
				setupChat();
		}
		flashCheckbox =(CheckBox)findViewById(R.id.checkBox1);
		flashCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					mOpenCvCameraView.setEffect(Camera.Parameters.FLASH_MODE_TORCH);
//					mOpenCvCameraView.setAWB(true);
//					mViewMode=VIEW_MODE_THRESH;
					flashCheckbox.setVisibility(View.INVISIBLE);
				}else{
					mOpenCvCameraView.setEffect(Camera.Parameters.FLASH_MODE_ON);
//					mOpenCvCameraView.setAWB(false);
//					mViewMode=VIEW_MODE_START;
				}
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		return true;
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mOpenCvCameraView != null) {
			mOpenCvCameraView.disableView();

		}

	}

	@Override
	public void onResume() {
		super.onResume();
//      OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this,
//            mLoaderCallback);
		if (!OpenCVLoader.initDebug()) {
			Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
			OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
		} else {
			Log.d(TAG, "OpenCV library found inside package. Using it!");
			mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
		}

	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
		if (mChatService != null)
			mChatService.stop();
	}
	@Override
	public void onCameraViewStarted(int width, int height) {
		mRgba = new Mat(height, width, CvType.CV_8UC4);
		mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
		mGray = new Mat(height, width, CvType.CV_8UC1);
		seekBar.setProgress(35);

	}


	@Override
	public void onCameraViewStopped() {


		mRgba.release();
		mGray.release();
		mIntermediateMat.release();


	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int cols = mRgba.cols();
		int rows = mRgba.rows();

		int xOffset = (mOpenCvCameraView.getWidth() - cols) / 2;
		int yOffset = (mOpenCvCameraView.getHeight() - rows) / 2;

		int x = (int)event.getX() - xOffset;
		int y = (int)event.getY() - yOffset;
		if (x<0||y<0||x>=cols||y>=cols) return false;
		if ((sel.x==0 && sel.y==0) || (sel.width!=0 && sel.height!=0))
		{
			mask = null;
			sel.x=x; sel.y=y;
			sel.width = sel.height = 0;
		} else {
			sel.width = x - sel.x;
			sel.height = y - sel.y;
			if ( sel.width <= 0 || sel.height <= 0 ) { // invalid, clear it all
				sel.x=sel.y=sel.width=sel.height = 0;
				mask = null;
				return false;
			}
			mask = Mat.zeros(mRgba.size(), mRgba.type());
			mask.submat(sel).setTo(Scalar.all(255));
		}
		Log.w("touch",sel.toString());
		return false;
	}






    //	CascadeClassifier cascadeClassifier;
	long curTime=System.currentTimeMillis();
	long prevTime=0;
	long secTime=0;
	float fps;
	public Mat onCameraFrame(final CvCameraViewFrame inputFrame) {
		frame_num++;
		curTime= System.currentTimeMillis();
		secTime=(int)curTime-(int)prevTime;
		prevTime=curTime;

		if(frame_num%10==0)
			fps=1000/secTime;



		InitBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {


				mGray = inputFrame.gray();
//				Point t1= new Point(700,200);
//				Point t2= new Point(1700,800);
//				cropped_img =new Mat(mGray,new Rect(t1,t2));
				SystemClock.sleep(1000);
				// TODO Auto-generated method stub
				Intent serverIntent = new Intent(getApplicationContext(), DeviceListActivity.class);
				startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
				if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
					// Start the Bluetooth chat services
					mChatService.start();
				}
				mViewMode = VIEW_MODE_INIT;

				if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
					String con = new String("STOP" + "\n");
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}if(!sendMsg.equals(con)){
						sendMessage(con);
						sendMsg = con;
					}
				}
				mText.setTextSize(20);



			}
		});


//		thresholdBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//													int progressvalue=20;
//													@Override
//													public void onProgressChanged(SeekBar seekBar, int progress,
//																				  boolean fromUser) {
//														// TODO Auto-generated method stub
//														thresholdProgress=thresholdBar.getProgress();
//													}
//													@Override
//													public void onStartTrackingTouch(SeekBar seekBar) {
//														// TODO Auto-generated method stub
//
//
//													}
//													@Override
//													public void onStopTrackingTouch(SeekBar seekBar) {
//														// TODO Auto-generated method stub
//													}
//												}
//		);
		StartBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				movingGo(10);
                minNeighbors=50;
				mViewMode = VIEW_MODE_START;




				timerHandler.sendEmptyMessage(MESSAGE_TIMER_START);

				Toast.makeText(getApplicationContext(),"Moving Forward",Toast.LENGTH_SHORT).show();
			}
		});
		StopBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mViewMode = VIEW_MODE_STOP;


				mText.setTextSize(20);

//
			}
		});
		MeasureBtn.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				FLAG_BUBBLE=false;
				FLAG_EMPTY=true;
				FLAG_ENTER=false;
				FLAG_FILLED=false;
				FLAG_INSUFF=false;
				FLAG_LEAVE=false;
				minNeighbors=80;
				Toast.makeText(getApplicationContext(),"Moving Backward",Toast.LENGTH_LONG).show();
			}
		});
		ReturnBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				FLAG_INI_VALUE=0;
				ini_F = false;
				movingBack(10);

				// TODO Auto-generated method stub
				if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
					String con = new String("RETURN" + "\n");

					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (!sendMsg.equals(con)) {
						try {
							Thread.sleep(30);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						sendMessage(con);
						sendMsg = con;
						Log.i("con_message", con);

						onStop();
						mViewMode = VIEW_MODE_INIT;
					}
					mText.setText("RETURN ");

				}
			}
		});

		final int viewMode = mViewMode;
		Rect Roi_chamber = new Rect(start_x,start_y,x_width,y_height);
		int avgR = 0, avgG = 0, avgB = 0;
		int avgR2 = 0, avgG2 = 0, avgB2 = 0;
		String Text_RGB;
		String Text_RGB_2;
		String Text_hue;

		double hue_value;
		CalculateHue calculateHue = new CalculateHue();
		double hue_value_2f;
		Point point_hue;
		Point point_rgb;
		Scalar fontColor;
		switch (viewMode) {
			case VIEW_MODE_RGBA:
				mRgba = inputFrame.rgba();
//				drawRect();
				break;
			case VIEW_MODE_START:
				mRgba = inputFrame.rgba();
				mGray =inputFrame.gray();
                MatOfRect circle_rect =new MatOfRect();
                MatOfRect enter_rect = new MatOfRect();
                MatOfRect filled_rect =new MatOfRect();
                MatOfRect leave_rect =new MatOfRect();
                MatOfRect empty_rect =new MatOfRect();
                MatOfRect insuff_rect =new MatOfRect();
                MatOfRect bubble_rect =new MatOfRect();

				Point t1= new Point(cropped_x,cropped_y);
				Point t2= new Point(cropped_x+cropped_w,cropped_y+cropped_h);

				Point tt1=new Point(detectzone_x,detectzone_y);
				Point tt2=new Point(detectzone_x+detectzone_w,detectzone_y+detectzone_h);

				cropped_img =new Mat(mGray,new Rect(t1,t2));
				detection_zone=new Mat(mGray,new Rect(tt1,tt2));

                if(mJavaDetector!=null && mJavaDetector2!=null){

                    mN1=17; mN2=40; mN3=10; mN4=10; mN5=2; mN6=2; mN7=5;
                    minNeighborsfill=17;minNeighborsEmpty=35;
                    mJavaDetector.detectMultiScale(cropped_img,circle_rect,1.02,0,0,new Size(250,250),new Size());
					mJavaDetector2.detectMultiScale(detection_zone,enter_rect,1.1,minNeighbors,0,new Size(),new Size());
					mJavaDetector3.detectMultiScale(detection_zone,filled_rect,1.1,220,0,new Size(),new Size());
					mJavaDetector4.detectMultiScale(detection_zone,leave_rect,1.1,80,0,new Size(),new Size());
					mJavaDetector5.detectMultiScale(detection_zone,empty_rect,1.1,40,0,new Size(20,20),new Size());
					mJavaDetector6.detectMultiScale(detection_zone,insuff_rect,1.1,40,0,new Size(14,14),new Size());
					mJavaDetector7.detectMultiScale(detection_zone,bubble_rect,1.1,40,0,new Size(5,5),new Size(50,50));

//					mJavaDetector.detectMultiScale(cropped_img,circle_rect,1.02,0,0,new Size(250,250),new Size());
//					mJavaDetector2.detectMultiScale(detection_zone,enter_rect,1.1,90,0,new Size(),new Size());
//					mJavaDetector3.detectMultiScale(detection_zone,filled_rect,1.1,275,0,new Size(),new Size());
//					mJavaDetector4.detectMultiScale(detection_zone,leave_rect,1.1,80,0,new Size(),new Size());
//					mJavaDetector5.detectMultiScale(detection_zone,empty_rect,1.1,55,0,new Size(20,20),new Size());
//					mJavaDetector6.detectMultiScale(detection_zone,insuff_rect,1.1,70,0,new Size(14,14),new Size());
//					mJavaDetector7.detectMultiScale(detection_zone,bubble_rect,1.1,40,0,new Size(5,5),new Size(50,50));

				}


				DetectCircle_array =circle_rect.toArray();
				DetectEnter_array =enter_rect.toArray();
				DetectFilled_array =filled_rect.toArray();
				DetectLeave_array =leave_rect.toArray();
				DetectEmpty_array =empty_rect.toArray();
				DetectInsuff_array=insuff_rect.toArray();
				DetectBubble_array=bubble_rect.toArray();



				if(true){
					for (int k = 0; k < DetectCircle_array.length; k++) {

						double tmp_tl_x=0.0,tmp_tl_y=0.0,tmp_br_x=0.0,tmp_br_y=0.0;
						updated_count+=1;

						tmp_tl_x = DetectCircle_array[k].tl().x + cropped_x;
						tmp_tl_y = DetectCircle_array[k].tl().y + cropped_y;
						tmp_br_x = DetectCircle_array[k].br().x + cropped_x;
						tmp_br_y = DetectCircle_array[k].br().y + cropped_y;



						cropped_x=(int)tmp_tl_x-20;
						cropped_y=(int)tmp_tl_y-20;
//						cropped_w=(int)(tmp_br_x-tmp_tl_x)+40;
//						cropped_h=(int)(tmp_br_y-tmp_tl_y)+40;
						cropped_w=400;
						cropped_h=400;

//						detectMessage = "Chamber";

//						detectzone_x = (int) (tmp_tl_x+cropped_w/6);
//						detectzone_y = (int) (tmp_tl_y+cropped_h/4);
//						detectzone_w = 165;
//						detectzone_h = 141;
						detectzone_x = (int)((tmp_br_x+tmp_tl_x)/2 - 82);
						detectzone_y = (int)((tmp_br_y+tmp_tl_y)/2 - 70);
						detectzone_w = 165;
						detectzone_h = 141;

						is_in = false;
						colorSelect=new Scalar(255,0,0);
						break;
					}
				}
//

                if(FLAG_ENTER) {
                    for (int k = 0; k < DetectEnter_array.length; k++) {

						enter_count+=1;
                        empty_count=0;
                        if(enter_count>1) {
							tl_x = DetectEnter_array[k].tl().x + detectzone_x;
							tl_y = DetectEnter_array[k].tl().y + detectzone_y;
							br_x = DetectEnter_array[k].br().x + detectzone_x;
							br_y = DetectEnter_array[k].br().y + detectzone_y;
							detectMessage = "Fluid Entering";
							if(enter_count>40){ detectMessage = "Insufficient Filling"; }


							minNeighborsfill=25;
							FLAG_EMPTY = false;
							FLAG_FILLED = true;
							FLAG_INSUFF=true;
							is_in = false;
						}
                        break;
                    }



                }

                if(FLAG_FILLED) {

                    for (int k = 0; k < DetectFilled_array.length; k++) {
//						frame_num=0;
						minNeighbors=120;
                        filled_count+=1;
                        enter_count=0;
						if(filled_count>0) {

                            tl_x = DetectFilled_array[k].tl().x + detectzone_x;
                            tl_y = DetectFilled_array[k].tl().y + detectzone_y;
                            br_x = DetectFilled_array[k].br().x + detectzone_x;
                            br_y = DetectFilled_array[k].br().y + detectzone_y;

							detectMessage = "Filled";
							FLAG_ENTER = false;



							//FILLED가 한번 나온경우, STOP MESSAGE 보내고 타이머동작
							if(!FLAG_LEAVE) {
								movingStop(10);
								final TimerTask tt=new TimerTask() {
									@Override
									public void run() {
										timer_counter++;

										Log.i(TAG,String.format("TIMERTASK %d",timer_counter));

										runOnUiThread(new Runnable() {
											@Override
											public void run() {
												mTitle.setText(String.format("Time left to start\n %d s",9-timer_counter));
											}
										});
										if(timer_counter>=9)
										{
											movingGo(10);
											timer_counter=0;
											this.cancel();

											runOnUiThread(new Runnable() {
												@Override
												public void run() {
													mTitle.setText(String.format("GO"));
												}
											});
										}
									}
								};

								Timer timer=new Timer();
								timer.schedule(tt,0,1000);

							}


							FLAG_LEAVE = true;
							FLAG_BUBBLE=true;







						}
                        break;

                    }

                }


                if(FLAG_LEAVE) {
                    for (int k = 0; k < DetectLeave_array.length; k++) {




						leave_count+=1;
						filled_count=0;
						bubble_count=0;
						if(leave_count>1) {

							tl_x = DetectLeave_array[k].tl().x + detectzone_x;
							tl_y = DetectLeave_array[k].tl().y + detectzone_y;
							br_x = DetectLeave_array[k].br().x + detectzone_x;
							br_y = DetectLeave_array[k].br().y + detectzone_y;
							detectMessage = "Fluid Leaving";
							is_in = false;
                            minNeighbors=100;
							FLAG_EMPTY = true;
							FLAG_FILLED = false;

						}
                        break;


                    }

                }

                if(FLAG_EMPTY) {
                    for (int k = 0; k < DetectEmpty_array.length; k++) {



						empty_count+=1;
                        leave_count=0;
                        if(empty_count>1) {
							tl_x = DetectEmpty_array[k].tl().x + detectzone_x;
							tl_y = DetectEmpty_array[k].tl().y + detectzone_y;
							br_x = DetectEmpty_array[k].br().x + detectzone_x;
							br_y = DetectEmpty_array[k].br().y + detectzone_y;

							detectMessage = "Empty";


							FLAG_LEAVE = false;
							FLAG_BUBBLE = false;

							FLAG_ENTER = true;
						}

                        break;


                    }
                }
                if(FLAG_INSUFF){
				for (int k = 0; k < DetectInsuff_array.length; k++) {



					if(enter_count>3 ) {
						tl_x = DetectInsuff_array[k].tl().x + detectzone_x;
						tl_y = DetectInsuff_array[k].tl().y + detectzone_y;
						br_x = DetectInsuff_array[k].br().x + detectzone_x;
						br_y = DetectInsuff_array[k].br().y + detectzone_y;
						detectMessage = "Insufficient Filling ";
						minNeighborsfill = 15;


						FLAG_ENTER = false;
						FLAG_FILLED = true;


						break;
						}
					}
				}

                if(FLAG_BUBBLE) {



						for (int k = 0; k < DetectBubble_array.length; k++) {

						    bubble_count+=1;
							if(bubble_count>3) {
								tl_x = DetectBubble_array[k].tl().x + detectzone_x;
								tl_y = DetectBubble_array[k].tl().y + detectzone_y;
								br_x = DetectBubble_array[k].br().x + detectzone_x;
								br_y = DetectBubble_array[k].br().y + detectzone_y;

								Imgproc.rectangle(detection_zone, new Point(tl_x, tl_y), new Point(br_x, br_y), new Scalar(0, 0, 0), 1);

								detectMessage = "Bubble";

								FLAG_FILLED = false;
								FLAG_INSUFF=false;
								FLAG_EMPTY = true;


								break;
							}
					}
				}


				drawRect();


				break;

			case VIEW_MODE_INIT:
				// input frame has RBGA format
				mRgba = inputFrame.rgba();
				mGray=inputFrame.gray();
				drawRect() ;
				break;
			case VIEW_MODE_STOP:
				mRgba = inputFrame.rgba();
				colorSelect=new Scalar(0,0,0);
				detectzone_x=0;detectzone_y=0;detectzone_h=0;detectzone_w=0;
			break;
			case VIEW_MODE_CHECK:
				mRgba = inputFrame.rgba();
				drawRect() ;
				break;
		}

		return mRgba;
	}
	String writingMessage="";
	double writingx=0;

	private void drawRect() {
		// TODO Auto-generated method stub
		Scalar red=new Scalar(255,0,0);

		p.x=cropped_x+0;
		p.y=cropped_y+cropped_h+60;
		p_to.x=cropped_x +40;
		p_to.y=cropped_y + cropped_h+100;


//		Log.i(TAG,String.format("%d %d",(int)writingx,(int)tl_x));

		if((int)writingx==(int)tl_x){
			writingx=tl_x;
			count++;
		}else{
			writingx=tl_x;
			count=0;
		}



		if(count<3) {
			Imgproc.rectangle(mRgba, new Point(tl_x, tl_y), new Point(br_x, br_y), new Scalar(255, 255, 255), 1);
			Imgproc.putText(mRgba, detectMessage, new Point(cropped_x +50 , cropped_y + cropped_h+100), 2, 2, new Scalar(0, 0, 0), 3);
			Imgproc.rectangle(mRgba,p,p_to,new Scalar(255,255,255),2);

		}



		
            Imgproc.rectangle(mRgba, new Point(cropped_x, cropped_y), new Point(cropped_x + cropped_w, cropped_y + cropped_h), colorSelect, thickness);
            Imgproc.putText(mRgba, String.format(Locale.KOREA, "FPS: %.1f", fps), new Point(cropped_x, cropped_y - 20), 1, 2, new Scalar(0, 0, 0), 2);

            Imgproc.rectangle(mRgba,new Point(detectzone_x,detectzone_y),new Point(detectzone_x+detectzone_w,detectzone_y+detectzone_h),new Scalar(255,255,255),1);
}

	private final  Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MESSAGE_STATE_CHANGE:
					if (D)
						Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
					switch (msg.arg1) {
						case BluetoothChatService.STATE_CONNECTED:

							mTitle.setText(R.string.title_connected_to);
							mTitle.append(Integer.toString(frame_num));
							break;
						case BluetoothChatService.STATE_CONNECTING:
							mTitle.setText(R.string.title_connecting);
							break;
						case BluetoothChatService.STATE_LISTEN:
						case BluetoothChatService.STATE_NONE:
							mTitle.setText(R.string.title_not_connected);
							break;
					}
					break;
				case MESSAGE_WRITE:

					break;
				case MESSAGE_READ:
					byte[] readBuf = (byte[]) msg.obj;
					// construct a string from the valid bytes in the buffer
					String readMessage = new String(readBuf, 0, msg.arg1);
//            mConversationArrayAdapter.add(mConnectedDeviceName + ":  "
//                  + readMessage);
					break;
				case MESSAGE_DEVICE_NAME:
					// save the connected device's name
					mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
					Toast.makeText(getApplicationContext(),
							"Connected to " + mConnectedDeviceName,
							Toast.LENGTH_SHORT).show();
					break;
				case MESSAGE_TOAST:
					Toast.makeText(getApplicationContext(),
							msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
							.show();
					break;
			}
		}
	};


	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (D)
			Log.d(TAG, "onActivityResult " + resultCode);
		switch (requestCode) {
			case REQUEST_CONNECT_DEVICE:
				// When DeviceListActivity returns with a device to connect
				if (resultCode == Activity.RESULT_OK) {
					// Get the device MAC address
					String address = data.getExtras().getString(
							DeviceListActivity.EXTRA_DEVICE_ADDRESS);
					// Get the BLuetoothDevice object
					BluetoothDevice device = mBluetoothAdapter
							.getRemoteDevice(address);
					// Attempt to connect to the device
					mChatService.connect(device);
				}
				break;
			case REQUEST_ENABLE_BT:
				// When the request to enable Bluetooth returns
				if (resultCode == Activity.RESULT_OK) {
					// Bluetooth is now enabled, so set up a chat session
					setupChat();
				} else {
					// User did not enable Bluetooth or an error occured
					Log.d(TAG, "BT not enabled");
					Toast.makeText(this, R.string.bt_not_enabled_leaving,
							Toast.LENGTH_SHORT).show();
					finish();
				}
		}
	}

	private void setupChat() {
		Log.d(TAG, "setupChat()");
		// Initialize the BluetoothChatService to perform bluetooth connections
		mChatService = new BluetoothChatService(this, mHandler);
		// Initialize the buffer for outgoing messages
		mOutStringBuffer = new StringBuffer("");
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.scan:
				// Launch the DeviceListActivity to see devices and do scan
				Intent serverIntent = new Intent(this, DeviceListActivity.class);
				startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
				return true;
		}
		return false;
	}

	private void sendMessage(String message) {
		// Check that we're actually connected before trying anything
		if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
					.show();
			return;
		}
		byte[] send = message.getBytes();
		mChatService.write(send);

		// Check that there's actually something to send
	}


	Handler mhandler = new Handler();

	Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			textView_for_th.setText(Integer.toString(RGB_data_array[0]));

			mTitle.setText(Integer.toString(frame_num));

			//To calculate hue value
//			Double test = new Double();
			CalculateHue calculateHue = new CalculateHue();
			if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
				String con = new String("GO" + "\n");
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}if(!sendMsg.equals(con)){
					sendMessage(con);
					Log.i("con_message",con);

					sendMsg = con;

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				mText.setText("MOTOR : ON ");
			}
		}

	};
//
//	public void onClick0(View v){
//		switch (v.getId()){
//			case R.id.btn_up:
//				start_y-=10;
//
//
//				seekBarProgress=seekBar.getProgress();
//
//				Log.i("start_up",Integer.toString(start_y));
//
//				Log.i("start_z",Integer.toString(seekBarProgress));
//
//
//		}
//	}
//
//
//	public void onClick1(View v){
//		switch (v.getId()){
//
//			case R.id.btn_down:
//				start_y+=10;
//
//
//		}
//	}   public void onClick3(View v){
//		switch (v.getId()){
//
//			case R.id.btn_left:
//				start_x-=10;
//				Log.i("start_left",Integer.toString(start_x));
//				Toast.makeText(getApplicationContext(),Integer.toString(minNeighbors),Toast.LENGTH_SHORT).show();
//
//				if(minNeighbors>2) minNeighbors-=1;
//		}
//	}   public void onClick2(View v){
//		switch (v.getId()){
//
//			case R.id.btn_right:
//				start_x+=10;
//				if(minNeighbors<30)minNeighbors+=1;
//				Toast.makeText(getApplicationContext(),Integer.toString(minNeighbors),Toast.LENGTH_SHORT).show();
//				Log.i("start_right",Integer.toString(start_x));
//
//
//
//		}
//	}
	public int checking_r_changed =0;
	public int checking_g_changed=0;
	public int checking_b_changed=0;


	protected void onStart(){
		super.onStart();
		mhandler= new Handler();
//		mhandler.postDelayed(mRunnable,10);

	}

	protected void onStop(){
		super.onStop();
		mhandler.removeCallbacks(mRunnable);
	}





	public void movingStop(int milliseconds){

		if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
			BTMessage="STOP";
			Log.i(TAG,BTMessage);
			String con = new String(BTMessage + "\n");
			try {
				Thread.sleep(milliseconds);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if(!sendMsg.equals(con)){
				Log.i("con_message",con);

				sendMessage(con);

				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				sendMsg = con;
			}



		}
	}


	public void movingGo(int milliseconds){
		if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
			BTMessage="GO";
			Log.i(TAG,BTMessage);

			String con = new String(BTMessage + "\n");
			try {
				Thread.sleep(milliseconds);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if(!sendMsg.equals(con)){
				Log.i("con_message",con);

				sendMessage(con);
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				sendMsg = con;


			}


		}
	}
	public void movingBack(int milliseconds){
		if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
			BTMessage="RETURN";
			Log.i(TAG,BTMessage);

			String con = new String(BTMessage + "\n");
			try {
				Thread.sleep(milliseconds);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if(!sendMsg.equals(con)){
				Log.i("con_message",con);

				sendMessage(con);
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				sendMsg = con;


			}


		}
	}


	//////////////////////////////// 퍼미션 관련 메소드///////////////////////////////////////
	static final int PERMISSIONS_REQUEST_CODE = 1000;
	String[] PERMISSIONS = {"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
	private boolean hasPermissions(String[] permissions) {
		int result;

		//스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
		for (String perms : permissions) {

			result = ContextCompat.checkSelfPermission(this, perms);

			if (result == PackageManager.PERMISSION_DENIED) {
				//허가 안된 퍼미션 발견
				return false;
			}
		}

		//모든 퍼미션이 허가되었음
		return true;
	}
	/////////////////////////////////퍼미션 관련 메소드///////////////////////////////////////



	private  class TimerHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what){
				case MESSAGE_TIMER_START:
					timer_counter=0;
					this.removeMessages(MESSAGE_TIMER_REPEAT);
					this.sendEmptyMessage(MESSAGE_TIMER_REPEAT);
					break;




					//180초동안 대기
				case MESSAGE_TIMER_REPEAT:
					if(timer_counter>50) {
						movingGo(10);
						this.sendEmptyMessageDelayed(MESSAGE_TIMER_STOP, 1000);
					}

					timer_counter+=1;

					Log.i(TAG,String.format("TImer counter %d",timer_counter));
					this.sendEmptyMessageDelayed(MESSAGE_TIMER_REPEAT,1000);

					//GO 명령내리고 타이머 소멸
				case MESSAGE_TIMER_STOP:

					this.removeMessages(MESSAGE_TIMER_REPEAT);
					break;
			}
		}
	}

}
