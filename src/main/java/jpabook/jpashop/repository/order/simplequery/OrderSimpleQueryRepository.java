package jpabook.jpashop.repository.order.simplequery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    private final EntityManager em;

    public List<SimpleOrderQueryDto> findOrderDtos() {
        //api 에 맞춰진 쿼리로, 재사용성이 작다! 화면에 dependency한 쿼리 > 일반 repository 성향과는 다름
        //repository는 가급적 엔티티 자체를 조회, 수정 ,, 등 하는 용도로 사용해야함. > 복잡한 쿼리로 dto를 뽑을땐 따로 두기
        // repository는 controller 와 의존관계가 생기면 안된다. 의존관계는 안으로 들어와야함 C -> S -> R (C->R) 이런식으로 한방향이어야함!!
        // new 오퍼레이트 에서는 o(Order 엔티티) 자체를 넘기면 안됨, 엔티티가 식별자로 넘어감
        return em.createQuery(
                        "select new jpabook.jpashop.repository.order.simplequery.SimpleOrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address) from Order o" +
                                " join o.member m"+
                                " join o.delivery d", SimpleOrderQueryDto.class)
                .getResultList();
    }

}
