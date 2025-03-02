package com.podofarm.dev.api.problem.domain.entity;

import com.podofarm.dev.api.code.domain.entity.CodeEntity;
import jakarta.persistence.*;
import lombok.*;

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

    @Id
    @Column(name = "problem_id")
    private Long problemId;

    @Column(name = "problem_solution")
    private String problemSolution;

    @OneToMany(mappedBy = "problemEntity")
    private final List<CodeEntity> codeList = new ArrayList<>();


}
