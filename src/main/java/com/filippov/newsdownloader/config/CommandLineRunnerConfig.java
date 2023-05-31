package com.filippov.newsdownloader.config;

import com.filippov.newsdownloader.service.ArticleBufferExecutor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandLineRunnerConfig {
    private final ArticleBufferExecutor articleBufferExecutor;

    public CommandLineRunnerConfig(ArticleBufferExecutor articleBufferExecutor) {
        this.articleBufferExecutor = articleBufferExecutor;
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> articleBufferExecutor.executeArticleDownload();
    }
}
