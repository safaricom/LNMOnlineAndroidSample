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

package com.myduka.app.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.myduka.app.R;
import com.myduka.app.api.ApiUtils;
import com.myduka.app.api.model.STKPush;
import com.myduka.app.utils.SharedPrefsUtil;
import com.myduka.app.api.services.STKPushService;
import com.myduka.app.app.Config;
import com.myduka.app.app.Utils;
import com.myduka.app.utils.NotificationUtils;
import com.myduka.app.utils.RecyclerviewListDecorator;

import junit.framework.Assert;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements PriceTransfer {

    int price_total = 0;
    private ArrayList<String> sums = new ArrayList<>();

    @BindView(R.id.cart_list)
    RecyclerView cart_list;
    @BindView(R.id.txt_response)
    TextView txt_response;
    @BindView(R.id.buttonCheckout)
    Button buttonCheckout;

    private Editable editable;
    private static final String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    STKPushService stkPushService;
    private String token = null;
    private String phone_number = "";
    private String regId;
    private LinearLayoutManager layoutManager;
    ArrayList<String> cart_items;
    ArrayList<String> cart_prices;
    ArrayList<Integer> prices = new ArrayList<>();
    private ArrayList<String> slideshow;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        dialog = new ProgressDialog(this);


        getToken(Config.CONSUMER_KEY, Config.CONSUMER_SECRET);

        layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);

        cart_list.setLayoutManager(layoutManager);
        cart_list.addItemDecoration(new RecyclerviewListDecorator(MainActivity.this,
                LinearLayoutManager.HORIZONTAL));
// Creating fruit ArrayList.
        cart_items = new ArrayList<>();
        cart_items.add("Tomatoes");
        cart_items.add("Apples");
        cart_items.add("Bananas");

// creating price ArrayList
        cart_prices = new ArrayList<>();
        cart_prices.add("1");
        cart_prices.add("200");
        cart_prices.add("120");

        cart_list.setAdapter(new CartListAdapter(MainActivity.this, cart_items, cart_prices, MainActivity.this));

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    getFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");
                    //Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                    createNotification(message);
                    showResultdialog(message);
                }
            }
        };

        getFirebaseRegId();

        buttonCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prices.size() > 0)
                    //Calling getPhoneNumber method.
                    getPhoneNumber();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_checkout:
                Snackbar.make(findViewById(R.id.pay_layout), "Item 1 Selected", Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.RED)
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String getToken(String clientKey, String clientSecret) {

        try {
            String keys = clientKey + ":" + clientSecret;
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(Config.TOKEN_URL)
                    .get()
                    .addHeader("authorization", "Basic " + Base64.encodeToString(keys.getBytes(), Base64.NO_WRAP))
                    .addHeader("cache-control", "no-cache")
                    .build();

            client.newCall(request)
                    .enqueue(new okhttp3.Callback() {
                        @Override
                        public void onFailure(okhttp3.Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "Fetching token failed", Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        
                        @Override
                        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                            String res = response.body().string();
                            token = res;

                            JsonParser jsonParser = new JsonParser();
                            JsonObject jo = (JsonObject) jsonParser.parse(token).getAsJsonObject();
                            Assert.assertNotNull(jo);
                            //Log.e("Token", token + jo.get("access_token"));
                            token = jo.get("access_token").getAsString();
                            stkPushService = ApiUtils.getTasksService(token);
                        }

                    });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Please add your app credentials", Toast.LENGTH_LONG).show();
        }
        return token;
    }

    public void getPhoneNumber() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Customer's Safaricom phone number (2547XXX) to checkout Kshs " + String.valueOf(getTotal(prices)));

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_PHONE);
        input.setHint("07xxxxxxxx");
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                phone_number = input.getText().toString();
                try {
                    performSTKPush(phone_number);
                }catch (Exception e){
                    Toast.makeText(MainActivity.this,"Error fetching token", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Clear Cart", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                prices.clear();
                buttonCheckout.setText("Checkout");
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void performSTKPush(String phone_number) {
        dialog.setMessage("Processing..");
        dialog.setTitle("Please Wait");
        dialog.setIndeterminate(true);
        dialog.show();
        String timestamp = Utils.getTimestamp();
        STKPush stkPush = new STKPush(Config.BUSINESS_SHORT_CODE,
                Utils.getPassword(Config.BUSINESS_SHORT_CODE, Config.PASSKEY, timestamp),
                timestamp,
                Config.TRANSACTION_TYPE,
                String.valueOf(getTotal(prices)),
                Utils.sanitizePhoneNumber(phone_number),
                Config.PARTYB,
                Utils.sanitizePhoneNumber(phone_number),
                Config.CALLBACKURL + regId,
                "test", //The account reference
                "test"); //The transaction description

        Log.e("Party B", phone_number);

        Call<STKPush> call = stkPushService.sendPush(stkPush);
        call.enqueue(new Callback<STKPush>() {
            @Override
            public void onResponse(Call<STKPush> call, Response<STKPush> response) {
                dialog.dismiss();
                try {
                    //Log.e("Response SUccess", response.toString());
                    if (response.isSuccessful()) {
                        Log.d(TAG, "post submitted to API." + response.body().toString());
                    } else {
                        Log.e("Response", response.errorBody().string());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<STKPush> call, Throwable t) {
                dialog.dismiss();
                Log.e(TAG, "Unable to submit post to API." + t.getMessage());
                t.printStackTrace();
                Log.e("Error message", t.getLocalizedMessage());
            }
        });
        //Log.e("Method end", "method end");
    }

    private void getFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        regId = pref.getString("regId", null);

        //Log.e(TAG, "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId)) {
            SharedPrefsUtil sharedPrefsUtil = new SharedPrefsUtil(MainActivity.this);
            sharedPrefsUtil.createKey(regId);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void setPrices(ArrayList<Integer> prices) {
        //Log.e("Size of list", prices.size() + " ");
        this.prices = prices;
        //Log.e("Size of NEW list", this.prices.size() + " ");

        buttonCheckout.setText("Checkout Kshs. " + String.valueOf(getTotal(prices)));
    }

    public int getTotal(ArrayList<Integer> prices) {
        int sum = 0;
        for (int i = 0; i < prices.size(); i++) {
            sum = sum + prices.get(i);
            //Log.e("value to calculate", String.valueOf(prices.get(i)));
        }

        if (prices.size() == 0) {
            Toast.makeText(MainActivity.this, String.valueOf("Total: " + sum), Toast.LENGTH_SHORT).show();
            return 0;
        } else
            return sum;
    }

    public void createNotification(String content) {
        Notification noti = new Notification.Builder(this)
                .setContentTitle(content)
                .setContentText("Subject").setSmallIcon(R.mipmap.ic_launcher).build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(1, noti);

    }

    public void showResultdialog(String result) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(!prefs.getBoolean("firstTime", false)) {
            // run your one time code
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();

            new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Payment Notification")
                    .setContentText("Payment made succesfully")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean("firstTime", false);
                            editor.commit();
                        }
                    })
                    .show();
        }
    }
}
