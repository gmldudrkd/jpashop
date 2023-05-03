package jpabook.jpashop.repository.order.query;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Orderstatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderQueryDto {

    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private Orderstatus orderstatus;
    private Address address;
    private List<OrderItemQueryDto> orderItems;

    // jpql은 list를 매개변수로 넣을 수 없다.
    public OrderQueryDto(Long orderId, String name, LocalDateTime orderDate, Orderstatus orderstatus, Address address) {
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderstatus = orderstatus;
        this.address = address;
    }
}
