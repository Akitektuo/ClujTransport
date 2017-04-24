package com.akitektuo.clujtransport.util;

import android.widget.ListView;

import com.skobbler.ngx.SKCoordinate;

public class StationInfoItem {

    private String name;
    private String address;
    private boolean tickets;
    private double firstCoordinate;
    private double secondCoordinate;
    private String lines;

    public StationInfoItem(String name, String address, boolean tickets, double firstCoordinate,
                           double secondCoordinate, String lines) {
        setName(name);
        setAddress(address);
        setTickets(tickets);
        setFirstCoordinate(firstCoordinate);
        setSecondCoordinate(secondCoordinate);
        setLines(lines);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isTickets() {
        return tickets;
    }

    public void setTickets(boolean tickets) {
        this.tickets = tickets;
    }

    public double getSecondCoordinate() {
        return secondCoordinate;
    }

    public void setSecondCoordinate(double secondCoordinate) {
        this.secondCoordinate = secondCoordinate;
    }

    public double getFirstCoordinate() {
        return firstCoordinate;
    }

    public void setFirstCoordinate(double firstCoordinate) {
        this.firstCoordinate = firstCoordinate;
    }

    public String getLines() {
        return lines;
    }

    public void setLines(String lines) {
        this.lines = lines;
    }
}
