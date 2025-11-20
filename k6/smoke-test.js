import http from 'k6/http';
import { check, sleep } from 'k6';

import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";
import { textSummary } from "https://jslib.k6.io/k6-summary/0.0.1/index.js";

// Smoke test - minimal load to verify the system works
export const options = {
  vus: 1, // 1 virtual user
  duration: '30s', // Run for 30 seconds
  thresholds: {
    http_req_duration: ['p(95)<3000'], // 95% of requests under 3s
    http_req_failed: ['rate<0.01'],    // Less than 1% errors
  },
};

const BASE_URL = 'http://localhost:8000';

// Use just one user for smoke test
const testUser = {
  username: 'test1',
  password: 'password123',
  accountNumber: '0978710798',
};

const recipient = {
  username: 'test2',
  accountNumber: '0723110421',
};

export default function () {
  // Login
  const loginRes = http.post(
    `${BASE_URL}/api/auth/login`,
    JSON.stringify({
      username: testUser.username,
      password: testUser.password,
    }),
    {
      headers: { 'Content-Type': 'application/json' },
    }
  );

  check(loginRes, {
    'login successful': (r) => r.status === 200,
  });

  if (loginRes.status !== 200) {
    console.error('Smoke test failed: Cannot login');
    return;
  }

  const token = JSON.parse(loginRes.body).data.token;

  // Transfer
  const transferRes = http.post(
    `${BASE_URL}/api/transactions/transfer`,
    JSON.stringify({
      accountNumber: testUser.accountNumber,
      toAccountNumber: recipient.accountNumber,
      amount: 50,
      description: 'Smoke test transfer',
    }),
    {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
    }
  );

  check(transferRes, {
    'transfer successful': (r) => r.status === 201,
  });

  // Get transaction history
  const txnRes = http.get(
    `${BASE_URL}/api/transactions?accountNumber=${testUser.accountNumber}&page=0&size=10`,
    {
      headers: { 'Authorization': `Bearer ${token}` },
    }
  );

  check(txnRes, {
    'transaction history retrieved': (r) => r.status === 200,
  });

  // Get account details
  const accountRes = http.get(`${BASE_URL}/api/accounts`, {
    headers: { 'Authorization': `Bearer ${token}` },
  });

  check(accountRes, {
    'account details retrieved': (r) => r.status === 200,
  });

  sleep(1);
}

export function handleSummary(data) {
  return {
    "summary.html": htmlReport(data),
    "summary.json": JSON.stringify(data),
    stdout: textSummary(data, { indent: " ", enableColors: true }),
  };
}

