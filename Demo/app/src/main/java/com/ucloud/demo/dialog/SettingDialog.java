package com.ucloud.demo.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ucloud.demo.CallActivity;
import com.ucloud.demo.R;


import static android.content.Context.MODE_PRIVATE;


public class SettingDialog extends Dialog {
    private static final String TAG = "SettingDialog";
    private CallActivity activity;
    private RadioGroup rg_resolution;
    private RadioButton rb_standard;
    private RadioButton rb_high;
    private CheckBox cb_network_delay, cb_mute;
    private TextView tv_network_delay_value;
    private Button bt_home;
    private Button bt_menu;
    private Button bt_back;
    private Button bt_add_volume;
    private Button bt_dec_volume;

    public static final int RESOLUTION_STANDARD = 0;
    public static final int RESOLUTION_HIGH = 3;
    public static final int RESOLUTION_ULTRA = 6;

    public SettingDialog(@NonNull Activity context, int themeResId) {
        super(context, themeResId);
        initView(context);
    }

    private void initView(Activity context) {
        this.activity = (CallActivity) context;

        View contentView = LayoutInflater.from(context).inflate(R.layout.setting_dialog, null);
        if (activity.isPortrait) {
            contentView.setRotation(0);
        } else {
            contentView.setRotation(90);
        }

        rg_resolution = contentView.findViewById(R.id.rg_resolution);
        rb_standard = contentView.findViewById(R.id.rb_standard);
        rb_high = contentView.findViewById(R.id.rb_high);
        rb_high.setChecked(true);
        cb_network_delay = contentView.findViewById(R.id.cb_network_delay);
        cb_mute = contentView.findViewById(R.id.cb_mute);
        tv_network_delay_value = contentView.findViewById(R.id.tv_network_delay_value);
        bt_home = contentView.findViewById(R.id.bt_home);
        bt_menu = contentView.findViewById(R.id.bt_menu);
        bt_back = contentView.findViewById(R.id.bt_back);
        bt_add_volume = contentView.findViewById(R.id.bt_add_volume);
        bt_dec_volume = contentView.findViewById(R.id.bt_dec_volume);

        rg_resolution.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rb_standard.isChecked()) {//标清
                    activity.setResolution(RESOLUTION_STANDARD);
                } else if (rb_high.isChecked()) {
                    activity.setResolution(RESOLUTION_HIGH);
                }
            }
        });

        cb_network_delay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tv_network_delay_value.setVisibility(View.VISIBLE);
                } else {
                    tv_network_delay_value.setVisibility(View.INVISIBLE);
                }
                SharedPreferences sharedPreferences = activity.getSharedPreferences("ucloud_game_config", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("cb_network_delay_isChecked", isChecked ? true : false);
                editor.commit();
            }
        });

        cb_mute.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    activity.setAudioMute(true);
                } else {
                    activity.setAudioMute(false);
                }

            }
        });

        setCanceledOnTouchOutside(true);
        setContentView(contentView);

        Button btnExit = (Button) contentView.findViewById(R.id.tv_exit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.confirmQuit();
                dismiss();
            }
        });

        bt_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.sendKeyByName("home");
                dismiss();
            }
        });

        bt_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.sendKeyByName("clean");
                dismiss();
            }
        });

        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.sendKeyByName("back");
            }
        });

        bt_add_volume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.setVolume(1);
            }
        });

        bt_dec_volume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.setVolume(-1);
            }
        });
    }

    @Override
    public void show() {
        super.show();
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.LEFT;
        lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.x = 200;
        window.setAttributes(lp);
    }

    public void setStatistics(String stats) {
        tv_network_delay_value.setText(stats + " ms");
    }
}
