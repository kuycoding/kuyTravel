package com.kuycoding.kuytravel.handler;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.kuycoding.kuytravel.R;

import java.util.ArrayList;

public class PermissionHandler {
    private static PermissionHandler permissionHandler;
    private static final int REQUEST_CODE = 1;
    private Activity activity;
    private static final String PERMISSION_ACCESS_FINE_LOCTAION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String PERMISSION_ACCESS_COARSE_LOCTAION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private ArrayList<String> requiredPermissions;
    private ArrayList<String> ungrantedPermissions = new ArrayList<>();

    public PermissionHandler(Activity activity) {
        this.activity = activity;
    }

    public static synchronized PermissionHandler getInstance(Activity activity) {
        if (permissionHandler == null) {
            permissionHandler = new PermissionHandler(activity);
        }
        return permissionHandler;
    }

    private void initPermissions() {
        requiredPermissions = new ArrayList<>();
        requiredPermissions.add(PERMISSION_ACCESS_FINE_LOCTAION);
        requiredPermissions.add(PERMISSION_ACCESS_COARSE_LOCTAION);
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void deniedPermission(){
        ungrantedPermissions = getUnGratedPermissionList();
        if(canShowPermissionDialog()){
            showMessageOKCancel(activity.getResources().getString(R.string.permission_message),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            askPermissions();
                        }
                    });
            return;
        }
        askPermissions();
    }

    public void deniedPermission(final String permission){
        ungrantedPermissions = getUnGratedPermissionList();
        if(canShowPermissionDialog(permission)){
            showMessageOKCancel(activity.getResources().getString(R.string.permission_message),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            askPermissions();
                        }
                    });
            return;
        }
        askPermission(permission);
    }

    private void askPermissions() {
        if(ungrantedPermissions.size()>0) {
            ActivityCompat.requestPermissions(activity, ungrantedPermissions.toArray(new String[0]), REQUEST_CODE);
        }
    }
    private void askPermission(String permission) {
        ActivityCompat.requestPermissions(activity, new String[] {permission}, REQUEST_CODE);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton("Ok", okListener)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(activity, R.string.permission_message, Toast.LENGTH_SHORT).show();
                        activity.finish();
                    }
                })
                .create()
                .show();
    }

    private boolean canShowPermissionDialog() {
        boolean shouldshowRationale = false;
        for (String permission : ungrantedPermissions){
            boolean shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
            if(shouldShow){
                shouldshowRationale =true;
            }
        }
        return shouldshowRationale;
    }

    private boolean canShowPermissionDialog(String permission) {
        boolean shouldShowRationale = false;
        boolean shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
        if(shouldShow) {
            shouldShowRationale = true;
        }
        return shouldShowRationale;
    }
    private ArrayList<String> getUnGratedPermissionList() {
        ArrayList<String> permissionList = new ArrayList<>();
        for(String permission : requiredPermissions){
            int result = ActivityCompat.checkSelfPermission(activity, permission);
            if(result != PackageManager.PERMISSION_GRANTED){
                permissionList.add(permission);
            }
        }
        return permissionList;
    }

    public boolean isAllPermissionAvailable() {
        boolean isAllPermissionAvailable = true;
        initPermissions();
        for(String permission : requiredPermissions){
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED){
                isAllPermissionAvailable = false;
                break;
            }
        }
        return isAllPermissionAvailable;
    }
}
