package com.bhxx.xs_family.adapter;

import java.util.HashMap;
import java.util.List;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bhxx.xs_family.R;

/**
 * 
 * 项目名称：xs 类名称：PopWindowAdapter 创建人：许添明 创建时间：2016年9月11日 下午3:48:06
 * 
 * @version
 * 
 */

public class PopWindowAdapter extends BaseAdapter {
	private Context context;
	private List<String> data;

	public PopWindowAdapter(Context context, List<String> data) {
		this.context = context;
		this.data = data;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.popwindow_item, null);
			viewHolder.month = (TextView) convertView.findViewById(R.id.month);
			convertView.setTag(viewHolder);
		} else {

			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.month.setText(data.get(position).toString());
		return convertView;
	}

	class ViewHolder {
		private TextView month;

	}
}
