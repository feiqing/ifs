package com.fq.ifs.mq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author jifang
 * @since 2016/11/8 下午3:15.
 */
public abstract class AbsMQConsumer extends AbsMQBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbsMQConsumer.class);

    public AbsMQConsumer() {
        super();

        Consumer consumer = new DefaultConsumer(super.channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                handleMessage(new String(body, "UTF-8"));
            }
        };

        try {
            super.channel.basicConsume(AbsMQBase.IFS_QUEUE_NAME, true, consumer);
        } catch (IOException e) {
            LOGGER.error("mq consumer init error", e);
        }
    }

    protected abstract void handleMessage(String message);
}
