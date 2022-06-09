package com.ucloud.demo.customview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.ucloud.demo.R;
import com.ucloud.demo.util.DisplayUtil;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SettingButton extends LinearLayout {
    private static final String TAG = "SettingButton";
    private int parentHeight;
    private int parentWidth;
    private TextView tv_netspeed;
    private Button bt_setting;
    private int lastX;
    private int lastY;
    private boolean isDrag;
    private boolean isHorizontal = false;
    View contentView;

    private boolean bPortrait_saved = false;
    private Activity activity = null;

    Handler mHndler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
//                    Log.e(TAG, "getX=" + getX() + ";getY=" + getY()
//                            + ";parentWidth=" + parentWidth + ";getWidth:" + getWidth()
//                            + "emptyScreen:" + CallActivity.emptyScreen
//                            + "screenWidth:" + CallActivity.screenWidth
//                            + " getLeft:"+getLeft());
                    if (!isHorizontal) {
                        animate().setInterpolator(new DecelerateInterpolator())
                                .setDuration(200)
                                .x((float) (parentWidth - 0.5 * getWidth()))
                                .start();
                    } else {
                        animate().setInterpolator(new DecelerateInterpolator())
                                .setDuration(200)
                                .y((float) (parentHeight - 0.5 * getWidth()))
                                .start();
                    }
                    break;

            }
        }
    };


    public SettingButton(Context context) {
        super(context);
    }

    public SettingButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SettingButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SettingButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        initView(context);
    }

    private void initView(Context context) {
        activity = (Activity) context;
        contentView = LayoutInflater.from(context).inflate(R.layout.mobile_setting, this, true);
        tv_netspeed = findViewById(R.id.tv_netspeed);
        bt_setting = findViewById(R.id.bt_mobile_setting);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
//        return super.onInterceptTouchEvent(ev);
    }

    public void adjustSettingButtonDirection(boolean isPortrait) {
        if (isPortrait) {
            contentView.setRotation(0);
        } else {
            contentView.setRotation(90);
        }

        bPortrait_saved = isPortrait;

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                isDrag = false;
                getParent().requestDisallowInterceptTouchEvent(true);
                lastX = rawX;
                lastY = rawY;
                ViewGroup parent;
                if (getParent() != null) {
                    parent = (ViewGroup) getParent();
                    parentHeight = parent.getHeight();
                    parentWidth = parent.getWidth();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (parentHeight <= 0 || parentWidth == 0) {
                    isDrag = false;
                    break;
                } else {
                    isDrag = true;
                }
                int dx = rawX - lastX;
                int dy = rawY - lastY;
                //这里修复一些华为手机无法触发点击事件
                int distance = (int) Math.sqrt(dx * dx + dy * dy);
                if (distance == 0) {
                    isDrag = false;
                    break;
                }
                float x = getX() + dx;//触摸点x坐标+滑动的距离dx 保证滑到哪里走到哪里
                float y = getY() + dy;//触摸点坐标+滑动的距离dy

                int navHeight = getNavigationBarHeight(activity);
                int screen_height = DisplayUtil.getScreenHeight(activity);
                Log.e(TAG, "y: " + y + " screen_height:" + screen_height + " navHeight:" + navHeight);
                if (y > screen_height - navHeight * 2.5) {
                    y = (float) (screen_height - navHeight * 2.5);
                }
                if (y < 20) {
                    y = 20;
                }


                setX(x);
                setY(y);
                lastX = rawX;
                lastY = rawY;
                Log.e(TAG, "isDrag=" + isDrag + "getX=" + getX() + ";getY=" + getY() + ";parentWidth=" + parentWidth);
                requestLayout();//必须，因为fullsufacerender 由surfaceview 刷新
                break;
            case MotionEvent.ACTION_UP:
                if (isDrag) {
                    Log.e(TAG, "ACTION_UP getX()=" + getX() + "---getWidth()=" + getWidth());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

//                            mHndler.sendEmptyMessage(0);
                        }

                    }).start();

                }
                requestLayout();
                break;
        }
        //如果是拖拽则消耗事件，否则正常传递即可。
        return isDrag || super.onTouchEvent(event);
    }


    public void setNetSpeed(String speed) {
        tv_netspeed.setText("" + speed+"KB/s");
    }


    public static boolean isHasNavigationBar(Activity activity) {
        Point size = new Point();
        Point realSize = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            activity.getWindowManager().getDefaultDisplay().getRealSize(realSize);
        } else {
            Display display = activity.getWindowManager().getDefaultDisplay();

            Method mGetRawH = null;
            Method mGetRawW = null;

            int realWidth = 0;
            int realHeight = 0;

            try {
                mGetRawW = Display.class.getMethod("getRawWidth");
                mGetRawH = Display.class.getMethod("getRawHeight");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            try {
                realWidth = (Integer) mGetRawW.invoke(display);
                realHeight = (Integer) mGetRawH.invoke(display);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            realSize.set(realWidth, realHeight);
        }
        if (realSize.equals(size)) {
            return false;
        } else {
            size.y = size.y + getNavigationBarHeight(activity);
            if (realSize.y < size.y){
                return false;
            }
            return true;
        }
    }

    public static int getNavigationBarHeight(Activity activity) {
        //get navigation bar height.
        int resourceId = activity.getResources().getIdentifier(
                "navigation_bar_height", "dimen", "android");
        int navigationBarHeight = activity.getResources()
                .getDimensionPixelSize(resourceId);
        Log.e(TAG, "getNavigationBarHeight: " + navigationBarHeight);
        return navigationBarHeight;
    }

}
