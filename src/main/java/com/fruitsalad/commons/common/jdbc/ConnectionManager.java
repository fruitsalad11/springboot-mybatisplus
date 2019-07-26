package com.fruitsalad.commons.common.jdbc;

//数据库连接池  单例模式


import com.fruitsalad.commons.common.utils.PropertiesLoader;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 采用 高性能数据库连接池  HikariCP 数据源
 *
 */

public final class ConnectionManager {

	
	static PropertiesLoader loader = new PropertiesLoader("application.properties");
	
    private static ConnectionManager instance;

    private HikariDataSource dsWrite;
    private ConnectionManager() throws Exception {
     	HikariConfig config = new HikariConfig();
     	/**
     	 * 增加两个数据源配置 写入的
     	 */
     	
     	config.setMaximumPoolSize(20);
     	config.setMinimumIdle(4);
     	config.addDataSourceProperty("prepStmtCacheSize", "250");
     	config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
     	config.setConnectionTestQuery("SELECT 1");
    	config.setDataSourceClassName(loader.getProperty("dataSourceClassName"));
     	config.addDataSourceProperty("serverName", loader.getProperty("writeServerName"));
     	config.addDataSourceProperty("port",loader.getProperty("writePortNumber"));
     	config.addDataSourceProperty("databaseName",loader.getProperty("writeDatabaseName"));
     	config.addDataSourceProperty("user",loader.getProperty("writeUser"));
     	config.addDataSourceProperty("password",loader.getProperty("writePassword"));

     	
//     	config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
//     	config.addDataSourceProperty("serverName", "172.16.6.204");
//     	config.addDataSourceProperty("port","3306");
//     	config.addDataSourceProperty("databaseName","wgproject");
//     	config.addDataSourceProperty("user","jiuzhi");
//     	config.addDataSourceProperty("password","123456");
     	
     	//增加以下两行代码
     	config.addDataSourceProperty("useUnicode", "true");
     	dsWrite = new HikariDataSource(config);
    }

    
    /**
     * 进行线程同步
     * 需要在创建类的时候进行同步，所以只要将创建和getInstance()分开，单独为创建加synchronized关键字
     */
    private static synchronized void syncInit() {  
        if (instance == null) {  
        	 try {
                 instance = new ConnectionManager();
             } catch (Exception e) {
                 e.printStackTrace();
             }  
        }  
    }  
    
    
    public  static  final ConnectionManager getInstance()   {
        if (instance == null) {
        	syncInit(); 
        }
        return instance;
    }

    public synchronized final Connection getConnection(String ...mes) throws SQLException {
    	Connection con = null;
        try {
				  con = dsWrite.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            finalize();//清空数据库链接
            syncInit(); //初始化数据库链接
            con=dsWrite.getConnection();//重新获取数据库链接
        }
        return con;
    }

    public  void finalize() {
    	dsWrite.close();
    	instance=null;
    }

}
