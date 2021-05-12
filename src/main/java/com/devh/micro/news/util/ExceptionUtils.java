package com.devh.micro.news.util;

import org.json.simple.JSONObject;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * <pre>
 * Description :
 *     Exception 관련 유틸
 * ===============================================
 * Member fields :
 *
 * ===============================================
 *
 * Author : HeonSeung Kim
 * Date   : 2021-04-10
 * </pre>
 */
public class ExceptionUtils {

    /* Singleton */
    private static ExceptionUtils instance;
    public static ExceptionUtils getInstance() {
        if(instance == null)
            instance = new ExceptionUtils();

        return instance;
    }

    /**
     * <pre>
     * Description :
     *     예외 처리 공통화를 위한 Enum
     *
     * ===============================================
     * Member fields :
     *     MESSAGE("message")
     *     STACK_TRACE("stackTrace")
     * ===============================================
     *
     * Author : HeonSeung Kim
     * Date   : 2021/03/21
     * </pre>
     */
    public enum ExceptionKey {
        MESSAGE("message"),
        STACK_TRACE("stackTrace");

        private final String key;
        ExceptionKey(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    /**
     * <pre>
     * Description
     *     Exception의 printStackTrace 출력을 String으로 반환
     * ===============================================
     * Parameters
     *     Exception e
     * Returns
     *     String
     * Throws
     *     Nothing
     * ===============================================
     *
     * Author : HeonSeung Kim
     * Date   : 2021-04-10
     * </pre>
     */
    public String getPrintStackTraceToString(Exception e) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

    /**
     * <pre>
     * Description
     *     Logger와 Exception을 전달받아 특정한 포맷의 error로그 출력
     * ===============================================
     * Parameters
     *     Logger logger
     *     Exception e
     * Returns
     *     void
     * Throws
     *     Nothing
     * ===============================================
     *
     * Author : HeonSeung Kim
     * Date   : 2021-04-10
     * </pre>
     */
    public void printErrorLogWithException(Logger logger, Exception e) {
        logger.error("=====================ERROR======================");
        logger.error("\n"+getPrintStackTraceToString(e));
        logger.error("=====================ERROR======================");
    }

    /**
     * <pre>
     * Description
     *     Exception stackTrace를 JSONObject로 반환
     * ===============================================
     * Parameters
     *     Logger logger
     *     Exception e
     * Returns
     *     void
     * Throws
     *     Nothing
     * ===============================================
     *
     * Author : HeonSeung Kim
     * Date   : 2021-03-10
     * </pre>
     */
    public JSONObject toJson(Exception e) {

        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));

        BufferedReader br = new BufferedReader(
                new StringReader(
                        sw.toString().replaceAll("\t", "    ")
                )
        );

        ArrayList<String> stackTraceList = new ArrayList<>();

        try {

            String line;

            while( (line = br.readLine()) != null )
                stackTraceList.add(line);

        } catch (Exception e1) {
            e1.printStackTrace();
        }

        LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>();

        linkedHashMap.put(ExceptionKey.MESSAGE.getKey(), e.getMessage());
        linkedHashMap.put(ExceptionKey.STACK_TRACE.getKey(), stackTraceList);

        return new JSONObject(linkedHashMap);
    }
}
