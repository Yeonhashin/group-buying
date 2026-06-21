# AI 기반 테스트 자동화 스크립트

이 폴더는 테스트 파이프라인에 AI 분석을 추가하기 위한 Python 스크립트를 담고 있습니다.
GitHub Actions 안에서 자동으로 실행되며, 각각 독립적으로 로컬에서도 실행할 수 있습니다.

## 구성

| 스크립트 | 역할 | 트리거 시점 |
|---|---|---|
| `analyze_coverage.py` | 테스트 커버리지 하락을 감지하고 원인을 분석 | 매 테스트 실행 후 |
| `suggest_tests.py` | PR의 코드 변경사항(diff)을 분석하여 필요한 테스트 케이스 제안 | PR 생성/업데이트 시 |

두 스크립트 모두 [Anthropic API](https://docs.anthropic.com)를 호출하여 Claude가 직접 분석한 결과를 마크다운 파일로 저장하고, GitHub Actions에서 PR 코멘트로 자동 등록합니다.

## 1. analyze_coverage.py — 커버리지 하락 분석

### 동작 방식

1. JaCoCo가 생성한 커버리지 XML 두 개(현재, 이전)를 비교
2. 클래스별 커버리지가 임계치(5%) 이상 하락한 경우만 선별
3. 하락한 클래스의 미커버 라인 정보를 Claude API에 전달
4. 누락되었을 가능성이 높은 테스트 케이스를 분석받아 `coverage_analysis.md`에 저장

### 로컬 실행

\`\`\`bash
export ANTHROPIC_API_KEY="발급받은 키"

# 백엔드 테스트 실행 (커버리지 XML 생성)
cd group-buying-backend
./gradlew test jacocoTestReport
cd ..

# 이전 버전 XML이 있다면 비교 분석
python scripts/analyze_coverage.py \
group-buying-backend/build/reports/jacoco/test/jacocoTestReport.xml \
[이전 버전 XML 경로]

# 이전 버전이 없으면 (최초 실행)
python scripts/analyze_coverage.py \
group-buying-backend/build/reports/jacoco/test/jacocoTestReport.xml
\`\`\`

### 출력 예시

\`\`\`markdown
## 🤖 AI 커버리지 분석 리포트

### `com.hayeon.groupbuy.domain.user.service.UserService`
**커버리지: 100.0% → 94.1%** (-5.9%)

## 1. 누락 가능성이 높은 테스트
- 예외 처리 분기 누락
- null 체크 또는 유효성 검증 분기 누락

## 2. 추가 권장 테스트 케이스
1. 존재하지_않는_사용자_조회시_예외_발생_테스트
2. null_또는_빈값_입력시_유효성_검증_실패_테스트
   \`\`\`

## 2. suggest_tests.py — 코드 변경 기반 테스트 제안

### 동작 방식

1. `git diff`로 base 브랜치와의 변경사항을 Java 파일 기준으로 추출
2. 변경된 코드(diff)를 Claude API에 전달
3. 실질적인 로직 변경이 있는지 판단하고, 있다면 테스트 케이스를 제안받아 `test_suggestions.md`에 저장

단순 주석 추가나 포매팅 변경처럼 테스트가 불필요한 변경은 "테스트 불필요"로 판단하도록 프롬프트에 명시되어 있습니다.

### 로컬 실행

\`\`\`bash
export ANTHROPIC_API_KEY="발급받은 키"

git fetch origin main
python scripts/suggest_tests.py origin/main
\`\`\`

base 브랜치를 지정하지 않으면 기본값으로 `origin/main`을 사용합니다.

### 출력 예시

\`\`\`markdown
## 🤖 AI 테스트 케이스 제안

**변경된 파일:** 1개

## 1. 테스트가 필요한 변경 로직 요약
- 회원가입 로직에 새로운 검증 분기 추가됨

## 2. 권장 테스트 케이스
- 정상적인_회원가입_성공_테스트
- 이미_존재하는_이메일로_회원가입_시도시_예외_발생
  \`\`\`

## GitHub Actions 연동

두 스크립트는 `.github/workflows/test.yml`의 `backend-test` job 안에서 순서대로 실행됩니다.

\`\`\`
PR 생성/업데이트
│
├─ [1] suggest_tests.py 실행 → PR 코멘트 등록 (테스트 제안)
│
├─ 백엔드 유닛/통합 테스트 실행 (JaCoCo 리포트 생성)
│
└─ [2] analyze_coverage.py 실행 → PR 코멘트 등록 (커버리지 분석)
\`\`\`

`ANTHROPIC_API_KEY`는 GitHub Secrets에 등록되어 있으며, job 레벨 환경변수로 공유됩니다.

## 필요 환경

- Python 3.11+
- `pip install anthropic`
- `ANTHROPIC_API_KEY` 환경변수

## 설계 의도

이 스크립트들은 "AI가 테스트 코드를 대신 작성해준다"는 개념이 아니라, **사람이 놓칠 수 있는 지점을 AI가 짚어주는 보조 도구**로 설계했습니다. 핵심 도메인 로직의 테스트 시나리오 설계와 작성은 직접 수행했고, AI는 다음 역할만 담당합니다.

- 커버리지가 떨어진 원인을 빠르게 추정
- 코드 변경 시 놓칠 수 있는 테스트 케이스를 제안
- 실질적 로직 변경이 없는 PR(주석, 포매팅 등)은 분석을 건너뛰도록 판단