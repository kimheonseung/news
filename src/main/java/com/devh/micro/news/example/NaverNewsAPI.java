package com.devh.micro.news.example;

import com.devh.micro.news.constant.NewsIndex;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class NaverNewsAPI {
    private final String HEADER_ID = "X-Naver-Client-Id";
    private final String HEADER_SECRET = "X-Naver-Client-Secret";
    private final String ID = "";
    private final String SECRET = "";

    private final String DEFAULT_URL = "https://openapi.naver.com/v1/search/news.json";
    private final Integer MAX_DISP = 100;
    private final String METHOD_GET = "GET";

    private enum RequestParam {

        QUERY("query"),            /* String  !필수 검색을 원하는 문자열로서 UTF-8로 인코딩한다. */
        DISPLAY("display"),        /* Integer 검색 결과 출력 건수 지정 (기본 10, 최대 100) */
        START("start"),            /* Integer 검색 시작 위치로 최대 1000까지 가능 (기본 1, 최대 1000) */
        SORT("sort");              /* String 정렬 옵션: sim (유사도순), date (날짜순) - 기본 date */
        private String key;
        private RequestParam(String key) {
            this.key = key;
        }
        public String getKey() {
            return this.key;
        }
    }

    private enum ResponseParam {
        RSS("rss"),                            /* 디버그를 쉽게 하고 RSS 리더기만으로 이용할 수 있게 하기 위해 만든 RSS 포맷의 컨테이너이며 그 외의 특별한 의미는 없다. */
        CHANEL("channel"),                     /* 검색 결과를 포함하는 컨테이너이다. 이 안에 있는 title, link, description 등의 항목은 참고용으로 무시해도 무방하다. */
        LAST_BUILD_DATE("lastBuildDate"),      /* datetime 검색 결과를 생성한 시간이다. */
        TOTAL("total"),                        /* Integer 검색 결과 문서의 총 개수를 의미한다. */
        START("start"),                        /* Integer 검색 결과 문서 중, 문서의 시작점을 의미한다. */
        DISPLAY("display"),                    /* Integer 검색된 검색 결과의 개수이다. */
        ITEMS("items"),                        /* XML 포멧에서는 item 태그로, JSON 포멧에서는 items 속성으로 표현된다. 개별 검색 결과이며 title, originallink, link, description, pubDate를 포함한다. */
        TITLE("title"),                        /* String 개별 검색 결과이며, title, originallink, link, description, pubDate 를 포함한다. */
        ORIGINAL_LINK("originallink"),         /* String 검색 결과 문서의 제공 언론사 하이퍼텍스트 link를 나타낸다. */
        LINK("link"),                          /* String 검색 결과 문서의 제공 네이버 하이퍼텍스트 link를 나타낸다. */
        DESC("description"),                   /* String 검색 결과 문서의 내용을 요약한 패시지 정보이다. 문서 전체의 내용은 link를 따라가면 읽을 수 있다. 패시지에서 검색어와 일치하는 부분은 태그로 감싸져 있다. */
        PUB_DATE("pubDate");                   /* datetime 검색 결과 문서가 네이버에 제공된 시간이다. */
        private String key;
        private ResponseParam(String key) {
            this.key = key;
        }
        public String getKey() {
            return this.key;
        }
    }

    private enum ErrorCode {
        SE01("SE01", 400, "Incorrect query request (잘못된 쿼리요청입니다.)", "검색 API 요청에 오류가 있습니다. 요청 URL, 필수 요청 변수가 정확한지 확인 바랍니다."),
        SE02("SE02", 400, "Invalid display value (부적절한 display 값입니다.)", "display 요청 변수값이 허용 범위(1~100)인지 확인해 보세요."),
        SE03("SE03", 400, "Invalid start value (부적절한 start 값입니다.)", "start 요청 변수값이 허용 범위(1~1000)인지 확인해 보세요."),
        SE04("SE04", 400, "Invalid sort value (부적절한 sort 값입니다.)", "sort 요청 변수 값에 오타가 없는지 확인해 보세요."),
        SE05("SE05", 404, "Invalid search api (존재하지 않는 검색 api 입니다.)", "검색 API 대상에 오타가 없는지 확인해 보세요."),
        SE06("SE06", 400, "Malformed encoding (잘못된 형식의 인코딩입니다.)", "검색어를 UTF-8로 인코딩하세요."),
        SE99("SE99", 500, "System Error (시스템 에러)", "서버 내부 에러가 발생하였습니다. 포럼에 올려주시면 신속히 조치하겠습니다.");
        private String errorCode;
        private Integer httpCode;
        private String errorMessage;
        private String solutionMessage;
        private ErrorCode(String errorCode, Integer httpCode, String errorMessage, String solutionMessage) {
            this.errorCode = errorCode;
            this.httpCode = httpCode;
            this.errorCode = errorMessage;
            this.solutionMessage = solutionMessage;
        }
        public String getErrorCode() {
            return this.errorCode;
        }
        public Integer getHttpCode() {
            return this.httpCode;
        }
        public String getErrorMessage() {
            return this.errorMessage;
        }
        public String getSolutionMessage() {
            return this.solutionMessage;
        }
    }

    public void callExample() {
        try {
            final Gson gson = new GsonBuilder().setPrettyPrinting().create();
            final String query = String.format("%s=%s", RequestParam.QUERY.getKey(), URLEncoder.encode("[속보]", "UTF-8"));
            final String sort = String.format("%s=%s", RequestParam.SORT.getKey(), "date");

            URL url = new URL(DEFAULT_URL + "?" + query);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod(METHOD_GET);
            httpURLConnection.setRequestProperty(HEADER_ID, ID);
            httpURLConnection.setRequestProperty(HEADER_SECRET, SECRET);

            System.out.println("### request url connection... ");
            System.out.println();

            System.out.println(gson.toJson(getConnectionResult(httpURLConnection)));

        } catch (IOException e1) {
            System.out.println(DEFAULT_URL + " 접속 요청 중 에러 발생. " + e1.getMessage());
        }
    }

    private JSONObject getConnectionResult(HttpURLConnection httpURLConnection) {
        StringBuffer sbResult = new StringBuffer();

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));

            String inputLine;
            while ((inputLine = br.readLine()) != null)
                sbResult.append(inputLine);
            br.close();
        } catch (UnsupportedEncodingException e) {
            System.out.println("결과 반환 스트림 생성 중 에러 발생. " + e.getMessage());
        } catch (IOException e) {
            System.out.println("결과 반환 스트림 생성 중 에러 발생. " + e.getMessage());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JSONObject json = new JSONObject();

        try {
            json.putAll(objectMapper.readValue(sbResult.toString(), Map.class));
        } catch (JsonProcessingException e) {
            System.out.println("JSON 데이터 변환 중 에러 발생. " + e.getMessage());
        }
        return json;
    }


    public static void main(String[] args) {
//        NaverNewsAPI nna = new NaverNewsAPI();
//        nna.callExample();

        System.out.println(NewsIndex.PATTERN.getValue());
    }
}
