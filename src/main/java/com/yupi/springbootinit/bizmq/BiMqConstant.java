package com.yupi.springbootinit.bizmq;

/**
 * @author : wangshanjie
 * 将常量存放在一个包的接口中(创建一个专门用于存放常量的包，并在该包中定义接口或类来存放相关常量)。
 * 这里如果为了复用多个消费者，可以做多个路由键
 * @date : 9:40 2023/8/13
 */
public interface BiMqConstant {

    String BI_EXCHANGE_NAME = "bi_exchange";

    String BI_QUEUE_NAME = "bi_queue";

    String BI_ROUTING_KEY = "bi_routingKey";
}