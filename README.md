# react-native-ali-cloud-push
[![npm](https://img.shields.io/npm/v/react-native-aliyun-emas.svg?style=flat-square)](https://www.npmjs.com/package/react-native-aliyun-emas)

#### 感谢wonday和evan0204的贡献，此库是在[wonday](https://github.com/wonday/react-native-aliyun-push)和[evan0204](https://github.com/evan0204/react-native-aliyun-emas)库的基础上进行升级维护，优化本地推送功能；

## 官方RN集成文档

#### [阿里云移动推送+ReactNative最佳实践](https://help.aliyun.com/document_detail/434788.html)

## SDK版本更新

#### Android SDK已更新至 3.8.0 [Android SDK发布说明](https://help.aliyun.com/document_detail/434659.html)

#### 目前已完成Android12的匹配升级，升级到阿里云EMAS的SDK 3.7.4版本
#### 注意EMAS SDK 3.7.4 的Android配置发生了变化，[具体参考](https://github.com/evan0204/react-native-aliyun-emas/issues/1)

#### iOS SDK已更新至 1.9.9.7 [iOS SDK发布说明](https://help.aliyun.com/document_detail/434690.html?spm=a2c4g.11186623.0.0.56786268xfl580)

## 前提
使用本组件前提是注册过阿里云移动推送服务，注册过app并取得了appKey及appSecret, 如果要使用ios版还要向苹果公司申请证书并配置好阿里云上的设置。
这里不详细描述，请参考[阿里云移动推送文档](https://help.aliyun.com/document_detail/30054.html)

## 注册方法

### iOS

在 **AppDelegate.m** 文件添加如下代码：

```objc
#import "AliCloudPushManager.h"

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
  ...

  // 下面是添加阿里云推送的代码
  [[AliCloudPushManager sharedInstance] setParams:@"Your appKey"
                                        appSecret:@"Your appSecret"
                                    launchOptions:launchOptions
                createNotificationCategoryHandler:^{}
  ];
  // 添加结束

  return YES;
}

// 下面是添加的代码 阿里云推送

// APNs注册成功回调，将返回的deviceToken上传到CloudPush服务器
- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken
{
  [[AliCloudPushManager sharedInstance] application:application didRegisterForRemoteNotificationsWithDeviceToken:deviceToken];
}


// APNs注册失败回调
- (void)application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error
{
  [[AliCloudPushManager sharedInstance] application:application didFailToRegisterForRemoteNotificationsWithError:error];
}

// 打开／删除通知回调
- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo fetchCompletionHandler:(void (^)(UIBackgroundFetchResult result))completionHandler
{
  [[AliCloudPushManager sharedInstance] application:application didReceiveRemoteNotification:userInfo fetchCompletionHandler:completionHandler];
}


// 请求注册设定后，回调
- (void)application:(UIApplication *)application didRegisterUserNotificationSettings:(UIUserNotificationSettings *)notificationSettings
{
  [[AliCloudPushManager sharedInstance] application:application didRegisterUserNotificationSettings:notificationSettings];
}
// 添加结束

```

Android

在 **AndroidManifest** 文件中设置AppKey、AppSecret：

```xml
<application android:name="*****">
    <meta-data android:name="com.alibaba.app.appkey" android:value="*****"/> <!-- 请填写你自己的- appKey -->
    <meta-data android:name="com.alibaba.app.appsecret" android:value="****"/> <!-- 请填写你自己的appSecret -->
</application>
```

厂商通道：

```xml
<application android:name="*****">
<!-- 华为 -->
<meta-data
    android:name="com.huawei.hms.client.appid"
    android:value="appid=xxxxxx" />

<!-- 荣耀 -->
<meta-data
    android:name="com.hihonor.push.app_id"
    android:value="xxxxx" />

<!-- VIVO -->
<meta-data
    android:name="com.vivo.push.api_key"
    android:value="请填写vivo平台上注册应用的appKey" />
<meta-data
    android:name="com.vivo.push.app_id"
    android:value="请填写vivo平台上注册应用的appID" />
</application>
```

在 **MainApplication.java** 文件中添加如下代码：

```java
import org.wonday.aliyun.push.AliCloudPushPackage;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.alibaba.sdk.android.push.huawei.HuaWeiRegister;
import com.alibaba.sdk.android.push.register.HonorRegister;
import com.alibaba.sdk.android.push.register.VivoRegister;
import com.alibaba.sdk.android.push.register.MiPushRegister;
import com.alibaba.sdk.android.push.register.GcmRegister;

public class MainApplication extends Application implements ReactApplication {

    ...

    @Override
    public void onCreate() {
        super.onCreate();
        
        // 初始化阿里云推送
        PushServiceFactory.init(this);
        // 创建渠道
        this.createNotificationChannel();
        // 注册通道
        this.initCloudChannel(this);
    }
    
    /**
     * 创建通知渠道
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // 通知渠道的id
            String id = "1";
            // 用户可以看到的通知渠道的名字.
            CharSequence name = "name";
            // 用户可以看到的通知渠道的描述
            String description = "description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // 配置通知渠道的属性
            mChannel.setDescription(description);
            // 设置通知出现时的闪灯（如果 android 设备支持的话）
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            // 设置通知出现时的震动（如果 android 设备支持的话）
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            //最后在notification manager中创建该通知渠道
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    /**
     * 注册厂商通道
     */
    private void initThirdParty() {
        // 华为通道注册
        HuaWeiRegister.register(this);

        // VIVO通道注册
        VivoRegister.register(this);

        // 荣耀通道注册
        HonorRegister.register(this);

        // 小米通道注册
        MiPushRegister.register(applicationContext, "小米AppID", "小米AppKey");

        // FCM/GCM
        GcmRegister.register(this, sendId, applicationId, projectId, apiKey); //sendId/applicationId/projectId/apiKey为已获得的参数

        // OPPO通道注册
        OppoRegister.register(this, appKey, appSecret);

        // 魅族通道注册
        MeizuRegister.register(this, "appId", "appkey");
    }

    /**
     * 初始化阿里云推送通道
     * @param applicationContext
     */
    private void initCloudChannel(final Context applicationContext) {
        final CloudPushService pushService = PushServiceFactory.getCloudPushService();

        if (BuildConfig.DEBUG) {
            pushService.setLogLevel(CloudPushService.LOG_DEBUG);
        }

        pushService.setNotificationSmallIcon(R.mipmap.ic_launcher); //设置通知栏小图标， 需要自行添加

        pushService.register(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                // success
                initThirdParty();
            }

            @Override
            public void onFailed(String code, String message) {
                // failed
            }
        });
    }
}
```

## 安装

ReactNative 0.59.x及以前
```
npm install react-native-ali-cloud-push --save
react-native link react-native-ali-cloud-push
```

ReactNative 0.60.x及以后
```
yarn add react-native-ali-cloud-push
```

### 阿里云SDK接口封装

**获取deviceId**

示例:
```javascript
AliCloudPush.getDeviceId()
    .then((deviceId)=>{
        //console.log("deviceId:"+deviceId);
    })
    .catch((error)=>{
        console.log("getDeviceId() failed");
    });
```
**绑定账号**

参数：
- account 待绑定账号

示例:
```javascript
AliCloudPush.bindAccount(account)
    .then((data)=>{
        console.log("bindAccount success");
        console.log(JSON.stringify(data));
    })
    .catch((error)=>{
        console.log("bindAccount error");
        console.log(JSON.stringify(error));
    });
```
**解绑定账号**

示例:
```javascript
AliCloudPush.unbindAccount()
    .then((result)=>{
        console.log("unbindAccount success");
        console.log(JSON.stringify(result));
    })
    .catch((error)=>{
        console.log("bindAccount error");
        console.log(JSON.stringify(error));
    });
```
**绑定标签**

参数：
- target 目标类型，1：本设备；2：本设备绑定账号；3：别名
- tags 标签（数组输入）
- alias 别名（仅当target = 3时生效）

示例:
```javascript
AliCloudPush.bindTag(1,["testtag1","testtag2"],"")
    .then((result)=>{
        console.log("bindTag success");
        console.log(JSON.stringify(result));
    })
    .catch((error)=>{
        console.log("bindTag error");
        console.log(JSON.stringify(error));
    });
```
**解绑定标签**

参数:
- target 目标类型，1：本设备；2：本设备绑定账号；3：别名
- tags 标签（数组输入）
- alias 别名（仅当target = 3时生效）

示例:
```javascript
AliCloudPush.unbindTag(1,["testTag1"],"")
    .then((result)=>{
        console.log("unbindTag succcess");
        console.log(JSON.stringify(result));
    })
    .catch((error)=>{
        console.log("unbindTag error");
        console.log(JSON.stringify(error));
    });
```
**查询当前Tag列表**

参数:
- target 目标类型，1：本设备

示例:
```javascript
AliCloudPush.listTags(1)
    .then((result)=>{
        console.log("listTags success");
        console.log(JSON.stringify(result));
    })
    .catch((error)=>{
        console.log("listTags error");
        console.log(JSON.stringify(error));
    });
```
**添加别名**

参数:
- alias 要添加的别名

示例:
```javascript
AliCloudPush.addAlias("testAlias")
    .then((result)=>{
        console.log("addAlias success");
        console.log(JSON.stringify(result));
    })
    .catch((error)=>{
        console.log("addAlias error");
        console.log(JSON.stringify(error));
    });
```
**删除别名**

参数:
- alias 要移除的别名

示例:
```javascript
AliCloudPush.removeAlias("testAlias")
    .then((result)=>{
        console.log("removeAlias success");
        console.log(JSON.stringify(result));
    })
    .catch((error)=>{
        console.log("removeAlias error");
        console.log(JSON.stringify(error));
    });
```
**查询别名列表**

示例:
```javascript
AliCloudPush.listAliases()
    .then((result)=>{
        console.log("listAliases success");
        console.log(JSON.stringify(result));
    })
    .catch((error)=>{
        console.log("listAliases error");
        console.log(JSON.stringify(error));
    });
```
**设置桌面图标角标数字** (ios支持，android支持绝大部分手机)

参数:
- num角标数字，如果要清除请设置0

示例:
```javascript
AliCloudPush.setBadgeNumber(5);
```
**获取桌面图标角标数字** (ios支持，android支持绝大部分手机)

示例:
```javascript
AliCloudPush.getBadgeNumber((num)=>{
    console.log("ApplicationIconBadgeNumber:" + num);
});
```
**同步角标数到阿里云服务端** (仅ios支持)

参数:
- num角标数字

示例:
```javascript
AliCloudPush.syncBadgeNumer(5);
```
**获取用户是否开启通知设定** (ios 10.0+支持)

示例:
```javascript
AliCloudPush.getAuthorizationStatus((result)=>{
    console.log("AuthorizationStatus:" + result);
});
```

**获取初始消息**

app在未启动时收到通知后，点击通知启动app,
如果在向JS发消息时，JS没准备好或者没注册listener，则先临时保存该消息，
并提供getInitalMessage方法可以获取，在app的JS逻辑完成后可以继续处理该消息

示例:
```javascript
// 处理收到的推送
handleAliCloudPushMessage = React.useCallback((e) => {
    ...
}, [])

React.useEffect(() => {
		// 监听推送事件
    AliCloudPush.addListener(handleAliCloudPushMessage);
    AliCloudPush.getInitialMessage().then((msg) => {
        if(msg){
            handleAliCloudPushMessage(msg);
        }
    });
    return () => {
    	// 移除推送监听事件
    	AliCloudPush.removeListener(handleAliCloudPushMessage);
    }
}, [handleAliCloudPushMessage])
```
