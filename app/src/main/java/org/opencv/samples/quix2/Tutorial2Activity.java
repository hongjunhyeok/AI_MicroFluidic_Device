package org.opencv.samples.quix2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.samples.quix2.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

public class Tutorial2Activity extends Activity implements
		CvCameraViewListener2 {

	private int TEST=0;
	private static final String TAG = "OCVSample::Activity";

	private static final int VIEW_MODE_RGBA = 0;
	private static final int VIEW_MODE_START = 1;
	private static final int VIEW_MODE_RESULT = 2;
	private static final int VIEW_MODE_STOP = 3;
	private static final int VIEW_MODE_INIT = 4;

	private int mViewMode;
	private Mat mRgba;
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
	Button ResultBtn;
	CheckBox flashCheckbox;

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
		ResultBtn = (Button) findViewById(R.id.button6);


		mOpenCvCameraView = (Tutorial3View) findViewById(R.id.tutorial2_activity_surface_view);
		mOpenCvCameraView.setCvCameraViewListener(this);
		mOpenCvCameraView.setEnabled(true);
		mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);

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

	public void onDestroy() {
		super.onDestroy();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
		if (mChatService != null)
			mChatService.stop();
	}

	public void onCameraViewStarted(int width, int height) {
		mRgba = new Mat(height, width, CvType.CV_8UC4);
		mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
		mGray = new Mat(height, width, CvType.CV_8UC1);
	}

	public void onCameraViewStopped() {
		mRgba.release();
		mGray.release();
		mIntermediateMat.release();
	}

	public Mat onCameraFrame(final CvCameraViewFrame inputFrame) {



		mhandler.postDelayed(mRunnable,10);

		InitBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent serverIntent = new Intent(getApplicationContext(), DeviceListActivity.class);
				startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
				if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
					// Start the Bluetooth chat services
					mChatService.start();
				}
				mViewMode = VIEW_MODE_INIT;

				if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
					String con = new String("on" + "\n");
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
		ResultBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mViewMode = VIEW_MODE_RESULT;

				mText.setTextSize(20);
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
			
			Rect fRoi = new Rect(1095, 770, 2, 25);
			Rect bRoi = new Rect(1143, 770, 2, 25);
			
			int avgR = 0, avgG = 0, avgB = 0;
			int avgR2 = 0, avgG2 = 0, avgB2 = 0;

			for (int i = fRoi.x; i < fRoi.x + fRoi.width; i++)
				for (int j = fRoi.y; j < fRoi.y + fRoi.height; j++) {
					double[] rgbV = mRgba.get(j, i);
					avgR += rgbV[0];
					avgG += rgbV[1];
					avgB += rgbV[2];
				}
			avgR = avgR / (fRoi.width * fRoi.height);
			avgG = avgG / (fRoi.width * fRoi.height);
			avgB = avgB / (fRoi.width * fRoi.height);
			
			

			for (int i = bRoi.x; i < bRoi.x + bRoi.width; i++)
				for (int j = bRoi.y; j < bRoi.y + bRoi.height; j++) {
					double[] rgbV = mRgba.get(j, i);
					avgR2 += rgbV[0];
					avgG2 += rgbV[1];
					avgB2 += rgbV[2];
				}
			avgR2 = avgR2 / (bRoi.width * bRoi.height);
			avgG2 = avgG2 / (bRoi.width * bRoi.height);
			avgB2 = avgB2 / (bRoi.width * bRoi.height);
			
			String fText = new String("F RGB(" + avgR + "," + avgG + ", " + avgB + ")");
			String bText = new String("B RGB(" + avgR2 + "," + avgG2 + ", " + avgB2 + ")");


			if(avgR2>100){
				TEST=200;
			}else{
				TEST=0;
			}

			mhandler.postDelayed(mRunnable,0);


			Point tp1 = new Point(300, 450);
			Point tp2 = new Point(300, 550);
			Scalar fontColor = new Scalar(0, 0, 255);
			Imgproc.putText(mRgba, fText, tp1, 1, 3, fontColor, 2);
			Imgproc.putText(mRgba, bText, tp2, 1, 3, fontColor, 2);
			
			drawRect();
			
			int fResult = checkColor(avgR, avgG, avgB) ;
			int bResult = checkColor(avgR2, avgG2, avgB2) ;
			String resultValue = null;
			if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
				if(fResult == bResult){
					resultValue = new String(bResult + "\n");
					if(!sendMsg.equals(resultValue)){
						//sendMessage(resultValue);
						sendMsg = resultValue;
						Log.i("sendMsg",resultValue);
					}
				}
			}



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
		case VIEW_MODE_RESULT:

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
		Scalar color = new Scalar(0, 0, 255);
		
		Rect fRoi2 = new Rect(1095, 770, 2, 25);
		Point fp12 = new Point(fRoi2.x, fRoi2.y);
		Point fp22 = new Point(fRoi2.x + fRoi2.width, fRoi2.y + fRoi2.height);
		
		Rect bRoi2 = new Rect(1143, 770, 2, 25);
		Point bp12 = new Point(bRoi2.x, bRoi2.y);
		Point bp22 = new Point(bRoi2.x + bRoi2.width, bRoi2.y + bRoi2.height);
		
		Imgproc.rectangle(mRgba, fp12, fp22, color, 2);
		Imgproc.rectangle(mRgba, bp12, bp22, color, 2);
	}

	private final Handler mHandler = new Handler() {
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
	Runnable mRunnable = new Runnable() {
		@Override
		public void run() {

			if(TEST>0) {
				if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
					String con = new String("on" + "\n");
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (!sendMsg.equals(con)) {
						sendMessage(con);
						sendMsg = con;
					}

					mText.setText(sendMsg);

				}
			}else {

				if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
					String con = new String("off" + "\n");
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (!sendMsg.equals(con)) {
						sendMessage(con);
						sendMsg = con;
					}
					mText.setText(con);
				}
			}
		}
	};

	public native void FindFeatures(long matAddrGr, long matAddrRgba);
//	public native void Labling(long matAddrGr, long matAddrRgba);
}
