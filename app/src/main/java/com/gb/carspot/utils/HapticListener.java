package com.gb.carspot.utils;

import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;

public class HapticListener implements View.OnTouchListener
{
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_UP)
        {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
        }
        return false;
    }
}
