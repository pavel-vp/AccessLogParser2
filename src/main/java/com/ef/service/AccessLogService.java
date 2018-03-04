package com.ef.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by pasha on 03.03.18.
 */
@Service
public class AccessLogService {
    public static String durationHourly = "hourly";
    public static String durationDaily = "daily";

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<String> getIpsByParams(String duration, int threshold, Date startDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        if (durationHourly.equals(duration)) {
            calendar.add(Calendar.HOUR, 1);
        }
        if (durationDaily.equals(duration)) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        Date endDate = calendar.getTime();
        return getIpsByParamsByDates(threshold, startDate, endDate);
    }

    private List<String> getIpsByParamsByDates(int threshold, Date startDate, Date endDate) {
        String query = "select ip from logdata " +
                " where accessdate >= ? and accessdate < ? " +
                " group by ip" +
                " having count(1) > ? ;";
        return jdbcTemplate.query(query, new Object[] {startDate, endDate, threshold}, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getString(1);
            }
        });
    }

    public void storeIpsByParams(List<String> result, String duration, int threshold, Date startDate) {
        String query = "insert into logdataresult (ip, params) values (?, ?); ";
        String params = String.format("Query by duration=%s, threshold=%d, startdate=%s", duration, threshold, startDate);

        for (String ip : result) {
            jdbcTemplate.update(query, ip, params);
        }

    }
}
