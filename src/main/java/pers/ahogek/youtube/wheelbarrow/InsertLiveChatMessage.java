package pers.ahogek.youtube.wheelbarrow;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.LiveChatMessage;
import com.google.api.services.youtube.model.LiveChatMessageSnippet;
import com.google.api.services.youtube.model.LiveChatTextMessageDetails;
import com.google.common.collect.Lists;
import pers.ahogek.youtube.wheelbarrow.util.Auth;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * <p>
 * Inserts a message into a live broadcast of the current user or a video specified by id.
 * </p>
 *
 * @author AhogeK
 * @since 2020-10-29 10:36
 */
public class InsertLiveChatMessage implements KeyListener {

    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    private static YouTube youtube;

    /**
     * Inserts a message into a live broadcast.
     *
     * @param args The message to insert (required) followed by the videoId (optional).
     * If the videoId is given, live chat messages will be retrieved from the chat associated with
     * this video. If the videoId is not specified, the signed in user's current live broadcast will
     * be used instead.
     */
    public static void main(String[] args) {
        // This OAuth 2.0 access scope allows for write access to the authenticated user's account.
        List<String> scopes = Lists.newArrayList(YouTubeScopes.YOUTUBE_FORCE_SSL);

        try {
            // Authorize the request.
            Credential credential = Auth.authorize(scopes, "insertlivechatmessage");

            // This object is used to make YouTube Data API requests.
            youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
                    .setApplicationName("youtube-wheelbarrow").build();

            Scanner sc = new Scanner(System.in);
            System.out.println("请输入房间ID（即直播间地址v=[此即为id]）");
            String roomId = sc.nextLine();
            System.out.println("请输入对蝗弹药（暂只支持单句）：");
            String message = sc.nextLine();
            System.out.println("请输入弹药间隔（ms）：");
            long interval = sc.nextLong();

            // Get the liveChatId
            String liveChatId = null;
            while (liveChatId == null || liveChatId.isEmpty()) {
                liveChatId = GetLiveChatId.getLiveChatId(youtube, roomId);
                if (liveChatId != null) {
                    System.out.println("Live chat id: " + liveChatId);
                } else {
                    System.err.println("房间ID有误请重新输入！");
                    roomId = sc.nextLine();
                }
            }

            // Insert the message into live chat
            LiveChatMessage liveChatMessage = new LiveChatMessage();
            LiveChatMessageSnippet snippet = new LiveChatMessageSnippet();
            snippet.setType("textMessageEvent");
            snippet.setLiveChatId(liveChatId);
            LiveChatTextMessageDetails details = new LiveChatTextMessageDetails();
            details.setMessageText(message);
            snippet.setTextMessageDetails(details);
            liveChatMessage.setSnippet(snippet);
            YouTube.LiveChatMessages.Insert liveChatInsert =
                    youtube.liveChatMessages().insert("snippet", liveChatMessage);
            while (true) {
                System.err.println("[CTRL+C] 退出运行");
                LiveChatMessage response = liveChatInsert.execute();
                System.out.println(response);
                Thread.sleep(interval);
            }
        } catch (GoogleJsonResponseException e) {
            System.err
                    .println("GoogleJsonResponseException code: " + e.getDetails().getCode() + " : "
                            + e.getDetails().getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            e.printStackTrace();
        } catch (Throwable t) {
            System.err.println("Throwable: " + t.getMessage());
            t.printStackTrace();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
