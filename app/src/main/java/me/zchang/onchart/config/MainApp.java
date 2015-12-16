package me.zchang.onchart.config;

import android.app.Application;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;


/*
 *    Copyright 2015 Zhehua Chang
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

public class MainApp extends Application {
    public static final String APP_ID = "wx5a0d7c554008997c";
    public static IWXAPI api;
    private PreferenceManager preferenceManager;

    @Override
    public void onCreate() {
        super.onCreate();

        api = WXAPIFactory.createWXAPI(this, APP_ID, true);
        api.registerApp(APP_ID);

        preferenceManager = new PreferenceManager(this);

    }

    public PreferenceManager getPreferenceManager() {
        return preferenceManager;
    }

    public void setPreferenceManager(PreferenceManager preferenceManager) {
        this.preferenceManager = preferenceManager;
    }
}
