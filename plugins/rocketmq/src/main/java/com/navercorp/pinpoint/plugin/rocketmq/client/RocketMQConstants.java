package com.navercorp.pinpoint.plugin.rocketmq.client;

import static com.navercorp.pinpoint.common.trace.ServiceTypeProperty.QUEUE;
import static com.navercorp.pinpoint.common.trace.ServiceTypeProperty.RECORD_STATISTICS;

import com.navercorp.pinpoint.common.trace.AnnotationKey;
import com.navercorp.pinpoint.common.trace.AnnotationKeyFactory;
import com.navercorp.pinpoint.common.trace.AnnotationKeyProperty;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.common.trace.ServiceTypeFactory;

public interface RocketMQConstants {

    /**
     * rocketmq的服务类型
     */
    public static final ServiceType ROCKETMQ_CLIENT = ServiceTypeFactory.of(8321, "ROCKETMQ_CLIENT", QUEUE,
            RECORD_STATISTICS);


    public static final AnnotationKey ROCKETMQ_BROKER_URL = AnnotationKeyFactory.of(401, "rocketmq.broker.address",
            AnnotationKeyProperty.VIEW_IN_RECORD_SET);



    public static final ServiceType ROCKETMQ_CLIENT_INTERNAL = ServiceTypeFactory.of(9902, "ROCKETMQ_CLIENT_INTERNAL",
            "ROCKET_MQ_CLIENT");

    public static final AnnotationKey ROCKETMQ_MESSAGE = AnnotationKeyFactory.of(402, "rocketmq.message",
            AnnotationKeyProperty.VIEW_IN_RECORD_SET);

    public static final AnnotationKey ROCKETMQ_MSG_TOPIC = AnnotationKeyFactory.of(403, "rocketmq.msg.topic",
            AnnotationKeyProperty.VIEW_IN_RECORD_SET);

    public static final AnnotationKey ROCKETMQ_MSG_TAGS = AnnotationKeyFactory.of(404, "rocketmq.msg.tags",
            AnnotationKeyProperty.VIEW_IN_RECORD_SET);
    /**
     * rocketmq 基础包
     */
    String BASE_PACKAGE = "com.navercorp.pinpoint.plugin.rocketmq.client";

    /**
     * rocketmq 的生产者检测代码
     */
    //String LISTEN_PRODUCER = "com.alibaba.rocketmq.client.impl.MQClientAPIImpl";
    String LISTEN_PRODUCER = "org.apache.rocketmq.client.impl.MQClientAPIImpl";

    /**
     * rocketMQ 宝尊自定义
     */
    String LISTEN_BZ_PRODUCER = "com.baozun.scm.baseservice.message.rocketmq.service.server.RocketMQProducerServer" ;
    /**
     * rocketmq的生产者监听方法
     */
    String LISTEN_PRODUCER_METHOD = "sendMessage";

    /**
     * 无序发送
     */
    String LISTEN_PRODUCER_BZ_CCMETHOD = "sendDataMsgConcurrently" ;

    /**
     * hub队列拆分
     */
    String LISTEN_PRODUCER_BZ_HUBMETHOD = "hubSplitSendDataMsgConcurrently" ;


    // rocketmq的生产者切面基础包
    String AOP_LISTEN_PRODUCER_PACKAGE = BASE_PACKAGE + ".interceptor";

    /**
     * rocketmq的生产者切面实现
     */
    String AOP_LISTEN_PRODUCER_METHOD = AOP_LISTEN_PRODUCER_PACKAGE + ".RocketMQMessageProducerSendInterceptor";

    String AOP_LISTEN_PRODUCER_BZ_METHOD = AOP_LISTEN_PRODUCER_PACKAGE + ".RocketMQMessageBZProducerSendInterceptor";

    /**
     * rocketmq的管道
     */
    //String LISTEN_CHANNEL = "com.alibaba.rocketmq.remoting.netty.NettyRemotingClient";
    String LISTEN_CHANNEL = "org.apache.rocketmq.remoting.netty.NettyRemotingClient";

    String AOP_LISTEN_CHANNEL_METHOD = AOP_LISTEN_PRODUCER_PACKAGE + ".RocketMQChannelInterceptor";

    /**
     * rocketmq的消费者检测代码
     */
    //String LISTEN_CONSUMER_ORDERLYSERVICE = "com.alibaba.rocketmq.client.impl.consumer.ConsumeMessageOrderlyService";

    //String LISTEN_CONSUMER_ConcurrentlyService = "com.alibaba.rocketmq.client.impl.consumer.ConsumeMessageConcurrentlyService";

    String LISTEN_CONSUMER_ORDERLYSERVICE = "org.apache.rocketmq.client.impl.consumer.ConsumeMessageOrderlyService";

    String LISTEN_CONSUMER_ConcurrentlyService = "org.apache.rocketmq.client.impl.consumer.ConsumeMessageConcurrentlyService";

    String AOP_LISTEN_CONSUMER_METHOD = AOP_LISTEN_PRODUCER_PACKAGE + ".RocketMQMessageConsumeSendInterceptor";


    String LISTEN_CONSUMER_BZ_CONCURRENTLYSERVICE = "com.baozun.scm.baseservice.message.rocketmq.service.handle.MessageHandler";

    String AOP_LISTEN_CONSUMER_BZMETHOD = AOP_LISTEN_PRODUCER_PACKAGE + ".RocketMQMessageBZConsumeSendInterceptor";
}