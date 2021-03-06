package com.smsparking.parkman.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.smsparking.parkman.R;
import com.smsparking.parkmanobjects.Site;

public class SiteListBaseAdapter extends BaseAdapter implements Filterable {
	private static List<Site> siteList;
	private List<Site> allSiteList;

	private LayoutInflater mInflater;
	private SiteFilter siteFilter;

	public SiteListBaseAdapter(Context context, List<Site> results) {
		siteList = results;
		allSiteList = results;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return siteList.size();
	}

	@Override
	public Object getItem(int position) {
		return siteList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void clearAllLists() {
		allSiteList.clear();
		allSiteList.clear();
	}

	public void addSiteList(ArrayList<Site> results) {		
		clearAllLists();				
		siteList = results;
		allSiteList = results;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.site_item, null);
			holder = new ViewHolder();
			holder.parameter = (TextView) convertView.findViewById(R.id.siteName);
			holder.paramValue = (TextView) convertView.findViewById(R.id.siteCode);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.parameter.setText(siteList.get(position).getName());
		holder.paramValue.setText(siteList.get(position).getAddress());

		return convertView;
	}

	private static class ViewHolder {
		TextView parameter;
		TextView paramValue;
	}

	@Override
	public Filter getFilter() {
		if (siteFilter == null)
			siteFilter = new SiteFilter();

		return siteFilter;
	}

	@SuppressLint("DefaultLocale")
	private class SiteFilter extends Filter {

		@SuppressLint("DefaultLocale")
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {

			FilterResults results = new FilterResults();
			// Filter logic
			if (constraint == null || constraint.length() == 0) {
				// No filter implemented we return all the list
				results.values = allSiteList;
				results.count = allSiteList.size();
			} else {
				// Perform filtering
				List<Site> _siteList = new ArrayList<Site>();

				for (Site s : siteList) {
					if (s.getName().toUpperCase().startsWith(constraint.toString().toUpperCase())) {
						_siteList.add(s);		
					}
				}

				results.values = _siteList;
				results.count = _siteList.size();

			}
			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			if (results.count == 0) {
				notifyDataSetInvalidated();
			} else {
				siteList = (List<Site>) results.values;
				notifyDataSetChanged();
			}

		}

	}
}