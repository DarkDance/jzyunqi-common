package cn.jzyunqi.common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public class PageDto<T> implements Serializable {

    private static final long serialVersionUID = 8451866364522532796L;

    private final List<T> rows = new ArrayList<>();

    private final long total;

    public PageDto() {
        this.total = 0;
    }

    public PageDto(long total) {
        this.total = total;
    }

    public PageDto(Collection<T> content, long total) {
        this.rows.addAll(content);
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public long getTotal() {
        return total;
    }

}
