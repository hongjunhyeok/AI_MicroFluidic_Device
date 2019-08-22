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
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
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
import java.util.Calendar;
import java.util.Date;


public class Tutorial2Activity extends Activity implements
		CvCameraViewListener2,View.OnTouchListener {

	Zoomcameraview zoomcameraview;

	Thread threadDelay;


	private int RGB_data_array[]=new int[3];
	private int RGB_data_array_2[]=new int[3];
	private int RGB_avg_per_time[]=new int[10];
	private int frame_num=0;

	public boolean should_motor_on =false;
	public boolean FLAG_GREEN=false;
	public boolean FLAG_RED=false;
	public boolean FLAG_YELLOW=false;
	public boolean is_in=true;
	public boolean is_empty=false;


	public boolean mode_empty = true;
	public boolean mode_filled = false;
	public boolean mode_in = false;
	public boolean mode_out = false;
	public boolean tmp_flag =false;


	public int FLAG_INI_VALUE=0;
	public double scaleFactor;
	public int minNeighbors;
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



	private static final int COLOR_RED = 0;
	private static final int COLOR_GREEN=1;
	private  static  final  int COLOR_YELLOW=2;


	Rect[] bubble_array=new Rect[2];

	public double tl_x=0.0;
	public double tl_y=0.0;

	public double br_x=0.0;
	public double br_y=0.0;

	public String detectMessage="";

	public int seekBarProgress;
	public int thresholdProgress;
	public int start_x=1340;
	public int start_y=720;
	public int x_width=25;
	public int y_height=30;


	public int start_x_2=1360;
	public int start_y_2=520;
	public int cropped_x =1070;//1070
	public int cropped_y = 650;

	public int cropped_w = 250;
	public int cropped_h =250;
	public int thickness =2;


	public int x_width_2=15;
	public int y_height_2=15;

	private int mViewMode;
	private Mat mRgba,mask;
	private Mat mIntermediateMat;
	private Mat mGray, cropped_img;
	private File mCascadeFile, mModelFile2, mModelFile3, mModelFile4,mModelFile5,mModelFile6;
	private CascadeClassifier	mJavaDetector,mJavaDetector2,mJavaDetector3,mJavaDetector4,mJavaDetector5,mJavaDetector6;

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
	public int mN1,mN2,mN3,mN4,mN5,mN6;
	public double sF =1.3;



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
	Button ReturnBtn;
	Button thPlsBtn1,thPlsBtn2,thPlsBtn3,thPlsBtn4,thPlsBtn5,thPlsBtn6,thPlsBtn7;
	Button thmnsBtn1,thmnsBtn2,thmnsBtn3,thmnsBtn4,thmnsBtn5,thmnsBtn6,thmnsBtn7;

//	TimerHandler timerHandler = new TimerHandler();

	@Override
	public <T extends View> T findViewById(int id) {
		return super.findViewById(id);
	}

	CheckBox flashCheckbox;
	SeekBar seekBar,thresholdBar;
	TextView textView_for_th;

	File file;
	String Text_for_R="R: ";
	String Text_for_G="G: ";
	String Text_for_B="B: ";

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
						InputStream is = getResources().openRawResource(R.raw.enter); //cascade_filled_6f
						InputStream is_man=getResources().openRawResource(R.raw.filled); //cascade_filled_7
						InputStream is_3=getResources().openRawResource(R.raw.insuff); //cascade_filled_8
						InputStream is_4=getResources().openRawResource(R.raw.leave); //cascade_empty_6
						InputStream is_5=getResources().openRawResource(R.raw.cascade_green_moving_190708); //cascade_moved
						InputStream is_6=getResources().openRawResource(R.raw.cascade_empty_6); //cascade_moved


						scaleFactor=1.11;minNeighbors=5;
						mN1=20; mN2=15; mN3=7; mN4=7; mN5=6; mN6=5;


						File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
						File manisDir=getDir("cascade",Context.MODE_PRIVATE);
						File model3Dir=getDir("cascade",Context.MODE_PRIVATE);
						File model4Dir=getDir("cascade",Context.MODE_PRIVATE);
						File model5Dir=getDir("cascade",Context.MODE_PRIVATE);
						File modelempDir=getDir("cascade",Context.MODE_PRIVATE);


						Log.i(TAG,"directory_cascade :"+cascadeDir.getAbsolutePath());
						Log.i(TAG,"directory_maniscus :"+cascadeDir.getAbsolutePath());

						mCascadeFile = new File(cascadeDir, "cascade_in.xml");
						mModelFile2 = new File(manisDir,"cascade_out4.xml");
						mModelFile3= new File(model3Dir,"cascade_empty_6.xml");
						mModelFile4= new File(model4Dir,"cascade_empty_7.xml");
						mModelFile5= new File(model5Dir,"cascade_empty_8.xml");
						mModelFile6= new File(modelempDir,"cascade_empty_9.xml");

						FileOutputStream os = new FileOutputStream(mCascadeFile);
						FileOutputStream os_mains = new FileOutputStream(mModelFile2);
						FileOutputStream os_3=new FileOutputStream(mModelFile3);
						FileOutputStream os_4=new FileOutputStream(mModelFile4);
						FileOutputStream os_5=new FileOutputStream(mModelFile5);
						FileOutputStream os_6=new FileOutputStream(mModelFile6);


						byte[] buffer = new byte[4096];
						byte[] buffer2 = new byte[4096];
						byte[] buf3 = new byte[4096];
						byte[] buf4 = new byte[4096];
						byte[] buf5 = new byte[4096];
						byte[] buf6 = new byte[4096];


						int bytesRead,bytesRead2,bytesRead3,bytesRead4,bytesRead5,bytesRead6;
						while ((bytesRead = is.read(buffer)) != -1) {
							os.write(buffer, 0, bytesRead);
						}
						while ((bytesRead2 = is_man.read(buffer2)) != -1) {
							os_mains.write(buffer2, 0, bytesRead2);
						}
						while((bytesRead3 = is_3.read(buf3))!=-1){
							os_3.write(buf3,0,bytesRead3);
						}
						while((bytesRead4 = is_4.read(buf4))!=-1){
							os_4.write(buf4,0,bytesRead4);
						}
						while((bytesRead5 = is_5.read(buf5))!=-1){
							os_5.write(buf5,0,bytesRead5);
						}
						while((bytesRead6 = is_6.read(buf6))!=-1){
							os_6.write(buf6,0,bytesRead6);
						}

						is.close();
						is_man.close();
						is_3.close();
						is_4.close();
						is_5.close();
						is_6.close();
						os.close();
						os_mains.close();
						os_3.close();
						os_4.close();
						os_5.close();
						os_6.close();
						mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
						mJavaDetector2 =new CascadeClassifier(mModelFile2.getAbsolutePath());
						mJavaDetector3= new CascadeClassifier(mModelFile3.getAbsolutePath());
						mJavaDetector4= new CascadeClassifier(mModelFile4.getAbsolutePath());
						mJavaDetector5= new CascadeClassifier(mModelFile5.getAbsolutePath());
						mJavaDetector6= new CascadeClassifier(mModelFile6.getAbsolutePath());


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

		InitBtn = (Button) findViewById(R.id.button1);
		StartBtn = (Button) findViewById(R.id.button2);
		StopBtn = (Button) findViewById(R.id.button3);
		ReturnBtn = (Button) findViewById(R.id.button6);
		seekBar=(SeekBar)findViewById(R.id.CameraZoomControls);
		thresholdBar=(SeekBar)findViewById(R.id.ThresholdControls);
		thPlsBtn1=findViewById(R.id.button15);
		thPlsBtn2=findViewById(R.id.button16);
		thmnsBtn1=findViewById(R.id.button17);
		thmnsBtn2=findViewById(R.id.button18);
		thPlsBtn3=findViewById(R.id.button20);
		thmnsBtn3=findViewById(R.id.button21);
		thPlsBtn4=findViewById(R.id.button22);
		thmnsBtn4=findViewById(R.id.button23);
		thPlsBtn5=findViewById(R.id.button24);
		thmnsBtn5=findViewById(R.id.button25);
		thPlsBtn6=findViewById(R.id.button26);
		thmnsBtn6=findViewById(R.id.button27);
		thPlsBtn7=findViewById(R.id.button28);
		thmnsBtn7=findViewById(R.id.button29);




		thPlsBtn1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mN1+=1;
				Toast.makeText(getApplication(),Integer.toString(mN1),Toast.LENGTH_SHORT).show();
				Log.i(TAG,Integer.toString(mN1));
			}
		});

		thPlsBtn2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mN2+=1;
				Toast.makeText(getApplicationContext(),Integer.toString(mN2),Toast.LENGTH_SHORT).show();

			}
		});
		thPlsBtn3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mN3+=1;
				Toast.makeText(getApplicationContext(),Integer.toString(mN3),Toast.LENGTH_SHORT).show();

			}
		});
		thPlsBtn4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(sF<2.0)
					sF+=0.01;

				else
					sF+=0.1;

				Toast.makeText(getApplicationContext(),Double.toString(sF),Toast.LENGTH_SHORT).show();

			}

		});
		thPlsBtn5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mN4+=1;
				Toast.makeText(getApplicationContext(),Integer.toString(mN4),Toast.LENGTH_SHORT).show();

			}
		});
		thPlsBtn6.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mN5+=1;
				Toast.makeText(getApplicationContext(),Integer.toString(mN5),Toast.LENGTH_SHORT).show();

			}
		});
		thPlsBtn7.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mN6+=1;
				Toast.makeText(getApplicationContext(),Integer.toString(mN6),Toast.LENGTH_SHORT).show();

			}
		});


		thmnsBtn1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				if(mN1>2)
					mN1-=1;
				Toast.makeText(getApplicationContext(),Integer.toString(mN1),Toast.LENGTH_SHORT).show();

			}
		});
		thmnsBtn2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(mN2>2)

					mN2-=1;
				Toast.makeText(getApplicationContext(),Integer.toString(mN2),Toast.LENGTH_SHORT).show();

			}
		});
		thmnsBtn3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(mN3>2)

					mN3-=1;
				Toast.makeText(getApplicationContext(),Integer.toString(mN3),Toast.LENGTH_SHORT).show();

			}
		});
		thmnsBtn5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(mN4>2)

					mN4-=1;
				Toast.makeText(getApplicationContext(),Integer.toString(mN4),Toast.LENGTH_SHORT).show();

			}
		});
		thmnsBtn6.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(mN5>2)

					mN5-=1;
				Toast.makeText(getApplicationContext(),Integer.toString(mN5),Toast.LENGTH_SHORT).show();

			}
		});
		thmnsBtn7.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(mN6>2)

					mN6-=1;
				Toast.makeText(getApplicationContext(),Integer.toString(mN6),Toast.LENGTH_SHORT).show();

			}
		});
		thmnsBtn4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				if(sF<2.0)
					sF-=0.01;
				else
					sF-=0.1;

				Log.i("start_down",Integer.toString(start_y));
				Toast.makeText(getApplicationContext(),Double.toString(sF),Toast.LENGTH_SHORT).show();


			}
		});
		seekBarProgress=seekBar.getProgress();
		thresholdProgress=thresholdBar.getProgress();

		mOpenCvCameraView = (Tutorial3View) findViewById(R.id.tutorial2_activity_surface_view);
		mOpenCvCameraView.setCvCameraViewListener(this);
		mOpenCvCameraView.setEnabled(true);
		mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
		mOpenCvCameraView.setZoomControl((SeekBar) findViewById(R.id.CameraZoomControls));
//		zoomcameraview.setZoomControl(seekBar);



		mTitle = (TextView) findViewById(R.id.textView1);
		mText = (TextView) findViewById(R.id.textView2);
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
//					mOpenCvCameraView.setAWB(true);
//					mViewMode=VIEW_MODE_THRESH;
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



	int count=0;

//	CascadeClassifier cascadeClassifier;

	public Mat onCameraFrame(final CvCameraViewFrame inputFrame) {
		frame_num++;
//      count++;
//      if(count<5){
//         return null;
//      }
//      count =0;
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


		thresholdBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
													int progressvalue=20;
													@Override
													public void onProgressChanged(SeekBar seekBar, int progress,
																				  boolean fromUser) {
														// TODO Auto-generated method stub
														thresholdProgress=thresholdBar.getProgress();
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




		StartBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("con_message",Boolean.toString(FLAG_GREEN));
				// TODO Auto-generated method stub
				mViewMode = VIEW_MODE_START;
//				timerHandler.sendEmptyMessage(MESSAGE_TIMER_START);
				mHandler.sendEmptyMessage(MESSAGE_TIMER_START);

			}
		});
		StopBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mViewMode = VIEW_MODE_STOP;

				if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
					String con = new String("STOP" + "\n");
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(!sendMsg.equals(con)){
						sendMessage(con);
						sendMsg = con;}
				}

				mText.setTextSize(20);

				path = Environment.getExternalStorageDirectory()
						+ "/android/data/org.opencv.samples.quix2/test";
				file = new File(path);
				Log.i(TAG,path);
				if (!file.exists())
					file.mkdirs();
				try {
					Date currentTime = Calendar.getInstance().getTime();
					FileOutputStream fos = new FileOutputStream(path+"/"+currentTime+".txt", true);
					BufferedWriter buf = new BufferedWriter(new OutputStreamWriter(fos));

					buf.newLine(); // 개행
					buf.write(Text_for_R);
					buf.newLine(); // 개행
					buf.write(Text_for_G);
					buf.newLine();
					buf.write(Text_for_B);
					buf.newLine();
//
					Log.i(TAG,"saved");
					buf.close();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
					Log.i(TAG,"not saved"+e.toString());

				}
			}
		});
		ReturnBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FLAG_GREEN=false;
				FLAG_RED=false;
				FLAG_YELLOW=false;
				FLAG_INI_VALUE=0;
				ini_F = false;


				// TODO Auto-generated method stub
				Log.i("con_message",Boolean.toString(FLAG_GREEN));
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
		File file;


		switch (viewMode) {

			case VIEW_MODE_RGBA:
				mRgba = inputFrame.rgba();
				drawRect();

				break;
			case VIEW_MODE_START:
				thickness=2;
				mRgba = inputFrame.rgba();
				mGray =inputFrame.gray();


				MatOfRect bubble_rect =new MatOfRect();
				MatOfRect manis_rect = new MatOfRect();
				MatOfRect model3_rect =new MatOfRect();
				MatOfRect model4_rect =new MatOfRect();
				MatOfRect model5_rect =new MatOfRect();
				MatOfRect modele_emp_rect =new MatOfRect();

				Point t1= new Point(cropped_x,cropped_y);
				Point t2= new Point(cropped_x+cropped_w,cropped_y+cropped_h);


				cropped_img =new Mat(mGray,new Rect(t1,t2));


				if(mJavaDetector!=null && mJavaDetector2!=null){
					mJavaDetector.detectMultiScale(cropped_img,bubble_rect,sF,mN1,0,new Size(),new Size());
					mJavaDetector2.detectMultiScale(cropped_img,manis_rect,sF,mN2,0,new Size(),new Size());
					mJavaDetector3.detectMultiScale(cropped_img,model3_rect,sF,mN3,0,new Size(),new Size());
					mJavaDetector4.detectMultiScale(cropped_img,model4_rect,sF,mN4,0,new Size(),new Size());
					mJavaDetector5.detectMultiScale(cropped_img,model5_rect,sF,mN5,0,new Size(),new Size());
					mJavaDetector6.detectMultiScale(cropped_img,modele_emp_rect,sF,mN6,0,new Size(),new Size());

				}


				bubble_array=bubble_rect.toArray();
				Rect[] manis_array =manis_rect.toArray();




				Rect[] model3_array=model3_rect.toArray();
				Rect[] model4_array=model4_rect.toArray();
				Rect[] model5_array=model5_rect.toArray();
				Rect[] model_emp_array=modele_emp_rect.toArray();

				Mat b = new Mat();
				Point bubble_array_t1,bubble_array_t2;

					for (int k = 0; k < bubble_array.length; k++) {
						thickness=3;

						tl_x =bubble_array[k].tl().x+cropped_x;
						tl_y =bubble_array[k].tl().y+cropped_y;
						br_x =bubble_array[k].br().x+cropped_x;
						br_y =bubble_array[k].br().y+cropped_y;

						detectMessage="Fluid Entering";


//					Toast.makeText(getApplicationContext(),"x :"+bubble_array[0].tl().x+cropped_x+" y:"+bubble_array[0].tl().y+cropped_y,Toast.LENGTH_SHORT);


//						mhandler.postDelayed(delayStop,3000);

//						mViewMode = VIEW_MODE_STOP;
//						movingStop();
						break;
					}


//				}
//
					for (int k = 0; k < manis_array.length; k++) {

						tl_x =manis_array[k].tl().x+cropped_x;
						tl_y =manis_array[k].tl().y+cropped_y;
						br_x =manis_array[k].br().x+cropped_x;
						br_y =manis_array[k].br().y+cropped_y;

						detectMessage="Fluid Filled";


						break;
					}



					for (int k = 0; k < model3_array.length; k++) {
//						frame_num=0;

						tl_x =model3_array[k].tl().x+cropped_x;
						tl_y =model3_array[k].tl().y+cropped_y;
						br_x =model3_array[k].br().x+cropped_x;
						br_y =model3_array[k].br().y+cropped_y;

						detectMessage="Insufficent Filling";




						break;

					}




				for (int k = 0; k < model4_array.length; k++) {


					tl_x =model4_array[k].tl().x+cropped_x;
					tl_y =model4_array[k].tl().y+cropped_y;
					br_x =model4_array[k].br().x+cropped_x;
					br_y =model4_array[k].br().y+cropped_y;

					detectMessage="Fluid Leaving";

					break;


				}



				// input frame has gray scale format
				//+3 , -3 : because of Roi Thickness
				for (int i = Roi_chamber.x+3; i < Roi_chamber.x + Roi_chamber.width-3; i++)
					for (int j = Roi_chamber.y+3; j < Roi_chamber.y + Roi_chamber.height-3; j++) {
						double[] rgbV = mRgba.get(j, i);
						avgR += rgbV[0];
						avgG += rgbV[1];
						avgB += rgbV[2];
					}
				avgR = avgR / (Roi_chamber.width * Roi_chamber.height);
				avgG = avgG / (Roi_chamber.width * Roi_chamber.height);
				avgB = avgB / (Roi_chamber.width * Roi_chamber.height);

				RGB_data_array[0]=avgR;
				RGB_data_array[1]=avgG;
				RGB_data_array[2]=avgB;



				Text_RGB=("R : "+RGB_data_array[0]+" G : "+RGB_data_array[1]+" B : "+RGB_data_array[2]);

				Text_RGB_2=("R : "+RGB_data_array[0]+" G : "+RGB_data_array[1]+" B : "+RGB_data_array[2]);

				hue_value = calculateHue.getH(RGB_data_array);
				hue_value_2f=Double.parseDouble(String.format("%.2f",hue_value));
				point_hue  = new Point (cropped_x-50,cropped_y-40);

				Text_hue= ("H :  "+hue_value_2f);
				Text_for_R+=Integer.toString(RGB_data_array[0])+" ";
				Text_for_G+=Integer.toString(RGB_data_array[1])+" ";
				Text_for_B+=Integer.toString(RGB_data_array[2])+" ";



				point_rgb = new Point (cropped_x-50,cropped_y-80);
//         Point point_rgb_2 = new Point (start_x-150,start_y+90);

				fontColor = new Scalar(0, 0, 255);
				Imgproc.putText(mRgba,Text_hue,point_hue,1,3,fontColor,2);

				Imgproc.putText(mRgba,Text_RGB,point_rgb,1,3,fontColor,2);

				drawRect();

				movingGo();
				Log.i("handlerMessage",sendMsg);

				break;

			case VIEW_MODE_INIT:
				// input frame has RBGA format
				mRgba = inputFrame.rgba();
				mGray=inputFrame.gray();
				drawRect() ;
				break;
			case VIEW_MODE_STOP:
				mRgba = inputFrame.rgba();
				drawRect() ;
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
		Scalar color = new Scalar(255, 255, 255);
		Scalar red=new Scalar(255,0,0);

		p.x=start_x;
		p.y=start_y;
		p_to.x=start_x+x_width;
		p_to.y=start_y+y_height;


		Log.i(TAG,String.format("%d %d",(int)writingx,(int)tl_x));

		if((int)writingx==(int)tl_x){
			writingx=tl_x;
			count++;
		}else{
			writingx=tl_x;
			count=0;
		}

			Log.i(TAG,Integer.toString(count));
			Log.i(TAG,Integer.toString(count));

		if(count<100) {
			Imgproc.rectangle(mRgba, new Point(tl_x, tl_y), new Point(br_x, br_y), new Scalar(255, 255, 255), 2);
			Imgproc.putText(mRgba, detectMessage, new Point(cropped_x + 300, cropped_y + 100), 2, 3, new Scalar(0, 0, 0), 3);
		}

		Imgproc.rectangle(mRgba,p,p_to,color,2);
		Imgproc.rectangle(mRgba,new Point(cropped_x,cropped_y),new Point(cropped_x+cropped_w,cropped_y+cropped_h),red,thickness);
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
//	private int checkColor(int avgR, int avgG, int avgB){
//		int mode =0;
//		int result = 0 ;
//
////		if(avgR>150 && avgR<180){
////			if(avgG<130){
////				if(avgB<100)
////					mode = 1;} //����jm
////			else if(avgG>150 && avgG<195){
////				if(avgB<100)
////					mode = 2;} //������
////		}
////		else if(avgR<50){
////			if(avgG>70 && avgG<150)
////				if(avgG>95 && avgG<160)
////					mode = 3; //�Ķ���
////		}
//
//		if(avgR<64 && avgG<64){
//			if(avgB<60)
//				mode = 3; //�Ķ���
//		}
//		switch(mode){
//			case 0 :
//				result = 0; //�ƹ��͵��ش�ȵɶ�
//				break;
//			case 1 :
//				result = 1; //���������
//				break;
//			case 2 :
//				result = 2; //�Ķ������
//				break;
//			case 3 :
//				result = 3; //�����
//				break;
//			case 4 :
//				result = 4; //���
//				break;
//		}
//		return result;
//	}


	public void DicideOperation(int a, int b, int c){
		if(a <30 && b<30 && c < 30){
		}
	}


	Handler mhandler = new Handler();

	int handler_count=0;
	Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			textView_for_th.setText(Integer.toString(RGB_data_array[0]));

			mTitle.setText(Integer.toString(frame_num));

			//To calculate hue value
//			Double test = new Double();
//			CalculateHue calculateHue = new CalculateHue();

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

	public void onClick0(View v){
		switch (v.getId()){
			case R.id.btn_up:
				start_y-=10;


				seekBarProgress=seekBar.getProgress();

				Log.i("start_up",Integer.toString(start_y));

				Log.i("start_z",Integer.toString(seekBarProgress));


		}
	}


	public void onClick1(View v){
		switch (v.getId()){

			case R.id.btn_down:
				start_y+=10;


		}
	}   public void onClick3(View v){
		switch (v.getId()){

			case R.id.btn_left:
				start_x-=10;
				Log.i("start_left",Integer.toString(start_x));
				Toast.makeText(getApplicationContext(),Integer.toString(minNeighbors),Toast.LENGTH_SHORT).show();

				if(minNeighbors>2) minNeighbors-=1;
		}
	}   public void onClick2(View v){
		switch (v.getId()){

			case R.id.btn_right:
				start_x+=10;
				if(minNeighbors<30)minNeighbors+=1;
				Toast.makeText(getApplicationContext(),Integer.toString(minNeighbors),Toast.LENGTH_SHORT).show();
				Log.i("start_right",Integer.toString(start_x));



		}
	}
	public int checking_r_changed =0;
	public int checking_g_changed=0;
	public int checking_b_changed=0;

	public int DetectColor(int[] rgbData) {
		//1 = red , 2= green, 3=yeollow

		int color = -1;
		int r = rgbData[0];
		int g = rgbData[1];
		int b = rgbData[2];



		return -1;
	}


	protected void onStart(){
		super.onStart();
		mhandler= new Handler();
//		mhandler.postDelayed(mRunnable,10);

	}

	protected void onStop(){
		super.onStop();
		mhandler.removeCallbacks(mRunnable);
	}

	Runnable delayStart = new Runnable() {
		@Override
		public void run() {
			StartBtn.performClick();
			frame_num=0;

			Toast.makeText(getApplicationContext(),
					"Fluid Flowing",
					Toast.LENGTH_SHORT).show();

		}
	};

	Runnable delayStop = new Runnable() {
		@Override
		public void run() {
			StopBtn.performClick();

			Toast.makeText(getApplicationContext(),
					"Detected",
					Toast.LENGTH_SHORT).show();
			delayState();

		}
	};


	public void delayState(){
		mhandler.postDelayed(delayStart,10000);
	}
	public void movingStop(){

		if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
			String con = new String("STOP" + "\n");
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if(!sendMsg.equals(con)){
				Log.i("con_message",con);

				sendMessage(con);
				mViewMode=VIEW_MODE_STOP;

				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				sendMsg = con;
				onStop();



			}

			delayState();

		}
	}


	public void movingGo(){

		if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
			String con = new String("GO" + "\n");
			try {
				Thread.sleep(10);
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

				mViewMode=VIEW_MODE_START;


			}


		}
	}



}
