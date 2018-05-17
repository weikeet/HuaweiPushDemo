package com.weicools.huawei.push.demo;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.weicools.huawei.push.demo.agent.HMSAgent;
import com.weicools.huawei.push.demo.agent.common.handler.ConnectHandler;
import com.weicools.huawei.push.demo.agent.push.handler.DeleteTokenHandler;
import com.weicools.huawei.push.demo.agent.push.handler.EnableReceiveNormalMsgHandler;
import com.weicools.huawei.push.demo.agent.push.handler.EnableReceiveNotifyMsgHandler;
import com.weicools.huawei.push.demo.agent.push.handler.GetPushStateHandler;
import com.weicools.huawei.push.demo.agent.push.handler.GetTokenHandler;
import com.weicools.huawei.push.demo.agent.push.handler.QueryAgreementHandler;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import static com.weicools.huawei.push.demo.HuaweiPushRevicer.ACTION_TOKEN;
import static com.weicools.huawei.push.demo.HuaweiPushRevicer.ACTION_UPDATE_UI;

public class MainActivity extends AppCompatActivity
    implements View.OnClickListener, HuaweiPushRevicer.IPushCallback {
  private static final String TAG = "MainActivity";
  private String token;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    findViewById(R.id.btn_gettoken).setOnClickListener(this);
    findViewById(R.id.btn_deletetoken).setOnClickListener(this);
    findViewById(R.id.btn_getpushstatus).setOnClickListener(this);
    findViewById(R.id.btn_setnormal).setOnClickListener(this);
    findViewById(R.id.btn_setnofify).setOnClickListener(this);
    findViewById(R.id.btn_agreement).setOnClickListener(this);

    //以下代码作用仅仅为了在sample界面上显示push相关信息
    HuaweiPushRevicer.registerPushCallback(this);

    HMSAgent.connect(this, new ConnectHandler() {
      @Override
      public void onConnect(int rst) {
        Log.e(TAG, "onCreate: HMSAgent.onConnect rst:" + rst);
      }
    });
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    HuaweiPushRevicer.unRegisterPushCallback(this);
  }

  @Override
  public void onReceive(Intent intent) {
    if (intent != null) {
      String action = intent.getAction();
      Bundle b = intent.getExtras();
      if (b != null && ACTION_TOKEN.equals(action)) {
        token = b.getString(ACTION_TOKEN);
        Log.e("Token123", "onReceive: " + token);
      } else if (b != null && ACTION_UPDATE_UI.equals(action)) {
        String log = b.getString("log");
        showLog(log);
      }
    }
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_gettoken:
        getToken();
        break;
      case R.id.btn_deletetoken:
        deleteToken();
        break;
      case R.id.btn_getpushstatus:
        getPushStatus();
        break;
      case R.id.btn_setnormal:
        setReceiveNormalMsg(true);
        break;
      case R.id.btn_setnofify:
        setReceiveNotifyMsg(true);
        break;
      case R.id.btn_agreement:
        showAgreement();
        break;
      default:
        break;
    }
  }

  /**
   * 获取token
   */
  private void getToken() {
    showLog("get token: begin");
    HMSAgent.Push.getToken(new GetTokenHandler() {
      @Override
      public void onResult(int rtnCode) {
        showLog("get token: end code=" + rtnCode);
      }
    });
  }

  /**
   * 删除token
   */
  private void deleteToken() {
    showLog("deleteToken:begin");
    HMSAgent.Push.deleteToken(token, new DeleteTokenHandler() {
      @Override
      public void onResult(int rst) {
        showLog("deleteToken:end code=" + rst);
      }
    });
  }

  /**
   * 获取push状态
   */
  private void getPushStatus() {
    showLog("getPushState:begin");
    HMSAgent.Push.getPushState(new GetPushStateHandler() {
      @Override
      public void onResult(int rst) {
        showLog("getPushState:end code=" + rst);
      }
    });
  }

  /**
   * 设置是否接收普通透传消息
   *
   * @param enable 是否开启
   */
  private void setReceiveNormalMsg(boolean enable) {
    showLog("enableReceiveNormalMsg:begin");
    HMSAgent.Push.enableReceiveNormalMsg(enable, new EnableReceiveNormalMsgHandler() {
      @Override
      public void onResult(int rst) {
        showLog("enableReceiveNormalMsg:end code=" + rst);
      }
    });
  }

  /**
   * 设置接收通知消息
   *
   * @param enable 是否开启
   */
  private void setReceiveNotifyMsg(boolean enable) {
    showLog("enableReceiveNotifyMsg:begin");
    HMSAgent.Push.enableReceiveNotifyMsg(enable, new EnableReceiveNotifyMsgHandler() {
      @Override
      public void onResult(int rst) {
        showLog("enableReceiveNotifyMsg:end code=" + rst);
      }
    });
  }

  /**
   * 显示push协议
   */
  private void showAgreement() {
    showLog("queryAgreement:begin");
    HMSAgent.Push.queryAgreement(new QueryAgreementHandler() {
      @Override
      public void onResult(int rst) {
        showLog("queryAgreement:end code=" + rst);
      }
    });
  }

  StringBuffer sbLog = new StringBuffer();

  private void showLog(String logLine) {
    DateFormat format = new java.text.SimpleDateFormat("MMdd.hh-mm-ss.SSS", Locale.CHINA);
    String time = format.format(new Date());

    sbLog.append(time).append(":").append(logLine).append('\n');
    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        View vText = findViewById(R.id.tv_log);

        if (vText != null && vText instanceof TextView) {
          TextView tvLog = (TextView) vText;
          tvLog.setText(sbLog.toString());
        }

        View vScroll = findViewById(R.id.sv_log);
        if (vScroll != null && vScroll instanceof ScrollView) {
          ScrollView svLog = (ScrollView) vScroll;
          svLog.fullScroll(View.FOCUS_DOWN);
        }
      }
    });
  }
}
