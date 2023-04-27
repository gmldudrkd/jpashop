package jpabook.jpashop.study;

public abstract class GetStore {
    Store store;

    abstract Store getStore(String type);
}

/*
AllGetStore -> Store
Store <- CANStore
Store <- JPStore
Store <- CNStore
*/