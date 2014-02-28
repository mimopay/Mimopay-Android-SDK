package com.bayninestudios.androidogremesh;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Bundle;

import android.util.Log;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.RelativeLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.graphics.Typeface;
import android.view.View.OnClickListener;
import android.view.View;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.widget.Toast;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;

// java
import java.util.ArrayList;

// mimopay
import com.mimopay.Mimopay;
import com.mimopay.MimopayInterface;

public class AndroidOgreMesh extends Activity
{
	public void jprintf(String s) { Log.d("JimBas", "AndroidOgreMesh: " + s); }

    private GLSurfaceView mGLView = null;

	boolean btopup;
	boolean bsmartfren;
	boolean bsevelin;
	boolean batmbca;
	boolean bupoint;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState); jprintf("onCreate");
		Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler());
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		SharedPreferences userDetails = PreferenceManager.getDefaultSharedPreferences(this);

		if(userDetails != null) {
			boolean bset = userDetails.getBoolean("set", false);
			if(bset) {
				btopup = userDetails.getBoolean("topup", false);
				bsmartfren = userDetails.getBoolean("smartfren", false);
				bsevelin = userDetails.getBoolean("sevelin", false);
				batmbca = userDetails.getBoolean("atmbca", false);
				bupoint = userDetails.getBoolean("upoint", false);
				mGLView = new ClearGLSurfaceView(this);
				setContentView(mGLView);
			} else {
				setupPaymentOptionUI();
			}
		} else {
			mGLView = new ClearGLSurfaceView(this);
			setContentView(mGLView);
		}

		//addContentView(btch, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
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

	private void setupPaymentOptionUI()
	{
        RelativeLayout mainlayout = new RelativeLayout(this); //`

			int id = 0;

            final CheckBox cbMainTopup = new CheckBox(this);
            cbMainTopup.setId(++id);
			RelativeLayout.LayoutParams cbMainTopupLp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			//cbMainTopupLp.addRule(RelativeLayout.BELOW, id-1);
			//cbMainTopupLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
			cbMainTopup.setLayoutParams(cbMainTopupLp);
            cbMainTopup.setText("Top Up");
            mainlayout.addView(cbMainTopup);

            final CheckBox cbSmartfren = new CheckBox(this);
            cbSmartfren.setId(++id);
			RelativeLayout.LayoutParams cbSmartfrenLp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			cbSmartfrenLp.addRule(RelativeLayout.BELOW, id-1);
			//cbSmartfrenLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
			cbSmartfren.setLayoutParams(cbSmartfrenLp);
            cbSmartfren.setText("Smartfren");
            mainlayout.addView(cbSmartfren);

            final CheckBox cbSevelin = new CheckBox(this);
            cbSevelin.setId(++id);
			RelativeLayout.LayoutParams cbSevelinLp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			cbSevelinLp.addRule(RelativeLayout.BELOW, id-1);
			//cbSevelinLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
			cbSevelin.setLayoutParams(cbSevelinLp);
            cbSevelin.setText("Sevelin");
            mainlayout.addView(cbSevelin);

            final CheckBox cbAtmBca = new CheckBox(this);
            cbAtmBca.setId(++id);
			RelativeLayout.LayoutParams cbAtmBcaLp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			cbAtmBcaLp.addRule(RelativeLayout.BELOW, id-1);
			//cbAtmBcaLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
			cbAtmBca.setLayoutParams(cbAtmBcaLp);
            cbAtmBca.setText("ATM BCA");
            mainlayout.addView(cbAtmBca);

            final CheckBox cbUPoint = new CheckBox(this);
            cbUPoint.setId(++id);
			RelativeLayout.LayoutParams cbUPointLp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			cbUPointLp.addRule(RelativeLayout.BELOW, id-1);
			//cbUPointLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
			cbUPoint.setLayoutParams(cbUPointLp);
            cbUPoint.setText("U-Point");
            mainlayout.addView(cbUPoint);

			Button btch = new Button(this);
            btch.setId(++id);
			RelativeLayout.LayoutParams btchlp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			btchlp.addRule(RelativeLayout.BELOW, id-1);
			//btchlp.addRule(RelativeLayout.CENTER_HORIZONTAL);
			btch.setLayoutParams(btchlp);
			btch.setText("    SAVE    ");
			btch.setOnClickListener(new OnClickListener(){ public void onClick(View view) {
					boolean btopup = cbMainTopup.isChecked();
					boolean bsmartfren = cbSmartfren.isChecked();
					boolean bsevelin = cbSevelin.isChecked();
					boolean batmbca = cbAtmBca.isChecked();
					boolean bupoint = cbUPoint.isChecked();
					if(!btopup && !bsmartfren && !bsevelin && !batmbca && !bupoint) {
						Toast.makeText(getApplicationContext(), "You must checked at least one!", Toast.LENGTH_LONG).show();
					} else {
						SharedPreferences userDetails = PreferenceManager.getDefaultSharedPreferences(AndroidOgreMesh.this);
						Editor edit = userDetails.edit();
						edit.clear();
						edit.putBoolean("set", true);
						edit.putBoolean("topup", cbMainTopup.isChecked());
						edit.putBoolean("smartfren", cbSmartfren.isChecked());
						edit.putBoolean("sevelin", cbSevelin.isChecked());
						edit.putBoolean("atmbca", cbAtmBca.isChecked());
						edit.putBoolean("upoint", cbUPoint.isChecked());
						edit.commit();
						finish();
					}
			}});
            mainlayout.addView(btch);

        setContentView(mainlayout, new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)); //`
	}

    @Override
    protected void onPause()
    {
        super.onPause();
		if(mGLView != null) {
	        mGLView.onPause();
		}
    }

    @Override
    protected void onResume()
    {
        super.onResume();
		if(mGLView != null) {
	        mGLView.onResume();
		}
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) 
	{
		int id = 0; //`

		if(btopup) { //`
		    menu.add(0, ++id, 0, "Top Up");
			menu.findItem(id).setIcon(android.R.drawable.ic_menu_info_details);
		} //`
		if(bsmartfren) { //`
		    menu.add(0, ++id, 0, "Smartfren");
			menu.findItem(id).setIcon(android.R.drawable.ic_menu_info_details);
		} //`
		if(bsevelin) { //`
		    menu.add(0, ++id, 0, "Sevelin");
			menu.findItem(id).setIcon(android.R.drawable.ic_menu_info_details);
		} //`
		if(batmbca) { //`
		    menu.add(0, ++id, 0, "ATM BCA");
			menu.findItem(id).setIcon(android.R.drawable.ic_menu_info_details);
		} //`
		if(bupoint) { //`
		    menu.add(0, ++id, 0, "U-Point");
			menu.findItem(id).setIcon(android.R.drawable.ic_menu_info_details);
		} //`
		menu.add(0, ++id, 0, "Last Result");
		menu.findItem(id).setIcon(android.R.drawable.ic_menu_info_details);
        return super.onCreateOptionsMenu(menu);
    }

	private final int TOPUP = 1;
	private final int TOPUP_SMARTFREN = 2;
	private final int TOPUP_SEVELIN = 3;
	private final int TOPUP_ATMBCA = 4;
	private final int AIRTIME_UPOINT = 5;
	private final int LAST_RESULT = 6;

    @Override public boolean onOptionsItemSelected(MenuItem item) 
	{
		String s = item.getTitle().toString(); //`

		if(s != null) { //`
			if(s.equals("Top Up")) {
				initMimopay(TOPUP);
			} else if(s.equals("Smartfren")) {
				initMimopay(TOPUP_SMARTFREN);
			} else if(s.equals("Sevelin")) {
				initMimopay(TOPUP_SEVELIN);
			} else if(s.equals("ATM BCA")) {
				initMimopay(TOPUP_ATMBCA);
			} else if(s.equals("U-Point")) {
				initMimopay(AIRTIME_UPOINT);
			} else if(s.equals("Last Result")) {
				initMimopay(LAST_RESULT);
			} else {
				return super.onOptionsItemSelected(item);
			}
		} else { //`
			return super.onOptionsItemSelected(item);
		}
		return true;
    }

//jim
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

	void initMimopay(int paymentid)
	{
		String emailOrUserId = "YourEmailOrUserId";
		String merchantCode = "YourMerchantCode";
		String productName = "YourProductName";
		String transactionId = "";	// leave it empty, let the library generate it (unix timestamp)
		String secretKey = "YourSecretKey";
		String currency = "IDR";

		mMimopay = new Mimopay(getApplicationContext(), emailOrUserId,
			merchantCode, productName, transactionId, secretKey, currency, mMimopayInterface);

		//mMimopay.enableLog(true);
		
		AlertDialog.Builder altbld = null;
		AlertDialog alert = null;

        switch (paymentid)
		{
        case TOPUP: // launch topup activity
			mMimopay.executeTopup();
			break;
        case TOPUP_SMARTFREN:	// smartfren
			altbld = new AlertDialog.Builder(AndroidOgreMesh.this);
			altbld.setMessage("Mimopay's SDK supports both, Default UI and Quiet Mode. " +
				"In Quiet Mode, the voucher number is currently set to 1234567890123456. " +
				"You may change it later in this sample source code.\n" +
				"Now, please choose which one.")
			.setCancelable(false)
			.setPositiveButton("UI", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
				mQuietMode = false;
				mMimopay.executeTopup("smartfren");
			}})
			.setNegativeButton("Quiet", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
				mQuietMode = true;
				mMimopay.executeTopup("smartfren", "1234567890123456");
			}});
			alert = altbld.create();
			alert.setTitle("Smartfren Topup");
			alert.setIcon(android.R.drawable.stat_notify_error);
			alert.show();
			break;
        case TOPUP_SEVELIN: // sevelin
			altbld = new AlertDialog.Builder(AndroidOgreMesh.this);
			altbld.setMessage("Mimopay's SDK supports both, Default UI and Quiet Mode. " +
				"In Quiet Mode, the voucher number is currently set to 1234567890123456. " +
				"You may change it later in this sample source code.\n" +
				"Now, please choose which one.")
			.setCancelable(false)
			.setPositiveButton("UI", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
				mQuietMode = false;
				mMimopay.executeTopup("sevelin");
			}})
			.setNegativeButton("Quiet", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
				mQuietMode = true;
				mMimopay.executeTopup("sevelin", "1234567890123456");
			}});
			alert = altbld.create();
			alert.setTitle("Sevelin Topup");
			alert.setIcon(android.R.drawable.stat_notify_error);
			alert.show();
			break;
        case TOPUP_ATMBCA: // ATM BCA
			altbld = new AlertDialog.Builder(AndroidOgreMesh.this);
			altbld.setMessage("Mimopay's SDK supports both, Default UI and Quiet Mode. " +
				"In Quiet Mode, the value of mimocard is currently set to 50K. " +
				"You may change it later in this sample source code.\n" +
				"Now, please choose which one.")
			.setCancelable(false)
			.setPositiveButton("UI", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
				mQuietMode = false;
				mMimopay.executeTopup("atm_bca");
			}})
			.setNegativeButton("Quiet", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
				mQuietMode = true;
				mMimopay.executeTopup("atm_bca", "50000");
			}});
			alert = altbld.create();
			alert.setTitle("ATM BCA Topup");
			alert.setIcon(android.R.drawable.stat_notify_error);
			alert.show();
			break;
        case AIRTIME_UPOINT: // upoint
			altbld = new AlertDialog.Builder(AndroidOgreMesh.this);
			altbld.setMessage("Mimopay's SDK supports both, Default UI and Quiet Mode. " +
				"In Quiet Mode, the UPoint credits is currently set to 1000 and phone number is 082114078308. " +
				"You may change them later in this sample source code.\n" +
				"Now, please choose which one.")
			.setCancelable(false)
			.setPositiveButton("UI", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
				mQuietMode = false;
				mMimopay.executeUPointAirtime();
			}})
			.setNegativeButton("Quiet", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) {
				mQuietMode = true;
				mMimopay.executeUPointAirtime("1000", "082114078308", false);
			}});
			alert = altbld.create();
			alert.setTitle("UPoint Airtime");
			alert.setIcon(android.R.drawable.stat_notify_error);
			alert.show();
			break;
		case LAST_RESULT:
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
        }
	}
}

class ClearGLSurfaceView extends GLSurfaceView
{
    ClearRenderer mRenderer;

    public ClearGLSurfaceView(Context context)
    {
        super(context);
        mRenderer = new ClearRenderer(context, this);
        setRenderer(mRenderer);
    }
}

class ClearRenderer implements GLSurfaceView.Renderer
{
    private ClearGLSurfaceView view;
    private DrawModel model;
    private float angleZ = 0f;

    public ClearRenderer(Context context, ClearGLSurfaceView view)
    {
        this.view = view;
        model = new DrawModel(context.getResources().getXml(R.xml.colorcube));
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 25.0f, (view.getWidth() * 1f) / view.getHeight(), 1, 100);
        GLU.gluLookAt(gl, 0f, -10f, 6f, 0.0f, 0.0f, 0f, 0.0f, 1.0f, 1.0f);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glEnable(GL10.GL_DEPTH_TEST);
    }

    public void onSurfaceChanged(GL10 gl, int w, int h)
    {
        gl.glViewport(0, 0, w, h);
    }

    public void onDrawFrame(GL10 gl)
    {
        gl.glClearColor(0f, 0f, 0f, 1.0f);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glPushMatrix();
        gl.glRotatef(angleZ, 0f, 0f, 1f);
        model.draw(gl);
        gl.glPopMatrix();
        angleZ += 1.0f;
    }
}
