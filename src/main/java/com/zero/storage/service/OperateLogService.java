package com.zero.storage.service;

import com.zero.storage.model.FndOperateLog;

public interface OperateLogService {

    void succeedLog(Long funId, String funName, String operateType, String logContent, String extContent);

    void insert(FndOperateLog fndOperateLog);

    void failedLog(Long funId, String funName, String operateType, String logContent);
}
