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

package com.myduka.app.service

import android.content.Intent
import android.content.SharedPreferences
import android.support.v4.content.LocalBroadcastManager

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

import com.myduka.app.util.AppConstants.REGISTRATION_COMPLETE
import com.myduka.app.util.AppConstants.SHARED_PREF

/**
 * Created  on 6/30/2017.
 */

class MyFirebaseInstanceIDService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        super.onTokenRefresh()
        val refreshedToken = FirebaseInstanceId.getInstance().token

        // Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken)

        // sending reg id to your server
        sendRegistrationToServer(refreshedToken)

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        val registrationComplete = Intent(REGISTRATION_COMPLETE).apply {
            putExtra("token", refreshedToken)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete)
    }

    private fun sendRegistrationToServer(token: String?) {
        // sending gcm token to server
        //Log.e(TAG, "sendRegistrationToServer: " + token);
    }

    /**
     * Setting values in Preference:
     */

    private fun storeRegIdInPref(token: String?) {
        val pref = applicationContext.getSharedPreferences(SHARED_PREF, 0)
        pref.edit().apply {
            putString("regId", token)
            apply()
        }
    }

    companion object {
        private val TAG = MyFirebaseInstanceIDService::class.java.simpleName
    }
}
