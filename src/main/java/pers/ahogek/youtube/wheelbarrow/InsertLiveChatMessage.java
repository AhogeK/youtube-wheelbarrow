package pers.ahogek.youtube.wheelbarrow;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.LiveChatMessage;
import com.google.api.services.youtube.model.LiveChatMessageSnippet;
import com.google.api.services.youtube.model.LiveChatTextMessageDetails;
import com.google.common.collect.Lists;
import pers.ahogek.youtube.wheelbarrow.file.listener.FileChangeListener;
import pers.ahogek.youtube.wheelbarrow.file.monitor.DirectoryTargetMonitor;
import pers.ahogek.youtube.wheelbarrow.util.Auth;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import static pers.ahogek.youtube.wheelbarrow.common.CommonProperty.*;

/**
 * <p>
 * Inserts a message into a live broadcast of the current user or a video specified by id.
 * </p>
 *
 * @author AhogeK
 * @since 2020-10-29 10:36
 */
public class InsertLiveChatMessage {

    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    private static YouTube youtube;

    /**
     * Inserts a message into a live broadcast.
     *
     * @param args The message to insert (required) followed by the videoId (optional).
     *             If the videoId is given, live chat messages will be retrieved from the chat associated with
     *             this video. If the videoId is not specified, the signed in user's current live broadcast will
     *             be used instead.
     */
    public static void main(String[] args) {
        // This OAuth 2.0 access scope allows for write access to the authenticated user's account.
        List<String> scopes = Lists.newArrayList(YouTubeScopes.YOUTUBE_FORCE_SSL);

        Scanner sc = new Scanner(System.in);
        System.out.println("请输入弹药文件夹路径：");
        String path = sc.nextLine();
        System.err.println("文件弹药需要保存后才会倒入至系统（支持实时更新）");
        System.out.println("请输入房间ID（即直播间地址v=[此即为id]）");
        String roomId = sc.nextLine();
        System.out.println("请输入弹药间隔（ms）：");
        long interval = sc.nextLong();

        new Thread(() -> {
            DirectoryTargetMonitor monitor = new DirectoryTargetMonitor(new FileChangeListener(), path);
            try {
                monitor.startMonitor();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        ammunition.add("t s k k you play you mom play ne");

        try {
            // Authorize the request.
            Credential credential = Auth.authorize(scopes, "insertlivechatmessage");

            // This object is used to make YouTube Data API requests.
            youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
                    .setApplicationName("youtube-wheelbarrow").build();

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
            LiveChatMessage response = null;

            int index = 0;
            while (true) {
                System.out.println("############################################");
                System.err.println("[CTRL+C] 退出运行");
                try {
                    snippet.setType("textMessageEvent");
                    snippet.setLiveChatId(liveChatId);
                    LiveChatTextMessageDetails details = new LiveChatTextMessageDetails();
                    System.out.println("弹药：" + ammunition.get(index));
                    details.setMessageText(ammunition.get(index));
                    snippet.setTextMessageDetails(details);
                    liveChatMessage.setSnippet(snippet);
                    YouTube.LiveChatMessages.Insert liveChatInsert =
                            youtube.liveChatMessages().insert("snippet", liveChatMessage);
                    response = liveChatInsert.execute();
                    Thread.sleep(interval);
                } catch (GoogleJsonResponseException e) {
                    System.err
                            .println("GoogleJsonResponseException code: " + e.getDetails().getCode() + " : "
                                    + e.getDetails().getMessage());
                    e.printStackTrace();
                    System.out.println("+++++++++++++++++++++++++++++++++++++++");
                    Thread.sleep(interval += 1000);
                }
                if (interval == 15000) {
                    interval = 1000;
                }
                if (index != ammunition.size() - 1) {
                    index++;
                } else {
                    index = 0;
                }
                if (response != null) {
                    System.out.println(response);
                }
            }
        } catch (GoogleJsonResponseException e) {
            System.err
                    .println("GoogleJsonResponseException code: " + e.getDetails().getCode() + " : "
                            + e.getDetails().getMessage());
            e.printStackTrace();
            System.out.println("+++++++++++++++++++++++++++++++++++++++");
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            e.printStackTrace();
        } catch (Throwable t) {
            System.err.println("Throwable: " + t.getMessage());
            t.printStackTrace();
        }
    }
}
