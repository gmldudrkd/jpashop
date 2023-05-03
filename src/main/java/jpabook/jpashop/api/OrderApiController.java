package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.Orderstatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/*
<entitiy 조회>
* V1: 엔티티를 그대로 반환 > 사용지양!
* V2:  엔티티 조회 후 DTO 로 변환
* V3:  페치 조인으로 쿼리 수 최적화 (join 과 다르게 영속화를 시켜 1+N 문제해결) , 그러나! 컬렉션조회 시 페이징이 불가
* V3.1:  컬렉션 페이징 처리 : toOne 관계 = 페치조인, toMany = 지연로딩을 유지(페치조인이 영속화, 페치조인 안하면 영속화 안함), default_batch_fetch_size 옵션으로 처리
<DTO 조회>
* V5: 컬렉션조회 최적화 :  일대다 관계일 경우 IN절 사용해 메모리에 올려서 사용
* V6: 전부 Join 후 애플리케이션에서 원하는 모양으로 변경

권장!
- 엔티티로 조회 후 페치조인으로 쿼리 수 최적화, 컬렉션 있을 경우 페이징필요시 default_batch_fetch_size옵션사용 , 필요없을 경우 페치조인!
    -> 엔티티권장이유 : 엔티티조회는 옵션만
- 엔티티 조회 방식으로 안될 경우 DTO 조회 사용
* */

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> orderV1() {
        //entitiy를 직접노출하는 방법 > 사용하지 않아야함.
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName()); //item을 가져오며 초기화
        }
        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return collect;
    }

    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        //fetch 조인의 사용, 조인되면 데이터가 1:다 인경우 다쪽의 갯수로 노출됨
        // 1개 오더, 2개 아이템 > 로우는 2개 :: distinct 사용

        List<Order> orders = orderRepository.findAllWithItem();
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return collect;
    }

    // 페이징처리
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit
    ) {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit); //member, delivert (~toOne) 만 조회

        // default_batch_fetch_size (뒤의 숫자는 몇번 inquery를 날릴 것인가)옵션추가 시 Orders와 관련된 아이템을 미리 in query를 날려서 가져옴
        // 해당옵션으로 인해 IN 절로 쿼리수가 현저히 작아짐
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return collect;

        //v3 은 쿼리가 한번이지만 불필요한 데이터가 한번에 묶여서 나옴 > 메모리를 많이 차지함, 3.1 버전은 쿼리는 비교적 많지만 필요한 데이터만 가져오므로 데이터양, 환경 등을 고려해 선택
    }

    //DTO로 조회 (쿼리문에 DTO 객체를 조회)
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }

    //DTO로 조회 최적화 (1+N 문제해결)
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5() {
        return orderQueryRepository.findAllByDto_optimize();
    }

//    @GetMapping("/api/v6/orders")
//    public List<OrderQueryDto> ordersV6() {
//        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();
//        //OrderFlatDto 를 OrderQueryDto 형식으로 조회하려면 돌리면서 OrderQueryDto에 맞게 데이터 변경..
//    }

    @Data
    static class OrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private Orderstatus orderstatus;
        private Address address;
        private List<OrderItemDto> orderItems; //dto 에도 엔티티가 있으면안된다, 엔티티의 의존을 완전히 끊어야한다!

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderstatus = order.getStatus();
            address = order.getDelivery().getAddress();

            //dto 에도 엔티티가 있으면안된다
//            order.getOrderItems().stream().forEach(o -> o.getItem().getName());
//            orderItems = order.getOrderItems();

            //item도 dto처리
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList());
        }
    }

    @Data
    static class OrderItemDto {
        private String itemname;
        private int orderPrice;
        private int count;
        public OrderItemDto(OrderItem orderItem) {
            itemname = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }


}
