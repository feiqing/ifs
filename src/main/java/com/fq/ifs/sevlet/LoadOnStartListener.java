package com.fq.ifs.sevlet;

import com.fq.ifs.mq.IfsMQConsumer2;
import com.fq.ifs.mq.IfsMQProducer;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * @author jifang
 * @since 2016/11/8 下午3:28.
 */
@WebListener
public class LoadOnStartListener implements ServletContextListener {

    private IfsMQConsumer2 consumer;

    private IfsMQProducer producer;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        consumer = new IfsMQConsumer2();
        producer = new IfsMQProducer();

        ServletContext context = sce.getServletContext();
        context.setAttribute("mq_consumer", consumer);
        context.setAttribute("mq_producer", producer);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        consumer.tearDown();
        producer.tearDown();
    }
}
