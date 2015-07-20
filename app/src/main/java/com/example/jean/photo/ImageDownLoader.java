package com.example.jean.photo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jean on 2015/7/20.
 */
public class ImageDownLoader {

    private LruCache<String,Bitmap> mMemoryCache;

    private FileUtils fileUtils;

    private ExecutorService mImageThreadPool  = null;

    public ImageDownLoader(Context context){
        int maxMemory = (int)Runtime.getRuntime().maxMemory();
        int mCacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String,Bitmap>(mCacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
        fileUtils = new FileUtils(context);
    }
    /**
     * 获取线程池的方法，因为涉及到并发的问题，我们加上同步锁
     * @return
     */
    public ExecutorService getThreadPool(){
        if(mImageThreadPool == null){
            synchronized (ExecutorService.class){
                if(mImageThreadPool == null){
                    //为了下载图片更加的流畅，我们用了2个线程来下载图片
                    mImageThreadPool = Executors.newFixedThreadPool(2);
                }
            }
        }
        return mImageThreadPool;
    }

    /**
     * 添加Bitmap到内存
     * @param key
     * @param bitmap
     */
    public void addBitmapTpMemoryCache(String key,Bitmap bitmap){
        if(getBitmapFormMemCache(key) == null && bitmap != null){
            mMemoryCache.put(key,bitmap);
        }
    }

    /**
     * 从内存中获取一个Bitmap
     * @param key
     * @return
     */
    public Bitmap getBitmapFormMemCache(String key){
        return mMemoryCache.get(key);
    }

    /**
     * 先从内存中获取bitmap,没有则从 硬盘获取，都没有则下载
     * @return
     */
    public Bitmap dowmLoadImage(final String url, final onImageLoaderListener listener){
            final String subUrl = url.replaceAll("[^\\w]", "");
            Bitmap bitmap = showCacheBitmap(subUrl);
            if(bitmap !=null){
                return bitmap;
            }else{
                final Handler handler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        listener.onImageLoaderListener((Bitmap)msg.obj,url);
                    }
                };
                getThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap = getBitmapFormUrl(url);
                        Message msg = handler.obtainMessage();
                        msg.obj = bitmap;
                        handler.sendMessage(msg);//通知主线程
                        try {
                            fileUtils.saveBitmap(subUrl,bitmap);
                            addBitmapTpMemoryCache(subUrl,bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }

        return null;
    }

    /**
     * 从网络获取图片
     * @param url
     * @return
     */
    private Bitmap getBitmapFormUrl(String url){
        Bitmap bitmap = null;
        HttpURLConnection con = null;
        try {
            URL mImageUrl = new URL(url);
            con = (HttpURLConnection)mImageUrl.openConnection();
            con.setConnectTimeout(10 * 10000);
            con.setReadTimeout(10 * 1000);
            con.setDoInput(true);
            con.setDoOutput(true);
            bitmap = BitmapFactory.decodeStream(con.getInputStream());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(con!=null){
                con.disconnect();
            }
        }
        return null;

    }

    /**
     * 从缓存中获取bitmap
     * @param url
     * @return
     */
    public Bitmap showCacheBitmap(String url){
        if (getBitmapFormMemCache(url)!=null){
            return getBitmapFormMemCache(url);
        }else if (!fileUtils.isFileExist(url) && fileUtils.geiFileSize(url)!=0){
            Bitmap bitmap = fileUtils.getBitmap(url);
            addBitmapTpMemoryCache(url,bitmap);
            return bitmap;
        }
        return null;
    }
    public interface onImageLoaderListener{
        void onImageLoaderListener(Bitmap bitmap,String url);
    }

    public synchronized void cancelTask(){
        if (mImageThreadPool != null){
            mImageThreadPool.shutdown();
            mImageThreadPool = null;
        }
    }

}
