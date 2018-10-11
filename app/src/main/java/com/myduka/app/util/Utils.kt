package com.myduka.app.util

import android.util.Base64

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Created by miles on 23/11/2017.
 * Taken from https://github.com/bdhobare/mpesa-android-sdk
 *
 * Converted to Kotlin by Eton Otieno
 */

object Utils {

    @JvmStatic
    val timestamp: String
        get() = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())

    @JvmStatic
    fun sanitizePhoneNumber(phone: String): String {
        if (phone == "") {
            return ""
        }
        if ((phone.length < 11) and phone.startsWith("0")) {
            return phone.replaceFirst("^0".toRegex(), "254")
        }
        return if (phone.length == 13 && phone.startsWith("+")) {
            phone.replaceFirst("^+".toRegex(), "")
        } else phone
    }

    @JvmStatic
    fun getPassword(businessShortCode: String, passkey: String, timestamp: String): String {
        val str = businessShortCode + passkey + timestamp
        //encode the password to Base64
        return Base64.encodeToString(str.toByteArray(), Base64.NO_WRAP)
    }
}
