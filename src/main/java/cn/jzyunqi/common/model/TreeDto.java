package cn.jzyunqi.common.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public class TreeDto implements Serializable {

    private static final long serialVersionUID = 4373823456647142950L;

    public enum State {
        closed, open
    }

    private String id;

    private String parent;

    private String text;

    private String iconCls;

    private boolean checked;

    private State state;

    private Map<String, Object> attributes;

    private List<TreeDto> children;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIconCls() {
        return iconCls;
    }

    public void setIconCls(String iconCls) {
        this.iconCls = iconCls;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public List<TreeDto> getChildren() {
        return children;
    }

    public void setChildren(List<TreeDto> children) {
        this.children = children;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }
}
