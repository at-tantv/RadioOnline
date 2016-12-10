package com.tantv.vnradiotruyen.model;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by tantv on 28/09/2015.
 */
public class BusHolder {
    public static Bus mBus = new Bus(ThreadEnforcer.ANY);
    public static Bus get() {
        return mBus;
    }
}
