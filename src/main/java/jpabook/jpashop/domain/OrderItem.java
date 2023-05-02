package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.jpashop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

// 주문상품 생성은 생성메서드를 통해 진행해야한다, 서비스 단에서 객체를 선언해 사용하게 되면
// 로직이 다다르고, 추후 데이터 추가시 번거로울 수 있다. >> NoArgsConstructor
// OrderItem orderItem1 = new OrderItem(); 이렇게 사용시 컴파일 오ㅠ

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {
    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item; //item은 연관관계가 아닌 상속관계라 ITEM클래스에는 Onetomany 선언없음

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
    private int orderPrice;
    private int count; //주문수량

    //생성메서드
    public static OrderItem createOrderItem(Item item, int orderPrice, int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count); //재고를 다시 주문수량 만큼 없앰

        return orderItem;
    }

    //비지니스 로직
    public void cancel(){
        getItem().addStock(count); //재고를 다시 주문수량 만큼 늘림
    }

    public int getTotalPrice(){
        return getOrderPrice()*getCount();
    }
}
