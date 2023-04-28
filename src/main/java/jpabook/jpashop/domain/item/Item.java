package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotenoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) //상속관계 전략
@DiscriminatorColumn(name = "dtype") // 상속 시 DiscriminatorValue에 설정된 값으로 디비저장
@Getter @Setter
public abstract class Item {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    //==비지니스 로직==//
    //엔티티 자체가 해결 할 수 있는 것들은 데이터가 가지고 있는 엔티티에서 비지니스 로직을 실행
    //엔티티 자체가 해결 할 수 있는 것들은 데이터가 가지고 있는 엔티티에서 비지니스 로직을 실행
    //setter 없이 안의 로직으로 처리함 > 객체지향@@

    public void addStock(int quantity){
        //재고 증가
        this.stockQuantity += quantity;
    }

    public void removeStock(int quantity){
        //재고 감소
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0){
            throw new NotenoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }
}
