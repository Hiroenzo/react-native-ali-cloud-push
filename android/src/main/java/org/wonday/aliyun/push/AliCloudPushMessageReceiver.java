/**
 * Copyright (c) 2017-present, Wonday (@wonday.org)
 * All rights reserved.
 *
 * This source code is licensed under the MIT-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package org.wonday.aliyun.push;

import android.content.Context;

import com.alibaba.sdk.android.push.MessageReceiver;
import com.alibaba.sdk.android.push.notification.CPushMessage;
import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.Map;

import javax.annotation.Nullable;

public class AliCloudPushMessageReceiver extends MessageReceiver {
    public static ReactApplicationContext context;
    public static AliCloudPushMessageReceiver instance;

    private final String ALICLOUD_PUSH_TYPE_MESSAGE = "message";
    private final String ALICLOUD_PUSH_TYPE_NOTIFICATION = "notification";

    public AliCloudPushMessageReceiver() {
        super();
        instance = this;
    }

    @Override
    public void onMessage(Context context, CPushMessage cPushMessage) {

        WritableMap params = Arguments.createMap();
        params.putString("messageId", cPushMessage.getMessageId());
        params.putString("body", cPushMessage.getContent());
        params.putString("title", cPushMessage.getTitle());
        params.putString("type", ALICLOUD_PUSH_TYPE_MESSAGE);

        sendEvent("cloudPushReceived", params);
    }

    @Override
    public void onNotification(Context context, String title, String content, Map<String, String> extraMap) {
        FLog.d(ReactConstants.TAG, "onNotification.");

        WritableMap params = Arguments.createMap();
        params.putString("body", content);
        params.putString("title", title);

        WritableMap extraWritableMap = Arguments.createMap();
        for (Map.Entry<String, String> entry : extraMap.entrySet()) {
            extraWritableMap.putString(entry.getKey(),entry.getValue());
        }
        params.putMap("extras", extraWritableMap);

        params.putString("type", ALICLOUD_PUSH_TYPE_NOTIFICATION);

        sendEvent("cloudPushReceived", params);
    }

    @Override
    public void onNotificationOpened(Context context, String title, String content, String extraMap) {
        FLog.d(ReactConstants.TAG, "onNotificationOpened.");

        WritableMap params = Arguments.createMap();
        params.putString("body", content);
        params.putString("title", title);
        params.putString("extraStr", extraMap);

        params.putString("type", ALICLOUD_PUSH_TYPE_NOTIFICATION);
        params.putString("actionIdentifier", "opened");

        sendEvent("cloudPushReceived", params);
    }

    @Override
    protected void onNotificationClickedWithNoAction(Context context, String title, String content, String extraMap) {
        FLog.d(ReactConstants.TAG, "onNotificationClickedWithNoAction.");

        WritableMap params = Arguments.createMap();
        params.putString("body", content);
        params.putString("title", title);
        params.putString("extraStr", extraMap);

        params.putString("type", ALICLOUD_PUSH_TYPE_NOTIFICATION);
        params.putString("actionIdentifier", "opened");

        sendEvent("AliCloudReceived", params);
    }

    @Override
    protected void onNotificationRemoved(Context context, String messageId){
        FLog.d(ReactConstants.TAG, "onNotificationRemoved: messageId=" +  messageId);

        WritableMap params = Arguments.createMap();
        params.putString("messageId", messageId);

        params.putString("type", ALICLOUD_PUSH_TYPE_NOTIFICATION);
        params.putString("actionIdentifier", "removed");

        sendEvent("cloudPushReceived", params);
    }

    @Override
    protected void onNotificationReceivedInApp(Context context, String title, String content, Map<String, String> extraMap, int openType, String openActivity, String openUrl) {
        FLog.d(ReactConstants.TAG, "onNotificationReceivedInApp");

        WritableMap params = Arguments.createMap();
        params.putString("content", content);
        params.putString("title", title);
        params.putString("openType", String.valueOf(openType));
        params.putString("openActivity", openActivity);
        params.putString("openUrl", openUrl);

        WritableMap extraWritableMap = Arguments.createMap();
        for (Map.Entry<String, String> entry : extraMap.entrySet()) {
            extraWritableMap.putString(entry.getKey(),entry.getValue());
        }
        params.putMap("extras", extraWritableMap);

        params.putString("type", ALICLOUD_PUSH_TYPE_NOTIFICATION);

        sendEvent("cloudPushReceived", params);
    }

    private void sendEvent(String eventName, @Nullable WritableMap params) {
        if (context == null) {
            params.putString("appState", "background");
            initialMessage = params;
            FLog.d(ReactConstants.TAG, "reactContext==null");
        }else{
            context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
        }
    }
    public static WritableMap initialMessage = null;

}
