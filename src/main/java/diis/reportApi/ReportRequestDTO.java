package diis.reportApi;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import java.util.HashMap;
import java.util.Map;

public class ReportRequestDTO {
    private String name;
    private Map<String, Object> data;
    private Map<String, Object> params;
    private Map<String, Object> additional = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setdata(Map<String, Object> data) {
        this.data = data;
    }

    public Map<String, Object> getparams() {
        return params;
    }

    public void setparams(Map<String, Object> params) {
        this.params = params;
    }

    public Map<String, Object> getAdditional() {
        return additional;
    }

    @JsonAnySetter
    public void setAdditional(String name, Object value) {
        this.additional.put(name, value);
    }
}
