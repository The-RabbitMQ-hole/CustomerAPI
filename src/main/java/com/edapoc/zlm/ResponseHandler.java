package com.edapoc.zlm;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseHandler {
    public static ResponseEntity<Object> generateResponse(String message, HttpStatus status, Object responseObj){
        Map<String, Object> map = new HashMap<>();
        map.put("status", status.value());
        if(status.isError()) map.put("error", status.getReasonPhrase());
        if(responseObj != null) map.put("data", responseObj);
        if(message != null) map.put("message", message);

        return new ResponseEntity<>(map, status);
    }
}
