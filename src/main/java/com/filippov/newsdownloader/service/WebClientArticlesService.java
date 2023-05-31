package com.filippov.newsdownloader.service;

import com.filippov.newsdownloader.dto.ArticleDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class WebClientArticlesService {

    @Value("${api.base.url}")
    private String apiUrl;


    public List<ArticleDto> getArticlesWithPagination(int limit, int skipped) {
        WebClient webClient = WebClient.create();

        String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("_limit", limit)
                .queryParam("_start", skipped)
                .toUriString();

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToFlux(ArticleDto.class)
                .collectList()
                .block();
    }

}
