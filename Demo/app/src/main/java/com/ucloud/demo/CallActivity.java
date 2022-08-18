package com.ucloud.demo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.ucloud.demo.util.Constant;
import com.ucloud.uphonesdk.IUPhone;
import com.ucloud.uphonesdk.IUPhoneListener;
import com.ucloud.uphonesdk.UPhone;
import com.ucloud.uphonesdk.OnInitCallBackListener;
import com.ucloud.uphonesdk.USurfaceView;
import com.ucloud.demo.customview.SettingButton;
import com.ucloud.demo.dialog.SettingDialog;
import com.ucloud.demo.dialog.QuitCloudPhoneDialog;

import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;


public class CallActivity extends AppCompatActivity {
    private static final String TAG = "CallActivity";
    private static final int MY_PERMISSIONS_REQUEST_SAVE_PICTRUE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //云手机视图
    USurfaceView phoneView;
    // 云手机SDK调用接口
    IUPhone mUphone = null;
    public boolean isPortrait = true;//默认竖屏
    //设置窗口和按钮
    private SettingDialog settingDiag = null;
    SettingButton settingButton;
    private ImageView ivLoading;
    private int retryTimes = 0;
    private int repeatTimes = 0;

    private final OnInitCallBackListener initCallBackListener = new OnInitCallBackListener() {
        @Override
        public void success() {
            if (mUphone != null) {
                mUphone.connectUPhone(phoneView);
            }
        }

        @Override
        public void fail(String var) {
            Log.e(TAG, "init error: " + var);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(CallActivity.this, var, Toast.LENGTH_SHORT).show();
                }
            });
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUphone = new UPhone(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(getSystemUiVisibility());

        setContentView(R.layout.activity_call);
        phoneView = findViewById(R.id.game_view);
        ivLoading = findViewById(R.id.iv_loading);

        settingButton = findViewById(R.id.sb_mobile_setting);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View v) {
                if (settingDiag == null) {
                    settingDiag = new SettingDialog(CallActivity.this, R.style.DialogRight);
                }
                settingDiag.setStatistics(Integer.toString(mUphone.getNetDelay()));
                settingDiag.show();
                Log.e(TAG, "testinterface:" + "" +
                        " mUphone.getQRCodeData():" + mUphone.getQRCodeData()
                        + " getLossRate:" + mUphone.getLossRate()
                        + "  getNetworkSpeed:" + mUphone.getNetworkSpeed()
                        + "  getLastUserOperationTimestamp:" + mUphone.getLastOperationTimestamp()
                        + "isSupportLiving:" + mUphone.isSupportLiving());
            }
        });
        mUphone.registerUphoneListener(mUPhoneListener);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "phone id: " + bundle.getString("PHONE_ID"));
        //bundle.putString("GAME_PACKAGE_NAME", "com.tencent.tmgp.sgame");
        //bundle.putString("JOB_ID", "100001");
        //bundle.putString("TOKEN", "12345");
        //bundle.putBoolean("NEED_AUDIO", false);
        mUphone.initSdk(bundle, initCallBackListener);

        Timer mTimer = new Timer();
        TimerTask mTimerTask = new TimerTask() {//创建一个线程来执行run方法中的代码
            @Override
            public void run() {
                if (mUphone != null) {
                    double lossRate = mUphone.getLossRate();
                    double netSpeed = mUphone.getNetworkSpeed();
                    int netDelay = mUphone.getNetDelay();
                    //Log.d(TAG, "丢包率： " + lossRate + " 网速： " + netSpeed + " 延时： " + netDelay);
                }
            }
        };
        mTimer.schedule(mTimerTask, 1000, 1000);
    }

    @TargetApi(19)
    private static int getSystemUiVisibility() {
        int flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            flags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        return flags;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //如果点击的是后退键
            if (mUphone != null) {
                mUphone.disconnectUPhone();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void sendKeyByName(String keystr) {
        if (mUphone != null) {
            mUphone.sendKeyByName(keystr);
        }
    }

    public void setVolume(int val) {
        if (mUphone != null) {
            mUphone.setVolume(val);
        }
    }

    public void setResolution(int resolutionSelect) {
        if (mUphone != null) {
            mUphone.setResolution(resolutionSelect);
        }
    }

    public void setAudioMute(boolean mute) {
        if (mUphone != null) {
            mUphone.setAudioMute(mute);
        }
    }

    public void confirmQuit() {
        QuitCloudPhoneDialog dialog = new QuitCloudPhoneDialog(this);
        dialog.show();
        dialog.setOnRightBtnClickListner(new QuitCloudPhoneDialog.OnRightBtnClickListner() {
            @Override
            public void onClick(View view) {
                if (mUphone != null) {
                    mUphone.disconnectUPhone();
                }
                dialog.cancel();
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (settingDiag != null) {
            settingDiag.dismiss();
            settingDiag = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        if (mUphone != null) {
            mUphone.wakeupScreen();
        }
        super.onResume();
    }

    private final IUPhoneListener mUPhoneListener = new IUPhoneListener() {

        @Override
        public void onConnectionSuccess() {
            retryTimes = 0;
            Log.d(TAG, "connect success.");
        }

        @Override
        public void onConnectionFailure(int errorCode, String errorMsg) {
            Log.e(TAG, "fail code: " + errorCode + " error msg: " + errorMsg);
            //重连机制
            if (retryTimes < 10) {
                if (mUphone != null) {
                    if (repeatTimes == 3) {
                        //如果判断3次重复连接，可启用强制连接机制
                        mUphone.reconnection(true);
                        repeatTimes = 0;
                    } else {
                        mUphone.reconnection(false);
                    }
                    Log.d(TAG, "retry connect " + retryTimes);
                }
                if (errorCode == Constant.ERROR_DUPLICATE_CONNECTION) {
                    repeatTimes++;
                }
                retryTimes++;
            } else {
                mUphone.disconnectUPhone();
                finish();
            }
        }

        @Override
        public void onDrawFirstFrame() {
            Log.d(TAG, "onDrawFirstFrame: ");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    settingButton.setVisibility(View.VISIBLE);
                    ivLoading.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public void onChannelStatus(String status) {
            Log.d(TAG, "channel status: " + status);
            if (status == "open") {
                //mUphone.sendControlMsg(1001);
            } else if (status == "closed") {

            }
        }

        @Override
        public void onChannelMessage(byte[] msg) {
            String tempMsg = new String(msg, StandardCharsets.UTF_8);
            Log.d(TAG, "channel message: " + tempMsg);
        }

        @Override
        public void onControlResult(int type, int code, @NonNull String message) {
            switch (type) {
                case Constant.TYPE_CHANGE_RESOLUTION:
                    if (code == 0) {
                        Log.d(TAG, "分辨率切换成功");
                    } else {
                        Log.d(TAG, "分辨率切换失败" + message);
                    }
                    break;
                default:
                    break;
            }
        }
    };
}