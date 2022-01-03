package com.zero.storage.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;


@AllArgsConstructor
@Getter
public enum DeletedEnum {
    YES(1, "是"),
    NO(0, "否");

    private Integer code;
    private String value;

    public static DeletedEnum get(Integer code) {
        Optional<DeletedEnum> optional = Arrays.stream(DeletedEnum.values())
                .filter(item -> Objects.equals(item.getCode(), code)).findFirst();
        return optional.orElse(null);
    }

}
