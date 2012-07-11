package com.developerb.elasticsearch.sandbox;

import com.developerb.elasticsearch.test.ElasticSearchRule;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;

/**
 * @author Kim A. Betti
 */
public class ExperimentalTest {

    @Rule
    public ElasticSearchRule elasticSearchRule = new ElasticSearchRule();

    @Test
    public void experiment() {
        assertNotNull(elasticSearchRule);
    }

}
