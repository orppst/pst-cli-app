package org.orph2020.pst.cli;
/*
 * Created on 01/08/2023 by Paul Harrison (paul.harrison@manchester.ac.uk).
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.Dependent;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.orph2020.pst.apiimpl.client.ProposalRestAPI;
import picocli.CommandLine.*;

import jakarta.inject.Inject;

@Dependent
@Command(name = "fetchProposal")
public class FetchProposal implements Runnable {


    @Inject
    protected ObjectMapper mapper;

    @RestClient
    ProposalRestAPI  apiService;


    @Override
    public void run() {
       try {
          System.out.println(mapper.writeValueAsString(apiService.getObservatories()));
          System.out.println(mapper.writeValueAsString(apiService.getObservingProposal(1)));
       } catch (JsonProcessingException e) {
          throw new RuntimeException(e);
       }
    }
}
