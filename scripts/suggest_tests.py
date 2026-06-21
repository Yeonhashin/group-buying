import subprocess
import anthropic
import os
import sys


def get_diff(base_branch: str = "origin/main") -> str:
    """현재 브랜치와 base 브랜치 간의 diff를 가져옴 (Java 파일만)"""
    try:
        result = subprocess.run(
            ["git", "diff", f"{base_branch}...HEAD", "--", "*.java"],
            capture_output=True, text=True, check=True
        )
        return result.stdout
    except subprocess.CalledProcessError as e:
        print(f"git diff 실패: {e.stderr}")
        return ""


def get_changed_files(base_branch: str = "origin/main") -> list:
    """변경된 Java 파일 목록"""
    try:
        result = subprocess.run(
            ["git", "diff", "--name-only", f"{base_branch}...HEAD", "--", "*.java"],
            capture_output=True, text=True, check=True
        )
        return [f for f in result.stdout.strip().split("\n") if f]
    except subprocess.CalledProcessError:
        return []


def analyze_diff_with_ai(diff: str, changed_files: list) -> str:
    """diff를 Claude API로 분석하여 테스트 케이스 제안받기"""
    client = anthropic.Anthropic(api_key=os.environ['ANTHROPIC_API_KEY'])

    # diff가 너무 길면 일부만 사용 (토큰 제한 고려)
    truncated_diff = diff[:8000]

    files_list = "\n".join(f"- {f}" for f in changed_files)

    response = client.messages.create(
        model="claude-sonnet-4-5",
        max_tokens=1500,
        messages=[{
            "role": "user",
            "content": f"""다음은 Spring Boot 프로젝트의 PR에서 변경된 Java 코드 diff야.

변경된 파일:
{files_list}

diff 내용:
```diff
{truncated_diff}
```

이 변경사항을 분석해서:
1. 새로 추가되거나 수정된 로직 중 테스트가 필요한 부분을 짧게 요약해줘
2. JUnit5 + Mockito 기준으로 추가하면 좋을 테스트 케이스를 3~5개 제목 형태로 제안해줘 (예외 처리, 경계값, 정상 케이스 등 다양하게)
3. 너무 길게 쓰지 말고 핵심만 markdown 형식으로 간결하게 정리해줘"""
        }]
    )
    return response.content[0].text


def main():
    base_branch = sys.argv[1] if len(sys.argv) > 1 else "origin/main"

    changed_files = get_changed_files(base_branch)

    if not changed_files:
        output = "## 🤖 AI 테스트 케이스 제안\n\n변경된 Java 파일이 없습니다."
    else:
        diff = get_diff(base_branch)
        if not diff.strip():
            output = "## 🤖 AI 테스트 케이스 제안\n\ndiff 내용을 가져올 수 없습니다."
        else:
            analysis = analyze_diff_with_ai(diff, changed_files)
            output = f"## 🤖 AI 테스트 케이스 제안\n\n**변경된 파일:** {len(changed_files)}개\n\n{analysis}"

    with open('test_suggestions.md', 'w', encoding='utf-8') as f:
        f.write(output)

    print("분석 결과가 test_suggestions.md에 저장되었습니다.")


if __name__ == '__main__':
    main()