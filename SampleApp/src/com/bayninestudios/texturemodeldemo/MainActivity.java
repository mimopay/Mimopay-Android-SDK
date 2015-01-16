package com.bayninestudios.texturemodeldemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.view.MotionEvent;
import android.os.Handler;
import android.os.Build;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

//import android.app.Activity;
//import android.content.Context;
import android.content.pm.ActivityInfo;
//import android.opengl.GLSurfaceView;
//import android.opengl.GLU;
//import android.os.Bundle;

import android.util.Log;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.CheckBox;
import android.graphics.Typeface;
import android.view.View.OnClickListener;
import android.view.View;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;

import android.widget.Toast;

import android.app.Dialog;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;

// java
import java.util.ArrayList;

// mimopay
import com.mimopay.Mimopay;
import com.mimopay.MimopayInterface;
import com.mimopay.merchant.Merchant;

import javax.crypto.Cipher;

public class MainActivity extends Activity {
	public void jprintf(String s) { Log.d("JimBas", "MainActivity: " + s); }

    private GLSurfaceView mGLView;

	private final int TOPUP = 1;
	private final int SMARTFREN = 2;
	private final int SEVELIN = 3;
	private final int ATM = 4;
	private final int BCA = 5;
	private final int BERSAMA = 6;
	private final int UPOINT = 7;
	private final int XL = 8;
	private final int XLAIRTIME = 9;
	private final int XLHRN = 10;
	private final int MPOINT = 11;
	private final int DPOINT = 12;
	private final int LASTRESULT = 13;
	private final int STAGGATE = 14;

	private final int holobluelight = 0xff33b5e5;
	private final int holobluedark = 0xff0099cc;
	private final int holobluebright = 0xff00ddff;

	private final int TOTALMENUBTNS = 14;
	private ImageButton mbtnPay = null;
	private View[] mbtnMenuBtns = null;
	private int[] mnMenuBtns = {
		R.id.shoplistbtntopup, R.id.shoplistbtntopupsmartfren, R.id.shoplistbtntopupsevelin,
		R.id.shoplistbtnupoint,
		R.id.shoplistbtnatm, R.id.shoplistbtnatmbca, R.id.shoplistbtnatmbersama,
		R.id.shoplistbtnxl, R.id.shoplistbtnxlairtime, R.id.shoplistbtnxlvoucher,
		R.id.shoplistbtnmpointairtime, R.id.shoplistbtndpointairtime,
		R.id.shoplistbtnlastresult, R.id.shoplistbtnstaggate
	};
	private int[] mnMenuBtnsInitId = {
		TOPUP, SMARTFREN, SEVELIN,
		UPOINT,
		ATM, BCA, BERSAMA,
		XL, XLAIRTIME, XLHRN,
		MPOINT, DPOINT,
		LASTRESULT, STAGGATE
	};
	private View mvShop = null;
	private boolean mbShopBtn = false;
	private boolean mbGateway = false;

	// Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler());
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		LayoutInflater inflater = getLayoutInflater();
		mvShop = getLayoutInflater().inflate(R.layout.shop, null);

		mbtnPay = (ImageButton) mvShop.findViewById(R.id.shoplistbtnpay);
		mbtnPay.setOnTouchListener(new OnTouchListener() { @Override public boolean onTouch(View v, MotionEvent event) {
			if(mBtnShopHandler != null) return true;
			mbShopBtn = !mbShopBtn;
			v.setPressed(mbShopBtn);
			View vv = (View) mvShop.findViewById(R.id.shoplistitems);
			if(vv != null) {
				vv.setVisibility(mbShopBtn ? View.VISIBLE : View.GONE);
				if(mbShopBtn) {
					Handler handler = new Handler();
					handler.postDelayed(new Runnable(){@Override public void run() {
						LinearLayout llshop = (LinearLayout) mvShop.findViewById(R.id.shoplayout);
						llshop.getLayoutParams().width = mbtnPay.getLayoutParams().width;
					}}, 1000);
				}
			}
			mBtnShopHandler = new Handler();
			mBtnShopHandler.postDelayed(mBtnShopRunnable, 500);
			return true;
		}});

		mGLView = new ClearGLSurfaceView(this);
		setContentView(mGLView);

		mbtnMenuBtns = new View[TOTALMENUBTNS];
		for(int i=0;i<TOTALMENUBTNS;i++) {
			mbtnMenuBtns[i] = mvShop.findViewById(mnMenuBtns[i]);
			final int fi = i;
			mbtnMenuBtns[i].setOnClickListener(new OnClickListener(){ public void onClick(View view) {
				if(mBtnChooseHandler != null) return;
				mnBtnChooseId = fi;
				mBtnChooseHandler = new Handler();
				mBtnChooseHandler.postDelayed(mBtnChooseRunnable, 500);
			}});
		}

		getWindow().addContentView(mvShop, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		//initMimopay();
    }

	private class TopExceptionHandler implements Thread.UncaughtExceptionHandler
	{
		private Thread.UncaughtExceptionHandler defaultUEH;
		TopExceptionHandler() {
			this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
		}
		public void uncaughtException(Thread t, Throwable e) {
			jprintf("AbnormalTermination. reason: " + e.toString());
			//defaultUEH.uncaughtException(t, e);
			System.exit(666);
		}
	}

	private Handler mBtnShopHandler = null;
	Runnable mBtnShopRunnable = new Runnable() { @Override public void run() { mBtnShopHandler = null; }};
	private Handler mBtnChooseHandler = null;
	private int mnBtnChooseId = 0;
	Runnable mBtnChooseRunnable = new Runnable() { @Override public void run() { 
		runOnUiThread(new Runnable() { public void run() {
			jprintf("togglebutton: " + Integer.toString(mnBtnChooseId));
			mbShopBtn = false;
			mbtnPay.setPressed(mbShopBtn);
			View v = (View) mvShop.findViewById(R.id.shoplistitems);
			if(v != null) {
				v.setVisibility(View.GONE);
			}
			mBtnChooseHandler = null;
			initMimopay(mnMenuBtnsInitId[mnBtnChooseId]);
		}});
	}};

    @Override
    protected void onPause() {
        super.onPause();
		if(mGLView != null) {
	        mGLView.onPause();
		}
    }

    @Override
    protected void onResume() {
        super.onResume();
		if(mGLView != null) {
	        mGLView.onResume();
		}
    }

	boolean mQuietMode = false;
	Mimopay mMimopay = null;
	MimopayInterface mMimopayInterface = new MimopayInterface()
	{
		public void onReturn(String info, ArrayList<String> params)
		{
			String s,toastmsg = "";
			jprintf("onReturn: " + info);
			if(params != null) {
				toastmsg += (info + "\n\n");
				int i,j = params.size();
				for(i=0;i<j;i++) {
					s = params.get(i);
					toastmsg += (s + "\n");
					jprintf(String.format("[%d] %s", i, s));
					
				}
				if(mQuietMode) {
					final String ftoastmsg = toastmsg;
					runOnUiThread(new Runnable() { public void run() {
						Toast.makeText(getApplicationContext(), ftoastmsg, Toast.LENGTH_LONG).show();
					}});
				}
			}
		}
	};

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Main codes of How to use Mimopay SDK.
	//
	// To have better understanding it is strongly recommend to refer to http://staging.mimopay.com/api documentation.
	// Before create Mimopay object, you need to fill parameters that is described in documentation. Values that is used
	// in this sample is our mimopay's internal test account.
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	void initMimopay(int paymentid)
	{
		String emailOrUserId = "1385479814";	// this parameter is your user's unique id or user's email. Normally your app/game should have unique ID for every user
		String merchantCode = "ID-0031";		// for this parameter, after registration to mimopay, your should received this from us
		String productName = "ID-0031-0001";	// this parameter is also your own territory. It will be pass back to your server, either successful or an error occurs
		String transactionId = "";				// this should be unique in every transaction. If you leave it empty, SDK will generate unique numbers based on unix timestamp
		String secretKeyStaging = null;
		String secretKeyGateway = null;

		// You may initiate secretKeyStaging and secretKeyGateway hard-coded in your app's source code, however if this is not appropriate, you may use our
		// encrypted secretKey to avoid it. Every registerred merchant should received two files from us, jar file and txt file. Txt file contains secretKey's encrypted-key,
		// while the jar file contains secretKey's encrypted-value. So all you need to do is select and copy the encrypted-key from txt file, paste into Merchant.get() parameter,
		// it will return your real secretKey.

		try {
			secretKeyStaging = Merchant.get(true, "zLdLLbLX7xi2E4zxcbGMPg==");
			secretKeyGateway = Merchant.get(false, "5aSkczdhkk4ukFsZEHykkA==");
		} catch(Exception e) { jprintf("e: " + e.toString()); }
		String currency = "IDR";
		
		if(secretKeyStaging == null || secretKeyGateway == null) {
			Toast.makeText(getApplicationContext(), "secretKey problem!", Toast.LENGTH_LONG).show();
			return;
		}

		mMimopay = new Mimopay(getApplicationContext(), emailOrUserId,
			merchantCode, productName, transactionId, secretKeyStaging, secretKeyGateway, currency, mMimopayInterface);

		// enableLog is Mimopay SDK's internal log print. If set to enable, all logs will printed out in your app's log. This is very usefull in your development phase
		mMimopay.enableLog(true);

		// By default, the payment process will goes to staging.mimopay.com. Keep it commented out while you are still in development phase, 
		// when you are ready to production you can un-comment it, so SDK will goes to gateway.mimopay.com
		//
		mMimopay.enableGateway(mbGateway);

		AlertDialog.Builder altbld = null;
		AlertDialog alert = null;
		
		mQuietMode = false;

		// As stated in Mobile SDK documentation, it support two modes, UI and Quiet mode. UI mode methods have no
		// parameter(s) in it while Quiet mode methods have.

        switch (paymentid)
		{
        case TOPUP:
			//
			// this will launch UI mode top up activity. One or more payment channels will be shown, may not be the same every merchants
			//
			mMimopay.executeTopup();
			break;
        case SMARTFREN:	// smartfren
			altbld = new AlertDialog.Builder(MainActivity.this);
			altbld.setMessage("Mimopay's SDK supports both, Default UI and Quiet Mode. " +
				"In Quiet Mode, the voucher number is currently set to 1234567890123456. " +
				"You may change it later in this sample source code.\n" +
				"Now, please choose which one.")
			.setCancelable(true)
			.setPositiveButton("UI", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
				//
				// this will launch UI mode top up activity and straight to show Smartfren channel.
				//
				mMimopay.executeTopup("smartfren");
			}})
			.setNegativeButton("Quiet", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
				mQuietMode = true;
				//
				// Running Smartfren top up quietly (quiet mode). The running background task will straight call for Smartfren channel,
				// and do the top up with the number that passed in the second parameter. No UI will pops up.
				// You will notified the result via MimopayInterface.onReturn, check its 'info' string status
				//
				mMimopay.executeTopup("smartfren", "9861529055077264");
			}});
			alert = altbld.create();
			alert.setTitle("Smartfren Topup");
			alert.setIcon(android.R.drawable.stat_notify_error);
			alert.show();
			break;
        case SEVELIN: // sevelin
			altbld = new AlertDialog.Builder(MainActivity.this);
			altbld.setMessage("Mimopay's SDK supports both, Default UI and Quiet Mode. " +
				"In Quiet Mode, the voucher number is currently set to 1234567890123456. " +
				"You may change it later in this sample source code.\n" +
				"Now, please choose which one.")
			.setCancelable(true)
			.setPositiveButton("UI", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
				//
				// UI mode for sevelin, straight to show Sevelin channel
				// 
				mMimopay.executeTopup("sevelin");
			}})
			.setNegativeButton("Quiet", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
				mQuietMode = true;
				//
				// Running Sevelin top up quietly. You will notified the result via MimopayInterface.onReturn
				// check its 'info' string status
				// 
				mMimopay.executeTopup("sevelin", "1234567890123456");
			}});
			alert = altbld.create();
			alert.setTitle("Sevelin Topup");
			alert.setIcon(android.R.drawable.stat_notify_error);
			alert.show();
			break;
        case ATM: // ATM
			mMimopay.executeATMs();
			break;
        case BCA: // ATM BCA
			altbld = new AlertDialog.Builder(MainActivity.this);
			altbld.setMessage("Mimopay's SDK supports both, Default UI and Quiet Mode. " +
				"In Quiet Mode, the value of mimocard is currently set to 50K. " +
				"You may change it later in this sample source code.\n" +
				"Now, please choose which one.")
			.setCancelable(true)
			.setPositiveButton("UI", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
				mMimopay.executeATMs("atm_bca");
			}})
			.setNegativeButton("Quiet", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
				mQuietMode = true;
				mMimopay.executeATMs("atm_bca", "50000");
			}});
			alert = altbld.create();
			alert.setTitle("ATM BCA");
			alert.setIcon(android.R.drawable.stat_notify_error);
			alert.show();
			break;
        case BERSAMA: // ATM Bersama
			altbld = new AlertDialog.Builder(MainActivity.this);
			altbld.setMessage("Mimopay's SDK supports both, Default UI and Quiet Mode. " +
				"In Quiet Mode, the value of mimocard is currently set to 50K. " +
				"You may change it later in this sample source code.\n" +
				"Now, please choose which one.")
			.setCancelable(true)
			.setPositiveButton("UI", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
				mMimopay.executeATMs("atm_bersama");
			}})
			.setNegativeButton("Quiet", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
				mQuietMode = true;
				mMimopay.executeATMs("atm_bersama", "50000");
			}});
			alert = altbld.create();
			alert.setTitle("ATM Bersama");
			alert.setIcon(android.R.drawable.stat_notify_error);
			alert.show();
			break;
        case UPOINT: // upoint
			altbld = new AlertDialog.Builder(MainActivity.this);
			altbld.setMessage("Mimopay's SDK supports both, Default UI and Quiet Mode. " +
				"In Quiet Mode, the UPoint credits is currently set to 1000 and phone number is 081219106541. " +
				"You may change them later in this sample source code.\n" +
				"Now, please choose which one.")
			.setCancelable(true)
			.setPositiveButton("UI", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
				mMimopay.executeUPointAirtime();
			}})
			.setNegativeButton("Quiet", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
				mQuietMode = true;
				mMimopay.executeUPointAirtime("1000", "081219106541", false);
				//mMimopay.executeUPointAirtime("25000");
			}});
			alert = altbld.create();
			alert.setTitle("UPoint Airtime");
			alert.setIcon(android.R.drawable.stat_notify_error);
			alert.show();
			break;
        case XL: // XL
			mMimopay.executeXL();
			break;
        case XLAIRTIME: // XL Airtime
			altbld = new AlertDialog.Builder(MainActivity.this);
			altbld.setMessage("Mimopay's SDK supports both, Default UI and Quiet Mode. " +
				"In Quiet Mode, the XL Airtime credits is currently set to 10000 and phone number is 087771270843. " +
				"You may change them later in this sample source code.\n" +
				"Now, please choose which one.")
			.setCancelable(true)
			.setPositiveButton("UI", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
				mMimopay.executeXLAirtime();
			}})
			.setNegativeButton("Quiet", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
				mQuietMode = true;
				mMimopay.executeXLAirtime("10000", "087771270843", false);
				//mMimopay.executeXLAirtime("20000");
				//Toast.makeText(getApplicationContext(), "not yet implemented", Toast.LENGTH_LONG).show();
			}});
			alert = altbld.create();
			alert.setTitle("XL Airtime");
			alert.setIcon(android.R.drawable.stat_notify_error);
			alert.show();
			break;
        case XLHRN: // XL HRN
			altbld = new AlertDialog.Builder(MainActivity.this);
			altbld.setMessage("Mimopay's SDK supports both, Default UI and Quiet Mode. " +
				"In Quiet Mode, the voucher number (HRN) is currently set to 1234567890123456. " +
				"You may change it later in this sample source code.\n" +
				"Now, please choose which one.")
			.setCancelable(true)
			.setPositiveButton("UI", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
				mMimopay.executeXLHrn();
			}})
			.setNegativeButton("Quiet", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
				mQuietMode = true;
				mMimopay.executeXLHrn("1234567890123456");
			}});
			alert = altbld.create();
			alert.setTitle("XL HRN");
			alert.setIcon(android.R.drawable.stat_notify_error);
			alert.show();
			break;
        case MPOINT: // mpoint
			altbld = new AlertDialog.Builder(MainActivity.this);
			altbld.setMessage("Mimopay's SDK supports both, Default UI and Quiet Mode. " +
				"In Quiet Mode, the MPoint Airtime credits is currently set to 2 and phone number is 0175629621. " +
				"You may change them later in this sample source code.\n" +
				"Now, please choose which one.")
			.setCancelable(true)
			.setPositiveButton("UI", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
				AlertDialog.Builder altbld = new AlertDialog.Builder(MainActivity.this);
				altbld.setMessage("Choose your language ?\n(Dutch language here just an example)")
				.setCancelable(true)
				.setPositiveButton("English", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
					mMimopay.executeMPointAirtime();
				}})
				.setNegativeButton("Dutch", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
					// Please note that payment methods those are in Bahasa by default, cannot be customized.
					// Since MPoint is in English by default, you can do as below if you want to customized to other language.
					// Please refer to CustomLang.java, it shows how all words should be managed.
					jprintf(String.format("CustomLang.mDutch:%d", CustomLang.mDutch.length));
					mMimopay.setUiLanguage(CustomLang.mDutch);
					mMimopay.executeMPointAirtime();
				}});
				AlertDialog aldlg = altbld.create();
				aldlg.setTitle("Language");
				aldlg.setIcon(android.R.drawable.stat_notify_error);
				aldlg.show();
			}})
			.setNegativeButton("Quiet", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
				mQuietMode = true;
				mMimopay.executeMPointAirtime("2", "0175629621", false);
				//Toast.makeText(getApplicationContext(), "Quiet mode disabled for temporary", Toast.LENGTH_LONG).show();
			}});
			alert = altbld.create();
			alert.setTitle("MPoint Airtime");
			alert.setIcon(android.R.drawable.stat_notify_error);
			alert.show();
			break;
        case DPOINT: // dpoint
			altbld = new AlertDialog.Builder(MainActivity.this);
			altbld.setMessage("Mimopay's SDK supports both, Default UI and Quiet Mode. " +
				"In Quiet Mode, the DPoint Airtime credits is currently set to 200 and phone number is 0169041289. " +
				"You may change them later in this sample source code.\n" +
				"Now, please choose which one.")
			.setCancelable(true)
			.setPositiveButton("UI", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
				AlertDialog.Builder altbld = new AlertDialog.Builder(MainActivity.this);
				altbld.setMessage("Choose your language ?\n(Dutch language here just an example)")
				.setCancelable(true)
				.setPositiveButton("English", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
					mMimopay.executeDPointAirtime();
				}})
				.setNegativeButton("Dutch", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
					// Please note that payment methods those are in Bahasa by default, cannot be customized.
					// Since DPoint is in English by default, you can do as below if you want to customized to other language.
					// Please refer to CustomLang.java, it shows how all words should be managed.
					mMimopay.setUiLanguage(CustomLang.mDutch);
					mMimopay.executeDPointAirtime();
				}});
				AlertDialog aldlg = altbld.create();
				aldlg.setTitle("Language");
				aldlg.setIcon(android.R.drawable.stat_notify_error);
				aldlg.show();
			}})
			.setNegativeButton("Quiet", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
				AlertDialog.Builder altbld = new AlertDialog.Builder(MainActivity.this);
				altbld.setMessage("Choose DPoint Payment ?")
				.setCancelable(true)
				.setPositiveButton("New Transaction", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
					mQuietMode = true;
					mMimopay.executeDPointAirtime("0", "0169041289", false);
				}})
				.setNegativeButton("Complete Last Payment", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
					if(!mMimopay.isDPointPaymentIncomplete()) {
						Toast.makeText(getApplicationContext(), "The last transaction was not DPoint payment method", Toast.LENGTH_LONG).show();
					} else {
						final Dialog smsdlg = new Dialog(MainActivity.this);
						smsdlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
						smsdlg.setContentView(R.layout.digismscode);
						Button smsdlgButton = (Button) smsdlg.findViewById(R.id.smscodeTopup);
						smsdlgButton.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) {
							mQuietMode = true;
							EditText edit = (EditText) smsdlg.findViewById(R.id.smscodeEditText);
							mMimopay.completeDPointPayment(edit.getText().toString());
							smsdlg.dismiss();
						}});
						smsdlg.show();
					}
				}});
				AlertDialog aldlg = altbld.create();
				aldlg.setTitle("Language");
				aldlg.setIcon(android.R.drawable.stat_notify_error);
				aldlg.show();
			}});
			alert = altbld.create();
			alert.setTitle("DPoint Airtime");
			alert.setIcon(android.R.drawable.stat_notify_error);
			alert.show();
			break;
		case LASTRESULT:
			String s = "";
			int ires = 0;
			String [] sarr = mMimopay.getLastResult();
			if(sarr != null) {
				ires = sarr.length;
				for(int i=0;i<ires;i++) {
					s += (sarr[i] + "\n");
				}
			}
			//String toastmsg = String.format("ires=%d\ns=%s", ires, s);
			Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
			break;
		case STAGGATE:
			mbGateway = !mbGateway;
			Button btn = (Button) mbtnMenuBtns[STAGGATE-1];
			btn.setText(mbGateway ? "Switch to Staging" : "Switch to Gateway");
			Toast.makeText(getApplicationContext(), "SDK will points to " + (mbGateway ? "Gateway" : "Staging") + " on next transaction", Toast.LENGTH_LONG).show();
			break;
        }
	}
}

class ClearGLSurfaceView extends GLSurfaceView
{
    ClearRenderer mRenderer;
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private final float TRACKBALL_SCALE_FACTOR = 36.0f;
    private float mPreviousX;
    private float mPreviousY;
	private Handler mAfterTouchHandler = null;

    public ClearGLSurfaceView(Context context) {
        super(context);
        mRenderer = new ClearRenderer(context, this);
        setRenderer(mRenderer);
    }

    @Override public boolean onTrackballEvent(MotionEvent e) {
		afterTouch();
        mRenderer.mAngleX += e.getX() * TRACKBALL_SCALE_FACTOR;
        mRenderer.mAngleY += e.getY() * TRACKBALL_SCALE_FACTOR;
        requestRender();
        return true;
    }

    @Override public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        switch (e.getAction()) {
        case MotionEvent.ACTION_MOVE:
			afterTouch();
            float dx = x - mPreviousX;
            float dy = y - mPreviousY;
            mRenderer.mAngleX += dx * TOUCH_SCALE_FACTOR;
            mRenderer.mAngleY += dy * TOUCH_SCALE_FACTOR;
            requestRender();
        }
        mPreviousX = x;
        mPreviousY = y;
        return true;
    }

	private void afterTouch()
	{
		mRenderer.autoRotate = false;
		if(mAfterTouchHandler == null) mAfterTouchHandler = new Handler();
		mAfterTouchHandler.postDelayed(mStageRunnable, 1000);
	}

	Runnable mStageRunnable = new Runnable() { @Override public void run()
	{
		mRenderer.autoRotate = true;
		mAfterTouchHandler = null;
	}};
}

class ClearRenderer implements GLSurfaceView.Renderer {
    private ClearGLSurfaceView view;
    private Context context;
    private DrawModel model;
    //private float angleY = 0f;

    public float mAngleX = 0f;
    public float mAngleY = 0f;
	public boolean autoRotate = true;

    private int[] mTexture = new int[1];

    public ClearRenderer(Context context, ClearGLSurfaceView view) {
        this.view = view;
        this.context = context;
        model = new DrawModel(context, R.raw.rock);
    }

    private void loadTexture(GL10 gl, Context mContext, int mTex) {
        gl.glGenTextures(1, mTexture, 0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture[0]);
        Bitmap bitmap;
        bitmap = BitmapFactory.decodeResource(mContext.getResources(), mTex);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 25.0f, (view.getWidth() * 1f) / view.getHeight(), 1, 100);
        GLU.gluLookAt(gl, 0f, 0f, 12f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glEnable(GL10.GL_TEXTURE_2D);

        loadTexture(gl, context, R.drawable.rock);

        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
    }

    public void onSurfaceChanged(GL10 gl, int w, int h) {
        gl.glViewport(0, 0, w, h);
    }

    public void onDrawFrame(GL10 gl) {
        //gl.glClearColor(0f, 0f, .7f, 1.0f);
        gl.glClearColor(0f, 0f, 0f, 1.0f);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glPushMatrix();
        //gl.glRotatef(angleY, 0f, 1f, 0f);
        gl.glRotatef(mAngleX, 0, 1, 0);
        gl.glRotatef(mAngleY, 1, 0, 0);
        model.draw(gl);
        gl.glPopMatrix();
       	//angleY += 1f;
		if(autoRotate)
        	mAngleX += 1f;
    }
}
