package com.devh.micro.news.service;

import com.devh.micro.news.constant.NewsField;
import com.devh.micro.news.dto.NewsDTO;
import com.devh.micro.news.dto.NewsSearchParamsDTO;
import com.devh.micro.news.entity.News;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public interface NewsService {
    boolean indexAll(List<News> newsList);
    List<NewsDTO> search(NewsSearchParamsDTO newsSearchParamsDTO);

    /* 검색엔진 벌크 색인을 위해 각 엔티티를 맵으로 변환하기 위한 메소드 */
    default Map<String, Object> entityToMap(News news) {
        Map<String, Object> map = new HashMap<>();
        map.put(NewsField.ARTICLE_ID.getField(), news.getArticleId());
        map.put(NewsField.PRESS.getField(), news.getPress().getLowercase());
        map.put(NewsField.MAIN_CATEGORY.getField(), news.getMainCategory().getCamelCase());
        map.put(NewsField.SUB_CATEGORY.getField(), news.getSubCategory().getCamelCase());
        map.put(NewsField.ORIGINAL_LINK.getField(), news.getOriginalLink());
        map.put(NewsField.PUB_MILLIS.getField(), news.getPubMillis());
        map.put(NewsField.TITLE.getField(), news.getTitle());
        map.put(NewsField.SUMMARY.getField(), news.getSummary());
        return map;
    }

    default NewsDTO entityToDto(News news) {
        return NewsDTO.builder()
                .articleId(news.getArticleId())
                .press(news.getPress())
                .mainCategory(news.getMainCategory())
                .subCategory(news.getSubCategory())
                .originalLink(news.getOriginalLink())
                .pubMillis(news.getPubMillis())
                .title(news.getTitle())
                .summary(news.getSummary())
                .build();
    }

    default News dtoToEntity(NewsDTO newsDTO) {
        return News.builder()
                .articleId(newsDTO.getArticleId())
                .press(newsDTO.getPress())
                .mainCategory(newsDTO.getMainCategory())
                .subCategory(newsDTO.getSubCategory())
                .originalLink(newsDTO.getOriginalLink())
                .pubMillis(newsDTO.getPubMillis())
                .title(newsDTO.getTitle())
                .summary(newsDTO.getSummary())
                .build();
    }
}
