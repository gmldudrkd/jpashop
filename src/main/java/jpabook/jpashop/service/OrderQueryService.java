package jpabook.jpashop.service;

import org.springframework.transaction.annotation.Transactional;

//쿼리용 서비스 분리
/*
* OSIV(open session in view) 는 기본이 true, false일 경우 트랜잭션이 동작하는 동안에만 영속성 컨텍스트를 유지
* 계속 켜놓을 시 데이터소스 커넥션이 길어짐, 장애가 발생할 수 있음
* 끄고 영속성 컨텍스트를 repository, service 계층에서만 사용하도록 처리!!
*
* * 커멘드와 쿼리의 분리 > Osiv 를 끄고 복잡성을 관리하는 방법!
* 보통 쿼리를 조회하는데 성능이슈가 많이 발생
* 애플리케이션 개발 시 비지니스 로직(등록, 수정 - 커맨드) 과 복잡한 화면을 출력하기 위한 조회(쿼리)를 명확하게 분리!!
* ex, OrderService (핵심 비지니스 로직) / OrderQueryService (화면 혹은 API 에 맞춘 서비스, 주로 읽기전용 트랜잭션 사용)
* >> 보통 서비스 계층에서 트랜잭션을 유지 , 두서비스 두 트랜잭션을 유지 (osiv 꺼도) 지연로딩을 사용가능
*
* osiv 를 켜면 지연로딩을 어디서도 사용가능( 컨트롤러 등...)
* osiv 를 끄면 성능이 좋아짐! 왜냐면 커넥션이 유지되지 않아 쾌적함 >> 트래픽이 많은(API) 경우 커넥션이 마를 수 있어 끄자!
* 그러나 어드민 같은 경우 켜놔도,, 무방!! (커넥션이 많지 않으므로)
*
* 결국 !! 필요한 곳에 적절하게 사용하자!
* */
@Transactional(readOnly = true)
public class OrderQueryService {
}
