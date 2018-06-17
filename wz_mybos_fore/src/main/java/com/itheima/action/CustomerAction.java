package com.itheima.action;



import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Controller;

import com.itheima.domain.Customer;
import com.itheima.utils.MailUtils;
import com.opensymphony.xwork2.ModelDriven;

@Namespace("/")
@ParentPackage("struts-default")
@Controller(value="controller01")
@Scope("prototype")
public class CustomerAction implements ModelDriven<Customer>{
	@Autowired
	@Qualifier("jmsQueueTemplate")
	private JmsTemplate jmsTemlate;
	
	private Customer customer=new Customer();
	@Action(value="customer_sendSms")
	public void sendMessage() throws Exception{
		System.out.println("景区方法");
		
		final String checkCode = RandomStringUtils.randomNumeric(6);
		ServletActionContext.getRequest().getSession().setAttribute(customer.getTelephone(),checkCode);
		System.out.println(checkCode);
		
		//SendSmsResponse result = SmsUtils.sendSms(customer.getTelephone(),checkCode);
		
		jmsTemlate.send("bos_sms",new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				MapMessage mapMessage = session.createMapMessage();
				mapMessage.setString("telephone",customer.getTelephone());
				mapMessage.setString("checkCode",checkCode);
				return mapMessage;
			}
		});
	}
	@Autowired
	private RedisTemplate<String,String> redisTemplate;
	@Action(value="customer_registForm",results={@Result(name="succse",type="redirect",location="signup-success.html"),
			@Result(name="false",type="redirect",location="signup.html")})
	public String regist(){
		System.out.println("进入方法customer_registForm");
		
		String telephone = ServletActionContext.getRequest().getParameter("telephone");
		System.out.println(telephone);
		//String checkCode = ServletActionContext.getRequest().getParameter("checkCode");
		//String SessionCheckCode = (String) ServletActionContext.getRequest().getSession().getAttribute(customer.getTelephone());
		if(!"15927900079".equals(telephone)){
			System.out.println("不正确");
			return "false";
		}
		
			WebClient.create("http://localhost:9091/wz_crm_management/service/customerService/postCustomer")
			.type(MediaType.APPLICATION_JSON)
			.post(customer);
			//生成邮箱随机数验证码
			String activeCode = RandomStringUtils.randomNumeric(6);
			System.out.println(activeCode);
			redisTemplate.opsForValue().set(customer.getTelephone(),activeCode,24,TimeUnit.HOURS);
			String content = "尊敬的客户您好，请于24小时内，进行邮箱账户的绑定，点击下面地址完成绑定:<br/><a href='"
					+ MailUtils.activeUrl + "?telephone=" + customer.getTelephone()
					+ "&activeCode=" + activeCode + "'>速运快递邮箱绑定地址</a>";
			MailUtils.sendMail("速运邮箱激活", content,customer.getEmail());
			System.out.println(customer.getEmail());
			return "succse";
		}
	//绑定邮箱
	private String activeCode;
	
	public void setActiveCode(String activeCode) {
		this.activeCode = activeCode;
	}
	//绑定邮箱激活码
	@Action(value="message_actionMail")
	public void bdmail() throws Exception{
		System.out.println("景区方法》》》》》");
		ServletActionContext.getResponse().setContentType("text/html;charset=utf-8");
		String redisActiveCode = redisTemplate.opsForValue().get(customer.getTelephone());
		System.out.println(redisActiveCode+"--");
		System.out.println(activeCode+"==");
		if(redisActiveCode==null||!redisActiveCode.equals(activeCode)){
			//激活码无效
			ServletActionContext.getResponse().getWriter().println("激活码无效，请重新激活");
		}else{
			//激活码有效
			WebClient.create("http://localhost:9091/wz_crm_management/service/customerService/findTelephone"+customer.getTelephone()).accept(MediaType.APPLICATION_JSON).get(Customer.class);
			if(customer.getType()==null||customer.getType()!=1){
				//没有绑定
				WebClient.create("http://localhost:9091/service/customerService/updateType"+customer.getTelephone()).accept(MediaType.APPLICATION_JSON).get();
				ServletActionContext.getResponse().getWriter()
				.println("邮箱绑定成功！");
			}else{
				//绑定成功
				ServletActionContext.getResponse().getWriter().println("已经绑定过，无需重复绑定");
			}
		}
	}
	//客户登录
	@Action(value="customerForm",results={@Result(name="succse",type="redirect",location="index.html#/myhome"),@Result(name="false",type="redirect",location="login.html")})
	public String customerForm(){
		System.out.println("customerForm..........");
		Customer customer2 = WebClient.create("http://localhost:9091/wz_crm_management/service/customerService/loginForm?telephone="+customer.getTelephone()+"&password="+customer.getPassword())
		.accept(MediaType.APPLICATION_JSON)
		.get(Customer.class);
		System.out.println(customer2);
		if(customer2==null){
			//登录失败
			return "false";
		}else{
			//登陆成功
			ServletActionContext.getRequest().getSession().setAttribute("customer2",customer2);
			return "succse";
		}
	}
	@Override
	public Customer getModel() {
		return customer;
	}
}
