package com.mildo.dev.api.study.domain.entity;

import com.mildo.dev.api.user.domain.entity.UserEntity;
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

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

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
    private List<UserEntity> userEntity = new ArrayList<>();


}
