package com.example.jhw.newexapplication;

public class TrainBlockInfo { // 서버 DB에서 받아온 열차 차량 정보 저장 클래스

    private String traffic_num;
    private String empty_seat;
    private String total_seat;

    public String getTraffic_num() {
        return this.traffic_num;
    }
    public void setTraffic_num(String traffic_num) {
        this.traffic_num = traffic_num;
    }
    public String getEmpty_seat() {
        return this.empty_seat;
    }
    public void setEmpty_seat(String empty_seat) {
        this.empty_seat = empty_seat;
    }
    public String getTotal_seat() {
        return this.total_seat;
    }
    public void setTotal_seat(String total_seat) {
        this.total_seat = total_seat;
    }

    public TrainBlockInfo(String traffic_num, String empty_seat, String total_seat) {
        this.traffic_num = traffic_num;
        this.empty_seat = empty_seat;
        this.total_seat = total_seat;

    }
}
