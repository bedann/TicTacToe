package com.intersofteagles.tictactoe.Commoners;

import android.view.animation.Interpolator;

/**
 * Created by Monroe on 4/25/2017.
 */
public class MyBounceInter implements Interpolator {


    double amplitude = 1;
    double freq = 10;

    public MyBounceInter(double amplitude, double freq) {
        this.amplitude = amplitude;
        this.freq = freq;
    }

    @Override
    public float getInterpolation(float v) {
        return (float)(-1*Math.pow(Math.E,-v/amplitude)*Math.cos(freq*v)+1);
    }
}
