package com.filippov.newsdownloader.service;

import com.filippov.newsdownloader.dto.ArticleDto;
import com.filippov.newsdownloader.model.Article;
import com.filippov.newsdownloader.repo.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class ArticleBufferExecutor {

    @Value("${buffer.limit}")
    private int bufferLimit;

    @Value("${thread.pool.size}")
    private int threadPoolSize;

    @Value("${thread.pool.limitPerThread}")
    private int limitPerThread;

    @Value("${common.count.download.article}")
    private int maxCountDownloadArticle;

    private static final ConcurrentHashMap<String, List<ArticleDto>> buffer = new ConcurrentHashMap<>();

    private final WebClientArticlesService articlesService;
    private final ArticleRepository articleRepository;
    private final BlackListWordService blackListWordService;

    @Autowired
    public ArticleBufferExecutor(WebClientArticlesService articlesService, ArticleRepository articleRepository, BlackListWordService blackListWordService) {
        this.articlesService = articlesService;
        this.articleRepository = articleRepository;
        this.blackListWordService = blackListWordService;
    }


    public void executeArticleDownload() {
        // число итераций
        int countIteration = maxCountDownloadArticle / limitPerThread;
        if (maxCountDownloadArticle % limitPerThread != 0) {
            countIteration++;
        }

        // Счетчик скачанных статей
        AtomicInteger downloadedArticlesCount = new AtomicInteger();

        try (ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize)) {
            for (int i = 0; i < countIteration; i++) {
                int startIndex = i * limitPerThread;
                int endIndex = Math.min(startIndex + limitPerThread, maxCountDownloadArticle);

                executorService.execute(() -> {
                    List<ArticleDto> articlesDtoList = articlesService.getArticlesWithPagination(endIndex - startIndex, startIndex);
                    // Фильтрация статей по черному списку
                    articlesDtoList = filterArticlesByBlacklist(articlesDtoList);
                    // Сортировка статей по дате публикации
                    articlesDtoList.sort(Comparator.comparing(ArticleDto::getPublishedAt));
                    // Группировка статей по названию новостного сайта
                    Map<String, List<ArticleDto>> groupedArticles = groupArticlesByNewsSite(articlesDtoList);

                    // Обновление буфера
                    for (Map.Entry<String, List<ArticleDto>> entry : groupedArticles.entrySet()) {
                        String newsSite = entry.getKey();
                        List<ArticleDto> articles = entry.getValue();

                        buffer.computeIfAbsent(newsSite, key -> new ArrayList<>()).addAll(articles);

                        downloadedArticlesCount.getAndAdd(articles.size());

                        if (downloadedArticlesCount.get() >= maxCountDownloadArticle) {
                            buffer.forEach(this::processArticles);
                            buffer.clear();
                            executorService.shutdown();
                        }
                    }
                });
            }
        }
    }


    private List<ArticleDto> filterArticlesByBlacklist(List<ArticleDto> articleDtoList) {
        return articleDtoList.stream()
                .filter(article -> !containsBlacklistedWords(article.getTitle()))
                .collect(Collectors.toList());
    }

    private boolean containsBlacklistedWords(String title) {
        return blackListWordService.isBlacklisted(title);
    }

    private Map<String, List<ArticleDto>> groupArticlesByNewsSite(List<ArticleDto> articleDtoList) {
        return articleDtoList.stream()
                .collect(Collectors.groupingBy(ArticleDto::getNewsSite));
    }

    private void processArticles(String newsSite, List<ArticleDto> articles) {
        // Скачивание содержимого статей, помещение информации в таблицу и удаление записей из буфера
        for (ArticleDto articleDto : articles) {
            String articleContent = downloadArticleContent(articleDto.getUrl());
            saveArticleContent(articleDto, articleContent);
        }
        buffer.remove(newsSite);
    }

    private String downloadArticleContent(String url) {
        // логика скачивания содержимого статьи по заданному URL.
        // Возвращаемая строка должна содержать содержимое статьи.

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void saveArticleContent(ArticleDto articleDto, String content) {
        Article article = new Article();
        article.setId(articleDto.getId());
        article.setTitle(articleDto.getTitle());
        article.setNewsSite(articleDto.getNewsSite());
        article.setPublishedDate(articleDto.getPublishedAt());
        article.setArticle(content);

        articleRepository.save(article);
    }
}