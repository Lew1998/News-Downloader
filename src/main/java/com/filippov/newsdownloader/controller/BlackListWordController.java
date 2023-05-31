package com.filippov.newsdownloader.controller;

import com.filippov.newsdownloader.model.BlackListWord;
import com.filippov.newsdownloader.repo.BlackListWordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/blackList")
public class BlackListWordController {
    private final BlackListWordRepository blackListWordRepository;

    @Autowired
    public BlackListWordController(BlackListWordRepository blackListWordRepository) {
        this.blackListWordRepository = blackListWordRepository;
    }

    @GetMapping("/findAll")
    public List<BlackListWord> findBlackList() {
        return blackListWordRepository.findAll();
    }

    @PostMapping("/addWord/{word}")
    public void addWordInList(@PathVariable String word) {
        BlackListWord blackListWord = new BlackListWord();
        blackListWord.setWord(word);
        blackListWordRepository.save(blackListWord);
    }
}
