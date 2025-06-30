# ParkMate Notification Service

ParkMate 주차 서비스의 알림 전송을 담당하는 마이크로서비스입니다. 예약 생성, 주차 이력 등 다양한 이벤트에 대한 실시간 알림을 FCM(Firebase Cloud Messaging)을 통해 전송합니다.

## 🚀 주요 기능

- **실시간 알림 전송**: FCM을 통한 푸시 알림 전송
- **이벤트 기반 처리**: Kafka를 통한 비동기 이벤트 처리
- **예약 알림**: 예약 완료 시 사용자 및 호스트에게 알림
- **주차 이력 알림**: 입차/출차 알림 및 출차 시간 리마인더
- **알림 관리**: 알림 조회, 읽음 처리, 삭제 기능
- **사용자 토큰 관리**: FCM 토큰 저장 및 관리
- **부하분산 처리**: Rate Limiting, Circuit Breaker, 캐싱 적용
- **모니터링**: Prometheus 메트릭 수집 및 Actuator 엔드포인트

## 🏗️ 아키텍처

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Reservation   │    │ User Parking    │    │   User Service  │
│     Service     │    │   History       │    │                 │
└─────────┬───────┘    └─────────┬───────┘    └─────────┬───────┘
          │                      │                      │
          └──────────────────────┼──────────────────────┘
                                 │
                    ┌─────────────▼─────────────┐
                    │         Kafka             │
                    │   (Event Streaming)       │
                    └─────────────┬─────────────┘
                                  │
                    ┌─────────────▼─────────────┐
                    │   Notification Service    │
                    │                           │
                    │  ┌─────────────────────┐  │
                    │  │   Event Processor   │  │
                    │  └─────────────────────┘  │
                    │  ┌─────────────────────┐  │
                    │  │   FCM Service       │  │
                    │  └─────────────────────┘  │
                    │  ┌─────────────────────┐  │
                    │  │   MongoDB           │  │
                    │  └─────────────────────┘  │
                    │  ┌─────────────────────┐  │
                    │  │   Redis Cache       │  │
                    │  └─────────────────────┘  │
                    └───────────────────────────┘
                                  │
                    ┌─────────────▼─────────────┐
                    │         FCM              │
                    │   (Firebase Cloud        │
                    │    Messaging)            │
                    └───────────────────────────┘
```

## 🛠️ 기술 스택

### Backend
- **Java 17**
- **Spring Boot 3.4.3**
- **Spring Cloud 2024.0.1**
- **Spring Data MongoDB**
- **Spring Data JPA**
- **Spring Kafka**
- **Spring WebFlux**

### Database & Cache
- **MongoDB** (알림 데이터)
- **MySQL** (사용자 토큰)
- **Redis** (캐싱)

### Message Queue
- **Apache Kafka**

### Cloud Services
- **Firebase Cloud Messaging (FCM)**
- **Spring Cloud Netflix Eureka Client**

### Resilience & Monitoring
- **Resilience4j** (Circuit Breaker, Rate Limiter)
- **Bucket4j** (Rate Limiting)
- **Micrometer** (메트릭 수집)
- **Prometheus** (모니터링)

### Build Tool
- **Gradle**

## 📁 프로젝트 구조

```
src/main/java/com/parkmate/notificationservice/
├── common/                          # 공통 설정 및 유틸리티
│   ├── config/                      # 설정 클래스들
│   │   ├── AsyncConfig.java         # 비동기 스레드 풀 설정
│   │   ├── WebClientConfig.java     # WebClient 설정
│   │   ├── RateLimitConfig.java     # Rate Limiting 설정
│   │   ├── ResilienceConfig.java    # Circuit Breaker 설정
│   │   ├── CacheConfig.java         # Redis 캐싱 설정
│   │   ├── MonitoringConfig.java    # 모니터링 설정
│   │   └── WebMvcConfig.java        # WebMvc 설정
│   ├── interceptor/                 # 인터셉터
│   │   └── RateLimitInterceptor.java # Rate Limiting 인터셉터
│   ├── exception/                   # 예외 처리
│   └── response/                    # 공통 응답 형식
├── kafka/                           # Kafka 관련
│   ├── config/                      # Kafka 설정
│   ├── consumer/                    # Kafka 컨슈머
│   └── constant/                    # Kafka 상수
├── notification/                    # 알림 도메인
│   ├── application/                 # 애플리케이션 서비스
│   ├── domain/                      # 도메인 로직
│   ├── dto/                         # 데이터 전송 객체
│   ├── infrastructure/              # 인프라스트럭처
│   └── presentation/                # 컨트롤러
├── notificationsender/              # 알림 전송
│   └── firebase/                    # FCM 서비스
├── scheduler/                       # 스케줄러
└── usertoken/                       # 사용자 토큰 관리
```

## 🚀 시작하기

### Prerequisites

- Java 17+
- Gradle 8.0+
- MongoDB
- MySQL
- Redis
- Apache Kafka
- Firebase 프로젝트

### 환경 설정

1. **환경 변수 설정**
```bash
# application.yml 또는 환경 변수로 설정
SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092
SPRING_DATA_MONGODB_URI=mongodb://localhost:27017/parkmate
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/parkmate
REDIS_HOST=localhost
REDIS_PORT=6379
FIREBASE_CREDENTIALS_PATH=/path/to/firebase-credentials.json
```

2. **Firebase 설정**
   - Firebase 프로젝트 생성
   - 서비스 계정 키 다운로드
   - `firebase-credentials.json` 파일을 프로젝트 루트에 배치

### 실행 방법

1. **로컬 실행**
```bash
./gradlew bootRun
```

2. **Docker 실행**
```bash
docker-compose -f docker-compose-notification.yml up -d
```

3. **빌드 후 실행**
```bash
./gradlew build
java -jar build/libs/parkmate-notification-service-0.0.1-SNAPSHOT.jar
```

## 📡 API 문서

서비스 실행 후 Swagger UI를 통해 API 문서를 확인할 수 있습니다:
- **URL**: `http://localhost:8080/swagger-ui.html`

### 주요 API 엔드포인트

#### 알림 관리
- `GET /api/v1/notifications` - 알림 목록 조회
- `PUT /api/v1/notifications/{id}/read` - 알림 읽음 처리
- `DELETE /api/v1/notifications/{id}` - 알림 삭제
- `GET /api/v1/notifications/unread-count` - 읽지 않은 알림 개수

#### 사용자 토큰 관리
- `POST /api/v1/user-tokens` - FCM 토큰 저장

#### 모니터링 엔드포인트
- `GET /actuator/health` - 헬스 체크
- `GET /actuator/metrics` - 메트릭 정보
- `GET /actuator/prometheus` - Prometheus 메트릭

## 🔄 이벤트 처리

### 지원하는 이벤트

1. **예약 생성 이벤트** (`reservation-created`)
   - 예약 완료 시 사용자 및 호스트에게 알림
   - 예약 정보 포함 (차량번호, 주차장, 시간 등)

2. **주차 이력 이벤트** (`user-parking-history`)
   - 입차 시 즉시 알림
   - 출차 10분 전 리마인더 알림

### 이벤트 처리 흐름

```
Kafka Event → Event Processor → Notification Creation → DB Save → Schedule → FCM Send
```

## ⚙️ 설정

### 스레드 풀 설정

```java
// AsyncConfig.java
- 일반 작업: 200 스레드
- DB 작업: 100 스레드  
- FCM 전송: 50 스레드
- 스케줄러: 200 스레드
```

### Kafka 설정

```java
// 배치 처리
- MAX_POLL_RECORDS: 500
- Concurrency: 3
- Batch Listener: true
```

### WebClient 설정

```java
// 연결 풀
- Max Connections: 500
- Pending Acquire Max Count: 5000
- Response Timeout: 30초
```

### Rate Limiting 설정

```yaml
# API 요청: 분당 1000개
# FCM 전송: 분당 500개
# 외부 서비스 호출: 분당 200개
```

### Circuit Breaker 설정

```yaml
### 주요 메트릭
- 알림 생성 성공률
- FCM 전송 성공률
- Kafka 메시지 처리량
- 응답 시간

## 🧪 테스트

### 단위 테스트 실행
```bash
./gradlew test
```

### 통합 테스트 실행
```bash
./gradlew integrationTest
```

## 🐳 Docker

### 이미지 빌드
```bash
docker build -t parkmate-notification-service .
```

### 컨테이너 실행
```bash
docker run -p 8080:8080 parkmate-notification-service
```

## 🔧 개발 가이드

### 새로운 알림 타입 추가

1. **이벤트 클래스 생성**
```java
public class NewEvent extends NotificationEvent {
    // 이벤트 데이터
}
```

2. **이벤트 프로세서 구현**
```java
@Component
public class NewEventProcessor implements EventProcessor<NewEvent> {
    @Override
    public CompletableFuture<List<Notification>> create(NewEvent event) {
        // 알림 생성 로직
    }
}
```

3. **Kafka 컨슈머 추가**
```java
@KafkaListener(topics = "new-event-topic")
public void consumeNewEvent(List<NewEvent> events) {
    eventHandler.handleEvent(events);
}
```

### 코드 컨벤션

- **패키지 구조**: 도메인 중심의 계층형 구조
- **네이밍**: 명확하고 일관된 네이밍 컨벤션
- **예외 처리**: 공통 예외 핸들러 사용
- **로깅**: 구조화된 로깅 사용

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다. 자세한 내용은 `LICENSE` 파일을 참조하세요.

## 📞 문의

프로젝트에 대한 문의사항이 있으시면 이슈를 생성해 주세요.

---

**ParkMate Notification Service** - 안정적이고 확장 가능한 알림 서비스 
