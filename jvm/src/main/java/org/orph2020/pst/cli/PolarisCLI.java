package org.orph2020.pst.cli;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.arc.Arc;
import io.quarkus.oidc.client.*;
import io.quarkus.oidc.client.runtime.OidcClientsConfig;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.jose4j.json.internal.json_simple.parser.JSONParser;
import org.jose4j.json.internal.json_simple.parser.ParseException;
import org.orph2020.pst.apiimpl.client.ProposalRestAPI;
import picocli.CommandLine;
import picocli.CommandLine.*;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.lang.Thread.sleep;

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
public class PolarisCLI implements QuarkusApplication {

    private static final Logger logger = Logger.getLogger(PolarisCLI.class);

    @Inject
    IFactory factory;

    @Inject
    ObjectMapper mapper;


    @Inject
    OidcClient oidcClient;

    @Inject
    OidcClients oidcClients;

    @Inject
    OidcClientsConfig clientConfigs;


    @RestClient
    ProposalRestAPI api;


    public PolarisCLI(){
       logger.info("Starting Polaris CLI");


    }
    @Override
    public int run(String... args) throws Exception {
        authenticate();
        return new CommandLine(this, factory)
              .execute(args);
    }

    //IMPL https://quarkus.io/guides/security-openid-connect-client-reference#quarkus-oidc-client_quarkus-oidc-client-grant-options-grant-name
    //IMPL https://quarkus.io/guides/rest-client#programmatic-client-creation-with-quarkusrestclientbuilder
    //IMPL https://github.com/quarkusio/quarkus/pull/39262

    //IMPL https://www.keycloak.org/securing-apps/token-exchange

    public void authenticate() {
        logger.info("starting authentication");

        String device_code = "";
        String verification_uri_complete = "";
        Long poll_delay_seconds = 10L;

        // Generate a device code
        HttpClient httpClient = HttpClient.newHttpClient();
        var client = HttpClient.newHttpClient();
        Config config = ConfigProvider.getConfig();

        URI authURL = URI.create(config.getConfigValue("quarkus.oidc-client.auth-server-url").getValue()
              + "/protocol/openid-connect/auth/device");

        HttpRequest request = HttpRequest.newBuilder(authURL)
              .POST(HttpRequest.BodyPublishers.ofString("client_id="
                    +      config.getConfigValue("quarkus.oidc-client.client-id").getValue()
                    + "&client_secret="
                    + config.getConfigValue("quarkus.oidc-client.credentials.client-secret.value").getValue()))
              .setHeader("Content-Type", "application/x-www-form-urlencoded")
              .build();

        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 200) {
                System.out.println(response.body());

                JSONParser parser = new JSONParser();
                JSONObject jsonResponse = (JSONObject) parser.parse(response.body());

                device_code = (String) jsonResponse.get("device_code");
                verification_uri_complete = (String) jsonResponse.get("verification_uri_complete");
                poll_delay_seconds = (Long) jsonResponse.get("interval");
            }
            else {
                throw new RuntimeException("first stage of device authentication failed:"+response.body());
            }

        } catch (IOException | InterruptedException | ParseException e) {
            throw new RuntimeException("first stage of device authentication failed:",e);
        }

        boolean browserOpened = false;

        // Open browser for credentials
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(verification_uri_complete));
                browserOpened = true;

            } catch (IOException | URISyntaxException e) {
                logger.warn("Unable to open browser: " + e.getMessage());
            }
        } else {
            logger.warn("Desktop is not supported");
        }
        if (!browserOpened) {
            System.out.println("you need to open the following URL in a browser:" + verification_uri_complete);
        }

        // Use device code to get an auth token
        Map<String, String> grantParams = new HashMap<>();
        grantParams.put("device_code", device_code);
        String encodedIdToken = "";

        boolean failed = false;
        boolean waiting = true;

        while (waiting && !failed) {
            try {
                sleep(poll_delay_seconds * 1000L);

                OidcClientConfig cfg = clientConfigs.defaultClient;
                cfg.getGrant().setType(OidcClientConfig.Grant.Type.DEVICE);
                OidcClient devclient = oidcClients.newClient(cfg).await().indefinitely();
                Tokens tokens = devclient.getTokens(grantParams).await().indefinitely();
                System.out.println("Got a token: " + tokens.getRefreshToken());
                Tokens newtokens = oidcClient.refreshTokens(tokens.getRefreshToken()).await().indefinitely();
                System.out.println("Refreshed access token: " + newtokens.getAccessToken());
                waiting = false;

            } catch (InterruptedException | OidcClientException e) {
                System.out.println("OidcClientException: " + e.getMessage());
                JSONParser parser = new JSONParser();
                JSONObject jsonResponse = null;
                try {
                    jsonResponse = (JSONObject) parser.parse(e.getMessage());
                    String error = (String) jsonResponse.get("error");

                    if (!error.equals("authorization_pending") && !error.equals("slow_down")) {
                        //Something other than pending is an error
                        failed = true;
                    }

                } catch (ParseException ex) {
                    throw new RuntimeException(ex);
                }

            }

        }
    }
}
