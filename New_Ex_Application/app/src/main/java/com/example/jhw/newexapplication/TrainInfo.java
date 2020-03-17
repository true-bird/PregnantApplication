package com.example.jhw.newexapplication;

public class TrainInfo { // 서버 DB에서 받아온 열차 정보 저장 클래스
    private String train_num;
    private String first_traffic_num;
    private String empty_seat;
    private String total_seat;

    public String getTrain_num() {
        return this.train_num;
    }
    public void setTrain_num(String train_num) {
        this.train_num = train_num;
    }
    public String getFirst_traffic_num() {
        return this.first_traffic_num;
    }
    public void setFirst_traffic_num(String first_traffic_num) {
        this.first_traffic_num = first_traffic_num;
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

    public TrainInfo(String train_num, String first_traffic_num, String empty_seat, String total_seat) {
        this.train_num = train_num;
        this.first_traffic_num = first_traffic_num;
        this.empty_seat = empty_seat;
        this.total_seat = total_seat;
    }

}
