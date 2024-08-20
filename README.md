# 🌟 Artinus Backend 과제 - 구독 서비스 구현 🌟

## 📌 과제 분석

1. 회원 관리 시스템에서 구독 서비스 일부를 구현
2. 회원은 하나의 구독 상태를 가지고, 여러 번 구독하거나 해지 가능
3. 회원의 채널 별 구독일, 구독 해지일을 알 수 있어야 함
4. 다중화 환경에서 동시성 이슈를 고려
5. API 문서는 Swagger로 자동 생성 처리

## 🛠 기술 스택

- 언어: Java ☕
- 빌드 도구: Gradle 🛠️
- 프레임워크: Spring Boot 🍃
- 데이터베이스: MySQL 🐬
- 데이터 접근 기술: Spring Data JPA 🗂️
- API 문서: Swagger 📘

### 📊 테이블 구조

1. **Member 테이블** 👤

- id (PK)
- phone_number (Unique)
- subscription_status (ENUM: 'NONE', 'BASIC', 'PREMIUM')
- created_date
- updated_date

2. **Channel 테이블** 📺

- id (PK)
- name
- type (ENUM: 'FULL_ACCESS', 'SUBSCRIBE_ONLY', 'UNSUBSCRIBE_ONLY')
- created_date
- updated_date

3. **SubscriptionHistory 테이블** 📜

- id (PK)
- member_id (FK)
- channel_id (FK)
- action_type (ENUM: 'SUBSCRIBE', 'UNSUBSCRIBE')
- status (ENUM: 'NONE', 'BASIC', 'PREMIUM')
- action_date

### 🔗 테이블 관계

- Member ↔️ SubscriptionHistory: 1:N (한 멤버는 여러 구독 이력을 가질 수 있음)
- Channel ↔️ SubscriptionHistory: 1:N (한 채널은 여러 구독 이력을 가질 수 있음)

## 📂 프로젝트 구조, 주요 패키지 및 클래스 설명

- **src/main/java**: 주요 소스 코드 디렉터리
    - **controller**: API 엔드포인트를 정의하고 요청을 처리 🎮
        - `SubscriptionController`: 구독 관련 API를 제공
    - **dto**: 데이터 전송 객체를 정의 🚚
        - `SubscriptionHistoryDto`: 구독 이력 정보를 전달하는 데 사용
    - **entity**: 데이터베이스 테이블과 매핑되는 객체 📊
        - `Channel`, `Member`, `SubscriptionHistory`: 각각 채널, 회원, 구독 이력 정보를 나타냄
    - **enums**: ENUM 타입들을 정의 🔢
        - `ActionType`, `ChannelType`, `SubscriptionStatus`: 액션 타입(구독/구독 해제), 채널 타입, 구독 상태를 나타냄
    - **repository**: 데이터베이스 작업을 처리하는 인터페이스 🗄️
        - `ChannelRepository`, `MemberRepository`, `SubscriptionHistoryRepository`: 각 엔티티에 대한 데이터
          접근을 담당
    - **service**: 비즈니스 로직을 처리 🧠
        - `SubscriptionHistoryService`: 구독 이력 관련 로직을 처리
        - `SubscriptionService`: 구독 관련 주요 비즈니스 로직을 처리

- **src/main/resources**: 설정 파일과 DB 스키마 파일 ⚙️
    - `application.properties`: 애플리케이션 설정 정보
    - `schema.sql`: 데이터베이스 스키마를 정의

- **src/test/java**: 테스트 코드 디렉터리 🧪
    - **test**: 테스트 관련 파일
        - `api_test.http`: API 테스트를 위한 HTTP 요청 파일

- **build.gradle**: Gradle 빌드 설정 파일
- **settings.gradle**: 프로젝트 설정 파일
- **README.md**: 프로젝트에 대한 설명과 가이드 문서

## 🔑 주요 구현 내용

### 1. 구독 상태, 채널 타입

- 모두 ENUM으로 처리 🧹
    - 구독 상태: NONE, BASIC, PREMIUM
    - 채널 타입: FULL_ACCESS, SUBSCRIBE_ONLY, UNSUBSCRIBE_ONLY

### 2. 주요 API

- 구독하기 API 🔼
    - INPUT : 휴대폰번호 / 채널 ID / 구독 상태
    - OUTPUT : 성공 및 실패 여부
    - 최초 회원의 구독 상태는 모두 선택 가능
    - 기존과 동일한 상태를 보낼 경우에는 차단
    - 최초 구독 후 업그레이드만 가능 ⏫
        - 구독 안함 → 일반 구독
        - 구독 안함 → 프리미엄 구독
        - 일반 구독 → 프리미엄 구독
- 구독 해지 API 🔽
    - INPUT : 휴대폰 번호 / 채널 ID / 구독 상태
    - OUTPUT : 성공 및 실패 여부
    - 구독하지 않은 상태에서는 구독 해지 불가능
    - 기존과 동일한 상태를 보낼 경우에는 차단
    - 구독 변경은 다운그레이드만 가능 ⏬
        - 구독 안함 → 일반 구독
        - 구독 안함 → 프리미엄 구독
        - 일반 구독 → 프리미엄 구독
- 구독 이력 조회 API 📊
    - INPUT : 휴대폰 번호
    - OUTPUT : 회원의 일자 별, 채널 별, 구독 상태 이력을 조회
- 채널 이력 조회 API 📈
    - INPUT : 일자(yyyymmdd) / 채널 ID
    - OUTPUT : 일자와 채널 ID에 맞는 회원의 구독 이력 조회

### 3. 동시성 제어

- Optimistic Lock을 사용, 성능이 좋고 Entity에 @Version을 추가하는 것으로 구현이 간단함
- 충돌이 자주 일어날 것 같지 않아 Optimistic Lock이 적합해 보임

### 4. API 문서

- Swagger를 이용한 자동화 생성 처리
    - 프로젝트 실행 후 아래 링크에서 API 문서 확인
    - http://localhost:8080/swagger-ui.html
    - SUCCEED / FAILED 케이스는 test/http/api_test.http 파일 참조
        - API 테스트 방법 🔍
            - IntelliJ IDEA를 사용할 경우 이 파일을 열고 각 요청 왼쪽의 ▶️ 버튼을 클릭해서 바로 테스트 가능
            - 각 API별로 성공(SUCCEED) / 실패(FAILED) 케이스를 모두 포함
            - 예를 들어, 구독하기 API의 경우:
                ```http
                ### 성공 케이스 (NONE → BASIC):
                POST http://localhost:8080/api/subscriptions/subscribe?phoneNumber=01011111111&channelId=1&newStatus=BASIC
    
                ### 실패 케이스 (BASIC → NONE, 다운그레이드 불가능한 상태 변경):
                POST http://localhost:8080/api/subscriptions/subscribe?phoneNumber=01011111111&channelId=1&newStatus=NONE
                ```

## 🚀 실행 방법

1. 레포지토리를 클론합니다.
2. MySQL 데이터베이스를 설정합니다.
3. `application.properties` 파일에서 데이터베이스 연결 정보를 수정합니다.
4. 프로젝트를 실행합니다.
5. `http://localhost:8080/swagger-ui.html`에서 API 문서를 확인하고 테스트 합니다.
