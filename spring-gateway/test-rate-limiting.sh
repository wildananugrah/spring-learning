#!/bin/bash

# Rate Limiting Test Script for Spring Cloud Gateway
# This script demonstrates rate limiting behavior

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

GATEWAY_URL="http://localhost:9090"

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Rate Limiting Test Script${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""

# Test 1: Basic Rate Limit Test
echo -e "${YELLOW}Test 1: Basic Rate Limit (25 rapid requests)${NC}"
echo "Expected: First 20 succeed, remaining 5 get 429 Too Many Requests"
echo ""

SUCCESS_COUNT=0
RATE_LIMITED_COUNT=0

for i in {1..25}; do
  HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" \
    -X POST "$GATEWAY_URL/api/auth/login" \
    -H "Content-Type: application/json" \
    -d '{"username":"test","password":"test123"}')

  if [ "$HTTP_CODE" == "429" ]; then
    echo -e "Request $i: ${RED}429 Too Many Requests${NC}"
    RATE_LIMITED_COUNT=$((RATE_LIMITED_COUNT + 1))
  else
    echo -e "Request $i: ${GREEN}$HTTP_CODE (OK)${NC}"
    SUCCESS_COUNT=$((SUCCESS_COUNT + 1))
  fi
done

echo ""
echo -e "${GREEN}Results:${NC}"
echo "  Success: $SUCCESS_COUNT"
echo "  Rate Limited: $RATE_LIMITED_COUNT"
echo ""

# Test 2: Rate Limit Recovery
echo -e "${YELLOW}Test 2: Rate Limit Recovery${NC}"
echo "Waiting 5 seconds for rate limit to recover..."
sleep 5

echo "Making 5 requests after recovery:"
for i in {1..5}; do
  HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" \
    -X POST "$GATEWAY_URL/api/auth/login" \
    -H "Content-Type: application/json" \
    -d '{"username":"test","password":"test123"}')

  if [ "$HTTP_CODE" == "429" ]; then
    echo -e "Request $i: ${RED}429 Too Many Requests${NC}"
  else
    echo -e "Request $i: ${GREEN}$HTTP_CODE (Recovered!)${NC}"
  fi
done
echo ""

# Test 3: Check Rate Limit Headers
echo -e "${YELLOW}Test 3: Rate Limit Headers${NC}"
echo "Making a single request to check headers:"
RESPONSE=$(curl -s -i -X POST "$GATEWAY_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"test123"}')

echo "$RESPONSE" | grep -i "X-RateLimit"
echo ""

# Test 4: Different Endpoints, Different Limits
echo -e "${YELLOW}Test 4: Different Limits Per Endpoint${NC}"
echo ""

echo "Testing /api/auth/** (10 req/sec, burst 20):"
AUTH_429_COUNT=0
for i in {1..25}; do
  HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" \
    "$GATEWAY_URL/api/auth/login" \
    -X POST \
    -H "Content-Type: application/json" \
    -d '{}')

  if [ "$HTTP_CODE" == "429" ]; then
    AUTH_429_COUNT=$((AUTH_429_COUNT + 1))
  fi
done
echo "Rate limited requests: $AUTH_429_COUNT / 25"

echo ""
echo "Waiting 3 seconds..."
sleep 3
echo ""

echo "Testing /api/transactions/** (3 req/sec, burst 5):"
# Note: This requires JWT token, so we'll just test without auth
TRANS_429_COUNT=0
for i in {1..10}; do
  HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" \
    "$GATEWAY_URL/api/transactions/deposit" \
    -X POST \
    -H "Content-Type: application/json" \
    -d '{}')

  if [ "$HTTP_CODE" == "429" ]; then
    TRANS_429_COUNT=$((TRANS_429_COUNT + 1))
  fi
done
echo "Rate limited requests: $TRANS_429_COUNT / 10"
echo ""

# Summary
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Test Complete!${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo "Summary:"
echo "  - Rate limiting is working if you see 429 responses"
echo "  - Check X-RateLimit headers for remaining tokens"
echo "  - Different endpoints have different limits"
echo ""
echo "To adjust rate limits, edit: src/main/resources/application.yml"
echo ""
