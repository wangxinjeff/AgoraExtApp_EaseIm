package com.hyphenate.easeim;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.ChatTotalLayout;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMChatRoomChangeListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMCustomMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMUserInfo;
import com.hyphenate.easeim.constant.DemoConstant;
import com.hyphenate.easeim.domain.Gift;
import com.hyphenate.easeim.interfaces.GiftViewListener;
import com.hyphenate.easeim.modules.danmaku.Danmaku;
import com.hyphenate.easeim.modules.danmaku.DanmakuCreator;
import com.hyphenate.easeim.modules.danmaku.DanmakuView;
import com.hyphenate.easeim.utils.ScreenUtil;
import com.hyphenate.easeim.interfaces.ChatInputMenuListener;
import com.hyphenate.easeim.utils.SoftInputUtil;
import com.hyphenate.easeim.widget.ChatInputMenu;
import com.hyphenate.easeim.widget.GiftView;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;
import com.hyphenate.easeim.modules.danmaku.DanmakuManager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.agora.extension.AgoraExtAppBase;
import io.agora.extension.AgoraExtAppContext;
import io.agora.extension.AgoraExtAppRoomInfo;
import io.agora.extension.AgoraExtAppUserInfo;

public class EaseChatExtApp extends AgoraExtAppBase {

    private static final String TAG = EaseChatExtApp.class.getSimpleName();

    private View layout;
    private Context context;

    private String userName = "";
    private String chatRoomId = "148364667715585";
    private String nickName = "学生A";
    private String avatarUrl = "https://download-sdk.oss-cn-beijing.aliyuncs.com/downloads/IMDemo/avatar/Image1.png";
    private String roomUuid = "";

    private int loginLimit;
    private int joinLimit;

    private EMMessageListener messageListener;
    private EMChatRoomChangeListener chatRoomChangeListener;

    private ChatTotalLayout totalLayout;


    @Override
    public void onExtAppLoaded(Context context) {
        this.context = context;
        loginLimit = 0;
        joinLimit = 0;
        EaseIM.getInstance().init(context);
    }

    @NotNull
    @Override
    public View onCreateView(@NotNull Context content) {
        AgoraExtAppUserInfo userInfo = getExtAppContext().getLocalUserInfo();
        AgoraExtAppRoomInfo roomInfo = getExtAppContext().getRoomInfo();
        Map<String, Object> properties = getExtAppContext().getProperties();
        EMLog.e(TAG, "userInfo:" + userInfo.toString());
        EMLog.e(TAG, "roomInfo:" + roomInfo.toString());
        EMLog.e(TAG, "properties:" + properties.toString());
        userName = userInfo.getUserUuid();
        roomUuid = roomInfo.getRoomUuid();
        nickName = (String) properties.get(DemoConstant.NICK_NAME);
        avatarUrl = (String) properties.get(DemoConstant.AVATAR_URL);
//        chatRoomId = (String) properties.get(DemoConstant.CHAT_ROOM_ID);
        layout = LayoutInflater.from(content).inflate(
                R.layout.ease_chat_layout, null, false);
        totalLayout = layout.findViewById(R.id.chat_total);
        totalLayout.setAvatarUrl(avatarUrl);
        totalLayout.setChatRoomId(chatRoomId);
        totalLayout.setNickName(nickName);
        totalLayout.setRoomUuid(roomUuid);
        initEaseListener();
        loginIM(userName, DemoConstant.DEFAULT_PWD);
        return layout;
    }

    @Override
    public void onPropertyUpdated(Map<String, Object> properties, Map<String, Object> cause) {
    }

    @Override
    public void onExtAppUnloaded() {
        totalLayout.cancelHandler();
        cancelEaseListener();
        logoutIM();
    }

    @Override
    public void onRoomInfoUpdate(@NotNull AgoraExtAppRoomInfo roomInfo) {
        super.onRoomInfoUpdate(roomInfo);
    }

    @Override
    public void onLocalUserInfoUpdate(@NotNull AgoraExtAppUserInfo userInfo) {
        super.onLocalUserInfoUpdate(userInfo);
    }

    @Override
    public void onPropertiesUpdate(@NotNull Map<String, Object> properties, @Nullable Map<String, Object> cause) {
        super.onPropertiesUpdate(properties, cause);
    }


    public static String getAppIdentifier() {
        return "com.easemob.chat";
    }

    public static int getAppIconResource() {
        return io.agora.extension.R.drawable.agora_tool_icon_countdown;
    }

    /**
     * 注册消息监听
     */
    private void initEaseListener() {
        messageListener = new EMMessageListener() {
            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                for (EMMessage message : messages) {
                    // 只显示普通弹幕消息
                    if (message.getIntAttribute(DemoConstant.MSG_TYPE, 0) == DemoConstant.MSG_TYPE_NORMAL) {
                        totalLayout.sendHandleMessage(message);
                    }
                }
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                for (EMMessage message : messages) {
                    EMCmdMessageBody body = (EMCmdMessageBody) message.getBody();
                    String action = body.action();
                    if (action.equals(DemoConstant.DEL_ACTION)) {
                        String msgId = message.getStringAttribute(DemoConstant.MSG_ID, "");
                        totalLayout.sendHandleRemoveDanmaku(msgId);
                    }
                }
            }

            @Override
            public void onMessageRead(List<EMMessage> messages) {

            }

            @Override
            public void onMessageDelivered(List<EMMessage> messages) {

            }

            @Override
            public void onMessageRecalled(List<EMMessage> messages) {
            }

            @Override
            public void onMessageChanged(com.hyphenate.chat.EMMessage message, Object change) {

            }
        };
        EMClient.getInstance().chatManager().addMessageListener(messageListener);

        chatRoomChangeListener = new EMChatRoomChangeListener() {
            @Override
            public void onChatRoomDestroyed(String roomId, String roomName) {

            }

            @Override
            public void onMemberJoined(String roomId, String participant) {

            }

            @Override
            public void onMemberExited(String roomId, String roomName, String participant) {

            }

            @Override
            public void onRemovedFromChatRoom(int reason, String roomId, String roomName, String participant) {

            }

            @Override
            public void onMuteListAdded(String chatRoomId, List<String> mutes, long expireTime) {
                for (String member : mutes) {
                    totalLayout.sendHandleEnable(context.getResources().getString(R.string.you_have_been_silenced), false);
                }
            }

            @Override
            public void onMuteListRemoved(String chatRoomId, List<String> mutes) {
                for (String member : mutes) {
                    totalLayout.sendHandleEnable(context.getResources().getString(R.string.send_danmaku), true);
                }
            }

            @Override
            public void onWhiteListAdded(String chatRoomId, List<String> whitelist) {

            }

            @Override
            public void onWhiteListRemoved(String chatRoomId, List<String> whitelist) {

            }

            @Override
            public void onAllMemberMuteStateChanged(String roomId, boolean isMuted) {
                if (chatRoomId.equals(roomId)) {
                    if (isMuted) {
                        totalLayout.sendHandleEnable(context.getResources().getString(R.string.total_silence), false);
                    } else {
                        totalLayout.sendHandleEnable(context.getResources().getString(R.string.send_danmaku), true);
                    }

                }
            }

            @Override
            public void onAdminAdded(String chatRoomId, String admin) {

            }

            @Override
            public void onAdminRemoved(String chatRoomId, String admin) {

            }

            @Override
            public void onOwnerChanged(String chatRoomId, String newOwner, String oldOwner) {

            }

            @Override
            public void onAnnouncementChanged(String chatRoomId, String announcement) {

            }
        };
        EMClient.getInstance().chatroomManager().addChatRoomChangeListener(chatRoomChangeListener);

    }

    /**
     * 注销监听
     */
    private void cancelEaseListener() {
        EMClient.getInstance().chatManager().removeMessageListener(messageListener);
        EMClient.getInstance().chatroomManager().removeChatRoomListener(chatRoomChangeListener);
    }

    /**
     * 创建用户
     *
     * @param userName
     * @param pwd
     */
    private void createIM(String userName, String pwd) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().createAccount(userName, pwd);
                    loginIM(userName, pwd);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /***
     * 登录环信
     */
    private void loginIM(String userName, String pwd) {
        loginLimit++;
        totalLayout.sendHandleEnable(context.getResources().getString(R.string.in_the_login), false);
        EMClient.getInstance().login(userName, pwd, new EMCallBack() {
            @Override
            public void onSuccess() {
                EMUserInfo info = new EMUserInfo();
                info.setNickName(nickName);
                info.setAvatarUrl(avatarUrl);
                EaseIM.getInstance().updateOwnInfo(info);
                joinChatRoom();
            }

            @Override
            public void onError(int code, String error) {
                EMLog.e("Login:", code + ":" + error);
                if (loginLimit == 2) {
                    totalLayout.sendHandleToast(context.getResources().getString(R.string.login_failed));
                    totalLayout.sendHandleEnable(context.getResources().getString(R.string.login_failed), false);
                    return;
                }
                // 判断不存在去注册再登录
                if (code == EMError.USER_NOT_FOUND) {
                    loginLimit = 0;
                    createIM(userName, pwd);
                } else {
                    loginIM(userName, pwd);
                }
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
    }

    /***
     * 加入聊天室
     */
    private void joinChatRoom() {
        joinLimit++;
        totalLayout.sendHandleEnable(context.getResources().getString(R.string.in_the_join), false);
        EMClient.getInstance().chatroomManager().joinChatRoom(chatRoomId, new EMValueCallBack<EMChatRoom>() {
            @Override
            public void onSuccess(EMChatRoom value) {
                EMLog.e("Login:", "join success");
                isAllMemberMuted();
            }

            @Override
            public void onError(int error, String errorMsg) {
                EMLog.e("Login:", "join  " + error + ":" + errorMsg);
                if (joinLimit == 2) {
                    totalLayout.sendHandleToast(context.getResources().getString(R.string.join_failed));
                    totalLayout.sendHandleEnable(context.getResources().getString(R.string.join_failed), false);
                    return;
                }
                joinChatRoom();
            }
        });
    }

    /***
     * 登出环信
     */
    private void logoutIM() {
        EMClient.getInstance().logout(false);
    }


    /**
     * 判断聊天室是否是全员禁言状态
     */
    private void isAllMemberMuted() {
        EMClient.getInstance().chatroomManager().asyncFetchChatRoomFromServer(chatRoomId, new EMValueCallBack<EMChatRoom>() {
            @Override
            public void onSuccess(EMChatRoom value) {
                if (value.isAllMemberMuted()) {
                    totalLayout.sendHandleEnable(context.getResources().getString(R.string.total_silence), false);
                } else {
                    totalLayout.sendHandleEnable(context.getResources().getString(R.string.send_danmaku), true);
                }
            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });
    }



}
