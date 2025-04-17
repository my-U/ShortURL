package com.example.short_url.service;

import com.example.short_url.domain.Post;
import com.example.short_url.domain.UrlMapping;
import com.example.short_url.dto.ShareShortUrlResponseDto;
import com.example.short_url.repository.PostRepository;
import com.example.short_url.repository.UrlMappingRepository;
import com.example.short_url.util.ShortCodeGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class ShortUrlService {

    private final UrlMappingRepository urlMappingRepository;
    private final PostRepository postRepository;
    private final ShortCodeGenerator shortCodeGenerator;

    public void generatePost() {
        Post post = new Post();
        postRepository.save(post);
    }

    @Transactional
    public ShareShortUrlResponseDto shareShortUrl(Long postId) {
        String shortUrl;

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        if (urlMappingRepository.existsByPost(post)) {
            shortUrl = getShortUrl(post);
        } else {
            String newShortCode = shortCodeGenerator.generateShortCode();

            UrlMapping mapping = UrlMapping.builder()
                    .shortUrl(newShortCode)
                    .originalUrl("https://www.google.com") // 필요 시 원본 URL도 저장
                    .post(post)
                    .build();

            urlMappingRepository.save(mapping);
            shortUrl = newShortCode;
        }

        String fullShortUrl = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/s/")
                .path(shortUrl)
                .toUriString();

        return ShareShortUrlResponseDto.builder()
                .shortUrl(fullShortUrl)
                .build();
    }

    @Transactional
    public ResponseEntity<Void> redirectOriginalUrl(String shortUrl) {
        String originalUrl = getOriginalUrl(shortUrl);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, originalUrl)
                .build();
    }

    public String getShortUrl(Post post) {
        return urlMappingRepository.findByPost(post)
                .map(UrlMapping::getShortUrl)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
    }

    public String getOriginalUrl(String shortUrl) {
        return urlMappingRepository.findByShortUrl(shortUrl)
                .map(UrlMapping::getOriginalUrl)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 short URL입니다: " + shortUrl));
    }

}
