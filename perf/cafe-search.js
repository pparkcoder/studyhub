import http from 'k6/http';
import {check} from 'k6';

const BASE_URL = 'http://localhost:8080';
const SEARCH_PATH = '/cafe';   // TODO: CafeController의 검색 경로로 맞추기

const REGIONS = [
    '서울 강남구', '서울 금천구', '서울 관악구', '서울 구로구', '서울 마포구',
    '서울 송파구', '서울 영등포구', '서울 종로구', '서울 서초구', '서울 동작구',
];

// "스터디카페"의 부분 문자열 15개. 측정용 카페 이름이 모두 "스터디카페"를 포함하므로
// 어떤 키워드로 검색해도 지역의 카페 1,000개가 모두 나온다.
const KEYWORDS = [
    '스', '스터', '스터디', '스터디카', '스터디카페',
    '터', '터디', '터디카', '터디카페',
    '디', '디카', '디카페',
    '카', '카페',
    '페',
];

const COUNT = REGIONS.length * KEYWORDS.length;   // 150

export const options = {
    scenarios: {
        search: {
            executor: 'shared-iterations',
            vus: 1,
            iterations: COUNT,
        },
    },
    summaryTrendStats: ['avg', 'med', 'p(95)', 'p(99)', 'max'],
};

export default function () {
    const region = REGIONS[Math.floor(__ITER / KEYWORDS.length)];
    const keyword = KEYWORDS[__ITER % KEYWORDS.length];

    const url = `${BASE_URL}${SEARCH_PATH}?region=${encodeURIComponent(region)}&keyword=${encodeURIComponent(keyword)}`;
    const res = http.get(url);

    check(res, {
        'status is 200': (r) => r.status === 200,
        'result count is 1000': (r) => r.json().length === 1000,
    });
}