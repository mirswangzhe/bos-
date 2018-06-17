package com.itheima.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.itheima.domain.Customer;

public interface CustomerDao extends JpaRepository<Customer, Integer> {

	List<Customer> findByFixedAreaIdIsNull();
	
	List<Customer> findByFixedAreaId(String fixedAreaId);
	
	@Query(value="update Customer set fixedAreaId=? where id=?")
	@Modifying
	void updateFixedAreaId(String fixedAreaId,Integer id);
	
	@Query(value="update Customer set fixedAreaId=null where fixedAreaId=?")
	@Modifying
	void clearFixedArea(String fixedAreaId);
	
	public Customer findByTelephone(String telephone);

	@Query("update Customer set type=1 where telephone= ?")
	@Modifying
	public void updateType(String telephone);

	public Customer findByTelephoneAndPassword(String telephone, String password);

	@Query("select fixedAreaId from Customer where address=?")
	public String findFixedAreaIdByAddress(String address);





}
