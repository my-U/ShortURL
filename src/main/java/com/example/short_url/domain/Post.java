package com.example.short_url.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "post", schema = "${schema.base}")
public class Post {

    @Id
    @Column(name = "id_post", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPost;
}
