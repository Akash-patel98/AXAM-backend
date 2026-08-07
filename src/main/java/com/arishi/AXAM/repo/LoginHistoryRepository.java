package com.arishi.AXAM.repo;


import com.arishi.AXAM.model.LoginHistory;
import com.arishi.AXAM.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {


    List<LoginHistory> findByUserOrderByLoginTimeDesc(Users user);

}
