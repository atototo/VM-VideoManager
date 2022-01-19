package com.lab.vm.model.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;


/**
 * packageName : com.lab.vm.model.domain
 * fileName : Authority
 * author : yelee
 * date : 2022-01-18
 * description : 권한 정보 Entity
 * ===========================================================
 * DATE                  AUTHOR                  NOTE
 * -----------------------------------------------------------
 * 2022-01-18              yelee             최초 생성
 */
@Entity
@Table(name = "authority")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Authority  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "name", length = 50)
    @NotNull
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Authority authority = (Authority) o;
        return name == authority.name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }


    public Authority(String name) {
        this.name = name;
    }
}
