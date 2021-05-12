package com.devh.micro.news.scheduler;

import com.devh.micro.news.bean.JsoupConnector;
import com.devh.micro.news.constant.MainCategory;
import com.devh.micro.news.constant.Press;
import com.devh.micro.news.constant.SubCategory;
import com.devh.micro.news.entity.News;
import com.devh.micro.news.entity.NewsLastRead;
import com.devh.micro.news.repository.NewsLastReadRepository;
import com.devh.micro.news.repository.NewsRepository;
import com.devh.micro.news.service.NewsService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class YonhapNewsSchedulerTests {
    private final Logger logger = LoggerFactory.getLogger(YonhapNewsSchedulerTests.class);

    private final SimpleDateFormat PUB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private final String LIST_SELECTOR = "#container > div > div > div.section01 > section > div.list-type038 > ul";
    private final String PUB_TIME_SELECTOR = "span.txt-time";
    private final String LINK_SELECTOR = "div.news-con > a";
    private final String TITLE_SELECTOR = "div.news-con > a > strong";
    private final String SUMMARY_SELECTOR = "div.news-con > p";
    private int docCount = 0;

    private final Map<String, NewsLastRead> newsLastReadMap = new HashMap<>();
    private final Map<String, NewsLastRead> updateNewsLastReadMap = new HashMap<>();

    @Autowired
    private JsoupConnector jsoupConnector;
    @Autowired
    private NewsLastReadRepository newsLastReadRepository;
    @Autowired
    private NewsService newsService;

    @Test
    public void test() {
        final Press press = Press.YONHAP;
        setNewsLastReadMap();

        List<News> indexTargetList = new ArrayList<>();
        for(MainCategory mainCategory : MainCategory.values()) {
            for(SubCategory subCategory : SubCategory.values()) {
                if(!mainCategory.equals(subCategory.getMainCategory()))
                    continue;

                final String url = getURL(mainCategory, subCategory);
                Document doc = jsoupConnector.getDocumentFromURL(url);
                if(doc == null)
                    continue;
                Elements listElements = doc.select(LIST_SELECTOR);
                final String key = generateRowId(press, mainCategory, subCategory);
                final long lastPubMillis = newsLastReadMap.get(key).getPubMillis();
                final String lastArticleId = newsLastReadMap.get(key).getArticleId();
                long maxLastPubMillis = lastPubMillis;
                Set<String> articleIdSet = new HashSet<>();
                for (Element listElement : listElements) {
                    Elements liElements = listElement.select("li > div.item-box01");
                    for(Element item : liElements) {
                        final long pubMillis;
                        final String originalLink;
                        final String title;
                        final String summary;
                        final String articleId;
                        long tmpPubMillis;
                        try {
                            tmpPubMillis = getPubMillis(item.select(PUB_TIME_SELECTOR).text());
                        } catch (ParseException e) {
                            logger.error("PUB_TIME Parse failed. " + url);
                            tmpPubMillis = System.currentTimeMillis();
                        }
                        pubMillis = tmpPubMillis;

                        originalLink = "https:" + item.select(LINK_SELECTOR).attr("href");
                        title = item.select(TITLE_SELECTOR).text();
                        summary = item.select(SUMMARY_SELECTOR).text();
                        articleId = originalLink.substring(originalLink.indexOf("AKR"), originalLink.indexOf("?"));
                        if(lastPubMillis <= pubMillis && articleId.compareTo(lastArticleId) >= 1) {
                            increaseDocCount();

                            News news = News.builder()
                                    .press(press)
                                    .mainCategory(mainCategory)
                                    .subCategory(subCategory)
                                    .articleId(articleId)
                                    .pubMillis(pubMillis)
                                    .originalLink(originalLink)
                                    .title(title)
                                    .summary(summary)
                                    .build();

                            indexTargetList.add(news);
                            if(maxLastPubMillis <= pubMillis)
                                maxLastPubMillis = pubMillis;

                            articleIdSet.add(articleId);
                        }
                    }
                }

                String finalLastArticleId = lastArticleId;
                for(String ai : articleIdSet) {
                    if(ai.compareTo(finalLastArticleId) >= 1)
                        finalLastArticleId = ai;
                }
                final long scheduledMillis = System.currentTimeMillis();
                if(lastPubMillis <= maxLastPubMillis && finalLastArticleId.compareTo(lastArticleId) >= 1)
                    updateNewsLastReadMap.put(key, NewsLastRead.builder().rowId(key).scheduledMillis(scheduledMillis).pubMillis(maxLastPubMillis).articleId(finalLastArticleId).mainCategory(mainCategory).subCategory(subCategory).press(press).build());
            }
        }


        if(indexTargetList.size() > 0)
            newsService.indexAll(indexTargetList);

        List<NewsLastRead> updateNewsLastReadList = new ArrayList<>();

        for(String key : updateNewsLastReadMap.keySet())
            updateNewsLastReadList.add(updateNewsLastReadMap.get(key));

        logger.info("update size : " + updateNewsLastReadList.size());
        if(updateNewsLastReadList.size() > 0)
            newsLastReadRepository.saveAll(updateNewsLastReadList);

        logger.info("count : " + docCount);
    }



    private String getURL(MainCategory mainCategory, SubCategory subCategory) {
        if (MainCategory.LOCAL.equals(subCategory.getMainCategory()) || SubCategory.INT_COREESPONDENTS.equals(subCategory))
            return String.format("https://www.yna.co.kr/%s/%s/", mainCategory.getUrl(), subCategory.getUrl());
        else
            return String.format("https://www.yna.co.kr/%s/%s", mainCategory.getUrl(), subCategory.getUrl());
    }

    private String generateRowId(Press press, MainCategory mainCategory, SubCategory subCategory) {
        return press+"-"+mainCategory+"-"+subCategory;
    }

    private long getPubMillis(String pubTime) throws ParseException {
        return PUB_DATE_FORMAT.parse(Calendar.getInstance().get(Calendar.YEAR) + "-" + pubTime).getTime();
    }

    private void increaseDocCount() {
        ++docCount;
    }

    private void setNewsLastReadMap() {
        for(NewsLastRead newsLastRead : newsLastReadRepository.findAll())
            newsLastReadMap.put(generateRowId(newsLastRead.getPress(), newsLastRead.getMainCategory(), newsLastRead.getSubCategory()), newsLastRead);
    }



//     listElements.stream().findAny().ifPresent(element -> element.select("li").stream().findAny().ifPresentOrElse(liElement -> {
//                    final long pubMillis;
//                    final String originalLink;
//                    final String title;
//                    final String summary;
//                    final String articleId;
//                    long tmpPubMillis;
//                    try {
//                        tmpPubMillis = getPubMillis(liElement.select(PUB_TIME_SELECTOR).text());
//                    } catch (ParseException e) {
//                        logger.error("PUB_TIME Parse failed. " + url);
//                        tmpPubMillis = System.currentTimeMillis();
//                    }
//                    pubMillis = tmpPubMillis;
//
//                    originalLink = "https:" + liElement.select(LINK_SELECTOR).attr("href");
//                    title = liElement.select(TITLE_SELECTOR).text();
//                    summary = liElement.select(SUMMARY_SELECTOR).text();
//                    articleId = originalLink.substring(originalLink.indexOf("AKR"), originalLink.indexOf("?"));
//                    if(maxLastPubMillis[0] <= pubMillis && articleId.compareTo(lastArticleId) >= 1) {
//                        increaseDocCount();
//
//                        News news = News.builder()
//                                .press(press)
//                                .mainCategory(mainCategory)
//                                .subCategory(subCategory)
//                                .articleId(articleId)
//                                .pubMillis(pubMillis)
//                                .originalLink(originalLink)
//                                .title(title)
//                                .summary(summary)
//                                .build();
//
//                        logger.info(news.getMainCategory().getKorean() + " " + news.getSubCategory().getKorean() + " " + news.getTitle());
//
//                        if(maxLastPubMillis[0] <= pubMillis)
//                            maxLastPubMillis[0] = pubMillis;
//
//                        articleIdSet.add(articleId);
//                    }
//                }, () -> {}));
}
