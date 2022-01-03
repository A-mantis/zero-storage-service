package com.zero.storage.control;

import com.zero.storage.data.ResponseEntity;
import com.zero.storage.exception.AssistBusinessException;
import com.zero.storage.response.AttachmentResponse;
import com.zero.storage.service.StorageService;
import com.zero.storage.vo.BaseFileVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/v1/")
@Api(tags = "存储接口")
public class StorageController {

    @Resource
    private StorageService storageService;

    @GetMapping("/file/{attachmentId}")
    @ApiOperation("根据attachmentId获取文件信息")
    public ResponseEntity<BaseFileVO> getAttachment(@PathVariable Long attachmentId) {
        if (attachmentId == null) {
            throw new AssistBusinessException("根据attachmentId获取文件信息失败，请输入attachmentId");
        }
        return ResponseEntity.ok(storageService.getAttachment(attachmentId));
    }

    @ApiOperation("上传文件")
    @PostMapping("/file")
    public ResponseEntity<String> upload(@RequestParam(value = "uploadFile", required = false) MultipartFile uploadFile,
                                         @RequestParam(value = "funId", required = false) Long funId,
                                         @RequestParam(value = "filePath", required = false) String filePath,
                                         @RequestParam(value = "funName", required = false) String funName) {
        if (uploadFile.isEmpty()) {
            throw new AssistBusinessException("上传失败，请选择文件");
        }
        if (funId == null) {
            throw new AssistBusinessException("上传失败，请输入funId");
        }
        if (StringUtils.isEmpty(filePath)) {
            throw new AssistBusinessException("上传失败，请输入filePath");
        }
        if (StringUtils.isEmpty(funName)) {
            throw new AssistBusinessException("上传失败，请输入funName");
        }
        storageService.upload(uploadFile, funId, filePath, funName);
        return ResponseEntity.ok("上传成功！");
    }

    @ApiOperation("下载文件")
    @GetMapping("/file/download/{attachmentId}")
    public void download(HttpServletResponse response, @PathVariable Long attachmentId) {
        if (attachmentId == null) {
            throw new AssistBusinessException("下载文件失败，请输入attachmentId");
        }
        storageService.download(response, attachmentId);
    }

    @ApiOperation("删除文件")
    @DeleteMapping("/file/{attachmentId}")
    public ResponseEntity<String> delete(@PathVariable Long attachmentId) {
        if (attachmentId == null) {
            throw new AssistBusinessException("删除文件失败，请输入attachmentId");
        }
        storageService.delete(attachmentId);
        return ResponseEntity.ok("刪除成功！");
    }

    @ApiOperation("根据funId和funName查询文件")
    @GetMapping("/file")
    public List<AttachmentResponse> list(@RequestParam("funId") Long funId,
                                         @RequestParam("funName") String funName) {
        if (funId == null) {
            throw new AssistBusinessException("查询失败，请输入funId");
        }
        if (funName == null) {
            throw new AssistBusinessException("查询失败，请输入funName");
        }
        return storageService.list(funId, funName);
    }
}
