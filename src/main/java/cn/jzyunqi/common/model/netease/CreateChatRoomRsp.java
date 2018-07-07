package cn.jzyunqi.common.model.netease;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author wiiyaya
 * @date 2018/6/2.
 */
@Getter
@Setter
public class CreateChatRoomRsp extends NeteaseBaseRsp {
    private static final long serialVersionUID = -5278138864950047414L;

    @JsonProperty("chatroom")
    private ChatRoom chatRoom;

    @Getter
    @Setter
    public static class ChatRoom {

        @JsonProperty("roomid")
        private Long roomId;
    }
}
