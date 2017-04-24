package com.akitektuo.clujtransport.util;

/**
 * Created by AoD Akitektuo on 10-Jun-16.
 */
public class BusPoiItem {

    public static final int BUS = 0;
    public static final int TROLLEY_BUS = 1;
    public static final int TRAM = 2;
    public static final int MINIBUS = 3;

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

    public enum BusType{
        BUS(0),
        TROLLEY_BUS(1),
        TRAM(2),
        MINIBUS(3);

        int value;

        BusType(int value){
            this.value = value;
        }
    }
    private BusType type;

    private String line;

    private String time;

    private int busType;

    public BusPoiItem(BusType type, String line, String time){
        setType(type);
        setLine(line);
        setTime(time);
    }

    public BusPoiItem(int busType, String line, String time) {
        setBusType(busType);
        setLine(line);
        setTime(time);
    }

    public BusType getType() {
        return type;
    }

    public void setType(BusType type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
