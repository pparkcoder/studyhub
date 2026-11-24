import http from 'k6/http';
import {check} from 'k6';

const BASE_URL = 'http://localhost:8080';
const JSON_HEADERS = {'Content-Type': 'application/json'};
const USERNAME = 'perfuser1';
const PASSWORD = 'perf-password';

// claude: Claude 호출 / cache: 조건 캐시 적중 / rule: 규칙 추출 (키를 틀린 값으로 바꾼 상태)
const MODE = __ENV.MODE;
if (!['claude', 'cache', 'rule'].includes(MODE)) {
    throw new Error(`MODE는 claude, cache, rule 중 하나여야 합니다. 현재 값: ${MODE}`);
}
const EXPECTED_AI_USED = MODE !== 'rule';

// 측정용 데이터의 지역 10개를 모두 포함한 문장 20개 (회차마다 같은 문장을 사용)
const QUERIES = [
    '강남구에서 조용한 스터디카페',
    '금천구 스터디카페 추천해줘',
    '관악구에 지금 열려있는 스터디카페',
    '구로구 근처 공부할 곳',
    '마포구에서 공부하기 좋은 스터디카페',
    '송파구 조용한 카페',
    '영등포구 스터디카페 지금 영업 중인 곳',
    '종로구에서 공부하기 좋은 곳',
    '서초구 스터디카페 알려줘',
    '동작구에 있는 스터디카페',
    '강남구 지금 열려 있는 스터디카페',
    '금천구에서 지금 갈 수 있는 스터디카페',
    '관악구 조용한 공부 공간',
    '구로구 지금 열려 있는 곳',
    '마포구 스터디카페 찾아줘',
    '송파구에서 지금 영업하는 스터디카페',
    '영등포구 조용한 스터디카페',
    '종로구 지금 열려있는 스터디카페',
    '서초구에서 공부할 스터디카페',
    '동작구 조용한 곳 추천',
];

export const options = {
    scenarios: {
        ai: {
            executor: 'shared-iterations',
            vus: 1,
            iterations: QUERIES.length,
        },
    },
    thresholds: {
        // AI 검색 요청만 따로 응답 시간을 보기 위한 설정 (setup의 로그인 요청 제외)
        'http_req_duration{name:ai-search}': ['max>=0'],
    },
    summaryTrendStats: ['avg', 'med', 'p(95)', 'p(99)', 'max'],
};

export function setup() {
    const res = http.post(`${BASE_URL}/auth/login`, JSON.stringify({
        username: USERNAME,
        password: PASSWORD,
    }), {headers: JSON_HEADERS});

    const token = res.json('accessToken');
    if (!token) {
        throw new Error(`로그인 실패: status=${res.status}, body=${res.body}`);
    }
    return {token: token};
}

export default function (data) {
    const query = QUERIES[__ITER];
    const res = http.post(`${BASE_URL}/ai-search`, JSON.stringify({query: query}), {
        headers: {...JSON_HEADERS, Authorization: `Bearer ${data.token}`},
        tags: {name: 'ai-search'},
    });

    const ok = check(res, {
        'status is 200': (r) => r.status === 200,
        'aiUsed is expected': (r) => r.json('aiUsed') === EXPECTED_AI_USED,
    });
    if (!ok) {
        console.log(`확인 실패 query=${query} status=${res.status} body=${res.body}`);
    }
}