package cn.jzyunqi.common.helper;

import cn.jzyunqi.common.model.ali.enums.LiveProtocol;
import cn.jzyunqi.common.model.ali.enums.LiveType;
import cn.jzyunqi.common.utils.DigestUtilPlus;
import cn.jzyunqi.common.utils.StringUtilPlus;

import java.net.URLDecoder;

/**
 * @author wiiyaya
 * @date 2018/5/31.
 */
public class AliLiveHelper {

    private String liveAppName;

    private String livePrivateKey;

    private long liveAuthValidTime;

    private String livePushAddress;

    private String livePullAddressRtmp;

    private String livePullAddressFlv;

    private String livePullAddressM3u8;

    public AliLiveHelper(String liveAppName, String livePrivateKey, long liveAuthValidTime, String livePullDomain) {
        this.liveAppName = liveAppName;
        this.livePrivateKey = livePrivateKey;
        this.liveAuthValidTime = liveAuthValidTime;

        this.livePushAddress = StringUtilPlus.join("rtmp://video-center.alivecdn.com/", liveAppName, "/%s?vhost=", livePullDomain, "&auth_key=%s");
        this.livePullAddressRtmp = StringUtilPlus.join("rtmp://", livePullDomain, "/", liveAppName, "/%s?auth_key=%s");
        this.livePullAddressFlv = StringUtilPlus.join("http://", livePullDomain, "/", liveAppName, "/%s?auth_key=%s");
        this.livePullAddressM3u8 = StringUtilPlus.join("http://", livePullDomain, "/", liveAppName, "/%s?auth_key=%s");
    }

    public String getLivePushAddress(String streamName) {
        return String.format(livePushAddress, streamName, getLiveAuthKey(streamName));
    }

    public String getLivePullAddress(String streamName, LiveProtocol protocol, LiveType liveType) {
        if (protocol != LiveProtocol.m3u8 && liveType != null) {
            streamName = StringUtilPlus.join(streamName, "_", liveType);
        }
        streamName = StringUtilPlus.join(streamName, protocol.getSuffix());

        switch (protocol) {
            case rtmp:
                return String.format(livePullAddressRtmp, streamName, getLiveAuthKey(streamName));
            case flv:
                return String.format(livePullAddressFlv, streamName, getLiveAuthKey(streamName));
            case m3u8:
                return String.format(livePullAddressM3u8, streamName, getLiveAuthKey(streamName));
            default:
                return String.format(livePullAddressRtmp, streamName, getLiveAuthKey(streamName));
        }
    }

    /**
     * 获取直播授权
     *
     * @param streamName 流名称
     * @return 授权key
     */
    private String getLiveAuthKey(String streamName) {
        long timestamp = System.currentTimeMillis() / 1000 + liveAuthValidTime;
        String authStr = "/" + liveAppName + "/" + streamName + "-" + timestamp + "-0-0-" + livePrivateKey;
        return timestamp + "-0-0-" + DigestUtilPlus.MD5.sign(authStr);
    }

    /**
     * 校验回调
     *
     * @param requestParams 请求参数
     * @return true 合法回调
     */
    public boolean verifyLiveCallback(String streamName, String requestParams) {
        try {
            String params = URLDecoder.decode(requestParams, "utf-8");
            String authString = StringUtilPlus.substringBetween(params, "auth_key=", "&");

            long timestamp = Long.parseLong(StringUtilPlus.split(authString, "-")[0]);
            String authStr = "/" + liveAppName + "/" + streamName + "-" + timestamp + "-0-0-" + livePrivateKey;
            String checkString = timestamp + "-0-0-" + DigestUtilPlus.MD5.sign(authStr);
            return checkString.equals(authString);
        } catch (Exception e) {
            return false;
        }
    }
}
