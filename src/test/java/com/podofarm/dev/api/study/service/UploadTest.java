package com.podofarm.dev.api.study.service;

public class UploadTest {

    /*익스텐션에서 Upload 메소드가 실행되었을 때 Processing Time, Latency Time을 줄이기 위한 공간

    - 기존코드
     1. Upload 실행 후 Parsing Data 수집
     2. codeUpload 서비스 실행
     3. - OPEN AI  호출하여 sourceText 1차 수정
        - problemId로 problem 테이블에서 problemSolution 조회 후 sourceText에 추가
        - 최종 코드 발행
        - Insert문 진행


    // 수정 방향성
     1. 중복된 Parsing Data 삭제
     2. Cache를 이용하여 정적 Data는 async로 미리 insert
     3. 이 때 insert가 되었을 때 사용자한테는 완료되었다고 모달 또는 아이콘 전달 - Latency Time 줄임
     4. Upload 되었을 때 sourceText 로직을 다음과 같이 변경
        - insert할 때 soureText 부분에 먼저 problemSolution 제공
        - Open AI가 가져온 Source를 이어붙임

    기존 로직 14.6초
    //Test Case 설정


    */


}
