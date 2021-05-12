package com.devh.micro.news.service;

import com.devh.micro.news.constant.NewsField;
import com.devh.micro.news.dto.NewsSearchParamsDTO;
import com.devh.micro.news.result.ResultToJsonConverter;
import com.devh.micro.news.util.ExceptionUtils;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * <pre>
 * Description :
 *     엘라스틱서치 검색 컴퍼넌트
 * ===============================================
 * Member fields :
 *
 * ===============================================
 *
 * Author : HeonSeung Kim
 * Date   : 2021-05-12
 * </pre>
 */
@Component
@RequiredArgsConstructor
public class NewsSearchService extends AbstractESSearch {
    private final Logger logger = LoggerFactory.getLogger(NewsSearchService.class);

    private NewsSearchParamsDTO newsSearchParamsDTO;

    /* DI */
    private final RestHighLevelClient restHighLevelClient;

    public void setNewsSearchParamsDTO(NewsSearchParamsDTO newsSearchParamsDTO) {
        this.newsSearchParamsDTO = newsSearchParamsDTO;
    }

    @Override
    protected void setSearchTimeMillis() {
        super.fromMillis = this.newsSearchParamsDTO.getFromMillis();
        super.toMillis = this.newsSearchParamsDTO.getToMillis();
    }

    @Override
    protected SearchSourceBuilder addQuery() {
        resetSearchCondition();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(super.nResult);
        searchSourceBuilder.from(super.nDocStart);
        searchSourceBuilder.sort(super.sidx, SortOrder.valueOf(super.sord));
        searchSourceBuilder.query(createBoolQuery(this.newsSearchParamsDTO));
        return searchSourceBuilder;
    }

    @Override
    protected SearchSourceBuilder addAggregation(SearchSourceBuilder searchSourceBuilder) {
        return searchSourceBuilder;
    }

    @Override
    protected SearchResponse getSearchResponse(SearchSourceBuilder searchSourceBuilder, long fromMillis, long toMillis) {
        SearchResponse searchResponse = null;
        try {
            SearchRequest searchRequest = new SearchRequest();
            searchRequest.indices(getIndices()).source(searchSourceBuilder);
            long start = System.currentTimeMillis();
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            long end = System.currentTimeMillis();
            logger.info("index to search : {}", Arrays.toString(searchRequest.indices()));
            logger.info("search query : {}", searchSourceBuilder);
            double execTime = (end - start) / 1000.0;
            logger.info("execute time : {}", execTime);
        } catch (Exception e) {
            ExceptionUtils.getInstance().printErrorLogWithException(logger, e);
        }
        return searchResponse;
    }

    @Override
    protected JSONObject getJsonResult(SearchResponse searchResponse) {
        ResultToJsonConverter resultToJsonConverter = ResultToJsonConverter.init();

        List<Map<String, Object>> resultList = new ArrayList<>();
        SearchHit[] searchHits = searchResponse.getHits().getHits();

        for(SearchHit searchHit : searchHits)
            resultList.add(searchHit.getSourceAsMap());

        resultToJsonConverter.putResultMapToResultJson(resultList);

        return resultToJsonConverter.get();
    }

    private String[] getIndices() {
        return new String[] {"news_*"};
    }
    
    /**
     * <pre>
     * Description
     *     뉴스 검색 파라미터들을 통해 불쿼리를 만든다.
     * ===============================================
     * Parameters
     *     NewsSearchParamsDTO newsSearchParamsDTO
     * Returns
     *     BoolQueryBuilder
     * Throws
     *     
     * ===============================================
     *
     * Author : HeonSeung Kim
     * Date   : 2021-05-13
     * </pre>
     */
    private BoolQueryBuilder createBoolQuery(NewsSearchParamsDTO newsSearchParamsDTO) {
        final boolean isPress = newsSearchParamsDTO.getPress() != null;
        final boolean isKeyword = newsSearchParamsDTO.getKeyword() != null;
        final boolean isMainCategory = newsSearchParamsDTO.getMainCategory() != null;
        final boolean isSubCategory = newsSearchParamsDTO.getSubCategory() != null;
        final boolean isFrom = newsSearchParamsDTO.getFromMillis() > 0;
        final boolean isTo = newsSearchParamsDTO.getToMillis() > 0;

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        if(isPress)
            boolQueryBuilder.must(QueryBuilders.termQuery(NewsField.PRESS.getField(), newsSearchParamsDTO.getKeyword()));

        if(isKeyword) {
            BoolQueryBuilder innerBoolQuery = new BoolQueryBuilder();
            innerBoolQuery.should(QueryBuilders.wildcardQuery(NewsField.TITLE.getField(), "*"+newsSearchParamsDTO.getKeyword()+"*"));
            innerBoolQuery.should(QueryBuilders.wildcardQuery(NewsField.SUMMARY.getField(), "*"+newsSearchParamsDTO.getKeyword()+"*"));
            boolQueryBuilder.must(innerBoolQuery);
        }

        if(isMainCategory)
            boolQueryBuilder.must(QueryBuilders.termQuery(NewsField.MAIN_CATEGORY.getField(), newsSearchParamsDTO.getMainCategory().getCamelCase()));

        if(isSubCategory)
            boolQueryBuilder.must(QueryBuilders.termQuery(NewsField.SUB_CATEGORY.getField(), newsSearchParamsDTO.getSubCategory().getCamelCase()));

        if(isFrom && isTo)
            boolQueryBuilder.must(QueryBuilders.rangeQuery(super.sidx).from(newsSearchParamsDTO.getFromMillis()).to(newsSearchParamsDTO.getToMillis()));
        else if(isFrom)
            boolQueryBuilder.must(QueryBuilders.rangeQuery(super.sidx).from(newsSearchParamsDTO.getFromMillis()));
        else if(isTo)
            boolQueryBuilder.must(QueryBuilders.rangeQuery(super.sidx).to(newsSearchParamsDTO.getToMillis()));

        if(!isPress && !isKeyword && !isMainCategory && !isSubCategory && !isFrom && !isTo)
            boolQueryBuilder.must(QueryBuilders.matchAllQuery());

        return boolQueryBuilder;
    }
}
