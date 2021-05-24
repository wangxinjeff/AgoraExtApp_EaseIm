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
import android.widget.TextView;
import android.widget.Toast;

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

public class EaseChatExtApp extends AgoraExtAppBase implements View.OnClickListener, View.OnTouchListener, ChatInputMenuListener, GiftViewListener {

    private static final String TAG = EaseChatExtApp.class.getSimpleName();

    private FrameLayout mContainer;

    private ChatInputMenu chatInputMenu;

    private LinearLayout bottom;

    private DanmakuManager mManager;

    private TextView mDanmakuSend;
    private View layout;
    private Context context;
    private GiftView giftView;
    private ImageView gift;

    private String userName;
    private String chatRoomId = "148364667715585";
    private String nickName = "学生A";
    private String avatarUrl = "https://download-sdk.oss-cn-beijing.aliyuncs.com/downloads/IMDemo/avatar/Image1.png";
    private String roomUuid;

    private static final int MESSAGE_CODE = 0;
    private static final int TOAST_CODE = 1;
    private static final int ENABLE_CODE = 2;
    private static final int UN_ENABLE_CODE = 3;
    private static final int REMOVE_DANMAKU = 4;

    private int loginLimit;
    private int joinLimit;

    private EMMessageListener messageListener;
    private EMChatRoomChangeListener chatRoomChangeListener;

    protected Handler handler;

    //创建handler
    private void initHandler(Context context) {
        handler = new Handler(context.getMainLooper()) {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_CODE) {
                    EMMessage message = (EMMessage) msg.obj;
                    Danmaku danmaku = mDanmakuCreator.create(message);
                    mManager.send(danmaku);
                } else if (msg.what == TOAST_CODE) {
                    String toast = (String) msg.obj;
                    Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
                } else if (msg.what == UN_ENABLE_CODE) {
                    mDanmakuSend.setText((String)msg.obj);
                    unEnableBottomView();
                } else if (msg.what == ENABLE_CODE) {
                    mDanmakuSend.setText((String)msg.obj);
                    enableBottomView();
                } else if (msg.what == REMOVE_DANMAKU) {
                    String msgId = (String) msg.obj;
                    DanmakuView view = mManager.getDanmakuView(msgId);
                    if(view != null)
                        mContainer.removeView(view);
                }
            }
        };
    }


    private DanmakuCreator mDanmakuCreator;

    @Override
    public void onExtAppLoaded(Context context) {
        this.context = context;
        loginLimit = 0;
        joinLimit = 0;
        initHandler(context);
        EaseIM.getInstance().init(context);
    }

    @NotNull
    @Override
    public View onCreateView(@NotNull Context content) {
        AgoraExtAppUserInfo userInfo = getExtAppContext().getLocalUserInfo();
        AgoraExtAppRoomInfo roomInfo = getExtAppContext().getRoomInfo();
        userName = userInfo.getUserName();
        roomUuid = roomInfo.getRoomUuid();
        layout = LayoutInflater.from(content).inflate(
                R.layout.ease_chat_layout, null, false);
        initView();
        initDanmaku();
        initEaseListener();
        loginIM(userName, DemoConstant.DEFAULT_PWD);
        return layout;
    }

    @Override
    public void onPropertyUpdated(Map<String, Object> properties, Map<String, Object> cause) { }

    @Override
    public void onExtAppUnloaded() {
        handler.removeCallbacksAndMessages(null);
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

    private void initView() {
        mContainer = layout.findViewById(R.id.danmaku_container);
        bottom = layout.findViewById(R.id.danmaku_bottom);

        chatInputMenu = layout.findViewById(R.id.input_menu);

        mDanmakuSend = layout.findViewById(R.id.etSend);

        giftView = layout.findViewById(R.id.gift_view);
        gift = layout.findViewById(R.id.gift);
        initListener();
    }

    private void initListener() {
        mContainer.setOnTouchListener(this);
        mDanmakuSend.setOnClickListener(this);
        chatInputMenu.setChatInputMenuListener(this);
        giftView.setGiftViewListener(this);
        gift.setOnClickListener(this);
        SoftInputUtil softInputUtil = new SoftInputUtil();
        softInputUtil.attachSoftInput(chatInputMenu, new SoftInputUtil.ISoftInputChanged() {
            @Override
            public void onChanged(boolean isSoftInputShow, int softInputHeight, int viewOffset) {
                if (isSoftInputShow) {
                    chatInputMenu.setTranslationY(chatInputMenu.getTranslationY() - viewOffset);
                } else {
                    chatInputMenu.setTranslationY(0);
                }
            }
        });
    }


    /**
     * 初始化弹幕参数
     */
    private void initDanmaku() {
        mManager = DanmakuManager.getInstance();
        mManager.init(context, mContainer); // 必须首先调用init方法

        DanmakuManager.Config config = mManager.getConfig(); // 弹幕相关设置
        config.setDurationScroll(10000); // 设置滚动字幕显示时长，默认10秒
        config.setLineHeight(ScreenUtil.autoSize(60)); // 设置行高

        mDanmakuCreator = new DanmakuCreator();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.etSend) {
            showInputMenu();
        } else if (id == R.id.gift) {
            showGiftView();
        }
    }

    /***
     * 显示礼物View
     */
    private void showGiftView() {
        giftView.setVisibility(View.VISIBLE);
        hideInputBottom();
    }

    /***
     * 隐藏礼物View
     */
    private void hideGiftView() {
        giftView.setVisibility(View.INVISIBLE);
        showInputBottom();
    }

    /***
     * 发送消息
     * @param content
     */
    @Override
    public void onSendMessage(String content) {
        sendTextMessage(content);
        hideInputMenu();
    }

    /***
     * 隐藏底部View
     */
    private void hideInputBottom() {
        bottom.setVisibility(View.INVISIBLE);
        hideInputMenu();
    }

    /***
     * 显示底部View
     */
    private void showInputBottom() {
        bottom.setVisibility(View.VISIBLE);
    }

    /***
     * 隐藏输入框
     */
    private void hideInputMenu() {
        chatInputMenu.setVisibility(View.INVISIBLE);
        chatInputMenu.reset();
    }

    /***
     * 显示输入框
     */
    private void showInputMenu() {
        chatInputMenu.setVisibility(View.VISIBLE);
        chatInputMenu.etHasFocus();
    }

    /***
     * handle发送弹幕
     * @param message
     */
    private void sendHandleMessage(EMMessage message) {
        Message msg = Message.obtain(handler, MESSAGE_CODE, message);
        handler.sendMessage(msg);
    }

    /**
     * handle删除弹幕View
     *
     * @param msgId
     */
    private void sendHandleRemoveDanmaku(String msgId) {
        Message msg = Message.obtain(handler, REMOVE_DANMAKU, msgId);
        handler.sendMessage(msg);
    }

    /**
     * handle设置UI是否可以点击和修改文本提示
     * @param content
     * @param enable
     */
    private void sendHandleEnable(String content, Boolean enable) {
        Message msg = Message.obtain(handler, enable ? ENABLE_CODE : UN_ENABLE_CODE, content);
        handler.sendMessage(msg);
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
                        sendHandleMessage(message);
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
                        sendHandleRemoveDanmaku(msgId);
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
                    sendHandleEnable(context.getResources().getString(R.string.you_have_been_silenced), false);
                }
            }

            @Override
            public void onMuteListRemoved(String chatRoomId, List<String> mutes) {
                for (String member : mutes) {
                    sendHandleEnable(context.getResources().getString(R.string.send_danmaku), true);
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
                    if(isMuted){
                        sendHandleEnable(context.getResources().getString(R.string.total_silence), false);
                    } else {
                        sendHandleEnable(context.getResources().getString(R.string.send_danmaku), true);
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
    private void cancelEaseListener(){
        EMClient.getInstance().chatManager().removeMessageListener(messageListener);
        EMClient.getInstance().chatroomManager().removeChatRoomListener(chatRoomChangeListener);
    }

    /**
     * 创建用户
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
        sendHandleEnable(context.getResources().getString(R.string.in_the_login),false);
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
                if(loginLimit == 2){
                    Message msg = Message.obtain(handler, TOAST_CODE, context.getResources().getString(R.string.login_failed));
                    handler.sendMessage(msg);
                    sendHandleEnable(context.getResources().getString(R.string.login_failed),false);
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
        joinLimit ++;
        sendHandleEnable(context.getResources().getString(R.string.in_the_join),false);
        EMClient.getInstance().chatroomManager().joinChatRoom(chatRoomId, new EMValueCallBack<EMChatRoom>() {
            @Override
            public void onSuccess(EMChatRoom value) {
                EMLog.e("Login:", "join success");
                isAllMemberMuted();
            }

            @Override
            public void onError(int error, String errorMsg) {
                EMLog.e("Login:", "join  " + error + ":" + errorMsg);
                if(joinLimit == 2){
                    Message msg = Message.obtain(handler, TOAST_CODE, context.getResources().getString(R.string.join_failed));
                    handler.sendMessage(msg);
                    sendHandleEnable(context.getResources().getString(R.string.join_failed),false);
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

    /***
     * 发送文本消息
     * @param content
     */
    private void sendTextMessage(String content) {
        EMMessage message = EMMessage.createTxtSendMessage(content, chatRoomId);
        sendMessage(message);
    }

    /***
     * 发送礼物
     * @param gift
     */
    private void sendGiftMessage(Gift gift) {
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CUSTOM);
        EMCustomMessageBody body = new EMCustomMessageBody("gift");
        Map<String, String> params = new HashMap<>();
        params.put(DemoConstant.NUMBER, gift.getScore());
        params.put(DemoConstant.DES, gift.getDesc());
        params.put(DemoConstant.URL, gift.getImg());
        body.setParams(params);
        message.addBody(body);
        message.setTo(chatRoomId);
        message.setAttribute(DemoConstant.AVATAR_URL, avatarUrl);
        sendMessage(message);

    }

    /***
     * 发送消息api
     * @param message
     */
    private void sendMessage(EMMessage message) {
        message.setChatType(EMMessage.ChatType.ChatRoom);
        message.setAttribute(DemoConstant.ROOM_UUID, roomUuid);
        message.setAttribute(DemoConstant.MSG_TYPE, DemoConstant.MSG_TYPE_NORMAL);
        message.setAttribute(DemoConstant.ROLE, DemoConstant.ROLE_STUDENT);
        message.setAttribute(DemoConstant.NICK_NAME, nickName);
        message.setAttribute(DemoConstant.AVATAR_URL, avatarUrl);
        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                sendHandleMessage(message);
            }

            @Override
            public void onError(int code, String error) {
                if (code == EMError.MESSAGE_INCLUDE_ILLEGAL_CONTENT) {
                    Message msg = Message.obtain(handler, TOAST_CODE, context.getResources().getString(R.string.message_incloud_illegal_content));
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    @Override
    public void onGiftSend(Gift gift) {
        sendGiftMessage(gift);
    }

    @Override
    public void onCloseGiftView() {
        hideGiftView();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int id = view.getId();
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            if (id == R.id.danmaku_container) {
                if (chatInputMenu.getVisibility() == View.VISIBLE) {
                    hideInputMenu();
                }
            }
            return false;
        }
        return false;
    }

    /**
     * 禁言UI
     */
    private void unEnableBottomView() {
        mDanmakuSend.setEnabled(false);
        gift.setEnabled(false);
        hideInputMenu();
    }

    /**
     * 解除禁言UI
     */
    private void enableBottomView() {
        mDanmakuSend.setEnabled(true);
        gift.setEnabled(true);
    }

    /**
     * 判断聊天室是否是全员禁言状态
     */
    private void isAllMemberMuted() {
        EMClient.getInstance().chatroomManager().asyncFetchChatRoomFromServer(chatRoomId, new EMValueCallBack<EMChatRoom>() {
            @Override
            public void onSuccess(EMChatRoom value) {
                if(value.isAllMemberMuted()){
                    sendHandleEnable(context.getResources().getString(R.string.total_silence), false);
                } else {
                     sendHandleEnable(context.getResources().getString(R.string.send_danmaku), true);
                }
            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });
    }
}
