package com.example.jhw.newexapplication;

public class RealtimeStationArrivalInfo { // 오픈api를 통해 받아온 실시간 열차 도착 정보 저장 클래스

    private String rowNum;
    private String subwayId;
    private String updnLine;
    private String trainLineNm;
    private String barvlDt;
    private String bstatnNm;
    private String arvlMsg2;
    private String btrainNo;

    public RealtimeStationArrivalInfo() {
    }

    public String getRowNum() {
        return this.rowNum;
    }
    public void setRowNum(String rowNum) {
        this.rowNum = rowNum;
    }
    public String getSubwayId() {
        return this.subwayId;
    }
    public void setSubwayId(String subwayId) {
        this.subwayId = subwayId;
    }
    public String getUpdnLine() {
        return this.updnLine;
    }
    public void setUpdnLine(String updnLine) {
        this.updnLine = updnLine;
    }
    public String getTrainLineNm() {
        return this.trainLineNm;
    }
    public void setTrainLineNm(String trainLineNm) {
        this.trainLineNm = trainLineNm;
    }
    public String getBarvlDt() {
        return this.barvlDt;
    }
    public void setBarvlDt(String barvlDt) {
        this.barvlDt = barvlDt;
    }
    public String getBstatnNm() {
        return this.bstatnNm;
    }
    public void setBstatnNm(String bstatnNm) {
        this.bstatnNm = bstatnNm;
    }
    public String getBtrainNo() {
        return this.btrainNo;
    }
    public void setBtrainNo(String btrainNo) {
        this.btrainNo = btrainNo;
    }
    public String getArvlMsg2() {
        return this.arvlMsg2;
    }
    public void setArvlMsg2(String arvlMsg2) {
        this.arvlMsg2 = arvlMsg2;
    }

    public RealtimeStationArrivalInfo(String bstatnNm, String btrainNo, String arvlMsg2) {
        this.bstatnNm = bstatnNm;
        this.btrainNo = btrainNo;
        this.arvlMsg2 = arvlMsg2;
    }

    public RealtimeStationArrivalInfo(String rowNum, String subwayId, String updnLine, String trainLineNm, String barvlDt, String bstatnNm, String btrainNo, String arvlMsg2) {
        this.rowNum = rowNum;
        this.subwayId = subwayId;
        this.updnLine = updnLine;
        this.trainLineNm = trainLineNm;
        this.barvlDt = barvlDt;
        this.bstatnNm = bstatnNm;
        this.btrainNo = btrainNo;
        this.arvlMsg2 = arvlMsg2;
    }
}
