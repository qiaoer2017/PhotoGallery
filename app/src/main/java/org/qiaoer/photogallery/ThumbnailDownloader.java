package org.qiaoer.photogallery;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by qiaoer on 16/6/7.
 */
public class ThumbnailDownloader<T> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;

    private Handler mHandler;

    private Map<T, String> requestMap = Collections.synchronizedMap(new HashMap<T, String>());
    private Handler mResponseHandler;
    private OnThumbnailDownloadedListener<T> mListener;

    public ThumbnailDownloader() {
        super(TAG);
    }

    public ThumbnailDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == MESSAGE_DOWNLOAD) {
                    @SuppressWarnings("unchecked")
                    T t = (T) msg.obj;
                    Log.i(TAG, "Got a request for url: " + requestMap.get(t));
                    handleRequest(t);
                }
            }
        };
    }

    private void handleRequest(final T t) {
        final String url = requestMap.get(t);
        if (url == null) {
            return;
        }

        try {
            byte[] bitmapBytes = new FlickrFetcher().getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            Log.i(TAG, "Bitmap created");

            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (requestMap.get(t) != url) {
                        return;
                    }

                    requestMap.remove(t);
                    mListener.onThumbnailDownloaded(t, bitmap);
                }
            });

        } catch (IOException e) {
            Log.e(TAG, "Error downloading image", e);
        }
    }

    public void queueThumbnail(T t, String url) {
        Log.i(TAG, "Got an URL: " + url);

        requestMap.put(t, url);

        mHandler.obtainMessage(MESSAGE_DOWNLOAD, t).sendToTarget();
    }

    public void clearQueue() {
        mHandler.removeMessages(MESSAGE_DOWNLOAD);
        requestMap.clear();
    }

    public interface OnThumbnailDownloadedListener<T> {
        void onThumbnailDownloaded(T t, Bitmap thumbnail);
    }

    public void setOnThumbnailDownloadedListener(OnThumbnailDownloadedListener<T> listener) {
        this.mListener = listener;
    }


}
