package jpabook.jpashop.repository;

import jpabook.jpashop.api.OrderSimpleApiController;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    //검색 메서드, 추후 개발
//    public List<Order> findAll(OrderSearch orderSearch) {
//        em.createQuery("select 0 from Order o join o.member m" +
//                " where o.status=:status and m.name like :name", Order.class)
//                .setParameter("status", orderSearch.getOrderStatus())
//                .setParameter("name", orderSearch.getMemberName())
//                .setMaxResults(1000) //최대 1000건
//                .getResultList();
//    }

    //JPQL 로 처리
    public List<Order> findAllByString(OrderSearch orderSearch) {
        //language=JPAQL
        String jpql = "select o From Order o join o.member m";
        boolean isFirstCondition = true;
        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }
        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000); //최대 1000건
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();
    }

    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class
        ).getResultList();
        //fetch join : 조인된 객체를 지연로딩(LAZY)와 관계없이 내용을 select 해서 조인!
    }

    public List<Order> findAllWithItem() {
        // jpa의 distinct는 엔티티(Order)가 중복일 경우 제거해준다, (원래 데이터베이스는 완전 동일한 로우일 경우제거)
        //fetch join 의 경우 페이징이 불가, 모든데이터를 조회, 메모리에서 페이징해버림>> 우ㅣ험함!
        //조인이 많을 수록 데이터의 정합성이 떨어져서 (여러 데이터가 기준없이 혼합되어 나오므로) limit 가 불가
        return em.createQuery(
                "select distinct o from Order o"
                + " join fetch o.member m"
                + " join fetch  o.delivery d"
                + " join fetch  o.orderItems oi"
                + " join fetch  oi.item i" , Order.class)
                .getResultList();
    }

    //member, delivert (~toOne) 은 페이지 조회가능
    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        //fetch join 으로 잡을 수 있는건 조인하고 나머지 tomany 관계는 페이징으로
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }
}
