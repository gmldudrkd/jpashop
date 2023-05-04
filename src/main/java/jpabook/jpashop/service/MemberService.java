package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.MemberRepositoryOld;
import lombok.RequiredArgsConstructor;
import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
//Jpa의 데이터 관련은 기본적으로 트랜젝션 안에서 동작해야함
@Transactional(readOnly = true) //조회 시 성능을 최적화하는 옵션 - 읽기전용 트랜잭션임을 알려줌
//@AllArgsConstructor
@RequiredArgsConstructor //final 이 붙은 레포를 인젝션
public class MemberService {

    //@Autowired //빈에 등록된 리포지토리를 인젝션(주입)해줌

//    @Autowired //setter injection - 값을 받아서 주입, 변경이 가능(장점이자 단점)
//    public void setMemberRepository(MemberRepositoryOld memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    // AllArgsConstructor 어노테이션이 있으면 생성자 자동생성해서 아래내용 필요없음
//    //@Autowired //생성자 인젝션 - 생성할 떼 주입완료, 중간에 Set 해도 변경 없음, 테스트 케이스 작성 시 직접주입가능 (autowired 없애도 자동으로 스프링이 인젝션)
//    public MemberService(MemberRepositoryOld memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    private final MemberRepository memberRepository;

    //회원가입
    @Transactional //읽기 옵션이 아니므로 따로 선언
    public Long join(Member member){
        //중복체크
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    @Transactional
    public void update(Long id, String name){
        Member member = memberRepository.findById(id).get();
        member.setName(name);
        //transaction 이 끝나는 시점에 jpa가 변경감지해서 commit
    }

    private void validateDuplicateMember(Member member){
        List<Member> findMembers = memberRepository.findByname(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalIdentifierException("이미 존재하는 회원입니다.");
        }
        //동시성 문제가 잇으니 최후 한번더 확인
    }

    //전체조회
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    //단건조회
    public Member findOne(Long memberId){
        return memberRepository.findById(memberId).get();
    }
}
