package com.project.core.tags;

import static freemarker.template.ObjectWrapper.DEFAULT_WRAPPER;

import java.util.HashMap;
import java.util.Map;

import com.project.common.utils.LoggerUtils;
import com.project.common.utils.SpringContextUtil;
import com.shiro.core.statics.Constant;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * 
 * 项目名称：ssmShiroDemo   
 * 类名称：APITemplateModel   
 * 类描述：   Freemarker 自定义标签 API公共入口
 *
 * 创建人：wangbiao  
 * 创建时间：2017年3月30日 下午1:56:05   
 * @version
 */

public class APITemplateModel extends WYFTemplateModel {

	@Override
	@SuppressWarnings({  "unchecked" })
	protected Map<String, TemplateModel> putValue(Map params)
			throws TemplateModelException {
		
		Map<String, TemplateModel> paramWrap = null ;
		if(null != params && params.size() != 0 || null != params.get(Constant.TARGET)){
			String name =  params.get(Constant.TARGET).toString() ;
			paramWrap = new HashMap<String, TemplateModel>(params);
			
			/**
			 * 获取子类，用父类接收，
			 */
			SuperCustomTag tag =  SpringContextUtil.getBean(name,SuperCustomTag.class);
			//父类调用子类方法
			Object result = tag.result(params);
			
			//输出
			paramWrap.put(Constant.OUT_TAG_NAME, DEFAULT_WRAPPER.wrap(result));
		}else{
			LoggerUtils.error(getClass(), "Cannot be null, must include a 'name' attribute!");
		}
		return paramWrap;
	}

	
	
	
	
	
}
