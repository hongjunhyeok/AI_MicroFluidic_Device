package org.opencv.samples.quix2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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

public class Tutorial2Activity extends Activity implements
		CvCameraViewListener2,View.OnTouchListener {

	Zoomcameraview zoomcameraview;

	private int RGB_data_array[]=new int[3];
	public boolean should_motor_on =false;
	public boolean FLAG_GREEN=false;
	public boolean FLAG_RED=false;
	public boolean FLAG_YELLOW=false;



	private static final String TAG = "OCVSample::Activity";

	private static final int VIEW_MODE_RGBA = 0;
	private static final int VIEW_MODE_START = 1;
	private static final int VIEW_MODE_RESULT = 2;
	private static final int VIEW_MODE_STOP = 3;
	private static final int VIEW_MODE_INIT = 4;
	private static final int VIEW_MODE_CHECK =5;

	private static final int COLOR_RED = 0;
	private static final int COLOR_GREEN=1;
	private  static  final  int COLOR_YELLOW=2;

	public int seekBarProgress;
	public int start_x=1360;
	public int start_y=520;
	public int x_width=15;
	public int y_height=15;

	private int mViewMode;
	private Mat mRgba,mask;
	private Mat mIntermediateMat;
	private Mat mGray;
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

	Button InitBtn;
	Button StartBtn;
	Button StopBtn;
	Button ReturnBtn;
	CheckBox flashCheckbox;
	SeekBar seekBar;


	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.i(TAG, "OpenCV loaded successfully");

				// Load native library after(!) OpenCV initialization
				System.loadLibrary("native-lib");

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
		seekBarProgress=seekBar.getProgress();


		mOpenCvCameraView = (Tutorial3View) findViewById(R.id.tutorial2_activity_surface_view);
		mOpenCvCameraView.setCvCameraViewListener(this);
		mOpenCvCameraView.setEnabled(true);
		mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
		mOpenCvCameraView.setZoomControl((SeekBar) findViewById(R.id.CameraZoomControls));



		mTitle = (TextView) findViewById(R.id.textView1);
		mText = (TextView) findViewById(R.id.textView2);


		File file;

		path = Environment.getExternalStorageDirectory()
				+ "/android/data/org.opencv.samples.tutorial2/result";
		file = new File(path);
		Log.i("file",path);
		if (!file.exists())
			file.mkdirs();
		file = new File(path + "/result.txt");
		try {
			fos = new FileOutputStream(file);
			fos.close();
		} catch (IOException e) {
		}

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

				}else{
					mOpenCvCameraView.setEffect(Camera.Parameters.FLASH_MODE_ON);
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
//		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this,
//				mLoaderCallback);

		if (!OpenCVLoader.initDebug()) {
			Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
			OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
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

		if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
			String con = new String("RETURN" + "\n");
			Log.i("con_message_bye",con);
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
	public Mat onCameraFrame(final CvCameraViewFrame inputFrame) {


//		count++;
//		if(count<5){
//			return null;
//		}
//		count =0;





		InitBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
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
					String con = new String("goback" + "\n");
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
		StartBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("con_message",Boolean.toString(FLAG_GREEN));
				// TODO Auto-generated method stub
				mViewMode = VIEW_MODE_START;

			}
		});
		StopBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mViewMode = VIEW_MODE_STOP;


				if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
					String con = new String("off" + "\n");
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
			}
		});
		ReturnBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FLAG_GREEN=false;
				FLAG_RED=false;
				FLAG_YELLOW=false;


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


		switch (viewMode) {
		case VIEW_MODE_RGBA:
			mRgba = inputFrame.rgba();
			drawRect();


			break;
		case VIEW_MODE_START:
			// input frame has gray scale format
			mRgba = inputFrame.rgba();
			Rect Roi_chamber = new Rect(start_x,start_y,x_width,y_height);
			int avgR = 0, avgG = 0, avgB = 0;
			int avgR2 = 0, avgG2 = 0, avgB2 = 0;
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

			double hue_value=0.0;
			RGB_data_array[0]=avgR;
			RGB_data_array[1]=avgG;
			RGB_data_array[2]=avgB;

			CalculateHue calculateHue= new CalculateHue();


			hue_value = calculateHue.getH(RGB_data_array);
			double hue_value_2f=Double.parseDouble(String.format("%.2f",hue_value));


			String Text_hue= ("H :  "+hue_value_2f);
			String Text_RGB=("R : "+RGB_data_array[0]+" G : "+RGB_data_array[1]+" B : "+RGB_data_array[2]);




			Point point_hue  = new Point (start_x-150,start_y-50);
			Point point_rgb = new Point (start_x-150,start_y-90);
			Scalar fontColor = new Scalar(0, 0, 255);
			Imgproc.putText(mRgba,Text_hue,point_hue,1,3,fontColor,2);
			Imgproc.putText(mRgba,Text_RGB,point_rgb,1,3,fontColor,2);

			drawRect();

			//핸들러로 블루투스 통신 시작.
			mhandler.postDelayed(mRunnable,100);

			Log.i("handlerMessage",sendMsg);



			break;

		case VIEW_MODE_INIT:

			// input frame has RBGA format
			mRgba = inputFrame.rgba();
			drawRect() ;
			break;
		case VIEW_MODE_STOP:

				// input frame has RBGA format
				mRgba = inputFrame.rgba();
				drawRect() ;
				break;
		case VIEW_MODE_CHECK:

			// input frame has gray scale format

			mRgba = inputFrame.rgba();
			drawRect() ;
			break;
		/*
		 * case VIEW_MODE_FEATURES: // input frame has RGBA format mRgba =
		 * inputFrame.rgba(); mGray = inputFrame.gray();
		 * Labling(mGray.getNativeObjAddr(), mRgba.getNativeObjAddr()); break;
		 */
		}

		return mRgba;
	}

	private void drawRect() {
		// TODO Auto-generated method stub
		Scalar color = new Scalar(255, 255, 255);

		Point p=new Point (start_x,start_y);
		Point p_to=new Point(start_x+x_width,start_y+y_height);

		Imgproc.rectangle(mRgba,p,p_to,color,2);
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
					mTitle.append(mConnectedDeviceName);
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
//				mConversationArrayAdapter.add(mConnectedDeviceName + ":  "
//						+ readMessage);
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
	private int checkColor(int avgR, int avgG, int avgB){
		int mode =0;
		int result = 0 ;

		if(avgR>150 && avgR<180){
			if(avgG<130){
				if(avgB<100)
					mode = 1;} //����jm
			else if(avgG>150 && avgG<195){
				if(avgB<100)
					mode = 2;} //������
		}
		else if(avgR<50){
			if(avgG>70 && avgG<150)
				if(avgG>95 && avgG<160)
					mode = 3; //�Ķ���
		}
		switch(mode){
		case 0 :
			result = 0; //�ƹ��͵��ش�ȵɶ�
			break;
		case 1 :
			result = 1; //���������
			break;
		case 2 :
			result = 2; //�Ķ������
			break;
		case 3 :
			result = 3; //�����
			break;
		case 4 :
			result = 4; //���
			break;
		}
			return result;
	}


	public void DicideOperation(int a, int b, int c){
		if(a <30 && b<30 && c < 30){


		}
	}


	Handler mhandler = new Handler();

	int handler_count=0;
	Runnable mRunnable = new Runnable() {
		@Override
		public void run() {

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



			if(DetectColor(RGB_data_array)==COLOR_YELLOW&&!FLAG_YELLOW ){
				if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
					String con = new String("STOP" + "\n");
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}if(!sendMsg.equals(con)){
						Log.i("con_message",con);

						sendMessage(con);
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						sendMsg = con;
						FLAG_YELLOW=true;
						FLAG_GREEN=false;
						FLAG_RED=false;

						onStop();

						mViewMode = VIEW_MODE_INIT;

						Toast.makeText(getApplicationContext(),
								"Detection : Y.  [R, G, B] = ["+RGB_data_array[0]+", "+RGB_data_array[1]+", "+RGB_data_array[2]+"]",
								Toast.LENGTH_SHORT).show();
					}
					mText.setText("Detection: Y ");

				}

			} else if (DetectColor(RGB_data_array)==COLOR_RED&&!FLAG_RED) {

				if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
					String con = new String("STOP" + "\n");
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}if(!sendMsg.equals(con)){
						Log.i("con_message",con);

						sendMessage(con);
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						sendMsg = con;
						FLAG_RED=true;
						FLAG_GREEN=false;
						FLAG_YELLOW=false;
						onStop();
						mViewMode = VIEW_MODE_INIT;

						Toast.makeText(getApplicationContext(),
								"Detection : R.  [R, G, B] = ["+RGB_data_array[0]+", "+RGB_data_array[1]+", "+RGB_data_array[2]+"]",
								Toast.LENGTH_SHORT).show();
					}
					mText.setText("Detection: R ");

				}
			}else if (DetectColor(RGB_data_array)==COLOR_GREEN&&!FLAG_GREEN) {

				if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
					String con = new String("STOP" + "\n");
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}if(!sendMsg.equals(con)){
						Log.i("con_message",con);

						sendMessage(con);
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						sendMsg = con;
						FLAG_GREEN=true;
						FLAG_YELLOW=false;
						FLAG_RED=false;
						onStop();
						mViewMode = VIEW_MODE_INIT;
						Toast.makeText(getApplicationContext(),
								"Detection : G.  [R, G, B] = ["+RGB_data_array[0]+", "+RGB_data_array[1]+", "+RGB_data_array[2]+"]",
								Toast.LENGTH_SHORT).show();

					}
					mText.setText("Detection: G ");

				}
			}
		}

	};

	public void onClick0(View v){
		switch (v.getId()){
			case R.id.btn_up:
				start_y-=20;


				seekBarProgress=seekBar.getProgress();

				Log.i("start_up",Integer.toString(start_y));

				Log.i("start_z",Integer.toString(seekBarProgress));


		}
	}




	public void onClick1(View v){
		switch (v.getId()){

			case R.id.btn_down:
				start_y+=20;

				Log.i("start_down",Integer.toString(start_y));


		}
	}	public void onClick3(View v){
		switch (v.getId()){

			case R.id.btn_left:
				start_x-=20;
				Log.i("start_left",Integer.toString(start_x));

		}
	}	public void onClick2(View v){
		switch (v.getId()){

			case R.id.btn_right:
				start_x+=20;

				Log.i("start_right",Integer.toString(start_x));



		}
	}
	public int checking_r_changed =0;
	public int checking_g_changed=0;
	public int checking_b_changed=0;

	public int DetectColor(int[] rgbData){
		//1 = red , 2= green, 3=yeollow

		int color=-1;
		int r=rgbData[0];
		int g=rgbData[1];
		int b=rgbData[2];


		//현재는 고정값이지만 후에는 상대값으로 바꿀것.
//		if(Math.max(r,Math.max(g,b))==r && b <33 && g< 33 && r>33 ){
//			return COLOR_RED;
//		}
//
//		if(Math.max(r,Math.max(g,b))==g &&r<33 && b<33 && g>33 ){
//			return COLOR_GREEN;
//		}
//		if(r>33 && g>33 && b<10 ){
//			return COLOR_YELLOW;
//		}
		if(Math.max(r,Math.max(g,b))==r && b <33 && g< 33 && r>33 ){
			return COLOR_RED;
		}

		if(Math.max(r,Math.max(g,b))==g &&r<33 && b<33 && g>33 ){
			return COLOR_GREEN;
		}
		if(r>33 && g>33 && b<r*0.2 ){
			return COLOR_YELLOW;
		}

		return -1;
	}


	protected void onStart(){
		super.onStart();
		mhandler= new Handler();
		mhandler.postDelayed(mRunnable,10);
	}

	protected void onStop(){
		super.onStop();
		mhandler.removeCallbacks(mRunnable);
	}


	public native void FindFeatures(long matAddrGr, long matAddrRgba);
//	public native void Labling(long matAddrGr, long matAddrRgba);



}

