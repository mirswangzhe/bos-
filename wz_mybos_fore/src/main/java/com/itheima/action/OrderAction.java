package com.itheima.action;


import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.itheima.domain.Area;
import com.itheima.domain.Constans;
import com.itheima.domain.Customer;
import com.itheima.domain.take_delivery.Order;
import com.opensymphony.xwork2.ModelDriven;

@Namespace("/")
@ParentPackage("struts-default")
@Scope("prototype")
@Controller
public class OrderAction implements ModelDriven<Order>{
	private Order order=new Order();
	
	
	
	private String sendAreaInfo;
	private String recAreaInfo;
	
	public void setSendAreaInfo(String sendAreaInfo) {
		this.sendAreaInfo = sendAreaInfo;
	}
	public void setRecAreaInfo(String recAreaInfo) {
		this.recAreaInfo = recAreaInfo;
	}
	@Action(value="order_add",results={@Result(name="succse",type="redirect",location="index.html")})
	public String orderadd(){
		System.out.println("order_add...............");
		System.out.println(sendAreaInfo);
		System.out.println(recAreaInfo);
		//封裝area
		Area sendArea=new Area();
		
		String[]  sendarea= sendAreaInfo.split("/");
		sendArea.setProvince(sendarea[0]);
		sendArea.setCity(sendarea[1]);
		sendArea.setDistrict(sendarea[2]);
		
		Area recArea=new Area();
		String[]  recarea= recAreaInfo.split("/");
		recArea.setProvince(recarea[0]);
		recArea.setCity(recarea[1]);
		recArea.setDistrict(recarea[2]);
		
		order.setRecArea(recArea);
		order.setSendArea(sendArea);
		//从session中取出登录时存进的customer信息
		Customer customer = (Customer) ServletActionContext.getRequest().getSession().getAttribute("customer2");
		order.setCustomer_id(customer.getId());
		//用webservice调用wz_bos_management的信息
		WebClient.create(Constans.wz_bos_management_url+"/wz_bos_management/service/OrderService/order")
		.accept(MediaType.APPLICATION_JSON)
		.post(order);
		return "succse";
	}
	
	
	@Override
	public Order getModel() {
		return order;
	}

}
