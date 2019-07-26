package com.fruitsalad.commons.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    /**
     * <p>
     * 判断是否为NULL或空字符换
     * </P>
     *
     * @param arg
     * @return
     */
    public static boolean isNullEmpty(String arg) {
        if (arg == null) {
            return true;
        }
        if (arg.trim().equals("")) {
            return true;
        }
        return false;
    }

    /**
     * <p>
     * 判断俩字符串是否相等
     * </P>
     *
     * @param first
     * @param second
     * @return
     */
    public static boolean isEquals(String first, String second) {
        if (first.trim().equals(second.trim())) {
            return true;
        }
        return false;
    }

    /**
     * 字符串检测是否是字母加数字 而且8-16位
     *
     * @param password
     * @return
     */
    public static boolean matcherPatternPassword(String password) {
        Pattern regex = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,16}$");
        Matcher matcher = regex.matcher(password);
        return matcher.matches();
    }

    /**
     * 移动电话的检测
     *
     * @param emailStr
     * @return
     */
    public static boolean matcherPatternMObile(String phoneStr) {
        Pattern regex = Pattern.compile("^[1][3,4,5,6,7,8,9][0-9]{9}$");
        Matcher matcher = regex.matcher(phoneStr);
        return matcher.matches();
    }

    /**
     * <p>
     * 集合返回字符串，以逗号分隔
     * </P>
     *
     * @param list
     * @return
     */
    public static String getStringFromList(List<String> list) {
        if (null == list || list.size() <= 0) {
            return "";
        }

        StringBuffer buffer = new StringBuffer();
        for (String str : list) {
            buffer.append(str + ",");
        }

        String str = buffer.toString();
        return str.substring(0, str.length() - 1);
    }

    /**
     * 采用正则表达 过滤SQL需要替换占位符
     *
     * @param source
     */
    public static String[] sqlPatternRender(String source) {
        String newSql = source;
        StringBuffer sb = new StringBuffer("");
        Pattern p = Pattern.compile("(#\\{[^\\}]*\\})");
        Matcher m = p.matcher(source);
        while (m.find()) {
            String temp = m.group(1);
            sb.append(temp.replaceAll("\\}", "").replaceAll("#\\{", "")).append(";");
            newSql = newSql.replaceFirst(escapeExprSpecialWord(temp), "?");
        }
        if (sb.length() >= 1)
            return new String[]{newSql, sb.toString()};
        else
            return null;
    }

    /**
     * 转义正则特殊字符 （$()*+.[]?\^{},|）
     *
     * @param keyword
     * @return 转义后字符串
     */
    public static String escapeExprSpecialWord(String keyword) {
        if (StringUtils.isNotBlank(keyword)) {
            String[] fbsArr = {"\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|"};
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
        }
        return keyword;
    }

    /**
     * 捕获异常信息并返回字符串
     *
     * @param e
     */
    public static String getMysqlExceptionMes(Exception e) {
        // 获取异常信息
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(stream));
        return stream.toString();

    }

    /**
     * 数字型1是/2否转布尔型
     *
     * @param o
     */
    public static boolean toBooleanFromNum(Object o) {
        // 转换
        if (o == null)
            return false;
        if (o.equals("1")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 数字型1是/2否转布尔型
     *
     * @param o
     */
    public static String getUpdateSqlSelective(String[] param, Map sqlKey, Map map, String tableName,
                                               String cndKeyName) {
        StringBuilder strb = new StringBuilder(128);
        int t = 0;
        strb.append("UPDATE ").append(tableName).append(" SET ");
        for (String string : param) {
            if (map.containsKey(string)) {
                if (++t == 1) {// 第一个key前部无需加逗号
                    strb.append(sqlKey.get(string)).append("=");
                    strb.append("#{").append(string).append("}");
                } else {
                    strb.append(", ").append(sqlKey.get(string)).append("=");
                    strb.append("#{").append(string).append("}");
                }
            }
        }
        // 根据更新条件键加入条件
        if (map.containsKey(cndKeyName)) {
            strb.append(" WHERE ").append(sqlKey.get(cndKeyName)).append("=");
            strb.append("#{").append(cndKeyName).append("}");
        } else {
            strb.append(" WHERE 1=2");
        }
        return strb.toString();
    }

    /**
     * 数字型1是/2否转布尔型
     *
     * @param o
     */
    public static String getInsertSqlSelective(String[] param, Map sqlKey, Map map, String tableName) {
        StringBuilder strb = new StringBuilder(128);
        int t = 0;
        strb.append("INSERT INTO ").append(tableName).append("(");
        // 字段
        for (String string : param) {
            if (map.containsKey(string)) {
                if (++t == 1) {// 第一个key前部无需加逗号
                    strb.append(sqlKey.get(string));
                } else {
                    strb.append(", ").append(sqlKey.get(string));
                }
            }
        }
        strb.append(")values(");

        // 值参
        t = 0;
        for (String string : param) {
            if (map.containsKey(string)) {
                if (++t == 1) {// 第一个key前部无需加逗号
                    strb.append("#{").append(string).append("}");
                } else {
                    strb.append(", #{").append(string).append("}");
                }
            }
        }

        strb.append(")");

        return strb.toString();
    }

    /**
     * 增加时间格式验证
     *
     * @param email
     * @return
     */
    public static boolean validateDateFormat(String date) {
        Pattern p = Pattern.compile(
                "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
        return p.matcher(date).matches();

    }

    /**
     * 增加邮箱验证工具
     *
     * @param email
     * @return
     */
    public static boolean validateEmail(String email) {
        // Pattern pattern =
        // Pattern.compile("[0-9a-zA-Z]*.[0-9a-zA-Z]*@[a-zA-Z]*.[a-zA-Z]*",
        // Pattern.LITERAL);
        if (email == null) {
            return false;
        }

        // 验证开始

        // 不能有连续的.
        if (email.indexOf("..") != -1) {
            return false;
        }

        // 必须带有@
        int atCharacter = email.indexOf("@");
        if (atCharacter == -1) {
            return false;
        }

        // 最后一个.必须在@之后,且不能连续出现
        if (atCharacter > email.lastIndexOf('.') || atCharacter + 1 == email.lastIndexOf('.')) {
            return false;
        }

        // 不能以.,@结束和开始
        if (email.endsWith(".") || email.endsWith("@") || email.startsWith(".") || email.startsWith("@")) {
            return false;
        }

        return true;
    }

    /**
     * java 执行shell脚本 获取 返回结果
     *
     * @param shellString
     */
    public static void callShellResult(String shellString) {
        try {
            Process process = Runtime.getRuntime().exec(shellString);
            int exitValue = process.waitFor();
            if (0 != exitValue) {
                System.err.println("shell 脚本执行错误！");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            System.out.println("shell 脚本执行异常");
        }
    }

    /***
     * java 执行shell脚本 并获取执行过程信息
     *
     * @param command
     */
    public static void callShellStr(String command) {
        Process process = null;
        List<String> processList = new ArrayList<String>();
        try {
            // process = Runtime.getRuntime().exec("ps -aux");
            process = Runtime.getRuntime().exec("command");

            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            while ((line = input.readLine()) != null) {
                processList.add(line);
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String line : processList) {
            System.out.println(line);
        }
    }

    /**
     * 删除单个文件
     *
     * @param sPath 被删除文件的路径+文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String sPath) {
        Boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    // 创建目录
    public static boolean createDir(String destDirName) {
        File dir = new File(destDirName);
        if (dir.exists()) {// 判断目录是否存在
            System.out.println("创建目录失败，目标目录已存在！");
            return true;
        }
        if (!destDirName.endsWith(File.separator)) {// 结尾是否以"/"结束
            destDirName = destDirName + File.separator;
        }
        if (dir.mkdirs()) {// 创建目标目录
            System.out.println("创建目录成功！" + destDirName);
            return true;
        } else {
            System.out.println("创建目录失败！");
            return false;
        }
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     *
     * @param sPath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String sPath) {
        // 如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        Boolean flag = true;
        // 删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            } // 删除子目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag)
            return false;
        // 删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 除去Html标签
     *
     * @param inputString
     * @return
     */
    public static String Html2Text(String inputString) {
        String htmlStr = inputString; //含html标签的字符串
        String textStr = "";
        Pattern p_script;
        Matcher m_script;
        Pattern p_style;
        Matcher m_style;
        Pattern p_html;
        Matcher m_html;

        try {
            String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; //定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script> }
            String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; //定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style> }
            String regEx_html = "<[^>]+>"; //定义HTML标签的正则表达式

            p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
            m_script = p_script.matcher(htmlStr);
            htmlStr = m_script.replaceAll(""); //过滤script标签

            p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
            m_style = p_style.matcher(htmlStr);
            htmlStr = m_style.replaceAll(""); //过滤style标签

            p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
            m_html = p_html.matcher(htmlStr);
            htmlStr = m_html.replaceAll(""); //过滤html标签
            htmlStr = htmlStr.replace("&nbsp;", "");
            textStr = htmlStr;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return textStr;//返回文本字符串
    }
}
