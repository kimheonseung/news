package com.devh.micro.news.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <pre>
 * Description :
 *     News Entity 서브 카테고리 상수
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
public enum SubCategory {
    POL_PRESIDENT("president", "president", "청와대/총리실", MainCategory.POLITICS),
    POL_NATIONAL_ASSEMBLY("nationalAssembly", "national-assembly", "국회/정당", MainCategory.POLITICS),
    POL_DIPLOMACY("diplomacy", "diplomacy", "외교", MainCategory.POLITICS),
    POL_DEFENSE("defense", "defense", "국방", MainCategory.POLITICS),

    ECO_ECONOMIC_POLICY("economicPolicy", "economic-policy", "경제/정책", MainCategory.ECONOMY),
    ECO_FINANCE("finance", "finance", "금융", MainCategory.ECONOMY),
    ECO_STOCK_MARKET("stockMarket", "stock-market", "증시", MainCategory.ECONOMY),
    ECO_FINANCIAL_TECHNOLOGY("financialTechnology", "financial-technology", "재테크", MainCategory.ECONOMY),
    ECO_REAL_ESTATE("realEstate", "real-estate", "부동산", MainCategory.ECONOMY),
    ECO_JOB_FOUNDATION("jobFoundation", "job-foundation", "취업/창업", MainCategory.ECONOMY),
    ECO_CONSUMER("consumer", "consumer", "소비자", MainCategory.ECONOMY),
    ECO_INTERNATIONAL_ECONOMY("internationalEconomy", "international-economy", "국제경제", MainCategory.ECONOMY),


    IND_INDUSTRIAL_ENTERPRISE("industrialEnterprise", "industrial-enterprise", "산업/기업", MainCategory.INDUSTRY),
    IND_ELECTRONICS("electronics", "electronics", "전기전자", MainCategory.INDUSTRY),
    IND_HEAVY_CHEMISTRY("heavyChemistry", "heavy-chemistry", "중화학", MainCategory.INDUSTRY),
    IND_AUTOMOBILE("automobile", "automobile", "자동차", MainCategory.INDUSTRY),
    IND_CONSTRUCTION("construction", "construction", "건설", MainCategory.INDUSTRY),
    IND_ENERGY_RESOURCE("energyResource", "energy-resource", "에너지/자원", MainCategory.INDUSTRY),
    IND_TECHNOLOGY_SCIENCE("technologyScience", "technology-science", "IT/과학", MainCategory.INDUSTRY),
    IND_DISTRIBUTION_SERVICE_INDUSTRY("distributionServiceIndustry", "distribution-service-industry", "유통/서비스", MainCategory.INDUSTRY),
    IND_VENTURE_BUSINESS("ventureBusiness", "venture-business", "중기/벤처", MainCategory.INDUSTRY),
    IND_BIOINDUSTRY_HEALTH("bioIndustryHealth", "bioindustry-health", "바이오/헬스", MainCategory.INDUSTRY),
    IND_AGRICULTURE("agriculture", "agriculture", "농림축산", MainCategory.INDUSTRY),
    IND_OCEAN_FISHERY("oceanFishery", "ocean-fishery", "해양수산", MainCategory.INDUSTRY),

    SOC_ACCIDENT("accident", "accident", "사건/사고", MainCategory.SOCIETY),
    SOC_COURT_PROSECUTION("courtProsecution", "court-prosecution", "법원/경찰", MainCategory.SOCIETY),
    SOC_EDUCATION("education", "education", "교육", MainCategory.SOCIETY),
    SOC_WELFARE_LABOR("welfareLabor", "welfare-labor", "복지/노동", MainCategory.SOCIETY),
    SOC_ENVIRONMENT("environment", "environment", "환경", MainCategory.SOCIETY),
    SOC_WOMEN_CHILD("womenChild", "women-child", "여성/아동", MainCategory.SOCIETY),
    SOC_OVERSEAS_KOREANS("overseasKoreans", "overseas-koreans", "재외동포", MainCategory.SOCIETY),
    SOC_MULTIPLE_CULTURES("multipleCultures", "multiple-cultures", "다문화", MainCategory.SOCIETY),

    LOC_GYEONGGI("gyeonggi", "gyeonggi", "경기", MainCategory.LOCAL),
    LOC_INCHEON("incheon", "incheon", "인천", MainCategory.LOCAL),
    LOC_BUSAN("busan", "busan", "부산", MainCategory.LOCAL),
    LOC_ULSAN("ulsan", "ulsan", "울산", MainCategory.LOCAL),
    LOC_GYEONGNAM("gyeongnam", "gyeongnam", "경남", MainCategory.LOCAL),
    LOC_DAEGU_GYEONGBUK("daeguGyeongbuk", "daegu-gyeongbuk", "대구/경북", MainCategory.LOCAL),
    LOC_GWANGJU_JEONNAM("gwangjuJeonnam", "gwangju-jeonnam", "광주/전남", MainCategory.LOCAL),
    LOC_JEONBUK("jeonbuk", "jeonbuk", "전북", MainCategory.LOCAL),
    LOC_DAEJEON_CHUNGNAM_SEJONG("daejeonChungnamSejong", "daejeon-chungnam-sejong", "대전/충남/세종", MainCategory.LOCAL),
    LOC_CHUNGBUK("chungbuk", "chungbuk", "충북", MainCategory.LOCAL),
    LOC_GANGWON("gangwon", "gangwon", "강원", MainCategory.LOCAL),
    LOC_JEJU("jeju", "jeju", "제주", MainCategory.LOCAL),

    INT_COREESPONDENTS("correspondents", "correspondents", "특파원", MainCategory.INTERNATIONAL),
    INT_NORTH_AMERICA("northAmerica", "north-america", "미국/북미", MainCategory.INTERNATIONAL),
    INT_CHINA("china", "china", "중국", MainCategory.INTERNATIONAL),
    INT_JAPAN("japan", "japan", "일본", MainCategory.INTERNATIONAL),
    INT_ASIA_AUSTRALIA("asiaAustralia", "asia-australia", "아시아/호주", MainCategory.INTERNATIONAL),
    INT_EUROPE("europe", "europe", "유럽", MainCategory.INTERNATIONAL),
    INT_CENTRALSOUTH_AMERICA("centralsouthAmerica", "centralsouth-america", "중남미", MainCategory.INTERNATIONAL),
    INT_MIDDLEEAST_AFRICA("middleeastAfrica", "middleeast-africa", "중동/아프리카", MainCategory.INTERNATIONAL),
    INT_INTERNATIONAL_ORG("internationalOrg", "international-org", "국제기구", MainCategory.INTERNATIONAL),

    CUL_BOOKS("books", "books", "책/문화", MainCategory.CULTURE),
    CUL_RELIGION("religion", "religion", "종교", MainCategory.CULTURE),
    CUL_PERFORMANCE_EXHIBITION("performanceExhibition", "performance-exhibition", "공연/전시", MainCategory.CULTURE),
    CUL_SCHOLARSHIP("scholarship", "scholarship", "학술/문화재", MainCategory.CULTURE),
    CUL_MEDIA("media", "media", "미디어", MainCategory.CULTURE),

    LIF_LIFE_INFORMATION("lifeInformation", "life-information", "생활", MainCategory.LIFESTYLE),
    LIF_HEALTH("health", "health", "건강", MainCategory.LIFESTYLE),
    LIF_FASHION_BEAUTY("fashionBeauty", "fashion-beauty", "패션/뷰티", MainCategory.LIFESTYLE),
    LIF_LEISURE("leisure", "leisure", "레저", MainCategory.LIFESTYLE),
    LIF_TRAVEL_FESTIVAL("travelFestival", "travel-festival", "여행/축제", MainCategory.LIFESTYLE),

    ENT_TV("tv", "tv", "방송", MainCategory.ENTERTAINMENT),
    ENT_MOVIES("movies", "movies", "영화", MainCategory.ENTERTAINMENT),
    ENT_POP_SONG("popSong", "pop-song", "가요", MainCategory.ENTERTAINMENT),
    ENT_GLOBAL_ENTERTAINMENT("globalEntertainment", "global-entertainment", "해외연예", MainCategory.ENTERTAINMENT),

    SPO_BASEBALL("baseball", "baseball", "야구", MainCategory.SPORTS),
    SPO_FOOTBALL("football", "football", "축구", MainCategory.SPORTS),
    SPO_BASKETBALL_VOLLEYBALL("basketballVolleyball", "basketball-volleyball", "농구/배구", MainCategory.SPORTS),
    SPO_GOLF("golf", "golf", "골프", MainCategory.SPORTS);

    private final String camelCase;
    private final String url;
    private final String korean;
    private final MainCategory mainCategory;
}
