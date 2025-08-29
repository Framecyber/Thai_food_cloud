import http from 'k6/http';
import { sleep, check } from 'k6';
import { Trend } from 'k6/metrics';

const trend = new Trend('api_latency');

export const options = {
  vus: Number(ENV.VUS || 10),
  duration: ENV.DURATION  '1m',
  thresholds: {
    http_req_failed: ['rate<0.01'],           // <1% errors
    http_req_duration: ['p(95)<800'],         // 95% < 800ms
  },
};

const BASE = __ENV.BASE_URL  'https://api.example.com/';

export default function () {
  // Adjust endpoints to your API
  const res1 = http.get(${BASE}/api/categories);
  trend.add(res1.timings.duration);
  check(res1, {
    'categories 200': (r) => r.status === 200,
  });

  // Example: menu by category id
  const res2 = http.get(${BASE}/api/foods?categoryId=1);
  trend.add(res2.timings.duration);
  check(res2, { 'foods 200': (r) => r.status === 200 });

  // Optional: create order (if you have a sandbox/test env)
  // const res3 = http.post(${BASE}/api/orders, JSON.stringify({ items: [{id: 1, qty: 2}] }), {
  //   headers: { 'Content-Type': 'application/json' },
  // });
  // check(res3, { 'order 201': (r) => r.status === 201 });

  sleep(1);
}