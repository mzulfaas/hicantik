package hi.beauty.haicantik;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static void requestAllPermission(Activity activity, int reqCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissionsNeeded = new ArrayList<String>();

            final List<String> permissionsList = new ArrayList<String>();
//            if (!addPermission(activity,permissionsList, Manifest.permission.READ_CONTACTS))
//                permissionsNeeded.add("Baca Kontak");
//            if (!addPermission(activity,permissionsList, Manifest.permission.CALL_PHONE))
//                permissionsNeeded.add("Baca Kontak");
            if (!addPermission(activity,permissionsList, Manifest.permission.CAMERA))
                permissionsNeeded.add("Camera");
//            if (!addPermission(activity,permissionsList, Manifest.permission.INTERNET))
//                permissionsNeeded.add("Internet");
//            if (!addPermission(activity,permissionsList, Manifest.permission.ACCESS_NETWORK_STATE))
//                permissionsNeeded.add("ANS");
//            if (!addPermission(activity,permissionsList, Manifest.permission.RECORD_AUDIO))
//                permissionsNeeded.add("RECORD_AUDIO");
//            if (!addPermission(activity,permissionsList, Manifest.permission.MODIFY_AUDIO_SETTINGS))
//                permissionsNeeded.add("MODIFY_AUDIO_SETTINGS");
//            if (!addPermission(activity,permissionsList, Manifest.permission.READ_PHONE_STATE))
//                permissionsNeeded.add("READ_PHONE_STATE");
            if (!addPermission(activity,permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                permissionsNeeded.add("READ_PHONE_STATE");

//            if (!addPermission(activity,permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
//                permissionsNeeded.add("Fine LocationInfo");
//            if (!addPermission(activity,permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION))
//                permissionsNeeded.add("Coarse LocationInfo");
//            if (!addPermission(activity,permissionsList, Manifest.permission.READ_PHONE_STATE))
//                permissionsNeeded.add("Read Phone State");
            if (!addPermission(activity,permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
                permissionsNeeded.add("Read External Storage");
//            if (!addPermission(activity,permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
//                permissionsNeeded.add("Write External Storage");
//            if (!addPermission(activity,permissionsList, Manifest.permission.CAMERA))
//                permissionsNeeded.add("Camera");

            if (permissionsList.size() > 0) {
                activity.requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), reqCode);
            }
        }

    }

    private static boolean addPermission(Activity activity, List<String> permissionsList, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
                // Check for Rationale Option
                if (!activity.shouldShowRequestPermissionRationale(permission))
                    return false;
            }
        }
        return true;
    }
}