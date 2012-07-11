package com.developerb.elasticsearch.sandbox;

import com.developerb.elasticsearch.test.ElasticSearchRule;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.elasticsearch.action.WriteConsistencyLevel.ONE;
import static org.elasticsearch.action.support.replication.ReplicationType.SYNC;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.queryString;

/**
 * @author Kim A. Betti
 */
public class ExperimentalTest {

    @Rule
    public ElasticSearchRule elasticSearchRule = new ElasticSearchRule();

    private Client client;

    @Before
    public void setUp() {
        client = elasticSearchRule.client();
    }

    @Test
    public void experiment() throws Exception {
        client.prepareIndex("application", "messages")
                .setSource(jsonBuilder()
                        .startObject()
                        .field("to", "Nasse Nøff")
                        .field("from", "Ole Brumm")
                        .field("message", "Ja takk begge deler!")
                        .endObject()
                )
                .setConsistencyLevel(ONE)
                .setReplicationType(SYNC)
                .execute()
                .actionGet();

        elasticSearchRule.refreshIndices("application");

        QueryBuilder qb = queryString("takk");
        SearchResponse result = client.prepareSearch("application")
                .setQuery(qb)
                .setExplain(true)
                .execute()
                .actionGet();

        assertEquals(1, result.getHits().totalHits());
    }

}
