package org.orph2020.pst.cli;
/*
 * Created on 01/08/2023 by Paul Harrison (paul.harrison@manchester.ac.uk).
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.oidc.client.OidcClient;
import io.quarkus.oidc.client.OidcClientConfig;
import io.quarkus.oidc.client.OidcClients;
import io.quarkus.oidc.client.runtime.TokensHelper;
import io.quarkus.rest.client.reactive.QuarkusRestClientBuilder;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.Dependent;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.orph2020.pst.apiimpl.client.ProposalRestAPI;
import picocli.CommandLine.*;

import jakarta.inject.Inject;

import java.net.URI;
import java.util.Map;

@Dependent
@Command(name = "fetchProposal")
public class FetchProposal implements Runnable {


    @Inject
    protected ObjectMapper mapper;

    //@RestClient
    ProposalRestAPI apiService;

    @Inject
    OidcClients oidcClients;

    OidcClient client;// = oidcClients.getClient();
    TokensHelper tokenHelper = new TokensHelper();

    private volatile OidcClient oidcClient;

    //Try to programmatically create a client
    private Uni<OidcClient> createOidcClient() {
        OidcClientConfig cfg = new OidcClientConfig();
        cfg.setId("oidc-client");
        cfg.setAuthServerUrl("http://localhost:53536/realms/orppst");
        cfg.setClientId("pst-gui"); //FIXME with new Id for CLI
        cfg.getCredentials().setSecret("eLt4izrWhxRftFTWTIcMbQsYlbyhfZtU");
        cfg.getGrant().setType(OidcClientConfig.Grant.Type.PASSWORD);
        cfg.setGrantOptions(Map.of("password",
                Map.of("username", "pi", "password", "pi")));
        return oidcClients.newClient(cfg);
    }

    @Override
    public void run() {
        try {
            createOidcClient().subscribe().with(client -> {
                oidcClient = client;
                    tokenHelper.getTokens(oidcClient)
                            .onItem().transformToUni(tokens -> {System.out.println("Tokens: " + tokens); return null; })
                            .await().indefinitely();

                System.out.println("After the await... ");
            }).wait();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        apiService = QuarkusRestClientBuilder.newBuilder()
               .baseUri(URI.create("http://localhost:8084/pst/api/"))
               .build(ProposalRestAPI.class);
       System.out.println("Skipping api calls");
       /*
        try {
            System.out.println(mapper.writeValueAsString(apiService.getObservatories()));
            System.out.println(mapper.writeValueAsString(apiService.getObservingProposal(1)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        */
    }
}
