package com.example.short_url;

import com.example.short_url.domain.Post;
import com.example.short_url.domain.UrlMapping;
import com.example.short_url.dto.ShareShortUrlResponseDto;
import com.example.short_url.repository.PostRepository;
import com.example.short_url.repository.UrlMappingRepository;
import com.example.short_url.service.ShortUrlService;
import com.example.short_url.util.ShortCodeGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // 테스트 코드 실행 시 test.yml 파일 사용
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ShortUrlApplicationTests {

	@Autowired
	private MockMvc mockMvc; // 통합 테스트용. 프로젝트 규모가 커질수록 통합 테스트와 단위 테스트의 분리가 좋음

	/**
	 *  Mockito가 해당 필드를 “가짜(Mock)” 객체로 만들어주는 어노테이션
	 *  가짜 객체 사용 이유 : 내가 테스트하고 싶은 "대상 클래스의 로직만" 독립적으로 검증하기 위함
	 *  단위 테스트 실행 시 가짜 객체가 아닌 실제 Repository 객체를 사용한다면
	 *  DB에 연결된 실제 구현이 실행되고, 데이터가 없거나 충돌이 발생하면 테스트 실패
	 */
	@Autowired
	private UrlMappingRepository urlMappingRepository;
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private ShortCodeGenerator shortCodeGenerator;
	@Autowired
	private ShortUrlService shortUrlService; // 단위 테스트용. 단위 테스트는 일반적으로 @Autowired를 사용하지 않고 직접 생성

	@BeforeEach
	void setUp() {
		/** MockitoAnnotations.openMocks(this); **/ // @Mock를 사용하기 위한 선언. 또는 @ExtendWith(MockitoExtension.class)를 사용해도 됨
		/** mockShortUrlService = new ShortUrlService(mockUrlMappingRepository, mockPostRepository, mockShortCodeGenerator); **/ // 단위 테스트용 service
		// 어떤 값이 인자로 들어오든 상관없이 항상 false를 리턴
		// short URL 중복 체크 로직이 무한 루프에 빠지지 않도록 막기 위해 항상 false 사용
		/** when(mockUrlMappingRepository.existsByShortUrl(any())).thenReturn(false); **/
		// save() 메서드가 호출되면, 전달받은 인자(= 저장할 엔티티)를 그대로 리턴
		// 실제 JPA Repository는 .save() 호출 시 DB에 저장하고, 저장된 엔티티 객체를 리턴
		// mock 객체는 DB와 연결되어 있지 않기 때문에 아무 값도 리턴하지 않으면 null 리턴 -> NullPointerException  오류 발생
		/** when(mockUrlMappingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0)); **/
	}

	@Test
	@DisplayName("단축 URL 생성")
	void testGenerateShortUrl() {
		Post post = postRepository.save(new Post());

		ShareShortUrlResponseDto shareShortUrlResponseDto = shortUrlService.shareShortUrl(post.getIdPost());

		assertThat(shareShortUrlResponseDto.getShortUrl()).contains("/s/");
	}

	@Test
	@DisplayName("단축 URL로 원본 URL을 리다이렉트")
	void testRedirect() throws Exception {
		Post post = postRepository.save(new Post());

		String shortUrl = shortCodeGenerator.generateShortCode();

		UrlMapping mapping = UrlMapping.builder()
				.shortUrl(shortUrl)
				.originalUrl("https://www.naver.com") // 필요 시 원본 URL도 저장
				.post(post)
				.build();

		urlMappingRepository.save(mapping);

		// 그 결과로 나온 shortUrl을 GET 요청에 사용
		mockMvc.perform(
						get("/s/" + shortUrl)
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isFound())
				.andExpect(header().string("Location", "https://www.naver.com"));
	}
}
