package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @NotEmpty
    private String name;

    @Embedded //내장타입을 포함
    private Address address;

    @OneToMany(mappedBy = "member") //order 테이블(class)의 member에 매핑되어 따라간다
    private List<Order> orders = new ArrayList<>();
}
