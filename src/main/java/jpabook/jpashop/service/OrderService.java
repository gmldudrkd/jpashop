package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    //주문
    @Transactional //데이터의 변경
    public Long order(Long memberId, Long itemId, int count) {
        //엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        //배송 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        //주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        //주문생성
        Order order = Order.createOrder(member, delivery, orderItem);

        //주문 저장
        orderRepository.save(order);
        //delivery 와 orderItem은 따로 save 해야하지만 order 엔티티에 casecade 옵션이 설정되어 있어 order만 세이브해도 자동저장됨
        //이경우 주문,배송,아이템의 라이프사이클이 완전히 동일한 경우임, 조심해서 사용해야함
        return order.getId();
    }

    //취소
    @Transactional
    public void cancelOrder(Long id){
        Order order = orderRepository.findOne(id);
        order.cancel();
        //JPA 를 사용하기 때문에 cancel을 호출하면 변경된 내역을 찾아서 업데이트 쿼리를 날려줌
        //(엔티티에서 변경된 내용을 찾음)
        //addStock의 경우 stockQuantity 값이 변경 > 업데이트 쿼리 진행
    }

    //검색
//    public List<Order> findOrders(OrderSearch orderSearch){
//        return orderRepository.findAll(orderSearch);
//    }
}
