package com.lab.vm.model.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name="videos")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Video implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name ="video_id")
    private Long id;

    @Column(name="video_name")
    private String name;

    @Column(name="video_size")
    private Long size;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;         //연관관계의 주인


    @Column(name="upload_date")
    private LocalDateTime uploadDate;   // 하이버네이트가 알아서 포맷 지원


}
