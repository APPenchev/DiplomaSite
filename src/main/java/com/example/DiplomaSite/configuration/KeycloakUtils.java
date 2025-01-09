package com.example.DiplomaSite.configuration;

import com.example.DiplomaSite.dto.UserCredentials;
import jakarta.ws.rs.core.Response;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class KeycloakUtils {

    private final Keycloak keycloak;
    private final String realm;
    private final String authServerUrl;
    private final String clientId;
    private final String clientSecret;

    @Autowired
    public KeycloakUtils(Keycloak keycloak,
                         @Value("${keycloak.realm}") String realm,
                         @Value("${keycloak.auth-server-url}") String authServerUrl,
                         @Value("${keycloak.resource}") String clientId,
                         @Value("${keycloak.credentials.secret}") String clientSecret) {
        this.keycloak = keycloak;
        this.realm = realm;
        this.authServerUrl = authServerUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    /**
     * Get a token for the provided user credentials.
     */
    public String getToken(UserCredentials userCredentials) {
        Keycloak userKeycloak = KeycloakBuilder.builder()
                .serverUrl(authServerUrl)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(OAuth2Constants.PASSWORD)
                .username(userCredentials.getUsername())
                .password(userCredentials.getPassword())
                .build();

        // Get the token response
        AccessTokenResponse tokenResponse = userKeycloak.tokenManager().getAccessToken();
        return tokenResponse.getToken();
    }

    /**
     * Create a user in Keycloak.
     */
    public String createUser(UserCredentials userCredentials) {
        CredentialRepresentation credentials = new CredentialRepresentation();
        credentials.setTemporary(false);
        credentials.setType(CredentialRepresentation.PASSWORD);
        credentials.setValue(userCredentials.getPassword());

        UserRepresentation user = new UserRepresentation();
        user.setUsername(userCredentials.getUsername());
        user.setEnabled(true);
        user.setCredentials(Collections.singletonList(credentials));

        Response response = keycloak.realm(realm)
                .users()
                .create(user);

        if (response.getStatus() == 201) {

            String location = response.getHeaderString("Location");
            if (location != null) {
                return location.substring(location.lastIndexOf("/") + 1);
            }
            return "User created, but user ID could not be retrieved.";
        } else {
            throw new RuntimeException("Failed to create user: " + response.getStatusInfo().getReasonPhrase());
        }
    }

    /**
     * Get the user's ID from the Keycloak token.
     */
    public static String getUserId(Authentication auth) {
        KeycloakPrincipal<?> kp = (KeycloakPrincipal<?>) auth.getPrincipal();
        return kp.getKeycloakSecurityContext().getToken().getSubject();
    }

    /**
     * Get all roles assigned to the user.
     */
    public static Set<String> getUserRoles(Authentication auth) {
        KeycloakPrincipal<?> kp = (KeycloakPrincipal<?>) auth.getPrincipal();
        AccessToken token = kp.getKeycloakSecurityContext().getToken();
        Set<String> roles = new HashSet<>();
        AccessToken.Access realmAccess = token.getRealmAccess();
        if (realmAccess != null) {
            roles.addAll(realmAccess.getRoles());
        }
        Map<String, AccessToken.Access> resourceAccessMap = token.getResourceAccess();
        if (resourceAccessMap != null) {
            for (Map.Entry<String, AccessToken.Access> entry : resourceAccessMap.entrySet()) {
                AccessToken.Access resourceAccess = entry.getValue();
                if (resourceAccess != null) {
                    roles.addAll(resourceAccess.getRoles());
                }
            }
        }
        return roles;
    }
}
