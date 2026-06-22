[![Backend Test](https://github.com/Yeonhashin/group-buying/actions/workflows/test.yml/badge.svg)](https://github.com/Yeonhashin/group-buying/actions/workflows/test.yml)
[![codecov](https://codecov.io/gh/Yeonhashin/group-buying/branch/main/graph/badge.svg)](https://codecov.io/gh/Yeonhashin/group-buying)
# Group Buying Commerce

Spring Boot와 React를 기반으로 구현한 공동구매 커머스 서비스입니다.

공동구매 참여 시 발생하는 동시성 문제를 해결하기 위해 Redis를 활용하였으며, React Query를 적용하여 서버 상태 관리를 구현했습니다.

**이 프로젝트는 기능 구현 외에도 AI 기반 테스트 자동화 파이프라인 구축에 중점을 두었습니다.**
테스트 전략, AI 활용 범위, 트러블슈팅 상세 내용은 [TESTING.md](TESTING.md)를 참고하세요.

---

## 프로젝트 소개

공동구매 상품을 생성하고, 사용자가 공동구매에 참여하여 목표 인원을 달성하면 구매가 확정되는 서비스입니다.

대량의 참여 요청이 동시에 발생할 수 있는 공동구매 특성을 고려하여 Redis 기반 참여 인원 관리 구조를 설계했습니다.

---

## 기술 스택

### Backend

* Java 17
* Spring Boot 3.3.5
* Spring Security
* Spring Data JPA
* Spring Validation
* JWT
* MySQL
* Redis
* Lombok
* Gradle

### Frontend

* React
* Vite
* Axios
* TanStack React Query

### Infrastructure

* Docker
* Docker Compose

### 테스트 / CI

* JUnit5 + Mockito
* Playwright (E2E)
* GitHub Actions
* Anthropic Claude API (AI 자동화)

---

## 시스템 아키텍처

```text
React
   │
   ▼
Spring Boot API
   │
   ├── MySQL
   │
   └── Redis
```

### 역할

#### MySQL

* 사용자 정보 저장
* 상품 정보 저장
* 공동구매 정보 저장
* 주문 정보 저장
* 참여 이력 저장

#### Redis

* 공동구매 참여 인원 캐시
* 동시성 제어
* 빠른 참여 인원 조회

---

## 주요 기능

### 1. 회원 기능

* 회원가입
* 로그인
* JWT 인증
* 사용자별 주문 조회

### 2. 상품 기능

* 상품 등록 / 수정 / 삭제 (이미지 업로드 포함)
* 상품 목록 조회 / 검색
* 상품 상세 조회

### 3. 공동구매 기능

* 공동구매 생성 / 수정
* 공동구매 목록 / 상세 조회
* 공동구매 참여 / 취소 (Redis 기반 동시성 제어)

### 4. 주문 기능

* 주문 생성 / 결제 / 주문 취소 (48시간 제한)
* 내 주문 목록 조회
* 주문 상태 조회

### 5. 알림 기능

* 주문 생성 / 결제 / 취소 시 자동 알림
* 읽음 / 안읽음 처리

### 6. 마이페이지

* 참여 공동구매 조회
* 주문 내역 조회 / 결제 / 취소

---

## Redis 기반 참여 처리 설계

공동구매 서비스의 핵심 문제는 동시 참여 요청입니다.

예를 들어 모집 인원이 100명일 때 100명 이상이 동시에 참여 요청을 보내면 데이터 정합성이 깨질 수 있습니다.

이를 해결하기 위해 Redis를 활용했습니다.

### Redis Key

```text
group_purchase:{id}:count
```

### 참여 처리 흐름

```text
사용자 참여 요청
      │
      ▼
Redis INCR (Lua Script - Atomic)
      │
      ▼
목표 인원 초과 여부 확인
      │
      ├── 초과 → 실패 (Redis DECR 롤백)
      │
      └── 성공
              │
              ▼
      DB 참여 정보 저장
              │
      (실패 시 Redis DECR + RedisFailLog 저장)
```

### 적용 기술

* Redis Atomic INCR
* Lua Script
* DB + Cache 보상 트랜잭션

---

## 공동구매 상태 관리

공동구매는 다음 상태를 가집니다.

```text
RECRUITING → COMPLETED (목표 인원 달성)
RECRUITING → FAILED    (기간 종료 + 인원 미달)
```

스케줄러를 사용하여 종료 시간과 참여 인원을 기준으로 상태를 변경합니다.

---

## React Query 적용

서버 상태 관리를 위해 React Query를 사용했습니다.

### 적용 내용

* Query Cache / Mutation / Query Invalidation

### 효과

* 불필요한 API 호출 감소
* 사용자 경험 향상
* 서버 상태 동기화 단순화

---

## AI 기반 테스트 자동화 파이프라인

이 프로젝트의 핵심 중 하나는 **AI를 활용한 테스트 자동화**입니다.

### 테스트 현황

| 분류 | 테스트 수 |
|---|---|
| 유닛 테스트 (JUnit5 + Mockito) | 91개 |
| 통합 테스트 (SpringBootTest + H2) | 19개 |
| E2E 테스트 (Playwright) | 23개 |
| **합계** | **133개** |

### AI 자동화 구성

```
PR 생성/업데이트
    │
    ├─ suggest_tests.py  → PR diff 분석 → 테스트 케이스 제안 코멘트
    ├─ JUnit 테스트 실행 + JaCoCo 커버리지 측정
    └─ analyze_coverage.py → 커버리지 하락 분석 → PR 코멘트
```

### 테스트를 통해 발견한 실제 버그

* 회원가입 후 페이지 미이동
* 로그인 실패 시 전체 페이지 리로드
* SecurityConfig 경로 오타 (비로그인 공동구매 목록 403)
* Redis 미연결 시 공동구매 목록 API 500 에러

> 상세 내용은 [TESTING.md](TESTING.md) 참고

---

## 프로젝트 구조

### Backend

```text
domain
 ├─ auth
 ├─ user
 ├─ product
 ├─ groupPurchase
 │   ├─ compensation  (Redis 보상 트랜잭션)
 │   ├─ participation
 │   ├─ redis
 │   └─ scheduler
 ├─ order
 └─ notification
global
 ├─ exception
 ├─ response
 └─ security
```

### Frontend

```text
src
 ├─ api
 ├─ assets
 ├─ components
 ├─ hooks
 ├─ pages
 ├─ services
 └─ store
```

---

## 로컬 실행 방법

### 백엔드

```bash
cd group-buying-backend
./gradlew bootRun --args='--spring.profiles.active=test,e2e'
```

### 프론트엔드

```bash
cd group-buying-frontend
npm install
npm run dev
```

### E2E 테스트 (백엔드/프론트엔드 실행 상태에서)

```bash
cd group-buying-frontend
npx playwright test --reporter=list
```

---

## 트러블 슈팅

### 공동구매 참여 동시성 문제

#### 문제

동시에 여러 사용자가 참여할 경우 참여 인원이 실제보다 많이 증가할 수 있음.

#### 해결

Redis Atomic 연산과 Lua Script를 적용하여 참여 인원 증가를 원자적으로 처리.
DB 저장 실패 시 Redis 카운트를 DECR로 롤백하는 보상 트랜잭션 구현.

---

### 서버 상태 관리 복잡도 증가

#### 문제

참여 후 화면 데이터를 직접 수정해야 하는 코드가 증가.

#### 해결

React Query의 Mutation + Invalidate 패턴을 적용하여 데이터 동기화를 단순화.

---

> 테스트 관련 트러블슈팅 (Testcontainers Windows 문제, JaCoCo 계산 방식, Playwright 레이스 컨디션 등) 은 [TESTING.md](TESTING.md)에 상세히 기록되어 있습니다.

---

## 향후 개선 계획

* Kafka 기반 이벤트 처리
* 실시간 알림 기능
* 결제 시스템 연동
* 대규모 트래픽 부하 테스트
* 모니터링 시스템 적용