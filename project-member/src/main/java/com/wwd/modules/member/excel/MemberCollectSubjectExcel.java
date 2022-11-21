package com.wwd.modules.member.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * 会员收藏的专题活动
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Data
public class MemberCollectSubjectExcel {
    @Excel(name = "id")
    private Long id;
    @Excel(name = "subject_id")
    private Long subjectId;
    @Excel(name = "subject_name")
    private String subjectName;
    @Excel(name = "subject_img")
    private String subjectImg;
    @Excel(name = "活动url")
    private String subjectUrll;

}