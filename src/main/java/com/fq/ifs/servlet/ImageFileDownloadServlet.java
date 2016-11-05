package com.fq.ifs.servlet;

import com.google.common.io.BaseEncoding;
import com.google.common.io.ByteStreams;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;

/**
 * @author jifang.
 * @since 2016/5/9 17:50.
 */
@WebServlet(name = "ImageFileDownloadServlet", urlPatterns = "/download.action")
public class ImageFileDownloadServlet extends HttpServlet {

    private static final String READ_FILE_NAME = "hdfs://139.129.9.166/ifs/%s";

    private static final String USER = "root";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/octet-stream");
        String fileName = request.getParameter("filename");
        response.setHeader("Content-Disposition", "attachment;fileName=" + filenameEncoding(fileName, request));

        String fileLocation = String.format(READ_FILE_NAME, fileName);
        FileSystem fs;
        try {
            fs = FileSystem.get(URI.create(fileLocation), new Configuration(), USER);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        ByteStreams.copy(fs.open(new Path(fileLocation)), response.getOutputStream());
    }

    private String filenameEncoding(String filename, HttpServletRequest request) throws IOException {
        // 根据浏览器信息判断
        if (request.getHeader("User-Agent").contains("Firefox")) {
            filename = String.format("=?utf-8?B?%s?=", BaseEncoding.base64().encode(filename.getBytes("UTF-8")));
        } else {
            filename = URLEncoder.encode(filename, "utf-8");
        }
        return filename;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
