package com.example.drushti.utils;

/**
 * Created by Nitin.Kumbhar on 10/13/2017.
 */


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Created by vishal.patil on 10/13/2017.
 */

public class PermissionUtils {

    public static final int GRANTED = 0;
    public static final int DENIED = 1;

    public static final int PERMISSION_VIBRATE = 1;
    public static final int PERMISSION_CALL_PHONE = 2;
    public static final int PERMISSION_SEND_SMS = 3;
    public static final int PERMISSION_WRITE_CONTACT = 4;
    public static final int PERMISSION_READ_CONTACT = 5;


    private static PermissionUtils INSTANCE = null;

    public static PermissionUtils getInstance() {
        if (INSTANCE == null)
            INSTANCE = new PermissionUtils();
        return INSTANCE;

    }

    public void requestPermission(Context context, Object object, String permission, int requestCode) {
        int status = getPermissionStatus(context, permission);
        Log.d("MainActivity", "status = " + status);
        switch (status) {
            case DENIED:
                if (object instanceof Activity) {
                    Activity activity = (Activity) object;
                    ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
                } else {
                    Fragment activity = (Fragment) object;
                    activity.requestPermissions(new String[]{permission}, requestCode);
                }

                break;
        }
    }

    public int getPermissionStatus(Context context, String androidPermissionName) {
        if (ContextCompat.checkSelfPermission(context, androidPermissionName) != PackageManager.PERMISSION_GRANTED) {
            return DENIED;
        }
        return GRANTED;
    }

    public boolean checkPermission(Context context, String androidPermissionName) {
        if (Build.VERSION.SDK_INT >= 23) {
            int result = ContextCompat.checkSelfPermission(context, androidPermissionName);
            if (result == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public static void openAppInfoSetting(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", context.getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}


