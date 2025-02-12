package com.mildo.dev.api.code.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mildo.dev.api.code.domain.dto.UploadDTO;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
public class CodeService {
    public void upload(JsonNode request) throws ParseException {
        UploadDTO upload = new UploadDTO();

        String memberId = request.get("Id").asText();
        String problemId;
        String codeSource;
        String codeSolvedDate;
        String codeAnnotation;
        String codeStatus;
        String codeTime;

        /*
        //memberId 를 request에서 getId로
        String id = request.getId();
        String filename = request.getFilename();
        filename = filename.substring(0, filename.length() - 5);
        String sourceText = request.getSourceText();
        String readmeText = request.getReadmeText();
        String dateInfo = request.getDateInfo();
        int problemId = Integer.parseInt(request.getProblemId());
        // "lv2" -> "2"로 변경 (자료형 유지를 위해 charAt 등과 같은 것을 사용하지 않음)
        String level = request.getLevel();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime date = LocalDateTime.parse(dateInfo, formatter);

        // TODO: 추가 로직 작성 필요(코드 아이디에 대한 의견 필요)
        // codeLikes 의 값이 Y / N 임.
        CodeVO vo = new CodeVO(id, filename, readmeText, sourceText, "N", level, problemId, 0, date);

        codeRepository.upload(vo);
            */
    }

}
