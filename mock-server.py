#!/usr/bin/env python3
"""
ParkMate Notification Service 모의 서버
부하테스트를 위한 간단한 HTTP 서버
"""

import time
import json
import threading
from http.server import HTTPServer, BaseHTTPRequestHandler
from urllib.parse import urlparse, parse_qs
import random

# Rate Limiting을 위한 전역 변수
request_count = 0
last_reset = time.time()
RATE_LIMIT = 1000  # 1분당 1000개 요청
RATE_LIMIT_WINDOW = 60  # 60초

class MockNotificationHandler(BaseHTTPRequestHandler):
    
    def __init__(self, *args, **kwargs):
        self.rate_limit_bucket = {}
        super().__init__(*args, **kwargs)
    
    def do_GET(self):
        global request_count, last_reset
        
        # Rate Limiting 체크
        current_time = time.time()
        if current_time - last_reset >= RATE_LIMIT_WINDOW:
            request_count = 0
            last_reset = current_time
        
        request_count += 1
        
        # URL 파싱
        parsed_url = urlparse(self.path)
        path = parsed_url.path
        
        # Rate Limiting 헤더 설정
        self.send_response(200)
        self.send_header('Content-type', 'application/json')
        self.send_header('X-Rate-Limit-Limit', str(RATE_LIMIT))
        self.send_header('X-Rate-Limit-Remaining', str(max(0, RATE_LIMIT - request_count)))
        self.send_header('X-Rate-Limit-Reset', str(int(last_reset + RATE_LIMIT_WINDOW)))
        
        # Rate Limiting 체크
        if request_count > RATE_LIMIT:
            self.send_response(429)
            self.send_header('Content-type', 'application/json')
            self.end_headers()
            response = {
                "error": "Rate limit exceeded",
                "message": "Too many requests",
                "retry_after": int(RATE_LIMIT_WINDOW - (current_time - last_reset))
            }
            self.wfile.write(json.dumps(response).encode())
            return
        
        # 엔드포인트별 응답
        if path == '/test/health':
            response = {
                "status": "UP",
                "timestamp": time.strftime("%Y-%m-%d %H:%M:%S"),
                "service": "ParkMate Notification Service (Mock)",
                "version": "1.0.0"
            }
        elif path == '/test/rate-limit-test':
            # 가끔 지연 시뮬레이션
            if random.random() < 0.1:  # 10% 확률로 지연
                time.sleep(random.uniform(0.1, 0.5))
            
            response = {
                "message": "Rate limit test successful",
                "timestamp": time.strftime("%Y-%m-%d %H:%M:%S"),
                "request_count": request_count,
                "remaining": RATE_LIMIT - request_count
            }
        elif path == '/test/circuit-breaker-test':
            # Circuit Breaker 시뮬레이션 (가끔 실패)
            if random.random() < 0.05:  # 5% 확률로 실패
                self.send_response(500)
                self.send_header('Content-type', 'application/json')
                self.end_headers()
                response = {
                    "error": "Internal server error",
                    "message": "Circuit breaker test failed"
                }
                self.wfile.write(json.dumps(response).encode())
                return
            
            response = {
                "message": "Circuit breaker test successful",
                "timestamp": time.strftime("%Y-%m-%d %H:%M:%S"),
                "status": "healthy"
            }
        elif path == '/test/async':
            # 비동기 처리 시뮬레이션
            time.sleep(random.uniform(0.01, 0.1))
            response = {
                "message": "Async test successful",
                "timestamp": time.strftime("%Y-%m-%d %H:%M:%S"),
                "thread": threading.current_thread().name,
                "processing_time": random.uniform(0.01, 0.1)
            }
        elif path == '/test/metrics':
            import psutil
            response = {
                "timestamp": time.strftime("%Y-%m-%d %H:%M:%S"),
                "memory_usage": psutil.virtual_memory().percent,
                "cpu_usage": psutil.cpu_percent(),
                "request_count": request_count,
                "uptime": time.time() - last_reset
            }
        else:
            self.send_response(404)
            self.send_header('Content-type', 'application/json')
            self.end_headers()
            response = {
                "error": "Not found",
                "message": f"Endpoint {path} not found"
            }
            self.wfile.write(json.dumps(response).encode())
            return
        
        self.end_headers()
        self.wfile.write(json.dumps(response, indent=2).encode())
    
    def log_message(self, format, *args):
        # 로그 출력 비활성화 (부하테스트 시 성능 향상)
        pass

def run_server(port=8080):
    server_address = ('', port)
    httpd = HTTPServer(server_address, MockNotificationHandler)
    print(f"🚀 ParkMate Notification Service 모의 서버 시작")
    print(f"📍 서버 주소: http://localhost:{port}")
    print(f"📊 Rate Limit: {RATE_LIMIT} requests per {RATE_LIMIT_WINDOW} seconds")
    print(f"🔗 사용 가능한 엔드포인트:")
    print(f"   - GET /test/health")
    print(f"   - GET /test/rate-limit-test")
    print(f"   - GET /test/circuit-breaker-test")
    print(f"   - GET /test/async")
    print(f"   - GET /test/metrics")
    print(f"⏹️  서버를 중지하려면 Ctrl+C를 누르세요")
    print("-" * 50)
    
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        print("\n🛑 서버를 중지합니다...")
        httpd.shutdown()

if __name__ == '__main__':
    run_server() 