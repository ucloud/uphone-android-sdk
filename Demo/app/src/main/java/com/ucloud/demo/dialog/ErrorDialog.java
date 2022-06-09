package com.ucloud.demo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ucloud.demo.R;


public class ErrorDialog extends Dialog {
    private final String error_str;
    private TextView tv_hint;


    public ErrorDialog(@NonNull Context context, String error_msg) {
        super(context, R.style.QuitDialog);
        error_str = error_msg;
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.error_dialog, null);
        view.findViewById(R.id.tv_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRightBtnClickListner != null) {
                    onRightBtnClickListner.onClick(v);
                }
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

        tv_hint = view.findViewById(R.id.tv_hint);
        tv_hint.setText(error_str);

        setCanceledOnTouchOutside(false);
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
