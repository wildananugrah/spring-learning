# K6 Performance Testing Suite

Complete performance testing suite for the Banking Application using k6.

---

## Prerequisites

### Install k6

**macOS (Homebrew):**
```bash
brew install k6
```

**Linux (Debian/Ubuntu):**
```bash
sudo gpg -k
sudo gpg --no-default-keyring --keyring /usr/share/keyrings/k6-archive-keyring.gpg --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69
echo "deb [signed-by=/usr/share/keyrings/k6-archive-keyring.gpg] https://dl.k6.io/deb stable main" | sudo tee /etc/apt/sources.list.d/k6.list
sudo apt-get update
sudo apt-get install k6
```

**Windows (Chocolatey):**
```bash
choco install k6
```

**Or download from:** https://k6.io/docs/getting-started/installation/

---

## Test Users

The performance tests use 5 pre-configured users:

| Username | Password     | Account Number |
|----------|--------------|----------------|
| test1    | password123  | 0978710798     |
| test2    | password123  | 0723110421     |
| test3    | password123  | 0544021686     |
| test4    | password123  | 0978685596     |
| test5    | password123  | 0840958275     |

---

## Test Scripts

### 1. **smoke-test.js** - Smoke Test
**Purpose**: Minimal load test to verify the system works correctly

**Duration**: 30 seconds
**Virtual Users**: 1
**Use Case**: Quick sanity check before other tests

```bash
k6 run smoke-test.js
```

**What it tests**:
- User login
- Money transfer ($50)
- Transaction history retrieval
- Account details retrieval

---

### 2. **load-test.js** - Load Test
**Purpose**: Simulate realistic user load over time

**Duration**: ~4.5 minutes
**Virtual Users**: 5-10
**Use Case**: Test normal and peak load conditions

```bash
k6 run load-test.js
```

**Test Stages**:
1. Ramp-up: 0 → 5 users (30s)
2. Sustained: 5 users (2m)
3. Spike: 5 → 10 users (30s)
4. Peak: 10 users (1m)
5. Ramp-down: 10 → 0 users (30s)

**Scenario per user**:
1. Login
2. Transfer $50 to random user
3. Check transaction history
4. Check account details

---

### 3. **stress-test.js** - Stress Test
**Purpose**: Push the system beyond normal capacity to find breaking points

**Duration**: ~10 minutes
**Virtual Users**: Up to 50
**Use Case**: Find system limits and bottlenecks

```bash
k6 run stress-test.js
```

**Test Stages**:
1. Ramp-up: 0 → 10 users (1m)
2. Scale: 10 → 20 users (2m)
3. Scale: 20 → 30 users (2m)
4. Stress: 30 → 40 users (2m)
5. Peak: 40 → 50 users (1m)
6. Ramp-down: 50 → 0 users (2m)

---

### 4. **spike-test.js** - Spike Test
**Purpose**: Test system behavior under sudden traffic bursts

**Duration**: ~3 minutes
**Virtual Users**: 5 → 50 (sudden spike)
**Use Case**: Simulate viral events, flash sales, etc.

```bash
k6 run spike-test.js
```

**Test Stages**:
1. Normal: 5 users (30s)
2. **SPIKE**: 5 → 50 users in 10s! 💥
3. Sustain: 50 users (1m)
4. Recovery: 50 → 5 users (30s)
5. Ramp-down: 5 → 0 users (30s)

---

## Running Tests

### Basic Execution

```bash
# Navigate to k6 folder
cd k6

# Run smoke test
k6 run --out web-dashboard smoke-test.js
k6 run --out web-dashboard --open smoke-test.js

# Run load test
k6 run load-test.js

# Run stress test
k6 run stress-test.js

# Run spike test
k6 run spike-test.js
```

### Advanced Options

**Save results to file:**
```bash
k6 run --out json=results.json load-test.js
```

**Generate HTML report (requires xk6-reporter):**
```bash
k6 run --out html=report.html smoke-test.js
k6 run --out html=report.html load-test.js
k6 run --out html=report.html spike-test.js
k6 run --out html=report.html stress-test.js
```

**Override test configuration:**
```bash
# Run with 10 VUs for 1 minute
k6 run --vus 10 --duration 1m load-test.js

# Run specific stage
k6 run --stage 30s:10,1m:20,30s:0 load-test.js
```

**Set environment variables:**
```bash
# Change base URL
k6 run -e BASE_URL=http://production-server:8000 load-test.js
```

---

## Understanding Results

### Key Metrics

**http_req_duration**: Request response time
- `p(95)`: 95th percentile - 95% of requests completed under this time
- `p(99)`: 99th percentile - 99% of requests completed under this time
- `avg`: Average response time
- `max`: Maximum response time

**http_req_failed**: Percentage of failed HTTP requests
- Should be < 1% for healthy systems
- Higher rates indicate errors or timeouts

**http_reqs**: Total number of HTTP requests
- Throughput indicator

**vus**: Virtual Users
- Number of concurrent users

**iterations**: Number of test iterations completed

### Sample Output

```
     ✓ login status is 200
     ✓ login returns token
     ✓ transfer status is 201
     ✓ transfer successful
     ✓ transaction history status is 200
     ✓ transaction history returns data
     ✓ account details status is 200
     ✓ account details returns data

     checks.........................: 100.00% ✓ 800   ✗ 0
     data_received..................: 2.1 MB  70 kB/s
     data_sent......................: 156 kB  5.2 kB/s
     http_req_blocked...............: avg=1.2ms    p(95)=3.4ms
     http_req_duration..............: avg=145ms    p(95)=389ms
     http_req_failed................: 0.00%   ✓ 0     ✗ 400
     http_reqs......................: 400     13.3/s
     iterations.....................: 100     3.33/s
     vus............................: 5       min=0   max=10
```

### Interpreting Results

**✅ Good Performance**:
- `http_req_duration` p(95) < 2000ms
- `http_req_failed` < 1%
- All checks passing (✓)

**⚠️ Warning Signs**:
- `http_req_duration` p(95) > 3000ms
- `http_req_failed` > 5%
- Some checks failing

**❌ Poor Performance**:
- `http_req_duration` p(95) > 5000ms
- `http_req_failed` > 10%
- Many checks failing

---

## Thresholds

Each test has defined thresholds that determine pass/fail:

### Smoke Test
```javascript
http_req_duration: ['p(95)<3000']  // 95% under 3s
http_req_failed: ['rate<0.01']     // < 1% errors
```

### Load Test
```javascript
http_req_duration: ['p(95)<2000']  // 95% under 2s
http_req_failed: ['rate<0.1']      // < 10% errors
```

### Stress Test
```javascript
http_req_duration: ['p(95)<5000']  // 95% under 5s (more lenient)
http_req_failed: ['rate<0.3']      // < 30% errors (stress expected)
```

### Spike Test
```javascript
http_req_duration: ['p(95)<8000']  // 95% under 8s (very lenient)
http_req_failed: ['rate<0.5']      // < 50% errors (spike recovery)
```

---

## Troubleshooting

### Application Not Running

**Error**: `Connection refused`

**Solution**:
```bash
# Make sure application is running
docker compose up -d

# Or run locally
./mvnw spring-boot:run

# Verify application is accessible
curl http://localhost:8000/actuator/health
```

### Users Not Found

**Error**: `Login failed` or `Invalid credentials`

**Solution**: Make sure all 5 users are registered in the database:
```bash
# Use the api-tests.http file to register users
# Or manually register via API
```

### Insufficient Balance

**Error**: `Insufficient balance for transfer`

**Solution**: Add initial balance to accounts or use deposit endpoint:
```bash
# Deposit money to accounts before running tests
curl -X POST http://localhost:8000/api/transactions/deposit \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"accountNumber": "0978710798", "amount": 10000, "description": "Initial balance"}'
```

### High Error Rates

**Possible Causes**:
1. Database connection pool exhausted
2. Application out of memory
3. Too many concurrent requests
4. Database locks/deadlocks

**Solutions**:
- Increase database connection pool size
- Increase JVM memory
- Add caching layer
- Optimize database queries
- Add database indexes

---

## Best Practices

### Before Running Tests

1. **Ensure clean state**:
   ```bash
   # Reset database if needed
   docker compose down -v
   docker compose up -d
   ```

2. **Warm up the application**:
   ```bash
   # Run smoke test first
   k6 run smoke-test.js
   ```

3. **Monitor resources**:
   ```bash
   # Watch Docker stats
   docker stats

   # Check application logs
   docker compose logs -f app
   ```

### During Tests

1. **Monitor Actuator metrics**:
   ```bash
   # Health
   watch -n 1 'curl -s http://localhost:8000/actuator/health'

   # Metrics
   curl http://localhost:8000/actuator/metrics/http.server.requests
   ```

2. **Check database connections**:
   ```bash
   curl http://localhost:8000/actuator/metrics/hikaricp.connections.active
   ```

### After Tests

1. **Analyze results**:
   - Check for failed requests
   - Review response times
   - Identify bottlenecks

2. **Generate reports**:
   ```bash
   # Export to JSON for analysis
   k6 run --out json=results/load-test-$(date +%Y%m%d-%H%M%S).json load-test.js
   ```

3. **Clean up**:
   ```bash
   # Check database state
   # Review application logs
   # Reset if needed
   ```

---

## Test Execution Order

**Recommended order for comprehensive testing**:

1. **Smoke Test** - Verify basic functionality
   ```bash
   k6 run smoke-test.js
   ```

2. **Load Test** - Test normal operations
   ```bash
   k6 run load-test.js
   ```

3. **Stress Test** - Find breaking points
   ```bash
   k6 run stress-test.js
   ```

4. **Spike Test** - Test recovery
   ```bash
   k6 run spike-test.js
   ```

---

## Configuration

### Change Base URL

Edit the `BASE_URL` constant in each script:

```javascript
const BASE_URL = 'http://localhost:8000';
// Change to:
const BASE_URL = 'http://your-server:8000';
```

Or use environment variable:
```bash
k6 run -e BASE_URL=http://production:8000 load-test.js
```

### Adjust Test Users

Edit the `users` array in each script:

```javascript
const users = [
  { username: 'user1', password: 'password123', accountNumber: '0978710798' },
  // Add more users...
];
```

### Modify Test Stages

Edit the `stages` in the options:

```javascript
export const options = {
  stages: [
    { duration: '1m', target: 10 },  // Customize duration and target
    { duration: '2m', target: 20 },
  ],
};
```

---

## Advanced Features

### Custom Metrics

The tests include custom error tracking:

```javascript
import { Rate } from 'k6/metrics';
const errorRate = new Rate('errors');

// Track custom errors
if (!success) {
  errorRate.add(1);
}
```

### Checks vs Thresholds

**Checks**: Validate responses (don't stop test if failed)
```javascript
check(response, {
  'status is 200': (r) => r.status === 200,
});
```

**Thresholds**: Define pass/fail criteria (fail test if not met)
```javascript
thresholds: {
  http_req_duration: ['p(95)<2000'],
}
```

---

## CI/CD Integration

### GitHub Actions Example

```yaml
name: Performance Tests

on:
  push:
    branches: [ main ]

jobs:
  performance:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: Start application
        run: docker compose up -d

      - name: Install k6
        run: |
          sudo gpg -k
          sudo gpg --no-default-keyring --keyring /usr/share/keyrings/k6-archive-keyring.gpg --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69
          echo "deb [signed-by=/usr/share/keyrings/k6-archive-keyring.gpg] https://dl.k6.io/deb stable main" | sudo tee /etc/apt/sources.list.d/k6.list
          sudo apt-get update
          sudo apt-get install k6

      - name: Run smoke test
        run: k6 run k6/smoke-test.js

      - name: Run load test
        run: k6 run k6/load-test.js
```

---

## Resources

- **k6 Documentation**: https://k6.io/docs/
- **k6 Examples**: https://k6.io/docs/examples/
- **k6 Cloud**: https://k6.io/cloud/ (for advanced analytics)
- **Grafana k6**: https://grafana.com/docs/k6/latest/

---

## Summary

This test suite provides comprehensive performance testing for the banking application:

| Test Type | Duration | Max Users | Purpose |
|-----------|----------|-----------|---------|
| Smoke | 30s | 1 | Sanity check |
| Load | 4.5m | 10 | Normal operations |
| Stress | 10m | 50 | Find limits |
| Spike | 3m | 50 | Burst traffic |

**Quick Start**:
```bash
cd k6
k6 run smoke-test.js
k6 run load-test.js
```

Happy testing! 🚀
