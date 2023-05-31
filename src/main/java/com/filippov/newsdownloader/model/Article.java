package com.filippov.newsdownloader.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "ARTICLES")
@Schema(description = "Информация о статье")
public class Article {
    @Id
    @Schema(description = "Индификатор статьи")
    private String id;

    @Schema(description = "Заголовок")
    private String title;

    @Schema(description = "Название новостного сайта")
    private String newsSite;

    @Schema(description = "Дата публикации")
    private LocalDate publishedDate;

    @Column(length = 1000, columnDefinition = "TEXT")
    @Schema(description = "Содержание сайта")
    private String article;
}
