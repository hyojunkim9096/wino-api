# WINO 프로젝트

Spring Boot + Flutter 기반의 풀스택 웹 애플리케이션

---

## 🌐 프로젝트 소개

**WINO 프로젝트**는 Java 기반의 Spring Boot 백엔드와 Flutter Web 프론트엔드를 통합하여 구축한 풀스택 애플리케이션입니다.  
Flutter에서 빌드한 정적 파일을 Spring Boot의 `resources/static` 디렉토리에 포함시켜 배포합니다.

---

## 🔧 기술 스택

| 구분        | 사용 기술                                  |
|-------------|---------------------------------------------|
| Backend     | Spring Boot 3.5.4, Java 21                  |
| Frontend    | Flutter 3.x (Web)                           |
| DB          | MySQL (개발 중: PlanetScale → GCP 예정)     |
| 인증 보안   | Spring Security, JWT, 비밀번호 암호화 등   |
| 인프라(예정)| GCP App Engine / Compute Engine + Cloud SQL |
| 배포 방식   | Git + GitHub + GCP                          |

---

## 📁 프로젝트 구조

```
wino-api/
├── src/
│   ├── main/
│   │   ├── java/com/wino/wino_api/
│   │   │   ├── config/           # WebConfig, 보안 설정 등
│   │   │   ├── controller/       # Main, User, Auth 컨트롤러
│   │   │   ├── dto/              # 요청/응답 DTO
│   │   │   ├── entity/           # JPA 엔티티
│   │   │   └── security/         # JWT, 필터, 예외처리
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       ├── static/
│   │       │   └── flutter/      # Flutter build 결과
│   │       └── templates/
│
├── flutter/                      # Flutter frontend 원본 (별도 프로젝트)
│   └── wino_app/
│       ├── lib/
│       ├── build/
│       └── pubspec.yaml
│
├── README.md
└── pom.xml
```

---

## 🚀 실행 방법

### ✅ Flutter 빌드 후 정적 파일 복사

```bash
cd frontend/wino_app
flutter build web

# 빌드된 정적 파일을 Spring Boot static 디렉토리로 복사
rm -rf ../../backend/wino-api/src/main/resources/static/flutter
cp -r build/web ../../backend/wino-api/src/main/resources/static/flutter
```

### ✅ Spring Boot 실행

```bash
cd backend/wino-api
./mvnw spring-boot:run
# 또는 IntelliJ에서 실행
```

---

## 🔐 보안 기능

- CORS 정책 적용 (`WebConfig`)
- JWT 토큰 인증/인가
- 비밀번호 암호화 (Spring Security + PasswordEncoder)
- 예외 핸들러(GlobalExceptionHandler) 구성
- 필터: `JwtAuthenticationFilter`, `JwtExceptionFilter`

---

## 🌍 배포 계획

- GCP 환경에서 Spring Boot + Flutter 앱 배포
- 도메인 연결: `https://your-domain.com`
- Cloud SQL과 연결된 프로덕션 DB 구성 예정

---

## 📌 작업 진행 상태

- [x] Flutter 앱 구조 구성
- [x] Flutter → Web 빌드 연동
- [x] Spring Boot 기본 API 구성 (`/hello`)
- [x] 회원가입/로그인용 엔티티 및 컨트롤러 구성
- [x] 보안 필터 및 JWT 인증 구현
- [ ] GCP 배포
- [ ] 도메인 연결
- [ ] Flutter → 백엔드 회원가입 API 연동

---

## 🙋‍♂️ 개발자

- 김효준  
- 기술 블로그: [https://winoslife.tistory.com](https://winoslife.tistory.com)

---

## 📄 라이선스

MIT License
