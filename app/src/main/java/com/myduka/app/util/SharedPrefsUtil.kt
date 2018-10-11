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

package com.myduka.app.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

/**
 * Created  on 7/4/2017.
 */

class SharedPrefsUtil(context: Context) {
    private val pref: SharedPreferences
    private val editor: SharedPreferences.Editor

    val firebaseRegistrationID: String?
        get() = pref.getString("regId", null)

    val isFirstTime: Boolean
        get() = pref.getBoolean("firstTime", false)

    init {
        pref = context.getSharedPreferences(SHARED_PREFER_FILE_NAME, MODE_PRIVATE)
        editor = pref.edit()
        editor.apply()
    }

    fun saveFirebaseRegistrationID(firebaseRegId: String) {
        editor.putString("regId", firebaseRegId)
        editor.commit()
    }

    fun saveIsFirstTime(isFirstTime: Boolean) {
        editor.putBoolean("firstTime", isFirstTime)
        editor.commit()
    }

    companion object {
        private const val SHARED_PREFER_FILE_NAME = "keys"
    }
}
