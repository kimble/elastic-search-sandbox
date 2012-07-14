package com.developerb.elasticsearch.sandbox;

import com.developerb.elasticsearch.test.ElasticSearchRule;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.junit.Before;
import org.junit.Rule;

/**
 * @author Kim A. Betti <kim@developer-b.com>
 */
public class AbstractElasticSearchTest {

    @Rule
    public ElasticSearchRule elasticSearch = new ElasticSearchRule();

    protected Client client;
    protected IndicesAdminClient indices;

    @Before
    public void setUp() {
        client = elasticSearch.client();
        indices = client.admin().indices();
    }

    protected MappingMetaData findMappingFor(String index, String type) {
        elasticSearch.refreshIndices(index);

        ClusterState cs = client.admin()
                                .cluster()
                                .prepareState()
                                .setFilterIndices(index)
                                .execute()
                                .actionGet()
                                .getState();

        IndexMetaData imd = cs.getMetaData().index(index);
        return imd.mapping(type);
    }

}
