package com.fq.ifs.servlet;

import com.fq.ifs.servlet.dao.FileDAO;
import com.google.common.base.Strings;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@MultipartConfig
@WebServlet(name = "FileUploadServlet", urlPatterns = "/upload.action", asyncSupported = true)
public class FileUploadServlet extends HttpServlet {

    private FileDAO dao = new FileDAO();

    private static final String ROOT_DIR = "/data/files/";

    private static final String LINK_ROOT_DIR = "http://139.129.9.166:8000/files/";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Part image = request.getPart("file");
        String fileName = getFileName(image);
        if (isFileValid(image, fileName)) {
            String uniqueFileName = toUniqueName(fileName);

            // 保存文件
            String levelDir = toLevelDir(uniqueFileName);
            String saveDir = ROOT_DIR + levelDir;
            makeDirs(saveDir);
            image.write(saveDir + uniqueFileName);

            // 生成外链
            String url = LINK_ROOT_DIR + levelDir + uniqueFileName;
            dao.insertFile(fileName, url);

            response.getWriter().println(toLinkUrl(url));
        } else {
            response.getWriter().println("wrong file");
        }
    }

    private String toLinkUrl(String url) {
        String linkPattern = "<a href=\"%s\">%s</a>";
        return String.format(linkPattern, url, url);
    }

    private boolean isFileValid(Part file, String fileName) {
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

    private String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "_");
    }

    private String toUniqueName(String fileName) {
        String uniqueName = generateUUID();
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            uniqueName += fileName.substring(index);
        }
        return uniqueName;
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
            dir.setReadable(true, false);
            dir.setExecutable(true, false);
            dir.setWritable(true, false);
        }
    }
}