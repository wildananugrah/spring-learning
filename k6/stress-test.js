import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

const errorRate = new Rate('errors');

// Stress test - push the system to its limits
export const options = {
  stages: [
    { duration: '1m', target: 10 },   // Ramp-up to 10 users
    { duration: '2m', target: 20 },   // Scale to 20 users
    { duration: '2m', target: 30 },   // Scale to 30 users
    { duration: '2m', target: 40 },   // Scale to 40 users (stress)
    { duration: '1m', target: 50 },   // Spike to 50 users
    { duration: '2m', target: 0 },    // Ramp-down
  ],
  thresholds: {
    http_req_duration: ['p(95)<5000'], // Accept slower response under stress
    http_req_failed: ['rate<0.3'],     // Accept up to 30% errors under stress
    errors: ['rate<0.3'],
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
    }
  );

  const loginSuccess = check(loginRes, {
    'login status is 200': (r) => r.status === 200,
  });

  if (!loginSuccess) {
    errorRate.add(1);
    sleep(0.5);
    return;
  }

  const token = JSON.parse(loginRes.body).data.token;
  sleep(0.5);

  // Transfer
  const recipient = getRandomRecipient(user.accountNumber);
  const transferRes = http.post(
    `${BASE_URL}/api/transactions/transfer`,
    JSON.stringify({
      accountNumber: user.accountNumber,
      toAccountNumber: recipient.accountNumber,
      amount: 50,
      description: `Stress test transfer`,
    }),
    {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
    }
  );

  const transferSuccess = check(transferRes, {
    'transfer status is 201': (r) => r.status === 201,
  });

  if (!transferSuccess) {
    errorRate.add(1);
  }

  sleep(0.3);

  // Transaction history
  const txnRes = http.get(
    `${BASE_URL}/api/transactions?accountNumber=${user.accountNumber}&page=0&size=20`,
    {
      headers: { 'Authorization': `Bearer ${token}` },
    }
  );

  check(txnRes, {
    'transaction history status is 200': (r) => r.status === 200,
  }) || errorRate.add(1);

  sleep(0.3);

  // Account details
  const accountRes = http.get(`${BASE_URL}/api/accounts`, {
    headers: { 'Authorization': `Bearer ${token}` },
  });

  check(accountRes, {
    'account details status is 200': (r) => r.status === 200,
  }) || errorRate.add(1);

  sleep(Math.random() * 0.5);
}
