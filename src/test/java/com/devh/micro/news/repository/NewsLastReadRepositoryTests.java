package com.devh.micro.news.repository;

import com.devh.micro.news.constant.MainCategory;
import com.devh.micro.news.constant.Press;
import com.devh.micro.news.constant.SubCategory;
import com.devh.micro.news.entity.NewsLastRead;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NewsLastReadRepositoryTests {
    private final Logger logger = LoggerFactory.getLogger(NewsLastReadRepositoryTests.class);
    @Autowired
    private NewsLastReadRepository newsLastReadRepository;

    @Test
    public void get() {
        logger.info("===========================================================================");
        logger.info("Test get start...");
        List<NewsLastRead> newsLastReadList = newsLastReadRepository.findAll();
        logger.info("newsLastReadList size : " + newsLastReadList.size());
        for(NewsLastRead newsLastRead : newsLastReadList)
            logger.info(newsLastRead.toString());

        logger.info("Test get end...");
        logger.info("===========================================================================");
    }

    @Test
    public void getOne() {
        logger.info("===========================================================================");
        logger.info("Test getOne start...");
        Press press = Press.YONHAP;
        MainCategory mainCategory = MainCategory.CULTURE;
        SubCategory subCategory = SubCategory.CUL_MEDIA;
        Optional<NewsLastRead> optionalNewsLastRead = newsLastReadRepository.findById(generateRowId(press, mainCategory, subCategory));
        optionalNewsLastRead.ifPresentOrElse(
                newsLastRead -> logger.info("newsLastRead : " + newsLastRead.toString()),
                () -> logger.warn("Cannot find newsLastRead Data.")
        );
        logger.info("Test getOne end...");
        logger.info("===========================================================================");
    }

    @Test
    public void save() {
        logger.info("===========================================================================");
        logger.info("Test save start...");
        Press press = Press.YONHAP;
        MainCategory mainCategory = MainCategory.CULTURE;
        SubCategory subCategory = SubCategory.CUL_BOOKS;
        NewsLastRead newsLastRead = NewsLastRead.builder()
                .rowId(generateRowId(press, mainCategory, subCategory))
                .press(press)
                .articleId("AKR2021050610440211")
                .mainCategory(mainCategory)
                .subCategory(subCategory)
                .pubMillis(System.currentTimeMillis())
                .scheduledMillis(System.currentTimeMillis() - 1000L)
                .build();
        newsLastReadRepository.save(newsLastRead);
        logger.info("Success to save " + newsLastRead.toString());
        logger.info("Test save end...");
        logger.info("===========================================================================");
    }

    @Test
    public void saveDefault() {
        Press press = Press.YONHAP;
        List<NewsLastRead> newsLastReadList = new ArrayList<>();
        for(MainCategory mainCategory : MainCategory.values()) {
            for(SubCategory subCategory : SubCategory.values()) {
                if(mainCategory.equals(subCategory.getMainCategory())) {
                    NewsLastRead newsLastRead = NewsLastRead.builder()
                            .rowId(generateRowId(press, mainCategory, subCategory))
                            .press(press)
                            .articleId("")
                            .mainCategory(mainCategory)
                            .subCategory(subCategory)
                            .pubMillis(0L)
                            .scheduledMillis(0L)
                            .build();
                    newsLastReadList.add(newsLastRead);
                }
            }
        }
        newsLastReadRepository.saveAll(newsLastReadList);

    }

    @Test
    public void test() {
        logger.info(String.valueOf("AKR20210510001500094".compareTo("AKR20210510001500099")));
    }

    private String generateRowId(Press press, MainCategory mainCategory, SubCategory subCategory) {
        return press+"-"+mainCategory+"-"+subCategory;
    }

}
