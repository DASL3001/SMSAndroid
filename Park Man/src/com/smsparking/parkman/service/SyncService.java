package com.smsparking.parkman.service;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.smsparking.parkman.R;

public class SyncService extends AsyncTask<String, String, String> implements ServiceRegister {
	
	private Vector<ServiceListner> observers = new Vector<ServiceListner>();

	private HashMap<String, String> mData = null;// post data
	private ProgressDialog dialog;
	private Context ctx;
	private boolean silent;

	public SyncService(HashMap<String, String> data, Context ctx, boolean silent) {
		mData = data;
		this.ctx = ctx;	
		this.silent = silent;
	}

	@Override
	protected void onPreExecute() {
		try {
			
			super.onPreExecute();
			if(silent){
				return;
			}
			if (dialog != null) {
				stopProgressDialog();
			}
			dialog = ProgressDialog.show(ctx, "Sync service", "Connecting to SMS service....");
		} catch (Exception e) {
			e.printStackTrace();
			stopProgressDialog();
		}
	}

	@Override
	public void registerServiceListner(ServiceListner o) {
		try {
			observers.add(o);			
		} catch (Exception e) {
		}

	}

	@Override
	public void removeServiceListner(ServiceListner o) {
		try {
			observers.remove(0);			
		} catch (Exception e) {
		}

	}

	@Override
	public void notifyServiceListner(Object result) {
		for (int loopIndex = 0; loopIndex < observers.size(); loopIndex++) {
			ServiceListner observer = (ServiceListner) observers.get(loopIndex);
			observer.update(result);
		}

	}

	@Override
	protected String doInBackground(String... params) {
		byte[] result = null;
		String str = "";
		HttpParams p = new BasicHttpParams();
		p.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		HttpClient client = new DefaultHttpClient(p);
		// set connection time out for 15 seconds
	    HttpConnectionParams.setConnectionTimeout(client.getParams(),  15000);	
	    
		HttpPost post = new HttpPost(params[0]);
		try {

			// set up post data
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			if(mData != null){
			Iterator<String> it = mData.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				nameValuePair.add(new BasicNameValuePair(key, mData.get(key)));
			}
			}

			post.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
			try {
				if(silent == false){	
					if (dialog != null) {
						this.dialog.setMessage("Waiting ...");
						if (!this.dialog.isShowing()) {
							this.dialog.show();
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			HttpResponse response = client.execute(post);
			StatusLine statusLine = response.getStatusLine();
			Log.i("parkman getStatusCode ok",statusLine.getStatusCode()+"");
			result = EntityUtils.toByteArray(response.getEntity());
			str = new String(result, "UTF-8");
			Log.i("parkman reply :;;;",str);
			
			if (statusLine.getStatusCode() == HttpURLConnection.HTTP_OK) {
			} else {			
				str = "ERROR";
			}
		} catch (Exception e) {
			e.printStackTrace();
			str = "ERROR";
			stopProgressDialog();
		}
		return str;
	}

	private void stopProgressDialog() {
		try {
			if (dialog != null) {
				dialog.dismiss();
				dialog = null;
			}
		} catch (Exception e) {			
		}

	}

	@Override
	protected void onPostExecute(String result) {
		try {
			if(silent == false){
				if (this.dialog != null && this.dialog.isShowing()) {
					stopProgressDialog();
				}
			}
		} catch (Exception e) {

		}
		try {
			if ("ERROR".contains(result)) {
				if(silent == false){
					String mess = ctx.getResources().getString(R.string.conn_refuse);		
					showAlertDialog(ctx, "Error Connecting", mess , false);
					return;
				}
			}
			notifyServiceListner(result);

		} catch (Exception e) {

		}
	}

	private void showAlertDialog(Context context, String title, String message, Boolean status) {
		Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle(title);
		alertDialogBuilder.setMessage(message);
		alertDialogBuilder.setIcon((status) ? R.drawable.success : R.drawable.about);
		alertDialogBuilder.setPositiveButton("OK", null);
		AlertDialog dialog = alertDialogBuilder.create();
		dialog.show();
	}

}
