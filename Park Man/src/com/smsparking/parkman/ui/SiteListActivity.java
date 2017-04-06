package com.smsparking.parkman.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.smsparking.parkman.R;
import com.smsparking.parkman.common.CommonConstants;
import com.smsparking.parkman.main.LoginActivity;
import com.smsparking.parkman.service.ServiceListner;
import com.smsparking.parkman.service.SyncService;
import com.smsparking.parkman.utils.ConnectionUtils;
import com.smsparking.parkmanobjects.GetParks;
import com.smsparking.parkmanobjects.PreferanceManager;
import com.smsparking.parkmanobjects.Site;

public class SiteListActivity extends Activity {

	private PreferanceManager prefMan;
	private SiteListBaseAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_site_list);
		prefMan = new PreferanceManager(getApplicationContext());
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setTitle("Site Select");
		init();
	}

	void init() {
		try {
			String siteList = prefMan
					.getStringPref(CommonConstants.PREF_SITE_LIST);
			Gson g = new Gson();

			GetParks fromJson = g.fromJson(siteList, GetParks.class);
			ArrayList<Site> searchResults = (ArrayList<Site>) fromJson.sites;

			final ListView lv = (ListView) findViewById(R.id.list_view);
			adapter = new SiteListBaseAdapter(getApplicationContext(),
					searchResults);
			lv.setAdapter(adapter);

			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adapter, View v,
						int position, long id) {
					Site item = (Site) adapter.getItemAtPosition(position);

					Gson g = new Gson();
					prefMan.addStringPref(CommonConstants.PREF_SITE,
							g.toJson(item));
					openMainMenue();

				}

			});

			EditText inputSearch = (EditText) findViewById(R.id.inputSearch);
			/**
			 * Enabling Search Filter
			 * */
			inputSearch.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence cs, int arg1, int arg2,
						int arg3) {
					// When user changed the Text
					SiteListActivity.this.adapter.getFilter().filter(cs);
				}

				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1,
						int arg2, int arg3) {

				}

				@Override
				public void afterTextChanged(Editable arg0) {

				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onBackPressed() {
		// This will be called either automatically for you on 2.0

		Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to log out?");
		builder.setCancelable(true);
		builder.setPositiveButton("Yes", new OkOnClickListener());
		builder.setNegativeButton("No", new CancelOnClickListener());
		AlertDialog dialog = builder.create();
		dialog.show();
		return;
	}

	private final class CancelOnClickListener implements
			DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {

		}
	}

	private final class OkOnClickListener implements
			DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			Intent i = new Intent(getApplicationContext(), LoginActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(i);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {

		return true;
	}

	private void openMainMenue() {
		Intent i = new Intent(getApplicationContext(),
				ParkedVehiclesActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(i);
	}

	public void refreshSiteList(View v) {
		ConnectionUtils connectionUtils = new ConnectionUtils(this);
		if (!connectionUtils.isConnectingToInternet()) {
			showAlertDialog(this, "No Internet Connection",
					"You don't have internet connection.", false);
			return;
		} else {
			try {
				HashMap<String, String> params = new HashMap<String, String>();
				params.put(CommonConstants.REQUEST_CALL,
						CommonConstants.REQUEST_Get_Parks);
				// connect
				final SyncService service = new SyncService(params,
						SiteListActivity.this, false);

				service.registerServiceListner(new ServiceListner() {
					@Override
					public void update(Object result) {
						service.removeServiceListner(this);
						try {
							prefMan.removeKey(CommonConstants.PREF_SITE_LIST);
							if ("ERROR".contains(result.toString())) {
								return;
							}
							prefMan.addStringPref(
									CommonConstants.PREF_SITE_LIST,
									result.toString());
							repopulateList();
						} catch (Exception e) {
						}
					}
				});
				String host = prefMan.getStringPref(CommonConstants.HOST);
				String url = host
						+ CommonConstants.PARK_INFO;
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					service.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
							url);
				} else {
					service.execute(url);
				}
			} catch (Exception e) {
				showAlertDialog(this, "Error Connecting", e.getMessage(), false);
				return;
			}
		}
	}

	public void showAlertDialog(Context context, String title, String message,
			Boolean status) {
		Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle(title);
		alertDialogBuilder.setMessage(message);
		alertDialogBuilder.setIcon((status) ? R.drawable.success
				: R.drawable.about);
		alertDialogBuilder.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}

				});
		AlertDialog dialog = alertDialogBuilder.create();
		dialog.show();
	}

	@Override
	protected void onResume() {
		try {
			super.onResume();
			repopulateList();
		} catch (Exception e) {
		}
	}

	private void repopulateList() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					String siteList = prefMan
							.getStringPref(CommonConstants.PREF_SITE_LIST);
					Gson g = new Gson();

					GetParks fromJson = g.fromJson(siteList, GetParks.class);
					ArrayList<Site> searchResults = (ArrayList<Site>) fromJson.sites;

					adapter.clearAllLists();
					adapter.addSiteList(searchResults);
					adapter.notifyDataSetChanged();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}