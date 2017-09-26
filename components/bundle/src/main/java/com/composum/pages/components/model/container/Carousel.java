package com.composum.pages.components.model.container;

import com.composum.pages.commons.model.Container;

public class Carousel extends Container {

    public static final Integer DEFAULT_INTERVAL = 10000;

    private transient Boolean useControls;
    private transient Boolean showIndicators;
    private transient Boolean autoStart;
    private transient Boolean noPause;
    private transient Integer interval;

    public boolean getUseControls() {
        if (useControls == null) {
            useControls = getProperty ("useControls", Boolean.FALSE);
        }
        return useControls;
    }

    public boolean isShowIndicators() {
        if (showIndicators == null) {
            showIndicators = getProperty ("showIndicators", Boolean.FALSE);
        }
        return showIndicators;
    }

    public boolean isAutoStart() {
        if (autoStart == null) {
            autoStart = getProperty ("autoStart", Boolean.FALSE);
        }
        return autoStart;
    }

    public boolean isNoPause() {
        if (noPause == null) {
            noPause = getProperty ("noPause", Boolean.FALSE);
        }
        return noPause;
    }

    public int getInterval() {
        if (interval == null) {
            interval = getProperty ("interval", DEFAULT_INTERVAL);
        }
        return interval;
    }
}
