package com.fq.ifs.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author jifang
 * @since 2016/11/8 下午3:04.
 */
public abstract class AbsMQBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbsMQBase.class);

    protected static final String IFS_QUEUE_NAME = "ifs";

    private static final String MQ_USER = "test";

    private static final String MQ_PASSWD = "test";

    private static final String MQ_HOST = "aliyun";

    private static final String MQ_V_HOST = "/";

    private static final int MQ_PORT = 5672;

    private Connection connection;

    protected Channel channel;

    @PostConstruct
    private void setUp() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(MQ_USER);
        factory.setPassword(MQ_PASSWD);
        factory.setHost(MQ_HOST);
        factory.setVirtualHost(MQ_V_HOST);
        factory.setPort(MQ_PORT);

        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(IFS_QUEUE_NAME, false, false, false, null);
        } catch (IOException | TimeoutException e) {
            LOGGER.error("mq init error", e);
        }
    }

    public AbsMQBase() {
        if (connection == null && channel == null) {
            this.setUp();
        }
    }

    @PreDestroy
    public void tearDown() {
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException e) {
                LOGGER.error("mq connection close error", e);
            }
        }
        if (channel != null) {
            try {
                channel.close();
            } catch (IOException | TimeoutException e) {
                LOGGER.error("mq chanel close error", e);
            }
        }
    }
}