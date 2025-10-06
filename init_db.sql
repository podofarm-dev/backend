-- Podofarm Database Schema
-- 생성일: 2025-10-06

-- 1. study 테이블 (외래키 참조 없음)
CREATE TABLE study (
    study_id VARCHAR(255) PRIMARY KEY,
    study_name VARCHAR(255),
    study_pwd VARCHAR(255),
    study_start DATE,
    study_end DATE
);

-- 2. problem 테이블 (외래키 참조 없음)
CREATE TABLE problem (
    problem_id BIGINT PRIMARY KEY,
    problem_no BIGINT,
    problem_title VARCHAR(255),
    problem_level VARCHAR(50),
    problem_link VARCHAR(500),
    problem_readme TEXT,
    problem_type VARCHAR(100),
    problem_solution TEXT
);

-- 3. member 테이블 (study 참조)
CREATE TABLE member (
    member_id VARCHAR(255) PRIMARY KEY,
    member_name VARCHAR(255),
    member_googleid VARCHAR(255),
    member_email VARCHAR(255),
    member_solvedproblem INTEGER DEFAULT 0,
    member_leader VARCHAR(1) DEFAULT 'N',
    member_isparticipant DATE,
    member_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    member_img_url VARCHAR(500),
    study_id VARCHAR(255),
    CONSTRAINT fk_member_study FOREIGN KEY (study_id) REFERENCES study(study_id) ON DELETE SET NULL
);

-- 4. token 테이블 (member 참조)
CREATE TABLE token (
    token_no BIGSERIAL PRIMARY KEY,
    refresh_token TEXT,
    access_token TEXT,
    refresh_expiration_time TIMESTAMP,
    member_id VARCHAR(255),
    CONSTRAINT fk_token_member FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE
);

-- 5. code 테이블 (member, problem 참조)
CREATE TABLE code (
    code_no BIGSERIAL PRIMARY KEY,
    code_source TEXT,
    code_solved_date TIMESTAMP,
    code_annotation TEXT,
    code_status BOOLEAN,
    code_time TIME,
    code_performance VARCHAR(255),
    code_accuracy VARCHAR(255),
    member_id VARCHAR(255),
    problem_id BIGINT,
    CONSTRAINT fk_code_member FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE,
    CONSTRAINT fk_code_problem FOREIGN KEY (problem_id) REFERENCES problem(problem_id) ON DELETE CASCADE
);

-- 6. comment 테이블 (member, code 참조)
CREATE TABLE comment (
    comment_no BIGSERIAL PRIMARY KEY,
    comment_content TEXT,
    comment_date TIMESTAMP,
    member_id VARCHAR(255),
    code_no BIGINT,
    CONSTRAINT fk_comment_member FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_code FOREIGN KEY (code_no) REFERENCES code(code_no) ON DELETE CASCADE
);

-- 인덱스 생성 (성능 향상)
CREATE INDEX idx_member_study_id ON member(study_id);
CREATE INDEX idx_member_email ON member(member_email);
CREATE INDEX idx_token_member_id ON token(member_id);
CREATE INDEX idx_code_member_id ON code(member_id);
CREATE INDEX idx_code_problem_id ON code(problem_id);
CREATE INDEX idx_comment_member_id ON comment(member_id);
CREATE INDEX idx_comment_code_no ON comment(code_no);
CREATE INDEX idx_problem_level ON problem(problem_level);
CREATE INDEX idx_code_solved_date ON code(code_solved_date);
