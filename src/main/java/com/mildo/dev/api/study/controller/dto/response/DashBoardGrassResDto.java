package com.mildo.dev.api.study.controller.dto.response;

import com.mildo.dev.api.study.repository.dto.GrassInfoDto;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class DashBoardGrassResDto {

    private List<MemberGrassResDto> data;

    @Getter
    public static class MemberGrassResDto {
        private String memberId;
        private List<SolvedPerDateResDto> grass;

        public static MemberGrassResDto of(String memberId, int lengthOfMonth) {
            MemberGrassResDto result = new MemberGrassResDto(memberId);
            for (int date = 1; date <= lengthOfMonth; date++) {
                result.getGrass().add(SolvedPerDateResDto.of(date));
            }
            return result;
        }

        @Builder
        private MemberGrassResDto(String memberId) {
            this.memberId = memberId;
            this.grass = new ArrayList<>();
        }
    }

    @Getter
    @Builder
    public static class SolvedPerDateResDto {
        private Integer date;
        private Integer value;

        public static SolvedPerDateResDto of(int date) {
            return new SolvedPerDateResDto(date, 0);
        }

        public void plus(int value) {
            this.value += value;
        }
    }

    public static DashBoardGrassResDto fromRepoDto(List<String> memberIds, List<GrassInfoDto> repoDto, int lengthOfMonth) {
        Map<String, MemberGrassResDto> result = new HashMap<>(); //key: memberId

        //result Map 초기화
        for (String memberId : memberIds) {
            result.put(memberId, MemberGrassResDto.of(memberId, lengthOfMonth));
        }

        for (GrassInfoDto aRepoDto : repoDto) {
            MemberGrassResDto resDto = result.get(aRepoDto.getMemberId());
            resDto.getGrass().get(aRepoDto.getDate() - 1).plus(aRepoDto.getValue());
        }

        return new DashBoardGrassResDto(result.values().stream().toList());
    }

}
