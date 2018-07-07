package cn.jzyunqi.common.model.poi;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public class ExcelDataDto {

    public static final String VIEW_NAME = "excelReportView";

    public static final String MODEL_NAME = "excelReportDto";

    private SXSSFWorkbook workbook;

    private CellStyle dateCellStyle;

    private CellStyle moneyCellStyle;

    private CellStyle otherCellStyle;

    private CellStyle headerCellStyle;

    public ExcelDataDto() {
        this.workbook = new SXSSFWorkbook(1);

        DataFormat dataFormat = workbook.createDataFormat();
        this.dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(dataFormat.getFormat("yyyy-MM-dd"));
        dateCellStyle.setWrapText(true);
        dateCellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

        this.moneyCellStyle = workbook.createCellStyle();
        moneyCellStyle.setDataFormat(dataFormat.getFormat("#0.00"));
        moneyCellStyle.setWrapText(true);
        moneyCellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

        this.otherCellStyle = workbook.createCellStyle();
        otherCellStyle.setWrapText(true);
        otherCellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

        Font font = workbook.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        this.headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(font);
        headerCellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);//图案样式（设置单元格颜色必须）
        headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());//单元格颜色
    }

    public SXSSFWorkbook getWorkbook() {
        return workbook;
    }

    public int addOneSheet(List<ExcelColumnHeader> columnHeaders) {
        SXSSFSheet sheet = workbook.createSheet();

        SXSSFRow firstRow = sheet.createRow(0);
        int cellIndex = 0;
        for (ExcelColumnHeader colHeader : columnHeaders) {
            if (colHeader.getWidth() != 0) {
                sheet.setColumnWidth(cellIndex, 256 * colHeader.getWidth());
            }
            Cell cell = firstRow.createCell(cellIndex);
            cell.setCellValue(colHeader.getName());
            cell.setCellStyle(headerCellStyle);

            cellIndex = cellIndex + 1;
        }
        return workbook.getSheetIndex(sheet);
    }

    public void fillOneRow(int sheetIndex, Object... datas) {
        SXSSFSheet sheet = workbook.getSheetAt(sheetIndex);
        int rowIndex = sheet.getLastRowNum() + 1;
        SXSSFRow row = sheet.createRow(rowIndex);
        int i = 0;
        for (Object data : datas) {
            fillCellValue(row, i, data);
            i = i + 1;
        }
    }

    private void fillCellValue(SXSSFRow row, int cellIndex, Object data) {
        Cell cell = row.createCell(cellIndex);
        if (data != null) {
            if (data instanceof String) {
                cell.setCellValue((String) data);
                cell.setCellStyle(otherCellStyle);
            } else if (data instanceof Date) {
                cell.setCellValue((Date) data);
                cell.setCellStyle(dateCellStyle);
            } else if (data instanceof BigDecimal) {
                cell.setCellValue(((BigDecimal) data).doubleValue());
                cell.setCellStyle(moneyCellStyle);
            } else if (data instanceof Number) {
                cell.setCellValue(((Number) data).doubleValue());
                cell.setCellStyle(otherCellStyle);
            } else if (data instanceof Boolean) {
                cell.setCellValue((Boolean) data);
                cell.setCellStyle(otherCellStyle);
            } else {
                cell.setCellValue(data.toString());
                cell.setCellStyle(otherCellStyle);
            }
        }
    }
}
