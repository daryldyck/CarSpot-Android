//
//  Advanced Android - MADS4006
//  CarSpot
//
//  Group 7
//  Brian Domingo - 101330689
//  Daryl Dyck - 101338429
//

package com.gb.carspot.utils;

import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;

// haptic feedback for touch inputs
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
