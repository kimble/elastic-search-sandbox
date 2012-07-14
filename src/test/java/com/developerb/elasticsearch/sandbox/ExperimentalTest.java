package com.developerb.elasticsearch.sandbox;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.queryString;

/**
 * @author Kim A. Betti
 */
public class ExperimentalTest extends AbstractElasticSearchTest {

    @Test
    public void experiment() throws Exception {
        client.prepareIndex("application", "pets")
                .setSource(jsonBuilder()
                        .startObject()
                            .field("name", "Pluto")
                            .startObject("location")
                                .field("lat", 70.066446)
                                .field("lon", 24.982973)
                            .endObject()
                        .endObject()
                )
                .execute()
                .actionGet();

        elasticSearch.refreshIndices("application");

        QueryBuilder qb = queryString("pluto");
        SearchResponse result = client.prepareSearch("application")
                .setQuery(qb)
                .setExplain(true)
                .execute()
                .actionGet();

        assertEquals(1, result.getHits().totalHits());
    }

}
