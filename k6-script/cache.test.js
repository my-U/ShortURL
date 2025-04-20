/** 캐시 효과 측정 **/
import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    vus: 50,
    iterations: 1000,
};

export default function () {
    const res = http.get('http://localhost:8080/s/D', {
        redirects: 0,
    });

    // 실패 응답 로그 출력
    if (res.status !== 200 && res.status !== 302) {
        console.error(`Failed: status=${res.status} | url=${res.url}`);
    }

    check(res, {
        'status is 200 or 302': (r) => r.status === 200 || r.status === 302,
        'response time < 200ms': (r) => r.timings.duration < 200,
    });

    sleep(0.2);
}