package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController //(responsebody > 데이터를 Json 형식으로 리턴)
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){ //entity 자체를 param 으로 받으면 변경될 가능성이 있어서 DTO 로 빼서 진행
        //json 으로 온 데이터를 member에 넣어준다, @Valid > 넘어온 json 을 validation으로 변경

        //REQUEST 값을 엔티티자체로 받을 경우 api 와 1:!로 매핑되어 api 스펙이 변경되면 엔티티가 변경되어야해서 불편함
        // 엔티티를 외부에서 바인딩 받는 데이터로 사용하면 안됨!!! > api 스펙에 맞춰 별도의 Dto로 받기
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
        //오류 관련 처리른 controller advice 같은거 잡아서 처리 , 부트 페이지 참고
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        //엔티티를 외부에 노출하거나 파람으로 그대로 받는건 지양하기! CreateMemberRequest dto을 만들어서 지정
        Member member = new Member();
        member.setName(request.name);
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id, @RequestBody @Valid UpdateMemberRequest request ) {
        //DTO 로 request, response 둘다 진행
        memberService.update(id,request.getName());
        Member findmember = memberService.findOne(id); //update 하면서 member 내용을 가져올 수 있지만 그러면 업데이트와 동시에 조회하는 꼴이기 때문에 조회는 따로 진행!
        //조회, 수정은 분리해서 진행
        return new UpdateMemberResponse(findmember.getId(), findmember.getName()); //allarg 어노테이션으로 인해 반환값이 전체
    }

    @GetMapping("/api/v1/members")
    public List<Member> membersV1() { //list 같은 array 로 바로 반환 시 유연성이 떨어진다 > {data:[], count:""} 형식으로 가야함!
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result memberV2() {
        List<Member> findmembers = memberService.findMembers();
        List<MemberDto> collect = findmembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());

        return new Result(collect.size(), collect); //리스트로 반환된 collect(json 배열 타입) 에 Result 라는 껍데기를  씌워준다. -> {data:[], count:""} 이런 형태를 위해 (유연성)
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data3;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }



    @Data
    static class UpdateMemberRequest{
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse{
        private Long id;
        private String name;
    }

    @Data
    static class CreateMemberRequest {
        //api 스펙에 맞춘 Request, 엔티티가 변경되어도 영향없음,
        @NotEmpty
        private  String name;
    }

    @Data
    static class CreateMemberResponse{
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }
}
