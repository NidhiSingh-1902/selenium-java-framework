package com.framework.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ExcelUtils — Reads test data from Excel (.xlsx) files for data-driven testing.
 *
 * Uses Apache POI to parse Excel files. Each row becomes a Map<String, String>
 * where keys are column headers (row 1) and values are cell values.
 *
 * Typical usage with TestNG @DataProvider:
 *
 *   // In your test class:
 *   @DataProvider(name = "loginData")
 *   public Object[][] getData() {
 *       return ExcelUtils.toDataProvider("src/test/resources/testdata/login.xlsx", "LoginData");
 *   }
 *
 *   @Test(dataProvider = "loginData")
 *   public void loginTest(Map<String, String> row) {
 *       String username = row.get("username");
 *       String password = row.get("password");
 *       // use values in test...
 *   }
 *
 * Excel file format expected:
 *   Row 1 (headers): | username | password | expectedResult |
 *   Row 2+  (data):  | admin    | pass123  | success        |
 *                    | user1    | wrong    | failure        |
 */
public class ExcelUtils {

    // Private constructor prevents instantiation — this is a static utility class
    private ExcelUtils() {}

    private static final Logger log = LogManager.getLogger(ExcelUtils.class);

    /**
     * Reads all data rows from the specified sheet and returns them as a List of Maps.
     * Row 1 is treated as headers; all subsequent rows are data rows.
     *
     * @param filePath  Path to the .xlsx file (relative to project root)
     * @param sheetName Name of the sheet tab to read
     * @return List of rows, each row as a Map of header->value pairs
     * @throws RuntimeException if file or sheet is not found
     */
    public static List<Map<String, String>> readSheet(String filePath, String sheetName) {
        List<Map<String, String>> data = new ArrayList<>();

        // try-with-resources ensures FileInputStream and Workbook are closed automatically
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                // IllegalArgumentException: caller passed a sheet name that doesn't exist
                throw new IllegalArgumentException("Sheet '" + sheetName + "' not found in: " + filePath);
            }

            // Row 0 = header row; data rows start at index 1
            Row headerRow = sheet.getRow(0);
            int colCount = headerRow.getLastCellNum();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue; // skip completely empty rows

                // Build a map for this row: column header -> cell value
                Map<String, String> rowData = new LinkedHashMap<>(); // LinkedHashMap preserves column order
                for (int j = 0; j < colCount; j++) {
                    String header = getCellValue(headerRow.getCell(j));
                    String value  = getCellValue(row.getCell(j));
                    rowData.put(header, value);
                }
                data.add(rowData);
            }

            log.info("Read {} rows from sheet '{}' in {}", data.size(), sheetName, filePath);

        } catch (IOException e) {
            log.error("Failed to read Excel file: {}", filePath, e);
            // UncheckedIOException wraps IOException for use in contexts that don't declare checked exceptions
            throw new UncheckedIOException("Could not read Excel: " + filePath, e);
        }

        return data;
    }

    /**
     * Converts the sheet data into the Object[][] format required by TestNG @DataProvider.
     * Each element is a single-element array containing a Map<String, String> for that row.
     *
     * @param filePath  Path to the .xlsx file
     * @param sheetName Name of the sheet tab
     * @return Object[][] where each row is Object[]{ Map<String,String> }
     */
    public static Object[][] toDataProvider(String filePath, String sheetName) {
        List<Map<String, String>> rows = readSheet(filePath, sheetName);
        Object[][] result = new Object[rows.size()][1];
        for (int i = 0; i < rows.size(); i++) {
            result[i][0] = rows.get(i); // wrap each row map in a single-element array
        }
        return result;
    }

    /**
     * Extracts the string value from a POI Cell regardless of its data type.
     * Handles STRING, NUMERIC (including dates), BOOLEAN, and FORMULA cells.
     * Returns empty string for blank or null cells.
     *
     * @param cell The POI Cell to read
     * @return String representation of the cell's value
     */
    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING  -> cell.getStringCellValue().trim();
            case NUMERIC ->
                // DateUtil detects Excel date-formatted numeric cells
                DateUtil.isCellDateFormatted(cell)
                    ? cell.getDateCellValue().toString()
                    : String.valueOf((long) cell.getNumericCellValue()); // cast to long drops decimal .0
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula(); // returns the formula string, not computed value
            default      -> "";
        };
    }
}
