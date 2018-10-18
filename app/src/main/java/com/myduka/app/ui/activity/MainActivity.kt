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

import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.InputType
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessaging
import com.myduka.app.R
import com.myduka.app.api.ApiClient
import com.myduka.app.api.model.AccessToken
import com.myduka.app.api.model.STKPush
import com.myduka.app.ui.RecyclerViewListDecorator
import com.myduka.app.ui.adapter.CartListAdapter
import com.myduka.app.ui.callback.PriceTransfer
import com.myduka.app.util.AppConstants.BUSINESS_SHORT_CODE
import com.myduka.app.util.AppConstants.CALLBACKURL
import com.myduka.app.util.AppConstants.PARTYB
import com.myduka.app.util.AppConstants.PASSKEY
import com.myduka.app.util.AppConstants.PUSH_NOTIFICATION
import com.myduka.app.util.AppConstants.REGISTRATION_COMPLETE
import com.myduka.app.util.AppConstants.TOPIC_GLOBAL
import com.myduka.app.util.AppConstants.TRANSACTION_TYPE
import com.myduka.app.util.NotificationUtils
import com.myduka.app.util.SharedPrefsUtil
import com.myduka.app.util.Utils
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*

class MainActivity : AppCompatActivity(), PriceTransfer {

    private lateinit var mFireBaseRegId: String
    private lateinit var mProgressDialog: ProgressDialog
    private lateinit var mSharedPrefsUtil: SharedPrefsUtil
    private lateinit var mApiClient: ApiClient

    private var mPriceArrayList = ArrayList<Int>()

    private val mRegistrationBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // checking for type intent filter
            if (intent.action == REGISTRATION_COMPLETE) {
                // gcm successfully registered
                // now subscribe to `global` topic to receive app wide notifications
                FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_GLOBAL)
                getFirebaseRegId()

            } else if (intent.action == PUSH_NOTIFICATION) {
                val message = intent.getStringExtra("message")
                NotificationUtils.createNotification(applicationContext, message)
                showResultDialog(message)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        supportActionBar?.title = getString(R.string.home_toolbar)

        buttonCheckout.setOnClickListener {
            //Calling getPhoneNumber method.
            if (mPriceArrayList.size > 0) showCheckoutDialog()
        }

        mProgressDialog = ProgressDialog(this)
        mSharedPrefsUtil = SharedPrefsUtil(this)
        mApiClient = ApiClient()

        mApiClient.setIsDebug(true) //Set True to enable logging, false to disable.

        getAccessToken()

        val cartItems = ArrayList<String>().apply {
            add("Tomatoes")
            add("Apples")
            add("Bananas")
        }

        val cartPrices = ArrayList<String>().apply {
            add("1")
            add("200")
            add("120")
        }

        cart_list.apply {
            this.layoutManager = LinearLayoutManager(this@MainActivity,
                    LinearLayoutManager.VERTICAL, false)

            addItemDecoration(RecyclerViewListDecorator(this@MainActivity,
                    LinearLayoutManager.HORIZONTAL))

            adapter = CartListAdapter(this@MainActivity, cartItems, cartPrices,
                    this@MainActivity)
        }

        getFirebaseRegId()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)//Menu Resource, Menu
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mRegistrationBroadcastReceiver)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_checkout -> {
                Snackbar.make(pay_layout, "Item 1 Selected", Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.RED)
                        .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getAccessToken() {
        mApiClient.setGetAccessToken(true)
        mApiClient.mpesaService().getAccessToken().enqueue(object : Callback<AccessToken> {
            override fun onResponse(call: Call<AccessToken>, response: Response<AccessToken>) {

                if (response.isSuccessful) {
                    mApiClient.setAuthToken(response.body()?.accessToken)
                }
            }

            override fun onFailure(call: Call<AccessToken>, t: Throwable) {

            }
        })
    }

    private fun showCheckoutDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.checkout_dialog_title, getTotal(mPriceArrayList)))

        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_PHONE
            hint = getString(R.string.hint_phone_number)
        }
        builder.setView(input)

        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            val phoneNumber = input.text.toString()
            performSTKPush(phoneNumber)
        }
        builder.setNegativeButton(getString(R.string.clear_cart)) { dialog, _ ->
            mPriceArrayList.clear()
            buttonCheckout.text = getString(R.string.checkout)
            dialog.cancel()
        }

        builder.show()
    }

    private fun performSTKPush(phone_number: String) {
        mProgressDialog.setMessage(getString(R.string.dialog_message_processing))
        mProgressDialog.setTitle(getString(R.string.title_wait))
        mProgressDialog.isIndeterminate = true
        mProgressDialog.show()
        val timestamp = Utils.timestamp
        val stkPush = STKPush(
                BUSINESS_SHORT_CODE,
                Utils.getPassword(BUSINESS_SHORT_CODE, PASSKEY, timestamp),
                timestamp,
                TRANSACTION_TYPE,
                getTotal(mPriceArrayList).toString(),
                Utils.sanitizePhoneNumber(phone_number),
                PARTYB,
                Utils.sanitizePhoneNumber(phone_number),
                CALLBACKURL + mFireBaseRegId,
                "test", //The account reference
                "test"  //The transaction description
        )

        mApiClient.setGetAccessToken(false)

        mApiClient.mpesaService().sendPush(stkPush).enqueue(object : Callback<STKPush> {
            override fun onResponse(call: Call<STKPush>, response: Response<STKPush>) {
                mProgressDialog.dismiss()
                try {
                    if (response.isSuccessful) {
                        Timber.d("post submitted to API. %s", response.body())
                    } else {
                        Timber.e("Response %s", response.errorBody()!!.string())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<STKPush>, t: Throwable) {
                mProgressDialog.dismiss()
                Timber.e(t)
            }
        })
    }

    private fun getFirebaseRegId() {
        mFireBaseRegId = mSharedPrefsUtil.firebaseRegistrationID.toString()

        if (!TextUtils.isEmpty(mFireBaseRegId)) {
            mSharedPrefsUtil.saveFirebaseRegistrationID(mFireBaseRegId)
        }
    }

    override fun onResume() {
        super.onResume()
        // register GCM registration complete receiver
        registerReceiver(mRegistrationBroadcastReceiver, IntentFilter(REGISTRATION_COMPLETE))

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        registerReceiver(mRegistrationBroadcastReceiver, IntentFilter(PUSH_NOTIFICATION))

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(applicationContext)
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver)
        super.onPause()
    }

    override fun setPrices(prices: ArrayList<Int>) {
        this.mPriceArrayList = prices
        buttonCheckout.text = getString(R.string.checkout_value, getTotal(prices))
    }

    private fun getTotal(prices: ArrayList<Int>): Int {
        var sum = 0
        prices.forEach { sum += it }
        return if (prices.size == 0) {
            Toast.makeText(this@MainActivity, "Total: $sum", Toast.LENGTH_SHORT).show()
            0
        } else
            sum
    }

    fun showResultDialog(result: String) {
        Timber.d(result)
        if (!mSharedPrefsUtil.isFirstTime) {
            // run your one time code
            mSharedPrefsUtil.saveIsFirstTime(true)

            SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText(getString(R.string.title_success))
                    .setContentText(getString(R.string.dialog_message_success))
                    .setConfirmClickListener { sDialog ->
                        sDialog.dismissWithAnimation()
                        mSharedPrefsUtil.saveIsFirstTime(false)
                    }
                    .show()
        }
    }
}
