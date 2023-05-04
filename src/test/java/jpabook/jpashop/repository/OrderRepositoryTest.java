package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class) //junit 실행 시 spring 이랑 같이 테스트
@SpringBootTest //spring boot 안에서 테스트 - autowired 사용
@Transactional
class OrderRepositoryTest {

    @Autowired OrderRepository orderRepository;

    @Test
    void findAllWithItem() {
    }
}