package com.zero.storage.data;

import com.zero.storage.enums.CommonStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
public class ResponseEntity<T> implements Serializable {
    private static final long serialVersionUID = -1771426378340695807L;
    private T data;
    private int status = 200;
    private String message;

    public ResponseEntity() {
    }

    public ResponseEntity(T data) {
        this.data = data;
    }

    public ResponseEntity(int status, T data) {
        this.status = status;
        this.data = data;
    }

    public static ResponseEntity<String> succeed() {
        return new ResponseEntity<>(CommonStatus.SUCCEED.name());
    }

    public static <T> ResponseEntity fail(int status, T data) {
        return new ResponseEntity(status, data);
    }

    public static <T> ResponseEntity<T> nothing() {
        return new ResponseEntity<>(null);
    }

    public static <T> ResponseEntity<T> ok(T data) {
        return new ResponseEntity<>(data);
    }

}
