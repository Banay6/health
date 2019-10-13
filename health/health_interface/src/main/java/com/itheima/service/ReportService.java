package com.itheima.service;

import java.util.List;
import java.util.Map;

public interface ReportService {
    Map<String, Object> getBusinessReport() throws Exception;

    List<Map<String, Object>> getGender();

    List<Map<String, Object>> getAge();
}
