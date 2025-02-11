package com.mildo.dev.api.problem.domain.entity;

import com.mildo.dev.api.code.domain.entity.CodeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "problem")
public class ProblemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "problem_no")
    private Long problemNo;

    @Column(name = "problem_title")
    private String problemTitle;

    @Column(name = "problem_level")
    private String problemLevel;

    @Column(name = "problem_link")
    private String problemLink;

    @Column(name = "problem_readme")
    private String problemReadme;

    @Column(name = "problem_type")
    private String problemType;

    @Column(name = "problemd_")
    private String problemId;

    @OneToMany(mappedBy = "problemEntity")
    private final List<CodeEntity> codeList = new ArrayList<>();

}
