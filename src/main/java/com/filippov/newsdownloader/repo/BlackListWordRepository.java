package com.filippov.newsdownloader.repo;

import com.filippov.newsdownloader.model.BlackListWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlackListWordRepository extends JpaRepository<BlackListWord, Long> {
}
