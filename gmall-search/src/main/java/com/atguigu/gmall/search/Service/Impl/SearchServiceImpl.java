package com.atguigu.gmall.search.Service.Impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.constant.EsConstant;
import com.atguigu.gmall.service.SearchService;
import com.atguigu.gmall.to.es.ESProduct;
import com.atguigu.gmall.to.es.SearchParam;
import com.atguigu.gmall.to.es.SearchParamVo;
import com.atguigu.gmall.to.es.SearchResponse;
import io.searchbox.client.JestClient;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

@Service(version = "1.0")
@Component
public class SearchServiceImpl implements SearchService {

    @Autowired
    JestClient jestClient;

    @Override
    public boolean saveProductInfoToES(ESProduct esProduct) {
        Index index = new Index.Builder(esProduct)
                .index(EsConstant.ES_PRODUCT_INDEX)
                .type(EsConstant.ES_PRODUCT_TYPE)
                .id(esProduct.getId() + "").build();
        DocumentResult execute = null;
        try {
            execute = jestClient.execute(index);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return execute.isSucceeded();
    }

    @Override
    public SearchResponse search(SearchParam searchParam) throws IOException {
        //1、根据页面传来的数据构建检索的DSL语句
        String query = builderSearchDSL(searchParam);
        Search search = new Search.Builder(query)
                .addIndex("gulishop")
                .addType("product").build();
        //2、执行查询
        SearchResult result = jestClient.execute(search);
        //3、解析并封装查询结果返回页面
        SearchResponse response = builderSearchResult(result);
        //4、封装分页信息
        response.setPageNum(searchParam.getPageNum());
        response.setPageSize(searchParam.getPageSize());
        response.setTotal(result.getTotal());

        return response;
    }

    /**
     * 封装查询结果
     * @param result
     * @return
     */
    private SearchResponse builderSearchResult(SearchResult result) {
        //创建页面需要的数据对象
        SearchResponse response = new SearchResponse();

        List<SearchResult.Hit<ESProduct, Void>> hits = result.getHits(ESProduct.class);
        hits.forEach(hit -> {
            ESProduct source = hit.source;
            response.getEsProducts().add(source);
        });

        MetricAggregation aggregations = result.getAggregations();

        //封装检索出的品牌信息
        SearchParamVo brand = new SearchParamVo();
        brand.setName("品牌");
        aggregations.getTermsAggregation("brandIdAgg")
                .getBuckets().forEach(b -> {
            b.getTermsAggregation("brandNameAgg")
                    .getBuckets().forEach(bb -> {
                brand.getValue().add(bb.getKey());
            });
        });
        response.setBrand(brand);

        //封装检索出的分类信息
        SearchParamVo category = new SearchParamVo();
        category.setName("分类");
        aggregations.getTermsAggregation("productCategoryIdAgg")
                .getBuckets().forEach(b -> {
                    b.getTermsAggregation("productCategoryNameAgg")
                            .getBuckets().forEach(bb -> {
                        category.getValue().add(bb.getKey());
                    });
        });
        response.setCatelog(category);

        //封装检索出的属性信息
        TermsAggregation termsAggregation = aggregations.getChildrenAggregation("productAttrAgg")
                .getChildrenAggregation("productAttributeIdAgg")
                .getTermsAggregation("productAttributeIdAgg");
        List<TermsAggregation.Entry> buckets = termsAggregation.getBuckets();
        buckets.forEach(b -> {
            //封装属性ID
            SearchParamVo attr = new SearchParamVo();
            attr.setProductAttributeId(Long.parseLong(b.getKey()));
            b.getTermsAggregation("productAttributeNameAgg")
                    .getBuckets().forEach(bb -> {
                        //封装属性名
                attr.setName(bb.getKey());
                bb.getTermsAggregation("productAttributeAgg")
                        .getBuckets().forEach(bbb -> {
                    attr.getValue().add(bbb.getKey());
                });
            });
            response.getSearchParamVos().add(attr);
        });

        return response;
    }

    /**
     * 构建检索条件
     * @param searchParam
     * @return
     */
    private String builderSearchDSL(SearchParam searchParam) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //根据关键字查询：match是用来做模糊的，term是用来做精确的，filter和match一样但filter不计算相关性评分的
        if(!StringUtils.isEmpty(searchParam.getKeyword())){
            //must是条件必须满足
            boolQuery.must(QueryBuilders.matchQuery("name",searchParam.getKeyword()));
            //should是用来增加命中分数
            boolQuery.should(QueryBuilders.matchQuery("subTitle",searchParam.getKeyword()));
            boolQuery.should(QueryBuilders.matchQuery("keywords",searchParam.getKeyword()));
        }

        //过滤查询条件
        //分类过滤
        if(!StringUtils.isEmpty(searchParam.getCategory3()) && searchParam.getCategory3().length > 0){
            boolQuery.filter(QueryBuilders.termsQuery("productCategoryName",searchParam.getCategory3()));
        }

        //品牌过滤
        if(!StringUtils.isEmpty(searchParam.getBrand()) && searchParam.getBrand().length > 0){
            boolQuery.filter(QueryBuilders.termsQuery("brandName",searchParam.getBrand()));
        }

        //属性过滤
        if(searchParam.getProperties() != null && searchParam.getProperties().length > 0){
            String[] properties = searchParam.getProperties();
            for (String property : properties) {
                String attrId = property.split(":")[0];
                String attrValue = property.split(":")[1];
                String[] attrValues = attrValue.split("-");
                BoolQueryBuilder must = QueryBuilders.boolQuery()
                        .must(QueryBuilders.termQuery("attrValueList.productAttributeId", attrId))
                        .must(QueryBuilders.termsQuery("attrValueList.value", attrValues));
                boolQuery.filter(QueryBuilders.nestedQuery("attrValueList",must, ScoreMode.None));
            }
        }

        //价格区间过滤
        if(searchParam.getFrom() != null){
            boolQuery.filter(QueryBuilders.rangeQuery("price").gte(searchParam.getFrom()));
        }
        if(searchParam.getTo() != null){
            boolQuery.filter(QueryBuilders.rangeQuery("price").lte(searchParam.getTo()));
        }

        //检索过滤条件组装完成
        sourceBuilder.query(boolQuery);


        //聚合品牌信息
        TermsAggregationBuilder brandAggs = AggregationBuilders
                .terms("brandIdAgg")
                .field("brandId")
                .size(100)
                .subAggregation(AggregationBuilders.terms("brandNameAgg")
                        .field("brandName")
                        .size(100));
        sourceBuilder.aggregation(brandAggs);

        //集合商品分类信息
        TermsAggregationBuilder categoryAggs = AggregationBuilders.terms("productCategoryIdAgg")
                .field("productCategoryId")
                .size(100)
                .subAggregation(AggregationBuilders.terms("productCategoryNameAgg")
                        .field("productCategoryName")
                        .size(100));
        sourceBuilder.aggregation(categoryAggs);

        //聚合商品筛选属性
        NestedAggregationBuilder attrAggs = AggregationBuilders.nested("productAttrAgg", "attrValueList")
                .subAggregation(AggregationBuilders
                        .filter("productAttributeIdAgg",
                                QueryBuilders.termQuery("attrValueList.type", "1"))
                        .subAggregation(AggregationBuilders.terms("productAttributeIdAgg")
                                .field("attrValueList.productAttributeId")
                                .size(1000)
                                .subAggregation(AggregationBuilders.terms("productAttributeNameAgg")
                                        .field("attrValueList.name")
                                        .size(100)
                                        .subAggregation(AggregationBuilders.terms("productAttributeAgg")
                                                .field("attrValueList.value")
                                                .size(100)))));
        sourceBuilder.aggregation(attrAggs);

        //高亮显示搜索关键字
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name").preTags("<b style='color:blue'>").postTags("</b>");
        sourceBuilder.highlighter(highlightBuilder);

        //分页显示
        sourceBuilder.from((searchParam.getPageNum() - 1) * searchParam.getPageSize());
        sourceBuilder.size(searchParam.getPageSize());

        //根据排序规则进行排序显示
        if(!StringUtils.isEmpty(searchParam.getOrder())){
            String order = searchParam.getOrder();
            String type = order.split(":")[0];
            String asc = order.split(":")[1];
            switch (type){
                case "0"://综合查询，按照评分
                    sourceBuilder.sort(SortBuilders.scoreSort().order(SortOrder.fromString(asc)));
                    break;
                case "1"://按照销量查询
                    sourceBuilder.sort(SortBuilders.fieldSort("sale").order(SortOrder.fromString(asc)));
                    break;
                case "2"://按照价格查询
                    sourceBuilder.sort(SortBuilders.fieldSort("price").order(SortOrder.fromString(asc)));
                    break;
            }
        }

        return sourceBuilder.toString();
    }
}
