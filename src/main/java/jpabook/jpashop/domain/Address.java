package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class Address {
    private String city;
    private String street;
    private String zipcode;

    protected Address() { //JPA 스펙상 엔티티나 임베디드 타입은 자바 기본생성자를 설정해야함 (리플렉션과 같은 기능 지원을 위해)
    }

    // 값 타입은 변경이 안되도록 설계, setter 제공없이 최초 생성자로 처리
    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
