package com.example.kyshi.finding_lost_kids_application;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kyshi.finding_lost_kid_application.R;

import uk.co.senab.photoview.PhotoViewAttacher;


public class Finding_Kid_Location_Activity extends AppCompatActivity {
    private Context mContext = this;
    private Intent intenttolostchild = null;
    ImageView map;
    PhotoViewAttacher attacher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.finding_kid_location);
        map = (ImageView)findViewById(R.id.imageview);
        Bitmap image1,image2;
        Paint paint = new Paint();
        image1 = BitmapFactory.decodeResource(getResources(), R.drawable.map);
        Bitmap tempBitmap = Bitmap.createBitmap(image1.getWidth(), image1.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(tempBitmap);
        //paint.setColor(Color.WHITE);
        //canvas.drawPaint(paint);
        canvas.drawBitmap(image1,0,0,null);

        map.setImageBitmap(tempBitmap);
        //map.setVisibility(View.INVISIBLE);
        //loc =(ImageView)findViewById(R.id.location);
        attacher = new PhotoViewAttacher(map);

        attacher.setOnViewTapListener(viewTapListener);


    }

    PhotoViewAttacher.OnViewTapListener viewTapListener = new PhotoViewAttacher.OnViewTapListener() {
        @Override
        public void onViewTap(View view, float x, float y) {
            //map.setVisibility(View.VISIBLE);
            Bitmap image1, image2, resize;
            image1 = BitmapFactory.decodeResource(getResources(), R.drawable.map);
            image2 = BitmapFactory.decodeResource(getResources(), R.drawable.location);
            Bitmap tempBitmap = Bitmap.createBitmap(map.getWidth(), map.getWidth(), Bitmap.Config.RGB_565);
            resize = Bitmap.createScaledBitmap(image1,map.getWidth(),map.getHeight(),true);
            Canvas canvas = new Canvas(tempBitmap);
            canvas.drawBitmap(resize,0,0,null);
            canvas.drawBitmap(image2,x,y,null);

            map.setImageDrawable(new BitmapDrawable(getResources(),tempBitmap));
            Toast.makeText(getApplicationContext(), "image_width: " + image1.getWidth() +  "image_height: " + image1.getHeight(), Toast.LENGTH_SHORT).show();


        }

    };





    }

