package com.zero.storage.service;

import com.zero.storage.model.FndAttachment;
import com.zero.storage.response.AttachmentResponse;
import com.zero.storage.vo.BaseFileVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author wei.wang
 * @date 2021/4/17 10:14
 */
public interface StorageService {

    BaseFileVO getAttachment(Long attachmentId);

    void upload(MultipartFile uploadFile, Long funId, String filePath, String funName);

    void delete(Long attachmentId);

    List<FndAttachment> fndAttachmentListByFunIdAndFunName(Long funId, String funName);

    List<AttachmentResponse> list(Long funId, String funName);

    void download(HttpServletResponse response, Long id);

    FndAttachment findById(Long attachmentId);
}
