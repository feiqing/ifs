package com.fq.ifs.mq;

import com.alibaba.fastjson.JSONObject;
import com.fq.ifs.dao.IfsDAO;

/**
 * @author jifang
 * @since 2016/11/8 下午3:24.
 */
public class IfsMQConsumer extends AbsMQConsumer {

    private IfsDAO dao = new IfsDAO();

    @Override
    protected void handleMessage(String message) {
        JSONObject json = JSONObject.parseObject(message);
        String fileName = json.getString("file_name");
        String fileLocation = json.getString("location");
        long size = json.getLongValue("size");
        dao.insertFile(fileName, fileLocation, size);
    }
}
