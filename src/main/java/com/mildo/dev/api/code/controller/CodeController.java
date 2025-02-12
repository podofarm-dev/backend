package com.mildo.dev.api.code.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mildo.dev.api.code.domain.dto.UploadDTO;
import com.mildo.dev.api.code.service.CodeService;
import com.mildo.dev.api.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;


@RestController
@RequestMapping("/code")
@RequiredArgsConstructor
@Slf4j
public class CodeController {

    private final MemberService memberService;
    private final CodeService codeService;

    @CrossOrigin(origins = "chrome-extension://magnaalaamndcofdpgeicpnlpdjajbjb")
    @PostMapping("/receive-sync")
    public ResponseEntity<String> receiveSync(@RequestBody String data) {
        try {
            /* 연동하기 눌렀을 때 ID, 와 STUDYCODE 확인 후, 연동하는 작업 */
            ObjectMapper sync = new ObjectMapper();
            JsonNode convertSync = sync.readTree(data);

            log.info("오브젝트 값 확인" + convertSync);
            if (validateUserStudySync(convertSync)) {
                return ResponseEntity.ok("success");
            } else {
                return ResponseEntity.ok("fail");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while processing the request");
        }
    }

    @CrossOrigin(origins = {"chrome-extension://kmleenknngfkjncchnbfenfamoighddf", "https://school.programmers.co.kr"})
    @PostMapping("/receive-data")
    public ResponseEntity<String> receiveData(@RequestBody String data) {
        try {

            /* 파싱된 데이터를 옮기는 작업*/
            ObjectMapper Data = new ObjectMapper();
            JsonNode convertData = Data.readTree(data);

            if (validateUserStudySync(convertData)) {
                return ResponseEntity.ok("success");
            } else {
                return ResponseEntity.ok("fail");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while processing the request");
        }
    }

    @CrossOrigin(origins = {"chrome-extension://kmleenknngfkjncchnbfenfamoighddf", "https://school.programmers.co.kr"})
    @RequestMapping(method = RequestMethod.POST, value = "/upload")
    public ResponseEntity<String> upload(@RequestBody String request) throws JsonProcessingException, ParseException {
        ObjectMapper Data = new ObjectMapper();
        JsonNode convertData = Data.readTree(request);
        log.info("coverData Test" + convertData);


        codeService.upload(convertData);
        return ResponseEntity.ok("Upload successful");
    }



    private boolean validateUserStudySync(JsonNode convertData) {
        // 필요한 정보 추출
        String userId = convertData.get("id").asText();
        String studyId = convertData.get("studyId").asText();

        return memberService.checkExtensionSync(userId, studyId);
    }


}
