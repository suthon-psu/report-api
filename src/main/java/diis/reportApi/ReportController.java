package diis.reportApi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/api/report")
public class ReportController {
	public static final String DEFAULT_REPORT_DIR = "./sample";
	private String reportDir = DEFAULT_REPORT_DIR;

	@Autowired
    private ObjectMapper objectMapper;

	@PostConstruct
	public void init(){
		if(System.getenv("REPORT_DIR") != null){
			reportDir = System.getenv("REPORT_DIR");
		}
	}

	@GetMapping("/hello")
	public String index() {
		return "index...";
	}

	@PostMapping(value = "/generate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> generateReport(@RequestBody ReportRequestDTO requestDTO) {
        try {
            // Get JRXML file path (adjust this path according to your project structure)
            String jrxmlPath = new File(reportDir, requestDTO.getName() + ".jrxml").getAbsolutePath();

            // Create OutputStream to hold the generated PDF
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // Add REPORT_DIR to params
            Map<String, Object> params = requestDTO.getparams();
            if (params == null) {
                params = new HashMap<>();
            }
            params.put("REPORT_DIR", reportDir);

            // Generate the report
            JasperReportGenerator.generateReport(
                jrxmlPath, 
                objectMapper.writeValueAsString(requestDTO.getData()), 
                outputStream, 
                params
            );

            // Prepare response
            byte[] reportContent = outputStream.toByteArray();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "report.pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(reportContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            //e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to generate report");
            errorResponse.put("message", e.getMessage());
            String errorBody = "{}";
            try{
                errorBody = objectMapper.writeValueAsString(errorResponse);
            }catch(Exception _e){}

            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorBody.getBytes());
        }
    }
}