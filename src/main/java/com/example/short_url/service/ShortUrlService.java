package com.example.short_url.service;

import com.example.short_url.domain.Post;
import com.example.short_url.domain.UrlMapping;
import com.example.short_url.dto.ShareShortUrlResponseDto;
import com.example.short_url.repository.PostRepository;
import com.example.short_url.repository.UrlMappingRepository;
import com.example.short_url.util.ShortCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ShortUrlService {

    private final UrlMappingRepository urlMappingRepository;
    private final PostRepository postRepository;
    private final ShortCodeGenerator shortCodeGenerator;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String CACHE_PREFIX = "shorturl:";
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

    /** Redis 도입 전
    @Transactional
    public ResponseEntity<Void> redirectOriginalUrl(String shortUrl) {
        String originalUrl = getOriginalUrl(shortUrl);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, originalUrl)
                .build();
    }
    **/

    /** Redis 도입 후 **/
    @Transactional(readOnly = true)
    public ResponseEntity<Void> redirectOriginalUrl(String shortUrl) {
        String cacheKey = CACHE_PREFIX + shortUrl;

        // 1. Redis 캐시에서 먼저 조회
        String originalUrl = redisTemplate.opsForValue().get(cacheKey);
        if (originalUrl == null) {
            // 2. 캐시에 없으면 DB에서 조회
            originalUrl = urlMappingRepository.findByShortUrl(shortUrl)
                    .map(UrlMapping::getOriginalUrl)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 단축 URL입니다."));

            // 3. Redis 캐시에 저장 (TTL: 10분 예시)
            redisTemplate.opsForValue().set(cacheKey, originalUrl, Duration.ofMinutes(10));
        }

        // 4. Redirect 응답
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
