package com.atguigu.gmall.pms;

import com.atguigu.gmall.pms.entity.Album;
import com.atguigu.gmall.pms.mapper.AlbumMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallPmsApplicationTests {

	@Autowired
	JedisPool jedisPool;

	@Autowired
	AlbumMapper albumMapper;

	@Test
	public void contextLoads() {

	}

	@Test
	public void jedisTest(){
		Jedis jedis = jedisPool.getResource();
		jedis.set("hello","world");
		String s = jedis.get("hello");
		System.out.println(s);
	}

	@Test
	public void testWrite(){
		Album album = new Album();
		album.setName("测试");
		int result = albumMapper.insert(album);
		System.out.println(result > 0? "添加成功！":"添加失败！");
	}

	@Test
	public void testRead(){
		//强制去主库
		// HintManager.getInstance().setMasterRouteOnly();
		Album album = albumMapper.selectById(2);
		System.out.println(album.getName());
	}

}
