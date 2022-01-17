package com.lab.vm.model.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name="VIDEOS")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Video implements Serializable {

    @Id
    @GeneratedValue
    @Column(name ="VIDEO_ID")
    private Long id;

    @Column(name="VIDEO_NAME")
    private String name;

    @Column(name="VIDEO_SIZE")
    private Long size;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="USER_ID")
    private User user;         //연관관계의 주인


    @Column(name="UPLOAD_DATE")
    private LocalDateTime uploadDate;   // 하이버네이트가 알아서 포맷 지원


}
