package com.example.jhw.newexapplication;

public class BlockSeatInfo {

    private String traffic_num;
    private String reader_id;
    private String state;
    private String located;

    public String getTraffic_num() {
        return this.traffic_num;
    }
    public void setTraffic_num(String traffic_num) {
        this.traffic_num = traffic_num;
    }
    public String getReader_id() {
        return this.reader_id;
    }
    public void setReader_id(String reader_id) {
        this.reader_id = reader_id;
    }
    public String getState() {
        return this.state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getLocated() {
        return this.located;
    }
    public void setLocated(String located) {
        this.located = located;
    }


    public BlockSeatInfo(String traffic_num, String reader_id, String state, String located) {
        this.traffic_num = traffic_num;
        this.reader_id = reader_id;
        this.state = state;
        this.located = located;
    }
}
