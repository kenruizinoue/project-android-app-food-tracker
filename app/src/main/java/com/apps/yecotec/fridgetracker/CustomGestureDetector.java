package com.apps.yecotec.fridgetracker;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by kenruizinoue on 9/27/17.
 */

public class CustomGestureDetector implements View.OnTouchListener {

    private final GestureDetector gestureDetector;

    public CustomGestureDetector(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener()) ;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        //set to default distance if the user stop scrolling
        if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
            setSumToDefault();
        }

        return gestureDetector.onTouchEvent(motionEvent);
    }

    private final class GestureListener implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            simpleClick();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY) {
            customScroll(distanceX, distanceY);
            return true;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {
        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return true;
        }
    }

    public void setSumToDefault() {

    }

    public void customScroll(float horizontalDistance, float verticalDistance) {

    }

    public void simpleClick() {

    }

}