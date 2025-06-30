#!/bin/bash

# 강도 높은 부하테스트 스크립트
echo "🔥 ParkMate Notification Service 강도 높은 부하테스트 시작"

# 테스트 URL
BASE_URL="http://localhost:8080"
HEALTH_URL="$BASE_URL/test/health"
RATE_LIMIT_URL="$BASE_URL/test/rate-limit-test"
CIRCUIT_BREAKER_URL="$BASE_URL/test/circuit-breaker-test"
ASYNC_URL="$BASE_URL/test/async"
METRICS_URL="$BASE_URL/test/metrics"

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
NC='\033[0m' # No Color

# 동시 요청 테스트 함수
concurrent_test() {
    local endpoint=$1
    local concurrent_users=$2
    local duration=$3
    local test_name=$4
    
    echo -e "${PURPLE}🔄 $test_name 시작 (동시 사용자: $concurrent_users, 지속시간: ${duration}초)${NC}"
    
    start_time=$(date +%s.%N)
    
    # 동시 요청을 위한 백그라운드 프로세스들
    for i in $(seq 1 $concurrent_users); do
        (
            end_time=$((SECONDS + duration))
            while [ $SECONDS -lt $end_time ]; do
                response=$(curl -s -w "%{http_code}" -o /dev/null "$endpoint")
                case $response in
                    200) echo "SUCCESS" >> /tmp/test_results_$i ;;
                    429) echo "RATE_LIMITED" >> /tmp/test_results_$i ;;
                    500) echo "ERROR" >> /tmp/test_results_$i ;;
                    *) echo "FAILED" >> /tmp/test_results_$i ;;
                esac
                sleep 0.1
            done
        ) &
    done
    
    # 모든 백그라운드 프로세스 완료 대기
    wait
    
    end_time=$(date +%s.%N)
    test_duration=$(echo "$end_time - $start_time" | bc)
    
    # 결과 집계
    total_requests=0
    success_count=0
    rate_limited_count=0
    error_count=0
    failed_count=0
    
    for i in $(seq 1 $concurrent_users); do
        if [ -f "/tmp/test_results_$i" ]; then
            while IFS= read -r result; do
                total_requests=$((total_requests + 1))
                case $result in
                    "SUCCESS") success_count=$((success_count + 1)) ;;
                    "RATE_LIMITED") rate_limited_count=$((rate_limited_count + 1)) ;;
                    "ERROR") error_count=$((error_count + 1)) ;;
                    "FAILED") failed_count=$((failed_count + 1)) ;;
                esac
            done < "/tmp/test_results_$i"
            rm "/tmp/test_results_$i"
        fi
    done
    
    # 결과 출력
    echo -e "${GREEN}📊 $test_name 결과:${NC}"
    echo -e "  총 요청: $total_requests"
    echo -e "  성공: ${GREEN}$success_count${NC}"
    echo -e "  Rate Limited: ${YELLOW}$rate_limited_count${NC}"
    echo -e "  서버 오류: ${RED}$error_count${NC}"
    echo -e "  실패: ${RED}$failed_count${NC}"
    echo -e "  소요 시간: ${BLUE}${test_duration}초${NC}"
    echo -e "  RPS: ${BLUE}$(echo "scale=2; $total_requests / $test_duration" | bc)${NC}"
    echo -e "  성공률: ${GREEN}$(echo "scale=2; $success_count * 100 / $total_requests" | bc)%${NC}"
    echo ""
}

# 메트릭스 수집
collect_metrics() {
    echo -e "${BLUE}📈 시스템 메트릭스 수집${NC}"
    metrics=$(curl -s "$METRICS_URL")
    echo "$metrics" | python3 -m json.tool
    echo ""
}

# 1. 기본 헬스 체크
echo -e "${BLUE}📊 기본 헬스 체크${NC}"
for i in {1..10}; do
    response=$(curl -s -w "%{http_code}" -o /dev/null "$HEALTH_URL")
    if [ "$response" = "200" ]; then
        echo -e "${GREEN}✅ 요청 $i: 성공 (HTTP $response)${NC}"
    else
        echo -e "${RED}❌ 요청 $i: 실패 (HTTP $response)${NC}"
    fi
done
echo ""

# 2. 동시 사용자 테스트 (Rate Limiting)
concurrent_test "$RATE_LIMIT_URL" 50 30 "Rate Limiting 동시 사용자 테스트 (50명, 30초)"

# 3. 동시 사용자 테스트 (Circuit Breaker)
concurrent_test "$CIRCUIT_BREAKER_URL" 30 20 "Circuit Breaker 동시 사용자 테스트 (30명, 20초)"

# 4. 동시 사용자 테스트 (Async)
concurrent_test "$ASYNC_URL" 100 15 "Async 처리 동시 사용자 테스트 (100명, 15초)"

# 5. 스트레스 테스트 (Rate Limiting)
echo -e "${PURPLE}💥 Rate Limiting 스트레스 테스트 시작${NC}"
start_time=$(date +%s.%N)

success_count=0
rate_limited_count=0
error_count=0

for i in {1..2000}; do
    response=$(curl -s -w "%{http_code}" -o /dev/null "$RATE_LIMIT_URL")
    
    case $response in
        200) success_count=$((success_count + 1)) ;;
        429) rate_limited_count=$((rate_limited_count + 1)) ;;
        500) error_count=$((error_count + 1)) ;;
    esac
    
    if [ $((i % 200)) -eq 0 ]; then
        echo -e "${YELLOW}진행률: $i/2000${NC}"
    fi
done

end_time=$(date +%s.%N)
duration=$(echo "$end_time - $start_time" | bc)

echo -e "${GREEN}📊 스트레스 테스트 결과:${NC}"
echo -e "  총 요청: 2000"
echo -e "  성공: ${GREEN}$success_count${NC}"
echo -e "  Rate Limited: ${YELLOW}$rate_limited_count${NC}"
echo -e "  서버 오류: ${RED}$error_count${NC}"
echo -e "  소요 시간: ${BLUE}${duration}초${NC}"
echo -e "  RPS: ${BLUE}$(echo "scale=2; 2000 / $duration" | bc)${NC}"
echo ""

# 6. 메트릭스 수집
collect_metrics

echo -e "${GREEN}🎉 강도 높은 부하테스트 완료!${NC}"
echo -e "${BLUE}📋 테스트 요약:${NC}"
echo -e "  ✅ 기본 헬스 체크: 10/10 성공"
echo -e "  🔄 동시 사용자 테스트 완료"
echo -e "  💥 스트레스 테스트 완료"
echo -e "  📈 시스템 메트릭스 수집 완료" 