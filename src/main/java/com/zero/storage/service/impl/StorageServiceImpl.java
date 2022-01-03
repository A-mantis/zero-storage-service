package com.zero.storage.service.impl;

import com.zero.storage.dao.FndAttachmentMapper;
import com.zero.storage.enums.DeletedEnum;
import com.zero.storage.enums.OperateEnum;
import com.zero.storage.exception.AssistBusinessException;
import com.zero.storage.model.FndAttachment;
import com.zero.storage.response.AttachmentResponse;
import com.zero.storage.service.OperateLogService;
import com.zero.storage.service.StorageService;
import com.zero.storage.utils.JsonUtil;
import com.zero.storage.vo.BaseFileVO;
import com.zero.storage.vo.FileNameVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

@Slf4j
@Service
public class StorageServiceImpl implements StorageService {

    @Resource
    private FndAttachmentMapper fndAttachmentMapper;

    @Resource
    private OperateLogService operateLogService;

    @Override
    public BaseFileVO getAttachment(Long attachmentId) {
        Example example = new Example(FndAttachment.class);
        example.and()
                .andEqualTo("deleted", DeletedEnum.NO.getCode())
                .andEqualTo("id", attachmentId);
        FndAttachment attachment = fndAttachmentMapper.selectOneByExample(example);
        new AssistBusinessException("文件不存在").throwIf(attachment == null);
        return new BaseFileVO(attachment.getFileName(), attachment.getFilePath());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upload(MultipartFile uploadFile, Long funId, String filePath, String funName) {
        try {
            String fileName = uploadFile.getOriginalFilename();
            String saveFileName = createFile(fileName, filePath, uploadFile.getInputStream());
            //插入附件表
            FndAttachment fndAttachment = new FndAttachment();
            fndAttachment.setFileName(fileName);
            fndAttachment.setFunId(funId);
            fndAttachment.setFunName(funName);
            fndAttachment.setFilePath(filePath + "/" + saveFileName);
            fndAttachment.setDeleted(DeletedEnum.NO.getCode());
            fndAttachment.setCreatedBy("AssistSession.getCurrentUser()");
            fndAttachment.setUpdatedBy("AssistSession.getCurrentUser()");
            fndAttachmentMapper.insert(fndAttachment);
            log.info("createFile Result: {}", saveFileName);
            operateLogService.succeedLog(funId, funName, OperateEnum.UPLOAD_FILE.getCode(), OperateEnum.UPLOAD_FILE.getValue(), JsonUtil.objectToJson(fndAttachment));
        } catch (Exception e) {
            log.error("uploadFile Exception:{}", e.getMessage());
            operateLogService.failedLog(funId, funName, OperateEnum.UPLOAD_FILE.getCode(), OperateEnum.UPLOAD_FILE.getValue());
            throw new AssistBusinessException("文件上传失败");
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long attachmentId) {
        Example example = new Example(FndAttachment.class);
        example.and()
                .andEqualTo("deleted", DeletedEnum.NO.getCode())
                .andEqualTo("id", attachmentId);
        FndAttachment fndAttachment = fndAttachmentMapper.selectOneByExample(example);
        new AssistBusinessException("文件不存在").throwIf(fndAttachment == null);
        try {
            fndAttachment.setDeleted(DeletedEnum.YES.getCode());
            fndAttachment.setUpdateTime(new Date());
            fndAttachmentMapper.updateByPrimaryKey(fndAttachment);
            operateLogService.succeedLog(fndAttachment.getFunId(), fndAttachment.getFunName(), OperateEnum.DELETE_FILE.getCode(), OperateEnum.DELETE_FILE.getValue(), JsonUtil.objectToJson(fndAttachment));
        } catch (Exception e) {
            log.error("deleteFile Exception:{}", e.getMessage());
            operateLogService.failedLog(fndAttachment.getFunId(), fndAttachment.getFunName(), OperateEnum.UPLOAD_FILE.getCode(), OperateEnum.UPLOAD_FILE.getValue());
            throw new AssistBusinessException("文件删除失败");
        }
    }

    @Override
    public List<FndAttachment> fndAttachmentListByFunIdAndFunName(Long funId, String funName) {
        Example example = new Example(FndAttachment.class);
        example.and()
                .andEqualTo("deleted", DeletedEnum.NO.getCode())
                .andEqualTo("funId", funId)
                .andEqualTo("funName", funName);
        return Optional.ofNullable(fndAttachmentMapper.selectByExample(example)).orElse(new ArrayList<>());
    }

    /**
     * 根据funId和funName查询文件，获取最新版本
     * @param funId
     * @param funName
     * @return
     */
    @Override
    public List<AttachmentResponse> list(Long funId, String funName) {
        List<AttachmentResponse> attachmentResponseList = new ArrayList<>();
        Optional<FndAttachment> fndAttachmentOptional = fndAttachmentListByFunIdAndFunName(funId, funName)
                .stream().max(Comparator.comparing(FndAttachment::getCreateTime));
        fndAttachmentOptional.ifPresent(fndAttachment ->
                attachmentResponseList.add(AttachmentResponse.convert(fndAttachment))
        );
        return attachmentResponseList;
    }

    @Override
    public void download(HttpServletResponse response, Long attachmentId) {
        FndAttachment fndAttachment = findById(attachmentId);
        if (fndAttachment == null) {
            throw new AssistBusinessException("下载异常，请联系管理员或稍后再试");
        }
        //待下载文件名
        String filePath = fndAttachment.getFilePath();
        File file = new File(filePath);
        if (!file.exists()) {
            throw new AssistBusinessException("下载异常，文件不存在");
        }
        byte[] buff = new byte[1024];
        //创建缓冲输入流
        BufferedInputStream bis = null;
        OutputStream outputStream = null;
        try {
            response.reset();
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fndAttachment.getFileName(), "UTF-8"));
            outputStream = response.getOutputStream();
            //这个路径为待下载文件的路径
            bis = new BufferedInputStream(new FileInputStream(file));
            int read = bis.read(buff);
            //通过while循环写入到指定了的文件夹中
            while (read != -1) {
                outputStream.write(buff, 0, buff.length);
                outputStream.flush();
                read = bis.read(buff);
            }
        } catch (IOException e) {
            e.printStackTrace();
            //出现异常返回给页面失败的信息
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public FndAttachment findById(Long attachmentId) {
        Example example = new Example(FndAttachment.class);
        example.and()
                .andEqualTo("deleted", DeletedEnum.NO.getCode())
                .andEqualTo("id", attachmentId);
        return fndAttachmentMapper.selectOneByExample(example);
    }

    private String generateFileName(String fileName) {
        FileNameVO fileNameVo = new FileNameVO(fileName);
        return System.currentTimeMillis() + (StringUtils.isBlank(fileNameVo.getSuffix()) ? "" : "." + fileNameVo.getSuffix());
    }

    public String createFile(String filename, String path, InputStream in) {
        try {
            isFileExist(path);
            String saveFilename = generateFileName(filename);
            FileOutputStream out =
                    new FileOutputStream(path + "/" + saveFilename);
            //创建一个缓冲区
            byte buffer[] = new byte[1024];
            //判断输入流中的数据是否已经读完的标识
            int len = 0;
            //循环将输入流读入到缓冲区当中，(len=in.read(buffer))>0就表示in里面还有数据
            while ((len = in.read(buffer)) > 0) {
                //使用FileOutputStream输出流将缓冲区的数据写入到指定的目录(savePath + "/" + filename)当中
                out.write(buffer, 0, len);
            }
            out.close();
            closeInputStream(in);
            return saveFilename;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 关闭输入流
     *
     * @param in
     */
    private void closeInputStream(InputStream in) {
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 校验文件夹是否存在
     *
     * @param path
     */
    private void isFileExist(String path) {
        File pathFile = new File(path);
        if (!pathFile.exists()) {
            //创建目录
            pathFile.mkdirs();
        }
    }
}
