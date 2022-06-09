package com.ucloud.demo.customview;

import android.net.TrafficStats;

/**
 * <p>
 * <code>lastTotalRxBytes = getTotalRxBytes();<br>
 * lastTimeStamp = System.currentTimeMillis();<br>
 * new Timer().schedule(task, 1000, 2000); // 1s后启动任务，每2s执行一次<br>
 * TimerTask task = new TimerTask();
 */
public class NetSpeed {
    private static final String TAG = NetSpeed.class.getSimpleName();
    private long lastTotalRxBytes = 0;
    private long lastTimeStamp = 0;

    public String getNetSpeed(int uid) {
        long nowTotalRxBytes = getTotalRxBytes(uid);
//        Log.i(TAG, "nowTotalRxBytes  = " + nowTotalRxBytes);
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换
        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;
        return String.valueOf(speed) + " KB/s";
    }

    //getApplicationInfo().uid
    public long getTotalRxBytes(int uid) {
        return TrafficStats.getTotalRxBytes() == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//转为KB
    }
}
