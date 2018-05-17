package com.weicools.huawei.push.demo;

import android.app.Application;
import android.util.Log;

import com.weicools.huawei.push.demo.agent.HMSAgent;
import com.weicools.huawei.push.demo.agent.common.handler.ConnectHandler;
import com.weicools.huawei.push.demo.agent.push.handler.GetPushStateHandler;

public class PushApplication extends Application {
  private static final String TAG = "PushApplication";

  @Override
  public void onCreate() {
    super.onCreate();
    boolean init = HMSAgent.init(this);
    if (init) {
      Log.e(TAG, "App onCreate: init HMSAgent success");
    } else {
      Log.e(TAG, "App onCreate: init HMSAgent failed");
    }

    HMSAgent.Push.getPushState(new GetPushStateHandler() {
      @Override
      public void onResult(int rst) {
        Log.e(TAG, "App onCreate: getPushState " + rst);
      }
    });
  }
}
