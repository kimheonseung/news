package com.devh.micro.news.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <pre>
 * Description
 *     News Entity 엘라스틱서치 필드명 상수
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
 * Date   : 2021-05-05
 * </pre>
 */
@Getter
@AllArgsConstructor
public enum NewsField {
    ARTICLE_ID("articleId"),
    PRESS("press"),
    MAIN_CATEGORY("mainCategory"),
    SUB_CATEGORY("subCategory"),
    ORIGINAL_LINK("originalLink"),
    PUB_MILLIS("pubMillis"),
    TITLE("title"),
    SUMMARY("summary");
    private final String field;
}
