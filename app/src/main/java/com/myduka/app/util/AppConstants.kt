package com.myduka.app.util

/**
 * Object to hold constants needed in the app
 *
 * @author Eton Otieno
 * Converted to Kotlin by Eton Otieno
 */

object AppConstants {

    /**
     * Connection timeout duration
     */
    const val CONNECT_TIMEOUT = 60 * 1000
    /**
     * Connection Read timeout duration
     */
    const val READ_TIMEOUT = 60 * 1000
    /**
     * Connection write timeout duration
     */
    const val WRITE_TIMEOUT = 60 * 1000
    /**
     * Base URL
     */
    const val BASE_URL = "https://sandbox.safaricom.co.ke/"
    /**
     * global topic to receive app wide push notifications
     */
    const val TOPIC_GLOBAL = "global"

    // broadcast receiver intent filters
    const val REGISTRATION_COMPLETE = "registrationComplete"
    const val PUSH_NOTIFICATION = "pushNotification"

    // id to handle the notification in the notification tray
    const val NOTIFICATION_ID = 100
    const val NOTIFICATION_ID_BIG_IMAGE = 101
    const val SHARED_PREF = "ah_firebase"

    //STKPush Properties
    const val BUSINESS_SHORT_CODE = "174379"
    const val PASSKEY = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919"
    const val TRANSACTION_TYPE = "CustomerPayBillOnline"
    const val PARTYB = "174379"
    const val CALLBACKURL = "https://spurquoteapp.ga/pusher/pusher.php?title=stk_push&message=result&push_type=individual&regId="

}
