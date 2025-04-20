/** 기본 응답 확인 **/
import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    vus: 2,         // 동시에 2명 실행
    iterations: 10, // 총 10회 요청
};

export default function () {
    const res = http.get('http://localhost:8080/s/D', {
        // 리다이렉트 주소 429 방지로 redirects: 0 설정
        // 응답 시간 측정이 나의 서버 응답만 포함되기 때문에 더 정확한 테스트 가능
        redirects: 0,
    });

    // 실패 응답 로그 출력
    if (res.status !== 200 && res.status !== 302) {
        console.error(`❌ Failed: status=${res.status} | url=${res.url}`);
    }

    check(res, {
        'status is 200 or 302': (r) => r.status === 200 || r.status === 302,
    });

    sleep(1);
}