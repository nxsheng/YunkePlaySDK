package com.yunke.player.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.yunke.player.util.UIHelper;

import java.io.IOException;
import java.util.List;

public class FaceGVAdapter extends BaseAdapter {
	private static final String TAG = "FaceGVAdapter";
	private List<String> list;
	private Context mContext;

	public FaceGVAdapter(List<String> list, Context mContext) {
		super();
		this.list = list;
		this.mContext = mContext;
	}

	public void clear() {
		this.mContext = null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHodler hodler;
		if (convertView == null) {
			hodler = new ViewHodler();
			convertView = LayoutInflater.from(mContext).inflate(UIHelper.getResIdByName(mContext, UIHelper.TYPE_LAYOUT, "yunke_face_image"), null);
			hodler.iv = (ImageView) convertView.findViewById(UIHelper.getResIdByName(mContext, UIHelper.TYPE_ID, "face_img"));
			hodler.tv = (TextView) convertView.findViewById(UIHelper.getResIdByName(mContext, UIHelper.TYPE_ID, "face_text"));
			convertView.setTag(hodler);
		} else {
			hodler = (ViewHodler) convertView.getTag();
		}
		try {
			Bitmap mBitmap = BitmapFactory.decodeStream(mContext.getAssets().open("face/" + list.get(position)));
			hodler.iv.setImageBitmap(mBitmap);
		} catch (IOException e) {
			e.printStackTrace();
		}
		hodler.tv.setText("face/" + list.get(position));

		return convertView;
	}

	class ViewHodler {
		ImageView iv;
		TextView tv;
	}
}
