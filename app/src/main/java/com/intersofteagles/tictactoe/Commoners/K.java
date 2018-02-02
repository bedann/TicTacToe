package com.intersofteagles.tictactoe.Commoners;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import android.animation.Animator;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;


/**
 * Created by Monroe on 4/12/2017.
 */
public class K {

    public static final int OPTIONS = 0,OPTIONS_PHOTO = 1,ALBUM = 2,PLAIN_TEXT = 3,TEXT_PHOTO = 4,OPTIONS_VIDEO = 5,TEXT_VIDEO=6;

    public static boolean validated(View ...views){
        boolean ok = true;
        for (View v:views){
            if (v instanceof EditText){
                if(TextUtils.isEmpty(((EditText)v).getText().toString())){
                    ok = false;
                    ((EditText)v).setError("Required");
                }
            }
        }
        return ok;
    }



    public static String getDLink(String type,String id){
        try {
            id = id.replaceAll(" ","__");
        }catch (Exception e){}
        String link = "https://fr3qz.app.goo.gl/?link=https://www.mybeautypot.com/"+type+"/"+id+"&apn=intersofteagles.apps.com.mybeautypot&amv=4";
        return link;
    }




    public static Transition move(int c){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ChangeBounds move = new ChangeBounds();
            if(c == 0){
                move.setInterpolator(new AnticipateInterpolator());}
            else if(c == 2){
                move.setInterpolator(new AccelerateInterpolator());
            }else{
                move.setInterpolator(new DecelerateInterpolator());
            }
            move.setDuration(500);
            return move;
        }
        return  null;
    }

    public static Bitmap bitmapR(Resources res,int resId, int reqWidth){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; //checks the dimensions
        try {
            BitmapFactory.decodeResource(res, resId, options); //decodes without loading in memory
        }catch (Exception e){}
        options.inSampleSize = calculateSampleSize(options,reqWidth,reqWidth);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }



    public static Bitmap decodedBitmap(String filePath, int reqWidth, int reqHeight,int quality){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; //checks the dimensions
        try {
            BitmapFactory.decodeFile(filePath, options); //decodes without loading in memory
        }catch (Exception e){}
        options.inSampleSize = calculateSampleSize(options,reqWidth,reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap bitmap =  BitmapFactory.decodeFile(filePath, options);
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        if (bitmap != null){
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bytearrayoutputstream);
        }else {
            return null;
        }

        byte[] BYTE = bytearrayoutputstream.toByteArray();

        return BitmapFactory.decodeByteArray(BYTE, 0, BYTE.length);
    }

    public static Bitmap bitmapFromView(ImageView imageView){
        try{
            return ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        }catch (Exception e){
            return null;
        }
    }

    public static int calculateSampleSize(BitmapFactory.Options options, int rw, int rh){
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;
        if (width > rw || height > rh){
            int halfH = height/2;
            int halfW = width/2;
            while ((halfH/inSampleSize)>rh && (halfW/inSampleSize)>rw){
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static byte[] bytes(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream);
        return stream.toByteArray();
    }


    public static byte[] smallBitmap(byte[] bytes, int reqWidth, int reqHeight){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; //checks the dimensions
        BitmapFactory.decodeByteArray(bytes,0,bytes.length,options); //decodes without loading in memory
        options.inSampleSize = calculateSampleSize(options,reqWidth,reqHeight);
        options.inJustDecodeBounds = false;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options).compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    public static Bitmap squareBitmap(Bitmap bitmap){
        int dim = Math.max(bitmap.getWidth(),bitmap.getHeight());
        Bitmap dest = Bitmap.createBitmap(dim, dim, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(dest);
        canvas.drawColor(Color.BLACK);
        canvas.drawBitmap(bitmap, (dim - bitmap.getWidth()) / 2, (dim - bitmap.getHeight()) / 2, null);
        return dest;
    }

    public static Bitmap imageFromBytes(byte[] bytes,int rw,int rh){
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true; //checks the dimensions
            BitmapFactory.decodeByteArray(bytes,0,bytes.length,options); //decodes without loading in memory
            options.inSampleSize = calculateSampleSize(options, rw, rh);
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length,options);
        }catch (Exception e){}
        return bitmap;
    }

    public static String toTitleCase(String str) {

        if (str == null) {
            return null;
        }

        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }

        return builder.toString();
    }



    public static File getTempImage(String name){
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/KU app/"+name+".jpg";
        return new File(filePath);
    }

    public static void deleteTemp(){
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/KU app/temp.jpg";
        try {
            new File(filePath).delete();
        }catch (Exception e){}
    }


    public static File saveTemp(Context context,Bitmap bitmap,String name){
        if (bitmap == null){
            return null;
        }
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/KU app";
        File dir = new File(filePath);
        if (!dir.exists()){
            dir.mkdirs();
        }
        File file = new File(dir, name+".jpg");
        if (file.exists())file.delete();
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();
            Intent mediaScan = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(file.getAbsolutePath());
            Uri contentUri = Uri.fromFile(f);
            mediaScan.setData(contentUri);
            context.sendBroadcast(mediaScan);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static void scanNewItem(File file,Context context){
        Intent mediaScan = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(file.getAbsolutePath());
        Uri contentUri = Uri.fromFile(f);
        mediaScan.setData(contentUri);
        context.sendBroadcast(mediaScan);
    }

    public static void initImageLoader(Context context){
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(context).diskCacheSize((1024*1024)*100).build();
        ImageLoader.getInstance().init(configuration);
    }


    public static void cache(Bitmap bitmap,String name){
        ImageLoader loader = ImageLoader.getInstance();
        if (bitmap == null || name == null)return;
        try {
            loader.getDiskCache().remove(name);
        }catch (Exception e){}
        try {
            loader.getDiskCache().save(name, bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap get(String name){
        File f = ImageLoader.getInstance().getDiskCache().get(name);
        if (f != null && f.exists()){
            return decodedBitmap(f.getAbsolutePath(), 500, 500,85);
        }else {
            return null;
        }
    }


    public static int extractHour(String time){
        String hr = time.split(":")[0];
        return Integer.parseInt(hr);
    }


    public static int extractMinutes(String time){
        String min = time.split(":")[1].split(" ")[0];
        return Integer.parseInt(min);
    }

    public static String bytesToMB(long bytes){
        DecimalFormat df = new DecimalFormat("####0.0");
        return df.format((1.0 * bytes) / (1024 * 1024));
    }

    public static double bytesToDouble(long bytes){
        return ((1.0*bytes)/(1024*1024));
    }

    public static String fileSize(File f){
        long size = f.length();
        if (size<(1024*1024)){
            return (size/1024)+" kb";
        }
        return (size/(1024*1024))+" mb";
    }




}
