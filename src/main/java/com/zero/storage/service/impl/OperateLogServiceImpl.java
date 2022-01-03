package com.zero.storage.service.impl;

import com.zero.storage.dao.FndOperateLogMapper;
import com.zero.storage.enums.CommonStatus;
import com.zero.storage.enums.DeletedEnum;
import com.zero.storage.model.FndOperateLog;
import com.zero.storage.service.OperateLogService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class OperateLogServiceImpl implements OperateLogService {

    @Resource
    private FndOperateLogMapper fndOperateLogMapper;

    @Async
    @Override
    public void succeedLog(Long funId, String funName, String operateType, String logContent, String extContent) {
        FndOperateLog fndOperateLog = new FndOperateLog();
        fndOperateLog.setCreatedBy("AssistSession.getCurrentUser()");
        fndOperateLog.setUpdatedBy("AssistSession.getCurrentUser()");
        fndOperateLog.setOperateBy("AssistSession.getCurrentUser()");
        fndOperateLog.setDeleted(DeletedEnum.NO.getCode());
        fndOperateLog.setOperateType(operateType);
        fndOperateLog.setLogContent(logContent);
        fndOperateLog.setExtContent(extContent);
        fndOperateLog.setFunId(funId);
        fndOperateLog.setFunName(funName);
        fndOperateLog.setResult(String.valueOf(CommonStatus.SUCCEED));
        fndOperateLogMapper.insert(fndOperateLog);
    }

    @Async
    @Override
    public void insert(FndOperateLog fndOperateLog) {
        fndOperateLogMapper.insert(fndOperateLog);
    }

    @Async
    @Override
    public void failedLog(Long funId, String funName, String operateType, String logContent) {
        FndOperateLog fndOperateLog = new FndOperateLog();
        fndOperateLog.setCreatedBy("AssistSession.getCurrentUser()");
        fndOperateLog.setOperateBy("AssistSession.getCurrentUser()");
        fndOperateLog.setDeleted(DeletedEnum.NO.getCode());
        fndOperateLog.setOperateType(operateType);
        fndOperateLog.setLogContent(logContent);
        fndOperateLog.setFunId(funId);
        fndOperateLog.setFunName(funName);
        fndOperateLog.setResult(String.valueOf(CommonStatus.FAILED));
        fndOperateLogMapper.insert(fndOperateLog);
    }
}
