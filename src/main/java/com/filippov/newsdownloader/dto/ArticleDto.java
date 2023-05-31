package com.filippov.newsdownloader.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ArticleDto {
    private String id;
    private String title;
    private String url;
    @JsonProperty("imageUrl")
    private String imageUrl;
    private String newsSite;
    private String summary;
    private LocalDate publishedAt;
    private String updatedAt;
    private boolean featured;
    private List<Object> launches;
    private List<Object> events;
}