package com.yunke.player.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.yunke.player.api.ApiHttpClient;
import com.yunke.player.api.remote.GN100Image;
import com.yunke.player.bean.WebSocketEnty;
import com.yunke.player.ui.PlayVideoActivity;
import com.yunke.player.util.UIHelper;
import com.yunke.player.widget.RoundImageView;
import com.yunke.player.util.MyEmotionUtil;

import java.util.List;


public class GroupChatAdapter extends RecyclerView.Adapter {
	private final String SIGNAL = "signal";
	private boolean isVisibla = true;

	public static interface OnRecyclerViewListener {
		void onItemClick(View view, int position);
		boolean onItemLongClick(View view, int position);
	}

	private OnRecyclerViewListener onRecyclerViewListener;
	private PlayVideoActivity mPlayVideoActivity;
	private MyEmotionUtil myEmotionUtil;

	public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
		this.onRecyclerViewListener = onRecyclerViewListener;
	}

	public void setContentVisibal(boolean isVisibla) {
		if (this.isVisibla != isVisibla) {
			this.isVisibla = isVisibla;
			notifyDataSetChanged();
		}
	}

	private static final String TAG = GroupChatAdapter.class.getSimpleName();
	private List<WebSocketEnty.ResultEntity> list;

	public GroupChatAdapter(PlayVideoActivity activity, List<WebSocketEnty.ResultEntity> list) {
		this.list = list;

		mPlayVideoActivity = activity;
		myEmotionUtil = new MyEmotionUtil(activity, null, null, null);
		myEmotionUtil.initStaticFaces();
		myEmotionUtil.initEmotionString();
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View view = LayoutInflater.from(viewGroup.getContext()).
				inflate(UIHelper.getResIdByName(mPlayVideoActivity, UIHelper.TYPE_LAYOUT, "yunke_receive_group_message"), null);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		view.setLayoutParams(lp);
		return new GroupChatViewHolder(view);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
		GroupChatViewHolder holder = (GroupChatViewHolder) viewHolder;
		holder.position = i;
		WebSocketEnty.ResultEntity item = list.get(i);

		String content = "";
		String name = "";

		ForegroundColorSpan color1 = new ForegroundColorSpan(Color.parseColor("#f1da3e"));
		ForegroundColorSpan color2 = new ForegroundColorSpan(Color.parseColor("#ffffff"));
		ForegroundColorSpan white = new ForegroundColorSpan(Color.parseColor("#FFFFFF"));

		if (isMyselfByUid(item.getUf())) {
			name = mPlayVideoActivity.getString(UIHelper.getResIdByName(mPlayVideoActivity, UIHelper.TYPE_STRING, "yunke_me")) + " : ";
		} else {
			name = item.getUf_n() + " : ";
		}
		content = item.getC();

		try {
			GN100Image.updateCycleAvatarImageView(mPlayVideoActivity, ApiHttpClient.WS_IMG_URL + item.getUf_t(), holder.avater);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!isVisibla) {
			holder.v_content.setVisibility(View.VISIBLE);
			holder.cover.setVisibility(View.VISIBLE);
			holder.content.setSingleLine(true);

			holder.content.setText(name);

			if (isTeacherByUid(item.getUf())) {
				holder.content.setTextColor(Color.parseColor("#f1da3e"));
			} else {
				holder.content.setTextColor(Color.parseColor("#ffffff"));
			}
		} else {
			holder.v_content.setVisibility(View.GONE);
			holder.cover.setVisibility(View.GONE);
			holder.content.setSingleLine(false);

			if (content != null && !"".equals(content) && !item.getMt().equals("good") && !item.getMt().equals(SIGNAL)) {
				holder.content.setTextColor(Color.parseColor("#ffffff"));
				content = name + content;
			} else {
				content = name;
			}

			try {
				SpannableStringBuilder builder = myEmotionUtil.handler(holder.content, Html.fromHtml(content) + "");
				if (isTeacherByUid(item.getUf())) {
					builder.setSpan(color1, 0, name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				} else {
					builder.setSpan(color2, 0, name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				builder.setSpan(white, name.length(), builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

				holder.content.setText(builder);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public int getItemCount() {
		try {
			return list == null ? 0 : list.size();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	class GroupChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener
	{
		public TextView content;
		public TextView v_content;
		public View cover;
		public RoundImageView avater;
		public int position;

		public GroupChatViewHolder(View itemView) {
			super(itemView);
			content = (TextView) itemView.findViewById(UIHelper.getResIdByName(mPlayVideoActivity, UIHelper.TYPE_ID, "content"));
			v_content = (TextView) itemView.findViewById(UIHelper.getResIdByName(mPlayVideoActivity, UIHelper.TYPE_ID, "v_content"));
			cover = itemView.findViewById(UIHelper.getResIdByName(mPlayVideoActivity, UIHelper.TYPE_ID, "cover"));
			avater = (RoundImageView)itemView.findViewById(UIHelper.getResIdByName(mPlayVideoActivity, UIHelper.TYPE_ID, "chat_head_portrait"));
		}

		@Override
		public void onClick(View v) {
			if (null != onRecyclerViewListener) {
				onRecyclerViewListener.onItemClick(itemView, position);
			}
		}

		@Override
		public boolean onLongClick(View v) {
			if(null != onRecyclerViewListener){
				return onRecyclerViewListener.onItemLongClick(itemView, position);
			}
			return false;
		}
	}

	private boolean isTeacherByUid(int uid) {
		boolean flag = false;

		try {
			if (mPlayVideoActivity.teacher_id.equalsIgnoreCase(uid+"")) {
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return  flag;
	}

	private boolean isMyselfByUid(int uid) {
		boolean flag = false;

		try {
			if (PlayVideoActivity.uid == uid) {
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return  flag;
	}

}