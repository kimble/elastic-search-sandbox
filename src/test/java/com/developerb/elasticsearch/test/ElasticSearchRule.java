package com.developerb.elasticsearch.test;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.File;
import java.util.UUID;

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

    private class ElasticStatement extends Statement {

        private final Statement base;

        public ElasticStatement(Statement base) {
            this.base = base;
        }

        @Override
        public void evaluate() throws Throwable {
            File testFolder = createTemporaryTestFolder();

            try {
                Settings settings = ImmutableSettings.settingsBuilder()
                        .put("path.data", new File(testFolder, "data").getAbsolutePath())
                        .put("path.logs", new File(testFolder, "logs").getAbsolutePath())
                        .build();

                node = nodeBuilder()
                        .settings(settings)
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

    }

    private File createTemporaryTestFolder() {
        File temporaryFolder = new File(System.getProperty("java.io.tmpdir"));
        File testFolder = new File(temporaryFolder, UUID.randomUUID().toString());

        assertTrue("Unable to create test folder: " + testFolder, testFolder.mkdir());
        return testFolder;
    }

}
