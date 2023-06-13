package com.bigone.restaurant.dao;

import com.bigone.restaurant.POJO.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillDao extends JpaRepository<Bill, Integer> {
    List<Bill> getAllBills();

    List<Bill> getBillByUserName(String currentUsername);
}
