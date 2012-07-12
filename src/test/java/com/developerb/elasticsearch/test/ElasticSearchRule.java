package com.developerb.elasticsearch.test;

import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.File;
import java.util.UUID;

import static java.util.concurrent.TimeUnit.SECONDS;
import static junit.framework.Assert.assertTrue;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;


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

    /**
     * Useful to combat the "near real time" aspect of Elastic Search during testing.
     * @param indices to refresh
     */
    public void refreshIndices(String... indices) {
        client().admin()
                .indices()
                .refresh(new RefreshRequest(indices))
                .actionGet(3, SECONDS);
    }

    private class ElasticStatement extends Statement {

        private final Statement base;

        public ElasticStatement(Statement base) {
            this.base = base;
        }

        @Override
        public void evaluate() throws Throwable {
            File testFolder = createTemporaryTestFolder();

            try {
                node = nodeBuilder()
                        .settings(buildSettings(testFolder))
                        .local(true)
                        .node();

                base.evaluate();
            }
            finally {
                try {
                    node.close();
                }
                finally {
                    deleteDirectory(testFolder);
                }
            }
        }

        private File createTemporaryTestFolder() {
            File temporaryFolder = new File(System.getProperty("java.io.tmpdir"));
            File testFolder = new File(temporaryFolder, UUID.randomUUID().toString());

            assertTrue("Unable to create test folder: " + testFolder, testFolder.mkdir());
            return testFolder;
        }

        private Settings buildSettings(File testFolder) {
            return ImmutableSettings.settingsBuilder()
                    .put("path.data", new File(testFolder, "data").getAbsolutePath())
                    .put("path.logs", new File(testFolder, "logs").getAbsolutePath())
                    .build();
        }

    }

}
