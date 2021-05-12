package com.devh.micro.news.repository;

import com.devh.micro.news.entity.NewsLastRead;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <pre>
 * Description : 
 *     마지막으로 읽은 뉴스 상황에 대한 레파지토리 (Database)
 * ===============================================
 * Member fields : 
 *     
 * ===============================================
 * 
 * Author : HeonSeung Kim
 * Date   : 2021-05-12
 * </pre>
 */
public interface NewsLastReadRepository extends JpaRepository<NewsLastRead, String> {
}
