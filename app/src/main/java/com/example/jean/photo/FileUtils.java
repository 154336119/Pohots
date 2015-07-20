package com.example.jean.photo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by jean on 2015/7/20.
 */
public class FileUtils {
    /**
     *
     */
    private static String mSDRootPath = Environment.getExternalStorageDirectory().getPath();

    private static String mDataRootPath = null;

    private static String mImageDataPath = "AndroidImage";

    public FileUtils(Context context){
        mDataRootPath = context.getCacheDir().getPath();
    }

    private String getStorageDirectory(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?
                mSDRootPath + mImageDataPath : mDataRootPath + mImageDataPath;
    }

    /**
     * 保存图片到硬盘
     * @param fileName
     * @param bitmap
     * @throws IOException
     */
    public void saveBitmap(String fileName,Bitmap bitmap) throws IOException {
        if(bitmap == null){
            return;
        }
        String path = getStorageDirectory();
        File folderFile = new File(path);
        if(!folderFile.exists()){
            folderFile.mkdir();
        }
        File file = new File(path + File.separator + fileName);
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
        fos.flush();
        fos.close();

    }

    /**
     * 读取图片
     * @param fileName
     * @return
     */
    public Bitmap getBitmap(String fileName){
        return BitmapFactory.decodeFile(getStorageDirectory() + File.separator + fileName);
    }

    /**
     * 判断文件是否存在
     * @param fileName
     * @return
     */
    public boolean isFileExist(String fileName){
        return new File(getStorageDirectory() + File.separator + fileName).exists();
    }

    /**
     * 获取文件大小
     */
    public long geiFileSize(String fileName){
        return new File(getStorageDirectory() + File.separator + fileName).length();
    }

    /**
     * 删除硬盘缓存的图片
     */
    public void deleteFile(){
        File dirFile = new File(getStorageDirectory());
        if(!dirFile.exists()){
            return;
        }
        if(dirFile.isDirectory()){
            String[] childern = dirFile.list();
            for(int i=0;i<childern.length;i++){
                new File(dirFile,childern[i]).delete();
            }
        }
    }
}
