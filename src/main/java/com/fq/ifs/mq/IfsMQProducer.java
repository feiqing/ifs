package com.fq.ifs.mq;

import com.alibaba.fastjson.JSONObject;

/**
 * @author jifang
 * @since 2016/11/8 下午3:32.
 */
public class IfsMQProducer extends AbsMQProducer {

    public void publish(String fileName, String fileLocation, long size) {
        JSONObject json = new JSONObject(3);
        json.put("file_name", fileName);
        json.put("location", fileLocation);
        json.put("size", size);

        super.publish(json.toJSONString());
    }
}
