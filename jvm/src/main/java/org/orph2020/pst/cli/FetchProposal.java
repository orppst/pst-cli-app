package org.orph2020.pst.cli;
/*
 * Created on 01/08/2023 by Paul Harrison (paul.harrison@manchester.ac.uk).
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.oidc.client.OidcClient;
import io.quarkus.oidc.client.OidcClientConfig;
import io.quarkus.oidc.client.OidcClients;
import io.quarkus.rest.client.reactive.QuarkusRestClientBuilder;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import jakarta.enterprise.context.Dependent;
import org.orph2020.pst.apiimpl.client.ProposalRestAPI;
import picocli.CommandLine.*;

import jakarta.inject.Inject;

import java.net.URI;
import java.util.HashMap;
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

    private static OidcClient oidcClient;
    private static boolean authenticated;//= false;

    //Programmatically create a client
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
        Map<String, String> grantParams = new HashMap<>();

        if(!authenticated) {
            createOidcClient().subscribe().with(client -> {
                oidcClient = client;
                oidcClient.getTokens(grantParams).subscribe().with(
                        Unchecked.consumer(tokens -> {
                            System.out.println("tokens = " + tokens.toString());
                            authenticated = true;
                        })
                );
            });
        } /*else*/ {
            apiService = QuarkusRestClientBuilder.newBuilder()
                    .baseUri(URI.create("http://localhost:8084/pst/api/"))
          //          .clientHeadersFactory()
                    .build(ProposalRestAPI.class);

            try {
                System.out.println(mapper.writeValueAsString(apiService.getObservatories()));
                System.out.println(mapper.writeValueAsString(apiService.getObservingProposal(1)));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
