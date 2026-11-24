import http from 'k6/http';
import {sleep} from 'k6';
import {Counter} from 'k6/metrics';

const BASE_URL = 'http://localhost:8080';
const USER_COUNT = 100;
const PASSWORD = 'perf-password';
const JSON_HEADERS = {'Content-Type': 'application/json'};

const CAFE_ID = Number(__ENV.CAFE_ID);
const SEAT_ID = Number(__ENV.SEAT_ID);
const START_TIME = __ENV.START_TIME;   // 예: 2026-11-24T23:00:00 (오늘 날짜, 지금보다 뒤)

const successCount = new Counter('reservation_success');
const failCount = new Counter('reservation_fail');

export const options = {
    setupTimeout: '120s',
    scenarios: {
        reserve: {
            executor: 'per-vu-iterations',
            vus: USER_COUNT,
            iterations: 1,
            maxDuration: '60s',
        },
    },
    thresholds: {
        // 예약 요청만 따로 응답 시간을 보기 위한 설정 (setup의 가입·로그인 요청 제외)
        'http_req_duration{name:reserve}': ['max>=0'],
    },
    summaryTrendStats: ['avg', 'med', 'p(95)', 'p(99)', 'max'],
};

export function setup() {
    const tokens = [];
    for (let i = 1; i <= USER_COUNT; i++) {
        const username = `perfuser${i}`;

        // 두 번째 실행부터는 이미 가입된 회원이라 실패하지만, 로그인만 되면 되므로 계속 진행
        const signUpRes = http.post(`${BASE_URL}/member/signup`, JSON.stringify({
            username: username,
            nickname: username,
            email: `${username}@test.com`,
            password: PASSWORD,
            role: 'MEMBER',
        }), {headers: JSON_HEADERS});

        if (signUpRes.status >= 400) {
            console.log(`가입 실패(이미 가입된 경우 무시): ${username}, status=${signUpRes.status}, body=${signUpRes.body}`);
        }

        const loginRes = http.post(`${BASE_URL}/auth/login`, JSON.stringify({
            username: username,
            password: PASSWORD,
        }), {headers: JSON_HEADERS});

        const token = loginRes.json('accessToken');
        if (!token) {
            throw new Error(`로그인 실패: ${username}, status=${loginRes.status}, body=${loginRes.body}`);
        }
        tokens.push(token);
    }
    return {tokens: tokens, startAt: Date.now() + 3000};
}

export default function (data) {
    // 모든 사용자가 같은 시각에 요청하도록 기다림
    const waitSeconds = (data.startAt - Date.now()) / 1000;
    if (waitSeconds > 0) {
        sleep(waitSeconds);
    }

    const token = data.tokens[__VU - 1];
    const res = http.post(`${BASE_URL}/reservation`, JSON.stringify({
        cafeId: CAFE_ID,
        seatId: SEAT_ID,
        startTime: START_TIME,
        duration: 'ONE_HOUR',
    }), {
        headers: {...JSON_HEADERS, Authorization: `Bearer ${token}`},
        tags: {name: 'reserve'},
    });

    if (res.status === 201) {
        successCount.add(1);
    } else {
        failCount.add(1);
        console.log(`실패 status=${res.status} body=${res.body}`);
    }
}