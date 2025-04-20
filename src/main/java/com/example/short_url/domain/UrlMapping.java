package com.example.short_url.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "url_mapping", schema = "${schema.base}")
public class UrlMapping {

    @Id
    @Column(name = "id_url_mapping", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUrlMapping;

    @OneToOne
    @JoinColumn(name = "post_id", nullable = false, unique = true)
    private Post post;

    @Column(name = "short_url", nullable = false, unique = true)
    private String shortUrl;

    @Column(name = "original_url", nullable = false)
    private String originalUrl;

    @Builder.Default
    @Column(name = "reg_date_url_mapping", nullable = false, updatable = false)
    private LocalDateTime regDateUrlMapping = LocalDateTime.now();

}
