package com.podofarm.dev.global.exception.message;

public abstract class ExceptionMessage {

    public static final String MEMBER_NOT_FOUND_MSG = "해당 ID의 회원이 존재하지 않습니다.";

    public static final String STUDY_NOT_FOUND_MSG = "해당 CODE의 스터디가 존재하지 않습니다.";
    public static final String STUDY_PASSWORD_MISMATCH_MSG = "스터디의 비밀번호가 일치하지 않습니다.";
    public static final String ALREADY_IN_STUDY_MSG = "이미 가입한 스터디가 있습니다.";

    public static final String NOT_IN_THAT_STUDY_MSG = "해당 스터디에 속한 사용자가 아닙니다.";
    public static final String SOMEONE_NOT_IN_MSG = "해당 스터디에 속해있지 않은 사용자가 있습니다.";
    public static final String NOT_STUDY_LEADER_MSG = "해당 스터디의 리더가 아닙니다.";
    public static final String NOT_EXIST_LEADER_MSG = "해당 스터디에 리더가 존재하지 않습니다.";
    public static final String LEADER_CANNOT_LEAVE_MSG = "스터디장은 스터디를 나갈 수 없습니다.";

    public static final String PROBLEM_LEVEL_TYPE_MISMATCH = "\'problem_level\'값이 형식에 맞지 않습니다.";

}
