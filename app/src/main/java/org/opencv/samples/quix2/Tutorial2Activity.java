package org.opencv.samples.quix2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
import android.hardware.Camera;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class Tutorial2Activity extends Activity implements
		CvCameraViewListener2 {




	private int frame_num=0;

	public boolean FLAG_ENTER=false;
	public boolean FLAG_FILLED=false;
	public boolean FLAG_LEAVE=true;
	public boolean FLAG_EMPTY=true;
//	public boolean FLAG_INSUFF=false;
	public boolean FLAG_BUBBLE=false;



	public boolean is_in=true;
	public boolean is_started=true;


	public int updated_count=0;
	public int FLAG_INI_VALUE=0;
	public double scaleFactor;
	public int minNeighbors,minNeighborsfill,minNeighborsEmpty;
	public boolean ini_F = false;


	private static final String TAG = "Tutorial2Activity.java";

	private static final int VIEW_MODE_RGBA = 0;
	private static final int VIEW_MODE_START = 1;
	private static final int VIEW_MODE_STOP = 3;
	private static final int VIEW_MODE_INIT = 4;
	private static final int VIEW_MODE_CHECK =5;





	private static final int MESSAGE_TIMER_START=100;
	private static final int MESSAGE_TIMER_REPEAT=101;
	private static final int MESSAGE_TIMER_STOP=102;
	int timer_counter;
	int stop_counter=0;


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
	public int start_x=1340;
	public int start_y=720;
	public int x_width=25;
	public int y_height=30;

	public int cropped_x =700;//1070
	public int cropped_y = 300;

	public int cropped_w = 1200;
	public int cropped_h =600;


	public int detectzone_x =0;//1070
	public int detectzone_y = 0;//710

	public int detectzone_w = 0;//160
	public int detectzone_h =0;//100

	public int thickness =0;


	private int mViewMode;
	private Mat mRgba;
	private Mat mIntermediateMat;
	private Mat mGray;

	private CascadeClassifier	mJavaDetector,mJavaDetector2,mJavaDetector3,mJavaDetector4,mJavaDetector5;

	private TextView mTitle;
	private TextView mText;

	private Tutorial3View mOpenCvCameraView;

	private String sendMsg="";



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

	double[] rgb_value=new double[3];
	double hue_value;
	CalculateHue calculateHue = new CalculateHue();

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;
	// Name of the connected device

	// Array adapter for the conversation thread

	// String buffer for outgoing messages

	// Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter = null;

	private BluetoothChatService mChatService = null;
	Point p = new Point(0,0);
	Point p_to = new Point(0,0);


	Button InitBtn;
	Button StartBtn;
	Button StopBtn;
	Button ReturnBtn;
	Button Btn_motorGo,Btn_motorStop,Btn_motorBack;
	Button Btn_up,Btn_down,Btn_left,Btn_right;


	int count=0;
	int enter_count=0,filled_count=0,leave_count=0,empty_count=0,bubble_count=0;
	long fpsStratTime=0L;

//	TimerHandler timerHandler = new TimerHandler();

	CheckBox flashCheckbox;
	SeekBar seekBar;

	TextView textView_for_th;


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
						InputStream is = getResources().openRawResource(R.raw.pillar3); //cascade_filled_6f
						InputStream is_enter=getResources().openRawResource(R.raw.enter20200807_2); //maniscus 13
						InputStream is_filled=getResources().openRawResource(R.raw.filled_20200807);
						InputStream is_leave=getResources().openRawResource(R.raw.leave_20200807); //cascade_empty_6
						InputStream is_empty=getResources().openRawResource(R.raw.empty_20200807); //cempty_4 ok
						InputStream is_insuff=getResources().openRawResource(R.raw.insuff20191226); //cascade_moved
						InputStream is_bubble=getResources().openRawResource(R.raw.bubble20200108); //cascade_moved

						seekBar.post(new Runnable() {
							@Override
							public void run() {
								seekBar.setProgress(20);
							}
						});

						scaleFactor=1.11;

						 File mCascadeFile, mModelFile2, mModelFile3, mModelFile4,mModelFile5,mModelFile6,mModelFile7;

						File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
						File manisDir=getDir("cascade",Context.MODE_PRIVATE);
						File model3Dir=getDir("cascade",Context.MODE_PRIVATE);
						File model4Dir=getDir("cascade",Context.MODE_PRIVATE);
						File model5Dir=getDir("cascade",Context.MODE_PRIVATE);
						File modelempDir=getDir("cascade",Context.MODE_PRIVATE);



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
//						mJavaDetector6= new CascadeClassifier(mModelFile6.getAbsolutePath());
//						mJavaDetector7= new CascadeClassifier(mModelFile7.getAbsolutePath());


						if (mJavaDetector.empty() || mJavaDetector2.empty()) {
							Log.e(TAG, "Failed to load cascade classifier");
							mJavaDetector = null;
						} else
							Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

//						mNativeDetector = new DetectionBasedTracker(mCascadeFile.getAbsolutePath(), 0);



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



	TimerTask tt=null;
	public int START_TIME_SET =900;
	public int ANTIGEN_TIME_SET =900;
	public int TMB_TIME_SET=600;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.tutorial2_surface_view);



		fpsStratTime=System.currentTimeMillis();

		InitBtn = findViewById(R.id.button1);
		StartBtn = findViewById(R.id.button2);
		StopBtn = findViewById(R.id.button3);
		ReturnBtn = findViewById(R.id.button6);
		Btn_motorGo=findViewById(R.id.btn_motorGo);
		Btn_motorStop=findViewById(R.id.btn_motorStop);
		Btn_motorBack=findViewById(R.id.btn_motorBack);
		seekBar=findViewById(R.id.CameraZoomControls);
		Btn_up=findViewById(R.id.btn_up);
		Btn_down=findViewById(R.id.btn_down);
		Btn_left=findViewById(R.id.btn_left);
		Btn_right=findViewById(R.id.btn_right);


		seekBarProgress=seekBar.getProgress();
		mOpenCvCameraView =  findViewById(R.id.tutorial2_activity_surface_view);
		mOpenCvCameraView.setCvCameraViewListener(this);
		mOpenCvCameraView.setEnabled(true);
		mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
		mOpenCvCameraView.setZoomControl((SeekBar) findViewById(R.id.CameraZoomControls));


//        mOpenCvCameraView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_MOVE) {
//                    focusOnTouch(event);
//                }
//
//
//
//				Log.i(TAG,Integer.toString(event.getAction()));
//
//				return true;
//            }
//
//
//        });

		mTitle =findViewById(R.id.textView1);
		mText =  findViewById(R.id.textView2);
		textView_for_th = findViewById(R.id.textView4);

		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

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
					seekBar.setProgress(30);
//					mOpenCvCameraView.setAWB(true);
//					mViewMode=VIEW_MODE_THRESH;
//					flashCheckbox.setVisibility(View.INVISIBLE);
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
		seekBar.setProgress(30);

	}


	@Override
	public void onCameraViewStopped() {


		mRgba.release();
		mGray.release();
		mIntermediateMat.release();


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
			fps=(float)1000/secTime;


		for(int i=start_x+1;i<start_x+10;i++){
			for (int j=start_y+1;j<start_y+10;j++){
				rgb_value=mRgba.get(j,i);
				int[] tmp=new int[3];
				tmp[0]=(int)rgb_value[0];
				tmp[1]=(int)rgb_value[1];
				tmp[2]=(int)rgb_value[2];
				hue_value=calculateHue.getH(tmp);
			}
		}


		InitBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				seekBar.post(new Runnable() {
					@Override
					public void run() {
						seekBar.setProgress(20);
					}
				});

				mGray = inputFrame.gray();
//				Point t1= new Point(700,200);
//				Point t2= new Point(1700,800);
//				cropped_img =new Mat(mGray,new Rect(t1,t2));
				SystemClock.sleep(1000);
				Intent serverIntent = new Intent(getApplicationContext(), DeviceListActivity.class);
				startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
				if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
					// Start the Bluetooth chat services
					mChatService.start();
				}
				mViewMode = VIEW_MODE_INIT;

				if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
					String con = ("STOP" + "\n");
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}if(!sendMsg.equals(con)){
						sendMessage(con);
						sendMsg = con;
					}
				}
				mText.setTextSize(20);



			}
		});

		Btn_motorGo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				movingGo(10);
				if(tt!=null) tt.cancel();
			}
		});
		Btn_motorBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				movingBack(10);
				if(tt!=null) tt.cancel();
			}
		});
		Btn_motorStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				movingStop(10);
				if(tt!=null) tt.cancel();
			}
		});

		StartBtn.setOnClickListener(new OnClickListener() {

			int elisaTimer=0;
			@Override
			public void onClick(View v) {


				if (is_started) {
					//ELISA 타이머 작동
					is_started=false;

					tt = new TimerTask() {
						@Override
						public void run() {
							elisaTimer += 1;


							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									mTitle.setText(String.format(Locale.KOREA,"Time left to start\n %d s", START_TIME_SET -elisaTimer));
								}
							});


							if (elisaTimer >= START_TIME_SET) {

								Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
								Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
								ringtone.play();
								tt.cancel();
								movingGo(10);
								mViewMode = VIEW_MODE_START;



								elisaTimer=0;


							}

						}
					};
					Timer timer = new Timer();
					timer.schedule(tt, 0, 1000);

				}
				else{
					is_started=true;
					Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
					Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
					ringtone.play();
					tt.cancel();
					movingGo(10);
					mViewMode = VIEW_MODE_START;
				}

			}

		});
		StopBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mViewMode = VIEW_MODE_STOP;


				mText.setTextSize(20);

//
			}
		});

		ReturnBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				FLAG_INI_VALUE=0;
				ini_F = false;
				movingReturn(10);

				if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
					String con = "RETURN" + "\n";

					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					if (!sendMsg.equals(con)) {
						try {
							Thread.sleep(30);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						sendMessage(con);
						sendMsg = con;
						Log.i("con_message", con);

						onStop();
						mViewMode = VIEW_MODE_INIT;
					}


				}
			}
		});

		Btn_up.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				start_y-=10;
			}
		});
		Btn_down.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				start_y+=10;
			}
		});
		Btn_left.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				start_x-=10;
			}
		});
		Btn_right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				start_x+=10;
			}
		});

		final int viewMode = mViewMode;





		Mat cropped_img,detection_zone;


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
					mJavaDetector2.detectMultiScale(detection_zone,enter_rect,1.1,0,0,new Size(),new Size());
					mJavaDetector3.detectMultiScale(detection_zone,filled_rect,1.1,5,0,new Size(),new Size());
					mJavaDetector4.detectMultiScale(detection_zone,leave_rect,1.1,10,0,new Size(),new Size());
					mJavaDetector5.detectMultiScale(detection_zone,empty_rect,1.1,1,0,new Size(),new Size());
//					mJavaDetector6.detectMultiScale(detection_zone,insuff_rect,1.1,40,0,new Size(14,14),new Size());
//					mJavaDetector7.detectMultiScale(detection_zone,bubble_rect,1.1,40,0,new Size(5,5),new Size(50,50));

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



				if(is_in){
					if(DetectCircle_array.length==1){

						double tmp_tl_x,tmp_tl_y;
						updated_count+=1;

						tmp_tl_x = DetectCircle_array[0].tl().x + cropped_x;
						tmp_tl_y = DetectCircle_array[0].tl().y + cropped_y;




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
						detectzone_x = (int)((tmp_tl_x) +50);
						detectzone_y = (int)((tmp_tl_y)+50);
						detectzone_w = 300;
						detectzone_h = 300;
						start_x=detectzone_x+(detectzone_w)/2;
						start_y=detectzone_y+(detectzone_h)/2;
						is_in = false;
						colorSelect=new Scalar(255,0,0);
						break;
					}
				}
//

				if(FLAG_ENTER) {
					if ( DetectEnter_array.length==1) {

						enter_count+=1;
						empty_count=0;
						if(enter_count>1) {
							tl_x = DetectEnter_array[0].tl().x + detectzone_x;
							tl_y = DetectEnter_array[0].tl().y + detectzone_y;
							br_x = DetectEnter_array[0].br().x + detectzone_x;
							br_y = DetectEnter_array[0].br().y + detectzone_y;
							detectMessage = "Fluid Entering";



							minNeighborsfill=25;
//							FLAG_EMPTY = false;
//							FLAG_FILLED = true;
//							FLAG_INSUFF=true;
							is_in = false;
						}
						break;
					}



				}

				if(FLAG_FILLED) {

					if(DetectFilled_array.length==1) {
//						frame_num=0;
						minNeighbors=120;
						filled_count+=1;
						enter_count=0;
						if(filled_count>0) {

							tl_x = DetectFilled_array[0].tl().x + detectzone_x;
							tl_y = DetectFilled_array[0].tl().y + detectzone_y;
							br_x = DetectFilled_array[0].br().x + detectzone_x;
							br_y = DetectFilled_array[0].br().y + detectzone_y;

							detectMessage = "Filled";




							//FILLED가 한번 나온경우,stop 카운터에 따라 STOP MESSAGE 보내고 타이머동작
							if(!FLAG_LEAVE) {



								//if stop카운터%3==0 항체반응시간동안 stop. 그후 모터동작 및 stop_counter+1
								//else if stop카운터%3==1 washing 할때 stop하지 않고 stop_counter+1
								//else stop카운터%3==2 tmb반응시간동안 stop. 그후 모터동작 및 stop_counter+1
								if(stop_counter%3==0) {
									movingStop(10);
									tt = new TimerTask() {
										@Override
										public void run() {
											timer_counter++;

											Log.i(TAG, String.format("TIMERTASK %d", timer_counter));

											runOnUiThread(new Runnable() {
												@Override
												public void run() {
													mTitle.setText(String.format(Locale.KOREA, "Time left to start\n %d s", ANTIGEN_TIME_SET - timer_counter));
												}
											});

											if(timer_counter==ANTIGEN_TIME_SET-60){
												Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
												Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
												ringtone.play();
											}
											if (timer_counter >= ANTIGEN_TIME_SET) {

												movingGo(10);
												timer_counter = 0;
												stop_counter += 1; // stop카운터가 짝수면 멈추고, 홀수면 지나감.
												this.cancel();


												runOnUiThread(new Runnable() {
													@Override
													public void run() {
														mTitle.setText(String.format(Locale.KOREA, "GO"));
													}
												});
											}
										}
									};

									Timer timer = new Timer();
									timer.schedule(tt, 0, 1000);
								}
								else if(stop_counter%3==1){
									stop_counter+=1;



								}else{
									movingStop(10);
									tt = new TimerTask() {
										@Override
										public void run() {
											timer_counter++;

											Log.i(TAG, String.format("TIMERTASK %d", timer_counter));

											runOnUiThread(new Runnable() {
												@Override
												public void run() {
													mTitle.setText(String.format(Locale.KOREA, "Time left to start\n %d s", TMB_TIME_SET - timer_counter));
												}
											});


											if(timer_counter == TMB_TIME_SET-60){

												Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
												Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
												ringtone.play();
											}
											if (timer_counter >= TMB_TIME_SET) {

												movingGo(10);
												timer_counter = 0;
												stop_counter += 1; //
												this.cancel();


												runOnUiThread(new Runnable() {
													@Override
													public void run() {
														mTitle.setText(String.format(Locale.KOREA, "GO"));
													}
												});
											}
										}
									};

									Timer timer = new Timer();
									timer.schedule(tt, 0, 1000);
								}



							}

//
							FLAG_LEAVE = true;
							FLAG_EMPTY=true;
//							FLAG_BUBBLE=true;







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
//                            minNeighbors=100;
							FLAG_EMPTY = true;
							FLAG_FILLED = false;

						}
						break;


					}

				}

				if(FLAG_EMPTY) {
					if (FLAG_EMPTY) {
						for (int k = 0; k < DetectEmpty_array.length; k++) {


							tl_x = DetectEmpty_array[k].tl().x + detectzone_x;
							tl_y = DetectEmpty_array[k].tl().y + detectzone_y;
							br_x = DetectEmpty_array[k].br().x + detectzone_x;
							br_y = DetectEmpty_array[k].br().y + detectzone_y;

							detectMessage = "Empty";


							FLAG_LEAVE = false;
//							FLAG_BUBBLE = false;
//
//							FLAG_ENTER = true;


							break;


						}
					}
				}
//                if(FLAG_INSUFF){
//				for (int k = 0; k < DetectInsuff_array.length; k++) {
//
//
//
//					if(enter_count>3 ) {
//						tl_x = DetectInsuff_array[k].tl().x + detectzone_x;
//						tl_y = DetectInsuff_array[k].tl().y + detectzone_y;
//						br_x = DetectInsuff_array[k].br().x + detectzone_x;
//						br_y = DetectInsuff_array[k].br().y + detectzone_y;
//						detectMessage = "Insufficient Filling ";
//						minNeighborsfill = 15;
//
//
//						FLAG_ENTER = false;
//						FLAG_FILLED = true;
//
//
//						break;
//						}
//					}
//				}
//
//                if(FLAG_BUBBLE) {
//
//
//
//						for (int k = 0; k < DetectBubble_array.length; k++) {
//
//						    bubble_count+=1;
//							if(bubble_count>3) {
//								tl_x = DetectBubble_array[k].tl().x + detectzone_x;
//								tl_y = DetectBubble_array[k].tl().y + detectzone_y;
//								br_x = DetectBubble_array[k].br().x + detectzone_x;
//								br_y = DetectBubble_array[k].br().y + detectzone_y;
//
//								Imgproc.rectangle(detection_zone, new Point(tl_x, tl_y), new Point(br_x, br_y), new Scalar(0, 0, 0), 1);
//
//								detectMessage = "Bubble";
//
//								FLAG_FILLED = false;
//								FLAG_INSUFF=false;
//								FLAG_EMPTY = true;
//
//
//								break;
//							}
//					}
//				}


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
	double writingx=0;

	private void drawRect() {
		Scalar red=new Scalar(255,0,0);

		p.x=cropped_x;
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

		Imgproc.rectangle(mRgba,new Point(start_x,start_y),new Point(start_x+10,start_y+10),new Scalar(255,255,255),1);
		Imgproc.putText(mRgba, String.format(Locale.KOREA, "Hue: %.2f R:%.2f G:%.2f B:%.2f", hue_value,rgb_value[0],rgb_value[1],rgb_value[2]), new Point(cropped_x, cropped_y - 40), 1, 2, new Scalar(0, 0, 0), 2);

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
					String mConnectedDeviceName = null;
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
	public void onClick0(View v){
		switch (v.getId()){
			case R.id.btn_up:
				start_y-=10;


				seekBarProgress=seekBar.getProgress();

				Log.i("start_up",Integer.toString(start_y));

				Log.i("start_z",Integer.toString(seekBarProgress));


		}
	}





	protected void onStart(){
		super.onStart();
		mhandler= new Handler();

	}

	protected void onStop(){
		super.onStop();
		mhandler.removeCallbacks(mRunnable);
	}





	public void movingStop(int milliseconds){

		if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
			BTMessage="STOP";
			Log.i(TAG,BTMessage);
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mTitle.setText(BTMessage);
				}
			});
			String con =BTMessage + "\n";
			try {
				Thread.sleep(milliseconds);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if(!sendMsg.equals(con)){
				Log.i("con_message",con);

				sendMessage(con);

				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				sendMsg = con;
			}



		}
	}


	public void movingGo(int milliseconds){
		if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
			BTMessage="GO";

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mTitle.setText(BTMessage);
				}
			});
			String con = BTMessage + "\n";
			try {
				Thread.sleep(milliseconds);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}


			Log.i("con_message",con);

			sendMessage(con);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			sendMsg = con;

		}else{
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mTitle.setText("블루투스 연결불량");
				}
			});

		}
	}
	public void movingReturn(int milliseconds){
		if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
			BTMessage="RETURN";
			Log.i(TAG,BTMessage);
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mTitle.setText(BTMessage);
				}
			});
			String con = BTMessage + "\n";
			try {
				Thread.sleep(milliseconds);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if(!sendMsg.equals(con)){
				Log.i("con_message",con);

				sendMessage(con);
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				sendMsg = con;


			}


		}
	}
	public void movingBack(int milliseconds){
		if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
			BTMessage="BACK";
			Log.i(TAG,BTMessage);
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mTitle.setText(BTMessage);
				}
			});
			String con = BTMessage + "\n";
			try {
				Thread.sleep(milliseconds);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if(!sendMsg.equals(con)){
				Log.i("con_message",con);

				sendMessage(con);
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				sendMsg = con;


			}


		}
	}




}
