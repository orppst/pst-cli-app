package org.orph2020.pst.cli;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.orph2020.pst.apiimpl.client.ProposalRestAPI;
import picocli.CommandLine;

@Dependent
@CommandLine.Command(name="fetchPeople")
public class FetchPeople implements Runnable{

    @Inject
    protected ObjectMapper mapper;

    @RestClient
    ProposalRestAPI api;


    @Override
    public void run() {
        try {
            System.out.println("Fetching people:");
            System.out.println(mapper.writeValueAsString(api.getPeople()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
