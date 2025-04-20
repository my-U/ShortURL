/** 병목 한계 테스트 (최대 지점까지 ramp up) **/
import http from 'k6/http';
import { check } from 'k6';

export let options = {
    stages: [
        { duration: '1m', target: 20 },
        { duration: '2m', target: 60 },
        { duration: '2m', target: 100 },
        { duration: '1m', target: 0 },
    ],
};

export default function () {
    const res = http.get('http://localhost:8080/s/D', {
        redirects: 0,
    });

    // 실패 응답 로그 출력
    if (res.status !== 200 && res.status !== 302) {
        console.error(`❌ Failed: status=${res.status} | url=${res.url}`);
    }

    check(res, {
        'status is 200 or 302': (r) => r.status === 200 || r.status === 302,
    });
}