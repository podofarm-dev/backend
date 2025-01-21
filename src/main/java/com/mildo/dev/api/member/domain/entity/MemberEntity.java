package com.mildo.dev.api.member.domain.entity;

import com.mildo.dev.api.code.domain.entity.CodeEntity;
import com.mildo.dev.api.code.domain.entity.CommentEntity;
import com.mildo.dev.api.study.domain.entity.StudyEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "member_dev")
public class MemberEntity {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private String memberId;

    @Column(name = "member_name")
    private String name;

    @Column(name = "member_googleid")
    private String googleId;

    @Column(name = "member_email")
    private String email;

    @Column(name = "member_solvedproblem")
    private int solvedProblem;

    @Column(name = "member_leader")
    private String leader;

    @Column(name = "member_isparticipant")
    private Date isParticipant; // 스터디 시작일

    @Column(name = "member_date", updatable = false)
    @CreationTimestamp
    private Timestamp createDate; // 유저 생성일

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    private StudyEntity studyEntity;

    @OneToMany(mappedBy = "memberEntity")
    private List<CodeEntity> codeList = new ArrayList<>();

    @OneToMany(mappedBy = "memberEntity")
    private List<CommentEntity> commentEntityList = new ArrayList<>();

    @OneToOne(mappedBy = "memberEntity")
    private TokenEntity tokenEntity;

}
