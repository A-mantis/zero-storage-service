package com.zero.storage.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;


@AllArgsConstructor
@Getter
public enum OperateEnum {
    /**
     * 上传附件
     */
    UPLOAD_FILE("UPLOAD_FILE", "上传附件"),
    /**
     * 删除附件
     */
    DELETE_FILE("DELETE_FILE", "删除附件");


    private String code;
    private String value;

    public static OperateEnum get(String code) {
        Optional<OperateEnum> optional = Arrays.stream(OperateEnum.values())
                .filter(item -> Objects.equals(item.getCode(), code)).findFirst();
        return optional.orElse(null);
    }
}
