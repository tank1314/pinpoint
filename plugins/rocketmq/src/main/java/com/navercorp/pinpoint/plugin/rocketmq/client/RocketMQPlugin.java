package com.navercorp.pinpoint.plugin.rocketmq.client;

import java.security.ProtectionDomain;

import com.navercorp.pinpoint.bootstrap.instrument.InstrumentClass;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentException;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentMethod;
import com.navercorp.pinpoint.bootstrap.instrument.Instrumentor;
import com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformCallback;
import com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformTemplate;
import com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformTemplateAware;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.bootstrap.plugin.ProfilerPlugin;
import com.navercorp.pinpoint.bootstrap.plugin.ProfilerPluginSetupContext;
import com.navercorp.pinpoint.bootstrap.plugin.util.InstrumentUtils;

public class RocketMQPlugin implements ProfilerPlugin, TransformTemplateAware {

    private TransformTemplate transformTemplate;

    private final PLogger logger = PLoggerFactory.getLogger(this.getClass());

    /**
     * 监听入口
     * @param context
     */
    @Override
    public void setup(ProfilerPluginSetupContext context) {
        //this.addProducerEditor();
        //this.addChannelEditor();
        //this.addConsumerEditor();
        this.addBZProducerEditor() ;
        this.addBzConsumerEditor() ;
    }

    @Override
    public void setTransformTemplate(TransformTemplate transformTemplate) {
        this.transformTemplate = transformTemplate;
    }

    /**
     * 默认发送
     */
    private void addProducerEditor() {
        transformTemplate.transform(RocketMQConstants.LISTEN_PRODUCER, new TransformCallback() {
            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader loader, String className,
                                        Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer)
                    throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(loader, className, classfileBuffer);
                for (InstrumentMethod instrumentMethod : target.getDeclaredMethods()) {
                    if (RocketMQConstants.LISTEN_PRODUCER_METHOD.equals(instrumentMethod.getName())) {
                        instrumentMethod.addInterceptor(RocketMQConstants.AOP_LISTEN_PRODUCER_METHOD);
                    }
                }
                return target.toBytecode();
            }
        });
    }

    /**
     * 宝尊底层实现
     */
    private void addBZProducerEditor() {
        transformTemplate.transform(RocketMQConstants.LISTEN_BZ_PRODUCER, new TransformCallback() {
            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader loader, String className,
                                        Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer)
                    throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(loader, className, classfileBuffer);

                for (InstrumentMethod instrumentMethod : target.getDeclaredMethods()) {
                    if (RocketMQConstants.LISTEN_PRODUCER_BZ_CCMETHOD.equals(instrumentMethod.getName()) || RocketMQConstants.LISTEN_PRODUCER_BZ_HUBMETHOD.equals(instrumentMethod.getName())) {
                        instrumentMethod.addInterceptor(RocketMQConstants.AOP_LISTEN_PRODUCER_BZ_METHOD);
                    }
                }
                return target.toBytecode();
            }
        });
    }


    private void addChannelEditor() {
        transformTemplate.transform(RocketMQConstants.LISTEN_CHANNEL, new TransformCallback() {
            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader loader, String className,
                                        Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer)
                    throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(loader, className, classfileBuffer);

                for (InstrumentMethod instrumentMethod : target.getDeclaredMethods()) {

                    instrumentMethod.addInterceptor(RocketMQConstants.AOP_LISTEN_CHANNEL_METHOD);
                }
                return target.toBytecode();
            }
        });
    }

    private void addConsumerEditor() {
        String[] consumemessageservicefqcns = {RocketMQConstants.LISTEN_CONSUMER_ConcurrentlyService};
        for (String consumemessageservicefqcn : consumemessageservicefqcns) {
            transformTemplate.transform(consumemessageservicefqcn, new TransformCallback() {

                @Override
                public byte[] doInTransform(Instrumentor instrumentor, ClassLoader loader, String className,
                                            Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer)
                        throws InstrumentException {
                    InstrumentClass target = instrumentor.getInstrumentClass(loader, className, classfileBuffer);
                    for (InstrumentMethod method : target.getDeclaredMethods()) {
                        if ("submitConsumeRequest".equals(method.getName())) {
                            method.addInterceptor(RocketMQConstants.AOP_LISTEN_CONSUMER_METHOD);
                        }
                    }
                    return target.toBytecode();
                }
            });
        }
    }


    private void addBzConsumerEditor() {
        transformTemplate.transform(RocketMQConstants.LISTEN_CONSUMER_BZ_CONCURRENTLYSERVICE, new TransformCallback() {
            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader loader, String className,
                                        Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer)
                    throws InstrumentException {
                InstrumentClass target = instrumentor.getInstrumentClass(loader, className, classfileBuffer);
                for (InstrumentMethod instrumentMethod : target.getDeclaredMethods()) {
                    if ("excuteHandle".equals(instrumentMethod.getName())) {
                        instrumentMethod.addInterceptor(RocketMQConstants.AOP_LISTEN_CONSUMER_BZMETHOD);
                    }
                }
                return target.toBytecode();
            }
        });
    }
}