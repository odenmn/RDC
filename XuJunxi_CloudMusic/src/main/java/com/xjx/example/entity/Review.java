package com.xjx.example.entity;

import java.time.LocalDateTime;

public class Review {
    private Integer id;
    private Integer userId;
    private Integer uploadWorkId;
    private String content; // 要审核的类型，歌曲或者专辑
    private Integer status; // 举报状态, 0表示未处理，1表示处理中
    private boolean result; // 处理结果，true表示通过，false表示不通过
    private Integer version;
    private LocalDateTime uploadTime;
    private String statusStr;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getUploadWorkId() {
        return uploadWorkId;
    }

    public void setUploadWorkId(Integer uploadWorkId) {
        this.uploadWorkId = uploadWorkId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }
}



