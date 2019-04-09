package com.atguigu.gmall.ums;

import com.atguigu.gmall.ums.entity.Role;
import com.atguigu.gmall.ums.mapper.RoleMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallUmsApplicationTests {

	@Autowired
	RoleMapper roleMapper;

	@Test
	public void contextLoads() {
	}

	@Test
	public void testWrite(){
		Role role = new Role();
		role.setName("测试");
		int result = roleMapper.insert(role);
		System.out.println(result > 0? "添加成功！":"添加失败！");
	}

	@Test
	public void testRead(){
		//强制去主库
		// HintManager.getInstance().setMasterRouteOnly();
		Role role = roleMapper.selectById(8);
		System.out.println(role.getName());
	}

}
