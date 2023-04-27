package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class) //junit 실행 시 spring 이랑 같이 테스트
@SpringBootTest //spring boot 안에서 테스트 - autowired 사용
@Transactional
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;

    @Test
    //@Rollback(value = false) //jpa에서 commit시 (영속성 컨텍스트 플러쉬 시) 데이터 저장되는데 테스트에서 Transactional은 롤백을 진행해 로그에서 값을 확인불가
    public void join() throws Exception{
        //given
        Member member = new Member();
        member.setName("kim");
        //when
        Long saveID = memberService.join(member);
        //then
        em.flush(); //값을 보고싶다면 해당라인 추가 혹은 rollback -false
        Assert.assertEquals(member, memberRepository.findOne(saveID));
    }

    @Test
    public void duple() throws Exception{
        //given
        Member member1 = new Member();
        member1.setName("kim");
        Member member2 = new Member();
        member2.setName("kim");

        //when
        memberService.join(member1);
        try {
            memberService.join(member2); //예외 발생
        }catch (IllegalIdentifierException e){
            return;
        }

        //then
        fail("예외가 발생해야 합니다.");
    }
}