package com.broadcom.bt.le.api;

interface IBleProfileEventCallback {
    void onClientConnected(int paramInt, in String paramString,
                           boolean paramBoolean);
    void onAppRegisterCompleted(int paramInt1, int paramInt2);

}
