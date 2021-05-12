package com.devh.micro.news.scheduler;

import com.devh.micro.news.bean.JsoupConnector;
import com.devh.micro.news.constant.MainCategory;
import com.devh.micro.news.constant.Press;
import com.devh.micro.news.constant.SubCategory;
import com.devh.micro.news.entity.News;
import com.devh.micro.news.entity.NewsLastRead;
import com.devh.micro.news.repository.NewsLastReadRepository;
import com.devh.micro.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <pre>
 * Description : 
 *     뉴스 정보를 수집하는 스케줄러
 * ===============================================
 * Member fields : 
 *     
 * ===============================================
 * 
 * Author : HeonSeung Kim
 * Date   : 2021-05-11
 * </pre>
 */
@Component
@RequiredArgsConstructor
public class YonhapNewsScheduler {
    /* DI */
    private final JsoupConnector jsoupConnector;
    private final NewsLastReadRepository newsLastReadRepository;
    private final NewsService newsService;

    /* 스케줄 스레드 */
    private Thread mScheduleThread;

    /* 매 10분 마다 스케쥴 실행 */
//    @Scheduled(cron = "0 */10 * * * *")
    public void scheduleStart() {
        if(mScheduleThread == null || !mScheduleThread.isAlive()) {
            mScheduleThread = new Thread(new YonhapNewsSchedulerThread());
            mScheduleThread.start();
        }
    }

    private class YonhapNewsSchedulerThread implements Runnable {

        private final Logger logger = LoggerFactory.getLogger(YonhapNewsSchedulerThread.class);
        private final SimpleDateFormat PUB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        /* 마지막으로 읽은 뉴스에 대한 정보를 담는 맵 */
        private final Map<String, NewsLastRead> newsLastReadMap = new HashMap<>();
        /* 스케쥴 한 사이클 이후 마지막으로 읽은 뉴스에 대한 최종 정보를 담는 맵 */
        private final Map<String, NewsLastRead> updateNewsLastReadMap = new HashMap<>();

        @Override
        public void run() {
            /* 크롤링에 필요한 셀렉터 선언 */
            final String LIST_SELECTOR = "#container > div > div > div.section01 > section > div.list-type038 > ul";
            final String PUB_TIME_SELECTOR = "span.txt-time";
            final String LINK_SELECTOR = "div.news-con > a";
            final String TITLE_SELECTOR = "div.news-con > a > strong";
            final String SUMMARY_SELECTOR = "div.news-con > p";

            final Press press = Press.YONHAP;
            /* 언론사-카테도리별 최신 읽은 상황에 대한 정보 세팅 */
            setNewsLastReadMap();

            /* 검색엔진에 색인할 엔티티 리스트 */
            List<News> indexTargetList = new ArrayList<>();

            for(MainCategory mainCategory : MainCategory.values()) {
                for(SubCategory subCategory : SubCategory.values()) {
                    /* 서브 카테고리가 메인카테고리의 하위 소속이 아니라면 스킵 */
                    if(!mainCategory.equals(subCategory.getMainCategory()))
                        continue;

                    final String url = getURL(mainCategory, subCategory);
                    Document doc = jsoupConnector.getDocumentFromURL(url);
                    /* url로부터 파싱할 문서를 가져오지 못하면 스킵 */
                    if(doc == null)
                        continue;

                    /* news div 카드들에 대한 elements */
                    Elements listElements = doc.select(LIST_SELECTOR);

                    /* 최신 읽은 상황에 대한 키값 */
                    final String key = generateRowId(press, mainCategory, subCategory);

                    /* 기록된 마지막 송고시간 기준 */
                    final long lastPubMillis = newsLastReadMap.get(key).getPubMillis();
                    /* 기록된 마지막 뉴스 ID 기준 */
                    final String lastArticleId = newsLastReadMap.get(key).getArticleId();

                    /* 해당 카테고리에서 마지막으로 읽은 뉴스의 송고시간 */
                    long maxLastPubMillis = lastPubMillis;
                    /* 해당 카테고리에서 마지막으로 읽은 뉴스의 뉴스 ID */
                    Set<String> articleIdSet = new HashSet<>();

                    for (Element listElement : listElements) {
                        /* 단일 뉴스 카드의 elements */
                        Elements liElements = listElement.select("li > div.item-box01");

                        /* 단일 뉴스 카드 */
                        for(Element item : liElements) {
                            /* 색인할 데이터 선언 */
                            final long pubMillis;
                            final String originalLink;
                            final String title;
                            final String summary;
                            final String articleId;
                            long tmpPubMillis;
                            try {
                                tmpPubMillis = getPubMillis(item.select(PUB_TIME_SELECTOR).text());
                            } catch (ParseException e) {
                                /* 송고시간 파싱 실패시 현재시간으로 대체 */
                                logger.error("PUB_TIME Parse failed. " + url);
                                tmpPubMillis = System.currentTimeMillis();
                            }
                            pubMillis = tmpPubMillis;

                            originalLink = "https:" + item.select(LINK_SELECTOR).attr("href");
                            title = item.select(TITLE_SELECTOR).text();
                            summary = item.select(SUMMARY_SELECTOR).text();
                            articleId = originalLink.substring(originalLink.indexOf("AKR"), originalLink.indexOf("?"));
                            
                            /* 마지막으로 읽은 뉴스의 송고시간 이후의 뉴스 ID가 큰 값만 색인 */
                            if(lastPubMillis <= pubMillis && articleId.compareTo(lastArticleId) >= 1) {
                                /* 색인할 엔티티 객체 생성 */
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

                                /* 색인할 엔티티 리스트에 추가 */
                                indexTargetList.add(news);
                                /* 현재 읽고있는 뉴스의 송고시간으로 최신 시간 값을 갱신 */
                                if(maxLastPubMillis <= pubMillis)
                                    maxLastPubMillis = pubMillis;
                                /* 현재 읽고있는 뉴스의 뉴스 ID로 최신 값을 갱신하기 위해 컬렉션에 추가 */
                                articleIdSet.add(articleId);
                            }
                        }
                    }

                    /* 최종 뉴스 ID 값 골라내기 */
                    String finalLastArticleId = lastArticleId;
                    for(String ai : articleIdSet) {
                        if(ai.compareTo(finalLastArticleId) >= 1)
                            finalLastArticleId = ai;
                    }
                    /* 현재 스케쥴러가 돈 시간을 셋팅 */
                    final long scheduledMillis = System.currentTimeMillis();
                    
                    /* 최종 읽은 뉴스의 송고시간과 뉴스 ID가 갱신이 필요하다면 갱신 대상에 추가 */
                    if(lastPubMillis <= maxLastPubMillis && finalLastArticleId.compareTo(lastArticleId) >= 1)
                        updateNewsLastReadMap.put(key, NewsLastRead.builder().rowId(key).scheduledMillis(scheduledMillis).pubMillis(maxLastPubMillis).articleId(finalLastArticleId).mainCategory(mainCategory).subCategory(subCategory).press(press).build());
                }
            }

            /* 엔티티 리스트 색인 */
            if(indexTargetList.size() > 0)
                newsService.indexAll(indexTargetList);

            /* 갱신할 최종 읽은 뉴스에 대한 정보 리스트 */
            List<NewsLastRead> updateNewsLastReadList = new ArrayList<>();

            for(String key : updateNewsLastReadMap.keySet())
                updateNewsLastReadList.add(updateNewsLastReadMap.get(key));

            logger.info("update size : " + updateNewsLastReadList.size());
            /* 최종 읽은 뉴스에 대한 정보 업데이트 */
            if(updateNewsLastReadList.size() > 0)
                newsLastReadRepository.saveAll(updateNewsLastReadList);
        }

        /**
         * <pre>
         * Description
         *     카테고리별 URL 생성
         * ===============================================
         * Parameters
         *     MainCategory mainCategory
         *     SubCategory subCategory
         * Returns
         *     String - url
         * Throws
         *
         * ===============================================
         *
         * Author : HeonSeung Kim
         * Date   : 2021-05-12
         * </pre>
         */
        private String getURL(MainCategory mainCategory, SubCategory subCategory) {
            if (MainCategory.LOCAL.equals(subCategory.getMainCategory()) || SubCategory.INT_COREESPONDENTS.equals(subCategory))
                return String.format("https://www.yna.co.kr/%s/%s/", mainCategory.getUrl(), subCategory.getUrl());
            else
                return String.format("https://www.yna.co.kr/%s/%s", mainCategory.getUrl(), subCategory.getUrl());
        }

        /**
         * <pre>
         * Description
         *     언론사와 카테고리로 마지막으로 읽은 뉴스 상황에 대한 기록을 위해 고유 ID 생성
         * ===============================================
         * Parameters
         *     Press press
         *     MainCategory mainCategory
         *     SubCategory subCategory
         * Returns
         *     String
         * Throws
         *
         * ===============================================
         *
         * Author : HeonSeung Kim
         * Date   : 2021-05-12
         * </pre>
         */
        private String generateRowId(Press press, MainCategory mainCategory, SubCategory subCategory) {
            return press+"-"+mainCategory+"-"+subCategory;
        }

        /**
         * <pre>
         * Description
         *     송고시간을 밀리세컨으로 반환
         * ===============================================
         * Parameters
         *     String pubTime - 송고시간
         * Returns
         *     long - 변환된 밀리세컨
         * Throws
         *     throws ParseException
         * ===============================================
         *
         * Author : HeonSeung Kim
         * Date   : 2021-05-12
         * </pre>
         */
        private long getPubMillis(String pubTime) throws ParseException {
            return PUB_DATE_FORMAT.parse(Calendar.getInstance().get(Calendar.YEAR) + "-" + pubTime).getTime();
        }

        /**
         * <pre>
         * Description
         *     마지막으로 읽은 뉴스 상황에 대한 정보를 맵에 저장
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
         * Date   : 2021-05-12
         * </pre>
         */
        private void setNewsLastReadMap() {
            for(NewsLastRead newsLastRead : newsLastReadRepository.findAll())
                newsLastReadMap.put(generateRowId(newsLastRead.getPress(), newsLastRead.getMainCategory(), newsLastRead.getSubCategory()), newsLastRead);
        }
    }
}
