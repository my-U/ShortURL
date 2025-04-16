package com.example.short_url.repository;

import com.example.short_url.domain.Post;
import com.example.short_url.domain.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {
    boolean existsByPost(Post post);
    Optional<UrlMapping> findByPost(Post post);
    boolean existsByShortUrl(String shortUrl);
    Optional<UrlMapping> findByShortUrl(String shortUrl);
}
