package com.akkipedia.skeleton.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Akash on 29/03/17.
 */

public class PermissionManager {
    private Set<String> permissions;
    private Activity activity;
    private static final int PERMISSION_MANAGER_CODE = 44;
    private PermissionListener permissionListener;

    private final String[] storagePermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private final String[] locationPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private final String cameraPermission = Manifest.permission.CAMERA;

    private PermissionManager(){
        permissions = new HashSet<>();
    }

    public boolean hasPermissions(String... permissions){
        if(permissions.length==0)
            permissions = this.permissions.toArray(new String[]{});
        boolean hasPermissions = true;
        for(String permission: permissions) {
            hasPermissions &= ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
        }
        return hasPermissions;
    }

    public boolean hasPermissions(List<String> permissions){
        boolean hasPermissions = true;
        for(String permission: permissions) {
            hasPermissions &= ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
        }
        return hasPermissions;
    }

    public boolean hasPermission(String permission){
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean hasDefaultPermissions(){
        return hasPermissions(storagePermissions[0], storagePermissions[1], cameraPermission);
    }

    public boolean hasStoragePermissions(){
        return hasPermissions(storagePermissions);
    }

    public boolean hasLocationPermissions(){
        return hasPermissions(locationPermissions);
    }

    public boolean hasCameraPermission(){
        return hasPermission(cameraPermission);
    }

    public void askPermissions(String... permissions){
        if(permissions.length==0)
            ActivityCompat.requestPermissions(activity, this.permissions.toArray(new String[]{}), PERMISSION_MANAGER_CODE);
        else
            ActivityCompat.requestPermissions(activity, permissions, PERMISSION_MANAGER_CODE);
    }

    public void checkAndAskPermissions(){
        if(!hasPermissions())
            askPermissions();
        else
            permissionListener.onPermissionsResult(true, permissions.toArray(new String[]{}), new String[]{});
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(requestCode != PERMISSION_MANAGER_CODE)
            return;
        List<String> grantedPermissions = new ArrayList<>();
        List<String> rejectedPermissions = new ArrayList<>();

        for(int i = 0; i<grantResults.length; i++){
            if(grantResults[i] == PackageManager.PERMISSION_GRANTED)
                grantedPermissions.add(permissions[i]);
            else
                rejectedPermissions.add(permissions[i]);
        }

        permissionListener.onPermissionsResult(
                rejectedPermissions.size()==0,
                grantedPermissions.toArray(new String[]{}),
                rejectedPermissions.toArray(new String[]{})
        );
    }

    public static class Builder{
        private PermissionManager permissionManager;

        public Builder(PermissionListener permissionsListener){
            permissionManager = new PermissionManager();
            permissionManager.permissionListener = permissionsListener;
        }

        public Builder with(Activity activity){
            permissionManager.activity = activity;
            return this;
        }

        public Builder addPermission(String permission){
            permissionManager.permissions.add(permission);
            return this;
        }

        public Builder addPermissions(String[] permissions){
            permissionManager.permissions.addAll(Arrays.asList(permissions));
            return this;
        }

        public Builder addPermissions(List<String> permissions){
            permissionManager.permissions.addAll(permissions);
            return this;
        }

        public Builder addStoragePermissions(){
            addPermissions(permissionManager.storagePermissions);
            return this;
        }

        public Builder addLocationPermissions(){
            addPermissions(permissionManager.locationPermissions);
            return this;
        }

        public Builder addCameraPermissions(){
            addPermission(permissionManager.cameraPermission);
            return this;
        }

        public Builder addDefaultPermissions(){
            addStoragePermissions();
            addLocationPermissions();
            addCameraPermissions();
            return this;
        }

        public PermissionManager build(){
            return permissionManager;
        }

    }

    public interface PermissionListener{
        void onPermissionsResult(boolean allGranted, String[] grantedPermissions, String[] rejectedPermissions);
    }
}
