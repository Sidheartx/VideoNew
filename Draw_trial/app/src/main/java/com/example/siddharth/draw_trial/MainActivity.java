package com.example.siddharth.draw_trial;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;








public class MainActivity extends Activity {
    Bitmap bitmap;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new MyView(this));

    } Context mContext;

    public class MyView extends View {
        public MyView(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }

        @Override
        protected void onDraw(Canvas canvas) {
            // TODO Auto-generated method stub
            super.onDraw(canvas);
            int x = getWidth();
            int y = getHeight();
            int radius;
            radius = 100;
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            bitmap.compress(Bitmap.CompressFormat.PNG,50,stream);

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),bitmap);
            canvas.drawBitmap(bitmap,0,0,null);
            // Use Color.parseColor to define HTML colors
            paint.setColor(Color.parseColor("#CD5C5C"));
            Bitmap waterMark = BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher);
            canvas.drawCircle(x / 2, y / 2, radius, paint);
            canvas.drawBitmap(waterMark,300,500,null);

        }
    }
}
