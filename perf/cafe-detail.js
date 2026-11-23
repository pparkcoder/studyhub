import http from 'k6/http';
import {check} from 'k6';

const BASE_URL = 'http://localhost:8080';
const START = Number(__ENV.START || 1);
const COUNT = Number(__ENV.COUNT || 1000);

export const options = {
    scenarios: {
        detail: {
            executor: 'shared-iterations',
            vus: 1,
            iterations: COUNT,
        },
    },
    summaryTrendStats: ['avg', 'med', 'p(95)', 'p(99)', 'max'],
};

export default function () {
    const id = START + __ITER;
    const res = http.get(`${BASE_URL}/cafe/${id}`);
    check(res, {
        'status is 200': (r) => r.status === 200,
    });
}