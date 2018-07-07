package cn.jzyunqi.common.helper;

import cn.jzyunqi.common.exception.BusinessException;
import cn.jzyunqi.common.model.express.ExpressTraceResult;
import cn.jzyunqi.common.model.express.enums.ExpressStatus;
import cn.jzyunqi.common.model.express.enums.ExpressType;
import cn.jzyunqi.common.model.express.response.KdniaoETResult;
import cn.jzyunqi.common.utils.CollectionUtilPlus;
import cn.jzyunqi.common.utils.DigestUtilPlus;
import cn.jzyunqi.common.utils.StringUtilPlus;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wiiyaya
 * @date 2018/6/5.
 */
@Slf4j
public class ExpressHelper {

    private String appId;

    private String appSecret;

    private ExpressType type;

    private RestTemplate restTemplate;

    public ExpressHelper(String appId, String appSecret, ExpressType type, RestTemplate restTemplate) {
        this.appId = appId;
        this.appSecret = appSecret;
        this.type = type;
        this.restTemplate = restTemplate;
    }

    public ExpressTraceResult getExpressTraces(String expressCode, String trackingNum) throws BusinessException {
        if (type == ExpressType.KDNIAO) {
            return getKdniaoExpressTraces(expressCode, trackingNum);
        } else {
            return new ExpressTraceResult();
        }
    }

    private ExpressTraceResult getKdniaoExpressTraces(String expressCode, String trackingNum) throws BusinessException {
        KdniaoETResult kdniaoETResult;
        try {
            String requestData = "{'OrderCode':'','ShipperCode':'" + expressCode + "','LogisticCode':'" + trackingNum + "'}";
            String dataSign = DigestUtilPlus.Base64.encodeBase64String(DigestUtilPlus.MD5.sign(requestData + appSecret).getBytes("UTF-8"));

            URI orderTracesUri = new URIBuilder("http://api.kdniao.cc/Ebusiness/EbusinessOrderHandle.aspx")
                    .addParameter("RequestData", URLEncoder.encode(requestData, "UTF-8"))
                    .addParameter("EBusinessID", appId)
                    .addParameter("RequestType", "1002")
                    .addParameter("DataSign", URLEncoder.encode(dataSign, "UTF-8"))
                    .addParameter("DataType", "2")
                    .build();


            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            RequestEntity<String> requestEntity = new RequestEntity<>(header, HttpMethod.POST, orderTracesUri);
            ResponseEntity<KdniaoETResult> groupRst = restTemplate.exchange(requestEntity, KdniaoETResult.class);

            kdniaoETResult = groupRst.getBody();
        } catch (Exception e) {
            log.error("ExpressHelper.getKdniaoExpressTraces other error", e);
            return new ExpressTraceResult();
        }

        if (kdniaoETResult != null && kdniaoETResult.getSuccess()) {
            //组装返回信息
            ExpressTraceResult expressTraceDto = new ExpressTraceResult();
            expressTraceDto.setTrackingNo(kdniaoETResult.getLogisticCode());
            expressTraceDto.setStatus(getExpressStatus(kdniaoETResult.getState()));

            List<ExpressTraceResult.TraceDto> traceDtoList = new ArrayList<>();
            if (CollectionUtilPlus.isNotEmpty(kdniaoETResult.getTraces())) {
                for (KdniaoETResult.ExpressTraceModel expressTraceModel : kdniaoETResult.getTraces()) {
                    ExpressTraceResult.TraceDto traceDto = new ExpressTraceResult.TraceDto();
                    traceDto.setAcceptTime(expressTraceModel.getAcceptTime());
                    traceDto.setAcceptStation(expressTraceModel.getAcceptStation());
                    traceDto.setRemark(expressTraceModel.getRemark());

                    traceDtoList.add(0, traceDto);//倒序加入
                }
            }
            expressTraceDto.setTraceList(traceDtoList);

            return expressTraceDto;
        } else {
            if (kdniaoETResult == null) {
                kdniaoETResult = new KdniaoETResult();
            }
            log.error("ExpressHelper.getKdniaoExpressTraces 200 error[{}]", kdniaoETResult.getReason());
            return new ExpressTraceResult();
        }
    }


    private ExpressStatus getExpressStatus(String state) {
        if (StringUtilPlus.isNotEmpty(state)) {
            switch (state) {
                case "0":
                    return ExpressStatus.N;
                case "2":
                    return ExpressStatus.O;
                case "3":
                    return ExpressStatus.S;
                case "4":
                    return ExpressStatus.P;
                default:
                    break;
            }
        }
        return ExpressStatus.P;
    }
}
