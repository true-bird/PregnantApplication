# 지하철 임산부석 예약 어플리케이션
> 지하철 실시간 도착 정보와 열차별 임산부석 현황을 통해 임산부석을 예약

![Dev][dev-image]
- 가상의 임산부 번호를 입력하여 임산부 인증
- 지하철 역을 선택하여 타고자 하는 열차를 선택
- 열차에 남은 임산부석을 확인하여 임산부석을 예약

<iframe width="853" height="480" src="https://www.youtube.com/embed/NUS4N-Ldxn8" frameborder="0" allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>

## 특징
- 공공데이터의 지하철 실시간 도착 정보 오픈 API 사용
- 가상의 보건소 서버와 임산부석 관리 서버를 만들어 임산부 인증과 임산부석 예약 서비스 제공

## 데모

## 테스트
- 안드로이드 스튜디오에 'New_EX_Application' import 하여 실행
- 테스트용 임산부 번호를 입력하여 인증
  - _'111111111'_
  - _'222222222'_
  - _'333333333'_
  - _'444444444'_

## 설치 및 실행 방법
- Mysql에 `mysql/project_1.sql` import
- Apache 홈 디렉토리 안에 `www/html/` 내의 파일 복사
  - `dpcon.php` 의 내용을 Mysql 계정에 맞게 수정
- 안드로이드 스튜디오에 `New_EX_Application` import
  - `MainActivity.java` 의 _IP_ADDRESS_ 변수의 서버 ip 주소 수정

## 개발 환경
- Apache 2.4
- php 5.6
- mysql 5.5
- Android Studio 3.5
  - Android Studio Gradle 4.6
  - Android Studio Gradle Plugin 3.2.1
- Amazon Linux


<!-- Markdown link & img dfn's -->
[dev-image]: https://img.shields.io/badge/Dev-Android-green
