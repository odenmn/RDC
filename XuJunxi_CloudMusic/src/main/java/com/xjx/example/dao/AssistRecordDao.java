package com.xjx.example.dao;

import java.sql.SQLException;

public interface AssistRecordDao {
    boolean addAssistRecord(int couponId, int friendId, String assistValue) throws SQLException;

    boolean hasUserAssisted(int invitedId) throws SQLException;
}
