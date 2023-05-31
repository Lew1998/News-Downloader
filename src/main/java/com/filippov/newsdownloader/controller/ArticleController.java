package com.filippov.newsdownloader.controller;

import com.filippov.newsdownloader.dto.ArticleDto;
import com.filippov.newsdownloader.exception.ArticleNotFoundException;
import com.filippov.newsdownloader.model.Article;
import com.filippov.newsdownloader.repo.ArticleRepository;
import com.filippov.newsdownloader.service.ArticleBufferExecutor;
import com.filippov.newsdownloader.service.WebClientArticlesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    private WebClientArticlesService articlesService;
    @Autowired
    ArticleBufferExecutor articleBufferExecutor;

    @Autowired
    ArticleRepository articleRepository;

    @GetMapping
    public List<ArticleDto> findArticle(@RequestParam int limit, @RequestParam int skipped) {
        return articlesService.getArticlesWithPagination(limit, skipped);
    }

    @GetMapping("/execute")
    public void execute() {
        articleBufferExecutor.executeArticleDownload();
    }

    @GetMapping("/findAll")
    public List<Article> findAllArticle() {
        return articleRepository.findAll();
    }

    @GetMapping("/findById/{id}")
    public Article findArticleById(@PathVariable String id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new ArticleNotFoundException("Article not found with id: " + id));
    }


    @GetMapping("/findListArticleByNewsSite/{newsSite}")
    public List<Article> findListArticleByNewsSite(@PathVariable String newsSite) {
        return articleRepository.findAllByNewsSite(newsSite);
    }

}
