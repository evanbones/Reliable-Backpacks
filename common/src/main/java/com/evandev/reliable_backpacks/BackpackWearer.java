package com.evandev.reliable_backpacks;

public interface BackpackWearer {
    void onBackpackOpen();
    void onBackpackClose();

    int getOpenCount();
    int getOpenTicks();
}