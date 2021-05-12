package com.devh.micro.news.entity;

import com.devh.micro.news.constant.MainCategory;
import com.devh.micro.news.constant.Press;
import com.devh.micro.news.constant.SubCategory;
import lombok.*;

import javax.persistence.*;

/**
 * <pre>
 * Description : 
 *     마지막으로 읽은 뉴스 상황에 대한 엔티티 (Database)
 * ===============================================
 * Member fields : 
 *     
 * ===============================================
 * 
 * Author : HeonSeung Kim
 * Date   : 2021-05-09
 * </pre>
 */
@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NewsLastRead {
    @Id
    private String rowId;
    @Enumerated(EnumType.STRING)
    private Press press;
    @Enumerated(EnumType.STRING)
    private MainCategory mainCategory;
    @Enumerated(EnumType.STRING)
    private SubCategory subCategory;
    private String articleId;
    private Long pubMillis;
    private Long scheduledMillis;
}
