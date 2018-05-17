## 华为Push实现
### 创建应用
省略……
### 申请Push服务
1. 申请条件：
    - 应用包名(唯一)
    - SHA256
2. 申请过程参考[接入准备](http://developer.huawei.com/consumer/cn/service/hms/catalog/huaweipush_agent.html?page=hmssdk_huaweipush_prepare_agent)

### 集成SDK
1. 工程目录下`build.gradle`添加仓库依赖`maven {url 'http://developer.huawei.com/repo/'}`
    ![](http://blog-1251678165.coscd.myqcloud.com/2018-05-17-073616.png)
2. 模块目录下`build.gradle`添加SDK依赖`implementation 'com.huawei.android.hms:push:2.6.0.301'`
    ![](http://blog-1251678165.coscd.myqcloud.com/2018-05-17-073702.png)
3. 下载[HMS SDK Agent](https://obs.cn-north-2.myhwclouds.com/hms-ds-wf/sdk/HMSAgent_2.6.0.302.zip)
    - 运行`GetHMSAgent.bat`得到所需要的接入代码(cpid用不到可随便输)
        ![](http://blog-1251678165.coscd.myqcloud.com/2018-05-17-074659.png)
    - 拷贝到自己的模块里，或者使用`Buildcopysrc2jar.bat`生成Jar包进行依赖

### 配置Manifest
1. 添加权限

    ```xml
    <!--HMS-SDK引导升级HMS功能，访问OTA服务器需要网络权限-->
    <uses-permission android:name="android.permission.INTERNET" />
    <!--HMS-SDK引导升级HMS功能，保存下载的升级包需要SD卡写权限 -->
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <!--检测网络状态 | Detecting Network status-->
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <!--检测wifi状态 | Detecting WiFi status-->
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <!--获取用户手机的IMEI，用来唯一的标识设备 -->
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <!--如果是安卓8.0，应用编译配置的targetSdkVersion>=26，请务必添加以下权限 -->
    <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />
    ```

2. 配置AppId

    ```xml
    <!-- 接入HMSSDK 需要注册的appid参数。value的值用实际申请的appid替换 -->
    <meta-data
      android:name="com.huawei.hms.client.appid"
      android:value="appid=100283561" />
    ```

3. 注册Activity

    ```xml
    <!-- 使用 HMSAgent 代码接入HMSSDK 需要注册的activity -->
    <activity
      android:name="com.weicools.huawei.push.demo.agent.common.HMSAgentActivity"
      android:configChanges="orientation|locale|screenSize|layoutDirection|fontScale"
      android:excludeFromRecents="true"
      android:exported="false"
      android:hardwareAccelerated="true"
      android:theme="@android:style/Theme.Translucent">
      <meta-data
        android:name="hwc-theme"
        android:value="androidhwext:style/Theme.Emui.Translucent" />
    </activity>

    <!-- 接入HMSSDK 需要注册的activity -->
    <activity
      android:name="com.huawei.hms.activity.BridgeActivity"
      android:configChanges="orientation|locale|screenSize|layoutDirection|fontScale"
      android:excludeFromRecents="true"
      android:exported="false"
      android:hardwareAccelerated="true"
      android:theme="@android:style/Theme.Translucent">
      <meta-data
        android:name="hwc-theme"
        android:value="androidhwext:style/Theme.Emui.Translucent" />
    </activity>

    <!-- 接入HMSSDK 需要注册的activity -->
    <activity
      android:name="com.huawei.updatesdk.service.otaupdate.AppUpdateActivity"
      android:configChanges="orientation|screenSize"
      android:exported="false"
      android:theme="@style/upsdkDlDialog">
      <meta-data
        android:name="hwc-theme"
        android:value="androidhwext:style/Theme.Emui.Translucent.NoTitleBar" />
    </activity>

    <!-- 接入HMSSDK 需要注册的activity -->
    <activity
      android:name="com.huawei.updatesdk.support.pm.PackageInstallerActivity"
      android:configChanges="orientation|keyboardHidden|screenSize"
      android:exported="false"
      android:theme="@style/upsdkDlDialog">
      <meta-data
        android:name="hwc-theme"
        android:value="androidhwext:style/Theme.Emui.Translucent" />
    </activity>
    ```

4. 注册Receiver

    ```xml
    <!-- 接入HMSSDK PUSH模块需要注册，第三方相关 :接收Push消息（注册、Push消息、Push连接状态）广播，
    此receiver类需要开发者自己创建并继承com.huawei.hms.support.api.push.PushReceiver类，
    参考示例代码中的类：com.huawei.hmsagent.HuaweiPushRevicer -->
    <receiver android:name=".HuaweiPushRevicer">
      <intent-filter>
        <!-- 必须,用于接收token -->
        <action android:name="com.huawei.android.push.intent.REGISTRATION" />
        <!-- 必须，用于接收消息 -->
        <action android:name="com.huawei.android.push.intent.RECEIVE" />
        <!-- 可选，用于点击通知栏或通知栏上的按钮后触发onEvent回调 -->
        <action android:name="com.huawei.android.push.intent.CLICK" />
        <!-- 可选，查看push通道是否连接，不查看则不需要 -->
        <action android:name="com.huawei.intent.action.PUSH_STATE" />
      </intent-filter>
      <intent-filter>
        <action android:name="android.intent.action.BOOT_COMPLETED" />
      </intent-filter>
    </receiver>

    <!-- 接入HMSSDK PUSH模块需要注册 :接收通道发来的通知栏消息 -->
    <receiver android:name="com.huawei.hms.support.api.push.PushEventReceiver">
      <intent-filter>
        <action android:name="com.huawei.intent.action.PUSH" />
      </intent-filter>
    </receiver>
    ```

5. 注册Provider

    ```xml
    <!-- 接入HMSSDK 需要注册的provider，authorities 一定不能与其他应用一样，
    所以这边 HuaweiPushDemo 要替换上您应用的包名 com.weicools.huawei.push.demo -->
    <provider
      android:name="com.huawei.hms.update.provider.UpdateProvider"
      android:authorities="com.weicools.huawei.push.demo.hms.update.provider"
      android:exported="false"
      android:grantUriPermissions="true" />

    <!-- 接入HMSSDK 需要注册的provider，authorities 一定不能与其他应用一样，
    所以这边 HuaweiPushDemo 要替换上您应用的包名 com.weicools.huawei.push.demo -->
    <provider
      android:name="com.huawei.updatesdk.fileprovider.UpdateSdkFileProvider"
      android:authorities="com.weicools.huawei.push.demo.updateSdk.fileProvider"
      android:exported="false"
      android:grantUriPermissions="true" />
    ```

6. 注册Service

    ```xml
    <!-- 接入HMSSDK 需要注册的应用下载服务 -->
    <service
      android:name="com.huawei.updatesdk.service.deamon.download.DownloadService"
      android:exported="false" />
    ```

### 编码
#### 初始化Agent
![](http://blog-1251678165.coscd.myqcloud.com/2018-05-17-075001.png)
#### 调用connect接口
![](http://blog-1251678165.coscd.myqcloud.com/2018-05-17-075118.png)
#### Token相关操作
参考文档：[客户端开发指南](http://developer.huawei.com/consumer/cn/service/hms/catalog/huaweipush_agent.html?page=hmssdk_huaweipush_devguide_client_agent)

### 其他操作
参考[开发文档](http://developer.huawei.com/consumer/cn/service/hms/catalog/huaweipush_agent.html?page=hmssdk_huaweipush_sdkdownload_agent)或者[源码](https://github.com/lecymeng/HuaweiPushDemo)

### 配置混淆
开发者编译APK时请不要混淆本SDK，避免功能异常

```txt
-ignorewarning
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keep class com.hianalytics.android.**{*;}
-keep class com.huawei.updatesdk.**{*;}
-keep class com.huawei.hms.**{*;}

-keep class com.huawei.gamebox.plugin.gameservice.**{*;}

-keep public class com.huawei.android.hms.agent.** extends android.app.Activity { public *; protected *; }
-keep interface com.huawei.android.hms.agent.common.INoProguard {*;}
-keep class * extends com.huawei.android.hms.agent.common.INoProguard {*;}
```

### 参考
> [华为官方开发文档](http://developer.huawei.com/consumer/cn/service/hms/catalog/huaweipush_agent.html?page=hmssdk_huaweipush_sdkdownload_agent)

[HuaweiPushDemo源码](https://github.com/lecymeng/HuaweiPushDemo)