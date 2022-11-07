/**
 * Copyright (c) 2017-present, Wonday (@wonday.org)
 * All rights reserved.
 *
 * This source code is licensed under the MIT-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package org.wonday.aliyun.push;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.BroadcastReceiver;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.Promise;
import com.facebook.react.common.ReactConstants;
import com.facebook.common.logging.FLog;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.alibaba.sdk.android.push.CommonCallback;
import me.leolin.shortcutbadger.ShortcutBadger;

import org.wonday.aliyun.push.MIUIUtils;

public class AliCloudPushModule extends ReactContextBaseJavaModule implements LifecycleEventListener {
    private final ReactApplicationContext context;
    private int badgeNumber;

    public AliCloudPushModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.context = reactContext;
        this.badgeNumber = 0;
        AliCloudPushMessageReceiver.context = reactContext;
        ThirdPartMessageActivity.context = reactContext;

        context.addLifecycleEventListener(this);
    }

    // module name
    @Override
    public String getName() {
        return "AliCloudPush";
    }

    /**
     * 适配iOS调用初始化方法
     */
    @ReactMethod
    public void initCloudPush(final Promise promise) {
        promise.resolve("");
    }


    /**
     * 获取设备标识
     * 获取设备唯一标识，指定设备推送时需要
     */
    @ReactMethod
    public void getDeviceId(final Promise promise) {
        String deviceID = PushServiceFactory.getCloudPushService().getDeviceId();
        if (deviceID != null && deviceID.length() > 0) {
            promise.resolve(deviceID);
        } else {
            // 或许还没有初始化完成，等3秒钟再次尝试
            try{
                Thread.sleep(3000);
                deviceID = PushServiceFactory.getCloudPushService().getDeviceId();
                if (deviceID != null && deviceID.length() > 0) {
                    promise.resolve(deviceID);
                    return;
                }
            } catch (Exception e) {
            }
            promise.reject("getDeviceId() failed.");
        }
    }

    /**
     * 设置角标
     * @param badgeNumber 角标数量
     */
    @ReactMethod
    public void setBadgeNumber(int badgeNumber, final Promise promise) {
        // 小米特殊处理
        if (MIUIUtils.isMIUI(getReactApplicationContext())) {
            FLog.d(ReactConstants.TAG, "setApplicationIconBadgeNumber for xiaomi");
            if (badgeNumber==0) {
                promise.resolve("");
                return;
            }
            try {
                MIUIUtils.setBadgeNumber(this.context, getCurrentActivity().getClass(), badgeNumber);
                this.badgeNumber = badgeNumber;
                promise.resolve("");
            } catch (Exception e) {
                promise.reject(e.getMessage());
            }
        } else {
            FLog.d(ReactConstants.TAG, "setApplicationIconBadgeNumber for normal");
            try {
                ShortcutBadger.applyCount(this.context, badgeNumber);
                this.badgeNumber = badgeNumber;
                promise.resolve("");
            } catch (Exception e){
                promise.reject(e.getMessage());
            }
        }
    }

    /**
     * 获取角标
     * @param callback 回调
     */
    @ReactMethod
    public void getBadgeNumber(Callback callback) {
        callback.invoke(this.badgeNumber);
    }

    /**
     * 绑定账号
     * 将应用内账号和推送通道相关联，可以实现按账号的定点消息推送
     * @param account 账号名
     */
    @ReactMethod
    public void bindAccount(String account, final Promise promise) {
        PushServiceFactory.getCloudPushService().bindAccount(account, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                promise.resolve(response);
            }
            @Override
            public void onFailed(String code, String message) {
                promise.reject(code, message);
            }
        });
    }

    /**
     * 解绑账号
     * 将应用内账号和推送通道取消关联
     */
    @ReactMethod
    public void unbindAccount(final Promise promise) {
        PushServiceFactory.getCloudPushService().unbindAccount(new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                promise.resolve(response);
            }
            @Override
            public void onFailed(String code, String message) {
                promise.reject(code, message);
            }
        });
    }

    /**
     * 向指定目标添加自定义标签
     * 支持向本设备/本设备绑定账号/别名添加自定义标签，目标类型由target指定
     * @param target 目标类型可选值：
     *               1：本设备
     *               2：本设备绑定的账号
     *               3：别名
     *               目标类型可选值（SDK版本V2.3.5及以上版本）：
     *               CloudPushService.DEVICE_TARGET：本设备
     *               CloudPushService.ACCOUNT_TARGET：本账号
     *               CloudPushService.ALIAS_TARGET：别名
     * @param tags   标签名
     * @param alias  别名（仅当target = 3时生效）
     */
    @ReactMethod
    public void bindTag(int target, ReadableArray tags, String alias, final Promise promise) {
        String[] tagStrs = new String[tags.size()];
        for(int i=0; i<tags.size();i++) tagStrs[i] = tags.getString(i);
        PushServiceFactory.getCloudPushService().bindTag(target, tagStrs, alias, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                promise.resolve(response);
            }
            @Override
            public void onFailed(String code, String message) {
                promise.reject(code, message);
            }
        });
    }

    /**
     *  删除指定目标的自定义标签
     *  支持从本设备/本设备绑定账号/别名删除自定义标签，目标类型由target指定
     * @param target 目标类型可选值：
     *               1：本设备
     *               2：本设备绑定的账号
     *               3：别名
     *               目标类型可选值（SDK版本V2.3.5及以上版本）：
     *               CloudPushService.DEVICE_TARGET：本设备
     *               CloudPushService.ACCOUNT_TARGET：本账号
     *               CloudPushService.ALIAS_TARGET：别名
     *  @param tags  标签名
     *  @param alias 别名（仅当target = 3时生效）
     */
    @ReactMethod
    public void unbindTag(int target, ReadableArray  tags, String alias, final Promise promise) {
        String[] tagStrs = new String[tags.size()];
        for(int i=0; i<tags.size();i++) tagStrs[i] = tags.getString(i);
        PushServiceFactory.getCloudPushService().unbindTag(target, tagStrs, alias, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                promise.resolve(response);
            }
            @Override
            public void onFailed(String code, String message) {
                promise.reject(code, message);
            }
        });
    }

    /**
     * 查询绑定标签
     * 查询目标绑定的标签，当前仅支持查询设备标签
     * @param target 目标类型可选值：
     *               1：本设备
     *               目标类型可选值（SDK版本V2.3.5及以上版本）：
     *               CloudPushService.DEVICE_TARGET：本设备
     */
    @ReactMethod
    public void listTags(int target, final Promise promise) {
        PushServiceFactory.getCloudPushService().listTags(target, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                promise.resolve(response);
            }
            @Override
            public void onFailed(String code, String message) {
                promise.reject(code, message);
            }
        });
    }

    /**
     * 添加别名
     * 为设备添加别名
     * @param alias 别名名称
     */
    @ReactMethod
    public void addAlias(String alias, final Promise promise) {
        PushServiceFactory.getCloudPushService().addAlias(alias, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                promise.resolve(response);
            }
            @Override
            public void onFailed(String code, String message) {
                promise.reject(code, message);
            }
        });
    }

    /**
     * 删除别名
     * 删除设备别名
     * @param alias 别名名称 （alias = null or alias.length = 0 时，删除设备全部别名）
     */
    @ReactMethod
    public void removeAlias(String alias, final Promise promise) {
        PushServiceFactory.getCloudPushService().removeAlias(alias, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                promise.resolve(response);
            }
            @Override
            public void onFailed(String code, String message) {
                promise.reject(code, message);
            }
        });
    }

    /**
     * 查询别名
     * 查询设备别名
     */
    @ReactMethod
    public void listAliases(final Promise promise) {
        PushServiceFactory.getCloudPushService().listAliases(new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                promise.resolve(response);
            }
            @Override
            public void onFailed(String code, String message) {
                promise.reject(code, message);
            }
        });
    }

    @Override
    public void onHostResume() {
        ThirdPartMessageActivity.mainClass = getCurrentActivity().getClass();
    }

    @Override
    public void onHostPause() {
        //小米特殊处理, 处于后台时更新角标，否则会被系统清除，看不到
        if (MIUIUtils.isMIUI(getReactApplicationContext())) {
            FLog.d(ReactConstants.TAG, "onHostPause:setBadgeNumber for xiaomi");
            MIUIUtils.setBadgeNumber(this.context, getCurrentActivity().getClass(), badgeNumber);
        }
    }

    @Override
    public void onHostDestroy() {
        //小米特殊处理, 处于后台时更新角标，否则会被系统清除，看不到
        if (MIUIUtils.isMIUI(getReactApplicationContext())) {
            FLog.d(ReactConstants.TAG, "onHostDestroy:setBadgeNumber for xiaomi");
            MIUIUtils.setBadgeNumber(this.context, getCurrentActivity().getClass(), badgeNumber);
        }
    }

    @ReactMethod
    public void getInitialMessage(final Promise promise){
        promise.resolve(AliCloudPushMessageReceiver.initialMessage);
    }
}
