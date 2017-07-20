package com.akkipedia.skeleton.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by Akash on 18/10/16.
 */

public class BitmapUtils {


    public static Bitmap decodeScaledBitmap(Resources resources, int resId, int targetW, int targetH){
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;


        BitmapFactory.decodeResource(resources, resId, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeResource(resources, resId, bmOptions);
        return bitmap;
    }


    public static Bitmap decodeScaledBitmap(Resources resources, int resId, int targetW){
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;


        BitmapFactory.decodeResource(resources, resId, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = photoW/targetW;

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeResource(resources, resId, bmOptions);
        return bitmap;
    }




    public static String getBase64(Bitmap image){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //compress the image to jpg format
        int a = image.getByteCount();
        if(image.getByteCount()<10240000){
            image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        } else {
            float scaleFactor = (10240000.00f / (float) a) * 100;
            image.compress(Bitmap.CompressFormat.JPEG, (int) scaleFactor, byteArrayOutputStream);
        }
        String encodeImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        return encodeImage;
    }

    public static Bitmap decodeScaledBitmap(String imageUri, int targetW, int targetH){
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(imageUri, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imageUri, bmOptions);
        return bitmap;
    }

    public static Bitmap decodeScaledBitmapTop(Resources resources, int resId, int targetW, int targetH){
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;


        BitmapFactory.decodeResource(resources, resId, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        float scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = (int) scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeResource(resources, resId, bmOptions);
//        scaleFactor = Math.min(
//                bitmap.getWidth()/((float) targetW),
//                bitmap.getHeight()/((float) targetH)
//        );
//        if(scaleFactor<=1){
            //Image was smaller than target. Scale it up
            float scaleUpFactor = Math.max(
                    bmOptions.outWidth/((float) targetW),
                    bmOptions.outHeight/((float) targetH)
            );

            bitmap = Bitmap.createScaledBitmap(
                    bitmap,
                    (int) scaleUpFactor * bmOptions.outWidth,
                    (int) scaleUpFactor * bmOptions.outHeight,
                    true
            );
//        }

        bitmap = Bitmap.createBitmap(
                bitmap,
                0,
                0,
                targetW,
                targetH
        );

        return bitmap;
    }
}
