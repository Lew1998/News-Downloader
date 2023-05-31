package com.filippov.newsdownloader.service;

import com.filippov.newsdownloader.model.BlackListWord;
import com.filippov.newsdownloader.repo.BlackListWordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlackListWordService {
    private final BlackListWordRepository blackListWordRepository;

    @Autowired
    public BlackListWordService(BlackListWordRepository blackListWordRepository) {
        this.blackListWordRepository = blackListWordRepository;
    }

    public boolean isBlacklisted(String word) {
        List<BlackListWord> blackListWords = blackListWordRepository.findAll();
        for (BlackListWord blackListWord : blackListWords) {
            if (blackListWord.getWord().equalsIgnoreCase(word)) {
                return true;
            }
        }
        return false;
    }
}
