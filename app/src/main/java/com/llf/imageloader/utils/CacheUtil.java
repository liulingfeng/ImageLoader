package com.llf.imageloader.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.TextUtils;
import android.widget.ImageView;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by llf on 2017/9/20.
 * 缓存工具类
 */

public class CacheUtil {
    private static CacheUtil instance;

    private Context context;
    private ImageCache imageCache;

    private CacheUtil(Context context) {
        this.context = context.getApplicationContext();
        Map<String, SoftReference<Bitmap>> cacheMap = new HashMap<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) { // SDK版本判断
            this.imageCache = new ImageCache(cacheMap);
        }
    }

    public static CacheUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (CacheUtil.class) {
                if (instance == null) {
                    instance = new CacheUtil(context);
                }
            }
        }
        return instance;
    }

    /**
     * 将图片添加到缓存中
     */
    private void putBitmapIntoCache(String fileName, byte[] data) {
        // 将图片的字节数组写入到内存中
        FileUtil.getInstance(context).writeFileToStorage(fileName, data);
        // 将图片存入强引用（LruCache）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            imageCache.put(fileName, BitmapFactory.decodeByteArray(data, 0, data.length));
        }
    }

    /**
     * 从缓存中取出图片
     */
    private Bitmap getBitmapFromCache(String fileName) {
        // 从强引用（LruCache）中取出图片
        Bitmap bm = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR1) { // SDK版本判断
            bm = imageCache.get(fileName);
            if (bm == null) {
                // 如果图片不存在强引用中，则去软引用（SoftReference）中查找
                Map<String, SoftReference<Bitmap>> cacheMap = imageCache.getCacheMap();
                SoftReference<Bitmap> softReference = cacheMap.get(fileName);
                if (softReference != null) {
                    bm = softReference.get();
                    imageCache.put(fileName, bm);
                } else {
                    // 如果图片不存在软引用中，则去内存中找
                    byte[] data = FileUtil.getInstance(context).readBytesFromStorage(fileName);
                    if (data != null && data.length > 0) {
                        bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                        imageCache.put(fileName, bm);
                    }
                }
            }
        }
        return bm;
    }

    /**
     * 使用三级缓存为ImageView设置图片
     */
    public void setImageToView(final String path, final ImageView view) {
        if(TextUtils.isEmpty(path)){
            new IllegalThreadStateException("图片地址不能为空");
            return;
        }
        final String fileName = path.substring(path.lastIndexOf(File.separator) + 1);
        Bitmap bm = getBitmapFromCache(fileName);
        if (bm != null) {
            view.setImageBitmap(bm);
        } else {
            // 从网络获取图片
            new Thread(new Runnable() {
                @Override
                public void run() {
                    byte[] b = HttpUtil.getInstance().getByteArrayFromWeb(path);
                    if (b != null && b.length > 0) {
                        // 将图片字节数组写入到缓存中
                        putBitmapIntoCache(fileName, b);
                        final Bitmap bm = BitmapFactory.decodeByteArray(b, 0, b.length);
                        // 将从网络获取到的图片设置给ImageView
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                view.setImageBitmap(bm);
                            }
                        });
                    }
                }
            }).start();
        }
    }
}
