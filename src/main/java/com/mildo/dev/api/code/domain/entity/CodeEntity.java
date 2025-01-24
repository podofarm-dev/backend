package com.mildo.dev.api.code.domain.entity;

import com.mildo.dev.api.problem.domain.entity.ProblemEntity;
import com.mildo.dev.api.member.domain.entity.MemberEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "code_dev")
public class CodeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code_no")
    private Long codeNo;

    @Column(name = "code_source")
    private String codeSource;

    @Column(name = "code_solveddate")
    @CreationTimestamp
    private Timestamp codeSolveddate;

    @Column(name = "code_annotation")
    private String codeAnnotation;

    @Column(name = "code_status")
    private String codeStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity memberEntity;

    @OneToMany(mappedBy = "codeEntity")
    private final List<CommentEntity> commentList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_no")
    private ProblemEntity problemEntity;

}
