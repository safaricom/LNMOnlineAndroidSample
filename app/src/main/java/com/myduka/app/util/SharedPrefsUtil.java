/*
 *
 *  * Copyright (C) 2017 Safaricom, Ltd.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.myduka.app.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created  on 7/4/2017.
 */

public class SharedPrefsUtil {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private static final String SHARED_PREFER_FILE_NAME = "keys";

    /**
     * Retrieve data from preference:
     */

    public SharedPrefsUtil(Context context) {
        int PRIVATE_MODE = 0;
        pref = context.getSharedPreferences(SHARED_PREFER_FILE_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.apply();
    }

    public void saveFirebaseRegistrationID(String firebaseRegId){
        editor.putString("regId", firebaseRegId);
        editor.commit();
    }

    public String getirebaseRegistrationID(){
       return pref.getString("regId", null);
    }
}
