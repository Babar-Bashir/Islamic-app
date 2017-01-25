package com.pribno.islam.adapter;

import com.pribno.islam.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class PrayerListAdapter extends BaseAdapter{
	
	LayoutInflater inflater;
	private String[] prayerName;
	private String[] startTime;
	private  String[] endTime;
	private  Integer[] Imv;
	private boolean[] alarmToggleBtn;
	private boolean[] alarmVisible = {true,false,true,true,false,true,true};
	public PrayerListAdapter(Context context, Integer[] Imv, String[] prayerName,String[] startTime,String[] endTime,boolean[] alarmToggleBtn) {
		this.prayerName=prayerName;
		this.startTime=startTime;
		this.endTime=endTime;
		this.Imv=Imv;
		this.alarmToggleBtn=alarmToggleBtn;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return prayerName.length;

	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_row, parent, false);
			viewHolder = new ViewHolder();

			viewHolder.prayerName = (TextView) convertView.findViewById(R.id.prayer_name);
			viewHolder.startTime = (TextView) convertView.findViewById(R.id.start_time_tv);
			viewHolder.endTime = (TextView) convertView.findViewById(R.id.end_time_tv);
			viewHolder.prayerImage = (ImageView) convertView.findViewById(R.id.prayer_image);
//			viewHolder.alarm_ToggleBtn = (ToggleButton) convertView.findViewById(R.id.alarm_toggleBtn);
			convertView.setTag(viewHolder);
		}
		else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.prayerName.setText(prayerName[position]);
		viewHolder.startTime.setText(startTime[position]);
		viewHolder.endTime.setText(endTime[position]);
		viewHolder.prayerImage.setImageResource(Imv[position]);
//		if(alarmVisible[position] == false){
//			viewHolder.alarm_ToggleBtn.setVisibility(View.INVISIBLE);
//		viewHolder.alarm_ToggleBtn.setChecked(alarmToggleBtn[position]);
//		}
		return convertView;
	}
	private class ViewHolder{
		TextView prayerName,startTime,endTime;
		ImageView prayerImage;
//		ToggleButton alarm_ToggleBtn;
	}



}