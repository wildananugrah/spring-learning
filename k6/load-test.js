import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

// Custom metrics
const errorRate = new Rate('errors');

// Test configuration
export const options = {
  stages: [
    { duration: '30s', target: 5 },   // Ramp-up to 5 users
    { duration: '2m', target: 5 },    // Stay at 5 users for 2 minutes
    { duration: '30s', target: 10 },  // Spike to 10 users
    { duration: '1m', target: 10 },   // Stay at 10 users
    { duration: '30s', target: 0 },   // Ramp-down to 0 users
  ],
  thresholds: {
    http_req_duration: ['p(95)<2000'], // 95% of requests should be below 2s
    http_req_failed: ['rate<0.1'],     // Error rate should be less than 10%
    errors: ['rate<0.1'],
  },
};

// Base URL - adjust if needed
const BASE_URL = 'http://localhost:8000';

// Test users with their account numbers
const users = [
  { username: 'test1', password: 'password123', accountNumber: '0978710798' },
  { username: 'test2', password: 'password123', accountNumber: '0723110421' },
  { username: 'test3', password: 'password123', accountNumber: '0544021686' },
  { username: 'test4', password: 'password123', accountNumber: '0978685596' },
  { username: 'test5', password: 'password123', accountNumber: '0840958275' },
];

// Helper function to get random user
function getRandomUser() {
  return users[Math.floor(Math.random() * users.length)];
}

// Helper function to get different random user (not the same as current)
function getRandomRecipient(currentAccountNumber) {
  const otherUsers = users.filter(u => u.accountNumber !== currentAccountNumber);
  return otherUsers[Math.floor(Math.random() * otherUsers.length)];
}

// Login function
function login(username, password) {
  const loginPayload = JSON.stringify({
    username: username,
    password: password,
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  const loginRes = http.post(`${BASE_URL}/api/auth/login`, loginPayload, params);

  const loginSuccess = check(loginRes, {
    'login status is 200': (r) => r.status === 200,
    'login returns token': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.data && body.data.token;
      } catch (e) {
        return false;
      }
    },
  });

  if (!loginSuccess) {
    errorRate.add(1);
    console.error(`Login failed for ${username}: ${loginRes.status} - ${loginRes.body}`);
    return null;
  }

  const token = JSON.parse(loginRes.body).data.token;
  return token;
}

// Transfer money function
function transferMoney(token, fromAccountNumber, toAccountNumber, amount, description) {
  const transferPayload = JSON.stringify({
    accountNumber: fromAccountNumber,
    toAccountNumber: toAccountNumber,
    amount: amount,
    description: description,
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
    },
  };

  const transferRes = http.post(`${BASE_URL}/api/transactions/transfer`, transferPayload, params);

  const transferSuccess = check(transferRes, {
    'transfer status is 201': (r) => r.status === 201,
    'transfer successful': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.success === true;
      } catch (e) {
        return false;
      }
    },
  });

  if (!transferSuccess) {
    errorRate.add(1);
    console.error(`Transfer failed: ${transferRes.status} - ${transferRes.body}`);
  }

  return transferSuccess;
}

// Get transaction history
function getTransactionHistory(token, accountNumber) {
  const params = {
    headers: {
      'Authorization': `Bearer ${token}`,
    },
  };

  const txnRes = http.get(
    `${BASE_URL}/api/transactions?accountNumber=${accountNumber}&page=0&size=20&sortBy=createdAt&sortDirection=desc`,
    params
  );

  const txnSuccess = check(txnRes, {
    'transaction history status is 200': (r) => r.status === 200,
    'transaction history returns data': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.success === true && body.data;
      } catch (e) {
        return false;
      }
    },
  });

  if (!txnSuccess) {
    errorRate.add(1);
    console.error(`Get transaction history failed: ${txnRes.status} - ${txnRes.body}`);
  }

  return txnSuccess;
}

// Get account details
function getAccountDetails(token) {
  const params = {
    headers: {
      'Authorization': `Bearer ${token}`,
    },
  };

  const accountRes = http.get(`${BASE_URL}/api/accounts`, params);

  const accountSuccess = check(accountRes, {
    'account details status is 200': (r) => r.status === 200,
    'account details returns data': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.success === true && Array.isArray(body.data);
      } catch (e) {
        return false;
      }
    },
  });

  if (!accountSuccess) {
    errorRate.add(1);
    console.error(`Get account details failed: ${accountRes.status} - ${accountRes.body}`);
  }

  return accountSuccess;
}

// Main test scenario
export default function () {
  // Select a random user
  const user = getRandomUser();

  console.log(`\n=== Starting test for ${user.username} (Account: ${user.accountNumber}) ===`);

  // Step 1: Login
  console.log(`[${user.username}] Logging in...`);
  const token = login(user.username, user.password);

  if (!token) {
    console.error(`[${user.username}] Login failed, skipping this iteration`);
    sleep(1);
    return;
  }

  console.log(`[${user.username}] Login successful`);
  sleep(1); // Wait 1 second after login

  // Step 2: Perform transfer (amount: 50)
  const recipient = getRandomRecipient(user.accountNumber);
  console.log(`[${user.username}] Transferring $50 to ${recipient.username} (${recipient.accountNumber})`);

  const transferSuccess = transferMoney(
    token,
    user.accountNumber,
    recipient.accountNumber,
    50,
    `Load test transfer from ${user.username} to ${recipient.username}`
  );

  if (transferSuccess) {
    console.log(`[${user.username}] Transfer successful`);
  } else {
    console.log(`[${user.username}] Transfer failed`);
  }

  sleep(1); // Wait 1 second after transfer

  // Step 3: Check transaction history
  console.log(`[${user.username}] Checking transaction history...`);
  const txnSuccess = getTransactionHistory(token, user.accountNumber);

  if (txnSuccess) {
    console.log(`[${user.username}] Transaction history retrieved successfully`);
  } else {
    console.log(`[${user.username}] Failed to retrieve transaction history`);
  }

  sleep(1); // Wait 1 second

  // Step 4: Check account details
  console.log(`[${user.username}] Checking account details...`);
  const accountSuccess = getAccountDetails(token);

  if (accountSuccess) {
    console.log(`[${user.username}] Account details retrieved successfully`);
  } else {
    console.log(`[${user.username}] Failed to retrieve account details`);
  }

  console.log(`=== Completed test for ${user.username} ===\n`);

  // Think time between iterations
  sleep(Math.random() * 2 + 1); // Random sleep between 1-3 seconds
}
