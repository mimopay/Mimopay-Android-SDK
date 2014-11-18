package com.jandroid.simplesample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.widget.Button;

// java
import java.util.ArrayList;

// mimopay
import com.mimopay.Mimopay;
import com.mimopay.MimopayInterface;
import com.mimopay.merchant.Merchant;

public class MainActivity extends Activity
{
	public void dp(String s) { Log.d("JimBas", "SimpleSample: " + s); }

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
	private final int LASTRESULT = 12;
	private final int STAGGATE = 13;

	private boolean mbGateway = false;

	@Override public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState); dp("MainActivity: onCreate");
		setContentView(R.layout.main);
	}

	@Override protected void onStart() { super.onStart(); dp("MainActivity: onStart"); }
	@Override protected void onStop() { super.onStop(); dp("MainActivity: onStop"); }
	@Override protected void onResume() { super.onResume(); dp("MainActivity: onResume"); }
	@Override protected void onPause() { super.onPause(); dp("MainActivity: onPause"); }
	@Override protected void onDestroy() { super.onDestroy(); dp("MainActivity: onDestroyed"); }

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// TOP UP

	public void onClickTopup(View view) { initMimopay(TOPUP); }
	public void onClickSmartfren(View view) { initMimopay(SMARTFREN); }
	public void onClickSevelin(View view) { initMimopay(SEVELIN); }

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ATMs

	public void onClickAtms(View view) { initMimopay(ATM); }
	public void onClickAtmBca(View view) { initMimopay(BCA); }
	public void onClickAtmBersama(View view) { initMimopay(BERSAMA); }

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Upoint

	public void onClickUpoint(View view) { initMimopay(UPOINT); }

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// XL

	public void onClickXl(View view) { initMimopay(XL); }
	public void onClickXlPulsa(View view) { initMimopay(XLAIRTIME); }
	public void onClickXlVoucher(View view) { initMimopay(XLHRN); }

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// MPoint

	public void onClickMpoint(View view) { initMimopay(MPOINT); }

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Others

	public void onClickLastResult(View view) { initMimopay(LASTRESULT); }
	public void onClickSwitchGateway(View view) { initMimopay(STAGGATE); }

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Main codes of How to use Mimopay SDK.
	//
	// To have better understanding it is strongly recommend to refer to http://staging.mimopay.com/api documentation.
	// Before create Mimopay object, you need to fill parameters that is described in documentation. Values that is used
	// in this sample is our mimopay's internal test account.
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	boolean mQuietMode = false;
	Mimopay mMimopay = null;
	MimopayInterface mMimopayInterface = new MimopayInterface()
	{
		public void onReturn(String info, ArrayList<String> params)
		{
			String s,toastmsg = "";
			dp("onReturn: " + info);
			if(params != null) {
				toastmsg += (info + "\n\n");
				int i,j = params.size();
				for(i=0;i<j;i++) {
					s = params.get(i);
					toastmsg += (s + "\n");
					dp(String.format("[%d] %s", i, s));
					
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
		} catch(Exception e) { dp("e: " + e.toString()); }
		String currency = "IDR";
		
		if(secretKeyStaging == null || secretKeyGateway == null) {
			Toast.makeText(getApplicationContext(), "secretKey problem!", Toast.LENGTH_LONG).show();
			return;
		}

		mMimopay = new Mimopay(getApplicationContext(), emailOrUserId,
			merchantCode, productName, transactionId, secretKeyStaging, secretKeyGateway, currency, mMimopayInterface);

		AlertDialog.Builder altbld = null;
		AlertDialog alert = null;
		
		// enableLog is Mimopay SDK's internal log print. If set to enable, all logs will printed out in your app's log. This is very usefull in your development phase
		mMimopay.enableLog(true);

		// By default, the payment process will goes to staging.mimopay.com. Keep it commented out while you are still in development phase, 
		// when you are ready to production you can un-comment it, so SDK will goes to gateway.mimopay.com
		//
		mMimopay.enableGateway(mbGateway);

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
				mMimopay.executeTopup("smartfren", "1234567890123456");
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
				"In Quiet Mode, the MPoint Airtime credits is currently set to 200 and phone number is XXXXXXXX. " +
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
				mMimopay.executeMPointAirtime("200", "0126296221", false);
				//Toast.makeText(getApplicationContext(), "not yet implemented", Toast.LENGTH_LONG).show();
			}});
			alert = altbld.create();
			alert.setTitle("MPoint Airtime");
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
			Button btn = (Button) findViewById(R.id.btnswitchgateway);
			btn.setText(mbGateway ? "Switch to Staging" : "Switch to Gateway");
			Toast.makeText(getApplicationContext(), "SDK will points to " + (mbGateway ? "Gateway" : "Staging") + " on next transaction", Toast.LENGTH_LONG).show();
			break;
        }
	}
}

