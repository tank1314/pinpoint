package com.navercorp.pinpoint.plugin.rocketmq.client.interceptor;

import com.baozun.scm.baseservice.message.common.MessageCommond;
import com.baozun.utilities.json.JsonUtil;
import com.baozun.utilities.type.StringUtil;
import com.navercorp.pinpoint.bootstrap.context.*;
import com.navercorp.pinpoint.bootstrap.interceptor.SpanSimpleAroundInterceptor;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.plugin.rocketmq.client.RocketMQConstants;
import com.navercorp.pinpoint.plugin.rocketmq.client.RocketMQConsumerEntryMethodDescriptor;
import com.navercorp.pinpoint.plugin.rocketmq.client.secret.AESGeneralUtil;
import com.navercorp.pinpoint.plugin.rocketmq.client.secret.MD5Util;
import org.apache.rocketmq.common.message.MessageDecoder;
import org.apache.rocketmq.common.message.MessageExt;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RocketMQMessageBZConsumeSendInterceptor extends SpanSimpleAroundInterceptor {



    private final PLogger logger = PLoggerFactory.getLogger(this.getClass());

    private Map<String, String> keyValueMap = new HashMap<String, String>();

    public RocketMQMessageBZConsumeSendInterceptor(TraceContext traceContext) {
        this(traceContext, new RocketMQConsumerEntryMethodDescriptor());
    }

    private RocketMQMessageBZConsumeSendInterceptor(TraceContext traceContext, MethodDescriptor methodDescriptor) {
        super(traceContext, methodDescriptor, RocketMQMessageBZConsumeSendInterceptor.class);
        traceContext.cacheApi(methodDescriptor);
    }

    @Override
    protected void doInBeforeTrace(SpanRecorder recorder, Object target, Object[] args) {

        recorder.recordServiceType(RocketMQConstants.ROCKETMQ_CLIENT);
        //List<MessageExt> msgs = (List<MessageExt>) args[0];
        MessageCommond msgCommond = (MessageCommond)args[3] ; //消息头

        try {
            // 消费者的本机名
            recorder.recordEndPoint(InetAddress.getLocalHost().getHostAddress());
            // 消费者的连接的主机名
            //InetSocketAddress remoteAddress=(InetSocketAddress) messageext.getStoreHost();
            recorder.recordRemoteAddress(msgCommond.getProduceRouteInfo());
            // 订阅的Topic
            recorder.recordRpcName(msgCommond.getTopic());

            // 消息存储的地址
            recorder.recordAcceptorHost(msgCommond.getTopic());
            String body = msgCommond.getMsgBody() ;
            //String decryptMsg = bodyData(msgCommond.getTopic(),body) ;
            recorder.recordAttribute(RocketMQConstants.ROCKETMQ_MESSAGE, body);
            //MessageCommond msgCommond2 = JsonUtil.readValue(body,MessageCommond.class) ;
            String headerStr = msgCommond.getConsumerRouteInfo();
            if(StringUtil.isNotEmpty(headerStr)){
                Map header = JsonUtil.readValue(headerStr,Map.class) ;
                String parentApplicationName = (String)header.get("Pinpoint-pAppName");
                short parentApplicationType=-1;
                if (!recorder.isRoot() && parentApplicationName != null) {
                    parentApplicationType = Short.parseShort((String)header.get("Pinpoint-pAppType"));
                    recorder.recordParentApplication(parentApplicationName, parentApplicationType);
                }
            }
        } catch (Exception e) {
            logger.error(e + "");
        }
    }

    @Override
    protected Trace createTrace(Object target, Object[] args) {
        /*List<MessageExt> msgs = (List<MessageExt>) args[0];
        MessageExt messageext = msgs.get(0);*/
        String topic= (String) args[0]; //业务逻辑bean处理
        String tags = (String)args[1] ; //业务处理方法
        MessageCommond msgCommond = (MessageCommond)args[3] ; //消息头
        if (null == msgCommond) {
            return traceContext.newTraceObject();
        }

        String headerStr = msgCommond.getConsumerRouteInfo() ;
        if(StringUtil.isNotEmpty(headerStr)){
            Map<String, String> header = JsonUtil.readValue(headerStr, Map.class);
            String transactionId = header.get("Pinpoint-TraceID");
            long spanId = -1;
            if (null != header.get("Pinpoint-SpanID")) {
                spanId = Long.parseLong(header.get("Pinpoint-SpanID"));
            }
            long parentSpanId = -1;
            if (null != header.get("Pinpoint-pSpanID")) {
                parentSpanId = Long.parseLong(header.get("Pinpoint-pSpanID"));
            }
            short flags = 0;
            if (null != header.get("Pinpoint-Flags")) {
                flags = Short.parseShort(header.get("Pinpoint-Flags"));
            }
            header.get("Pinpoint-pAppName");
            header.get("Pinpoint-pAppType");
            if (transactionId != null) {
                final TraceId traceId = traceContext.createTraceId(transactionId, parentSpanId, spanId, flags);
                return traceContext.continueTraceObject(traceId);
            }
            else {
                return traceContext.newTraceObject();
            }
        }
        else{
            return traceContext.newTraceObject();
        }
    }

    @Override
    protected void doInAfterTrace(SpanRecorder recorder, Object target, Object[] args, Object result, Throwable throwable) {
        recorder.recordApi(methodDescriptor);
        if (throwable != null) {
            recorder.recordException(throwable);
        }
    }


    protected String bodyData(String topic,String body) {
        String secretInfo = "";
        if (keyValueMap.containsKey(topic)) {
            secretInfo = keyValueMap.get(topic);
        }
        else {
            MD5Util md5Util = new MD5Util();
            secretInfo = md5Util.getMd5_16New(topic);
            keyValueMap.put(topic, secretInfo);
        }
        String decryptMsg = ""; //消息体
        try {
            decryptMsg = AESGeneralUtil.decrypt(body, secretInfo);
        } catch (Exception e) {
            decryptMsg = body;
        }
        return decryptMsg ;
    }

}
