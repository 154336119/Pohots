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
     * ����������
     */
    private Context mContext;

    /**
     * URL����
     */
    private String[] mImageThrumbUrls;

    /**
     * GridView���������
     */
    private GridView mGridView;

    /**
     * ͼƬ������
     */
    private ImageDownLoader mImageDownLoader;

    /**
     * ���״ν�ȥ��������һ�����أ����ڽ��������򲻹�����Ļ����������ͼƬ�����⣩
     */
    private boolean isFirstEnter = true;

    /**
     * ��Ļ����ʾ�ĵ�һ�� itemλ��
     */
    private int mFirstVisibleItem;

    /**
     * һ��������item�ĸ���
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
        //����GridView��ֹʱ��ȥ����ͼƬ������ʱȡ�������������ص�����
        if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
            showImage(mFirstVisibleItem,mVisibleItemCount);
        }else{
            mImageDownLoader.cancelTask();
        }
    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
         mFirstVisibleItem = firstVisibleItem;
        mVisibleItemCount = visibleItemCount;
        if(isFirstEnter && totalItemCount>0){
            showImage(mFirstVisibleItem,mVisibleItemCount);
            isFirstEnter = false;
        }
    }

    private void showImage(int mFirstVisibleItem, int mVisibleItemCount){
        Bitmap bitmap = null;
        for(int i=mFirstVisibleItem;i<mFirstVisibleItem+mVisibleItemCount;i++){
            final String imageUrl = mImageThrumbUrls[i];
            final ImageView imageView = (ImageView)mGridView.findViewWithTag(imageUrl);
            bitmap = mImageDownLoader.dowmLoadImage(imageUrl, new ImageDownLoader.onImageLoaderListener() {
                @Override
                public void onImageLoaderListener(Bitmap bitmap, String url) {
                         if(bitmap!=null && imageView!=null){
                             imageView.setImageBitmap(bitmap);
                         }
                }
            });
        }
    }
}
