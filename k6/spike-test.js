import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

const errorRate = new Rate('errors');

// Spike test - sudden burst of traffic
export const options = {
  stages: [
    { duration: '30s', target: 5 },    // Normal load
    { duration: '10s', target: 50 },   // Sudden spike!
    { duration: '1m', target: 50 },    // Sustain spike
    { duration: '30s', target: 5 },    // Return to normal
    { duration: '30s', target: 0 },    // Ramp-down
  ],
  thresholds: {
    http_req_duration: ['p(95)<8000'],  // More lenient during spike
    http_req_failed: ['rate<0.5'],      // Accept 50% errors during spike
  },
};

const BASE_URL = 'http://localhost:8000';

const users = [
  { username: 'test1', password: 'password123', accountNumber: '0978710798' },
  { username: 'test2', password: 'password123', accountNumber: '0723110421' },
  { username: 'test3', password: 'password123', accountNumber: '0544021686' },
  { username: 'test4', password: 'password123', accountNumber: '0978685596' },
  { username: 'test5', password: 'password123', accountNumber: '0840958275' },
];

function getRandomUser() {
  return users[Math.floor(Math.random() * users.length)];
}

function getRandomRecipient(currentAccountNumber) {
  const otherUsers = users.filter(u => u.accountNumber !== currentAccountNumber);
  return otherUsers[Math.floor(Math.random() * otherUsers.length)];
}

export default function () {
  const user = getRandomUser();

  // Login
  const loginRes = http.post(
    `${BASE_URL}/api/auth/login`,
    JSON.stringify({
      username: user.username,
      password: user.password,
    }),
    {
      headers: { 'Content-Type': 'application/json' },
      timeout: '10s',
    }
  );

  if (!check(loginRes, { 'login successful': (r) => r.status === 200 })) {
    errorRate.add(1);
    return;
  }

  const token = JSON.parse(loginRes.body).data.token;

  // Transfer
  const recipient = getRandomRecipient(user.accountNumber);
  const transferRes = http.post(
    `${BASE_URL}/api/transactions/transfer`,
    JSON.stringify({
      accountNumber: user.accountNumber,
      toAccountNumber: recipient.accountNumber,
      amount: 50,
      description: 'Spike test transfer',
    }),
    {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
      timeout: '10s',
    }
  );

  check(transferRes, { 'transfer successful': (r) => r.status === 201 }) || errorRate.add(1);

  // Quick history check
  http.get(
    `${BASE_URL}/api/transactions?accountNumber=${user.accountNumber}&page=0&size=10`,
    {
      headers: { 'Authorization': `Bearer ${token}` },
      timeout: '10s',
    }
  );

  sleep(0.1); // Minimal sleep during spike
}
