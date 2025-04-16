package com.user.entity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMasterRepo extends JpaRepository<UserMaster, Integer>{

	public UserMaster findByEmail(String email);
}
