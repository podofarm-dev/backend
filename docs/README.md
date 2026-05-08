# Database Scripts

포도팜 프로젝트의 데이터베이스 스크립트 모음입니다.

## 파일 구성

- `init_schema.sql` - 데이터베이스 스키마 DDL (테이블 생성, 제약조건, 인덱스)
- `dummy_data.sql` - 개발/테스트용 더미 데이터

## 실행 방법

### 1. 스키마 생성

```bash
# 데이터베이스가 없는 경우 생성
createdb podofarm

# 스키마 실행
psql -U podofarm_user -d podofarm -f init_schema.sql
```

또는:

```bash
PGPASSWORD=podofarm123 psql -h localhost -U podofarm_user -d podofarm -f init_schema.sql
```

### 2. 더미 데이터 삽입

```bash
PGPASSWORD=podofarm123 psql -h localhost -U podofarm_user -d podofarm -f dummy_data.sql
```

### 3. 데이터 확인

```bash
PGPASSWORD=podofarm123 psql -h localhost -U podofarm_user -d podofarm

# psql 접속 후
\dt                    # 테이블 목록 확인
SELECT * FROM study;   # study 테이블 조회
SELECT * FROM member;  # member 테이블 조회
```

## 테이블 구조

### study
- 스터디 그룹 정보
- PK: study_id

### problem
- 알고리즘 문제 정보
- PK: problem_id

### member
- 회원 정보
- PK: member_id
- FK: study_id → study(study_id)

### code
- 코드 제출 정보
- PK: code_no
- FK: member_id → member(member_id)
- FK: problem_id → problem(problem_id)

### comment
- 코드 댓글 정보
- PK: comment_no
- FK: member_id → member(member_id)
- FK: code_no → code(code_no)

### token
- JWT 토큰 정보
- PK: token_no
- FK: member_id → member(member_id)

## 더미 데이터 내용

- **3개의 스터디 그룹**
- **5개의 알고리즘 문제** (백준 기준)
- **5명의 회원**
- **5개의 코드 제출**
- **6개의 댓글**
- **3개의 토큰**

## 주의사항

- 더미 데이터는 **개발/테스트 환경에서만** 사용하세요.
- 운영 환경에서는 실행하지 마세요.
- FK 제약조건으로 인해 **순서대로 삭제**해야 합니다:
  1. token, comment
  2. code
  3. member
  4. problem, study
