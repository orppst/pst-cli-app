package org.orph2020.pst.cli;

import com.fasterxml.jackson.core.JsonProcessingException;
import picocli.CommandLine.*;

@Command(name="fetchObservatory")
public class FetchObservatory implements Runnable {

    @ParentCommand
    private PolarisCLI parent;

    @Override
    public void run() {
        try {
            System.out.println("Fetching observatories");
            System.out.println(parent.mapper.writeValueAsString(parent.api.getObservatories()));
            System.out.println("Fetching observatory 3:");
            System.out.println(parent.mapper.writeValueAsString(parent.api.getObservatory(3L)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
