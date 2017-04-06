package com.smspark.smsgatewayprototype.objects;

import java.util.ArrayList;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.smspark.smsgatewayprototype.R;

public class HTMLListBaseAdapter extends BaseAdapter{

	private LayoutInflater mInflater;
	private ArrayList<String> results;

	public HTMLListBaseAdapter(Context context, ArrayList<String> results) {
		this.results = results;	
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return results.size();
	}

	@Override
	public Object getItem(int position) {
		return results.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public ArrayList<String> getResults(){
		return results;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.html_list_display, null);
			holder = new ViewHolder();
			holder.parameter = (TextView) convertView.findViewById(R.id.htmlItem);	
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.parameter.setText(Html.fromHtml(results.get(position)));	
		return convertView;
	}

	private static class ViewHolder {
		TextView parameter;	
	}

}
