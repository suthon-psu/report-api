package diis.reportApi;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.query.JsonQueryExecuterFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JasperReportGenerator {

    private static Logger logger = LoggerFactory.getLogger(JasperReportGenerator.class);
    // Cache to store compiled reports
    private static final Map<String, JasperReport> reportCache = new HashMap<>();

    public static void generateReport(String jrxmlPath, String jsonData, OutputStream output,
            Map<String, Object> parameters) throws Exception {
        // Get or compile the JasperReport
        JasperReport jasperReport = getJasperReport(jrxmlPath);

        // Load the JSON file
        InputStream jsonInputStream = new ByteArrayInputStream(jsonData.getBytes(StandardCharsets.UTF_8));
        parameters.put(JsonQueryExecuterFactory.JSON_INPUT_STREAM, jsonInputStream);

        // Fill the report
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters); // null, dataSource);

        // Export the report to PDF
        JasperExportManager.exportReportToPdfStream(jasperPrint, output);

    }

    private static synchronized JasperReport getJasperReport(String jrxmlPath) throws JRException {
        JasperReport jasperReport = reportCache.get(jrxmlPath);
        if (jasperReport == null) {
            // If not in cache, compile and cache the report
            File jrxmlFile = new File(jrxmlPath);
            jasperReport = JasperCompileManager.compileReport(jrxmlFile.getAbsolutePath());
            if("true".equals(System.getenv("REPORT_CACHE_ENABLED"))){                
                reportCache.put(jrxmlPath, jasperReport);
                logger.info("report with path={} has been cached.", jrxmlPath);
            }
        }
        return jasperReport;
    }

    public static void main(String[] args) throws Exception {
        String jrxmlPath = "sample/employee-report.jrxml";
        String jsonPath = "sample/employee-data.json";
        String outputPath = "sample/output.pdf";

        String jsonContent = new String(Files.readAllBytes(Paths.get(jsonPath)), StandardCharsets.UTF_8);
        Map<String, Object> parameters = new HashMap<>();

        OutputStream output = new FileOutputStream(outputPath);

        generateReport(jrxmlPath, jsonContent, output, parameters);
        output.close();

        System.out.println("Report generated successfully: " + outputPath);
    }
}