package com.atguigu.gmall.order.Config;

import io.shardingjdbc.core.api.MasterSlaveDataSourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

@Configuration
public class GmallShardingJdbcConfig {

    @Bean
    public DataSource getDataSource() throws IOException, SQLException {
        DataSource dataSource = MasterSlaveDataSourceFactory.createDataSource(ResourceUtils.getFile("classpath:sharding-jdbc.yml"));
        return dataSource;
    }
}
