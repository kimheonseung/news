package com.devh.micro.news.repository;

import com.devh.micro.news.entity.News;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * <pre>
 * Description : 
 *     뉴스 레파지토리 (Elasticsearch)
 * ===============================================
 * Member fields : 
 *     
 * ===============================================
 * 
 * Author : HeonSeung Kim
 * Date   : 2021-05-12
 * </pre>
 */
public interface NewsRepository extends ElasticsearchRepository<News, String> {

}
