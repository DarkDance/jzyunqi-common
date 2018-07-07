package cn.jzyunqi.common.model.ali.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author wiiyaya
 * @date 2018/5/22.
 */
@Getter
@Setter
public class SendSmsRsp extends AliYunBaseRsp {
    private static final long serialVersionUID = 9099950577474852485L;

    /**
     * 发送回执ID,可根据该ID查询具体的发送状态
     */
    @JsonProperty("BizId")
    private String bizId;
}
