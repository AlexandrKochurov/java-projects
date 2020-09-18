package main;

import main.model.Business;

import java.util.concurrent.ConcurrentHashMap;

public class Storage {
    private static ConcurrentHashMap<Integer, Business> businesses = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<Integer, Business> getBusinesses() {
        return businesses;
    }

    public static Business getBusinessById(int id) {
        return businesses.get(id);
    }

    public static synchronized int putBusiness(Business business){
        int id = businesses.size() + 1;
        business.setId(id);
        businesses.put(id, business);
        return id;
    }

    public static void removeBusiness(Business business) {
        businesses.remove(business.getId());
    }

    public static void clearBusiness() {
        businesses.clear();
    }
}
