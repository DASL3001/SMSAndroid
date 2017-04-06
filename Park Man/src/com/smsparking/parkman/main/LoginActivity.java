package com.smsparking.parkman.main;

import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.smsparking.parkman.R;
import com.smsparking.parkman.common.CommonConstants;
import com.smsparking.parkman.service.ServiceListner;
import com.smsparking.parkman.service.SyncService;
import com.smsparking.parkman.ui.SiteListActivity;
import com.smsparking.parkman.utils.ConnectionUtils;
import com.smsparking.parkmanobjects.PreferanceManager;

public class LoginActivity extends Activity {

	public static final String TAG = "PARK_MAN";
	private static ProgressDialog dialog = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		// FULL Screen mode no title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		
		// remove keyboard when starting
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		setContentView(R.layout.activity_login);
		
		setLoginDetails();
		
		populateAboutInfomation();
	}

	private void setLoginDetails() {	
		PreferanceManager prefMan = new PreferanceManager(getApplicationContext());
		String h = prefMan.getStringPref(CommonConstants.HOST);
		if(h != null && h.equals("") ==false && CommonConstants.LOGIN_HOST.equals(h)){
			h = CommonConstants.LOGIN_HOST;
		}
		EditText edtxtusername = (EditText) findViewById(R.id.editTextUserName);
		edtxtusername.setText(CommonConstants.LOGIN_HOST);
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			populateAboutInfomation();			

		} catch (Exception e) {
			
		}
	}

	@Override
	public void onBackPressed() {
		// This will be called either automatically for you on 2.0

		Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to exit?");
		builder.setCancelable(true);
		builder.setPositiveButton("Yes", new OkOnClickListener());
		builder.setNegativeButton("No", new CancelOnClickListener());
		AlertDialog dialog = builder.create();
		dialog.show();
		return;
	}

	private final class CancelOnClickListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {

		}
	}

	private class OkOnClickListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			try {

				
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			} catch (Exception e) {
				
			}

		}
	}

	@Override
	protected void onPause() {
		try {
			super.onPause();
		
		} catch (Exception e) {
		
		}
	}

	@Override
	protected void onDestroy() {
		try {
			super.onDestroy();			
		} catch (Exception e) {

		}
	}

	

	public void login(View v) {
		EditText edtxtusername = (EditText) findViewById(R.id.editTextUserName);

		String userName = edtxtusername.getText().toString().trim();
		if ("".equals(userName) ) {
			userName = com.smsparking.parkman.common.CommonConstants.LOGIN_HOST;
		}
		try {
			checkLogin(userName);

		} catch (Exception e) {
			
		}

	}

	@SuppressLint("DefaultLocale")
	private boolean isValidLogin(Document doc) {
		Node node = doc.getElementsByTagName("valid").item(0);
		String loginResult = node.getTextContent();
		if ("true".equals(loginResult.toLowerCase())) {
			return true;
		} else {
			return false;
		}
	}

	public void showAlertDialog(Context context, String title, String message, Boolean status) {
		Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle(title);
		alertDialogBuilder.setMessage(message);
		alertDialogBuilder.setIcon((status) ? R.drawable.success : R.drawable.about);
		alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}

		});
		AlertDialog dialog = alertDialogBuilder.create();
		dialog.show();
	}

	

	private void showStartScreen(int pendingchanges) {
		try {
			 
				startSiteSelectScreen();
			
		} catch (Exception e) {

		}
	}

	

	public void toast(String s) {
		Toast.makeText(this, s, Toast.LENGTH_LONG).show();
	}
	
	void startSiteSelectScreen() {
		try {			
				Intent i = new Intent(getApplicationContext(), SiteListActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(i);
			
		} catch (Exception e) {
			
		}
	}	

	public static final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (dialog != null) {
				dialog.dismiss();
			}
		}

	};	

	private void showAlertOnUiThread(final Context context, final String title, final String message, final boolean status) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Builder alertDialogBuilder = new AlertDialog.Builder(context);
				alertDialogBuilder.setTitle(title);
				alertDialogBuilder.setMessage(message);
				alertDialogBuilder.setIcon((status) ? R.drawable.success : R.drawable.about);
				alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}

				});
				AlertDialog dialog = alertDialogBuilder.create();
				dialog.show();
			}
		});
	}


	private void checkLogin(String userName) {
		final PreferanceManager prefMan = new PreferanceManager(getApplicationContext());
		ConnectionUtils connectionUtils = new ConnectionUtils(this);

		
			if (!connectionUtils.isConnectingToInternet()) {
				showAlertDialog(this, "No Internet Connection", "You don't have internet connection.", false);
				return;
			} else {
				HashMap<String, String> params = new HashMap<String, String>();

				params.put(CommonConstants.REQUEST_CALL, CommonConstants.REQUEST_Get_Parks);
				// connect
				final SyncService service = new SyncService(params, LoginActivity.this, false);
				service.registerServiceListner(new ServiceListner() {

					@Override
					public void update(Object result) {
						
						service.removeServiceListner(this);
						try {
							
							Log.i("PARK", result.toString());
							
							prefMan.removeKey(CommonConstants.PREF_SITE_LIST);
							if ("ERROR".contains(result.toString())) {
								return;
							}
							
							prefMan.addStringPref(CommonConstants.PREF_SITE_LIST, result.toString());
							startSiteSelectScreen();

						} catch (Exception e) {
							
						}
					}
				});
				EditText edtxtusername = (EditText) findViewById(R.id.editTextUserName);
				prefMan.addStringPref(CommonConstants.HOST, edtxtusername.getText().toString());
				// production
				String url = edtxtusername.getText().toString() + CommonConstants.PARK_INFO;
				Log.i("park url", url);

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					service.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
				} else {
					service.execute(url);
				}
			}		
	}	

	private void populateAboutInfomation() {
		TextView coyRightText = (TextView) findViewById(R.id.coyRightText);
		try {

			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			String versionStr = getResources().getString(R.string.version);
			String version = pInfo.versionName;

			String build_year = getResources().getString(R.string.build_year);

			String yearTxt = build_year + " ";
			String copy_right = getResources().getString(R.string.copy_right);
			String crCompanyName = getResources().getString(R.string.company_name);
			coyRightText.setText(copy_right + " " + yearTxt + " " + crCompanyName + " " + versionStr + " " + version);

		} catch (Exception e) {

		}
	}

	

}