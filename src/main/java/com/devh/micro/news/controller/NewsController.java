package com.devh.micro.news.controller;

import com.devh.micro.news.result.ResultToJsonConverter;
import com.devh.micro.news.dto.NewsSearchParamsDTO;
import com.devh.micro.news.service.NewsService;
import com.devh.micro.news.util.ExceptionUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("micro-news")
@RequiredArgsConstructor
public class NewsController {
    private final Logger logger = LoggerFactory.getLogger(NewsController.class);

    /* DI */
    private final NewsService newsService;

    @PostMapping
    @ResponseBody
    public ResponseEntity<Object> getTest(@RequestBody NewsSearchParamsDTO newsSearchParamsDTO) {
        ResponseEntity<Object> result;

        ResultToJsonConverter resultToJsonConverter = ResultToJsonConverter.init();
        logger.info(newsSearchParamsDTO.toString());
        try {
            resultToJsonConverter.putResultMapToResultJson(newsService.search(newsSearchParamsDTO));
            result = new ResponseEntity<>(resultToJsonConverter.get(), HttpStatus.OK);
        } catch (Exception e) {
            ExceptionUtils.getInstance().printErrorLogWithException(logger, e);
            resultToJsonConverter.putResultMapToExceptionInformation(e);
            result = new ResponseEntity<>(resultToJsonConverter.get(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }
}
