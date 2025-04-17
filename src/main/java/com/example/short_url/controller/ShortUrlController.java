package com.example.short_url.controller;

import com.example.short_url.dto.ShareShortUrlResponseDto;
import com.example.short_url.service.ShortUrlService;
import com.example.short_url.util.ResponseUtil;
import com.example.short_url.util.enums.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class ShortUrlController {
    private final ShortUrlService shortUrlService;

    @PostMapping("generate/post")
    public ResponseEntity<?> generatePost() {
        shortUrlService.generatePost();
        return ResponseUtil.createSuccessResponse(SuccessCode.INSERT_SUCCESS);
    }

    @PostMapping("share/{postId}/short-url")
    public ResponseEntity<?> shareShortUrl(@PathVariable Long postId) {
        ShareShortUrlResponseDto shareShortUrlResponseDto = shortUrlService.shareShortUrl(postId);
        return ResponseUtil.createSuccessResponse(SuccessCode.SELECT_SUCCESS, shareShortUrlResponseDto);
    }

    @GetMapping("s/{shortUrl}")
    public ResponseEntity<?> redirectOriginalUrl(@PathVariable String shortUrl) {
        return shortUrlService.redirectOriginalUrl(shortUrl);
    }
}
