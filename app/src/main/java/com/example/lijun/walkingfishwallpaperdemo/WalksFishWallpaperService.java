package com.example.lijun.walkingfishwallpaperdemo;

import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.example.lijun.walkingfishwallpaperdemo.view.WalksFishManager;

public class WalksFishWallpaperService extends WallpaperService {
    @Override
    public Engine onCreateEngine() {
        return new WalksFishEngine();
    }

    private class WalksFishEngine extends WallpaperService.Engine {
        private final boolean DEBUG = BuildConfig.DEBUG;
        private static final String TAG = "WalksFishEngine";

        private WalksFishManager mWalksFishManager;


        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            if (DEBUG) {
                Log.d(TAG, " onCreate() ");
            }
            mWalksFishManager = new WalksFishManager(getApplicationContext(), surfaceHolder);
            setTouchEventsEnabled(true);

        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            if (DEBUG) {
                Log.d(TAG, " onVisibilityChanged() " + "visible = [" + visible + "]");
            }
            mWalksFishManager.onVisibilityChanged(visible);
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            mWalksFishManager.onTouchEvent(event);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            if (DEBUG) {
                Log.d(TAG, " onDestroy() ");
            }
            mWalksFishManager.onDestroyView();
        }
    }
}
