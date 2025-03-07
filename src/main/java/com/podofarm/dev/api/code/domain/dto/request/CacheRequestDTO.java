package com.podofarm.dev.api.code.domain.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CacheRequestDTO {
    private String memberId;
    private Long problemId;
}
