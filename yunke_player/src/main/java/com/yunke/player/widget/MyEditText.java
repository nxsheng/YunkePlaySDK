package com.yunke.player.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.yunke.player.util.UIHelper;

@SuppressLint("NewApi") public class MyEditText extends EditText implements
		MenuItem.OnMenuItemClickListener {

	private final Context mContext;

	public MyEditText(Context context) {
		super(context);
		this.mContext = context;
	}

	public MyEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	public MyEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
	}

	@Override
	protected void onCreateContextMenu(ContextMenu menu) {
		super.onCreateContextMenu(menu);
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		return onTextContextMenuItem(item.getItemId());
	}

	@Override
	public boolean onTextContextMenuItem(int id) {

		boolean consumed = super.onTextContextMenuItem(id);
		switch (id) {
		case android.R.id.cut:
//			onTextCut();
			break;
		case android.R.id.paste:
//			onTextPaste();
			break;
		case android.R.id.copy:
//			onTextCopy();
		}
		return consumed;
	}

	public void onTextCut() {
		Toast.makeText(mContext, "Cut!", Toast.LENGTH_SHORT).show();
	}

	public void onTextCopy() {
		Toast.makeText(mContext, "Copy!", Toast.LENGTH_SHORT).show();
	}

	public void onTextPaste() {
		Toast.makeText(mContext, "Paste!", Toast.LENGTH_SHORT).show();
	}
}
