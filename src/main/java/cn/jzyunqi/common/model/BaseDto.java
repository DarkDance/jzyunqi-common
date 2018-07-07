package cn.jzyunqi.common.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public class BaseDto<U, PK extends Serializable> implements Serializable {
    private static final long serialVersionUID = 4456551000041423402L;

    private PK id;

    private Integer version;

    private U createdBy;

    private LocalDateTime createTime;

    private U updateBy;

    private LocalDateTime updateTime;

    public PK getId() {
        return id;
    }

    public void setId(PK id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public U getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(U createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public U getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(U updateBy) {
        this.updateBy = updateBy;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
