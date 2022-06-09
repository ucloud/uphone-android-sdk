package com.ucloud.demo;

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
import android.widget.Toast;
import com.ucloud.uphonesdk.IUPhone;
import com.ucloud.uphonesdk.IUPhoneListener;
import com.ucloud.uphonesdk.UPhone;
import com.ucloud.uphonesdk.OnInitCallBackListener;
import com.ucloud.uphonesdk.USurfaceView;
import com.ucloud.demo.customview.SettingButton;
import com.ucloud.demo.dialog.SettingDialog;
import com.ucloud.demo.dialog.QuitCloudPhoneDialog;



public class CallActivity extends AppCompatActivity {
    private static final String TAG = "CallActivity";
    private static final int MY_PERMISSIONS_REQUEST_SAVE_PICTRUE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //云手机视图
    USurfaceView gameView;
    // 云手机SDK调用接口
    IUPhone iUPhone = null;
    public boolean isPortrait = true;//默认竖屏
    //设置窗口和按钮
    private SettingDialog settingDiag = null;
    SettingButton settingButton;
    private int retryTimes = 0;

    private final OnInitCallBackListener initCallBackListener = new OnInitCallBackListener() {
        @Override
        public void success() {
            if (iUPhone != null) {
                iUPhone.connectUPhone(gameView);
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
        iUPhone = new UPhone(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(getSystemUiVisibility());

        setContentView(R.layout.activity_call);
        gameView = findViewById(R.id.game_view);

        settingButton = findViewById(R.id.sb_mobile_setting);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View v) {
                if (settingDiag == null) {
                    settingDiag = new SettingDialog(CallActivity.this, R.style.DialogRight);
                }
                settingDiag.setStatistics(Integer.toString(iUPhone.getNetDelay()));
                settingDiag.show();
                Log.e(TAG, "testinterface:" + "" +
                        " iUPhone.getQRCodeData():" + iUPhone.getQRCodeData()
                        + " getLossRate:" + iUPhone.getLossRate()
                        + "  getNetworkSpeed:" + iUPhone.getNetworkSpeed()
                        + "  getLastUserOperationTimestamp:" + iUPhone.getLastOperationTimestamp()
                        + "isSupportLiving:" + iUPhone.isSupportLiving());
            }
        });
        iUPhone.registerUphoneListener(mUPhoneListener);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        bundle.putString("GAME_PACKAGE_NAME", "com.tencent.tmgp.sgame/com.tencent.tmgp.sgame.SGameActivity");
        bundle.putString("JOB_ID", "100001");
        bundle.putString("TOKEN", "12345");
        iUPhone.initSdk(bundle, initCallBackListener);
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
            if (iUPhone != null) {
                iUPhone.disconnectUPhone();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void sendKeyByName(String keystr) {
        if (iUPhone != null) {
            iUPhone.sendKeyByName(keystr);
        }
    }

    public void startLive(String liveurl) {
        if (iUPhone != null) {
            iUPhone.startLive(liveurl);
        }
    }

    public void stopLive() {
        if (iUPhone != null) {
            iUPhone.stopLive();
        }
    }

    public void setResolution(int resolutionSelect) {
        iUPhone.setResolution(resolutionSelect);
    }

    public void setAudioMute(boolean mute) {
        if (iUPhone != null) {
            iUPhone.setAudioMute(mute);
        }
    }

    public void confirmQuit() {
        QuitCloudPhoneDialog dialog = new QuitCloudPhoneDialog(this);
        dialog.show();
        dialog.setOnRightBtnClickListner(new QuitCloudPhoneDialog.OnRightBtnClickListner() {
            @Override
            public void onClick(View view) {
                if (iUPhone != null) {
                    iUPhone.disconnectUPhone();
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

    private final IUPhoneListener mUPhoneListener = new IUPhoneListener() {
        @Override
        public void onConnectionFailure(String s) {
            Log.e(TAG, "connect fail reason: " + s);
            //重连机制
            if (retryTimes < 10) {
                if (iUPhone != null) {
                    iUPhone.reconnection();
                    Log.d(TAG, "retry connect " + retryTimes);
                }
                retryTimes++;
            } else {
                iUPhone.disconnectUPhone();
                finish();
            }
        }

        @Override
        public void onConnectionSuccess() {
            retryTimes = 0;
            Log.d(TAG, "connect success.");
        }

        @Override
        public void onControlMsgCallback(String type, int result, String error) {
            Log.e(TAG, "control msg: " + type + " " + error);
            if (type.equals("setresolution")) {
                if (result == 0) {
                    Log.d(TAG, "分辨率设置成功");
                } else {
                    Log.d(TAG,"分辨率设置失败");
                }
            }  else if (type.equals("startgame")) {
                if (result == 0) {
                    Log.d(TAG,"游戏启动成功");
                } else {
                    Log.e(TAG, "游戏启动失败");
                }
            } else if (type.equals("startlive")) {

            } else if (type.equals("stoplive")) {

            } else if (type.equals("serverinternal")) {

            }
        }
    };
}