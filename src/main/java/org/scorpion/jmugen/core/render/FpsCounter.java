package org.scorpion.jmugen.core.render;

public class FpsCounter {

    int framesCount, fps;
    long lastTs;

    public void init() {
        lastTs = System.currentTimeMillis();
    }

    public void incrementFrame() {
        framesCount++;
        if (framesCount < 0) {
            framesCount = 0;
        }
    }

    public int getFps() {
        long now = System.currentTimeMillis();
        fps = (int) (framesCount * (now - lastTs) / 1e3);
        lastTs = now;
        framesCount = 0;
        return fps;
    }

}
