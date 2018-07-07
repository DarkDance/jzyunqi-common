package cn.jzyunqi.common.persistence.domain;

import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
@EqualsAndHashCode
public class BaseDomain<U, PK extends Serializable> implements Serializable {
    private static final long serialVersionUID = -3880140778110262846L;

    private PK id;

    private Integer version;

    private U createdBy;

    private LocalDateTime createTime;

    private U updateBy;

    private LocalDateTime updateTime;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public PK getId() {

        return id;
    }

    public void setId(final PK id) {

        this.id = id;
    }

    @Version
    @Column
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @CreatedBy
    @Column(updatable = false)
    public U getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(U createdBy) {
        this.createdBy = createdBy;
    }

    @CreatedDate
    @Column(updatable = false)
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @LastModifiedBy
    @Column
    public U getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(U updateBy) {
        this.updateBy = updateBy;
    }

    @LastModifiedDate
    @Column
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
