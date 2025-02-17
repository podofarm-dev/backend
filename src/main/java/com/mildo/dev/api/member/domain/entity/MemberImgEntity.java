package com.mildo.dev.api.member.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "member_img")
public class MemberImgEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "img_no")
    private String imgNo;

    @Column(name = "img_url")
    private String imgUrl;

    @Column(name = "img_origin_name")
    private String imgOriginName;

    @Column(name = "img_change_name")
    private String imgChangeName;

//    @OneToOne
//    @JoinColumn(name = "member_id")
//    @JsonBackReference
//    private MemberEntity memberEntity;

}
