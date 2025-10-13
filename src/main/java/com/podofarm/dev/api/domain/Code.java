package com.podofarm.dev.api.domain;

import com.podofarm.dev.api.member.domain.entity.MemberEntity;
import com.podofarm.dev.api.problem.domain.entity.ProblemEntity;
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
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "code")
public class Code {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code_no")
    private Long codeNo;

    @Column(name = "code_source")
    private String codeSource;

    @Column(name = "code_solved_date")
    private Timestamp codeSolvedDate;

    @Column(name = "code_annotation")
    private String codeAnnotation;

    @Column(name = "code_status")
    private Boolean codeStatus;

    @Column(name = "code_time")
    private Time codeTime;

    @Column(name = "code_performance")
    private String codePerformance;

    @Column(name = "code_accuracy")
    private String codeAccuracy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity memberEntity;

    @OneToMany(mappedBy = "codeEntity")
    private final List<Comment> commentList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private ProblemEntity problemEntity;

}

