package com.atguigu.gmall.cms.config;

import io.shardingjdbc.core.api.MasterSlaveDataSourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

@Configuration
public class GmallShardingJdbcConfig {

    @Bean
    public DataSource dataSource() throws IOException, SQLException {
        DataSource dataSource = MasterSlaveDataSourceFactory.createDataSource(ResourceUtils.getFile("classpath:sharding-jdbc.yml"));
        return dataSource;
    }
}
