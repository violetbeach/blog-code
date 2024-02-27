package com.violetbeach.coveredindex;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class Controller {
    private final ArticleRepositoryImpl articleRepository;
    private final ToBeRepository toBeRepository;

    @GetMapping("/join")
    ResponseEntity<Page<ArticleInfo>> join() throws InterruptedException {
        Page<ArticleInfo> articles = articleRepository.findList("JP", PageRequest.of(0, 50));
        return ResponseEntity.ok().body(articles);
    }

    @GetMapping("/split")
    ResponseEntity<Page<ArticleInfo>> split() throws InterruptedException {
        toBeRepository.findList("JP", PageRequest.of(0, 50));
        return ResponseEntity.ok().body(null);
    }
}
