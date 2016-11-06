package com.fq.ifs.servlet;

import com.fq.ifs.servlet.dao.IfsDAO;
import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@MultipartConfig
@WebServlet(name = "FileUploadServlet", urlPatterns = "/upload.action", asyncSupported = true)
public class FileUploadServlet extends HttpServlet {

    private IfsDAO dao = new IfsDAO();

    private static final String FILE_ROOT_DIR = "/files/";

    private static final String LINK_ROOT_DIR = "http://139.129.9.166:5525/files/";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        Part file = request.getPart("file");
        String fileName = getFileName(file);
        if (isValidFile(file, fileName)) {
            String uniqueName = toUniqueName(fileName);

            // 保存文件
            String levelDir = toLevelDir(uniqueName);
            String saveDir = FILE_ROOT_DIR + levelDir;
            makeDirs(saveDir);
            if (saveFile(saveDir + uniqueName, file.getInputStream())) {
                // 生成外链
                String url = LINK_ROOT_DIR + levelDir + uniqueName;
                dao.insertFile(fileName, url, file.getSize());

                response.getWriter().println(toLinkUrl(url));
                return;
            }
        }

        response.getWriter().println("error");
    }

    private boolean saveFile(String filePath, InputStream is) throws IOException {
        File file = new File(filePath);
        if (file.createNewFile()) {
            setVisitable(file);
            long copy = ByteStreams.copy(is, new FileOutputStream(file));
            return copy != 0;
        }
        return false;
    }

    private String toLinkUrl(String url) {
        String linkPattern = "<a href=\"%s\">%s</a>";
        return String.format(linkPattern, url, url);
    }

    private boolean isValidFile(Part file, String fileName) {
        // 上传的并非文件
        if (file.getContentType() == null) {
            return false;
        }
        // 没有选择任何文件
        else if (Strings.isNullOrEmpty(fileName)) {
            return false;
        }

        return true;
    }

    private String toLevelDir(String destFileName) {
        String hash = Integer.toHexString(destFileName.hashCode());
        return String.format("%s/%s/", hash.charAt(0), hash.charAt(1));
    }


    private String toUniqueName(String fileName) {
        String unique = UUID.randomUUID().toString().replace("-", "_");
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            unique += fileName.substring(index);
        }
        return unique;
    }

    private String getFileName(Part part) {
        String[] elements = part.getHeader("content-disposition").split(";");
        for (String element : elements) {
            if (element.trim().startsWith("filename")) {
                return element.substring(element.indexOf("=") + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    private void makeDirs(String saveDir) {
        File dir = new File(saveDir);
        if (!dir.exists()) {
            dir.mkdirs();

            setVisitable(dir);
            setVisitable(dir.getParentFile());
        }
    }

    private void setVisitable(File dir) {
        dir.setReadable(true, false);
        dir.setExecutable(true, false);
        dir.setWritable(true, false);
    }
}