package org.qiaoer.photogallery;

import android.app.Fragment;

public class PhotoGalleryActivity extends SingleFragmentActivity {

    @Override
    Fragment createFragment() {
        return new PhotoGalleryFragment();
    }
}
