package com.hyphenate.easeim;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.hyphenate.easeim.widget.ChatTotalLayout;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMChatRoomChangeListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMUserInfo;
import com.hyphenate.easeim.constant.DemoConstant;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

import io.agora.extension.AgoraExtAppBase;
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
    private boolean isAllMute;
    private boolean isSingleMute;

    private EMMessageListener messageListener;
    private EMChatRoomChangeListener chatRoomChangeListener;

    private ChatTotalLayout totalLayout;


    @Override
    public void onExtAppLoaded(Context context) {
        this.context = context;
        loginLimit = 0;
        joinLimit = 0;
        isAllMute = false;
        isSingleMute = false;
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
        EMClient.getInstance().chatroomManager().leaveChatRoom(chatRoomId);
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
            }

            @Override
            public void onMuteListRemoved(String chatRoomId, List<String> mutes) {
            }

            @Override
            public void onWhiteListAdded(String chatRoomId, List<String> whitelist) {
                for (String member : whitelist) {
                    EMLog.e(TAG, "onWhiteListAdded:" + member);
                    if (member.equals(EMClient.getInstance().getCurrentUser())) {
                        isSingleMute = true;
                        if (!isAllMute) {
                            totalLayout.sendHandleEnable(context.getResources().getString(R.string.you_have_been_silenced), false);
                        }
                    }
                }
            }

            @Override
            public void onWhiteListRemoved(String chatRoomId, List<String> whitelist) {
                for (String member : whitelist) {
                    EMLog.e(TAG, "onWhiteListRemoved:" + member);
                    if (member.equals(EMClient.getInstance().getCurrentUser())) {
                        isSingleMute = false;
                        if (!isAllMute) {
                            totalLayout.sendHandleEnable(context.getResources().getString(R.string.send_danmaku), true);
                        }
                    }
                }
            }

            @Override
            public void onAllMemberMuteStateChanged(String roomId, boolean isMuted) {

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
            public void onAnnouncementChanged(String roomId, String announcement) {
                EMLog.e(TAG, "announcement_change:" + announcement);
                if (chatRoomId.equals(roomId)) {
                    if (announcement.substring(0, 1).equals("0")) {
                        isAllMute = false;
                        if (isSingleMute) {
                            totalLayout.sendHandleEnable(context.getResources().getString(R.string.you_have_been_silenced), true);
                        } else {
                            totalLayout.sendHandleEnable(context.getResources().getString(R.string.send_danmaku), true);
                        }
                    } else if (announcement.substring(0, 1).equals("1")) {
                        isAllMute = true;
                        totalLayout.sendHandleEnable(context.getResources().getString(R.string.total_silence), false);
                    } else {
                        EMLog.e(TAG, "announcement_change:" + announcement);
                    }
                }
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
                    EMLog.e(TAG, "create failed:" + e.getErrorCode() + ":" + e.getDescription());
                    totalLayout.sendHandleToast(context.getResources().getString(R.string.register_failed) + ":" + e.getDescription());
                    totalLayout.sendHandleEnable(context.getResources().getString(R.string.register_failed), false);
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
                info.setExt(String.valueOf(DemoConstant.ROLE_STUDENT));
                EaseIM.getInstance().updateOwnInfo(info);
                joinChatRoom();
            }

            @Override
            public void onError(int code, String error) {
                EMLog.e(TAG, "login failed:" + code + ":" + error);
                if (loginLimit == 2) {
                    totalLayout.sendHandleToast(context.getResources().getString(R.string.login_failed) + ":" + error);
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
                isAllMemberMuted();
            }

            @Override
            public void onError(int error, String errorMsg) {
                EMLog.e(TAG, "join failed:" + error + ":" + errorMsg);
                if (joinLimit == 2) {
                    totalLayout.sendHandleToast(context.getResources().getString(R.string.join_failed) + ":" + errorMsg);
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
     * 获取公告的首字符判断是否是全员禁言
     * 0 否
     * 1 是
     */
    private void isAllMemberMuted() {
        EMClient.getInstance().chatroomManager().asyncFetchChatRoomAnnouncement(chatRoomId, new EMValueCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                if (value.isEmpty()) {
                    totalLayout.sendHandleEnable(context.getResources().getString(R.string.send_danmaku), true);
                } else {
                    if (value.substring(0, 1).equals("0")) {
                        isAllMute = false;
                        totalLayout.sendHandleEnable(context.getResources().getString(R.string.send_danmaku), true);
                    } else if (value.substring(0, 1).equals("1")) {
                        isAllMute = true;
                        totalLayout.sendHandleEnable(context.getResources().getString(R.string.total_silence), false);
                    } else {
                        EMLog.e(TAG, "fetch_announcement:" + value);
                    }
                }
                checkSingleMute();
            }

            @Override
            public void onError(int error, String errorMsg) {
                EMLog.e(TAG, "fetch_announcement failed: " + error + ":" + errorMsg);
            }
        });
    }

    /**
     * 判断是否被单独禁言
     * 以白名单实现
     */
    private void checkSingleMute() {
        EMClient.getInstance().chatroomManager().checkIfInChatRoomWhiteList(chatRoomId, new EMValueCallBack<Boolean>() {
            @Override
            public void onSuccess(Boolean value) {
                EMLog.e(TAG, "checkIfInChatRoomWhiteList:" + value);
                isSingleMute = value;
                if (value) {
                    if (!isAllMute) {
                        totalLayout.sendHandleEnable(context.getResources().getString(R.string.you_have_been_silenced), false);
                    }
                }
            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });
    }


}
