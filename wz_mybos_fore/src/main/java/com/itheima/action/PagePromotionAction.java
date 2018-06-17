package com.itheima.action;



import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.alibaba.fastjson.JSONObject;
import com.itheima.domain.Constans;
import com.itheima.domain.PageBean;
import com.itheima.domain.Promotion;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Namespace("/")
@ParentPackage("struts-default")
@Controller
@Scope("prototype")
public class PagePromotionAction extends ActionSupport implements ModelDriven<Promotion>{
	private Promotion promotion=new Promotion();
	
	private int page;
	private int pageSize;
	
	public void setPage(int page) {
		this.page = page;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	@Action(value="pagePrommotion_aaa")
	public void pagePrommotion() throws Exception{
		System.out.println("pagePrommotion.........");
		PageBean<Promotion> pageBean = WebClient.create(Constans.wz_bos_management_url+"/wz_bos_management/service/promotionService/pagePromotion?page="+page+"&pageSize="+pageSize)
				.accept(MediaType.APPLICATION_JSON).get(PageBean.class);
		System.out.println(pageBean.getTotalCount());
		System.out.println(pageBean.getPageData());
		String json = JSONObject.toJSONString(pageBean);
		ServletActionContext.getResponse().getWriter().write(json);
	}
	//静态
	@Action(value="promotion_showDetail")
	public void promotion_ShowDetail() throws Exception{
		System.out.println("promotion_showDetail......");
		//先判断id对应html是否存在，如果存在直接返回
		//Promotion promotion1=new Promotion();
		String realPath = ServletActionContext.getServletContext().getRealPath("/freemarker");
		File htmlFile=new File(realPath+"/"+promotion.getId()+".html");
		System.out.println(htmlFile);
		//如果html文件不存在，查询数据库，结合freemarker结合生成页面
		if(!htmlFile.exists()){
			Configuration configuration=new Configuration(Configuration.VERSION_2_3_22);
			configuration.setDirectoryForTemplateLoading(new File(ServletActionContext.getServletContext().getRealPath("/WEB-INF/freemarker_templates")));
			Template template = configuration.getTemplate("promotion_detail.ftl");
			Promotion promotion1=WebClient.create(Constans.wz_bos_management_url+"/wz_bos_management/service/promotionService/promotion/"+promotion.getId())
					.accept(MediaType.APPLICATION_JSON)
					.get(Promotion.class);
			Map<String,Object> parameterMap=new HashMap<String,Object>();
			parameterMap.put("promotion",promotion1);
			
			template.process(parameterMap, new OutputStreamWriter(new FileOutputStream(htmlFile),"utf-8"));
				
				
		}
		//如果存在，直接将文件返回
		ServletActionContext.getResponse().setContentType("text/html;charset=utf-8");
		FileUtils.copyFile(htmlFile,ServletActionContext.getResponse().getOutputStream());
	}
	@Override
	public Promotion getModel() {
		return promotion;
	}
}
