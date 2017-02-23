package com.yunke.player.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;


import com.yunke.player.widget.dialog.ConfirmDialog;

public class DialogUtil {
    /**
     * 确认Dialog
     *
     * @param context
     * @param title
     * @param message
     * @param negativeText
     * @param negativeClickListener
     * @return
     */
    public static Dialog showConfirmDialog(final Context context, String title, String message, String negativeText, DialogInterface.OnClickListener negativeClickListener) {
        return showConfirmDialog(true, context, title, message, negativeText, context.getString(UIHelper.getResIdByName(context, UIHelper.TYPE_STRING, "yunke_cancel")), negativeClickListener, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 确认Dialog
     *
     * @param context
     * @param title
     * @param message
     * @param negativeText
     * @param positiveText
     * @param negativeClickListener
     * @param positiveClickListener
     * @return
     */
    public static Dialog showConfirmDialog(boolean cancelable, final Context context, String title, String message, String negativeText, String positiveText, DialogInterface.OnClickListener negativeClickListener, DialogInterface.OnClickListener positiveClickListener) {
        ConfirmDialog.Builder builder = new ConfirmDialog.Builder(context);
        builder
                .setTitle(title)
                .setMessage(Html.fromHtml(message).toString())
                .setNegativeButton(negativeText, negativeClickListener)
                .setPositiveButton(positiveText, positiveClickListener);
        ConfirmDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(cancelable);
        dialog.show();
        return dialog;
    }

}
