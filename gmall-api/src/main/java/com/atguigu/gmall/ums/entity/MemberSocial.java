package com.atguigu.gmall.ums.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author XiaoLe
 * @since 2019-04-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ums_member_social")
@ApiModel(value="MemberSocial对象", description="")
public class MemberSocial implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户ID")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty(value = "社交平台用户ID")
    @TableField("uid")
    private String uid;

    @ApiModelProperty(value = "社交平台类型")
    @TableField("type")
    private String type;

    @ApiModelProperty(value = "社交平台访问的accessToken")
    @TableField("access_token")
    private String accessToken;


}
