package com.developerb.elasticsearch.sandbox;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.FilterBuilders.geoDistanceFilter;
import static org.elasticsearch.index.query.QueryBuilders.filteredQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

/**
 * @author Kim A. Betti <kim@developer-b.com>
 */
public class GeoSpatialTest extends AbstractElasticSearchTest {

    @Test
    public void mappingTest() throws IOException {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("application")
                .mapping("pets", jsonBuilder()
                                    .startObject()
                                        .startObject("pets")
                                            .startObject("properties")
                                                .startObject("location")
                                                    .field("type", "geo_point")
                                                .endObject()
                                            .endObject()
                                        .endObject()
                                    .endObject());

        indices.create(createIndexRequest).actionGet();

        client.prepareIndex("application", "pets")
                .setSource(jsonBuilder()
                        .startObject()
                            .field("name", "Pluto")
                            .field("captured", new Date())
                            .field("location", "70.0664,24.9829")
                        .endObject()
                )
                .execute()
                .actionGet();

        elasticSearchRule.refreshIndices("application");

        SearchResponse searchResponse = client.prepareSearch("application")
                                            .setTypes("pets")
                                            .setQuery (
                                                    filteredQuery (
                                                            matchAllQuery(),
                                                            geoDistanceFilter("location")
                                                                    .distance("10km")
                                                                    .point(70.051328451, 24.972181320)
                                                    )
                                            )
                                            .execute()
                                            .actionGet();

        assertEquals(1, searchResponse.getHits().totalHits());
    }

}
