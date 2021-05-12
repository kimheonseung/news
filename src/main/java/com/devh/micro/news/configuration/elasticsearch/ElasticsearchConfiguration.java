package com.devh.micro.news.configuration.elasticsearch;

import com.devh.micro.news.constant.MainCategory;
import com.devh.micro.news.constant.Press;
import com.devh.micro.news.constant.SubCategory;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchCustomConversions;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.util.Arrays;

/**
 * <pre>
 * Description :
 *     Elasticsearch 관련 설정 클래스
 * ===============================================
 * Member fields :
 *
 * ===============================================
 *
 * Author : HeonSeung Kim
 * Date   : 2021-03-11
 * </pre>
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = {"com.devh.micro.news.repository"})
public class ElasticsearchConfiguration extends AbstractElasticsearchConfiguration {
    @Value("${elasticsearch.ip}")
    private String IP;
    @Value("${elasticsearch.port}")
    private String PORT;

    @Override
    @Bean
    public RestHighLevelClient elasticsearchClient() {
        final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(IP + ":" + PORT).build();
        return RestClients.create(clientConfiguration).rest();
    }

    /**
     * <pre>
     * Description
     *     News Entity에서 사용하는 enum에 대한 converter 정의
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
    @Override
    public ElasticsearchCustomConversions elasticsearchCustomConversions() {
        return
                new ElasticsearchCustomConversions(Arrays.asList(
                        new MainCategoryToStringConverter(), new StringToMainCategoryConverter(),
                        new SubCategoryToStringConverter(), new StringToSubCategoryConverter(),
                        new PressToStringConverter(), new StringToPressConverter()
                ));
    }


    @WritingConverter
    static class MainCategoryToStringConverter implements Converter<MainCategory, String> {
        @Override
        public String convert(MainCategory source) {
            return source.getCamelCase();
        }
    }

    @ReadingConverter
    static class StringToMainCategoryConverter implements Converter<String, MainCategory> {
        @Override
        public MainCategory convert(String source) {
            for(MainCategory mainCategory : MainCategory.values()) {
                if(mainCategory.getCamelCase().equals(source))
                    return mainCategory;
            }
            return null;
        }
    }

    @WritingConverter
    static class SubCategoryToStringConverter implements Converter<SubCategory, String> {
        @Override
        public String convert(SubCategory source) {
            return source.getCamelCase();
        }
    }

    @ReadingConverter
    static class StringToSubCategoryConverter implements Converter<String, SubCategory> {
        @Override
        public SubCategory convert(String source) {
            for(SubCategory subCategory : SubCategory.values()) {
                if(subCategory.getCamelCase().equals(source))
                    return subCategory;
            }
            return null;
        }
    }

    @WritingConverter
    static class PressToStringConverter implements Converter<Press, String> {
        @Override
        public String convert(Press source) {
            return source.getLowercase();
        }
    }

    @ReadingConverter
    static class StringToPressConverter implements Converter<String, Press> {
        @Override
        public Press convert(String source) {
            for(Press press : Press.values()) {
                if(press.getLowercase().equals(source))
                    return press;
            }
            return null;
        }
    }
}
