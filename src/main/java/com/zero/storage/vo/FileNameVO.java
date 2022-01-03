package com.zero.storage.vo;

import org.apache.commons.lang3.StringUtils;

public class FileNameVO {
    private String name;
    private String suffix;
    public FileNameVO(String fileName) {
        if (!StringUtils.isBlank(fileName)) {
            int idx = fileName.lastIndexOf(".");
            if (idx < 0) {
                name = fileName;
            } else {
                name = fileName.substring(0, idx);
                suffix = fileName.substring(idx + 1);
            }
        }
    }

    public String getName() {
        return StringUtils.isBlank(name) ? null : name;
    }

    public String getSuffix() {
        return StringUtils.isBlank(suffix) ? null : suffix;
    }
}
