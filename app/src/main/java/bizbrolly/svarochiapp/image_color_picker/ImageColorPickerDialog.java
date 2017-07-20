package bizbrolly.svarochiapp.image_color_picker;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import bizbrolly.svarochiapp.R;
import bizbrolly.svarochiapp.ibahn_logic.Data;
import bizbrolly.svarochiapp.ibahn_logic.DataSender;

/**
 * Created by bizbrolly on 9/19/16.
 */
public class ImageColorPickerDialog extends AlertDialog {
    ImageView colorImage;
    ImageView colorPointer;
    RelativeLayout layout, backgroundLayout;
    float centerX;
    ImageButton closeButton;
    float centerY;
    float radius;
    long previousTimeStamp = -1;
    Bitmap colorPickerBitmap;
    int deviceId;
    String imageUri;

    public ImageColorPickerDialog(Context context, int deviceId) {
        super(context);
        this.deviceId = deviceId;
        this.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_color_picker);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        colorImage = (ImageView) findViewById(R.id.color_picker_image);
        layout = (RelativeLayout) findViewById(R.id.layout);
        backgroundLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        closeButton = (ImageButton) findViewById(R.id.close);
        colorPointer = (ImageView) findViewById(R.id.pointer);
        ImageLoader.getInstance().displayImage(imageUri, colorImage, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                colorPickerBitmap = loadedImage;
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
//        colorPickerBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.circle_color);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                if (centerX == 0)
//                    centerX = colorPointer.getX();
//                if (centerY == 0)
//                    centerY = colorPointer.getY();
//                if (radius == 0)
//                    radius = colorImage.getWidth() / 2f;
                float x = event.getX();
                float y = event.getY();
                Log.e("Distance", "x : " + x + " y : " + y);
//                float distance = getDistance(x, y);
//                if (getDistance(x, y) > radius) {
//                    x = ((centerX - x) / distance);
//                    y = ((centerY - y) / distance);
//                    x = ((x + 1) / 2);
//                    y = ((y + 1) / 2);
//                    x = (centerX + radius) - (2 * x * radius);
//                    y = (centerY + radius) - (2 * y * radius);
//                }
                colorPointer.setX(x);
                colorPointer.setY(y);
//                float normX = (x - (centerX - radius)) / (2 * radius);
//                float normY = (y - (centerY - radius)) / (2 * radius);
//                normX = (normX * 0.98f) + 0.01f;
//                normY = (normY * 0.98f) + 0.01f;
//                normX = (normX * (colorPickerBitmap.getWidth()));
//                normY = (normY * (colorPickerBitmap.getWidth()));
//                if (normX < colorPickerBitmap.getWidth() && normY < colorPickerBitmap.getWidth()) {
                int color = colorPickerBitmap.getPixel((int) x, (int) y);
//                Color.alpha()
                int r = Color.red(color);
                int g = Color.green(color);
                int b = Color.blue(color);
                Log.e("Color", "r : " + r + " g : " + g + " b : " + b);
                int avg = (r + g + b) / 3;
                if (previousTimeStamp == -1 || (System.currentTimeMillis() - previousTimeStamp) > 100) {
                    previousTimeStamp = System.currentTimeMillis();
                    DataSender.sendData(deviceId, Data.COLOR.getDataValue(avg, r, g, b));
                }
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    ColorStateList colorStateList = new ColorStateList(new int[][]{new int[] {android.R.attr.state_enabled}}, new int[Color.argb(1,r,g,b)]);
//                    backgroundLayout.setBackgroundTintList(colorStateList);
//                }
//
//                }
                return true;
            }
        });
//        new EyeDropper(colorImage, new EyeDropper.ColorSelectionListener() {
//            @Override
//            public void onColorSelected(@ColorInt int color) {
//                // color is the color selected when you touch the targetView
////                (findViewById(R.id.colorSlot)).setBackgroundColor(color);
//            }
//        });
    }

    public void setColorPickerImage(String uri) {
        imageUri = uri;
    }

    private float getDistance(float x, float y) {
        Log.e("Distance", "total : " + (float) Math.pow(((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY)), 0.5f));
        return (float) Math.pow(((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY)), 0.5f);
    }

}
