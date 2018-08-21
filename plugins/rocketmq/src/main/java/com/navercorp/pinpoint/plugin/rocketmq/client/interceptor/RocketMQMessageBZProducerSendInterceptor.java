package com.navercorp.pinpoint.plugin.rocketmq.client.interceptor;


import com.alibaba.fastjson.JSON;
import com.baozun.scm.baseservice.message.common.MessageCommond;
import com.baozun.utilities.json.JsonUtil;
import com.navercorp.pinpoint.bootstrap.context.*;
import com.navercorp.pinpoint.bootstrap.interceptor.AroundInterceptor;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.common.trace.AnnotationKey;
import com.navercorp.pinpoint.plugin.rocketmq.client.RocketMQConstants;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageDecoder;
import org.apache.rocketmq.common.protocol.header.SendMessageRequestHeader;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;


public class RocketMQMessageBZProducerSendInterceptor implements AroundInterceptor {

    private final PLogger logger = PLoggerFactory.getLogger(this.getClass());
    private final boolean isDebug = logger.isDebugEnabled();

    private final TraceContext traceContext;
    private final MethodDescriptor descriptor;

    public RocketMQMessageBZProducerSendInterceptor(TraceContext traceContext, MethodDescriptor descriptor) {
        this.traceContext = traceContext;
        this.descriptor = descriptor;
    }

    @Override
    public void before(Object target, Object[] args) {
        if (isDebug) {
            logger.beforeInterceptor(target, args);
        }
        Trace trace = traceContext.currentRawTraceObject();
        if (trace == null) {
            trace = traceContext.newTraceObject();
        }
        String topic = (String)args[0] ;
        MessageCommond msg = null ;
        try{
            msg = (MessageCommond) args[1];
        }
        catch (Exception e){
            msg = (MessageCommond) args[2];
        }
        Map<String, String> header = new HashMap<String, String>() ;
        try {
            if (trace.canSampled()) {
                SpanEventRecorder recorder = trace.traceBlockBegin();
                recorder.recordServiceType(RocketMQConstants.ROCKETMQ_CLIENT);
                TraceId nextId = trace.getTraceId().getNextTraceId();
                recorder.recordNextSpanId(nextId.getSpanId());

                header.put("Pinpoint-TraceID", nextId.getTransactionId());
                header.put("Pinpoint-SpanID", nextId.getSpanId() + "");
                header.put("Pinpoint-pSpanID", nextId.getParentSpanId() + "");
                header.put("Pinpoint-Flags", nextId.getFlags() + "");
                header.put("Pinpoint-pAppName", traceContext.getApplicationName());
                header.put("Pinpoint-pAppType", traceContext.getServerTypeCode() + "");
            } else {
                header.put("Pinpoint-Sampled", 0+"");
            }
            if(null !=msg){
                msg.setConsumerRouteInfo(JsonUtil.writeValue(header));
            }
        } catch (Throwable t) {
            logger.warn("BEFORE. Cause:{}", t);
        }
    }

    @Override
    public void after(Object target, Object[] args, Object result, Throwable throwable) {
        if (isDebug) {
            logger.afterInterceptor(target, args);
        }
        Trace trace = traceContext.currentTraceObject();
        if (trace == null) {
            return;
        }
        String topic = "UNKNOW";
        String tags = "*";
        MessageCommond message = null ;
        if (null != args[0]) {
            topic = (String) args[0];
        }
        if (null != args[1]) {
            try{
                tags = (String) args[1];
            }
            catch (Exception e){
                message = (MessageCommond) args[1];
            }
        }
        if(null == message){
            message = (MessageCommond) args[2];
        }
        try {
            SpanEventRecorder recorder = trace.currentSpanEventRecorder();
            recorder.recordApi(descriptor);
            if (throwable == null) {
                recorder.recordAttribute(AnnotationKey.MESSAGE_QUEUE_URI,topic);
                /*recorder.recordAttribute(RocketMQConstants.ROCKETMQ_MSG_TOPIC, topic);
                recorder.recordAttribute(RocketMQConstants.ROCKETMQ_MSG_TAGS, tags);*/
                // This annotation indicates the uri to which the call is made
                recorder.recordAttribute(RocketMQConstants.ROCKETMQ_MESSAGE, message.toString());
                //SendMessageRequestHeader requestHeader = (SendMessageRequestHeader) args[3];
                // DestinationId is used to render the virtual queue node.
                recorder.recordEndPoint(InetAddress.getLocalHost().getHostAddress());
                recorder.recordDestinationId(topic);
            } else {
                recorder.recordException(throwable);
            }
        } catch (Throwable t) {
            logger.warn("AFTER error. Cause:{}", t.getMessage(), t);
        } finally {
            trace.traceBlockEnd();
        }
    }
}
