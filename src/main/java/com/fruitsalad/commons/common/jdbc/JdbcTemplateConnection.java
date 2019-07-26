package com.fruitsalad.commons.common.jdbc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Component
public class JdbcTemplateConnection {
    @Value("${spring.datasource.driverClassName}")
    private String driverName;
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    public void getConnection() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            System.out.println(driverName);
            Class.forName(driverName);
            conn = DriverManager.getConnection(url, username, password);
            String sql = "select * from user";
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery(sql);
            while (rs.next()) {
                // 通过字段检索
                String id = rs.getString("id");
                String name = rs.getString("username");
                String url = rs.getString("password");

                // 输出数据
                System.out.print("ID: " + id);
                System.out.print(", 名称: " + name);
                System.out.print(", 密码: " + url);
                System.out.print("\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
