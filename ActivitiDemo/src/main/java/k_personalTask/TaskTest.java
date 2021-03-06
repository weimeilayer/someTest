package k_personalTask;


import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskTest {

	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	
	/**部署流程定义（从inputStream）*/
	@Test
	public void deploymentProcessDefinition_inputStream(){
		InputStream inputStreamBpmn = this.getClass().getResourceAsStream("task.bpmn");
		InputStream inputStreamPng = this.getClass().getResourceAsStream("task.png");
		Deployment deployment = processEngine.getRepositoryService()//与流程定义和部署对象相关的Service
						.createDeployment()//创建一个部署对象
						.name("任务")//添加部署的名称
//						.addInputStream("task.bpmn", inputStreamBpmn)//
//						.addInputStream("task.png", inputStreamPng)//
				.addClasspathResource("k/task.bpmn")
				.addClasspathResource("k/task.png")
						.deploy();//完成部署
		System.out.println("部署ID："+deployment.getId());//
		System.out.println("部署名称："+deployment.getName());//
	}
	
	/**启动流程实例*/
	@Test
	public void startProcessInstance(){
		//流程定义的key
		String processDefinitionKey = "task";
		/**启动流程实例的同时，设置流程变量，使用流程变量用来指定任务的办理人，对应task.pbmn文件中#{userID}*/
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("userID", "王彪1");
		ProcessInstance pi = processEngine.getRuntimeService()//与正在执行的流程实例和执行对象相关的Service
						.startProcessInstanceByKey(processDefinitionKey,variables);//使用流程定义的key启动流程实例，key对应helloworld.bpmn文件中id的属性值，使用key值启动，默认是按照最新版本的流程定义启动
		System.out.println("流程实例ID:"+pi.getId());//流程实例ID    101
		System.out.println("流程定义ID:"+pi.getProcessDefinitionId());//流程定义ID   helloworld:1:4
	}
	
	/**查询当前人的个人任务*/
	@Test
	public void findMyPersonalTask(){
		String assignee = "王彪1";
		List<Task> list = processEngine.getTaskService()//与正在执行的任务管理相关的Service
						.createTaskQuery()//创建任务查询对象
						/**查询条件（where部分）*/
						.taskAssignee(assignee)//指定个人任务查询，指定办理人
//						.taskCandidateUser(candidateUser)//组任务的办理人查询
//						.processDefinitionId(processDefinitionId)//使用流程定义ID查询
//						.processInstanceId(processInstanceId)//使用流程实例ID查询
//						.executionId(executionId)//使用执行对象ID查询
						/**排序*/
						.orderByTaskCreateTime().asc()//使用创建时间的升序排列
						/**返回结果集*/
//						.singleResult()//返回惟一结果集
//						.count()//返回结果集的数量
//						.listPage(firstResult, maxResults);//分页查询
						.list();//返回列表
		if(list!=null && list.size()>0){
			for(Task task:list){
				System.out.println("任务ID:"+task.getId());
				System.out.println("任务名称:"+task.getName());
				System.out.println("任务的创建时间:"+task.getCreateTime());
				System.out.println("任务的办理人:"+task.getAssignee());
				System.out.println("流程实例ID："+task.getProcessInstanceId());
				System.out.println("执行对象ID:"+task.getExecutionId());
				System.out.println("流程定义ID:"+task.getProcessDefinitionId());
				System.out.println("########################################################");
			}
		}
	}
	
	/**完成我的任务*/
	@Test
	public void completeMyPersonalTask(){
		//任务ID
		String taskId = "22505";
		processEngine.getTaskService()//与正在执行的任务管理相关的Service
					.complete(taskId);
		System.out.println("完成任务：任务ID："+taskId);
	}



	/**查看流程图
	 * @throws IOException */
	@Test
	public void viewPic() throws IOException{
		/**将生成图片放到文件夹下*/
		String deploymentId = "22501";
		//获取图片资源名称
		List<String> list = processEngine.getRepositoryService()//
				.getDeploymentResourceNames(deploymentId);
		//定义图片资源的名称
		String resourceName = "";
		if(list!=null && list.size()>0){
			for(String name:list){
				if(name.indexOf(".png")>=0){
					resourceName = name;
				}
			}
		}
		RepositoryService repositoryService = processEngine
				.getRepositoryService();
		ProcessDefinition procDef = repositoryService
				.createProcessDefinitionQuery().processDefinitionId("22501")
				.singleResult();
		String diagramResourceName = procDef.getDiagramResourceName();
		InputStream in = repositoryService.getResourceAsStream(
				procDef.getDeploymentId(), diagramResourceName);

		//获取图片的输入流
//		InputStream in = processEngine.getRepositoryService()//
//				.getResourceAsStream(deploymentId, resourceName);
		System.out.print(resourceName+"图片名：、、、、");
		//将图片生成到D盘的目录下
		File file = new File("C://temp//"+resourceName);
		//将输入流的图片写到D盘下
		FileUtils.copyInputStreamToFile(in, file);
	}



	@Test
	public void viewImage() throws Exception {
		// 创建仓库服务对对象
		RepositoryService repositoryService = processEngine.getRepositoryService();
		// 从仓库中找需要展示的文件
		String deploymentId = "17501";
		List<String> names = repositoryService.getDeploymentResourceNames(deploymentId);
		String imageName = null;
		for (String name : names) {
			System.out.println("循环中的内容  "+name);
			if(name.indexOf(".png")>=0){
				imageName = name;
			}
		}
		System.out.println("图片名“：：："+imageName);
		if(imageName!=null){
//     System.out.println(imageName);
			File f = new File("C://temp//"+ imageName);
			// 通过部署ID和文件名称得到文件的输入流
			InputStream in =  repositoryService.getResourceAsStream(deploymentId, imageName);
			FileUtils.copyInputStreamToFile(in, f);
		}
	}



}
