/**
 * Copyright (c) 2017-present, Wonday (@wonday.org)
 * All rights reserved.
 *
 * This source code is licensed under the MIT-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

'use strict';
import React  from 'react';
import {Platform, NativeModules, NativeEventEmitter} from 'react-native';

const {AliCloudPush: AliCloudPushNative} = NativeModules
const LocalEventEmitter =  new NativeEventEmitter(AliCloudPushNative);

let listeners = {};

let id = 0;
const META = '__listener_id';

function getKey(listener, type){
    if (!listener.hasOwnProperty(META)){
        if (!Object.isExtensible(listener)) {
            return 'F';
        }
        Object.defineProperty(listener, META, {
            value: `L${type}${++id}`
        });
    }
    return listener[META];
}

export default class AliCloudPush {
    static initCloudPush = (params) => {
        if (Platform.OS === 'android') {
            return new Promise((resolve) => resolve("iOS only"));
        }
        return AliCloudPushNative.initCloudPush(params);
    }

    static getDeviceId = () => {
        return AliCloudPushNative.getDeviceId();
    }

    static getInitialMessage = () => {
        return AliCloudPushNative.getInitialMessage().then(e => {
            if(e && e.extraStr) {
                let extras = JSON.parse(e.extraStr);
                if (extras) {
                    if (extras.badge) {
                        let badgeNumber = parseInt(extras.badge);
                        if (!isNaN(badgeNumber)) {
                            AliyunPush.setApplicationIconBadgeNumber(badgeNumber);
                        }
                    }
                    e.extras = extras;
                }
                delete e.extraStr;
            }
            return e;
        });
    }

    static getApplicationIconBadgeNumber = (callback) => {
        AliCloudPushNative.getApplicationIconBadgeNumber(function(args) {
            callback(args);
        });
    }

    static setApplicationIconBadgeNumber = (num) => {
        AliCloudPushNative.setApplicationIconBadgeNumber(num);
    }

    static syncBadgeNum = (num) => {
        if(Platform.OS === 'android') {
            return;
        }
        AliCloudPushNative.syncBadgeNum(num);
    }

    static bindAccount = (account) => {
        return AliCloudPushNative.bindAccount(account);
    }

    static unbindAccount = () => {
        return AliCloudPushNative.unbindAccount();
    }

    static bindTag = (target, tags, alias) => {
        return AliCloudPushNative.bindTag(target, tags, alias);
    }

    static unbindTag = (target, tags, alias) => {
        return AliCloudPushNative.unbindTag(target, tags, alias);
    }

    static listTags = (target) => {
        return AliCloudPushNative.listTags(target);
    }

    static addAlias = (alias) => {
        return AliCloudPushNative.addAlias(alias);
    }

    static removeAlias = (alias) => {
        return AliCloudPushNative.removeAlias(alias);
    }

    static listAliases = () => {
        return AliCloudPushNative.listAliases();
    }

    static getAuthorizationStatus = (callback) => {
        if(Platform.OS === 'android') {
            // android always return true
            callback(true);
        } else {
            AliCloudPushNative.getAuthorizationStatus(function(args) {
                callback(args);
            });
        }
    }

    static addListener = (callback) => {
        AliCloudPush._addListener(callback,"cloudPushReceived");
    };

    static removeListener = (callback) => {
        AliCloudPush._removeListener(callback, "cloudPushReceived");
    };

    static removeAllListeners = () => {
        for(const key in listeners){
            listeners[key].remove();
            listeners[key] = null;
        }
    };

    static _addListener = (callback, type) => {
        const key = getKey(callback, type);
        listeners[key] = LocalEventEmitter.addListener(type, (e) => {
                // convert json string to obj
                if (e.extraStr) {
                    let extras = JSON.parse(e.extraStr);
                    if (extras) {
                        if (extras.badge) {
                            let badgeNumber = parseInt(extras.badge);
                            if (!isNaN(badgeNumber)) {
                                AliCloudPush.setApplicationIconBadgeNumber(badgeNumber);
                            }
                        }
                        e.extras = extras;
                    }
                    delete e.extraStr;
                    callback(e);
                } else {
                    callback(e);
                }
            });
    };

    static _removeListener = (callback, type) => {
        const key = getKey(callback, type);
        if (!listeners[key]) {
            return;
        }
        listeners[key].remove();
        listeners[key] = null;
    };
}
