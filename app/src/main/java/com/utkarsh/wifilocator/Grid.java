package com.utkarsh.wifilocator;

import android.content.Context;

import java.util.ArrayList;

public class Grid {
    int rssi1;
    int rssi2;
    int rssi3;
    int X;
    int Y;
    int rpid;

    public int getRssi1() {
        return rssi1;
    }

    public Grid(int rssi1, int rssi2, int rssi3, int x, int y, int rpid) {
        this.rssi1 = rssi1;
        this.rssi2 = rssi2;
        this.rssi3 = rssi3;
        X = x;
        Y = y;
        this.rpid = rpid;
    }

    public void setRssi1(int rssi1) {
        this.rssi1 = rssi1;
    }

    public int getRssi2() {
        return rssi2;
    }

    public void setRssi2(int rssi2) {
        this.rssi2 = rssi2;
    }

    public int getRssi3() {
        return rssi3;
    }

    public void setRssi3(int rssi3) {
        this.rssi3 = rssi3;
    }

    public int getX() {
        return X;
    }

    public void setX(int x) {
        X = x;
    }

    public int getY() {
        return Y;
    }

    public void setY(int y) {
        Y = y;
    }

    public int getRpid() {
        return rpid;
    }

    public void setRpid(int rpid) {
        this.rpid = rpid;
    }


}
