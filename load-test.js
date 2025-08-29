import { sleep } from 'k6'
import http from 'k6/http'

export const options = {
  stages: [
    { duration: '30s', target: 10 },  // Ramp up to 10 users
    { duration: '1m', target: 20 },   // Stay at 20 users
    { duration: '30s', target: 0 },   // Ramp down
  ],
  thresholds: {
    http_req_failed: ['rate<0.05'],      // Less than 5% errors
    http_req_duration: ['p(95)<2000'],   // 95% of requests under 2s
    http_reqs: ['rate>10'],              // At least 10 requests per second
  },
}

// Replace with your AWS deployed URL
const BASE_URL = 'https://your-app.amazonaws.com'  // Update this!

export default function() {
  // Simple GET request to your main endpoint
  let response = http.get(BASE_URL)
  
  // Basic checks
  if (response.status !== 200) {
    console.error(`Request failed with status: ${response.status}`)
  }
  
  // Small delay between requests (1-3 seconds)
  sleep(Math.random() * 2 + 1)
}
