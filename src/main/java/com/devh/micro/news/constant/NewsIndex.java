package com.devh.micro.news.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <pre>
 * Description :
 *     News 관련 엘라스틱서치 상수값
 *     news_2021-05
 *     {INDEX_NAME_PRE}{INDEX_DELIMITER}foo
 * ===============================================
 * Member fields :
 *
 * ===============================================
 *
 * Author : HeonSeung Kim
 * Date   : 2021-05-05
 * </pre>
 */
@Getter
@AllArgsConstructor
public enum NewsIndex {
    INDEX_NAME_PRE("news"),
    INDEX_DELIMITER("_"),
    TEMPLATE_ALIAS("news"),
    PATTERN("news*");
    private final String value;
}
