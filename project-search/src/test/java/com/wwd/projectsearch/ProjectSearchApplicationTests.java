package com.wwd.projectsearch;

import com.alibaba.fastjson.JSON;
import com.wwd.projectsearch.config.ElasticSearchConfig;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
class ProjectSearchApplicationTests {

    @Autowired
    private RestHighLevelClient client;
    @Test
    void contextLoads() {

        System.out.println(client);
    }

    @Test
    void testSearchData() throws IOException{
        //创建检索请求
        SearchRequest searchRequest = new SearchRequest();
        //指定索引
        searchRequest.indices("bank");
        //指定DSL，检索条件
        SearchSourceBuilder builder = new SearchSourceBuilder();

        builder.query(QueryBuilders.matchQuery("address", "mill"));
        System.out.println(builder.toString());
        searchRequest.source(builder);


        //执行检索
        SearchResponse searchResponse = client.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);
        System.out.println(searchResponse.toString());
    }

    @Test
    void testIndexData(){

        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("1");

        User user = new User();
        user.setUserName("zs");
        user.setGender("男");
        user.setAge(18);
        String jsonString = JSON.toJSONString(user);

        indexRequest.source(jsonString, XContentType.JSON);
    }

    @Data
    class User{
        private String userName;
        private String gender;
        private Integer age;
    }

}
