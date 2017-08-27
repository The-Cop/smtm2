package ru.thecop.smtm2.sms;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

public class SmsReceivePermissionRequester {
    private static final int PERMISSIONS_REQUEST_RECEIVE_SMS_CODE = 1;

    public static void requestPermission(AppCompatActivity activity) {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            //todo show sms receive permission explanation?
            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
//                    Manifest.permission.RECEIVE_SMS)) {

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

//            } else {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.RECEIVE_SMS},
                    PERMISSIONS_REQUEST_RECEIVE_SMS_CODE);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }
}

