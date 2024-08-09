package org.orph2020.pst.cli;

import com.fasterxml.jackson.core.JsonProcessingException;
import picocli.CommandLine.*;

@Command(name="fetchPeople")
public class FetchPeople implements Runnable{

    @ParentCommand
    private PolarisCLI parent;


    @Override
    public void run() {
        try {
            System.out.println("Fetching people:");
            System.out.println(parent.mapper.writeValueAsString(parent.api.getPeople()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
