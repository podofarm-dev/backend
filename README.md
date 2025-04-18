# 📦 Podofarm Backend 구조

## 📌 프로젝트 개요

- **프로젝트명**: Podofarm
- **설명**: 알고리즘 풀이 공유, 스터디 기능, 통계 시각화 등을 제공하는 백엔드 시스템
- **주요 기술 스택**:
  - Java 17
  - Spring Boot
  - Spring Security + OAuth2
  - JPA / Hibernate
  - AWS (S3, EC2 등)
  - Docker
  - Caffeine Cache

---

## 🧱 도메인별 구성

### 1. 🧩 코드 관리 (code)

- `CodeEntity`: 알고리즘 코드 본문 저장
- `CommentEntity`: 코드에 달린 댓글 저장
- 요청 DTO: CacheRequestDTO, CodeLevelDTO, CodeSolvedListDTO, CommentContentDTO, ExtensionSyncDTO, UploadDTO
- 응답 DTO: CommentListResponse, CommentResponse, OpenAIResponse, CodeInfoDTO
- 서비스: `CodeService`
- 레포지토리: `CodeRepository`, `CommentRepository`

---

### 2. 👤 회원 관리 (member)

- `MemberEntity`: 사용자 정보
- `TokenEntity`: Access, Refresh 토큰 저장
- 요청 DTO: MemberReNameDto, TokenDto
- 응답 DTO: SolvedMemberListResponse
- OAuth 관련:
  - `CustomUser`: OAuth 인증 사용자 정보
  - `CustomOAuthUserService`: OAuth2 로그인 처리 서비스
- 서비스: `MemberService`
- 레포지토리: `MemberRepository`, `TokenRepository`

---

### 3. 📚 문제 관리 (problem)

- `ProblemEntity`: 문제 정보 및 푼 사용자 관리
- 요청 DTO: UserProfileDto, ProblemSolverDto
- 응답 DTO: ProblemListResponse, ProblemStaticDto
- 서비스: `ProblemService`
- 레포지토리: `ProblemRepository`

---

### 4. 👨‍👩‍👧‍👦 스터디 관리 (study)

- `StudyEntity`: 스터디 생성 및 정보 저장
- 요청 DTO: DailySolvedSearchCond, StudyCreateReqDto, StudyJoinReqDto, StudyLeaderUpdateReqDto, StudyNameUpdateReqDto
- 응답 DTO: DailySolvedResDto, DashBoardFrameResDto, DashBoardGrassResDto, DashBoardSolvedCountResDto, LogResDto, MessageResDto, StudyDetailResDto, StudySummaryResDto
- 서비스: `StudyService`
- 레포지토리: `StudyRepository`, `CustomStudyRepository`, `CustomStudyRepositoryImpl`
- 예외 처리: `StudyExceptionHandler`

---

### 5. 🛠 유틸리티 (utils)

- `CookieUtil`: 쿠키 생성, 삭제 등 기능 제공
- `CodeGenerator`: 난수 코드 생성 유틸리티

---

### 6. 🌐 글로벌 설정 (global)

#### 📑 설정 (config)

- `AsyncConfig`: @Async 비동기 설정
- `CaffeineCacheConfig`: Caffeine 기반 캐시 설정
- `CorsConfig`: CORS 정책 적용
- `OpenAIConfig`: OpenAI API 연동용 설정
- `S3Config`: AWS S3 파일 업로드 설정
- `SecurityConfig`: OAuth2 및 JWT 보안 설정

#### 📦 캐시 (cache)

- `CacheAspect`: AOP 기반 캐싱 처리
- `CacheProblemList`: 문제 리스트 캐시 저장

#### 🧯 예외 처리 (exception)

- `GlobalException`: 전역 예외 처리 클래스
- `ErrorResDto`: 예외 발생 시 응답 객체
- `ExceptionMessage`: 예외 메시지 모음 상수
- 커스텀 예외들:
  - AlreadyInStudyException, LeaderCannotLeaveException, MemberEqException, NotInThatStudyException
  - ServerUnstableException, StudyPasswordMismatchException, TokenException

#### 🔐 OAuth & JWT (oauth)

- `JwtInterface`: 토큰 정보 정의 인터페이스
- `JwtTokenProvider`: JWT 토큰 발급 및 검증

---
## 📁 디렉토리 구조
```bash
podofarm/
└── src/main/java/com/podofarm/dev/
    ├── MildoBackendApplication.java                    # Spring Boot 메인 클래스
    │
    ├── api/                                            # 도메인 API 모듈 루트
    │   ├── code/                                       # 코드 관리 도메인
    │   │   ├── controller/
    │   │   │   └── CodeController.java                 # 코드 API 컨트롤러
    │   │   ├── domain/
    │   │   │   ├── dto/
    │   │   │   │   ├── request/
    │   │   │   │   │   ├── CacheRequestDTO.java        # 캐시 요청 DTO
    │   │   │   │   │   ├── CodeLevelDTO.java           # 난이도 DTO
    │   │   │   │   │   ├── CodeSolvedListDTO.java      # 푼 목록 요청 DTO
    │   │   │   │   │   ├── CommentContentDTO.java      # 댓글 작성 DTO
    │   │   │   │   │   ├── ExtensionSyncDTO.java       # 확장 동기화 DTO
    │   │   │   │   │   └── UploadDTO.java              # 코드 업로드 DTO
    │   │   │   │   └── response/
    │   │   │   │       ├── CommentListResponse.java    # 댓글 리스트 응답
    │   │   │   │       ├── CommentResponse.java        # 댓글 응답
    │   │   │   │       ├── OpenAIResponse.java         # AI 응답 DTO
    │   │   │   │       └── CodeInfoDTO.java            # 코드 상세 응답
    │   │   │   └── entity/
    │   │   │       ├── CodeEntity.java                 # 코드 본문 엔티티
    │   │   │       └── CommentEntity.java              # 댓글 엔티티
    │   │   ├── repository/
    │   │   │   ├── CodeRepository.java                 # 코드 JPA 인터페이스
    │   │   │   └── CommentRepository.java              # 댓글 JPA 인터페이스
    │   │   └── service/
    │   │       └── CodeService.java                    # 코드 서비스 클래스
    │
    │   ├── member/                                     # 회원 관리 도메인
    │   │   ├── controller/
    │   │   │   └── MemberController.java
    │   │   ├── domain/
    │   │   │   ├── entity/
    │   │   │   │   ├── MemberEntity.java               # 사용자 정보 엔티티
    │   │   │   │   └── TokenEntity.java                # 토큰 엔티티
    │   │   │   └── dto/
    │   │   │       ├── request/
    │   │   │       │   ├── MemberReNameDto.java        # 닉네임 변경 DTO
    │   │   │       │   └── TokenDto.java               # 토큰 요청 DTO
    │   │   │       └── response/
    │   │   │           └── SolvedMemberListResponse.java # 푼 사용자 리스트
    │   │   ├── repository/
    │   │   │   ├── MemberRepository.java
    │   │   │   └── TokenRepository.java
    │   │   ├── service/
    │   │   │   └── MemberService.java
    │   │   └── customoauth/
    │   │       ├── dto/
    │   │       │   └── CustomUser.java                 # OAuth 유저 정보
    │   │       └── handler/
    │   │           └── CustomOAuthUserService.java     # OAuth2 서비스
    │
    │   ├── problem/                                    # 문제 관리 도메인
    │   │   ├── controller/
    │   │   │   └── ProblemController.java
    │   │   ├── domain/
    │   │   │   ├── entity/
    │   │   │   │   └── ProblemEntity.java              # 문제 엔티티
    │   │   │   └── dto/
    │   │   │       ├── request/
    │   │   │       │   ├── UserProfileDto.java         # 사용자 문제 정보 요청
    │   │   │       │   └── ProblemSolverDto.java       # 문제 해결 정보
    │   │   │       └── response/
    │   │   │           ├── ProblemListResponse.java    # 문제 리스트 응답
    │   │   │           └── ProblemStaticDto.java       # 통계용 DTO
    │   │   ├── repository/
    │   │   │   └── ProblemRepository.java
    │   │   └── service/
    │   │       └── ProblemService.java
    │
    │   ├── study/                                      # 스터디 도메인
    │   │   ├── controller/
    │   │   │   └── StudyController.java
    │   │   ├── domain/
    │   │   │   ├── entity/
    │   │   │   │   └── StudyEntity.java
    │   │   │   └── dto/
    │   │   │       ├── request/
    │   │   │       │   ├── DailySolvedSearchCond.java
    │   │   │       │   ├── StudyCreateReqDto.java
    │   │   │       │   ├── StudyJoinReqDto.java
    │   │   │       │   ├── StudyLeaderUpdateReqDto.java
    │   │   │       │   └── StudyNameUpdateReqDto.java
    │   │   │       └── response/
    │   │   │           ├── DailySolvedResDto.java
    │   │   │           ├── DashBoardFrameResDto.java
    │   │   │           ├── DashBoardGrassResDto.java
    │   │   │           ├── DashBoardSolvedCountResDto.java
    │   │   │           ├── LogResDto.java
    │   │   │           ├── MessageResDto.java
    │   │   │           ├── StudyDetailResDto.java
    │   │   │           └── StudySummaryResDto.java
    │   │   ├── repository/
    │   │   │   ├── StudyRepository.java
    │   │   │   ├── CustomStudyRepository.java
    │   │   │   └── CustomStudyRepositoryImpl.java
    │   │   ├── service/
    │   │   │   └── StudyService.java
    │   │   └── exhandler/
    │   │       └── StudyExceptionHandler.java
    │
    │   └── utils/
    │       ├── cookie/
    │       │   └── CookieUtil.java                   # 쿠키 생성/삭제 유틸
    │       └── random/
    │           └── CodeGenerator.java                # 랜덤 코드 생성기
    │
    └── global/                                       # 글로벌 설정 모듈
        ├── config/
        │   ├── async/
        │   │   └── AsyncConfig.java                  # 비동기 처리 설정
        │   ├── caffeine/
        │   │   └── CaffeineCacheConfig.java          # 캐시 설정
        │   ├── cors/
        │   │   └── CorsConfig.java                   # CORS 정책 설정
        │   ├── openai/
        │   │   └── OpenAIConfig.java                 # OpenAI 연동 설정
        │   ├── s3/
        │   │   └── S3Config.java                     # AWS S3 연동 설정
        │   └── security/
        │       └── SecurityConfig.java               # Spring Security 설정
        │
        ├── cache/
        │   ├── CacheAspect.java                      # AOP 캐시 처리
        │   └── CacheProblemList.java                 # 문제 리스트 캐시
        │
        ├── exception/
        │   ├── GlobalException.java                  # 전역 예외 처리기
        │   ├── dto/
        │   │   └── ErrorResDto.java                  # 예외 응답 DTO
        │   ├── message/
        │   │   └── ExceptionMessage.java             # 메시지 상수
        │   └── exceptionClass/
        │       ├── AlreadyInStudyException.java
        │       ├── LeaderCannotLeaveException.java
        │       ├── MemberEqException.java
        │       ├── NotInThatStudyException.java
        │       ├── ServerUnstableException.java
        │       ├── StudyPasswordMismatchException.java
        │       └── TokenException.java
        │
        └── oauth/
            └── jwt/
                ├── JwtInterface.java                 # JWT 인터페이스 정의
                └── JwtTokenProvider.java             # JWT 발급/검증 제공
