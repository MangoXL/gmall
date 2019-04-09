package com.atguigu.gmall.sms;

import com.atguigu.gmall.sms.entity.Coupon;
import com.atguigu.gmall.sms.mapper.CouponMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallSmsApplicationTests {

	@Autowired
	CouponMapper couponMapper;

	@Test
	public void contextLoads() {
	}

	@Test
	public void testWrite(){
		Coupon coupon = new Coupon();
		coupon.setName("测试");
		int result = couponMapper.insert(coupon);
		System.out.println(result > 0? "添加成功！":"添加失败！");
	}

	@Test
	public void testRead(){
		//强制去主库
		// HintManager.getInstance().setMasterRouteOnly();
		Coupon coupon = couponMapper.selectById(23);
		System.out.println(coupon.getName());
	}

}
