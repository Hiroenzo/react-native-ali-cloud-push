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

**初始化 (仅ios支持)**

参数：
- appKey appKey
- appSecret appSecret
- launchOptions 启动参数
- categoryHandler 自定义消息分类回调方法

示例:
```javascript
AliCloudPush.initCloudPush(params)
    .then(()=>{
        //console.log("init:success");
    })
    .catch((error)=>{
        console.log("init:failed");
    });
```
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
