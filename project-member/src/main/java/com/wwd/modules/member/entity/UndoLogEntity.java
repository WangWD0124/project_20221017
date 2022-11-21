package com.wwd.modules.member.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Data
@TableName("undo_log")
public class UndoLogEntity {

    /**
     * 
     */
	private Long id;
    /**
     * 
     */
	private Long branchId;
    /**
     * 
     */
	private String xid;
    /**
     * 
     */
	private String context;
    /**
     * 
     */
	//private unknowType rollbackInfo;
    /**
     * 
     */
	private Integer logStatus;
    /**
     * 
     */
	private Date logCreated;
    /**
     * 
     */
	private Date logModified;
    /**
     * 
     */
	private String ext;
}