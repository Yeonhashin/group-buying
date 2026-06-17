import xml.etree.ElementTree as ET
import anthropic
import os
import sys


def parse_jacoco(xml_path: str) -> dict:
    """JaCoCo XML에서 클래스별 라인 커버리지 추출"""
    if not os.path.exists(xml_path):
        return {}

    tree = ET.parse(xml_path)
    root = tree.getroot()

    coverage = {}
    for package in root.findall('.//package'):
        for cls in package.findall('class'):
            name = cls.get('name').replace('/', '.')
            counters = cls.findall('counter[@type="LINE"]')
            if counters:
                missed = int(counters[0].get('missed'))
                covered = int(counters[0].get('covered'))
                total = missed + covered
                rate = (covered / total * 100) if total > 0 else 100
                coverage[name] = {
                    'rate': round(rate, 1),
                    'missed_lines': missed,
                    'total_lines': total
                }
    return coverage


def get_uncovered_line_numbers(xml_path: str, class_name: str) -> list:
    """특정 클래스의 미커버 라인 번호 추출"""
    tree = ET.parse(xml_path)
    root = tree.getroot()

    short_name = class_name.split('.')[-1]
    uncovered = []

    for package in root.findall('.//package'):
        for cls in package.findall('class'):
            if cls.get('name', '').endswith(short_name):
                for line in cls.findall('.//line'):
                    if int(line.get('ci', 0)) == 0:  # covered instructions = 0
                        uncovered.append(int(line.get('nr')))
    return uncovered


def analyze_with_ai(class_name: str, current: float, previous: float, missed: int, uncovered_lines: list) -> str:
    """커버리지 하락 클래스를 Claude API로 분석"""
    client = anthropic.Anthropic(api_key=os.environ['ANTHROPIC_API_KEY'])

    lines_info = f"미커버 라인 번호: {uncovered_lines[:15]}" if uncovered_lines else "라인 정보 없음"

    response = client.messages.create(
        model="claude-sonnet-4-5",
        max_tokens=1000,
        messages=[{
            "role": "user",
            "content": f"""다음은 Spring Boot 프로젝트의 테스트 커버리지 변화 정보야.

클래스: {class_name}
커버리지 변화: {previous}% → {current}%
미커버 라인 수: {missed}개
{lines_info}

이 정보를 기반으로:
1. 어떤 종류의 테스트 케이스가 누락되었을 가능성이 높은지 추정해줘 (조건문, 예외 처리, 경계값 등)
2. 일반적인 Spring Boot + JUnit5 패턴으로 추가하면 좋을 테스트 케이스 2~3개를 제목만 간단히 제안해줘
3. 너무 길게 설명하지 말고 핵심만 간결하게 정리해줘 (markdown 형식)"""
        }]
    )
    return response.content[0].text


def main():
    if len(sys.argv) < 2:
        print("사용법: python analyze_coverage.py <current_xml> [previous_xml]")
        sys.exit(1)

    current_xml = sys.argv[1]
    previous_xml = sys.argv[2] if len(sys.argv) > 2 else None

    current = parse_jacoco(current_xml)
    previous = parse_jacoco(previous_xml) if previous_xml else {}

    results = []
    threshold = 5.0  # 5% 이상 하락 시에만 분석

    for cls, data in current.items():
        prev_data = previous.get(cls)
        prev_rate = prev_data['rate'] if prev_data else data['rate']
        drop = prev_rate - data['rate']

        if drop >= threshold:
            print(f"⬇️ 커버리지 하락 감지: {cls}: {prev_rate}% → {data['rate']}%")
            uncovered = get_uncovered_line_numbers(current_xml, cls)
            analysis = analyze_with_ai(cls, data['rate'], prev_rate, data['missed_lines'], uncovered)
            results.append(f"### `{cls}`\n**커버리지: {prev_rate}% → {data['rate']}%** (-{round(drop, 1)}%)\n\n{analysis}")

    output_path = 'coverage_analysis.md'
    with open(output_path, 'w', encoding='utf-8') as f:
        if results:
            f.write("## 🤖 AI 커버리지 분석 리포트\n\n")
            f.write('\n\n---\n\n'.join(results))
        else:
            f.write("## 🤖 AI 커버리지 분석 리포트\n\n✅ 커버리지 하락이 감지되지 않았습니다.")

    print(f"분석 결과가 {output_path}에 저장되었습니다.")


if __name__ == '__main__':
    main()