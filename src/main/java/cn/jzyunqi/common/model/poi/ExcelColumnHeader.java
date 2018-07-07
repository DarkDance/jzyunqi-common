package cn.jzyunqi.common.model.poi;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public class ExcelColumnHeader {

    private String name;//列名

    private int width;//列宽， 以1个Excel英文字母长度为单位

    public ExcelColumnHeader(String name) {
        this(name, 0);
    }

    public ExcelColumnHeader(String name, int width) {
        this.name = name;
        this.width = width;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
