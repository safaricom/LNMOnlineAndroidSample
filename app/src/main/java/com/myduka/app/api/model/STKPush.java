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

package com.myduka.app.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created  on 5/28/2017.
 */

public class STKPush {

    @SerializedName("BusinessShortCode")
    private String businessShortCode;
    @SerializedName("Password")
    private String password;
    @SerializedName("Timestamp")
    private String timestamp;
    @SerializedName("TransactionType")
    private String transactionType;
    @SerializedName("Amount")
    private String amount;
    @SerializedName("PartyA")
    private String partyA;
    @SerializedName("PartyB")
    private String partyB;
    @SerializedName("PhoneNumber")
    private String phoneNumber;
    @SerializedName("CallBackURL")
    private String callBackURL;
    @SerializedName("AccountReference")
    private String accountReference;
    @SerializedName("TransactionDesc")
    private String transactionDesc;

    public STKPush(String businessShortCode, String password, String timestamp, String transactionType,
                   String amount, String partyA, String partyB, String phoneNumber, String callBackURL,
                   String accountReference, String transactionDesc) {
        this.businessShortCode = businessShortCode;
        this.password = password;
        this.timestamp = timestamp;
        this.transactionType = transactionType;
        this.amount = amount;
        this.partyA = partyA;
        this.partyB = partyB;
        this.phoneNumber = phoneNumber;
        this.callBackURL = callBackURL;
        this.accountReference = accountReference;
        this.transactionDesc = transactionDesc;
    }
}