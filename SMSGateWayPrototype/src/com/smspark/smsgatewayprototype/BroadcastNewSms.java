package com.smspark.smsgatewayprototype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.smspark.smsgatewayprototype.objects.HTMLListBaseAdapter;
import com.smspark.smsgatewayprototype.objects.Result;
import com.smspark.smsgatewayprototype.objects.SMS;
import com.smspark.smsgatewayprototype.service.ServiceListner;
import com.smspark.smsgatewayprototype.service.SyncService;

public class BroadcastNewSms extends Activity {

	private HTMLListBaseAdapter adapter;
	private boolean STOP = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// remove keyboard when starting
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		final ListView lv = (ListView) findViewById(R.id.sm_list_view);
		adapter = new HTMLListBaseAdapter(this, new ArrayList<String>());
		lv.setAdapter(adapter);

		EditText edtxtusername = (EditText) findViewById(R.id.tfip);
		edtxtusername.setText("http://channelcert.com/smspark");

		startListner();
		
		
	}

	private void startListner() {

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {

				STOP = false;

				MessageQueue messageQueue = MessageQueue.getInstance();
				BlockingQueue<SMS> queue = messageQueue.getQueue();

				final ListView listView = (ListView) BroadcastNewSms.this
						.findViewById(R.id.sm_list_view);
				final HTMLListBaseAdapter adapter = ((HTMLListBaseAdapter) listView
						.getAdapter());

				adapter.getResults().add("Running");
				adapter.notifyDataSetChanged();
				try {
					SMS msg;
					// consuming messages until exit message is received
					while ((msg = queue.take()) != null && STOP == false) {
						final String m = msg.number + ": " + msg.message;
						addActivity(msg);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								adapter.getResults().add(m);
								adapter.notifyDataSetChanged();

							}
						});
						Thread.sleep(10);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

		t.start();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		STOP = true;
	}

	private void addActivity(final SMS sms) {

		try {
			String[] s = sms.message.split(" ");
			if (s.length < 2) {
				return;
			}

			StringBuilder sb = new StringBuilder();
			for (int i = 2; i < s.length; i++) {
				String string = s[i];
				sb.append(string);
			}

			HashMap<String, String> params = new HashMap<String, String>();
			params.put("m", "addActivity");
			params.put("SMS_CODE", s[0]);
			params.put("HOURS", s[1]);
			params.put("PLATE", sb.toString());
			params.put("PHONE", sms.number);
			
			Log.d("GateWay", "ok");
			// connect
			final SyncService service = new SyncService(params,
					BroadcastNewSms.this, false);

			service.registerServiceListner(new ServiceListner() {
				@Override
				public void update(Object result) {
					service.removeServiceListner(this);
					try {
						if ("ERROR".contains(result.toString())) {
							return;
						}
						Gson g = new Gson();
						Result r = g.fromJson(result.toString(),
								Result.class);
						if(r.valid){
							sendReply(r,sms.number);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			EditText edtxtusername = (EditText) findViewById(R.id.tfip);
			String url = edtxtusername.getText().toString().trim() + "/Admin/services/park_info.php";
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				service.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
			} else {
				service.execute(url);
			}
		} catch (Exception e) {
			e.printStackTrace();
			//showAlertDialog(this, "Error Connecting", e.getMessage(), false);
			return;
		}
	}

	protected void sendReply(Result r, String number) {
		try {
			SmsManager sms = SmsManager.getDefault();
			sms.sendTextMessage(number, null, "You can now park until: " + ((r.endTime != null)? r.endTime : ""), null, null);
			
		} catch (Exception e) {
			// TODO: handle exception
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
}
