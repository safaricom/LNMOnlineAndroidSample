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

package com.myduka.app.ui.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import com.google.firebase.messaging.FirebaseMessaging
import com.myduka.app.R
import com.myduka.app.util.NotificationUtils
import com.myduka.app.util.SharedPrefsUtil

import com.myduka.app.util.AppConstants.PUSH_NOTIFICATION
import com.myduka.app.util.AppConstants.REGISTRATION_COMPLETE
import com.myduka.app.util.AppConstants.TOPIC_GLOBAL

class NotificationActivity : AppCompatActivity() {
    private lateinit var mRegistrationBroadcastReceiver: BroadcastReceiver
    private lateinit var txtMessage: TextView
    private lateinit var txtRegId: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        txtRegId = findViewById(R.id.txt_reg_id)
        txtMessage = findViewById(R.id.txt_push_message)

        mRegistrationBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                // checking for type intent filter
                if (intent.action == REGISTRATION_COMPLETE) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_GLOBAL)

                    displayFirebaseRegId()

                } else if (intent.action == PUSH_NOTIFICATION) {
                    // new push notification is received
                    val message = intent.getStringExtra("message")
                    Toast.makeText(applicationContext, "Push notification: $message", Toast.LENGTH_LONG).show()
                    txtMessage.text = message
                }
            }
        }

        displayFirebaseRegId()
    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    private fun displayFirebaseRegId() {
        val sharedPrefsUtil = SharedPrefsUtil(this)
        val regId = sharedPrefsUtil.firebaseRegistrationID
        if (!TextUtils.isEmpty(regId))
            txtRegId.setText("Firebase Reg Id: $regId")
        else
            txtRegId.setText("Firebase Reg Id is not received yet!")
    }

    override fun onResume() {
        super.onResume()

        // register GCM registration complete receiver.
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                IntentFilter(REGISTRATION_COMPLETE))

        // register new push message receiver.
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                IntentFilter(PUSH_NOTIFICATION))

        // clear the notification area when the app is opened.
        NotificationUtils.clearNotifications(applicationContext)
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver)
        super.onPause()
    }
}
