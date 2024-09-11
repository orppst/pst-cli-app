package org.orph2020.pst.cli;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.orph2020.pst.apiimpl.client.ProposalRestAPI;
import picocli.CommandLine;
import picocli.CommandLine.*;

@QuarkusMain
@Command(
        name = "polaris-cli",
        mixinStandardHelpOptions = true,
        subcommands = {
                HelpCommand.class,
                FetchPeople.class,
                FetchProposal.class,
                FetchObservatory.class,
                CreateObservatory.class
        })
public class PolarisCLI implements QuarkusApplication, Runnable {

    @Inject
    IFactory factory;

    @Inject
    ObjectMapper mapper;

    @RestClient
    ProposalRestAPI api;

    @Override
    public int run(String... args) throws Exception {
        return new CommandLine(this, factory)
                .execute(args);
    }

    @Override
    public void run() {
        System.out.println("Running Polaris CLI");

        System.out.println("I need to use the OidcClient.accessTokens \n"
                +"method to accept a Map of extra properties and pass the \n"
                +"device_code and redirect_uri parameters to exchange the \n"
                +"authorization code for the tokens.");

    }
}
