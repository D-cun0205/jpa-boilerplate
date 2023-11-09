package com.jpa.jpaboilerplate.repository;

import com.jpa.jpaboilerplate.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
