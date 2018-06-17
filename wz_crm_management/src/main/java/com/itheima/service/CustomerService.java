package com.itheima.service;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.itheima.domain.Customer;

@Produces("*/*")
public interface CustomerService {
	
	//未关联客户
	@GET
	@Path("/nocustomer")
	@Produces({"application/xml","application/json"})
	public List<Customer> findnocustomer();
	//已关联客户
	@GET
	@Path("/hascustomer/{fixedareaid}")
	@Produces({"application/xml","application/json"})
	public List<Customer> findhascustomer(@PathParam("fixedareaid") String fixedAreaId);
	
	//所有客户已关联到定区上的id
	@PUT
	@Path("/putcustomer")
	public void putcustomer(@QueryParam("customerIdStr") String customerIdStr,
			@QueryParam("fixedAreaId") String fixedAreaId);
	
	
	@Path("/postCustomer")
	@POST
	@Consumes({ "application/xml", "application/json" })
	public void regist(Customer customer);


	@Path("/findTelephone/{telephone}")
	@GET
	@Consumes({ "application/xml", "application/json" })
	public Customer findByTelephone(@PathParam("telephone") String telephone);

	@Path("/updateType/{telephone}")
	@GET
	public void updateType(@PathParam("telephone") String telephone);

	@Path("/loginForm")
	@GET
	@Consumes({ "application/xml", "application/json" })
	public Customer login(@QueryParam("telephone") String telephone,
			@QueryParam("password") String password);

	
	@Path("/findFixAreaIdByAddress")
	@GET
	@Consumes({ "application/xml", "application/json" })
	public String findFixedAreaIdByAddress(@QueryParam("address") String address);
}
