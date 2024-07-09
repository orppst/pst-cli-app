package org.orph2020.pst.cli;

import org.ivoa.dm.proposal.prop.Observatory;
import picocli.CommandLine.*;

import java.io.File;

@Command(name = "createObservatory",
        description = "create an Observatory in the Polaris tool from a JSON file")
public class CreateObservatory implements Runnable{

    @ParentCommand
    private PolarisCLI parent;

    @Option(names = {"-f", "--jsonFile"}, required = true, paramLabel = "JSONFILE",
            description = "the file containing the Observatory defined in JSON")
    File jsonFile;

    @Override
    public void run() {
        try {
            System.out.println("Creating Observatory");
            System.out.println("Observatory file input: " + jsonFile.getAbsolutePath());

            Observatory observatory = parent.mapper.readValue(jsonFile, Observatory.class);

            System.out.println(parent.mapper.writeValueAsString(parent.api.createObservatory(observatory)));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
