package com.devh.micro.news.result;

import com.devh.micro.news.util.ExceptionUtils;
import org.json.simple.JSONObject;

/**
 * <pre>
 * Description :
 *     REST 호출을 통해 반환되는 결과를 담은 Map
 * ===============================================
 * Member fields :
 *
 * ===============================================
 *
 * Author : HeonSeung Kim
 * Date   : 2021/03/21
 * </pre>
 */
public class ResultToJsonConverter {

    private final JSONObject resultJson;

    private ResultToJsonConverter() {
        this.resultJson = new JSONObject();
    }

    public static ResultToJsonConverter init() {
        return new ResultToJsonConverter();
    }

    public void putResultMapToResultJson(Object resultObject) {
        this.resultJson.put("result-json", resultObject);
    }

    public void putResultMapToExceptionInformation(Exception e) {
        this.resultJson.put("result-exception", ExceptionUtils.getInstance().toJson(e));
    }

    public JSONObject get() {
        return this.resultJson;
    }
}
