package cn.jzyunqi.common.support;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.IOException;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public class SpringContextUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtils.applicationContext = applicationContext;
    }

    /**
     * 获取指定bean对象
     *
     * @param name bean名称
     * @return bean对象
     * @throws org.springframework.beans.BeansException
     */
    public static Object getBean(String name) throws BeansException {
        return applicationContext.getBean(name);
    }

    /**
     * 获取相对路径的文件夹物理位置
     *
     * @param path 相对路径
     * @return 文件夹物理位置
     * @throws java.io.IOException
     */
    public static String getDirRealPath(String path) throws IOException {
        return getRealPath(path) + System.getProperty("file.separator");
    }

    /**
     * 获取相对路径的文件物理位置
     *
     * @param path 相对路径
     * @return 文件物理位置
     * @throws java.io.IOException
     */
    public static String getFileRealPath(String path) throws IOException {
        return getRealPath(path);
    }

    private static String getRealPath(String path) throws IOException {
        return applicationContext.getResource(path).getFile().getAbsolutePath();
    }
}
