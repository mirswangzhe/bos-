package com.itheima.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itheima.dao.CustomerDao;
import com.itheima.domain.Customer;
import com.itheima.service.CustomerService;
@Service
@Transactional
public  class customerServiceImpl implements CustomerService{
	@Autowired
	private CustomerDao customerDao;
	
	
	@Override
	public List<Customer> findnocustomer() {
		
		return customerDao.findByFixedAreaIdIsNull();
	}

	@Override
	public List<Customer> findhascustomer(String fixedAreaId) {
		
		return customerDao.findByFixedAreaId(fixedAreaId);
	}

	@Override
	public void putcustomer(String customerIdStr, String fixedAreaId) {
		customerDao.clearFixedArea(fixedAreaId);
		System.out.println(customerIdStr+"================");
		if(StringUtils.isBlank(customerIdStr)){
			return;
		}
		String[] split = customerIdStr.split(",");
		for (String idstr : split) {
			Integer id = Integer.parseInt(idstr);
			customerDao.updateFixedAreaId(fixedAreaId,id);
		}
	}

	@Override
	public void regist(Customer customer) {
		System.out.println(customer);
		customerDao.save(customer);
	}

	@Override
	public Customer findByTelephone(String telephone) {
		return customerDao.findByTelephone(telephone);
	}

	@Override
	public void updateType(String telephone) {
		customerDao.updateType(telephone);
	}

	@Override
	public Customer login(String telephone, String password) {
		return customerDao.findByTelephoneAndPassword(telephone,
				password);
	}

	@Override
	public String findFixedAreaIdByAddress(String address) {
		return customerDao.findFixedAreaIdByAddress(address);
	}



	
	
	

}
