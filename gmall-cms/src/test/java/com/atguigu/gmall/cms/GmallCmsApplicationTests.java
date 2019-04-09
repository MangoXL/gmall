package com.atguigu.gmall.cms;

import com.atguigu.gmall.cms.entity.HelpCategory;
import com.atguigu.gmall.cms.mapper.HelpCategoryMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallCmsApplicationTests {

	@Autowired
	HelpCategoryMapper helpCategoryMapper;

	@Test
	public void contextLoads() {
	}

	@Test
	public void testWrite(){
		HelpCategory helpCategory = new HelpCategory();
		helpCategory.setName("测试");
		int result = helpCategoryMapper.insert(helpCategory);
		System.out.println(result > 0? "添加成功！":"添加失败！");
	}

	@Test
	public void testRead(){
		//强制去主库
		// HintManager.getInstance().setMasterRouteOnly();
		HelpCategory helpCategory = helpCategoryMapper.selectById(1);
		System.out.println(helpCategory.getName());
	}

}
