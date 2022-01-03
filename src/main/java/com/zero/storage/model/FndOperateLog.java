package com.zero.storage.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "fnd_operate_log")
public class FndOperateLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 操作人
     */
    @Column(name = "operate_by")
    private String operateBy;

    /**
     * 操作类型
     */
    @Column(name = "operate_type")
    private String operateType;

    /**
     * 操作类型
     */
    @Column(name = "operate_time")
    private Date operateTime;

    /**
     * 操作内容
     */
    @Column(name = "log_content")
    private String logContent;

    /**
     * 额外内容
     */
    @Column(name = "ext_content")
    private String extContent;

    /**
     * 业务id
     */
    @Column(name = "fun_id")
    private Long funId;

    /**
     * 业务名称
     */
    @Column(name = "fun_name")
    private String funName;

    /**
     * 操作结果
     */
    @Column(name = "result")
    private String result;

    /**
     * 是否删除
     */
    private Integer deleted;

    /**
     * 创建人
     */
    @Column(name = "created_by")
    private String createdBy;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新人
     */
    @Column(name = "updated_by")
    private String updatedBy;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;
}