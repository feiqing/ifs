package com.fq.ifs.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author jifang
 * @since 2016/11/8 下午3:14.
 */
public abstract class AbsMQProducer extends AbsMQBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbsMQProducer.class);

    public AbsMQProducer() {
        super();
    }

    protected void publish(String message) {
        try {
            super.channel.basicPublish("", AbsMQBase.IFS_QUEUE_NAME, null, message.getBytes("UTF-8"));
        } catch (IOException e) {
            LOGGER.error("AbsMQProducer publish error, message: {}", message, e);
        }
    }
}
