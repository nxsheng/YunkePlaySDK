package com.yunke.player.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunke.player.util.UIHelper;


public class ConfirmDialog extends Dialog {

    public ConfirmDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {

        private Context context;
        private String title;
        private String message;
        private String positiveText;
        private String negativeText;
        private View contentView;

        private DialogInterface.OnClickListener
                positiveButtonClickListener,
                negativeButtonClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        /**
         * Set the Dialog message from String
         *
         * @param message
         * @return
         */
        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * Set the Dialog message from resource
         *
         * @param message
         * @return
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        /**
         * Set the Dialog title from resource
         *
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         *
         * @param title
         * @return
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * Set the positive button text and it's listener
         *
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public Builder setPositiveButton(String positiveButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.positiveText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * Set the negative button text and it's listener
         *
         * @param negativeButtonText
         * @param listener
         * @return
         */
        public Builder setNegativeButton(String negativeButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.negativeText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        public ConfirmDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final ConfirmDialog dialog = new ConfirmDialog(context,
                    UIHelper.getResIdByName(context, UIHelper.TYPE_STYLE, "yunke_confirm_dialog"));
            View layout = inflater.inflate(UIHelper.getResIdByName(context, UIHelper.TYPE_LAYOUT, "yunke_view_confirm_dialog"), null);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            // set the dialog title
            TextView tvTitle = (TextView) layout.findViewById(UIHelper.getResIdByName(context, UIHelper.TYPE_ID, "title"));
            if (!TextUtils.isEmpty(title)) {
                tvTitle.setText(title);
            } else {
                tvTitle.setVisibility(View.GONE);
            }
            // set the confirm button
            TextView tvPositive = (TextView) layout.findViewById(UIHelper.getResIdByName(context, UIHelper.TYPE_ID, "positive"));
            if (!TextUtils.isEmpty(positiveText)) {
                tvPositive.setText(positiveText);
                if (positiveButtonClickListener != null) {
                    tvPositive.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            positiveButtonClickListener.onClick(
                                    dialog,
                                    DialogInterface.BUTTON_POSITIVE);
                            dialog.dismiss();
                        }
                    });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(UIHelper.getResIdByName(context, UIHelper.TYPE_ID, "positive")).setVisibility(
                        View.GONE);
            }
            // set the cancel button
            TextView tvNegative = (TextView) layout.findViewById(UIHelper.getResIdByName(context, UIHelper.TYPE_ID, "negative"));
            if (!TextUtils.isEmpty(negativeText)) {
                tvNegative.setText(negativeText);
                if (negativeButtonClickListener != null) {
                    tvNegative.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            negativeButtonClickListener.onClick(
                                    dialog,
                                    DialogInterface.BUTTON_NEGATIVE);
                            dialog.dismiss();
                        }
                    });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(UIHelper.getResIdByName(context, UIHelper.TYPE_ID, "negative")).setVisibility(
                        View.GONE);
            }
            // set the content message
            TextView tvMessage = (TextView) layout.findViewById(UIHelper.getResIdByName(context, UIHelper.TYPE_ID, "message"));
            if (!TextUtils.isEmpty(message)) {
                tvMessage.setVisibility(View.VISIBLE);
                tvMessage.setText(message);
            } else if (contentView != null) {
                tvMessage.setVisibility(View.GONE);
                // if no message set
                // add the contentView to the dialog body
                ((LinearLayout) layout.findViewById(UIHelper.getResIdByName(context, UIHelper.TYPE_ID, "content")))
                        .removeAllViews();
                ((LinearLayout) layout.findViewById(UIHelper.getResIdByName(context, UIHelper.TYPE_ID, "content")))
                        .addView(contentView,
                                new ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            dialog.setContentView(layout);
            return dialog;
        }
    }
}
