package org.orph2020.pst.cli;

import io.quarkus.arc.Unremovable;
import io.quarkus.credentials.CredentialsProvider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
@Unremovable
@Named("my-credentials-provider")
public class MyCredentialsProvider implements CredentialsProvider {

    @Override
    public Map<String, String> getCredentials(String credentialsProviderName) {

        Map<String, String> properties = new HashMap<>();
        properties.put(USER_PROPERTY_NAME, "user");
        properties.put(PASSWORD_PROPERTY_NAME, "pass");
        return properties;
    }

}