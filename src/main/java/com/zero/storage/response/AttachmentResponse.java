package com.zero.storage.response;

import com.zero.storage.model.FndAttachment;
import com.zero.storage.utils.BeanCopyUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wei.wang
 * @date 2021/4/18 19:36
 */
@Data
public class AttachmentResponse implements Serializable {

    private static final long serialVersionUID = -5559517682174145323L;

    /**
     * 主键
     */
    @ApiModelProperty("主键")
    private Long id;

    /**
     * 文件名
     */
    @ApiModelProperty("文件名")
    private String fileName;

    /**
     * 业务id
     */
    @ApiModelProperty("业务id")
    private Long funId;

    /**
     * 文件名称
     */
    @ApiModelProperty("文件名称")
    private String funName;

    /**
     * 文件路径
     */
    @ApiModelProperty("文件路径")
    private String filePath;

    public static AttachmentResponse convert(FndAttachment fndAttachment) {
        AttachmentResponse attachmentResponse = new AttachmentResponse();
        BeanCopyUtil.copyProperties(fndAttachment, attachmentResponse);
        return attachmentResponse;
    }
}
