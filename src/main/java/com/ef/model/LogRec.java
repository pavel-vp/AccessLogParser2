package com.ef.model;

import java.util.Date;

/**
 * Created by pasha on 01.03.18.
 */
public class LogRec {
    private String date;
    private String ip;
    private String request;
    private String status;
    private String useragent;

    public LogRec(String date, String ip, String request, String status, String useragent) {
        this.date = date;
        this.ip = ip;
        this.request = request;
        this.status = status;
        this.useragent = useragent;
    }

    public LogRec() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUseragent() {
        return useragent;
    }

    public void setUseragent(String useragent) {
        this.useragent = useragent;
    }
}
