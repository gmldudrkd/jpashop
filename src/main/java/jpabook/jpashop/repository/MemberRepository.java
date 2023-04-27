package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.security.PublicKey;
import java.util.List;

@Repository //component 스캔에 의해 자동으로 스프링 빈으로 관리가 된다
@RequiredArgsConstructor
public class MemberRepository {
    //@PersistenceContext
    //@Autowired //아래 생성자 인젝션을 사용하면 부트에서 PersistenceContext를 Autowired로 제공해줌
//    public MemberRepository(EntityManager em){
//        this.em = em;
//    }

    private final EntityManager em;

    public void save(Member member){
        em.persist(member);
    }

    public Member findOne(Long id){
        return em.find(Member.class, id);
    }

    public List<Member> findAll(){
        //jpql > entity 객체를 대상으로 쿼리를 진행- from의 대상이 객체(엔티티 멤버 m을 대상으로 조회해!) /  Member.class 는 조회 타입
        List<Member> result = em.createQuery("select m from Member m", Member.class)
                .getResultList();
        return result;
    }

    public List<Member> findByname(String name){
        return em.createQuery("select m from Member m where m.name=:name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
