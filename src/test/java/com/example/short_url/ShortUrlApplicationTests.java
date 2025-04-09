package com.example.short_url;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // 테스트 코드 실행 시 test.yml 파일 사용
class ShortUrlApplicationTests {

	@Test
	void contextLoads() {
	}

}
