package com.fruitsalad.commons.common.jdbc;


import com.fruitsalad.commons.common.utils.JavaBeanUtil;
import com.fruitsalad.commons.common.utils.JsonTool;
import com.fruitsalad.commons.common.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;

/**
 * 此基础实现JDBC模板 升级工具类 采用占位符形式传参 格式 为 对象字段 #{name}　与入参　MAP 自动替换  提供日常用到的 增 删 改 查 对数据库操作
 *
 * @author tansixiang
 * @version 1.1
 * @category 此方法主要升级SQL执行模版 升级 采用占位符形式传参  传入对象为 Map<String,Object> 通过占位符形式自动替换成相应值
 */
@SuppressWarnings("resource")
public class JdbcTemplet {
    private static Logger log = LoggerFactory.getLogger(JdbcTemplet.class.getName());
    Map<String, Object> map = new HashMap<String, Object>();
    Map<String, String> map1 = new HashMap<String, String>();

    /**
     * 查询返回Map对象
     * 查询条件使用必须SQL 语句封装占位符必须与入参顺序一直
     *
     * @param sql 待执的SQL语言
     * @param obj 与SQL里面相对应占位符？ 与之对应参数
     * @param mes R 展示类和非时效性，W 总业务判断的主库（数据变更操作）
     * @return
     */
    public Map<String, Object> queryDateTempletForMap(String sql, Object[] obj, String... mes) {
        ConnectionManager cm = ConnectionManager.getInstance();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = cm.getConnection(mes);
            try {
                stmt = conn.prepareStatement(sql);
                if (obj != null && obj.length > 0)
                    for (int i = 0; i < obj.length; i++) {
                        stmt.setObject(i + 1, obj[i]);
                    }
                rs = stmt.executeQuery();
                ResultSetMetaData md = rs.getMetaData();
                int columns = md.getColumnCount();
                while (rs.next()) {
                    for (int i = 1; i <= columns; i++) {
                        String str = md.getColumnLabel(i);
                        Object object = rs.getObject(i);
                        object = rs.getObject(i) == null ? "" : rs.getObject(i);
                        if (object instanceof Integer) {
                            map.put((str.indexOf("_") >= 1) ? JavaBeanUtil.toCamelCaseString(str) : str, object.toString());
                        } else {
                            map.put((str.indexOf("_") >= 1) ? JavaBeanUtil.toCamelCaseString(str) : str, object);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                map.put("logTye", "MySqlError");
                map.put("logMes", e.getMessage());
                log.error("数据查询异常：" + e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
            map.put("logTye", "MySqlError");
            map.put("logMes", e.getMessage());
            log.error("数据库链接获取异常" + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }

            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();

            }
            try {
                if (conn != null) {
                    conn.close();
                }

            } catch (Exception e2) {
                e2.printStackTrace();

            }
        }

        return map;
    }


    /**
     * 查询返回Map对象
     * 查询条件使用必须SQL 语句封装占位符必须与入参顺序一直
     *
     * @param sql                待执的SQL语言
     * @param Map<String,Object> 与SQL里面相对应占位符 格式 ：#{name} Map的key 与之对应参数
     * @param mes                R 展示类和非时效性，W 总业务判断的主库（数据变更操作）
     * @return
     */
    public Map<String, Object> queryDateTempletForMap(String sql, Map<String, Object> map, String... mes) {
        Map<String, Object> returnMap = new HashMap<String, Object>();
        ConnectionManager cm = ConnectionManager.getInstance();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = cm.getConnection(mes);
            try {
                String[] muilStr = StringUtil.sqlPatternRender(sql);
                if (muilStr == null) {
                    stmt = conn.prepareStatement(sql);
                } else {
                    stmt = conn.prepareStatement(muilStr[0]);
                    String[] obj = muilStr[1].split(";");
                    if (obj != null && obj.length > 0)
                        for (int i = 0; i < obj.length; i++) {
                            if (map.containsKey(obj[i]))
                                stmt.setObject(i + 1, map.get(obj[i]).toString());
                            else
                                throw new Exception(
                                        "SQL语句与相应参数不对应 Map 缺少相应Key :" + obj[i]);
                        }
                }
                stmt.setQueryTimeout(9);
                rs = stmt.executeQuery();
                ResultSetMetaData md = rs.getMetaData();
                int columns = md.getColumnCount();
                while (rs.next()) {
                    for (int i = 1; i <= columns; i++) {
                        String str = md.getColumnLabel(i);
                        Object object = rs.getObject(i);
                        object = rs.getObject(i) == null ? "" : rs.getObject(i);

                        if (object instanceof Integer) {
                            returnMap.put((str.indexOf("_") >= 1) ? JavaBeanUtil.toCamelCaseString(str) : str, object.toString());
                        } else {
                            returnMap.put((str.indexOf("_") >= 1) ? JavaBeanUtil.toCamelCaseString(str) : str, object);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("数据库 获取异常：{}", StringUtil.getMysqlExceptionMes(e));
            }

        } catch (Exception e) {
            log.error("数据库 获取异常：{}", StringUtil.getMysqlExceptionMes(e));
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }

            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();

            }
            try {
                if (conn != null) {
                    conn.close();
                }

            } catch (Exception e2) {
                e2.printStackTrace();

            }
        }

        return returnMap;
    }


    /**
     * 返回一个List集合对象
     * 查询条件使用必须SQL 语句封装占位符必须与入参顺序一直
     * 查询条件使用必须SQL 语句封装占位符必须与入参顺序一直
     *
     * @param sql 待执的SQL语言
     * @param obj 与SQL里面相对应占位符？ 与之对应参数
     * @param mes R 展示类和非时效性，W 总业务判断的主库（数据变更操作）
     * @return
     */
    public List<Map<String, Object>> queryDateTempletForList(String sql, Object[] obj, String... mes) throws Exception {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        ConnectionManager cm = ConnectionManager.getInstance();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = cm.getConnection(mes);
            try {
                stmt = conn.prepareStatement(sql);
                if (obj != null && obj.length > 0)
                    for (int i = 0; i < obj.length; i++) {
                        stmt.setObject(i + 1, obj[i]);
                    }
                rs = stmt.executeQuery();
                ResultSetMetaData md = rs.getMetaData();
                int columns = md.getColumnCount();
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    for (int i = 1; i <= columns; i++) {

                        String str = md.getColumnLabel(i);
                        Object object = rs.getObject(i);
                        object = rs.getObject(i) == null ? "" : rs.getObject(i);
                        if (object instanceof Integer) {
                            map.put((str.indexOf("_") >= 1) ? JavaBeanUtil.toCamelCaseString(str) : str, object.toString());
                        } else {
                            map.put((str.indexOf("_") >= 1) ? JavaBeanUtil.toCamelCaseString(str) : str, object);
                        }
                    }
                    list.add(map);
                }

            } catch (Exception e) {
                map.put("logTye", "MySqlError");
                map.put("logMes", e.getMessage());
                map.put("sql", sql);
                map.put("obj", obj);
                e.printStackTrace();
            }
        } catch (Exception e) {
            map.put("logTye", "MySqlError");
            map.put("logMes", e.getMessage());
            map.put("sql", sql);
            map.put("obj", obj);
            log.error("数据库链接获取异常：" + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }

            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();

            }
            try {
                if (conn != null) {
                    conn.close();
                }

            } catch (Exception e2) {
                e2.printStackTrace();

            }
        }

        return list;
    }


    /**
     * 返回一个List集合对象
     * 查询条件使用必须SQL 语句封装占位符必须与入参顺序一直
     * 查询条件使用必须SQL 语句封装占位符必须与入参顺序一直
     *
     * @param sql                待执的SQL语言
     * @param Map<String,Object> 与SQL里面相对应占位符 格式 ：#{name} Map的key 与之对应参数
     * @param mes                R 展示类和非时效性，W 总业务判断的主库（数据变更操作）
     * @return
     */
    public List<Map<String, Object>> queryDateTempletForList(String sql, Map<String, Object> map, String... mes) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        ConnectionManager cm = ConnectionManager.getInstance();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = cm.getConnection(mes);
            try {
                String[] muilStr = StringUtil.sqlPatternRender(sql);
                if (muilStr == null) {
                    stmt = conn.prepareStatement(sql);
                } else {
                    stmt = conn.prepareStatement(muilStr[0]);
                    String[] obj = muilStr[1].split(";");
                    if (obj != null && obj.length > 0)
                        for (int i = 0; i < obj.length; i++) {
                            if (map.containsKey(obj[i])) {
                                stmt.setObject(i + 1, map.get(obj[i]));
                            } else {
                                throw new Exception(
                                        "SQL语句与相应参数不对应 Map 缺少相应Key :" + obj[i]);
                            }
                        }
                }
                stmt.setQueryTimeout(9);
                rs = stmt.executeQuery();
                ResultSetMetaData md = rs.getMetaData();
                int columns = md.getColumnCount();
                while (rs.next()) {
                    Map<String, Object> returnmap = new HashMap<String, Object>();
                    for (int i = 1; i <= columns; i++) {
                        String str = md.getColumnLabel(i);
                        Object object = rs.getObject(i);
                        object = rs.getObject(i) == null ? "" : rs.getObject(i);
                        if (object instanceof Integer) {
                            returnmap.put((str.contains("_")) ? JavaBeanUtil.toCamelCaseString(str) : str, object.toString());
                        } else {
                            returnmap.put((str.contains("_")) ? JavaBeanUtil.toCamelCaseString(str) : str, object);
                        }
                    }
                    list.add(returnmap);
                }

            } catch (Exception e) {
                log.error("数据库 获取异常：{}", StringUtil.getMysqlExceptionMes(e));
            }
        } catch (Exception e) {
            log.error("数据库 获取异常：{}", StringUtil.getMysqlExceptionMes(e));
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }

            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();

            }
            try {
                if (conn != null) {
                    conn.close();
                }

            } catch (Exception e2) {
                e2.printStackTrace();

            }
        }

        return list;
    }

    /**
     * 返回一个List集合对象
     * 查询条件使用必须SQL 语句封装占位符必须与入参顺序一直
     * 查询条件使用必须SQL 语句封装占位符必须与入参顺序一直
     *
     * @param sql                待执的SQL语言
     * @param Map<String,Object> 与SQL里面相对应占位符 格式 ：#{name} Map的key 与之对应参数
     * @param mes                R 展示类和非时效性，W 总业务判断的主库（数据变更操作）
     * @return
     */
    public List<String> queryDateTempletForStringList(String sql, Map<String, Object> map, String... mes) {
        List<String> list = new ArrayList<String>();
        ConnectionManager cm = ConnectionManager.getInstance();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = cm.getConnection(mes);
            try {
                String[] muilStr = StringUtil.sqlPatternRender(sql);
                if (muilStr == null) {
                    stmt = conn.prepareStatement(sql);
                } else {
                    stmt = conn.prepareStatement(muilStr[0]);
                    String[] obj = muilStr[1].split(";");
                    if (obj != null && obj.length > 0)
                        for (int i = 0; i < obj.length; i++) {
                            if (map.containsKey(obj[i])) {
                                stmt.setObject(i + 1, map.get(obj[i]));
                            } else {
                                throw new Exception(
                                        "SQL语句与相应参数不对应 Map 缺少相应Key :" + obj[i]);
                            }
                        }
                }
                stmt.setQueryTimeout(9);
                rs = stmt.executeQuery();
                ResultSetMetaData md = rs.getMetaData();
                int columns = md.getColumnCount();
                while (rs.next()) {
                    String str = null;
                    for (int i = 1; i <= columns; i++) {
                        Object object = rs.getObject(i);
                        object = rs.getObject(i) == null ? "" : rs.getObject(i);
                        str = object.toString();
                    }
                    list.add(str);
                }

            } catch (Exception e) {
                log.error("数据库 获取异常：{}", StringUtil.getMysqlExceptionMes(e));
            }
        } catch (Exception e) {
            log.error("数据库 获取异常：{}", StringUtil.getMysqlExceptionMes(e));
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }

            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();

            }
            try {
                if (conn != null) {
                    conn.close();
                }

            } catch (Exception e2) {
                e2.printStackTrace();

            }
        }

        return list;
    }

    /**
     * 查询总数
     * 查询条件使用必须SQL 语句封装占位符必须与入参顺序一直
     *
     * @param sql 待执的SQL语言
     * @param obj 与SQL里面相对应占位符？ 与之对应参数
     * @param mes R 展示类和非时效性，W 总业务判断的主库（数据变更操作）
     * @return
     */
    public int queryDataCountTemplet(String sql, Object[] obj, String... mes) throws Exception {

        int count = 0;
        ConnectionManager cm = ConnectionManager.getInstance();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = cm.getConnection(mes);
            stmt = conn.prepareStatement(sql);
            if (obj != null && obj.length > 0)
                for (int i = 0; i < obj.length; i++) {
                    stmt.setObject(i + 1, obj[i]);
                }
            rs = stmt.executeQuery();
            while (rs.next())
                count = rs.getInt(rs.getMetaData().getColumnLabel(1));

        } catch (Exception e) {
            log.error("数据库查询数量：" + e.getMessage());
            throw new Exception(StringUtil.getMysqlExceptionMes(e));
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }

            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();

            }
            try {
                if (conn != null) {
                    conn.close();
                }

            } catch (Exception e2) {
                e2.printStackTrace();

            }
        }

        return count;
    }


    /**
     * 查询总数
     * 查询条件使用必须SQL 语句封装占位符必须与入参顺序一直
     *
     * @param sql                待执的SQL语言
     * @param Map<String,Object> 与SQL里面相对应占位符 格式 ：#{name} Map的key 与之对应参数
     * @param mes                R 展示类和非时效性，W 总业务判断的主库（数据变更操作）
     * @return
     * @throws Exception
     */
    public int queryDataCountTemplet(String sql, Map<String, Object> map, String... mes) throws Exception {

        int count = 0;
        ConnectionManager cm = ConnectionManager.getInstance();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = cm.getConnection(mes);
            String[] muilStr = StringUtil.sqlPatternRender(sql);
            if (muilStr == null) {
                stmt = conn.prepareStatement(sql);
            } else {
                stmt = conn.prepareStatement(muilStr[0]);
                String[] obj = muilStr[1].split(";");
                if (obj != null && obj.length > 0)
                    for (int i = 0; i < obj.length; i++) {
                        if (map.containsKey(obj[i])) {
                            stmt.setObject(i + 1, map.get(obj[i]));
                        } else {
                            throw new Exception(
                                    "SQL语句与相应参数不对应 Map 缺少相应Key :" + obj[i]);
                        }
                    }
            }
            rs = stmt.executeQuery();
            while (rs.next())
                count = rs.getInt(rs.getMetaData().getColumnLabel(1));

        } catch (Exception e) {
            log.error("数据库 获取异常：{}", StringUtil.getMysqlExceptionMes(e));
            throw new Exception(StringUtil.getMysqlExceptionMes(e));
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }

            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();

            }
            try {
                if (conn != null) {
                    conn.close();
                }

            } catch (Exception e2) {
                e2.printStackTrace();

            }
        }

        return count;
    }

    /**
     * 变更数据数据 此方法适用于 插入 更新 删除数据
     * 查询条件使用必须SQL 语句封装占位符必须与入参顺序一直
     *
     * @param sql 待执的SQL语言
     * @param obj 与SQL里面相对应占位符？ 与之对应参数
     * @param mes R 展示类和非时效性，W 总业务判断的主库（数据变更操作）
     * @return
     */
    public boolean changeDataTemplet(String sql, Object[] obj, String... mes) throws Exception {
        ConnectionManager cm = ConnectionManager.getInstance();
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean flag = false;
        try {
            conn = cm.getConnection(mes);
            try {
                conn.setAutoCommit(false);
                stmt = conn.prepareStatement(sql);
                if (obj != null && obj.length > 0)
                    for (int i = 0; i < obj.length; i++) {
                        stmt.setObject(i + 1, obj[i]);
                    }
                int temp = stmt.executeUpdate();
                conn.commit();
                if (temp >= 1) {
                    flag = true;
                }
            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
                map.put("logTye", "MySqlError");
                map.put("logMes", e.getMessage());
                map.put("sql", sql);
                map.put("obj", obj);
                log.error("入口异常：数据回滚：" + JsonTool.mapToJson(map));
            }
        } catch (Exception e) {
            log.error("数据库获取链接" + JsonTool.mapToJson(map));
            throw new Exception(StringUtil.getMysqlExceptionMes(e));
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }

            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }


        return flag;
    }


    /**
     * 变更数据数据 此方法适用于 插入 更新 删除数据
     * 查询条件使用必须SQL 语句封装占位符必须与入参顺序一直
     *
     * @param sql                待执的SQL语言
     * @param Map<String,Object> 与SQL里面相对应占位符 格式 ：#{name} Map的key 与之对应参数
     * @param mes                R 展示类和非时效性，W 总业务判断的主库（数据变更操作）
     * @return
     * @throws Exception
     */
    public boolean changeDataTemplet(String sql, Map<String, Object> map, String... mes) throws Exception {
        ConnectionManager cm = ConnectionManager.getInstance();
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean flag = false;
        try {
            conn = cm.getConnection(mes);
            try {
                conn.setAutoCommit(false);

                String[] muilStr = StringUtil.sqlPatternRender(sql);
                if (muilStr == null) {
                    stmt = conn.prepareStatement(sql);
                } else {
                    stmt = conn.prepareStatement(muilStr[0]);
                    String[] obj = muilStr[1].split(";");
                    if (obj != null && obj.length > 0)
                        for (int i = 0; i < obj.length; i++) {
                            if (map.containsKey(obj[i])) {
                                stmt.setObject(i + 1, map.get(obj[i]));
                            } else {
                                throw new Exception(
                                        "SQL语句与相应参数不对应 Map 缺少相应Key :" + obj[i]);
                            }
                        }
                }

                int temp = stmt.executeUpdate();
                conn.commit();
                if (temp >= 0) {
                    flag = true;
                }
            } catch (Exception e) {
                conn.rollback();
                log.error("数据库 数据回滚 异常信息：{}", StringUtil.getMysqlExceptionMes(e));
                throw new Exception(StringUtil.getMysqlExceptionMes(e));
            }
        } catch (Exception e) {
            log.error("数据库 链接 获取异常：{}", StringUtil.getMysqlExceptionMes(e));
            throw new Exception(StringUtil.getMysqlExceptionMes(e));
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }

            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }


        return flag;
    }


    /**
     * 用于批量操作数据-此方法适用于 插入 更新 删除数据
     * 查询条件使用必须SQL 语句封装占位符必须与入参顺序一直
     *
     * @param sql 待执的SQL语言
     * @param obj 与SQL里面相对应占位符？ 与之对应参数
     * @param mes R 展示类和非时效性，W 总业务判断的主库（数据变更操作）
     * @return
     * @throws Exception
     */
    public boolean changeDataTempletBatch(String sql, List<Map<String, Object>> list, String... mes) throws Exception {
        Map<String, Object> returnMap = new HashMap<String, Object>();
        boolean flag = false;
        ConnectionManager cm = ConnectionManager.getInstance();
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = cm.getConnection(mes);
            try {
                conn.setAutoCommit(false);
                String[] muilStr = StringUtil.sqlPatternRender(sql);
                if (muilStr == null) {
                    stmt = conn.prepareStatement(sql);//没有参数的SqL执行
                } else {
                    stmt = conn.prepareStatement(muilStr[0]);//改成替换后的数据
                    String[] obj = muilStr[1].split(";");
                    Iterator<Map<String, Object>> it = list.iterator();
                    while (it.hasNext()) {
                        Map<String, Object> map = it.next();
                        if (obj != null && obj.length > 0)
                            for (int i = 0; i < obj.length; i++) {
                                if (map.containsKey(obj[i])) {
                                    stmt.setObject(i + 1, map.get(obj[i]));
                                } else {
                                    throw new NullPointerException(
                                            "SQL语句与相应参数不对应 Map 缺少相应Key :" + obj[i]);
                                }
                            }
                        stmt.addBatch();
                    }

                }
                stmt.executeBatch();
                conn.commit();
                flag = true;
            } catch (Exception e) {
                conn.rollback();
                log.error("入库异常：数据回滚：{}", JsonTool.mapToJson(returnMap));
                throw new Exception(StringUtil.getMysqlExceptionMes(e));
            }
        } catch (Exception e) {
            log.error("数据库链接获取异常：{}", JsonTool.mapToJson(returnMap));
            throw new Exception(StringUtil.getMysqlExceptionMes(e));
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return flag;
    }

    /**
     * 用于批量操作数据-此方法适用于 插入 更新 删除数据
     * 查询条件使用必须SQL 语句封装占位符必须与入参顺序一直
     *
     * @param states 待执的SQL语言List
     *               Map<String,Object> 与SQL里面相对应占位符 格式 ：#{name} Map的key 与之对应参数
     * @param mes    R 展示类和非时效性，W 总业务判断的主库（数据变更操作）
     * @return
     */
    public boolean batchSaveRelation(List<String> states, Map<String, Object> map, String... mes) {
        ConnectionManager cm = ConnectionManager.getInstance();
        Connection conn = null;
        boolean flag = false;
        PreparedStatement stmt = null;
        try {
            conn = cm.getConnection(mes);
            try {
                int temp = 0;
                conn.setAutoCommit(false);
                for (String sql : states) {//遍历待执行的SQL语句
                    //stmt = conn.prepareStatement(sql);
                    String[] muilStr = StringUtil.sqlPatternRender(sql);
                    if (muilStr == null) {//是否含有替换展占位符
                        stmt = conn.prepareStatement(sql);
                    } else {
                        stmt = conn.prepareStatement(muilStr[0]);
                        String[] obj = muilStr[1].split(";");
                        if (obj != null && obj.length > 0) {
                            //遍历 执行占位符数据
                            for (int n = 0; n < obj.length; n++) {
                                if (map.containsKey(obj[n])) {
                                    stmt.setObject(n + 1, map.get(obj[n]));
                                } else {
                                    throw new NullPointerException(
                                            "SQL语句与相应参数不对应 Map 缺少相应Key :" + obj[n]);
                                }
                            }
                        }

                    }
                    // 	  stmt.addBatch();//批处理sql 对相同sql预计和对应值会
                    temp = temp + stmt.executeUpdate();
                }
                //  int[]  changeRow=	 stmt.executeBatch(); //受影响的行状态
                conn.commit();
                //  log.debug("本次批量执行执行结果如下："+changeRow);
                //if(temp==states.size()){
                flag = true;
                //}
            } catch (Exception e) {
                conn.rollback();
                log.error("数据库 获取异常：{}", StringUtil.getMysqlExceptionMes(e));
                throw new Exception(StringUtil.getMysqlExceptionMes(e));
            }
        } catch (Exception e) {
            log.error("数据库 获取链接异常：{}", StringUtil.getMysqlExceptionMes(e));
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }


        return flag;
    }


    /**
     * 用于批量操作数据-此方法适用于 插入 更新 删除数据
     * 查询条件使用必须SQL 语句封装占位符必须与入参顺序一直
     *
     * @param sql 待执的SQL语言
     * @param obj 与SQL里面相对应占位符？ 与之对应参数
     * @param mes R 展示类和非时效性，W 总业务判断的主库（数据变更操作）
     * @return
     */

    public boolean batchSaveRelation(List<Map<String, Object>> states, String... mes) {
        ConnectionManager cm = ConnectionManager.getInstance();
        Connection conn = null;
        boolean flag = false;
        PreparedStatement stmt = null;
        try {
            conn = cm.getConnection(mes);

            try {
                int temp = 0;
                conn.setAutoCommit(false);
                String sql = "";
                Object[] objs = null;
                for (int i = 0; i < states.size(); i++) {
                    //循环每条待执行的 SQL和Map
                    Map<String, Object> map = states.get(i);
                    sql = (String) map.get("sql");
                    objs = (Object[]) map.get("objs");
                    stmt = conn.prepareStatement(sql);
                    for (int j = 0; j < objs.length; j++) {
                        stmt.setObject(j + 1, objs[j]);
                    }
                    temp = temp + stmt.executeUpdate();
                }
                conn.commit();
                flag = true;
            } catch (Exception e) {
                conn.rollback();
                log.error("数据库 获取异常：{}", StringUtil.getMysqlExceptionMes(e));
                throw new Exception(StringUtil.getMysqlExceptionMes(e));
            }
        } catch (Exception e) {
            log.error("数据库 获取链接异常：{}", StringUtil.getMysqlExceptionMes(e));
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }


        return flag;
    }


    /**
     * 查询一个String字符串。
     * 用于批量操作数据-此方法适用于 插入 更新 删除数据
     * 查询条件使用必须SQL 语句封装占位符必须与入参顺序一直
     *
     * @param sql                待执的SQL语言
     * @param Map<String,Object> 与SQL里面相对应占位符 格式 ：#{name} Map的key 与之对应参数
     * @param mes                R 展示类和非时效性，W 总业务判断的主库（数据变更操作）
     * @return
     * @throws Exception
     */
    public String queryDataStringTemplet(String sql, Map<String, Object> map, String... mes) throws Exception {
        String str = null;
        ConnectionManager cm = ConnectionManager.getInstance();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = cm.getConnection(mes);
            String[] muilStr = StringUtil.sqlPatternRender(sql);
            if (muilStr == null) {
                stmt = conn.prepareStatement(sql);
            } else {
                stmt = conn.prepareStatement(muilStr[0]);
                String[] obj = muilStr[1].split(";");
                if (obj != null && obj.length > 0)
                    for (int i = 0; i < obj.length; i++) {
                        if (map.containsKey(obj[i]))
                            stmt.setObject(i + 1, map.get(obj[i]));
                        else
                            throw new NullPointerException(
                                    "SQL语句与相应参数不对应 Map 缺少相应Key :" + obj[i]);
                    }
            }
            rs = stmt.executeQuery();
            while (rs.next()) {
                if (rs.getObject(1) == null || rs.getObject(1).equals("")) {
                    str = "";
                } else {
                    str = String.valueOf(rs.getObject(1));
                }
            }

        } catch (Exception e) {
            log.error("数据库 获取异常：{}", StringUtil.getMysqlExceptionMes(e));
            throw new Exception(StringUtil.getMysqlExceptionMes(e));
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }

            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();

            }
            try {
                if (conn != null) {
                    conn.close();
                }

            } catch (Exception e2) {
                e2.printStackTrace();

            }
        }
        return str;
    }

    /**
     * 查询一个int字符串。
     * 用于批量操作数据-此方法适用于 插入 更新 删除数据
     * 查询条件使用必须SQL 语句封装占位符必须与入参顺序一直
     *
     * @param sql                待执的SQL语言
     * @param Map<String,Object> 与SQL里面相对应占位符 格式 ：#{name} Map的key 与之对应参数
     * @param mes                R 展示类和非时效性，W 总业务判断的主库（数据变更操作）
     * @return
     * @throws Exception
     */
    public int queryDataIntTemplet(String sql, Map<String, Object> map, String... mes) throws Exception {
        int str = 0;
        ConnectionManager cm = ConnectionManager.getInstance();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = cm.getConnection(mes);
            String[] muilStr = StringUtil.sqlPatternRender(sql);
            if (muilStr == null) {
                stmt = conn.prepareStatement(sql);
            } else {
                stmt = conn.prepareStatement(muilStr[0]);
                String[] obj = muilStr[1].split(";");
                if (obj != null && obj.length > 0)
                    for (int i = 0; i < obj.length; i++) {
                        if (map.containsKey(obj[i]))
                            stmt.setObject(i + 1, map.get(obj[i]));
                        else
                            throw new NullPointerException(
                                    "SQL语句与相应参数不对应 Map 缺少相应Key :" + obj[i]);
                    }
            }
            rs = stmt.executeQuery();
            while (rs.next()) {
                str = rs.getInt(1);
            }

        } catch (Exception e) {
            log.error("数据库 获取异常：{}", StringUtil.getMysqlExceptionMes(e));
            throw new Exception(StringUtil.getMysqlExceptionMes(e));
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }

            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();

            }
            try {
                if (conn != null) {
                    conn.close();
                }

            } catch (Exception e2) {
                e2.printStackTrace();

            }
        }
        return str;
    }

    /**
     * 返回一个List集合对象
     * 查询条件使用必须SQL 语句封装占位符必须与入参顺序一直
     * 查询条件使用必须SQL 语句封装占位符必须与入参顺序一直
     *
     * @param sql                待执的SQL语言
     * @param Map<String,Object> 与SQL里面相对应占位符 格式 ：#{name} Map的key 与之对应参数
     * @param mes                R 展示类和非时效性，W 总业务判断的主库（数据变更操作）
     * @return
     */
    public List<String> queryStringTempletForList(String sql, Map<String, Object> map, String... mes) {
        List<String> list = new ArrayList<String>();
        ConnectionManager cm = ConnectionManager.getInstance();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = cm.getConnection(mes);
            try {
                String[] muilStr = StringUtil.sqlPatternRender(sql);
                if (muilStr == null) {
                    stmt = conn.prepareStatement(sql);
                } else {
                    stmt = conn.prepareStatement(muilStr[0]);
                    String[] obj = muilStr[1].split(";");
                    if (obj != null && obj.length > 0)
                        for (int i = 0; i < obj.length; i++) {
                            if (map.containsKey(obj[i]))
                                stmt.setObject(i + 1, map.get(obj[i]));
                            else
                                throw new NullPointerException(
                                        "SQL语句与相应参数不对应 Map 缺少相应Key :" + obj[i]);
                        }
                }
                rs = stmt.executeQuery();
                while (rs.next()) {
                    String s = null;
                    if (rs.getObject(1) == null || rs.getObject(1).equals("")) {
                        s = "";
                    } else {
                        s = (String) rs.getObject(1);
                    }
                    list.add(s);
                }

            } catch (Exception e) {
                log.error("数据库 获取异常：{}", StringUtil.getMysqlExceptionMes(e));
            }
        } catch (Exception e) {
            log.error("数据库 获取异常：{}", StringUtil.getMysqlExceptionMes(e));
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }

            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();

            }
            try {
                if (conn != null) {
                    conn.close();
                }

            } catch (Exception e2) {
                e2.printStackTrace();

            }
        }

        return list;
    }

    /**
     * 返回一个List集合对象
     * 查询条件使用必须SQL 语句封装占位符必须与入参顺序一直
     * 查询条件使用必须SQL 语句封装占位符必须与入参顺序一直
     *
     * @param sql                待执的SQL语言
     * @param Map<String,Object> 与SQL里面相对应占位符 格式 ：#{name} Map的key 与之对应参数
     * @param mes                R 展示类和非时效性，W 总业务判断的主库（数据变更操作）
     * @return
     */
    public List<Integer> queryIntegerTempletForList(String sql, Map<String, Object> map, String... mes) {
        List<Integer> list = new ArrayList<Integer>();
        ConnectionManager cm = ConnectionManager.getInstance();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = cm.getConnection(mes);
            try {
                String[] muilStr = StringUtil.sqlPatternRender(sql);
                if (muilStr == null) {
                    stmt = conn.prepareStatement(sql);
                } else {
                    stmt = conn.prepareStatement(muilStr[0]);
                    String[] obj = muilStr[1].split(";");
                    if (obj != null && obj.length > 0)
                        for (int i = 0; i < obj.length; i++) {
                            if (map.containsKey(obj[i]))
                                stmt.setObject(i + 1, map.get(obj[i]));
                            else
                                throw new NullPointerException(
                                        "SQL语句与相应参数不对应 Map 缺少相应Key :" + obj[i]);
                        }
                }
                rs = stmt.executeQuery();
                while (rs.next()) {
                    Integer s = null;
                    if (rs.getObject(1) == null || rs.getObject(1).equals("")) {
                        s = 0;
                    } else {
                        s = (Integer) rs.getObject(1);
                    }
                    list.add(s);
                }

            } catch (Exception e) {
                log.error("数据库 获取异常：{}", StringUtil.getMysqlExceptionMes(e));
            }
        } catch (Exception e) {
            log.error("数据库 获取异常：{}", StringUtil.getMysqlExceptionMes(e));
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }

            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();

            }
            try {
                if (conn != null) {
                    conn.close();
                }

            } catch (Exception e2) {
                e2.printStackTrace();

            }
        }

        return list;
    }


    public Map<String, String> queryTempletForMap(String sql, Map<String, Object> map, String... mes) {
        Map<String, String> returnMap = new HashMap<String, String>();
        ConnectionManager cm = ConnectionManager.getInstance();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = cm.getConnection(mes);
            try {
                String[] muilStr = StringUtil.sqlPatternRender(sql);
                if (muilStr == null) {
                    stmt = conn.prepareStatement(sql);
                } else {
                    stmt = conn.prepareStatement(muilStr[0]);
                    String[] obj = muilStr[1].split(";");
                    if (obj != null && obj.length > 0)
                        for (int i = 0; i < obj.length; i++) {
                            if (map.containsKey(obj[i]))
                                stmt.setObject(i + 1, map.get(obj[i]).toString());
                            else
                                throw new Exception(
                                        "SQL语句与相应参数不对应 Map 缺少相应Key :" + obj[i]);
                        }
                }
                stmt.setQueryTimeout(9);
                rs = stmt.executeQuery();
                ResultSetMetaData md = rs.getMetaData();
                int columns = md.getColumnCount();
                while (rs.next()) {
                    for (int i = 1; i <= columns; i++) {
                        String str = md.getColumnLabel(i);
                        //			System.out.println(str);
                        Object object = rs.getObject(i);


                        object = rs.getObject(i) == null ? "" : rs.getObject(i);

                        if (object instanceof Integer) {
                            returnMap.put((str.indexOf("_") >= 1) ? JavaBeanUtil.toCamelCaseString(str) : str, object.toString());
                        } else if (object instanceof String) {
                            returnMap.put((str.indexOf("_") >= 1) ? JavaBeanUtil.toCamelCaseString(str) : str, object.toString());
                        } else {
                            returnMap.put((str.indexOf("_") >= 1) ? JavaBeanUtil.toCamelCaseString(str) : str, (String) object);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("数据库 获取异常：{}", StringUtil.getMysqlExceptionMes(e));
            }

        } catch (Exception e) {
            log.error("数据库 获取异常：{}", StringUtil.getMysqlExceptionMes(e));
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }

            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();

            }
            try {
                if (conn != null) {
                    conn.close();
                }

            } catch (Exception e2) {
                e2.printStackTrace();

            }
        }

        return returnMap;
    }


    public static boolean CurdDataTemplet(String sql, Map<String, String> map, String... mes) throws Exception {
        ConnectionManager cm = ConnectionManager.getInstance();
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean flag = false;
        try {
            conn = cm.getConnection(mes);
            try {
                conn.setAutoCommit(false);

                String[] muilStr = StringUtil.sqlPatternRender(sql);
                if (muilStr == null) {
                    stmt = conn.prepareStatement(sql);
                } else {
                    stmt = conn.prepareStatement(muilStr[0]);
                    String[] obj = muilStr[1].split(";");
                    if (obj != null && obj.length > 0)
                        for (int i = 0; i < obj.length; i++) {
                            if (map.containsKey(obj[i])) {
                                stmt.setObject(i + 1, map.get(obj[i]));
                            } else {
                                throw new Exception(
                                        "SQL语句与相应参数不对应 Map 缺少相应Key :" + obj[i]);
                            }
                        }
                }

                int temp = stmt.executeUpdate();
                conn.commit();
                if (temp >= 1) {
                    flag = true;
                }
            } catch (Exception e) {
                conn.rollback();
                log.error("数据库 数据回滚 异常信息：{}", StringUtil.getMysqlExceptionMes(e));
                throw new Exception(StringUtil.getMysqlExceptionMes(e));
            }
        } catch (Exception e) {
            log.error("数据库 链接 获取异常：{}", StringUtil.getMysqlExceptionMes(e));
            throw new Exception(StringUtil.getMysqlExceptionMes(e));
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }

            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }


        return flag;
    }


}
