package com.example.DiplomaSite.configuration;

import com.example.DiplomaSite.dto.UserCredentials;
import jakarta.ws.rs.core.Response;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * A utility service for direct Keycloak interactions:
 * 1. Get token using a username/password (Resource Owner Password Credentials).
 * 2. Create a user in Keycloak.
 * 3. Extract Keycloak user info from the Authentication principal.
 */
@Service
public class KeycloakUtils {

    private static final Logger log = LoggerFactory.getLogger(KeycloakUtils.class);

    private final Keycloak keycloak;
    private final String realm;
    private final String authServerUrl;
    private final String clientId;
    private final String clientSecret;

    /**
     * Constructor injection for Keycloak + realm/client info.
     *
     * NOTE: If you are using the older Keycloak Spring Boot Adapter, you might
     * have a bean for Keycloak auto-configured. Otherwise, you can directly build
     * the 'keycloak' object here (client credentials approach).
     */
    @Autowired
    public KeycloakUtils(
            Keycloak keycloak,
            @Value("${keycloak.realm}") String realm,
            @Value("${keycloak.auth-server-url}") String authServerUrl,
            @Value("${keycloak.resource}") String clientId,
            @Value("${keycloak.credentials.secret}") String clientSecret
    ) {
        this.keycloak = keycloak;
        this.realm = realm;
        this.authServerUrl = authServerUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    /**
     * Get an access token using a username/password (Resource Owner Password flow).
     * <p>
     * This is a legacy approach for obtaining tokens via direct user credentials.
     * Typically, OAuth2 Resource Server or OAuth2 Login flows are recommended
     * instead, but if you must do ROPC, this works.
     *
     * @param userCredentials username + password
     * @return Access token (as a String)
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

        // Acquire the token response
        AccessTokenResponse tokenResponse = userKeycloak.tokenManager().getAccessToken();
        return tokenResponse.getToken();
    }

    /**
     * Create a new Keycloak user using the admin client in "keycloak" bean.
     *
     * @param userCredentials holds the username + password for the new user
     * @return The newly created User ID (or a message if ID retrieval fails)
     */
    public String createUser(UserCredentials userCredentials) {
        // Build Keycloak credential representation
        CredentialRepresentation credentials = new CredentialRepresentation();
        credentials.setTemporary(false);
        credentials.setType(CredentialRepresentation.PASSWORD);
        credentials.setValue(userCredentials.getPassword());

        // Build user representation
        UserRepresentation user = new UserRepresentation();
        user.setUsername(userCredentials.getUsername());
        user.setEnabled(true);
        user.setCredentials(Collections.singletonList(credentials));

        // Create user
        Response response = keycloak.realm(realm)
                .users()
                .create(user);

        if (response.getStatus() == 201) {
            // Extract the created user ID from the response Location
            String location = response.getHeaderString("Location");
            if (location != null) {
                return location.substring(location.lastIndexOf("/") + 1);
            }
            return "User created successfully, but user ID could not be retrieved from response.";
        } else {
            throw new RuntimeException("Failed to create user: " + response.getStatusInfo().getReasonPhrase());
        }
    }

    /**
     * Assigns a client role to a user in Keycloak.
     *
     * @param userId    The Keycloak user ID
     * @param roleName  The name of the role to assign (e.g., "student" or "teacher")
     */
    public void assignClientRole(String userId, String roleName) {
        // Get the client UUID
        List<ClientRepresentation> clients = keycloak.realm(realm)
                .clients()
                .findByClientId(clientId);

        if (clients.isEmpty()) {
            throw new RuntimeException("No client found with clientId: " + clientId);
        }

        String clientUuid = clients.get(0).getId();

        // Get the role representation for the specified role
        RoleRepresentation role = keycloak.realm(realm)
                .clients()
                .get(clientUuid)
                .roles()
                .get(roleName)
                .toRepresentation();

        // Assign the role to the user
        keycloak.realm(realm)
                .users()
                .get(userId)
                .roles()
                .clientLevel(clientUuid)
                .add(Collections.singletonList(role));
    }

    /**
     * Delete the user with the specified Keycloak ID.
     *
     * @param keycloakId The Keycloak user ID
     * @return true if the user was deleted successfully, false otherwise
     */

    public boolean deleteUser(String keycloakId) {
        Response response = keycloak.realm(realm)
                .users()
                .delete(keycloakId);

        return response.getStatus() == 204;
    }

    /**
     * Extract the Keycloak user ID (sub) from the JWT token.
     *
     * @param auth Spring Security Authentication
     * @return The 'sub' field (Keycloak user ID)
     */
    public static String getUserId(Authentication auth) {
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            return null;
        }
        return jwt.getClaimAsString("sub");
    }

    /**
     * Get all roles assigned to the user (both realm-level and resource-level roles).
     *
     * @param auth Spring Security Authentication
     * @return A set of role names
     */
    public static Set<String> getUserRoles(Authentication auth) {
        Set<String> roles = new HashSet<>();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            return roles;
        }

        // Extract realm roles
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            Object rolesObj = realmAccess.get("roles");
            if (rolesObj instanceof List<?> roleList) {
                roles.addAll((List<String>) roleList); // Convert List to Set
            }
        }

        // Extract resource-level roles
        Map<String, Map<String, Object>> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess != null) {
            for (Map.Entry<String, Map<String, Object>> entry : resourceAccess.entrySet()) {
                Map<String, Object> resourceRoles = entry.getValue();
                if (resourceRoles != null && resourceRoles.containsKey("roles")) {
                    Object rolesObj = resourceRoles.get("roles");
                    if (rolesObj instanceof List<?> roleList) {
                        roles.addAll((List<String>) roleList); // Convert List to Set
                    }
                }
            }
        }

        return roles;
    }
}
