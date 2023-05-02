package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.Orderstatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import jpabook.jpashop.repository.order.simplequery.SimpleOrderQueryDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/*
 * xToOne 관계 최적화
 * order
 * order -> member
 * order -> delivery
 * */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> OrdersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        //오류 1. order -> member -> order ... 무한루프 돔 ==> @JsonIgnore 처리해서 양방향중 하나를 끊어줌
        //오류 2. @ManyToOne(fetch = FetchType.LAZY) -> 지연로딩 (해당 객체를 사용하는 순간에 쿼리를 날려서 값을 가져옴) 으로 인해 맨처음 리스트를 띄울때 값을 찾지 못함
        //      ==> 해당값을 뿌리지 말라고 하이버네이트에게 명령( 모듈 등록) hibernate5Module

        // entitiy을 그대로 쓴 케이스 > api 스펙이 변경되면 젼체변경, 원하지 않는 내용들을 가져옴 (카테고리, 엔티티 주문에 엮인 주문아이템 등,...)
        for (Order order : all) {
            order.getMember().getName(); //LAZY 강제 초기화하고 지연로딩이지만 Hibernate가 쿼리에서 조회함
            order.getMember().getAddress();
        }
        return all;
    }

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2(){
        //DTO 로 변환
        //문제 1 : LAZY 로딩으로 인한 너무 많은 쿼리호출 (SimpleOrderDto)
        List<Order> orders = orderRepository.findAllByString(new OrderSearch()); //2개의 order가 나옴
        //stream > 결국엔 루프 돌리는 것! (order 2바퀴)
        //member, delivery의 LAZY 로딩으로 인해 오더1개당 2번의 쿼리호출, 2개면 총 4번,, 이게 많아지면,,!! >> 1+N 문제
        //1+N 문제:: 첫번째 쿼리 결과로 n 번만큼 쿼리가 추가실행 되는 것
        // order N 개 -> 1 + 회원(N) + 배송(N) = 1+2+2 :: 첫번째 orders 실행결과가 2, 쿼리가 2만큼 실행됨!
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithMemberDelivery(); //fetch 쿼리 적용 > member, delivery 객체가 조인되서 같이 조회되서 나옴!
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        return result;
    }

    @GetMapping("/api/v4/simple-orders")
    public List<SimpleOrderQueryDto> ordersV4(){
        //DTO에 바로 접근해서 정보가져오기
        // dto 를 조회 > api 스펙을 그대로 가져오는것
        //repository는 가급적 엔티티 자체를 조회, 수정 ,, 등 하는 용도로 사용해야함. > 복잡한 쿼리로 dto를 뽑을땐 따로 두기 >> orderSimpleQueryRepository
        // repository는 controller 와 의존관계가 생기면 안된다. 의존관계는 안으로 들어와야함 C -> S -> R (C->R) 이런식으로 한방향이어야함!!
        return orderSimpleQueryRepository.findOrderDtos();
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private Orderstatus orderstatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); // LAZY 초기화 -> 영속성 컨텍스트에서 해당 값을 찾아보고 없으면 쿼리 날림
            orderDate = order.getOrderDate();
            orderstatus = order.getStatus();
            address = order.getDelivery().getAddress(); // LAZY 초기화 -> 영속성 컨텍스트에서 해당 값을 찾아보고 없으면 쿼리 날림
        }
    }

}
