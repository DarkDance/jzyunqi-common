package cn.jzyunqi.common.helper;

import cn.jzyunqi.common.exception.BusinessException;
import cn.jzyunqi.common.model.netease.CreateChatRoomRsp;
import cn.jzyunqi.common.model.netease.CreateUserRsp;
import cn.jzyunqi.common.model.netease.NeteaseBaseRsp;
import cn.jzyunqi.common.model.netease.enums.MsgType;
import cn.jzyunqi.common.model.netease.enums.RoleOpt;
import cn.jzyunqi.common.utils.BooleanUtilPlus;
import cn.jzyunqi.common.utils.DigestUtilPlus;
import cn.jzyunqi.common.utils.RandomStringUtilPlus;
import cn.jzyunqi.common.utils.StringUtilPlus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.List;

/**
 * @author wiiyaya
 * @date 2018/6/1.
 */
@Slf4j
public class NeteaseImHelper {
    /**
     * 创建用户
     */
    private static final String NETEASE_IM_USER_CREATE = "https://api.netease.im/nimserver/user/create.action";

    /**
     * 创建聊天室
     */
    private static final String NETEASE_IM_CHATROOM_CREATE = "https://api.netease.im/nimserver/chatroom/create.action";

    /**
     * 修改聊天室开/关闭状态
     */
    private static final String NETEASE_IM_CHATROOM_TOGGLE_CLOSE = "https://api.netease.im/nimserver/chatroom/toggleCloseStat.action";

    /**
     * 设置聊天室内用户角色
     */
    private static final String NETEASE_IM_CHATROOM_SET_MEMBER_ROLE = "https://api.netease.im/nimserver/chatroom/setMemberRole.action";

    /**
     * 更新用户信息
     */
    private static final String NETEASE_IM_UPDATE_USER_INFO = "https://api.netease.im/nimserver/user/updateUinfo.action";

    /**
     * 发送聊天室消息
     */
    private static final String NETEASE_IM_CHATROOM_SEND_MSG = "https://api.netease.im/nimserver/chatroom/sendMsg.action";

    /**
     * 更新聊天室用户信息
     */
    private static final String NETEASE_IM_CHATROOM_UPDATE_CHAT_ROOM_ROLE = "https://api.netease.im/nimserver/chatroom/updateMyRoomRole.action";

    /**
     * im账号
     */
    private String appKey;

    /**
     * im账号密码
     */
    private String appSecret;

    private RestTemplate restTemplate;

    private ObjectMapper objectMapper;

    public NeteaseImHelper(String appKey, String appSecret, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 创建IM用户
     *
     * @param username 用户名
     * @param password 密码
     * @param nickname 昵称
     * @param avatar   头像
     */
    public void createUser(String username, String password, String nickname, String avatar) throws BusinessException {
        CreateUserRsp body;
        try {
            URI createUserUri = new URIBuilder(NETEASE_IM_USER_CREATE)
                    .addParameter("accid", username) //用户名
                    .addParameter("token", password) //密码
                    .addParameter("name", nickname) //昵称
                    .addParameter("icon", avatar) //头像
                    .build();

            RequestEntity requestEntity = new RequestEntity(this.getNeteaseHeader(), HttpMethod.POST, createUserUri);
            ResponseEntity<CreateUserRsp> createUserRsp = restTemplate.exchange(requestEntity, CreateUserRsp.class);
            body = createUserRsp.getBody();
        } catch (Exception e) {
            log.error("NeteaseImHelper createUser [{}] other error:", username, e);
            throw new BusinessException("common_error_ne_im_create_user_error");
        }

        if (body == null || !StringUtilPlus.equals(body.getCode(), "200")) {
            if (body == null) {
                body = new CreateUserRsp();
            }
            log.error("NeteaseImHelper createUser [{}] 200 error, code[{}][{}]:", username, body.getCode(), body.getDesc());
            throw new BusinessException("common_error_ne_im_create_user_failed");
        }
    }

    /**
     * 创建聊天室
     *
     * @param creator      创建者用户名
     * @param chatRoomName 聊天室名称
     * @return 聊天室id
     */
    public Long createChatRoom(String creator, String chatRoomName) throws BusinessException {
        CreateChatRoomRsp body;
        try {
            URI createUserUri = new URIBuilder(NETEASE_IM_CHATROOM_CREATE)
                    .addParameter("creator", creator) //聊天室所有者
                    .addParameter("name", StringUtilPlus.substring(chatRoomName, 0, 127)) //聊天室名称，长度限制128个字符
                    //.addParameter("announcement", "聊天室公告，不知道什么时候出来")
                    .addParameter("queuelevel", "1")//队列管理权限：0:所有人都有权限变更队列，1:只有主播管理员才能操作变更。默认0
                    .build();

            RequestEntity requestEntity = new RequestEntity(getNeteaseHeader(), HttpMethod.POST, createUserUri);
            ResponseEntity<CreateChatRoomRsp> createUserRsp = restTemplate.exchange(requestEntity, CreateChatRoomRsp.class);
            body = createUserRsp.getBody();
        } catch (Exception e) {
            log.error("NeteaseImHelper createChatRoom [{}] other error:", creator, e);
            throw new BusinessException("common_error_ne_im_create_chat_room_error");
        }

        if (body != null && StringUtilPlus.equals(body.getCode(), "200")) {
            return body.getChatRoom().getRoomId();
        } else {
            if (body == null) {
                body = new CreateChatRoomRsp();
            }
            log.error("NeteaseImHelper createChatRoom [{}] error, code[{}][{}]:", creator, body.getCode(), body.getDesc());
            throw new BusinessException("common_error_ne_im_create_chat_room_failed");
        }
    }

    /**
     * 关闭聊天室
     *
     * @param creator    聊天室所有者
     * @param chatRoomId 聊天室编号
     */
    public void closeChatRoom(String creator, String chatRoomId) throws BusinessException {
        NeteaseBaseRsp body;
        try {
            URI createUserUri = new URIBuilder(NETEASE_IM_CHATROOM_TOGGLE_CLOSE)
                    .addParameter("operator", creator) //聊天室所有者
                    .addParameter("roomid", chatRoomId) //聊天室id
                    .addParameter("valid", "false") //true或false，false:关闭聊天室；true:打开聊天室
                    .build();

            RequestEntity requestEntity = new RequestEntity(getNeteaseHeader(), HttpMethod.POST, createUserUri);
            ResponseEntity<NeteaseBaseRsp> createUserRsp = restTemplate.exchange(requestEntity, NeteaseBaseRsp.class);
            body = createUserRsp.getBody();
        } catch (Exception e) {
            log.error("NeteaseImHelper closeChatRoom [{}] other error:", creator, e);
            throw new BusinessException("common_error_ne_im_toggle_chat_room_close_error");
        }

        if (body == null || !StringUtilPlus.equals(body.getCode(), "200")) {
            if (body == null) {
                body = new NeteaseBaseRsp();
            }
            log.error("NeteaseImHelper closeChatRoom [{}] 200 error, code[{}][{}]:", creator, body.getCode(), body.getDesc());
            throw new BusinessException("common_error_ne_im_toggle_chat_room_close_failed");
        }
    }

    /**
     * 设置聊天室内用户角色
     *
     * @param chatRoomId 聊天室id
     * @param operator   操作者用户名
     * @param target     操作对象用户名
     * @param opt        操作类型
     * @param cancel     是否取消
     */
    public void setMemberRole(String chatRoomId, String operator, String target, RoleOpt opt, Boolean cancel) throws BusinessException {
        NeteaseBaseRsp body;
        try {
            URI createUserUri = new URIBuilder(NETEASE_IM_CHATROOM_SET_MEMBER_ROLE)
                    .addParameter("roomid", chatRoomId) //聊天室id
                    .addParameter("operator", operator) //操作者账号accid
                    .addParameter("target", target) //被操作者账号accid
                    .addParameter("opt", opt.getOpt().toString()) //操作
                    .addParameter("optvalue", BooleanUtilPlus.toStringTrueFalse(!cancel)) //true或false，true:设置；false:取消设置
                    .build();

            RequestEntity requestEntity = new RequestEntity(getNeteaseHeader(), HttpMethod.POST, createUserUri);
            ResponseEntity<NeteaseBaseRsp> createUserRsp = restTemplate.exchange(requestEntity, NeteaseBaseRsp.class);
            body = createUserRsp.getBody();
        } catch (Exception e) {
            log.error("NeteaseImHelper setMemberRole [{}][{}] other error:", chatRoomId, target, e);
            throw new BusinessException("common_error_ne_im_set_member_role_error");
        }

        if (body == null || !StringUtilPlus.equals(body.getCode(), "200")) {
            if (body == null) {
                body = new NeteaseBaseRsp();
            }
            log.error("NeteaseImHelper setMemberRole [{}][{}] 200 error, code[{}][{}]:", chatRoomId, target, body.getCode(), body.getDesc());
            throw new BusinessException("common_error_ne_im_set_member_role_failed");
        }
    }

    /**
     * 更新用户信息
     *
     * @param username 用户名
     * @param nickname 昵称
     * @param avatar   头像
     */
    public void updateUser(String username, String nickname, String avatar) throws BusinessException {
        NeteaseBaseRsp body;
        try {
            URI createUserUri = new URIBuilder(NETEASE_IM_UPDATE_USER_INFO)
                    .addParameter("accid", username) //) //用户名
                    .addParameter("name", nickname) //昵称
                    .addParameter("icon", avatar) //头像
                    .build();

            RequestEntity requestEntity = new RequestEntity(getNeteaseHeader(), HttpMethod.POST, createUserUri);
            ResponseEntity<NeteaseBaseRsp> createUserRsp = restTemplate.exchange(requestEntity, NeteaseBaseRsp.class);
            body = createUserRsp.getBody();
        } catch (Exception e) {
            log.error("NeteaseImHelper updateUser [{}] other error:", username, e);
            throw new BusinessException("common_error_ne_im_update_user_info_error");
        }

        if (body == null || !StringUtilPlus.equals(body.getCode(), "200")) {
            if (body == null) {
                body = new NeteaseBaseRsp();
            }
            log.error("NeteaseImHelper updateUser [{}] 200 error, code[{}][{}]:", username, body.getCode(), body.getDesc());
            throw new BusinessException("common_error_ne_im_update_user_info_failed");
        }
    }

    /**
     * 向聊天室发送消息
     *
     * @param chatRoomId   聊天室id
     * @param fromUsername 发送人名称
     * @param msgType      消息类型
     * @param msg          消息对象
     * @param ext          扩展消息
     */
    public void sendChatRoomMessage(String chatRoomId, String fromUsername, MsgType msgType, String msg, Object ext) throws BusinessException {
        NeteaseBaseRsp body;
        try {
            String msgId = RandomStringUtilPlus.random(30, true, true);
            URIBuilder sendMsgUri = new URIBuilder(NETEASE_IM_CHATROOM_SEND_MSG)
                    .addParameter("roomid", chatRoomId) //聊天室id
                    .addParameter("msgId", msgId) //客户端消息id，使用uuid等随机串，msgId相同的消息会被客户端去重
                    .addParameter("fromAccid", fromUsername) //消息发出者的账号accid
                    .addParameter("msgType", msgType.getType().toString()) //消息类型
                    .addParameter("attach", msg); //消息内容

            if (ext != null) {
                sendMsgUri.addParameter("ext", objectMapper.writeValueAsString(ext)); //扩展消息
            }

            RequestEntity requestEntity = new RequestEntity(getNeteaseHeader(), HttpMethod.POST, sendMsgUri.build());
            ResponseEntity<NeteaseBaseRsp> createUserRsp = restTemplate.exchange(requestEntity, NeteaseBaseRsp.class);
            body = createUserRsp.getBody();
        } catch (Exception e) {
            log.error("NeteaseImHelper sendChatRoomMessage [{}] other error:", chatRoomId, e);
            throw new BusinessException("common_error_ne_im_send_chat_room_msg_error");
        }

        if (body == null || !StringUtilPlus.equals(body.getCode(), "200")) {
            if (body == null) {
                body = new NeteaseBaseRsp();
            }
            log.error("NeteaseImHelper sendChatRoomMessage [{}] 200 error, code[{}][{}]:", chatRoomId, body.getCode(), body.getDesc());
            throw new BusinessException("common_error_ne_im_send_chat_room_msg_failed");
        }
    }

    /**
     * 更新聊天室用户信息
     *
     * @param chatRoomId 聊天室id
     * @param username   用户名
     * @param nickname   昵称
     * @param avatar     头像
     */
    public void updateChatRoomRole(String chatRoomId, String username, String nickname, String avatar) throws BusinessException {
        NeteaseBaseRsp body;
        try {
            URI createUserUri = new URIBuilder(NETEASE_IM_CHATROOM_UPDATE_CHAT_ROOM_ROLE)
                    .addParameter("roomid", chatRoomId)
                    .addParameter("accid", username)
                    .addParameter("nick", nickname)
                    .addParameter("avator", avatar)
                    .build();

            RequestEntity requestEntity = new RequestEntity(getNeteaseHeader(), HttpMethod.POST, createUserUri);
            ResponseEntity<NeteaseBaseRsp> createUserRsp = restTemplate.exchange(requestEntity, NeteaseBaseRsp.class);
            body = createUserRsp.getBody();
        } catch (Exception e) {
            log.error("NeteaseImHelper updateChatRoomRole [{}][{}] other error:", chatRoomId, username, e);
            throw new BusinessException("common_error_ne_im_update_chat_room_role_error");
        }

        if (body == null || !"200".equals(body.getCode())) {
            if (body == null) {
                body = new NeteaseBaseRsp();
            }
            log.error("NeteaseImHelper updateChatRoomRole [{}][{}] 200 error, code[{}][{}]:", chatRoomId, username, body.getCode(), body.getDesc());
            throw new BusinessException("common_error_ne_im_update_chat_room_role_failed");
        }
    }

    /**
     * 组装请求Header
     *
     * @return HttpHeaders
     */
    private HttpHeaders getNeteaseHeader() {
        HttpHeaders neteaseHeader = new HttpHeaders();

        String nonceStr = RandomStringUtilPlus.random(32, true, true);
        String curTime = String.valueOf(System.currentTimeMillis() / 1000);
        String checkSum = DigestUtilPlus.SHA1.sign(appSecret + nonceStr + curTime);

        // 设置请求的header
        neteaseHeader.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        neteaseHeader.set("AppKey", appKey); //im账号appKey
        neteaseHeader.set("Nonce", nonceStr); //随机数（最大长度128个字符）
        neteaseHeader.set("CurTime", curTime); //当前UTC时间戳，从1970年1月1日0点0 分0 秒开始到现在的秒数(String)
        neteaseHeader.set("CheckSum", checkSum); //SHA1(AppSecret + Nonce + CurTime),三个参数拼接的字符串，进行SHA1哈希计算，转化成16进制字符(String，小写)
        return neteaseHeader;
    }

    public void queryHist(String chatRoomId, String fromUsername) throws BusinessException {
        try {
            URI sendMsgUri = new URIBuilder("https://api.netease.im/nimserver/history/queryChatroomMsg.action")
                    .addParameter("roomid", chatRoomId)
                    .addParameter("accid", fromUsername)
                    .addParameter("timetag", String.valueOf(System.currentTimeMillis()))
                    .addParameter("limit", "10")
                    .addParameter("type", "0")
                    .build();

            RequestEntity requestEntity = new RequestEntity(getNeteaseHeader(), HttpMethod.POST, sendMsgUri);
            ResponseEntity<Object> createUserRsp = restTemplate.exchange(requestEntity, Object.class);
            createUserRsp.getBody();
        } catch (Exception e) {
            log.error("NeteaseImHelper queryHist [{}] other error:", chatRoomId, e);
            throw new BusinessException("common_error_ne_im_query_hist_error");
        }
    }

    public void getUinfos(List<String> fromUsernames) throws BusinessException {
        try {
            URI sendMsgUri = new URIBuilder("https://api.netease.im/nimserver/user/getUinfos.action")
                    .addParameter("accids", objectMapper.writeValueAsString(fromUsernames))
                    .build();

            RequestEntity requestEntity = new RequestEntity(getNeteaseHeader(), HttpMethod.POST, sendMsgUri);
            ResponseEntity<Object> createUserRsp = restTemplate.exchange(requestEntity, Object.class);
            createUserRsp.getBody();
        } catch (Exception e) {
            log.error("NeteaseImHelper getUinfos [{}] other error:", fromUsernames, e);
            throw new BusinessException("common_error_ne_im_get_uinfos_error");
        }
    }

    public void membersByPage(String chatRoomId) throws BusinessException {
        try {
            URI sendMsgUri = new URIBuilder("https://api.netease.im/nimserver/chatroom/membersByPage.action")
                    .addParameter("roomid", chatRoomId)
                    .addParameter("type", "0")
                    .addParameter("endtime", "0")
                    .addParameter("limit", "100")
                    .build();

            RequestEntity requestEntity = new RequestEntity(getNeteaseHeader(), HttpMethod.POST, sendMsgUri);
            ResponseEntity<Object> createUserRsp = restTemplate.exchange(requestEntity, Object.class);
            createUserRsp.getBody();
        } catch (Exception e) {
            log.error("NeteaseImHelper getUinfos [{}] other error:", chatRoomId, e);
            throw new BusinessException("common_error_ne_im_members_by_page_error");
        }
    }
}
