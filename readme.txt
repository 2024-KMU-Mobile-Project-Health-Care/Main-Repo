About the Project

이 앱은 환자의 증상을 시각적으로 관리할 수 있는 앱입니다.
사용자는 증상을 GUI로 입력하여 기록할 수 있으며, GPT API를 활용해 입력된 증상을 요약하고 예상 병명을 출력합니다.
이를 통해 환자가 의사에게 방문하기 전에 증상을 요약하여 제시하거나, 스스로 병명을 예측할 수 있습니다.


Team Members

- 성재승: 팀장, 증상 GUI 입력 부분 및 DB 연동 작업 담당
- 류재우: GPT 통신 담당, 요약 정보 생성 및 병명 예측 개발
- 제윤선: 메인 화면 UI 개발 담당, 아이콘 등 시각적 요소 개발
- 김호국: 투약 관리 기능 개발 담당


Requirements

- 안드로이드 버전: API 10 이상
- 외부 라이브러리: Retrofit (웹 통신), SQLite (데이터베이스)


Development Environment

- IDE : Android Studio
- Language : Java
- Android SDK Version : 14 (API Level : 34)


Usage

주요 기능과 목적

1. 증상 관리 및 기록
 - GUI를 통해 증상 유형과 강도를 시각적으로 입력받아 쉽고 편하게 환자의 증상을 기록
 - 증상은 DB에 기록되어 과거의 증상 기록도 열람 가능

2. AI 기반 증상 요약 및 병명 예측
 - GPT를 활용해 DB에 저장된 증상을 요약하여 의사에게 제시할 증상 요약 파일 생성
 - 가장 최근 증상을 기반으로 예상 병명 출력

3. 증상 요약 복사 및 저장
 - 요약된 정보를 버튼을 눌러 복사하거나, 저장 버튼을 통해 파일로 저장
 - 저장된 파일은 문서/HealthCareApplication 폴더 내에 저장


Acknowledgement

- Android Studio 공식 문서: https://developer.android.com/docs
- GPT API 공식 홈페이지: https://openai.com
- SQLite 공식 홈페이지: https://www.sqlite.org/index.html  
