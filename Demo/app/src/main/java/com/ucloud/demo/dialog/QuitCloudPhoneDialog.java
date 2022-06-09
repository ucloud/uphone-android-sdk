package com.ucloud.demo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ucloud.demo.R;


public class QuitCloudPhoneDialog extends Dialog {
    private TextView tv_title, tv_content, tv_yes, tv_no;


    public QuitCloudPhoneDialog(@NonNull Context context) {
        super(context, R.style.QuitDialog);
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.quit_dialog, null);
        view.findViewById(R.id.tv_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        view.findViewById(R.id.tv_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRightBtnClickListner != null) {
                    onRightBtnClickListner.onClick(v);
                }
            }
        });

        setCanceledOnTouchOutside(true);
        setContentView(view);
    }

    private OnRightBtnClickListner onRightBtnClickListner;

    public void setOnRightBtnClickListner(OnRightBtnClickListner onRightBtnClickListner) {
        this.onRightBtnClickListner = onRightBtnClickListner;
    }

    public interface OnRightBtnClickListner {
        void onClick(View view);
    }
}
