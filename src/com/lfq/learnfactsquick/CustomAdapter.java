package com.lfq.learnfactsquick;

import java.util.List;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter {

	private final Activity context;
	private final List<String> myList;
	private final List<Boolean> is_exists;
	private TextView tv;
	private ImageView iv;

	public CustomAdapter(Activity context, List<String> list,
			List<Boolean> is_exists) {
		super();
		this.context = context;
		this.myList = list;
		this.is_exists = is_exists;
		System.out.println("CUstome Adapter created!!!!!");

	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		View rowView = null;
		if (!myList.get(position).equals("")) {			
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.dropdown_item, null, true);
			tv = (TextView) rowView.findViewById(R.id.list_item_tv);
			iv = (ImageView) rowView.findViewById(R.id.list_item_iv);
			tv.setText(myList.get(position));
			if (is_exists.get(position)) {
				iv.setVisibility(View.VISIBLE);
			} else {
				iv.setVisibility(View.GONE);
			}
			System.out.println("item text="+myList.get(position)+", is_exists="+is_exists.get(position));
		}
		else{
			System.out.println("rowView=''");
		}
		return rowView;
	}

	public void setImage() {

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return myList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
}
