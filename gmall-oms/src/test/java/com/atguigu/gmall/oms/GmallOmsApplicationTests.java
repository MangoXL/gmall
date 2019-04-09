package com.atguigu.gmall.oms;

import com.atguigu.gmall.oms.entity.OrderReturnReason;
import com.atguigu.gmall.oms.mapper.OrderReturnReasonMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallOmsApplicationTests {

	@Autowired
	OrderReturnReasonMapper  orderReturnReasonMapper;

	@Test
	public void contextLoads() {
	}

	@Test
	public void testWrite(){
		OrderReturnReason orderReturnReason = new OrderReturnReason();
		orderReturnReason.setName("测试");
		int result = orderReturnReasonMapper.insert(orderReturnReason);
		System.out.println(result > 0? "添加成功！":"添加失败！");
	}

	@Test
	public void testRead(){
		//强制去主库
		// HintManager.getInstance().setMasterRouteOnly();
		OrderReturnReason orderReturnReason = orderReturnReasonMapper.selectById(17);
		System.out.println(orderReturnReason.getName());
	}

}
