📦 Podofarm Backend 구조
📌 프로젝트 개요
프로젝트명: Podofarm
주요 기술 스택:
Java 17
Spring Boot
Spring Security + OAuth2
JPA/Hibernate
AWS (S3, EC2 등)
Docker
---
🔍 주요 도메인별 클래스 구조
1. 코드 관리 (Code)
엔티티
CodeEntity: 코드 정보
CommentEntity: 코드 댓글
DTO
Request: CacheRequestDTO, CodeLevelDTO, CodeSolvedListDTO, CommentContentDTO, ExtensionSyncDTO, UploadDTO
Response: CommentListResponse, CommentResponse, OpenAIResponse, CodeInfoDTO
서비스: CodeService
리포지토리: CodeRepository, CommentRepository
2. 회원 관리 (Member)
엔티티
MemberEntity: 회원 정보
TokenEntity: 토큰 정보
DTO
Request: MemberReNameDto, TokenDto
Response: SolvedMemberListResponse
OAuth
CustomUser: OAuth 사용자 정보
CustomOAuthUserService: OAuth 인증 서비스
서비스: MemberService
리포지토리: MemberRepository, TokenRepository
3. 문제 관리 (Problem)
엔티티: ProblemEntity
DTO
Request: UserProfileDto, ProblemSolverDto
Response: ProblemListResponse, ProblemStaticDto
서비스: ProblemService
리포지토리: ProblemRepository
4. 스터디 관리 (Study)
엔티티: StudyEntity
DTO
Request: DailySolvedSearchCond, StudyCreateReqDto, StudyJoinReqDto, StudyLeaderUpdateReqDto, StudyNameUpdateReqDto
Response: DailySolvedResDto, DashBoardFrameResDto, DashBoardGrassResDto, DashBoardSolvedCountResDto, LogResDto, MessageResDto, StudyDetailResDto, StudySummaryResDto
서비스: StudyService
리포지토리: StudyRepository, CustomStudyRepository, CustomStudyRepositoryImpl
예외처리: StudyExceptionHandler
5. 유틸리티 (Utils)
쿠키: CookieUtil
랜덤 생성: CodeGenerator
6. 글로벌 설정 (Global)
설정
AsyncConfig: 비동기 처리
CaffeineCacheConfig: 캐시
CorsConfig: CORS
OpenAIConfig: OpenAI
S3Config: AWS S3
SecurityConfig: 보안
캐시
CacheAspect
CacheProblemList
예외
GlobalException
ErrorResDto
커스텀 예외 클래스들
OAuth/JWT
JwtInterface
JwtTokenProvider

---
🗂️ 프로젝트 디렉토리 상세 구조
```bash
podofarm/
├── src/main/java/com/podofarm/dev/
    ├── MildoBackendApplication.java              # 메인 애플리케이션
    │
    ├── api/                                      # API 모듈
    │   ├── code/                                 # 코드 관리
    │   │   ├── controller/
    │   │   │   └── CodeController.java
    │   │   ├── domain/
    │   │   │   ├── dto/
    │   │   │   │   ├── request/
    │   │   │   │   │   ├── CacheRequestDTO.java
    │   │   │   │   │   ├── CodeLevelDTO.java
    │   │   │   │   │   ├── CodeSolvedListDTO.java
    │   │   │   │   │   ├── CommentContentDTO.java
    │   │   │   │   │   ├── ExtensionSyncDTO.java
    │   │   │   │   │   └── UploadDTO.java
    │   │   │   │   └── response/
    │   │   │   │       ├── CommentListResponse.java
    │   │   │   │       ├── CommentResponse.java
    │   │   │   │       ├── OpenAIResponse.java
    │   │   │   │       └── CodeInfoDTO.java
    │   │   │   └── entity/
    │   │   │       ├── CodeEntity.java
    │   │   │       └── CommentEntity.java
    │   │   ├── repository/
    │   │   │   ├── CodeRepository.java
    │   │   │   └── CommentRepository.java
    │   │   └── service/
    │   │       └── CodeService.java
    │   │
    │   ├── member/                               # 회원 관리
    │   │   ├── controller/
    │   │   │   └── MemberController.java
    │   │   ├── domain/
    │   │   │   ├── entity/
    │   │   │   │   ├── MemberEntity.java
    │   │   │   │   └── TokenEntity.java
    │   │   │   └── dto/
    │   │   │       ├── request/
    │   │   │       │   ├── MemberReNameDto.java
    │   │   │       │   └── TokenDto.java
    │   │   │       └── response/
    │   │   │           └── SolvedMemberListResponse.java
    │   │   ├── repository/
    │   │   │   ├── MemberRepository.java
    │   │   │   └── TokenRepository.java
    │   │   ├── service/
    │   │   │   └── MemberService.java
    │   │   └── customoauth/
    │   │       ├── dto/
    │   │       │   └── CustomUser.java
    │   │       └── handler/
    │   │           └── CustomOAuthUserService.java
    │   │
    │   ├── problem/                              # 문제 관리
    │   │   ├── controller/
    │   │   │   └── ProblemController.java
    │   │   ├── domain/
    │   │   │   ├── entity/
    │   │   │   │   └── ProblemEntity.java
    │   │   │   └── dto/
    │   │   │       ├── request/
    │   │   │       │   ├── UserProfileDto.java
    │   │   │       │   └── ProblemSolverDto.java
    │   │   │       └── response/
    │   │   │           ├── ProblemListResponse.java
    │   │   │           └── ProblemStaticDto.java
    │   │   ├── repository/
    │   │   │   └── ProblemRepository.java
    │   │   └── service/
    │   │       └── ProblemService.java
    │   │
    │   ├── study/                                # 스터디 관리
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
    │   │   │   ├── CustomStudyRepository.java
    │   │   │   ├── CustomStudyRepositoryImpl.java
    │   │   │   └── StudyRepository.java
    │   │   ├── service/
    │   │   │   └── StudyService.java
    │   │   └── exhandler/
    │   │       └── StudyExceptionHandler.java
    │   │
    │   └── utils/                                # 유틸리티
    │       ├── cookie/
    │       │   └── CookieUtil.java
    │       └── random/
    │           └── CodeGenerator.java
    │
    └── global/                                   # 공통 모듈
        ├── config/                               # 설정
        │   ├── async/
        │   │   └── AsyncConfig.java
        │   ├── caffeine/
        │   │   └── CaffeineCacheConfig.java
        │   ├── cors/
        │   │   └── CorsConfig.java
        │   ├── openai/
        │   │   └── OpenAIConfig.java
        │   ├── s3/
        │   │   └── S3Config.java
        │   └── security/
        │       └── SecurityConfig.java
        │
        ├── cache/                                # 캐시
        │   ├── CacheAspect.java
        │   └── CacheProblemList.java
        │
        ├── exception/                            # 예외 처리
        │   ├── GlobalException.java
        │   ├── dto/
        │   │   └── ErrorResDto.java
        │   ├── message/
        │   │   └── ExceptionMessage.java
        │   └── exceptionClass/
        │       ├── AlreadyInStudyException.java
        │       ├── LeaderCannotLeaveException.java
        │       ├── MemberEqException.java
        │       ├── NotInThatStudyException.java
        │       ├── ServerUnstableException.java
        │       ├── StudyPasswordMismatchException.java
        │       └── TokenException.java
        │
        └── oauth/                                # OAuth 인증
            └── jwt/
                ├── JwtInterface.java
                └── JwtTokenProvider.java
