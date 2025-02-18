package com.mildo.dev.api.study.domain.entity;

import com.mildo.dev.api.member.domain.entity.MemberEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "study")
public class StudyEntity {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_id")
    private String studyId;

    @Column(name = "study_name")
    private String studyName;

    @Column(name = "study_pwd")
    private String studyPwd;

    @Column(name = "study_start")
    private Date studyStart;

    @Column(name = "study_end")
    private Date studyEnd;

    @OneToMany(mappedBy = "studyEntity")
    private final List<MemberEntity> memberEntityList = new ArrayList<>();

    public boolean containsAll(List<String> memberIds) {
        Set<String> result = memberEntityList.stream()
                .map(MemberEntity::getMemberId)
                .collect(toSet());

        return result.containsAll(memberIds);
    }

    public boolean contains(String memberId) {
        Set<String> result = memberEntityList.stream()
                .map(MemberEntity::getMemberId)
                .collect(toSet());

        return result.contains(memberId);
    }
}
