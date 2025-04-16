package com.example.short_url.util;

public class Base62Encoder {
    /**
     *  6자리 알파벳+숫자 코드 생성
     *  총 62개 문자 사용: [a-z] + [A-Z] + [0-9]
     *  Base64는 '+', '/', '=' 등의 URL에 적합하지 않은 문자를 포함하지만,
     *  Base62는 오직 URL-safe한 문자만 사용하여 URL 인코딩 이슈가 없음
     */
    private static final String CHAR_SET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int BASE = CHAR_SET.length();

    public static String encode(long number) {
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % BASE);
            sb.append(CHAR_SET.charAt(remainder));
            number /= BASE;
        }
        return sb.reverse().toString();
    }
}
