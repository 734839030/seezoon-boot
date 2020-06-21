package com.seezoon.boot.common.file.handler;

import com.seezoon.boot.common.file.properties.LocalProperties;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.io.*;

/**
 * 本地文件处理
 *
 * @author hdf 2018年4月15日
 */
public class LocalFileHandler implements FileHandler {

    private String storePath;
    private String urlPrefix;
    @Autowired
    private LocalProperties localConfig;

    public LocalFileHandler() {
    }

    public LocalFileHandler(String storePath) {
        super();
        Assert.hasLength(storePath, "存储路径为空");
        this.storePath = storePath;
    }

    public void init() {
        this.storePath = localConfig.getStorePath();
        this.urlPrefix = localConfig.getUrlPrefix();
    }

    @Override
    public void upload(String relativePath, InputStream in) throws IOException {
        Assert.hasLength(storePath, "存储路径为空");
        Assert.hasLength(relativePath, "相对路径为空");
        Assert.notNull(in, "上传文件为空");
        File dest = new File(storePath, relativePath);
        FileUtils.copyInputStreamToFile(in, dest);
    }

    @Override
    public InputStream download(String relativePath) throws FileNotFoundException {
        Assert.hasLength(relativePath, "相对路径为空");
        File file = new File(storePath, relativePath);
        FileInputStream in = new FileInputStream(file);
        return in;
    }

    @Override
    public void destroy() {
        // 云上传功能需要暴露销毁接口
    }

    @Override
    public String getFullUrl(String relativePath) {
        if (StringUtils.isNotEmpty(relativePath)) {
            return urlPrefix + relativePath;
        }
        return null;
    }
}
