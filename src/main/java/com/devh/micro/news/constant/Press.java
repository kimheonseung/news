package com.devh.micro.news.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <pre>
 * Description
 *     News Entity 언론사 상수
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
public enum Press {
    YONHAP("yonhap");
    private final String lowercase;
}
