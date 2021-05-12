package com.devh.micro.news.service;

import com.devh.micro.news.util.ExceptionUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.json.simple.JSONObject;

/**
 * <pre>
 * Description :
 *     엘라스틱서치 검색 추상화 객체
 *     템플릿 패턴 구현
 * ===============================================
 * Member fields :
 *
 * ===============================================
 *
 * Author : HeonSeung Kim
 * Date   : 2021-05-12
 * </pre>
 */
abstract class AbstractESSearch {
    protected int nResult;      /* 검색 결과 갯수 */
    protected int nDocStart;    /* 검색 결과 시작 순서 */
    protected String sidx;      /* 정렬 기준 */
    protected String sord;      /* 정렬 차순 */
    protected long fromMillis;  /* 검색 기준 시각 시작 */
    protected long toMillis;    /* 검색 기준 시각 끝 */

    protected final int DEFAULT_RESULT = 30;
    protected final int DEFAULT_DOC_START = 0;
    protected final String DEFAULT_SIDX = "pubMillis";
    protected final String DEFAULT_SORD = "DESC";

    /* 검색조건 초기화 */
    protected void resetSearchCondition() {
        this.nResult = DEFAULT_RESULT;
        this.nDocStart = DEFAULT_DOC_START;
        this.sidx = DEFAULT_SIDX;
        this.sord = DEFAULT_SORD;
    }

    protected abstract void setSearchTimeMillis();
    protected abstract SearchSourceBuilder addQuery();
    protected abstract SearchSourceBuilder addAggregation(SearchSourceBuilder searchSourceBuilder);
    protected abstract SearchResponse getSearchResponse(SearchSourceBuilder searchSourceBuilder, long fromMillis, long toMillis);
    protected abstract JSONObject getJsonResult(SearchResponse searchResponse);

    /**
     * <pre>
     * Description
     *     검색 과정 수행
     *     템플릿 메소드
     * ===============================================
     * Parameters
     *     
     * Returns
     *     
     * Throws
     *     
     * ===============================================
     *
     * Author : HeonSeung Kim
     * Date   : 2021-05-12
     * </pre>
     */
    protected JSONObject workSearchProcess() {
        /* 0. 검색 시간 범위 설정 */
        setSearchTimeMillis();
        /* 1/ 쿼리 생성 */
        SearchSourceBuilder searchSourceBuilder = addQuery();
        /* 2. 집계 설정 */
        searchSourceBuilder = addAggregation(searchSourceBuilder);
        /* 3. 결과 요청청 */
        SearchResponse searchResponse = getSearchResponse(searchSourceBuilder, fromMillis, toMillis);
        /* 4. 응답 가공 */
        return getJsonResult(searchResponse);
   }


}
