package org.qiaoer.photogallery;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

public class PhotoGalleryActivity extends SingleFragmentActivity {

    private static final String TAG = "PhotoGalleryActivity";

    @Override
    Fragment createFragment() {
        return new PhotoGalleryFragment();
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        PhotoGalleryFragment fragment = (PhotoGalleryFragment) getFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.i(TAG, "Received a new search query: " + query);

            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putString(FlickrFetcher.PREF_SEARCH_QUERY, query)
                    .commit();
        }

        fragment.updateItems();
    }
}
