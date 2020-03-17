package com.example.jhw.newexapplication;

public class StationInfo { // 오픈api를 통해 받아온 역 정보 저장 클래스
    private String subwayId;
    private String statnFnm;
    private String statnTnm;

    public StationInfo(String subwayId, String statnFnm, String statnTnm) {
        this.subwayId = subwayId;
        this.statnFnm = statnFnm;
        this.statnTnm = statnTnm;
    }

    public String getSubwayId() {
        return this.subwayId;
    }
    public void setSubwayId(String subwayId) {
        this.subwayId = subwayId;
    }

    public String getStatnFnm() {
        return this.statnFnm;
    }
    public void setStatnFnm(String statnFnm) {
        this.statnFnm = statnFnm;
    }

    public String getStatnTnm() {
        return this.statnTnm;
    }
    public void setStatnTnm(String statnTnm) {
        this.statnTnm = statnTnm;
    }


}
