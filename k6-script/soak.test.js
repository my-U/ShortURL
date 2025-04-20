/** 지속 성능 테스트 (예: 1시간 동안) **/
import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    vus: 10,
    duration: '1h',
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

    sleep(1);
}