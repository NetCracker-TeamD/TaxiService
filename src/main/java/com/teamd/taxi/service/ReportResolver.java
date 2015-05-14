package com.teamd.taxi.service;

import org.springframework.jdbc.core.RowMapper;

import javax.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * Created by vita on 5/3/15.
 */
public interface ReportResolver {


    public String getQuery() ;

    public RowMapper getRowMapper();

    public Object[]  getParams() ;



}
