package com.lab.vm.model.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lab.vm.model.dto.RegisterDto;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;


/**
 * packageName : com.lab.vm.model.domain
 * fileName : User
 * author : yelee
 * date : 2022-01-18
 * description : User 정보 Entity
 * ===========================================================
 * DATE                  AUTHOR                  NOTE
 * -----------------------------------------------------------
 * 2022-01-18              yelee             최초 생성
 */
@Entity
@Table(name = "user")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SqlResultSetMapping(
        name = "register_dto",
        classes = @ConstructorResult(
                targetClass = RegisterDto.class,
                columns = {
                        @ColumnResult(name = "id", type = Long.class),
                        @ColumnResult(name = "password", type = String.class),
                        @ColumnResult(name = "username", type = String.class),
                        @ColumnResult(name = "email", type = String.class),
                        @ColumnResult(name = "phone", type = String.class)
                }
        )
)
@NamedNativeQuery(
        name = "find_user_by_name_dto",
        query ="select m.id AS id, m.password AS password, m.username AS username,m.email AS email, m.phone AS phone from user m where m.username = :username",
        resultSetMapping = "register_dto"
)
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonIgnore
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 1)
    private Long id;

    @Column(name = "username", length = 50, unique = true)
    private String username;

    @JsonIgnore
    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "email", length = 50)
    private String email;

    @Column(name="phone")
    private String phone;

    @JsonIgnore
    @Column(name = "activated")
    private boolean activated;

    @ManyToMany
    @JoinTable(
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "name")})
    @BatchSize(size = 20)
    @Builder.Default
    private Set<Authority> authorities = new HashSet<>();


    @JsonManagedReference
    @OneToMany(mappedBy ="user", fetch = FetchType.LAZY)
    @BatchSize(size = 20)
    @Builder.Default
    private List<Video> videos = new ArrayList<>();



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", activated=" + activated +
                '}';
    }
}
