package org.orph2020.pst.cli;
/*
 * Created on 01/08/2023 by Paul Harrison (paul.harrison@manchester.ac.uk).
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import picocli.CommandLine.*;

@Command(name = "fetchProposal")
public class FetchProposal implements Runnable {

    @ParentCommand
    private PolarisCLI parent;

    @Override
    public void run() {
       try {
           System.out.println("Fetching proposal");
           System.out.println(parent.mapper.writeValueAsString(parent.api.getObservingProposal(1)));
       } catch (JsonProcessingException e) {
          throw new RuntimeException(e);
       }
    }
}
