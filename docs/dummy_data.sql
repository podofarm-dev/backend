--
-- Podofarm 더미 데이터 삽입 스크립트
-- 실행 순서: study -> problem -> member -> code -> comment -> token
--

-- 1. Study 데이터
INSERT INTO study (study_id, study_name, study_pwd, study_start, study_end) VALUES
('STUDY001', '알고리즘 마스터반', 'password123', '2025-01-01', '2025-06-30'),
('STUDY002', '코딩테스트 정복', 'test1234', '2025-02-01', '2025-07-31'),
('STUDY003', 'SSAFY 12기 스터디', 'ssafy2025', '2025-03-01', '2025-12-31');

-- 2. Problem 데이터
INSERT INTO problem (problem_id, problem_no, problem_title, problem_level, problem_link, problem_readme, problem_type, problem_solution, created_at, updated_at) VALUES
(1, 1000, '두 수 더하기', 'Bronze5', 'https://www.acmicpc.net/problem/1000', 'A+B를 출력하는 프로그램', 'Math', null, NOW(), NOW()),
(2, 2750, '수 정렬하기', 'Bronze2', 'https://www.acmicpc.net/problem/2750', 'N개의 수를 오름차순 정렬', 'Sort', null, NOW(), NOW()),
(3, 1920, '수 찾기', 'Silver4', 'https://www.acmicpc.net/problem/1920', '이진 탐색 문제', 'Binary Search', null, NOW(), NOW()),
(4, 11399, 'ATM', 'Silver4', 'https://www.acmicpc.net/problem/11399', '그리디 알고리즘', 'Greedy', 'ATM 대기 시간 최소화', NOW(), NOW()),
(5, 1260, 'DFS와 BFS', 'Silver2', 'https://www.acmicpc.net/problem/1260', '그래프 탐색', 'Graph', 'DFS, BFS 구현', NOW(), NOW());

-- 3. Member 데이터
INSERT INTO member (member_id, member_name, member_googleid, member_email, member_solvedproblem, member_leader, member_isparticipant, member_date, member_img_url, study_id) VALUES
('user001', '김철수', 'google_12345', 'chulsoo.kim@gmail.com', 15, 'Y', '2025-01-01', NOW(), 'https://via.placeholder.com/150', 'STUDY001'),
('user002', '이영희', 'google_23456', 'younghee.lee@gmail.com', 10, 'N', '2025-01-02', NOW(), 'https://via.placeholder.com/150', 'STUDY001'),
('user003', '박민수', 'google_34567', 'minsu.park@gmail.com', 20, 'Y', '2025-02-01', NOW(), 'https://via.placeholder.com/150', 'STUDY002'),
('user004', '정수진', 'google_45678', 'sujin.jung@gmail.com', 8, 'N', '2025-03-01', NOW(), 'https://via.placeholder.com/150', 'STUDY003'),
('user005', '최동욱', 'google_56789', 'dongwook.choi@gmail.com', 12, 'N', '2025-01-05', NOW(), 'https://via.placeholder.com/150', 'STUDY001');

-- 4. Code 데이터
INSERT INTO code (code_no, code_source, code_solved_date, code_annotation, code_status, code_time, code_performance, code_accuracy, member_id, problem_id) VALUES
(1, 'import sys\na, b = map(int, sys.stdin.readline().split())\nprint(a + b)', '2025-01-15 14:30:00', '간단한 덧셈 문제', true, '00:00:00.124', '2024KB', '100%', 'user001', 1),
(2, 'n = int(input())\narr = [int(input()) for _ in range(n)]\narr.sort()\nfor num in arr:\n    print(num)', '2025-01-16 10:20:00', '정렬 기본 문제', true, '00:00:01.500', '4096KB', '100%', 'user001', 2),
(3, 'n = int(input())\na = list(map(int, input().split()))\nm = int(input())\nb = list(map(int, input().split()))\na.sort()\nfor num in b:\n    print(1 if num in a else 0)', '2025-01-17 15:45:00', '이진 탐색 활용', true, '00:00:02.300', '8192KB', '100%', 'user002', 3),
(4, 'n = int(input())\ntime = list(map(int, input().split()))\ntime.sort()\ntotal = 0\nfor i in range(n):\n    total += time[i] * (n - i)\nprint(total)', '2025-02-05 11:15:00', '그리디 알고리즘 적용', true, '00:00:00.890', '3072KB', '100%', 'user003', 4),
(5, 'from collections import deque\n# DFS, BFS 구현\ndef dfs(graph, v, visited):\n    visited[v] = True\n    print(v, end=" ")', '2025-03-10 16:00:00', 'DFS/BFS 기본', false, '00:00:05.000', '16384KB', '80%', 'user004', 5);

-- 5. Comment 데이터
INSERT INTO comment (comment_no, comment_content, comment_date, member_id, code_no) VALUES
(1, '코드가 깔끔하네요!', '2025-01-15 15:00:00', 'user002', 1),
(2, 'sys.stdin을 사용하면 더 빠르답니다.', '2025-01-15 15:30:00', 'user003', 1),
(3, '정렬 알고리즘을 직접 구현해보는 것도 좋을 것 같아요.', '2025-01-16 11:00:00', 'user005', 2),
(4, '이진 탐색이 아니라 in 연산자를 사용하셨네요. 시간복잡도가 O(n)이에요.', '2025-01-17 16:00:00', 'user001', 3),
(5, '그리디 알고리즘의 정석이네요!', '2025-02-05 12:00:00', 'user001', 4),
(6, 'DFS 구현이 완성되지 않은 것 같습니다.', '2025-03-10 17:00:00', 'user003', 5);

-- 6. Token 데이터 (선택적)
INSERT INTO token (token_no, refresh_token, access_token, refresh_expiration_time, member_id) VALUES
(1, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.refresh_user001', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.access_user001', NOW() + INTERVAL '7 days', 'user001'),
(2, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.refresh_user002', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.access_user002', NOW() + INTERVAL '7 days', 'user002'),
(3, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.refresh_user003', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.access_user003', NOW() + INTERVAL '7 days', 'user003');

-- 시퀀스 값 재설정
SELECT setval('code_code_no_seq', (SELECT MAX(code_no) FROM code));
SELECT setval('comment_comment_no_seq', (SELECT MAX(comment_no) FROM comment));
SELECT setval('token_token_no_seq', (SELECT MAX(token_no) FROM token));
