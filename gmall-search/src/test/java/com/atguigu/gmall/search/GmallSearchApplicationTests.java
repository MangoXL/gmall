package com.atguigu.gmall.search;

import com.atguigu.gmall.search.bean.Account;
import io.searchbox.client.JestClient;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallSearchApplicationTests {

	@Autowired
	JestClient jestClient;

	@Test
	public void contextLoads() {
		System.out.println(jestClient);
	}

	@Test
	public void insert() throws IOException {
		Account account = new Account(6666L, 100000L, "LE", "XIAOLE", 18, "F", "BJ", "...", "xiaole@qq.com", "CP", "中国");
		Index index = new Index.Builder(account).index("bank").type("account").id(account.getAccount_number() + "").build();
		DocumentResult result = jestClient.execute(index);
		System.out.println(result);
	}

}
