# 공동구매 프로젝트 — AI 기반 테스트 자동화 파이프라인

> Spring Boot + React 기반 공동구매 서비스에 AI를 활용한 테스트 자동화 파이프라인을 구축한 포트폴리오 프로젝트입니다.

---

## 프로젝트 개요

단순히 "AI가 테스트를 대신 작성해준다"는 방식이 아니라, **사람이 테스트 전략을 설계하고 핵심 시나리오를 직접 작성하되, AI가 두 가지 자동화 분석을 보조**하는 구조를 목표로 했습니다.

```
테스트 전략 설계 및 핵심 시나리오 작성 → 사람
보일러플레이트 확장 및 엣지 케이스 제안 → AI
커버리지 하락 감지 및 분석 → AI 자동화
PR 코드 변경 기반 테스트 케이스 제안 → AI 자동화
```

---

## 기술 스택

| 분류 | 기술 |
|---|---|
| 백엔드 | Spring Boot 3, JPA, Spring Security, Redis |
| 프론트엔드 | React, Vite, TanStack Query |
| 테스트 | JUnit5, Mockito, H2(통합), Playwright(E2E) |
| CI/CD | GitHub Actions |
| AI 자동화 | Anthropic Claude API (claude-sonnet-4-5) |

---

## 테스트 전략

### 레이어별 테스트 구성

```
유닛 테스트 (JUnit5 + Mockito)
  └ 각 Service 메서드 단위 검증
  └ 외부 의존성(Repository, Redis 등) 전부 Mock 처리
  └ 비즈니스 로직 정확성에 집중

통합 테스트 (SpringBootTest + H2)
  └ 실제 DB(H2 인메모리)와 Bean을 사용한 전체 흐름 검증
  └ Redis는 TestRedisConfig의 Mock 빈으로 대체
  └ 도메인 간 연결 흐름 확인 (공동구매 → 주문 → 알림)

E2E 테스트 (Playwright)
  └ 실제 브라우저에서 사용자 시나리오 전체 검증
  └ 화면이 존재하는 기능만 대상
  └ 테스트 과정에서 실제 버그 3개 발견 및 수정
```

### 도메인별 테스트 현황

| 도메인 | 유닛 | 통합 | E2E |
|---|---|---|---|
| Auth (로그인/회원가입) | 13개 | 5개 | 7개 |
| Product (상품) | 17개 | 6개 | 5개 |
| GroupPurchase (공동구매) | 31개 | 8개 | 6개 |
| Order (주문) | 15개 | - | 5개 |
| Notification (알림) | 15개 | - | - |
| **합계** | **91개** | **19개** | **23개** |

---

## AI 활용 범위

AI가 모든 테스트를 생성한 것이 아닙니다. 아래 표로 구분합니다.

| 항목 | 사람 | AI |
|---|---|---|
| 테스트 전략 수립 | ✅ | |
| 핵심 도메인 시나리오 설계 | ✅ | |
| 유닛/통합/E2E 테스트 작성 | ✅ | |
| 보일러플레이트 코드 확장 | | ✅ (제안) |
| 경계값/엣지 케이스 제안 | | ✅ (제안, 선별 반영) |
| 커버리지 하락 분석 | | ✅ (자동화) |
| PR 코드 변경 테스트 제안 | | ✅ (자동화) |

### AI 제안 선별 기준

AI가 제안한 테스트 케이스를 무조건 반영하지 않고, 다음 기준으로 선별했습니다.

- 기존 테스트와 중복되는 케이스 → 제외
- 실제 구현 코드에 없는 로직 검증 → 제외
- 의미 있는 경계값이나 누락된 케이스 → 반영

각 PR 코멘트에 "AI 제안 검토 결과"를 남겨 어떤 것을 반영하고 어떤 것을 제외했는지 기록했습니다.

---

## AI 자동화 스크립트

자세한 내용은 [scripts/README.md](scripts/README.md)를 참고하세요.

### analyze_coverage.py
- JaCoCo XML 두 개(현재/이전)를 비교해 커버리지 하락 감지
- 하락한 클래스의 미커버 라인을 Claude API에 전달해 원인 분석
- 결과를 PR 코멘트로 자동 등록

### suggest_tests.py
- `git diff`로 PR의 Java 파일 변경사항 추출
- 실질적인 로직 변경이 있는지 판단 후 테스트 케이스 제안
- 주석만 추가된 변경은 "테스트 불필요"로 정확히 판단한 사례 확인

---

## GitHub Actions 파이프라인

```
PR 생성/업데이트
    │
    ├─ [backend-test job]
    │   ├─ suggest_tests.py 실행 → PR 코멘트 (테스트 제안)
    │   ├─ JUnit 테스트 실행 + JaCoCo 리포트 생성
    │   └─ analyze_coverage.py 실행 → PR 코멘트 (커버리지 분석)
    │
    └─ [frontend-test job]
        ├─ 백엔드 빌드 후 e2e 프로파일로 실행
        └─ Playwright 테스트 실행
```

---

## 테스트를 통해 발견한 실제 버그

### Bug 1 — 회원가입 후 페이지 미이동
- **발견 방법**: auth.spec.js E2E 테스트
- **원인**: `useRegisterForm.js`에서 회원가입 성공 후 navigate() 호출 누락
- **수정**: 회원가입 완료 후 로그인 페이지로 이동 처리 추가

### Bug 2 — 로그인 실패 시 전체 페이지 리로드
- **발견 방법**: auth.spec.js E2E 테스트
- **원인**: `apiClient.js`의 401/403 핸들러가 로그인 요청 자체도 리다이렉트
- **수정**: `skipAuthRedirect` 옵션 추가, 로그인 API에서 리다이렉트 제외

### Bug 3 — SecurityConfig 경로 오타
- **발견 방법**: group-purchase.spec.js E2E 테스트
- **원인**: `/api/groupPurchase/**`(카멜케이스)로 설정되어 실제 경로 `/api/group-purchases/**`에 미적용
- **결과**: 비로그인 사용자의 공동구매 목록/상세 접근 시 403 반환
- **수정**: Security 경로 오타 수정

### Bug 4 — Redis 미연결 시 공동구매 목록 API 500 에러
- **발견 방법**: group-purchase.spec.js E2E 테스트
- **원인**: `getCurrentParticipants()`에서 Redis 연결 실패 시 예외가 그대로 전파
- **수정**: try-catch 추가, Redis 실패 시 참여자 수 0으로 fallback 처리

---

## 트러블슈팅 기록

### 1. Testcontainers Windows 환경 문제
- **상황**: 통합 테스트에서 MySQL Testcontainers 사용 시도
- **문제**: Windows의 Docker Desktop npipe/TCP-2375 설정 충돌로 반복 실패
- **해결**: Testcontainers 포기, H2 인메모리 DB + `MODE=MySQL`로 전환
- **교훈**: CI 환경(Linux)에서는 Testcontainers가 정상 동작하지만, 개발 환경(Windows)에서 추가 설정 필요

### 2. JaCoCo @Disabled 테스트 커버리지 계산 방식
- **상황**: 커버리지를 의도적으로 낮추려고 테스트에 `@Disabled` 추가
- **문제**: JaCoCo는 `@Disabled`된 테스트를 분모에서 아예 제외 → 커버리지 변화 없음
- **해결**: 프로덕션 코드에 실제로 테스트되지 않는 분기를 추가해서 검증
- **교훈**: JaCoCo 커버리지는 "실행된 코드 라인 수 / 전체 코드 라인 수"로 계산됨

### 3. Gradle UP-TO-DATE 캐시로 테스트 재실행 안 됨
- **상황**: 테스트 코드 수정 후 `./gradlew test` 실행 시 "UP-TO-DATE"로 스킵
- **해결**: `./gradlew clean test` 또는 `--rerun` 옵션 사용
- **교훈**: Gradle은 입력/출력이 변경되지 않으면 태스크를 캐시에서 반환

### 4. GitHub Actions `working-directory`가 `uses:` step에 미적용
- **상황**: `defaults: run: working-directory: group-buying-backend` 설정 후 액션 step에도 적용되길 기대
- **문제**: `working-directory`는 `run:` 명령어에만 적용, `uses:` 액션에는 무효
- **해결**: `run:` step에만 명시적으로 `working-directory: .` 추가

### 5. GitHub Actions PR 코멘트 권한 오류
- **상황**: `marocchino/sticky-pull-request-comment` 실행 시 "Resource not accessible by integration" 에러
- **원인**: GitHub Actions 기본 토큰에 PR 코멘트 작성 권한 없음
- **해결**: workflow 최상단에 `permissions: pull-requests: write` 추가

### 6. suggest_tests.py 빈 파일로 커밋
- **상황**: 파일 생성 직후 내용 입력 전에 `git add` 실행
- **문제**: `e69de29` 해시(git 빈 파일 고정 해시)로 커밋됨, GitHub Actions에서 스크립트 실행해도 아무 동작 없음
- **해결**: 내용을 채운 후 다시 커밋
- **교훈**: `git diff` 결과에서 `e69de29`가 보이면 빈 파일로 커밋된 것

### 7. Playwright `fill()` vs React state 레이스 컨디션
- **상황**: `fill()`로 이메일 입력 후 "중복 확인" 클릭 시 "사용 가능한 이메일입니다" 미노출
- **원인**: `useEffect(() => { setEmailChecked(false); }, [email])`가 API 응답보다 늦게 실행되어 상태 리셋
- **해결**: `fill()` 후 `waitForTimeout(500)` 추가로 React state 안정화 대기
- **교훈**: Playwright의 빠른 `fill()`은 사람의 타이핑보다 빨라서 React의 debounce/useEffect와 충돌할 수 있음

### 8. react-hook-form 유효성 검증으로 폼 제출 조용히 실패
- **상황**: 회원가입 E2E에서 닉네임 `상품테스트유저`(7자) 입력 후 회원가입 버튼 클릭해도 alert 미발생
- **원인**: 닉네임 maxLength가 5자로 제한되어 있어 `react-hook-form`이 폼 제출 자체를 막음
- **해결**: 5자 이하 닉네임으로 변경
- **교훈**: alert가 안 뜬다면 폼 유효성 검증 실패를 먼저 의심

### 9. 공동구매 생성자는 자신의 공동구매에 참여 불가
- **상황**: E2E에서 공동구매 생성 후 바로 "참여하기" 버튼 클릭 시도
- **문제**: 생성자는 참여 버튼이 다르게 표시되거나 참여 불가
- **해결**: 별도 계정으로 로그인 후 참여하는 시나리오로 변경

### 10. UnnecessaryStubbingException (short-circuit 평가)
- **상황**: 조건문에서 앞 조건이 true면 뒤 조건을 평가하지 않아 뒤에 설정한 stubbing이 사용되지 않음
- **원인**: Java의 `||` 연산자는 short-circuit 평가 — 앞 조건이 true면 뒤는 실행 안 됨
- **해결**: 사용되지 않을 수 있는 stubbing에 `lenient().when()` 처리

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

### 테스트 실행
```bash
# 백엔드 유닛 + 통합 테스트
cd group-buying-backend
./gradlew clean test jacocoTestReport

# E2E 테스트 (백엔드/프론트엔드 실행 상태에서)
cd group-buying-frontend
npx playwright test --reporter=list
```

### AI 분석 스크립트 로컬 실행
```bash
export ANTHROPIC_API_KEY="발급받은 키"

# 커버리지 분석
python scripts/analyze_coverage.py \
  group-buying-backend/build/reports/jacoco/test/jacocoTestReport.xml

# 코드 변경 감지
git fetch origin main
python scripts/suggest_tests.py origin/main
```