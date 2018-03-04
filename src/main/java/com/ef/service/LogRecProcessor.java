package com.ef.service;

import com.ef.model.LogRec;
import com.ef.model.LogRecDB;
import org.springframework.batch.item.ItemProcessor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pasha on 02.03.18.
 */
public class LogRecProcessor implements ItemProcessor<LogRec, LogRecDB> {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public LogRecDB process(LogRec item) throws Exception {
        final Date date = sdf.parse(item.getDate());
        return new LogRecDB(date, item.getIp(), item.getRequest(), item.getStatus(), item.getUseragent());
    }
}
