package com.filippov.newsdownloader.repo;

import com.filippov.newsdownloader.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, String> {

    List<Article> findAll();

    Optional<Article> findById(String id);

    List<Article> findAllByNewsSite(String newsSite);

}
