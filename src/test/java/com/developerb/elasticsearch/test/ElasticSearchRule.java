package com.developerb.elasticsearch.test;

import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import static org.elasticsearch.node.NodeBuilder.*;


/**
 * @author Kim A. Betti
 */
public class ElasticSearchRule implements TestRule {

    private Node node;

    @Override
    public Statement apply(Statement base, Description description) {
        return new ElasticStatement(base);
    }

    public Client client() {
        return node.client();
    }

    private class ElasticStatement extends Statement {

        private final Statement base;

        public ElasticStatement(Statement base) {
            this.base = base;
        }

        @Override
        public void evaluate() throws Throwable {
            try {
                node = nodeBuilder()
                        .local(true)
                        .node();

                base.evaluate();
            }
            finally {
                node.close();
            }
        }

    }

}
