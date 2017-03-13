package com.sourcey.materiallogindemo.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.squareup.picasso.Transformation;

public class CircleTransform implements Transformation {
    @Override
    public Bitmap transform(Bitmap source) {

        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squeredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if(squeredBitmap != source){
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader bitmapShader = new BitmapShader(squeredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(bitmapShader);
        paint.setAntiAlias(true);

        float radio = size / 2f;
        canvas.drawCircle(radio, radio, radio, paint);
        squeredBitmap.recycle();
        //return ImageUtils.getCircularBitmapImage(source);

        return bitmap;
    }

    @Override
    public String key() {
        return "circle-img";
    }

}
