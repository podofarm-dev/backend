package com.mildo.dev.api.member.domain.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.mildo.dev.api.code.domain.entity.CodeEntity;
import com.mildo.dev.api.code.domain.entity.CommentEntity;
import com.mildo.dev.api.study.domain.entity.StudyEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "member")
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

    @Column(name = "member_img_url")
    private String imgUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    private StudyEntity studyEntity;

    @OneToMany(mappedBy = "memberEntity", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final List<CodeEntity> codeList = new ArrayList<>();

    @OneToMany(mappedBy = "memberEntity", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final List<CommentEntity> commentEntityList = new ArrayList<>();

    @OneToOne(mappedBy = "memberEntity", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private TokenEntity tokenEntity;

//    @OneToOne(mappedBy = "memberEntity")
//    @JsonManagedReference
//    private MemberImgEntity memberImgEntity;

}
