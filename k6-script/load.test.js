/** 배포 전 부하 테스트 **/
import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    vus: 200, // 가상 사용자
    duration: '1m', // 1분동안 계속 요청
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