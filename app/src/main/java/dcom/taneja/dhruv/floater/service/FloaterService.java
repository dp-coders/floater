package dcom.taneja.dhruv.floater.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import dcom.taneja.dhruv.floater.R;

/**
 * Created by dhruvtaneja on 10/02/18.
 */

public class FloaterService extends Service {

    private View mFloatingView;
    private View mExpandedView;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;

    private int mInitialX;
    private int mInitialY;
    private int mInitialTouchX;
    private int mInitialTouchY;
    private boolean mIsCollapsed = true;
    private boolean mHasMoved;
    private boolean mDragCollapse;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget,
                null);
        mLayoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        mLayoutParams.gravity = Gravity.TOP | Gravity.END;
        mLayoutParams.x = 0;
        mLayoutParams.y = 100;
        mLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, mLayoutParams);

        mExpandedView = mFloatingView.findViewById(R.id.expandedView);

        mFloatingView.findViewById(R.id.collapseActionView)
                .setOnTouchListener((View view, MotionEvent motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mInitialX = mLayoutParams.x;
                    mInitialY = mLayoutParams.y;

                    mInitialTouchX = (int) motionEvent.getRawX();
                    mInitialTouchY = (int) motionEvent.getRawY();
                    return true;

                case MotionEvent.ACTION_UP:
                    float xDiff = motionEvent.getRawX() - mInitialX;
                    float yDiff = motionEvent.getRawY() - mInitialY;

                    if (mDragCollapse) {
                        expandFloater();
                        mHasMoved = false;
                        mDragCollapse = false;
                    } else {
                        if (xDiff < 10f && yDiff < 10f) {
                            toggleFloater();
                        }
                    }
                    return true;

                case MotionEvent.ACTION_MOVE:
                    if (!mIsCollapsed) {
                        mDragCollapse = true;
                        mHasMoved = true;
                    }
                    collapseFloater();
                    mLayoutParams.x = (int) (mInitialX + (motionEvent.getRawX() - mInitialTouchX));
                    mLayoutParams.y = (int) (mInitialY + (motionEvent.getRawY() - mInitialTouchY));
                    mWindowManager.updateViewLayout(mFloatingView, mLayoutParams);
                    return true;

                default:
                    return false;
            }
        });
    }

    private void toggleFloater() {
        if (mExpandedView.getVisibility() == View.GONE) {
            mExpandedView.setVisibility(View.VISIBLE);
        } else {
            mExpandedView.setVisibility(View.GONE);
        }
    }

    private void expandFloater() {
        mExpandedView.setVisibility(View.VISIBLE);
        mIsCollapsed = false;
    }

    private void collapseFloater() {
        mExpandedView.setVisibility(View.GONE);
        mIsCollapsed = true;
        mLayoutParams.x = mInitialX;
        mLayoutParams.y = mInitialY;
        mWindowManager.updateViewLayout(mFloatingView, mLayoutParams);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWindowManager.removeViewImmediate(mFloatingView);
    }
}
