package pers.ahogek.youtube.wheelbarrow;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.LiveChatMessage;
import com.google.api.services.youtube.model.LiveChatMessageSnippet;
import com.google.api.services.youtube.model.LiveChatTextMessageDetails;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;

/**
 * <p>
 *
 * </p>
 *
 * @author AhogeK
 * @since 2020-10-29 16:18
 */
public class YoutubeLiveChatMessagesInsert {

    private static final String CLIENT_SECRETS= "/client_secret.json";
    private static final Collection<String> SCOPES =
            Arrays.asList("https://www.googleapis.com/auth/youtube.force-ssl");

    private static final String APPLICATION_NAME = "youtube-wheelbarrow";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /**
     * Create an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize(final NetHttpTransport httpTransport) throws IOException {
        // Load client secrets.
        InputStream in = YoutubeLiveChatMessagesInsert.class.getResourceAsStream(CLIENT_SECRETS);
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                        .build();
        Credential credential =
                new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        return credential;
    }

    /**
     * Build and return an authorized API client service.
     *
     * @return an authorized API client service
     * @throws GeneralSecurityException, IOException
     */
    public static YouTube getService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = authorize(httpTransport);
        return new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Call function to create API service object. Define and
     * execute API request. Print API response.
     *
     * @throws GeneralSecurityException, IOException, GoogleJsonResponseException
     */
    public static void main(String[] args)
            throws GeneralSecurityException, IOException, GoogleJsonResponseException {
        YouTube youtubeService = getService();

        // Define the LiveChatMessage object, which will be uploaded as the request body.
        LiveChatMessage liveChatMessage = new LiveChatMessage();

        // Add the snippet object property to the LiveChatMessage object.
        LiveChatMessageSnippet snippet = new LiveChatMessageSnippet();
        snippet.setLiveChatId("Cg0KC3hKMlR3cFVEekRRKicKGFVDZG41QlEwNlhxZ1hvQXhJaGJxdzVSZxILeEoyVHdwVUR6RFE");
        LiveChatTextMessageDetails textMessageDetails = new LiveChatTextMessageDetails();
        textMessageDetails.setMessageText("test");
        snippet.setTextMessageDetails(textMessageDetails);
        snippet.setType("textMessageEvent");
        liveChatMessage.setSnippet(snippet);

        // Define and execute the API request
        YouTube.LiveChatMessages.Insert request = youtubeService.liveChatMessages()
                .insert("snippet", liveChatMessage);
        int i = 0;
        while (i != 3) {
            LiveChatMessage response = request.execute();
            System.out.println(response);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
        }
    }
}
