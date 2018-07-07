package cn.jzyunqi.common.support.hibernate;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public enum DriverType {
    MYSQL5("com.mysql.jdbc.Driver", MySQL5InnoDBDialectPlus.class.getName()),;
    private String className;
    private String platform;

    private DriverType(String className, String platform) {
        this.className = className;
        this.platform = platform;
    }

    public String getClassName() {
        return className;
    }

    public String getPlatform() {
        return platform;
    }

}
