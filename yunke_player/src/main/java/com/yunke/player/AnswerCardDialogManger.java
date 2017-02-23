package com.yunke.player;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import com.yunke.player.bean.StudentAnswerEnty;
import com.yunke.player.util.ToastUtil;
import com.yunke.player.api.ApiHttpClient;
import com.yunke.player.bean.GetGoodDataBean;
import com.yunke.player.bean.TeacherAnswerEnty;
import com.yunke.player.bean.TeacherQuestionEnty;
import com.yunke.player.bean.WebSocketEnty;
import com.yunke.player.ui.PlayVideoActivity;
import com.yunke.player.util.PxToDp;
import com.yunke.player.util.SimpleTextWatcher;
import com.yunke.player.util.StringUtil;
import com.yunke.player.util.TLog;
import com.yunke.player.util.UIHelper;

import java.util.ArrayList;
import java.util.List;
import uk.co.senab.photoview.PhotoView;

import static com.yunke.player.R.dimen.yunke_answercard_height;

/**
 * ============================================================
 * 版 权 ：北京高能壹佰教育科技有限公司
 * 作 者 ：臧鹏飞
 * 邮 箱 ：zangpengfei@talkweb.com.cn
 * 版 本 ：1.1
 * 创建日期 ：2016/9/23 14:11
 * 描 述 ：
 * 修订历史 ：
 * ============================================================
 **/
public class AnswerCardDialogManger {

    //    private static AnswerCardDialogManger answerCardDialogManger;
    private AlertDialog mAnswerCardDialog;
    private ImageView ivAnswerCard;
    private String TAG = AnswerCardDialogManger.class.getCanonicalName();
    private final String SIGNAL = "signal";

    /**
     * 学生回答
     */
    private final int STUDENT_ANSWER = 1018;
    /**
     * 多选
     */
    private final String MULTI = "2";
    /**
     * 单选
     */
    private final String SINGLE = "1";
    /**
     * 判断
     */
    private final String JUDGE = "3";
    /**
     * 填空
     */
    private final String COMPLETION = "4";
    /**
     * 服务端获取的abcd四个选项
     */
    private TeacherQuestionEnty mTeacherQuestionEnty;
    /**
     * 服务端返回来的答案
     */
    private boolean answerHadSelected;
    private String exam_id;
    /**
     * 已选答案，要发送的答案
     */
    private String sureAnswer;
    /**
     * 选中的答案
     */
    private List<String> selectenAnswer = new ArrayList<>();
    private final String CLEAR = "";//清空
    private boolean ifCanCommitAnswerFlag;
    private MyAdapter adapter;
    private TextView ansertType;
    private TextView textQuestion;
    private PhotoView imgQuestion;
    private FrameLayout fl_img;
    private EditText tiankong;
    private LinearLayout ll_true_answer;
    private View view;
    private Activity mActivity;

    private View contentView;
    private ListView answerSelectedList;
    private TextView commit;
    private Chronometer tvTime;
    private RelativeLayout rl_rank_layout;
    private FrameLayout cancel;

    private LinearLayout rl_answer_result;
    private LinearLayout rl_rank_loading;

    private TextView rank_name_1;
    private TextView rank_name_2;
    private TextView rank_name_3;
    private TextView tv_my_rank;
    private LinearLayout ll_rank_1;
    private LinearLayout ll_rank_2;
    private LinearLayout ll_rank_3;
    private LinearLayout ll_rank_main;

    private TextView my_name;
    private TextView tv_true_wrong;
    private TextView tv_true_answer;
    private TextView tv_status;
    private TextView tv_my_answer;
    private TextView tv_all_wrong;
    private TextView tv_rank;//----排行榜-----
    private LinearLayout ll_rank;
    private ImageView iv_answer_dsc;
    private ImageView line;
    private TextView tv_rank_1;
    private TextView tv_rank_2;
    private TextView tv_rank_3;

    public AnswerCardDialogManger(ImageView ivAnswerCard) {
        this.ivAnswerCard = ivAnswerCard;

    }

    private void initAnswerCardData(PlayVideoActivity activity) {
        mActivity = activity;
        contentView = View.inflate(activity, UIHelper.getResIdByName(mActivity, UIHelper.TYPE_LAYOUT, "yunke_show_answer_card_dialog"), null);
        answerSelectedList = (ListView) contentView.findViewById(UIHelper.getResIdByName(mActivity, UIHelper.TYPE_ID, "answer_selected"));
        commit = (TextView) contentView.findViewById(UIHelper.getResIdByName(mActivity, UIHelper.TYPE_ID, "bt_commit"));
        tvTime = (Chronometer) contentView.findViewById(UIHelper.getResIdByName(mActivity, UIHelper.TYPE_ID, "tv_time"));
        rl_rank_layout = (RelativeLayout) contentView.findViewById(UIHelper.getResIdByName(mActivity, UIHelper.TYPE_ID, "rl_rank_layout"));//排行榜
        cancel = (FrameLayout) contentView.findViewById(UIHelper.getResIdByName(mActivity, UIHelper.TYPE_ID, "cancel"));

        rl_answer_result = (LinearLayout) contentView.findViewById(UIHelper.getResIdByName(mActivity, UIHelper.TYPE_ID, "rl_answer_result"));
        rl_rank_loading = (LinearLayout) contentView.findViewById(UIHelper.getResIdByName(mActivity, UIHelper.TYPE_ID, "rl_rank_loading"));
        rank_name_1 = (TextView) contentView.findViewById(UIHelper.getResIdByName(mActivity, UIHelper.TYPE_ID, "rank_name_1"));
        rank_name_2 = (TextView) contentView.findViewById(UIHelper.getResIdByName(mActivity, UIHelper.TYPE_ID, "rank_name_2"));
        rank_name_3 = (TextView) contentView.findViewById(UIHelper.getResIdByName(mActivity, UIHelper.TYPE_ID, "rank_name_3"));
        tv_my_rank = (TextView) contentView.findViewById(UIHelper.getResIdByName(mActivity, UIHelper.TYPE_ID, "tv_my_rank"));
        ll_rank_1 = (LinearLayout) contentView.findViewById(UIHelper.getResIdByName(mActivity, UIHelper.TYPE_ID, "ll_rank_1"));
        ll_rank_2 = (LinearLayout) contentView.findViewById(UIHelper.getResIdByName(mActivity, UIHelper.TYPE_ID, "ll_rank_2"));
        ll_rank_3 = (LinearLayout) contentView.findViewById(UIHelper.getResIdByName(mActivity, UIHelper.TYPE_ID, "ll_rank_3"));
        ll_rank_main = (LinearLayout) contentView.findViewById(UIHelper.getResIdByName(mActivity, UIHelper.TYPE_ID, "ll_rank_main"));
        my_name = (TextView) contentView.findViewById(UIHelper.getResIdByName(mActivity, UIHelper.TYPE_ID, "my_name"));
        tv_status = (TextView) contentView.findViewById(UIHelper.getResIdByName(mActivity, UIHelper.TYPE_ID, "tv_status"));
        tv_my_answer = (TextView) contentView.findViewById(UIHelper.getResIdByName(mActivity, UIHelper.TYPE_ID, "tv_my_answer"));
        tv_true_wrong = (TextView) contentView.findViewById(UIHelper.getResIdByName(mActivity, UIHelper.TYPE_ID, "tv_true_wrong"));
        tv_true_answer = (TextView) contentView.findViewById(UIHelper.getResIdByName(mActivity, UIHelper.TYPE_ID, "tv_true_answer"));
        tv_rank = (TextView) contentView.findViewById(UIHelper.getResIdByName(mActivity, UIHelper.TYPE_ID, "tv_rank"));
        tv_all_wrong = (TextView) contentView.findViewById(UIHelper.getResIdByName(mActivity, UIHelper.TYPE_ID, "tv_all_wrong"));
        ll_rank = (LinearLayout) contentView.findViewById(UIHelper.getResIdByName(mActivity, UIHelper.TYPE_ID, "ll_rank"));
        iv_answer_dsc = (ImageView) contentView.findViewById(UIHelper.getResIdByName(mActivity, UIHelper.TYPE_ID, "iv_answer_dsc"));
        line = (ImageView) contentView.findViewById(UIHelper.getResIdByName(mActivity, UIHelper.TYPE_ID, "line"));
        tv_rank_1 = (TextView) contentView.findViewById(UIHelper.getResIdByName(mActivity, UIHelper.TYPE_ID, "tv_rank_1"));
        tv_rank_2 = (TextView) contentView.findViewById(UIHelper.getResIdByName(mActivity, UIHelper.TYPE_ID, "tv_rank_2"));
        tv_rank_3 = (TextView) contentView.findViewById(UIHelper.getResIdByName(mActivity, UIHelper.TYPE_ID, "tv_rank_3"));

        addHeader(activity);
        setAnswerListOnItemClickListener(activity);

        if (tiankong != null) {
            tiankong.addTextChangedListener(mTextWatcher);
        }
    }

    private void showRankList() {
        tv_rank.setVisibility(View.VISIBLE);
        ll_rank.setVisibility(View.VISIBLE);
    }

    private void hideRankList() {
        tv_rank.setVisibility(View.GONE);
        ll_rank.setVisibility(View.GONE);
    }

    /**
     * 选择答案
     */
    private void setAnswerListOnItemClickListener(final PlayVideoActivity activity) {
        answerSelectedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onClickItem(i, activity);
            }
        });
    }

    private void onClickItem(int i, PlayVideoActivity activity) {
        if (answerHadSelected) {
            return;
        }
        //单选
        if (mTeacherQuestionEnty.getType().equals(SINGLE) || mTeacherQuestionEnty.getType().equals(JUDGE)) {
            if (answerIsSelected(i - 1)) {
                selectenAnswer.clear();
                fillUI(activity);
            } else {
                selectenAnswer.clear();
                selectenAnswer.add(mTeacherQuestionEnty.getAnswer().get(i - 1).split(" ")[0].toUpperCase());
                fillUI(activity);
            }

        } else if (mTeacherQuestionEnty.getType().equals(MULTI)) {

            //首先判断有没有选中  1，选中 移除 2，移除 选中
            if (answerIsSelected(i - 1)) {
                selectenAnswer.remove(mTeacherQuestionEnty.getAnswer().get(i - 1).split(" ")[0].toUpperCase());
                fillUI(activity);
            } else {
                selectenAnswer.add(mTeacherQuestionEnty.getAnswer().get(i - 1).split(" ")[0].toUpperCase());
                fillUI(activity);
            }
        }
    }

    /**
     * 判断该选项是否被选中
     *
     * @param position
     * @return
     */
    private boolean answerIsSelected(int position) {
        if (selectenAnswer.contains(mTeacherQuestionEnty.getAnswer().get(position).split(" ")[0].toUpperCase())) {
            return true;
        }
        return false;
    }

    private void addHeader(PlayVideoActivity activity) {
        if (view == null) {
            view = View.inflate(activity, UIHelper.getResIdByName(activity, UIHelper.TYPE_LAYOUT, "yunke_answer_tital"), null);
            ansertType = (TextView) view.findViewById(UIHelper.getResIdByName(activity, UIHelper.TYPE_ID, "tv_ansert_type"));
            textQuestion = (TextView) view.findViewById(UIHelper.getResIdByName(activity, UIHelper.TYPE_ID, "text_question"));
            imgQuestion = (PhotoView) view.findViewById(UIHelper.getResIdByName(activity, UIHelper.TYPE_ID, "img_question"));
            fl_img = (FrameLayout) view.findViewById(UIHelper.getResIdByName(activity, UIHelper.TYPE_ID, "fl_img"));
            tiankong = (EditText) view.findViewById(UIHelper.getResIdByName(activity, UIHelper.TYPE_ID, "tiankong"));
            ll_true_answer = (LinearLayout) view.findViewById(UIHelper.getResIdByName(activity, UIHelper.TYPE_ID, "ll_true_answer"));

            answerSelectedList.addHeaderView(view);
        }
    }

    private void fillUI(PlayVideoActivity activity) {

        if (mTeacherQuestionEnty == null) {
            return;
        }

        isJudgeType = false;
        if (mTeacherQuestionEnty.getType().equals(SINGLE)) {
            ansertType.setText("【单选题】");
        } else if (mTeacherQuestionEnty.getType().equals(MULTI)) {
            ansertType.setText("【多选题】");
        } else if (mTeacherQuestionEnty.getType().equals(JUDGE)) {
            isJudgeType = true;
            ansertType.setText("【判断题】");
        } else if (mTeacherQuestionEnty.getType().equals(COMPLETION)) {
            ansertType.setText("【填空题】");
        }

        if (!android.text.TextUtils.isEmpty(mTeacherQuestionEnty.getText()) && mTeacherQuestionEnty.getText() != null) {
            textQuestion.setVisibility(View.VISIBLE);
            fl_img.setVisibility(View.GONE);
            tiankong.setVisibility(View.GONE);
            textQuestion.setText(mTeacherQuestionEnty.getText());
            textQuestion.setText(mTeacherQuestionEnty.getText());

        } else if (!android.text.TextUtils.isEmpty(mTeacherQuestionEnty.getImg())) {
            fl_img.setVisibility(View.VISIBLE);
            textQuestion.setVisibility(View.GONE);
            tiankong.setVisibility(View.GONE);

            try {
                DisplayImageOptions defCourseOptions = new DisplayImageOptions.Builder()
                        .showImageOnLoading(UIHelper.getResIdByName(activity, UIHelper.TYPE_DRAWABLE, "yunke_default_course_icon")) // 设置图片在下载期间显示的图片
                        .showImageForEmptyUri(UIHelper.getResIdByName(activity, UIHelper.TYPE_DRAWABLE, "yunke_default_course_icon")) // 设置图片Uri为空或是错误的时候显示的图片
                        .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                        .cacheOnDisk(true)// 设置下载的图片是否缓存在SD卡中
                        .build(); // 构建完成
                ImageLoader.getInstance().displayImage(ApiHttpClient.WS_IMG_URL + mTeacherQuestionEnty.getImg(),
                        imgQuestion, defCourseOptions);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (android.text.TextUtils.isEmpty(mTeacherQuestionEnty.getText()) && mTeacherQuestionEnty.getType().equals(COMPLETION)) {
            tiankong.setVisibility(View.VISIBLE);
            textQuestion.setVisibility(View.GONE);
            fl_img.setVisibility(View.GONE);
        } else {
            tiankong.setVisibility(View.GONE);
            textQuestion.setVisibility(View.GONE);
            fl_img.setVisibility(View.GONE);
        }

        if (adapter == null) {
            adapter = new MyAdapter(activity);
            answerSelectedList.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

    }

    private final int judgeType = 1;
    private final int otherType = 0;
    private boolean isJudgeType = false;

    private class MyAdapter extends BaseAdapter {
        private PlayVideoActivity activity;

        public MyAdapter(PlayVideoActivity activity) {
            this.activity = activity;
        }

        @Override
        public int getCount() {
            if (mTeacherQuestionEnty.getType().equals(JUDGE)) {
                return 1;
            }
            return mTeacherQuestionEnty.getAnswer().size();
        }

        @Override
        public int getItemViewType(int position) {
            if (mTeacherQuestionEnty.getType().equals(JUDGE)) {
                return judgeType;
            }
            return otherType;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            int viewType = getItemViewType(i);

            if (viewType == judgeType) {

                ViewHolderJudge mViewHolderJudge = null;
                if (view == null) {
                    view = View.inflate(activity, UIHelper.getResIdByName(activity, UIHelper.TYPE_LAYOUT, "yunke_list_item_answer_judge"), null);
                    mViewHolderJudge = new ViewHolderJudge(view, activity);
                    view.setTag(mViewHolderJudge);
                } else {
                    mViewHolderJudge = (ViewHolderJudge) view.getTag();
                }

                if (answerIsSelected(0)) {
                    mViewHolderJudge.ib_judge_right.setBackgroundResource(UIHelper.getResIdByName(activity, UIHelper.TYPE_DRAWABLE, "yunke_answer_judge_right_1"));
                    mViewHolderJudge.ib_judge_wrong.setBackgroundResource(UIHelper.getResIdByName(activity, UIHelper.TYPE_DRAWABLE, "yunke_answer_judge_wrong_2"));
                } else if (answerIsSelected(1)) {
                    mViewHolderJudge.ib_judge_right.setBackgroundResource(UIHelper.getResIdByName(activity, UIHelper.TYPE_DRAWABLE, "yunke_answer_judge_right_2"));
                    mViewHolderJudge.ib_judge_wrong.setBackgroundResource(UIHelper.getResIdByName(activity, UIHelper.TYPE_DRAWABLE, "yunke_answer_judge_wrong_1"));
                } else {
                    mViewHolderJudge.ib_judge_right.setBackgroundResource(UIHelper.getResIdByName(activity, UIHelper.TYPE_DRAWABLE, "yunke_answer_judge_right_2"));
                    mViewHolderJudge.ib_judge_wrong.setBackgroundResource(UIHelper.getResIdByName(activity, UIHelper.TYPE_DRAWABLE, "yunke_answer_judge_wrong_2"));
                }

                mViewHolderJudge.judge_right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickItem(1, activity);
                    }
                });

                mViewHolderJudge.judge_wrong.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickItem(2, activity);
                    }
                });
            } else {
                ViewHolder mViewHolder = null;
                if (view == null) {
                    view = View.inflate(activity, UIHelper.getResIdByName(activity, UIHelper.TYPE_LAYOUT, "yunke_list_item_teacher_answer_selected"), null);
                    mViewHolder = new ViewHolder(view, activity);
                    view.setTag(mViewHolder);
                } else {
                    mViewHolder = (ViewHolder) view.getTag();
                }
                //选中
                if (answerIsSelected(i)) {
                    mViewHolder.selected.setTextColor(Color.parseColor("#FFA500"));
                    mViewHolder.trueAnswer.setBackgroundResource(UIHelper.getResIdByName(activity, UIHelper.TYPE_DRAWABLE, "yunke_answer_selcet"));
                } else {
                    mViewHolder.selected.setTextColor(Color.parseColor("#54371b"));
                    mViewHolder.trueAnswer.setBackgroundResource(UIHelper.getResIdByName(activity, UIHelper.TYPE_DRAWABLE, "yunke_answer_opption_bg"));
                }
                mViewHolder.selected.setText(mTeacherQuestionEnty.getAnswer().get(i).substring(1));
                mViewHolder.option.setText(mTeacherQuestionEnty.getType().equals(JUDGE) ? "" : mTeacherQuestionEnty.getAnswer().get(i).split(" ")[0].toUpperCase());
            }

            return view;
        }
    }

    class ViewHolder {
        public TextView selected;
        public ImageView trueAnswer;
        public TextView option;

        public ViewHolder(View view, Context context) {
            selected = (TextView)view.findViewById(UIHelper.getResIdByName(context, UIHelper.TYPE_ID, "selected"));
            trueAnswer = (ImageView)view.findViewById(UIHelper.getResIdByName(context, UIHelper.TYPE_ID, "tv_true_answer"));
            option = (TextView)view.findViewById(UIHelper.getResIdByName(context, UIHelper.TYPE_ID, "option"));
        }
    }

    class ViewHolderJudge {
        public FrameLayout judge_right;
        public FrameLayout judge_wrong;
        public ImageView ib_judge_right;
        public ImageView ib_judge_wrong;

        public ViewHolderJudge(View view, Context context) {
            judge_right = (FrameLayout)view.findViewById(UIHelper.getResIdByName(context, UIHelper.TYPE_ID, "judge_right"));
            judge_wrong = (FrameLayout)view.findViewById(UIHelper.getResIdByName(context, UIHelper.TYPE_ID, "judge_wrong"));
            ib_judge_right = (ImageView)view.findViewById(UIHelper.getResIdByName(context, UIHelper.TYPE_ID, "ib_judge_right"));
            ib_judge_wrong = (ImageView)view.findViewById(UIHelper.getResIdByName(context, UIHelper.TYPE_ID, "ib_judge_wrong"));
        }
    }

    /**
     * 提交答案
     */
    private void commitAnswer(PlayVideoActivity activity) {

        if (!activity.isApplyCourse()) {
            ToastUtil.showToast(activity, "请报名后再答题", 0, 0, Gravity.CENTER);
            return;
        }


        if (answerHadSelected) {
            return;
        }

        if (mTeacherQuestionEnty != null && !android.text.TextUtils.isEmpty(tiankong.getText().toString().trim()) && mTeacherQuestionEnty.getType().equals(COMPLETION)) {
            sureAnswer = tiankong.getText().toString().trim();
        }
        if (selectenAnswer.size() == 0 && android.text.TextUtils.isEmpty(sureAnswer)) {
            ToastUtil.showToast(activity, "请选择答案", 0, 0, Gravity.CENTER);
            return;
        }

        tvTime.stop();

        if (!ifCanCommitAnswerFlag) {
            return;
        }

        showHadCommitAnswer(activity);

        answerHadSelected = true;
        if (selectenAnswer.size() > 0) {
            for (int j = 0; j < selectenAnswer.size(); j++) {
                sureAnswer = sureAnswer + selectenAnswer.get(j) + ",";
            }
            if (sureAnswer.endsWith(",")) {
                sureAnswer = sureAnswer.substring(0, sureAnswer.lastIndexOf(","));
            }
        } else {
            selectenAnswer.add(sureAnswer);
            myAnswer("我的答案：" + sureAnswer, 30, activity);
        }

        StudentAnswerEnty mStudentAnswerEnty = new StudentAnswerEnty();
        mStudentAnswerEnty.id = exam_id;
        mStudentAnswerEnty.answer = sureAnswer;
        String body = mStudentAnswerEnty.toJson();
        answerSelectedList.setVisibility(View.GONE);
        rl_rank_layout.setVisibility(View.VISIBLE);
        if (mTeacherQuestionEnty.getType().equals(JUDGE)) {//判断
            if (sureAnswer.equals("A")) {
                tv_my_answer.setText(Html.fromHtml("我的答案：" + "<font color='#c12c20'>正确" + "</font>"));
            } else {
                tv_my_answer.setText(Html.fromHtml("我的答案：" + "<font color='#c12c20'>错误" + "</font>"));
            }
        } else {
            tv_my_answer.setText(Html.fromHtml("我的答案：" + "<font color='#c12c20'>" + sureAnswer + "</font>"));
        }

        MyWebSocketManager.getInstance(activity).sendMessage(SIGNAL, body, STUDENT_ANSWER,0);
        TLog.log(TAG,"发送答案");
        answerSelectedList.setVisibility(View.GONE);
        rl_rank_layout.setVisibility(View.VISIBLE);
        if (mTeacherQuestionEnty.getType().equals(JUDGE)) {//判断
            if (sureAnswer.equals("A")) {
                tv_my_answer.setText(Html.fromHtml("我的答案：" + "<font color='#c12c20'>正确" + "</font>"));
            } else {
                tv_my_answer.setText(Html.fromHtml("我的答案：" + "<font color='#c12c20'>错误" + "</font>"));
            }
        } else {
            tv_my_answer.setText(Html.fromHtml("我的答案：" + "<font color='#c12c20'>" + sureAnswer + "</font>"));
        }
    }

    private void myAnswer(String sureAnswer, int hight, PlayVideoActivity activity) {
        tiankong.setText(sureAnswer);
        tiankong.setTextColor(Color.parseColor("#ffffff"));
//        tiankong.setBackground(null);
        tiankong.setFocusable(false);
        ViewGroup.LayoutParams lp = tiankong.getLayoutParams();
        lp.height = PxToDp.dip2px(activity, hight);
        tiankong.setLayoutParams(lp);
        int padding = PxToDp.dip2px(activity, 6);
        tiankong.setPadding(padding, padding, 0, 0);
    }

    private void showWaitCommitAnswer(PlayVideoActivity activity) {
        commit.setVisibility(View.VISIBLE);
        commit.setTextColor(Color.parseColor("#ffffff"));
        commit.setBackgroundResource(UIHelper.getResIdByName(activity, UIHelper.TYPE_DRAWABLE, "yunke_submit"));
        commit.setText(UIHelper.getResIdByName(activity, UIHelper.TYPE_STRING, "yunke_commit"));
    }


    private void showHadCommitAnswer(PlayVideoActivity activity) {
        commit.setVisibility(View.VISIBLE);
        commit.setBackgroundColor(Color.parseColor("#00000000"));
        commit.setTextColor(Color.parseColor("#c12c20"));
        commit.setText(UIHelper.getResIdByName(activity, UIHelper.TYPE_STRING, "yunke_answer_commited"));
    }

    private void showAnswer() {
        commit.setVisibility(View.INVISIBLE);
    }

    TextWatcher mTextWatcher = new SimpleTextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            super.onTextChanged(s, start, before, count);
            // 长度限制
            if(StringUtil.isContainChinese(s.toString())) {
                tiankong.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
            } else {
                tiankong.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
            }
        }
    };

    /**
     * 显示答题卡
     */
    public void answerCardDialog(final PlayVideoActivity activity) {

        if (mAnswerCardDialog == null) {
            initAnswerCardData(activity);
            mAnswerCardDialog = new AlertDialog.Builder(activity, UIHelper.getResIdByName(activity, UIHelper.TYPE_STYLE, "yunke_theme_transparent")).create();
        }

        if (mHuDongDialog != null && mHuDongDialog.isShowing()) {
            mHuDongDialog.dismiss();
        }

        if (activity.isFinishing() || mAnswerCardDialog.isShowing()) {
            return;
        }

        mAnswerCardDialog.show();
        mAnswerCardDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        mAnswerCardDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mAnswerCardDialog.setContentView(contentView);
        mAnswerCardDialog.setCancelable(false);
        mAnswerCardDialog.setCanceledOnTouchOutside(false);

        WindowManager m = mAnswerCardDialog.getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = mAnswerCardDialog.getWindow().getAttributes();//修改宽度
//        p.width = (int) (d.getWidth()*0.6);
//        p.height = (int) (d.getHeight()*0.7);
        p.width = activity.getResources().getDimensionPixelOffset(UIHelper.getResIdByName(mActivity, UIHelper.TYPE_DIMEN, "yunke_answercard_widht"));
        p.height = activity.getResources().getDimensionPixelOffset(UIHelper.getResIdByName(mActivity, UIHelper.TYPE_DIMEN, "yunke_answercard_height"));

        mAnswerCardDialog.getWindow().setAttributes(p);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAnswerCardDialog.dismiss();
            }
        });

        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //提交
                commitAnswer(activity);
            }
        });
    }

    public void cancelAnswerCardDialog() {
        if (ivAnswerCard.getVisibility() == View.VISIBLE) {
            ivAnswerCard.setVisibility(View.GONE);
        }
        if (mAnswerCardDialog != null && mAnswerCardDialog.isShowing()) {
            mAnswerCardDialog.dismiss();
        }
        if (mHuDongDialog != null && mHuDongDialog.isShowing()) {
            mHuDongDialog.dismiss();
        }
    }

    /**
     * 发送答题卡
     *
     * @param mTeaQueEnty
     */
    public void onSendAnswerCard(TeacherQuestionEnty mTeaQueEnty, PlayVideoActivity activity) {
        ivAnswerCard.setVisibility(View.VISIBLE);
        mAnswerCard.cancel();
        answerCardDialog(activity);
        answerSelectedList.setVisibility(View.VISIBLE);
        rl_rank_layout.setVisibility(View.GONE);
        showLading();
        tv_status.setText("等待老师公布答案...");
        line.setVisibility(View.VISIBLE);


        answerSelectedList.setEnabled(true);
        //开始计时
        tvTime.setBase(SystemClock.elapsedRealtime());
        tvTime.start();

        ifCanCommitAnswerFlag = true;
        showWaitCommitAnswer(activity);
        answerHadSelected = false;
        mTeacherQuestionEnty = mTeaQueEnty;
        sureAnswer = CLEAR;//重置答案
        selectenAnswer.clear();//重置选中状态

        //a b c d e
        if (mTeacherQuestionEnty.getType().equals(COMPLETION)) {
            if (tiankong != null) {
                tiankong.setText("");
                tiankong.setTextColor(Color.parseColor("#000000"));
                tiankong.setFocusableInTouchMode(true);
                ViewGroup.LayoutParams lp = tiankong.getLayoutParams();
                lp.height = PxToDp.dip2px(activity, 76);
                tiankong.setLayoutParams(lp);
                int padding = PxToDp.dip2px(activity, 6);
                tiankong.setPadding(padding, padding, 0, 0);
            }
        } else {
//            answerList = mTeacherQuestionEnty.getAnswer();
        }

        exam_id = mTeacherQuestionEnty.getId();

        if (ll_true_answer != null) {
            ll_true_answer.setVisibility(View.GONE);
        }

        fillUI(activity);
    }

    /**
     * 发送答案
     *
     * @param answerListEnty
     */
    public void onSendAnswer(WebSocketEnty.ResultEntity answerListEnty, PlayVideoActivity activity) {
        ivAnswerCard.setVisibility(View.VISIBLE);
        mAnswerCard.start();
        answerCardDialog(activity);
        answerSelectedList.setVisibility(View.GONE);
        rl_rank_layout.setVisibility(View.VISIBLE);
        line.setVisibility(View.INVISIBLE);

        ifCanCommitAnswerFlag = false;
        showAnswer();

        Gson gson = new Gson();
        TeacherAnswerEnty mTeacherAnswerEnty = gson.fromJson(answerListEnty.getC(), TeacherAnswerEnty.class);
        List<TeacherAnswerEnty.RankEntity> rank = mTeacherAnswerEnty.getRank();
        List<TeacherAnswerEnty.UnanswerEntity> nanswerList = mTeacherAnswerEnty.getUnanswer();

        if (answerRight(rank)) {
            iv_answer_dsc.setBackgroundResource(UIHelper.getResIdByName(activity, UIHelper.TYPE_DRAWABLE, "yunke_answer_right_img"));
            tv_true_wrong.setText("恭喜您，答对了");
        } else if (noAnswer(nanswerList, activity)) {
            iv_answer_dsc.setBackgroundResource(UIHelper.getResIdByName(activity, UIHelper.TYPE_DRAWABLE, "yunke_answer_wrong_img"));
            tvTime.stop();
        } else {
            iv_answer_dsc.setBackgroundResource(UIHelper.getResIdByName(activity, UIHelper.TYPE_DRAWABLE, "yunke_answer_wrong_img"));
            tv_true_wrong.setText("很遗憾,答错了");
        }

        answerSelectedList.setEnabled(false);
        if (isJudgeType) {
            if (mTeacherAnswerEnty.getAnswer().equalsIgnoreCase("A")) {
                tv_true_answer.setText("正确答案：正确");
            } else {
                tv_true_answer.setText("正确答案：错误");
            }
        } else {
            tv_true_answer.setText("正确答案：" + mTeacherAnswerEnty.getAnswer());
        }
        fillUI(rank, activity);
    }

    /**
     * 判断是否回答
     *
     * @param nanswerList
     * @return
     */
    private boolean noAnswer(List<TeacherAnswerEnty.UnanswerEntity> nanswerList, PlayVideoActivity activity) {
        if (!activity.isApplyCourse()) {
            tv_true_wrong.setText("请报名后再答题");
            return true;
        }
        for (int i = 0; i < nanswerList.size(); i++) {
            if (nanswerList.get(i).getId().equals(PlayVideoActivity.uid + "")) {
                tv_true_wrong.setText("很遗憾，时间到了");
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否回答正确
     *
     * @param rank
     * @return
     */
    private boolean answerRight(List<TeacherAnswerEnty.RankEntity> rank) {
        for (int i = 0; i < rank.size(); i++) {
            if (rank.get(i).getId().equals(PlayVideoActivity.uid + "")) {
                return true;
            }
        }
        return false;
    }

    private void fillUI(List<TeacherAnswerEnty.RankEntity> rank, PlayVideoActivity activity) {

        showAnswerResult();
        if (rank == null || rank.size() == 0) {
            tv_all_wrong.setVisibility(View.VISIBLE);
            hideRankList();
            return;
        } else {
            tv_all_wrong.setVisibility(View.GONE);
            showRankList();
            initUI(rank, activity);
        }

    }

    private void showLading() {
        rl_answer_result.setVisibility(View.GONE);
        rl_rank_loading.setVisibility(View.VISIBLE);
    }

    private void showAnswerResult() {
        rl_answer_result.setVisibility(View.VISIBLE);
        rl_rank_loading.setVisibility(View.GONE);
    }

    private void initUI(List<TeacherAnswerEnty.RankEntity> rank, PlayVideoActivity activity) {

        int myRankNum = -1;
        for (int i = 0; i < rank.size(); i++) {
            if (rank.get(i).getId().equals(PlayVideoActivity.uid + "")) {
                myRankNum = i;
            }
        }

        Drawable mRouteOnDraw = activity.getResources().getDrawable(UIHelper.getResIdByName(activity, UIHelper.TYPE_DRAWABLE, "yunke_answer_rank_me"));
        mRouteOnDraw.setBounds(0, 0, 20, 20);
        showMyRankHead(myRankNum, mRouteOnDraw);

        ll_rank_main.setVisibility(View.GONE);
        if (rank.size() == 1) {
            rank_name_1.setText(rank.get(0).getName());
            ll_rank_2.setVisibility(View.GONE);
            ll_rank_3.setVisibility(View.GONE);
        } else if (rank.size() == 2) {
            rank_name_1.setText(rank.get(0).getName());
            rank_name_2.setText(rank.get(1).getName());
            ll_rank_2.setVisibility(View.VISIBLE);
            ll_rank_3.setVisibility(View.GONE);
        } else if (rank.size() == 3) {
            rank_name_1.setText(rank.get(0).getName());
            rank_name_2.setText(rank.get(1).getName());
            rank_name_3.setText(rank.get(2).getName());
            ll_rank_2.setVisibility(View.VISIBLE);
            ll_rank_3.setVisibility(View.VISIBLE);
        } else {
            ll_rank_2.setVisibility(View.VISIBLE);
            ll_rank_3.setVisibility(View.VISIBLE);
            for (int i = 0; i < rank.size(); i++) {
                if (rank.get(i).getId().equals(PlayVideoActivity.uid + "")) {
                    TLog.log(TAG, i + " ： i");
                    if (i < 3) {
                        rank_name_1.setText(rank.get(0).getName());
                        rank_name_2.setText(rank.get(1).getName());
                        rank_name_3.setText(rank.get(2).getName());
                        return;
                    } else {
                        TLog.log(TAG, i + 1 + " ： i+1");
                        ll_rank_main.setVisibility(View.VISIBLE);
                        rank_name_1.setText(rank.get(0).getName());
                        rank_name_2.setText(rank.get(1).getName());
                        rank_name_3.setText(rank.get(2).getName());
                        tv_my_rank.setText("第 " + (i + 1) + " 名");
                        my_name.setText(rank.get(i).getName());
                        return;
                    }
                }
            }
        }
    }

    private void showMyRankHead(int myRankNum, Drawable mRouteOnDraw) {
        if (myRankNum == 0) {
            tv_rank_1.setCompoundDrawables(null, null, mRouteOnDraw, null);
            tv_rank_2.setCompoundDrawables(null, null, null, null);
            tv_rank_3.setCompoundDrawables(null, null, null, null);
        } else if (myRankNum == 1) {
            tv_rank_2.setCompoundDrawables(null, null, mRouteOnDraw, null);
            tv_rank_1.setCompoundDrawables(null, null, null, null);
            tv_rank_3.setCompoundDrawables(null, null, null, null);
        } else if (myRankNum == 2) {
            tv_rank_3.setCompoundDrawables(null, null, mRouteOnDraw, null);
            tv_rank_1.setCompoundDrawables(null, null, null, null);
            tv_rank_2.setCompoundDrawables(null, null, null, null);
        } else {
            tv_rank_1.setCompoundDrawables(null, null, null, null);
            tv_rank_2.setCompoundDrawables(null, null, null, null);
            tv_rank_3.setCompoundDrawables(null, null, null, null);
        }
    }

    //------------互动问答--------------

    private long mMillisInFuture = 30 * 1000;
    private long mCountDownInterval = 1000;
    private TextView time;
    private String body;
    private AlertDialog mHuDongDialog;


    private final CountDownTimer mVerifyTimer = new CountDownTimer(mMillisInFuture, mCountDownInterval) {
        @Override
        public void onTick(long millisUntilFinished) {
            int timer = (int) (millisUntilFinished / mCountDownInterval);
            time.setText(timer-- + "");
        }

        @Override
        public void onFinish() {
            time.setText("0");
            mHuDongDialog.dismiss();
        }
    };


    /**
     * 互动问答
     *
     * @param mTeaQueEnty
     * @param planId
     */
    public void showHuDongWenDaDialog(TeacherQuestionEnty mTeaQueEnty, final String planId, final PlayVideoActivity activity) {
        if (mHuDongDialog == null) {
            mHuDongDialog = new AlertDialog.Builder(activity, UIHelper.getResIdByName(activity, UIHelper.TYPE_STYLE, "yunke_theme_transparent")).create();
        }
        if (mAnswerCardDialog != null && mAnswerCardDialog.isShowing()) {
            mAnswerCardDialog.dismiss();
        }
        if (activity.isFinishing() || mHuDongDialog.isShowing()) {
            return;
        }

        mHuDongDialog.show();
        View contentView = View.inflate(activity, UIHelper.getResIdByName(activity, UIHelper.TYPE_LAYOUT, "yunke_hudong_dialog"), null);
        mHuDongDialog.setContentView(contentView);
        mHuDongDialog.setCancelable(false);
        mHuDongDialog.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams p = mHuDongDialog.getWindow().getAttributes();//修改宽度
        p.width = activity.getResources().getDimensionPixelOffset(UIHelper.getResIdByName(mActivity, UIHelper.TYPE_DIMEN, "yunke_answercard_widht"));
        p.height = activity.getResources().getDimensionPixelOffset(UIHelper.getResIdByName(mActivity, UIHelper.TYPE_DIMEN, "yunke_answercard_height"));
        mHuDongDialog.getWindow().setAttributes(p);

        mVerifyTimer.start();
        final TextView no = (TextView) contentView.findViewById(UIHelper.getResIdByName(activity, UIHelper.TYPE_ID, "tv_no"));
        final TextView yes = (TextView) contentView.findViewById(UIHelper.getResIdByName(activity, UIHelper.TYPE_ID, "tv_yes"));
        final FrameLayout cancel = (FrameLayout) contentView.findViewById(UIHelper.getResIdByName(activity, UIHelper.TYPE_ID, "fl_cancel"));
        time = (TextView) contentView.findViewById(UIHelper.getResIdByName(activity, UIHelper.TYPE_ID, "time"));

        try {
            no.setText(mTeaQueEnty.getAnswer().get(1).split(" ")[1]);
            yes.setText(mTeaQueEnty.getAnswer().get(0).split(" ")[1]);
        } catch (Exception e) {
            e.printStackTrace();
            no.setText(mTeaQueEnty.getAnswer().get(1));
            yes.setText(mTeaQueEnty.getAnswer().get(0));
        }

        final StudentAnswerEnty mStudentAnswerEnty = new StudentAnswerEnty();
        mStudentAnswerEnty.id = mTeaQueEnty.getId();

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mStudentAnswerEnty.answer = "B";
                body = mStudentAnswerEnty.toJson();
                mVerifyTimer.cancel();
                mVerifyTimer.onFinish();
                MyWebSocketManager.getInstance(activity).sendMessage(SIGNAL, body, STUDENT_ANSWER,0);
                mHuDongDialog.dismiss();

            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStudentAnswerEnty.answer = "A";
                body = mStudentAnswerEnty.toJson();
                mVerifyTimer.cancel();
                mVerifyTimer.onFinish();
                MyWebSocketManager.getInstance(activity).sendMessage(SIGNAL, body, STUDENT_ANSWER,0);
                mHuDongDialog.dismiss();

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHuDongDialog.dismiss();
            }
        });

    }

    private AlertDialog mReplyDialog;
    private final int ANSWER_TYPE = 7; //答到

    /**
     * 答到
     */
    public void daDao(final String planId, final PlayVideoActivity activity) {

        if (mReplyDialog == null) {
            mReplyDialog = new AlertDialog.Builder(activity, UIHelper.getResIdByName(activity, UIHelper.TYPE_STYLE, "yunke_theme_transparent")).create();
        }

        if (activity.isFinishing() || mReplyDialog.isShowing()) {
            return;
        }

        mReplyDialog.show();
        View contentView = View.inflate(activity, UIHelper.getResIdByName(activity, UIHelper.TYPE_LAYOUT, "yunke_view_reply_dialog"), null);
        mReplyDialog.setContentView(contentView);
        mReplyDialog.setCancelable(false);
        mReplyDialog.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams p = mReplyDialog.getWindow().getAttributes();//修改宽度
        p.width = activity.getResources().getDimensionPixelOffset(UIHelper.getResIdByName(mActivity, UIHelper.TYPE_DIMEN, "yunke_dadao_widht"));
        p.height = activity.getResources().getDimensionPixelOffset(UIHelper.getResIdByName(mActivity, UIHelper.TYPE_DIMEN, "yunke_dadao_height"));
        mReplyDialog.getWindow().setAttributes(p);

        final ImageButton sure = (ImageButton) contentView.findViewById(UIHelper.getResIdByName(activity, UIHelper.TYPE_ID, "tv_sure"));

        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyWebSocketManager.getInstance(activity).sendMessage(SIGNAL, "", ANSWER_TYPE,0);
                mReplyDialog.dismiss();
            }
        });
    }

    private AlertDialog mNoticeDialog;

    /**
     * 公告
     */
    public void showNoticeDialog(String body, PlayVideoActivity activity) {

        if (activity.isFinishing()) {
            return;
        }

        if (mNoticeDialog == null) {
            mNoticeDialog = new AlertDialog.Builder(activity, UIHelper.getResIdByName(activity, UIHelper.TYPE_STYLE, "yunke_theme_transparent")).create();
        }

        mNoticeDialog.show();
        View contentView = View.inflate(activity, UIHelper.getResIdByName(activity, UIHelper.TYPE_LAYOUT, "yunke_view_notice_dialog"), null);
        mNoticeDialog.setContentView(contentView);
        mNoticeDialog.setCancelable(false);
        mNoticeDialog.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams p = mNoticeDialog.getWindow().getAttributes();//修改宽度
        p.width = activity.getResources().getDimensionPixelOffset(UIHelper.getResIdByName(mActivity, UIHelper.TYPE_DIMEN, "yunke_dadao_widht"));
        p.height = activity.getResources().getDimensionPixelOffset(UIHelper.getResIdByName(mActivity, UIHelper.TYPE_DIMEN, "yunke_dadao_height"));
        mNoticeDialog.getWindow().setAttributes(p);

        final Button sure = (Button) contentView.findViewById(UIHelper.getResIdByName(activity, UIHelper.TYPE_ID, "sure"));
        final TextView tv_notice = (TextView) contentView.findViewById(UIHelper.getResIdByName(activity, UIHelper.TYPE_ID, "tv_notice"));
        tv_notice.setText(body);

        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNoticeDialog.dismiss();
            }
        });
    }

    private AlertDialog mUpgradeDialog;

    /**
     * 升级
     */
    public void showUpgradeDialog(GetGoodDataBean mResult, PlayVideoActivity activity) {
        if (mUpgradeDialog == null) {
            mUpgradeDialog = new AlertDialog.Builder(activity, UIHelper.getResIdByName(activity, UIHelper.TYPE_STYLE, "yunke_theme_transparent")).create();
        }

        mUpgradeDialog.show();
        View contentView = View.inflate(activity, UIHelper.getResIdByName(activity, UIHelper.TYPE_LAYOUT, "yunke_view_upgrade_dialog"), null);
        mUpgradeDialog.setContentView(contentView);
        mUpgradeDialog.setCancelable(false);
        mUpgradeDialog.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams p = mUpgradeDialog.getWindow().getAttributes();//修改宽度
        p.width = activity.getResources().getDimensionPixelOffset(UIHelper.getResIdByName(activity, UIHelper.TYPE_DIMEN, "yunke_upgrade_widht"));
        mUpgradeDialog.getWindow().setAttributes(p);

        final Button sure = (Button) contentView.findViewById(UIHelper.getResIdByName(activity, UIHelper.TYPE_ID, "sure"));
        final TextView tv_up_des = (TextView) contentView.findViewById(UIHelper.getResIdByName(activity, UIHelper.TYPE_ID, "tv_up_des"));
        tv_up_des.setText("升级到书生" + mResult.getData().getFk_level() + ",加油！");

        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUpgradeDialog.dismiss();
            }
        });
    }

    public void dismissDialog() {

        if (mUpgradeDialog != null && mUpgradeDialog.isShowing()) {
            mUpgradeDialog.dismiss();
        }
        if (mNoticeDialog != null && mNoticeDialog.isShowing()) {
            mNoticeDialog.dismiss();
        }
        if (mReplyDialog != null && mReplyDialog.isShowing()) {
            mReplyDialog.dismiss();
        }
        if (mHuDongDialog != null && mHuDongDialog.isShowing()) {
            mHuDongDialog.dismiss();
        }
        if (mAnswerCardDialog != null && mAnswerCardDialog.isShowing()) {
            mAnswerCardDialog.dismiss();
        }

    }

    private final CountDownTimer mAnswerCard = new CountDownTimer(mMillisInFuture, mCountDownInterval) {
        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            ivAnswerCard.setVisibility(View.GONE);
            cancelAnswerCardDialog();
        }
    };
}