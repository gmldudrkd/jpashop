package jpabook.jpashop.study;

public class KrStore extends GetStore {
    @Override
    Store getStore(String type) {
        switch (type) {
            case "hongdae":
                return null;
            case "sinsa":
                return null;
            default:
                return null;
        }
    }
}
