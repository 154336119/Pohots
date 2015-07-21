package com.example.jean.photo;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by jean on 2015/7/21.
 */
public class ImageAdapter extends BaseAdapter implements AbsListView.OnScrollListener {
    /**
     * 上下文引用
     */
    private Context mContext;

    /**
     * URL数组
     */
    private String[] mImageThrumbUrls;

    /**
     * GridView对象的引用
     */
    private GridView mGridView;

    /**
     * 图片下载器
     */
    private ImageDownLoader mImageDownLoader;

    /**
     * 若首次进去，先请求一次下载（用于解决进入程序不滚动屏幕，不会下载图片的问题）
     */
    private boolean isFirstEnter = true;

    /**
     * 屏幕中显示的第一个 item位置
     */
    private int mFirstVisibleItem;

    /**
     * 一屏中所有item的个数
     */
    private int mVisibleItemCount;

    public ImageAdapter(Context context, GridView gridView, String[] imageThrumbUrls){
        mContext = context;
        mGridView = gridView;
        mImageThrumbUrls = imageThrumbUrls;
        mImageDownLoader = new ImageDownLoader(context);
        mGridView.setOnScrollListener(this);
    }

    @Override
    public int getCount() {
        return mImageThrumbUrls.length;
    }

    @Override
    public Object getItem(int position) {
        return mImageThrumbUrls[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        final String imageUrl = mImageThrumbUrls[position];
        if(convertView == null){
            imageView = new ImageView(mContext);
        }else{
            imageView = (ImageView)convertView;
        }

        imageView.setLayoutParams(new GridView.LayoutParams(150, 150));
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setTag(imageUrl);

        Bitmap bitmap = mImageDownLoader.showCacheBitmap(imageUrl.replaceAll("[^\\w]", ""));
        if(bitmap != null){
            imageView.setImageBitmap(bitmap);
        }else{
            imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_launcher));
        }
        return imageView;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    private void showImage(int mFirstVisibleItem, int mVisibleItemCount){
        Bitmap bitmap = null;
        for(int i=mFirstVisibleItem;i<mFirstVisibleItem+mVisibleItemCount;i++){

        }
    }
}
