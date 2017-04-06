package com.smsparking.parkman.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.smsparking.parkman.R;
import com.smsparking.parkman.common.CommonConstants;
import com.smsparking.parkman.service.ServiceListner;
import com.smsparking.parkman.service.SyncService;
import com.smsparking.parkman.utils.ConnectionUtils;
import com.smsparking.parkmanobjects.GetVehicles;
import com.smsparking.parkmanobjects.PreferanceManager;
import com.smsparking.parkmanobjects.Site;
import com.smsparking.parkmanobjects.Vehicle;

public class ParkedVehiclesActivity extends Activity{
	private PreferanceManager prefMan;
	private ParkedVehiclesBaseAdapter adapter;
	private List<Vehicle> searchResults = new ArrayList<Vehicle>();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_site_list);
		prefMan = new PreferanceManager(getApplicationContext());
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setTitle("Parked Vehicles");
		init();
	}

	void init() {
		try {
			
			final ListView lv = (ListView) findViewById(R.id.list_view);
			adapter = new ParkedVehiclesBaseAdapter(getApplicationContext(), searchResults);
			lv.setAdapter(adapter);
			
			lv.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
				}

			});

			EditText inputSearch = (EditText) findViewById(R.id.inputSearch);
			/**
			 * Enabling Search Filter
			 * */
			inputSearch.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
					// When user changed the Text
					ParkedVehiclesActivity.this.adapter.getFilter().filter(cs);
				}

				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

				}

				@Override
				public void afterTextChanged(Editable arg0) {

				}
			});
			refreshSiteList(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void refreshSiteList(View v) {
		ConnectionUtils connectionUtils = new ConnectionUtils(this);
		if (!connectionUtils.isConnectingToInternet()) {
			showAlertDialog(this, "No Internet Connection", "You don't have internet connection.", false);
			return;
		} else {
			try {	
				
				String siteList= prefMan.getStringPref(CommonConstants.PREF_SITE);
				Gson g = new Gson();
				
				Site site = g.fromJson(siteList, Site.class);
				
				HashMap<String, String> params = new HashMap<String, String>();
				params.put(CommonConstants.REQUEST_CALL, CommonConstants.REQUEST_Get_Vehicles);
				params.put(CommonConstants.PARK_INFO_SITE, site.dbid);
				// connect
				final SyncService service = new SyncService(params, ParkedVehiclesActivity.this, false);
				
				service.registerServiceListner(new ServiceListner() {
					@Override
					public void update(Object result) {
						service.removeServiceListner(this);
						try {
							if ("ERROR".contains(result.toString())) {
								return;
							}
							Gson g = new Gson();
							GetVehicles getVehicles = g.fromJson(result.toString(), GetVehicles.class);
							if(getVehicles.vehicles != null){
								searchResults = getVehicles.vehicles;
							}else{
								searchResults = new ArrayList<Vehicle>();
							}
							repopulateList();
							
						} catch (Exception e) {							
						}
						}
				});
				

				String host= prefMan.getStringPref(CommonConstants.HOST);
				
				String url = host + CommonConstants.PARK_INFO;
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					service.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
				} else {
					service.execute(url);
				}				
			} catch (Exception e) {
				showAlertDialog(this, "Error Connecting", e.getMessage(), false);
				return;
			}
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
