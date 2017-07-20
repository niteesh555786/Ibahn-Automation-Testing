package com.akkipedia.skeleton.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Akash on 28/11/16.
 */

public class GeneralUtils {

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm.getActiveNetworkInfo() == null)
            Toast.makeText(context,"No Internet Available",Toast.LENGTH_SHORT).show();
        return cm.getActiveNetworkInfo() != null;
    }


    public static String getUniqueIdentifier(Context context){
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static int getIntegerValue(String integerString){
        if(integerString.length()==0)
            return 0;
        try{
            return Integer.parseInt(integerString);
        } catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public static String SHA1(String text){
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
            md.update(text.getBytes("iso-8859-1"), 0, text.length());
            byte[] sha1hash = md.digest();
            return convertToHex(sha1hash);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String dateToJSONString(Date date){
        long l = date.getTime();
        String dateString = "/Date(" + l +"+0000)/";
        return dateString;
    }

    public static Date jsonStringToDate(String jsonDate) {
        //  "/Date(1321867151710+0100)/"
        int idx1 = jsonDate.indexOf("(");
        int idx2 = jsonDate.indexOf(")") - 5;
        String s = jsonDate.substring(idx1+1, idx2);
        long l = Long.valueOf(s);
        return new Date(l);
    }

    private static String mCurrentPhotoPath="";
    public static void takeImage(final Activity context, final int PICK_IMAGE_REQUEST_CODE,final int CAPTURE_IMAGE_REQUEST_CODE){
        final String[] items = new String[]{"Camera", "Gallery"};
        new AlertDialog.Builder(context)
                .setTitle("Select Picture")
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals("Camera")) {
                            dispatchTakePictureIntent(context,CAPTURE_IMAGE_REQUEST_CODE);
                        } else if (items[item].equals("Gallery")) {
                            if (Build.VERSION.SDK_INT <= 19) {
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                intent.addCategory(Intent.CATEGORY_OPENABLE);
                                context.startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST_CODE);
                            } else if (Build.VERSION.SDK_INT > 19) {
                                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                context.startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST_CODE);
                            }
                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                }).show();
    }

    private static void dispatchTakePictureIntent(Activity context,int CAPTURE_IMAGE_REQUEST_CODE) {
        String fileProviderUrl = context.getPackageName()+".fileprovider";
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile(context);
                mCurrentPhotoPath = photoFile.getAbsolutePath();
            } catch (IOException ex) {
                Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(context,
                        fileProviderUrl,
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST_CODE);
            }
        }
    }

    private static File createImageFile(Activity context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    public static String onImagePickerResult(int requestCode, int resultCode, Intent data,int PICK_IMAGE_REQUEST_CODE,int CAPTURE_IMAGE_REQUEST_CODE, ImageView imageView,Activity context){
        String imageBase64 = "";
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && null != data) {
            Uri selectedImageUri = data.getData();
            String selectedImagePath = getRealPathFromURIForGallery(selectedImageUri,context);
            imageBase64 = setPic(imageView,selectedImagePath);
        }
        if(requestCode == CAPTURE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            imageBase64 = setPic(imageView, mCurrentPhotoPath);
        }
        return imageBase64;
    }
    private static String getRealPathFromURIForGallery(Uri uri, Activity context) {
        if (uri == null) {
            return null;
        }
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }

    private static String setPic(ImageView mImageView, String imageUri) {
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        Bitmap bitmap = BitmapUtils.decodeScaledBitmap(imageUri, targetW, targetH);
        String base64 = BitmapUtils.getBase64(bitmap);
        mImageView.setImageBitmap(bitmap);
        return base64;
    }


    public static void takeImage(final Activity context, final Fragment fragment, final int PICK_IMAGE_REQUEST_CODE, final int CAPTURE_IMAGE_REQUEST_CODE){
        final String[] items = new String[]{"Camera", "Gallery"};
        new AlertDialog.Builder(context)
                .setTitle("Select Picture")
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals("Camera")) {
                            dispatchTakePictureIntent(context,fragment,CAPTURE_IMAGE_REQUEST_CODE);
                        } else if (items[item].equals("Gallery")) {
                            if (Build.VERSION.SDK_INT <= 19) {
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                intent.addCategory(Intent.CATEGORY_OPENABLE);
                                fragment.startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST_CODE);
                            } else if (Build.VERSION.SDK_INT > 19) {
                                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                fragment.startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST_CODE);
                            }
                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                }).show();
    }

    public static void restartApplication(Activity context){
        Intent i = context.getPackageManager()
                .getLaunchIntentForPackage( context.getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);
        context.finishAffinity();
    }
    private static void dispatchTakePictureIntent(Activity context,Fragment fragment,int CAPTURE_IMAGE_REQUEST_CODE) {
        String fileProviderUrl = context.getPackageName()+".fileprovider";
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile(context);
                mCurrentPhotoPath = photoFile.getAbsolutePath();
            } catch (IOException ex) {
                Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(context,
                        fileProviderUrl,
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                fragment.startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST_CODE);
            }
        }
    }


    public static boolean isBleSupported(Context context){
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    public static byte [] floatToByteArray (float value)
    {
        return ByteBuffer.allocate(4).putFloat(value).array();
    }

    public static float parseFloat (String floatString){
        if(floatString == null || floatString.length() == 0)
            return 0;
        try {
            return Float.valueOf(floatString.replace(",", ""));
        } catch (Exception e){
            return 0;
        }
    }

    public static int bcdToDecimal(byte bcd){
        return (bcd & 0xF) + (((int)bcd & 0xF0) >> 4)*10;
    }

    public static byte hexStringToByte(String hexString){
        if (hexString.length() != 2)
            return 0;
        return (byte) ((Character.digit(hexString.charAt(0), 16) << 4)
                + Character.digit(hexString.charAt(1), 16));
    }

}
