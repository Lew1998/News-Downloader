package com.filippov.newsdownloader.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Schema(description = "Черный список слов")
public class BlackListWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Индефикатор слова")
    private Long id;

    @Column(unique = true)
    @Schema(description = "Слово из черного списка")
    private String word;
}
