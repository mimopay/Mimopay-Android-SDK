package com.bayninestudios.texturemodeldemo;

// android
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
import android.util.Log;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageButton;
import android.view.View;
import android.widget.Toast;
import android.app.Dialog;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.ViewGroup;

// java
import java.util.ArrayList;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

// mimopay
import com.mimopay.Mimopay;
import com.mimopay.MimopayInterface;
import com.mimopay.merchant.Merchant;

public class MainActivity extends Activity
{
	public void jprintf(String s) { Log.d("JimBas", "MainActivity: " + s); }

    private GLSurfaceView mGLView;

	private final int SMARTFREN = 1;
	private final int SEVELIN = 2;
	private final int BCA = 3;
	private final int BERSAMA = 4;
	private final int UPOINTHRN = 5;
	private final int UPOINT = 6;
	private final int XLAIRTIME = 7;
	private final int XLHRN = 8;
	private final int INDOSAT = 9;
	private final int MPOINT = 10;
	private final int DPOINT = 11;
	private final int CELCOM = 12;
	private final int VNTELCO = 13;
	private final int LASTRESULT = 14;
	private final int STAGGATE = 15;

	private final int TOTALMENUBTNS = 15;
	private ImageButton mbtnPay = null;
	private View[] mbtnMenuBtns = null;
	private int[] mnMenuBtns = {
		R.id.shoplistbtntopupsmartfren, R.id.shoplistbtntopupsevelin,
		R.id.shoplistbtnupointhrn, R.id.shoplistbtnupoint,
		R.id.shoplistbtnatmbca, R.id.shoplistbtnatmbersama,
		R.id.shoplistbtnxlairtime, R.id.shoplistbtnxlvoucher, R.id.shoplistbtnindosat,
		R.id.shoplistbtnmpointairtime, R.id.shoplistbtndpointairtime, R.id.shoplistbtncelcom,
		R.id.shoplistbtnvntelco,
		R.id.shoplistbtnlastresult, R.id.shoplistbtnstaggate
	};
	private int[] mnMenuBtnsInitId = {
		SMARTFREN, SEVELIN,
		UPOINTHRN, UPOINT,
		BCA, BERSAMA,
		XLAIRTIME, XLHRN, INDOSAT,
		MPOINT, DPOINT,
		CELCOM, VNTELCO,
		LASTRESULT, STAGGATE
	};
	private View mvShop = null;
	private boolean mbShopBtn = false;
	private boolean mbGateway = false;
	private Mimopay mMimopay = null;

	private int mnBtnChooseId = 0;
	private Handler mBtnShopHandler = null;
	private Handler mBtnChooseHandler = null;

	// Called when the activity is first created.
    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler());

		LayoutInflater inflater = getLayoutInflater();
		mvShop = getLayoutInflater().inflate(R.layout.shop, null);

		mbtnPay = (ImageButton) mvShop.findViewById(R.id.shoplistbtnpay);
		mbtnPay.setOnTouchListener(new View.OnTouchListener() { @Override public boolean onTouch(View v, MotionEvent event) {
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
			mBtnShopHandler.postDelayed(
				new Runnable() { @Override public void run() { mBtnShopHandler = null; }},
				500);
			return true;
		}});

		mGLView = new ClearGLSurfaceView(this);
		setContentView(mGLView);

		mbtnMenuBtns = new View[TOTALMENUBTNS];
		for(int i=0;i<TOTALMENUBTNS;i++) {
			mbtnMenuBtns[i] = mvShop.findViewById(mnMenuBtns[i]);
			final int fi = i;
			mbtnMenuBtns[i].setOnClickListener(new View.OnClickListener(){ public void onClick(View view) {
				if(mBtnChooseHandler != null) return;
				mnBtnChooseId = fi;
				mBtnChooseHandler = new Handler();
				mBtnChooseHandler.postDelayed(
					new Runnable() { @Override public void run() { 
						jprintf("togglebutton: " + Integer.toString(mnBtnChooseId));
						mbShopBtn = false;
						mbtnPay.setPressed(mbShopBtn);
						View v = (View) mvShop.findViewById(R.id.shoplistitems);
						if(v != null) {
							v.setVisibility(View.GONE);
						}
						mBtnChooseHandler = null;
						initMimopay(mnMenuBtnsInitId[mnBtnChooseId]);
					}},
					500
				);
			}});
		}

		getWindow().addContentView(mvShop, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

	private class TopExceptionHandler implements Thread.UncaughtExceptionHandler
	{
		private Thread.UncaughtExceptionHandler defaultUEH;
		TopExceptionHandler() {
			this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
		}
		public void uncaughtException(Thread t, Throwable e) {
			jprintf("AbnormalTermination. reason: " + e.toString());
			System.exit(666);
		}
	}

    @Override protected void onPause()
    {
        super.onPause();
		if(mGLView != null) {
	        mGLView.onPause();
		}
    }

    @Override protected void onResume()
    {
        super.onResume();
		if(mGLView != null) {
	        mGLView.onResume();
		}
    }

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Main codes of How to use Mimopay SDK.
	//
	// To have better understanding it is strongly recommend to refer to http://staging.mimopay.com/api documentation.
	// Before create Mimopay object, you need to fill parameters that is described in documentation. Values that is used
	// in this sample is our mimopay's internal test account.
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private void initMimopay(int paymentid)
	{
		String emailOrUserId = "1385479814";	// this parameter is your user's unique id or user's email. Normally your app/game should have unique ID for every user
		String merchantCode = "ID-0031";		// for this parameter, after registration to mimopay, your should received this from us
		//String productName = "ID-0031-0001";	// this parameter is also your own territory. It will be pass back to your server, either successful or an error occurs
		String productName = "10_Coins";	// this parameter is also your own territory. It will be pass back to your server, either successful or an error occurs
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

		mMimopay = new Mimopay(getApplicationContext(),
			emailOrUserId,
			merchantCode,
			productName,
			transactionId, 
			secretKeyStaging,
			secretKeyGateway,
			currency, 
			new MimopayInterface() { public void onReturn(String info, ArrayList<String> params) {
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
				}
			}}
		);

		// enableLog is Mimopay SDK's internal log print. If set to enable, all logs will printed out in your app's log. This is very usefull in your development phase
		mMimopay.enableLog(true);

		// By default, the payment process will goes to staging.mimopay.com. Keep it commented out while you are still in development phase, 
		// when you are ready to production you can un-comment it, so SDK will goes to gateway.mimopay.com
		//
		mMimopay.enableGateway(mbGateway);

		AlertDialog aldlg = null;
		AlertDialog.Builder altbld = null;
		
        switch (paymentid)
		{
        case SMARTFREN:	// smartfren
			//
			// this will launch UI mode top up activity and straight to show Smartfren channel.
			//
			mMimopay.executeTopup("smartfren");
			break;
        case SEVELIN: // sevelin
			//
			// UI mode for sevelin, straight to show Sevelin channel
			// 
			mMimopay.executeTopup("sevelin");
			break;
        case BCA: // ATM BCA
			paymentATMs("atm_bca");
			break;
        case BERSAMA: // ATM Bersama
			paymentATMs("atm_bersama");
			break;
        case UPOINTHRN: // upoint hrn
			//
			// UI mode for upoint voucher, straight to show upoint voucher channel
			// 
			mMimopay.executeUPointHrn();
        	break;
        case UPOINT: // upoint
			altbld = new AlertDialog.Builder(MainActivity.this);
			altbld.setMessage("Denom list or fixed denom ?")
			.setCancelable(true)
			.setPositiveButton("Denom List", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
				mMimopay.executeUPointAirtime();
			}})
			.setNegativeButton("Fixed Denom (IDR 1000)", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
				mMimopay.executeUPointAirtime("1000");
			}});
			aldlg = altbld.create();
			aldlg.setTitle("Upoint Denom");
			aldlg.setIcon(android.R.drawable.stat_notify_error);
			aldlg.show();
			break;
        case XLAIRTIME: // XL Airtime
			altbld = new AlertDialog.Builder(MainActivity.this);
			altbld.setMessage("Denom list or fixed denom ?")
			.setCancelable(true)
			.setPositiveButton("Denom List", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
				mMimopay.executeXLAirtime();
			}})
			.setNegativeButton("Fixed Denom (IDR 20000)", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
				mMimopay.executeXLAirtime("20000");
			}});
			aldlg = altbld.create();
			aldlg.setTitle("XL Pulsa Denom");
			aldlg.setIcon(android.R.drawable.stat_notify_error);
			aldlg.show();
			break;
        case XLHRN: // XL HRN
			mMimopay.executeXLHrn();
			break;
        case INDOSAT: // indosat airtime
			altbld = new AlertDialog.Builder(MainActivity.this);
			altbld.setMessage("Denom list or fixed denom ?")
			.setCancelable(true)
			.setPositiveButton("Denom List", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
				mMimopay.executeIndosatAirtime();
			}})
			.setNegativeButton("Fixed Denom (IDR 11000)", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
				mMimopay.executeIndosatAirtime("11000");
			}});
			aldlg = altbld.create();
			aldlg.setTitle("Indosat Pulsa Denom");
			aldlg.setIcon(android.R.drawable.stat_notify_error);
			aldlg.show();
			break;
        case MPOINT: // mpoint
			altbld = new AlertDialog.Builder(MainActivity.this);
			altbld.setMessage("Choose your language ?\n(Dutch language here just an example)")
			.setCancelable(true)
			.setPositiveButton("English", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
				paymentMPoint();
			}})
			.setNegativeButton("Dutch", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
				// Please note that payment methods those are in Bahasa by default, cannot be customized.
				// Since MPoint is in English by default, you can do as below if you want to customized to other language.
				// Please refer to CustomLang.java, it shows how all words should be managed.
				mMimopay.setUiLanguage(CustomLang.mDutch);
				paymentMPoint();
			}});
			aldlg = altbld.create();
			aldlg.setTitle("Maxis MPoint");
			aldlg.setIcon(android.R.drawable.stat_notify_error);
			aldlg.show();
			break;
        case DPOINT: // dpoint
			altbld = new AlertDialog.Builder(MainActivity.this);
			altbld.setMessage("Choose your language ?\n(Dutch language here just an example)")
			.setCancelable(true)
			.setPositiveButton("English", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
				paymentDPoint();
			}})
			.setNegativeButton("Dutch", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
				// Please note that payment methods those are in Bahasa by default, cannot be customized.
				// Since DPoint is in English by default, you can do as below if you want to customized to other language.
				// Please refer to CustomLang.java, it shows how all words should be managed.
				mMimopay.setUiLanguage(CustomLang.mDutch);
				paymentDPoint();
			}});
			aldlg = altbld.create();
			aldlg.setTitle("Language");
			aldlg.setIcon(android.R.drawable.stat_notify_error);
			aldlg.show();
			break;
        case CELCOM:
			altbld = new AlertDialog.Builder(MainActivity.this);
			altbld.setMessage("Choose your language ?\n(Dutch language here just an example)")
			.setCancelable(true)
			.setPositiveButton("English", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
				paymentCelcom();
			}})
			.setNegativeButton("Dutch", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
				// Please note that payment methods those are in Bahasa by default, cannot be customized.
				// Since Celcom is in English by default, you can do as below if you want to customized to other language.
				// Please refer to CustomLang.java, it shows how all words should be managed.
				jprintf(String.format("CustomLang.mDutch:%d", CustomLang.mDutch.length));
				mMimopay.setUiLanguage(CustomLang.mDutch);
				paymentCelcom();
			}});
			aldlg = altbld.create();
			aldlg.setTitle("Language");
			aldlg.setIcon(android.R.drawable.stat_notify_error);
			aldlg.show();
			break;
        case VNTELCO:	// Vietnam Telco
			mMimopay.executeVnTelco();
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

	private void paymentATMs(final String channel)
	{
		AlertDialog.Builder altbld = new AlertDialog.Builder(MainActivity.this);
		altbld.setMessage("Denom list or fixed denom ?")
		.setCancelable(true)
		.setPositiveButton("Denom List", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
			mMimopay.executeATMs(channel);
		}})
		.setNegativeButton("Fixed Denom (IDR 90000)", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
			mMimopay.executeATMs(channel, "90000");
		}});
		AlertDialog aldlg = altbld.create();
		aldlg.setTitle("ATM Denom");
		aldlg.setIcon(android.R.drawable.stat_notify_error);
		aldlg.show();
	}

	private void paymentMPoint()
	{
		if(mMimopay == null) return;

		AlertDialog.Builder altbld = new AlertDialog.Builder(MainActivity.this);
		altbld.setMessage("Denom list or fixed denom ?")
		.setCancelable(true)
		.setPositiveButton("Denom List", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
        	mMimopay.setCurrency("MYR");
			mMimopay.executeMPointAirtime();
		}})
		.setNegativeButton("Fixed Denom (MYR 10)", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
        	mMimopay.setCurrency("MYR");
			mMimopay.executeMPointAirtime("10");
		}});
		AlertDialog aldlg = altbld.create();
		aldlg.setTitle("MPoint Denom");
		aldlg.setIcon(android.R.drawable.stat_notify_error);
		aldlg.show();
	}

	private void paymentDPoint()
	{
		if(mMimopay == null) return;

		AlertDialog.Builder altbld = new AlertDialog.Builder(MainActivity.this);
		altbld.setMessage("Denom list or fixed denom ?")
		.setCancelable(true)
		.setPositiveButton("Denom List", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
        	mMimopay.setCurrency("MYR");
			mMimopay.executeDPointAirtime();
		}})
		.setNegativeButton("Fixed Denom (MYR 5)", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
        	mMimopay.setCurrency("MYR");
			mMimopay.executeDPointAirtime("5");
		}});
		AlertDialog aldlg = altbld.create();
		aldlg.setTitle("DPoint Denom");
		aldlg.setIcon(android.R.drawable.stat_notify_error);
		aldlg.show();
	}

	private void paymentCelcom()
	{
		if(mMimopay == null) return;

		AlertDialog.Builder altbld = new AlertDialog.Builder(MainActivity.this);
		altbld.setMessage("Denom list or fixed denom ?")
		.setCancelable(true)
		.setPositiveButton("Denom List", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
        	mMimopay.setCurrency("MYR");
			mMimopay.executeCelcomAirtime();
		}})
		.setNegativeButton("Fixed Denom (MYR 1)", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
        	mMimopay.setCurrency("MYR");
			mMimopay.executeCelcomAirtime("1");
		}});
		AlertDialog aldlg = altbld.create();
		aldlg.setTitle("Celcom Denom");
		aldlg.setIcon(android.R.drawable.stat_notify_error);
		aldlg.show();
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

    public ClearGLSurfaceView(Context context)
    {
        super(context);
        mRenderer = new ClearRenderer(context, this);
        setRenderer(mRenderer);
    }

    @Override public boolean onTrackballEvent(MotionEvent e)
    {
		afterTouch();
        mRenderer.mAngleX += e.getX() * TRACKBALL_SCALE_FACTOR;
        mRenderer.mAngleY += e.getY() * TRACKBALL_SCALE_FACTOR;
        requestRender();
        return true;
    }

    @Override public boolean onTouchEvent(MotionEvent e)
    {
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

class ClearRenderer implements GLSurfaceView.Renderer
{
    private ClearGLSurfaceView view;
    private Context context;
    private DrawModel model;
    //private float angleY = 0f;

    public float mAngleX = 0f;
    public float mAngleY = 0f;
	public boolean autoRotate = true;

    private int[] mTexture = new int[1];

    public ClearRenderer(Context context, ClearGLSurfaceView view)
    {
        this.view = view;
        this.context = context;
        model = new DrawModel(context, R.raw.rock);
    }

    private void loadTexture(GL10 gl, Context mContext, int mTex)
    {
        gl.glGenTextures(1, mTexture, 0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture[0]);
        Bitmap bitmap;
        bitmap = BitmapFactory.decodeResource(mContext.getResources(), mTex);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
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

    public void onSurfaceChanged(GL10 gl, int w, int h)
    {
        gl.glViewport(0, 0, w, h);
    }

    public void onDrawFrame(GL10 gl)
    {
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
