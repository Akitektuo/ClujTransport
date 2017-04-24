package com.akitektuo.clujtransport.util;/*Copyright  © 2015 Telenav, Inc. All rights reserved. Telenav® is a registered trademark of Telenav, Inc.,Sunnyvale, California in the
United States and may be registered in other countries. Other names may be trademarks of their respective owners.*/

public class BusLineItem {

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public int getBusType() {
        return busType;
    }

    public void setBusType(int busType) {
        this.busType = busType;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    private String line;

    private String time;

    private String station;

    private int busType;

    public BusLineItem(int busType, String line, String time, String station){
        setBusType(busType);
        setLine(line);
        setTime(time);
        setStation(station);
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
