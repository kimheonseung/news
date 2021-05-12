package com.devh.micro.news.bean;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * <pre>
 * Description :
 *     News Entity 인덱스명 생성을 위한 Bean
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
@Setter
@Component
public class NewsIndexNameProvider {
    private String suffix;
}
