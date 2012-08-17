package com.broadcom.bt.le.api;

interface IBleProfileEventCallback {
    void onClientConnected(int connId, in String address,
                           boolean isConnected);
    void onAppRegisterCompleted(int status, int serIf);

}
