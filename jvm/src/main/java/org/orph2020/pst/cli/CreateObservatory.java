package org.orph2020.pst.cli;

import org.ivoa.dm.proposal.prop.*;
import picocli.CommandLine.*;

import java.io.File;
import java.util.List;

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

            Observatory inputObservatory = parent.mapper.readValue(jsonFile, Observatory.class);

            Observatory observatory = new Observatory(null, null, null, null,
                    inputObservatory.getName(), inputObservatory.getAddress(),
                    inputObservatory.getIvoid(), inputObservatory.getWikiId()
            );

            Observatory persistedObservatory = parent.api.createObservatory(observatory);
            Long id = persistedObservatory.getId();

            List<Telescope> telescopes = inputObservatory.getTelescopes();
            for (Telescope telescope : telescopes) {
                parent.api.createAndAddTelescopeToObservatory(id, telescope);
            }

            List<Instrument> instruments = inputObservatory.getInstruments();
            for (Instrument instrument : instruments) {
                parent.api.createAndAddInstrumentToObservatory(id, instrument);
            }

            List<Backend> backends = inputObservatory.getBackends();
            for (Backend backend : backends) {
                parent.api.createAndAddBackend(id, backend);
            }

            List<TelescopeArray> telescopeArrays = inputObservatory.getArrays();
            for (TelescopeArray telescopeArray : telescopeArrays) {
                parent.api.createAndAddArray(id, telescopeArray);
            }

            Observatory createdObservatory = parent.api.getObservatory(id);

            System.out.println("Observatory '" + createdObservatory.getName() + "' has been created " +
                    "with id: " + createdObservatory.getId());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
