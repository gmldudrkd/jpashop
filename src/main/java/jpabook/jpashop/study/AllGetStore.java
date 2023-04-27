package jpabook.jpashop.study;

public class AllGetStore {

    Store store;

    //    public Store getStore(String country){
    //        if (country.equals("korea")) store = new KrStore();
    //        if (country.equals("canada")) store = new CANStore();
    //        if (country.equals("japan")) store = new JPStore();
    //        if (country.equals("china")) store = new CNStore();
    //
    //        return store;
    //    }


}

/*
AllGetStore -> KrStore
AllGetStore -> CANStore
AllGetStore -> JPStore
AllGetStore -> CNStore

new 로 종속되어 있어 의존해야한다!
*/