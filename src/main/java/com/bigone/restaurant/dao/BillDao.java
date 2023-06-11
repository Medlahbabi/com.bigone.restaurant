package com.bigone.restaurant.dao;

import com.bigone.restaurant.POJO.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillDao extends JpaRepository<Bill, Integer> {
}
