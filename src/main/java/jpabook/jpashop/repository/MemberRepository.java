package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//spring-data-jpa
//일반화 하기 어려운 기능도 메서드 이름으로 정확한 JPQL쿼리를 실행한다.

public interface MemberRepository extends JpaRepository<Member, Long> {

    // findBy~ 가 있을 경우 > select m from member m where m,name=? 으로 조회함!
    //따로 메소드를 만들지 않아도 됨! spring-data-jpa
    List<Member> findByname(String name);
}
