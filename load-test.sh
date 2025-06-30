#!/bin/bash

# 부하테스트 스크립트
echo "🚀 ParkMate Notification Service 부하테스트 시작"

# 테스트 URL
BASE_URL="http://localhost:8080"
HEALTH_URL="$BASE_URL/test/health"
RATE_LIMIT_URL="$BASE_URL/test/rate-limit-test"
CIRCUIT_BREAKER_URL="$BASE_URL/test/circuit-breaker-test"

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 헬스 체크
echo -e "${BLUE}📊 헬스 체크 테스트${NC}"
for i in {1..5}; do
    response=$(curl -s -w "%{http_code}" -o /dev/null "$HEALTH_URL")
    if [ "$response" = "200" ]; then
        echo -e "${GREEN}✅ 요청 $i: 성공 (HTTP $response)${NC}"
    else
        echo -e "${RED}❌ 요청 $i: 실패 (HTTP $response)${NC}"
    fi
    sleep 0.1
done

echo ""

# Rate Limiting 테스트
echo -e "${BLUE}🚦 Rate Limiting 테스트${NC}"
echo "1000개의 요청을 빠르게 보냅니다..."
start_time=$(date +%s.%N)

success_count=0
failure_count=0
rate_limited_count=0

for i in {1..1000}; do
    response=$(curl -s -w "%{http_code}" -o /dev/null "$RATE_LIMIT_URL")
    
    case $response in
        200)
            success_count=$((success_count + 1))
            ;;
        429)
            rate_limited_count=$((rate_limited_count + 1))
            ;;
        *)
            failure_count=$((failure_count + 1))
            ;;
    esac
    
    # 진행률 표시
    if [ $((i % 100)) -eq 0 ]; then
        echo -e "${YELLOW}진행률: $i/1000${NC}"
    fi
done

end_time=$(date +%s.%N)
duration=$(echo "$end_time - $start_time" | bc)

echo ""
echo -e "${GREEN}📈 Rate Limiting 테스트 결과:${NC}"
echo -e "  총 요청: 1000"
echo -e "  성공: ${GREEN}$success_count${NC}"
echo -e "  Rate Limited: ${YELLOW}$rate_limited_count${NC}"
echo -e "  실패: ${RED}$failure_count${NC}"
echo -e "  소요 시간: ${BLUE}${duration}초${NC}"
echo -e "  RPS: ${BLUE}$(echo "scale=2; 1000 / $duration" | bc)${NC}"

echo ""

# Circuit Breaker 테스트
echo -e "${BLUE}⚡ Circuit Breaker 테스트${NC}"
for i in {1..10}; do
    response=$(curl -s -w "%{http_code}" -o /dev/null "$CIRCUIT_BREAKER_URL")
    if [ "$response" = "200" ]; then
        echo -e "${GREEN}✅ 요청 $i: 성공 (HTTP $response)${NC}"
    else
        echo -e "${RED}❌ 요청 $i: 실패 (HTTP $response)${NC}"
    fi
    sleep 0.2
done

echo ""
echo -e "${GREEN}🎉 부하테스트 완료!${NC}" 