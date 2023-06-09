package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
public class Category {

    @Id @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(name = "category_item", //다대다 매핑 시 매핑할 테이블
            joinColumns = @JoinColumn(name = "category_id"), //해당 클래스의 매핑 키
            inverseJoinColumns = @JoinColumn(name = "item_id")) //상대방 클래스의 매핑키
    private List<Item> items = new ArrayList<>();

    //DB계층관계, 내부에서 매핑
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

    public void addChildCatogory(Category child){
        this.child.add(child);
        child.setParent(this);
    }

}
