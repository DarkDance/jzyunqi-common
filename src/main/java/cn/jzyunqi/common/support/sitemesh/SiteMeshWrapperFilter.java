package cn.jzyunqi.common.support.sitemesh;

import cn.jzyunqi.common.utils.CollectionUtilPlus;
import cn.jzyunqi.common.utils.MapUtilPlus;
import cn.jzyunqi.common.utils.StringUtilPlus;
import org.sitemesh.builder.SiteMeshFilterBuilder;
import org.sitemesh.config.ConfigurableSiteMeshFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public class SiteMeshWrapperFilter extends ConfigurableSiteMeshFilter {

    private static final String DEFAULT_NO_WRAPPER_PARAMETER = "noSiteMeshWapper";

    private Map<String, String> decoratorMapping;
    private List<String> excludedPaths;
    private String noWrapperParameterName = DEFAULT_NO_WRAPPER_PARAMETER;

    @Override
    protected void applyCustomConfiguration(SiteMeshFilterBuilder builder) {
        if (MapUtilPlus.isEmpty(decoratorMapping)) {
            throw new NullPointerException("decorator can not be null!");
        }

        //decoratorMapping.forEach(builder::addDecoratorPath); Lambda 方式
        for (Map.Entry<String, String> entry : decoratorMapping.entrySet()) {
            builder.addDecoratorPath(entry.getKey(), entry.getValue());
        }
        if (CollectionUtilPlus.isNotEmpty(excludedPaths)) {
            //excludedPaths.forEach(builder::addExcludedPath); Lambda 方式
            for (String path : excludedPaths) {
                builder.addExcludedPath(path);
            }
        }
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        if (noNeedWrapper(request)) {
            chain.doFilter(req, res);
        } else {
            super.doFilter(req, res, chain);
        }

    }

    /*
     * ajax风格和特别指定不需要包装的页面（传递特定参数noSiteMeshWapper），不做包装
     */
    private boolean noNeedWrapper(HttpServletRequest request) {
        String xmlHttpRequest = request.getHeader("X-Requested-With");
        String noWrapperParameter = StringUtilPlus.defaultString(request.getParameter(noWrapperParameterName));
        return "XMLHttpRequest".equalsIgnoreCase(xmlHttpRequest)
                || noWrapperParameter.equalsIgnoreCase("true")
                || noWrapperParameter.equalsIgnoreCase("yes")
                || noWrapperParameter.equals("1");
    }

    /**
     * 设置页面装饰文件
     *
     * @param decoratorMapping 路径-装饰页面键值对
     */
    public void setDecoratorMapping(Map<String, String> decoratorMapping) {
        this.decoratorMapping = decoratorMapping;
    }

    /**
     * 设置不需要装饰的路径
     *
     * @param excludedPaths 路径列表，支持Ant匹配
     */
    public void setExcludedPaths(List<String> excludedPaths) {
        this.excludedPaths = excludedPaths;
    }

    /**
     * 设置不需要装饰的路径参数，当路径后面添加了该参数时候，动态的不装饰
     *
     * @param noWrapperParameterName 参数名，默认为“noSiteMeshWapper”
     */
    public void setNoWrapperParameterName(String noWrapperParameterName) {
        this.noWrapperParameterName = noWrapperParameterName;
    }

}