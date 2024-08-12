package org.orph2020.pst.cli;

import io.quarkus.test.junit.main.Launch;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusMainTest
public class PolarisCliTest {

    @Test
    @Launch({"--user=Batman", "--password=Batcave"})
    public void testLaunchCommand(LaunchResult result) {
        Assertions.assertTrue(result.getOutput()
                .contains("User: Batman\nPassword: Batcave"));
    }

    @Test
    @Launch({"createObservatory", "--jsonFile=src/test/data/jodrell_bank_alt.json"})
    public void testCreateObservatoryCommand(LaunchResult result) {
        Assertions.assertTrue(result.getOutput()
                .contains("Observatory 'Jodrell Bank Alt' has been created"));
    }

}
