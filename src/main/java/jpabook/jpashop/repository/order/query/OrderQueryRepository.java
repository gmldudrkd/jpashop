package jpabook.jpashop.repository.order.query;

import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
    //API에 연관성이 있을때, 특정화면에 핏한 쿼리를 적음
    // 이외 다른 디렉토리의 리포지토리는 엔티티에 관한 쿼리
    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos() {
        //controller의 orderdto를 가져오게 되면 R->C 참조하게 되어 의존관계가 순환함, 그래서 OrderQueryDto 신규생성
        List<OrderQueryDto> result = findOrders(); //컬렉션 부분은 조회가 불가해 아래서 루프로 돌며 채운다.

        result.forEach( o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems); //컬렉션인 orderitems 채우기
        });

        return result;
        //order 1번 + order item 2번 > 첫번째 N개 조회 후 N번 쿼리 진행 ==> 1+N
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)"+
                        "from OrderItem oi" +
                        " join oi.item i"+
                        " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    private List<OrderQueryDto> findOrders() {
        //jpql new 객체에 list 형을 조회불가
        return em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                                " from Order o" +
                                " join o.member m" +
                                " join o.delivery d", OrderQueryDto.class)
                .getResultList();
    }

    /********DTO 최적화*********/
    public List<OrderQueryDto> findAllByDto_optimize() {
        List<OrderQueryDto> result = findOrders(); // toOne 관계를 먼저 조회
        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(result); // 위에서 얻은 식별자 ID로 toMany 관계인 Item을 한꺼번에 조회 (in절)
        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));
        return result;
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<OrderQueryDto> result) {
        // 쿼리를 한번 날리고 : orderItems - IN 절 사용 > 메모리의 결과를 가져와서 맵 형태로 변경 (findOrderItemMap) > 메모리에 올려놓은 맵의 내용으로 result 에 넣기
        List<Long> orderIds = result.stream()
                .map(o -> o.getOrderId())
                .collect(Collectors.toList());//가져온 주문을 stream으로 OrderQueryDto (o)를 orderid 리스트로 변경한다.

        List<OrderItemQueryDto> orderItems = em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                                "from OrderItem oi" +
                                " join oi.item i" +
                                " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId())); // lamda > Map 형식으로 변경. 키가 orderId. 값은 OrderItemQueryDto
        return orderItemMap;
    }

    public List<OrderFlatDto> findAllByDto_flat() {
        return em.createQuery(
                "select new"+
                        " jpabook.jpashop.repository.order.query.OrderFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)"+
                        " from Order o"+
                        " join o.member m"+
                        " join o.delivery d"+
                        " join o.orderItems oi"+
                        " join oi.item i" , OrderFlatDto.class)
                .getResultList();
    }
}
