package cn.jzyunqi.common.support.spring;

import cn.jzyunqi.common.model.poi.ExcelDataDto;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public class ExcelReportView extends AbstractXlsxStreamingView {

    @Override
    protected SXSSFWorkbook createWorkbook(Map<String, Object> model, HttpServletRequest request) {
        ExcelDataDto dto = (ExcelDataDto) model.get(ExcelDataDto.MODEL_NAME);
        return dto.getWorkbook();
    }

    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {

    }

}
