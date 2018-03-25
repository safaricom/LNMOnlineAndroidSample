Lipa Na Mpesa Android Sample
------------------------------
This app demonstrates how to impeliment Lipa Na MPESA Online.
Documentation on the API can be found on [Safaricom Developer Portal] (https://developer.safaricom.co.ke/docs)

### Requirements

* JDK Version 1.7 & above
* Android Studio

### Getting Safaricom Credentials
1. Create an account on the [Safaricom Developer Portal] (https://developer.safaricom.co.ke/)
2. Create a Lipa na MPESA Online App

### Project Setup
1. Rename  `sample.gradle.properties` file to `gradle.properties` then add you `Consumer key` and `Consumer secret`.
2. Copy `sample-google-services.json` inside `app` directory and rename it to `google-services.json`. This will ensure your project build without an error.

#### NB
`sample-google-services.json` is just a sample file to help you bypass build error due to a `google-services.json` missing. 

####  Firebase Setup
In order to send push notifications to the user, you will need to setup [FCM - Firebase Cloud Messaging Service] (https://firebase.google.com/docs/cloud-messaging/android/client). AndroidHive has an awesome [tutorial](https://www.androidhive.info/2012/10/android-push-notifications-using-google-cloud-messaging-gcm) on this. 

###  Screenshots

##### Add an item to the cart: 
![alt text](https://github.com/safaricom/LNMOnlineAndroidSample/blob/master/art/a.jpg "Screen A")

##### Add a customers phone number: 
![alt text](https://github.com/safaricom/LNMOnlineAndroidSample/blob/master/art/b.jpg "Screen B")

##### The STK push payment popup is sent to the customer phone: 
![alt text](https://github.com/safaricom/LNMOnlineAndroidSample/blob/master/art/c.jpg "Screen C")

##### MPESA confirmation message: 
![alt text](https://github.com/Jaymo/LNMOnlineAndroidSample/blob/master/art/d.jpg "Screen D")

##### Payment confirmation from the API callback: 
![alt text](https://github.com/safaricom/LNMOnlineAndroidSample/blob/master/art/e.jpg "Screen E")

### Libraries Used
1. [Sweet alerts] (https://github.com/pedant/sweet-alert-dialog)
2. [Butterknife] (https://github.com/JakeWharton/butterknife)
3. [Retrofit] (http://square.github.io/retrofit/)
4. [GSON] (https://github.com/google/gson)
5. [FireBase] (https://firebase.google.com/docs/android/setup)
6. [Okhttp] (http://square.github.io/okhttp/)
7. [okio] (https://github.com/square/okio)
8. [OkHttp Interceptors](https://github.com/square/okhttp/wiki/Interceptors)
9. [Timber] (https://github.com/JakeWharton/timber)
