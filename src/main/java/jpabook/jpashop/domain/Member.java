package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

//entitiy에는 화면을 위한, api를 위한 로직이 있어서는 안된다! -> Ex, @Jsonignore 과 같은 노출제외 어노테이션 등... >> 엔티티 변경 시 API 스펙이 변경된다!
//사용하는 곳에서 DTO 를 선언!

@Entity
@Getter @Setter
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded //내장타입을 포함
    private Address address;

    @JsonIgnore //양방향 연관관계일 경우 한방향은 끊어줘야한다.
    @OneToMany(mappedBy = "member") //order 테이블(class)의 member에 매핑되어 따라간다
    private List<Order> orders = new ArrayList<>();
}
