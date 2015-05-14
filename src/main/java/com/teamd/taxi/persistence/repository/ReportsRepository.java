package com.teamd.taxi.persistence.repository;

import com.teamd.taxi.service.ReportResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Repository
public class ReportsRepository {
    private JdbcTemplate template;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        template = new JdbcTemplate(dataSource);
    }


    @Transactional(readOnly = true)
    public List<Map<String,Object >> getReport(ReportResolver resolver) {
        return template.query(resolver.getQuery(), resolver.getRowMapper(),resolver.getParams());
    }


}
