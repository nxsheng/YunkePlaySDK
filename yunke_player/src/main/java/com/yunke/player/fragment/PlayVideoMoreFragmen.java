package com.yunke.player.fragment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;


import com.yunke.player.bean.PlayPlanInfoResult;
import com.yunke.player.util.UIHelper;
import com.yunke.player.widget.NoScrollGridView;
import com.yunke.player.base.CommonFragment;
import com.yunke.player.ui.PlayVideoActivity;
import com.yunke.player.util.StringUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PlayVideoMoreFragmen extends CommonFragment {

    private RadioButton resolution_source;
    private RadioButton resolution_flow;
    private NoScrollGridView line_gridview;

    private PlayVideoActivity mActivity;
    private PlayPlanInfoResult mInfo;
    private GVAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return UIHelper.getResIdByName(getContext(), UIHelper.TYPE_LAYOUT, "yunke_fragment_play_video_more");
    }

    @Override
    public void initView(View view) {
        resolution_source = (RadioButton)view.findViewById(UIHelper.getResIdByName(getContext(), UIHelper.TYPE_ID, "resolution_source"));
        resolution_flow = (RadioButton)view.findViewById(UIHelper.getResIdByName(getContext(), UIHelper.TYPE_ID, "resolution_flow"));
        line_gridview = (NoScrollGridView)view.findViewById(UIHelper.getResIdByName(getContext(), UIHelper.TYPE_ID, "line_gridview"));

        mActivity = (PlayVideoActivity) getActivity();
        mInfo = mActivity.getPlayPlanInfo();

        // 分辨率
        setResolutionUI(view);

        // 线路选择
        if (null == mAdapter) {
            mAdapter = new GVAdapter(mActivity);
        }
        line_gridview.setAdapter(mAdapter);
    }

    private void setResolutionUI(View view) {
        // 根据播放信息来显示分辨率
        if ((null != mActivity.mStreamList) && (mActivity.mStreamList.size() > 0)) {
            resolution_source.setVisibility(View.VISIBLE);
            resolution_source.setText(mActivity.mStreamList.get(0).name);

            if (mActivity.mStreamList.size() > 1) {
                resolution_flow.setVisibility(View.VISIBLE);
                resolution_flow.setText(mActivity.mStreamList.get(1).name);
            } else {
                resolution_flow.setVisibility(View.GONE);
            }

            if (0 == mActivity.mCurrentDefinitionIdx) {
                resolution_source.setChecked(true);
            } else {
                resolution_flow.setChecked(true);
            }

            // 选择原画
            resolution_source.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (true == isChecked) {
                        mActivity.handleDefinitionOptionStatus();
                        mActivity.setFloatLayerPlayVidoeMore(false);
                    }
                }
            });

            // 选择流畅
            resolution_flow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (true == isChecked) {
                        mActivity.handleDefinitionOptionStatus();
                        mActivity.setFloatLayerPlayVidoeMore(false);
                    }
                }
            });
        }
    }

    static class GVHolder {
        private RadioButton list_item_line_select_cb;

        public GVHolder(Context context, View view) {
            list_item_line_select_cb = (RadioButton) view.findViewById(UIHelper.getResIdByName(context, UIHelper.TYPE_ID, "list_item_line_select_cb"));
        }
    }

    private class GVAdapter extends BaseAdapter {
        private Context mContext;

        public GVAdapter(Context context) {
            this.mContext=context;
        }

        @Override
        public int getCount() {
            int count = 0;
            if (mActivity.isLiveBroadcast) {
                count = mActivity.cdnRtmpList.size();
            } else {
                count = mActivity.cdnHlsList.size();
            }
            return count;
        }

        @Override
        public Object getItem(int position) {
            Object item = null;
            if (mActivity.isLiveBroadcast) {
                item = mActivity.cdnRtmpList.get(position);
            } else {
                item = mActivity.cdnHlsList.get(position);
            }

            return item;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            //定义一个ImageView,显示在GridView里
            GVHolder holder;
            if(convertView==null){
                convertView = View.inflate(mContext, UIHelper.getResIdByName(getContext(), UIHelper.TYPE_LAYOUT, "yunke_list_item_play_video_line_select"), null);
                holder = new GVHolder(getContext(), convertView);
                if (0 == position) {
                    holder.list_item_line_select_cb.setText("主线");
                } else {
                    holder.list_item_line_select_cb.setText("线路" + StringUtil.foematInteger(position+1));
                }
                convertView.setTag(holder);
            }else{
                holder = (GVHolder) convertView.getTag();
            }
            if (mActivity.mCurrentCdnLineIdx == position) {
                holder.list_item_line_select_cb.setChecked(true);
            } else {
                holder.list_item_line_select_cb.setChecked(false);
            }

            holder.list_item_line_select_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mActivity.switchVideoLine(position);

                        notifyDataSetInvalidated();

                        mActivity.setFloatLayerPlayVidoeMore(false);
                    }
                }
            });
            return convertView;
        }
    }
}
