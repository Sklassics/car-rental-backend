package com.sklassics.cars.services.utility;

import java.util.HashMap;
import java.util.Map;

public class ResponseUtil {

   
    public static class ResponseCodes {
        public static final int SUCCESS = 1000;
        public static final int VALIDATION_ERROR = 1001;
        public static final int NOT_FOUND = 1002;
        public static final int UNAUTHORIZED = 1003;
        public static final int INTERNAL_ERROR = 1004;
        public static final int CONFLICT = 1005;
        
    }


    public static class ErrorMessages {
        public static final String VEHICLE_NOT_FOUND = "The requested vehicle was not found";
        public static final String INVALID_VEHICLE_DATA = "Provided vehicle data is invalid";
        public static final String UNAUTHORIZED_ACCESS = "Authentication required to access this resource";
        public static final String INTERNAL_SERVER_ERROR = "An unexpected error occurred";

        
        public static String notFoundWithId(String entity, Long id) {
            return String.format("%s not found with ID: %d", entity, id);
        }

        public static String alreadyExists(String entity) {
            return String.format("%s already exists", entity);
        }
        
        public static String notFoundWithId(String entity, String value) {
            return entity + " not found with value: " + value;
        }
    }


   
    public static Map<String, Object> successWithData(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", ResponseCodes.SUCCESS);
        response.put("message", message);
        response.put("data", data);
        return response;
    }

   
    public static Map<String, Object> successMessage(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", ResponseCodes.SUCCESS);
        response.put("message", message);
        return response;
    }

    
    public static Map<String, Object> error(int code, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", code);
        response.put("message", message);
//      response.put("data", null);
        return response;
    }

    
    public static Map<String, Object> validationError(String message) {
        return error(ResponseCodes.VALIDATION_ERROR, message);
    }

    public static Map<String, Object> notFound(String message) {
        return error(ResponseCodes.NOT_FOUND, message);
    }

    public static Map<String, Object> unauthorized(String message) {
        return error(ResponseCodes.UNAUTHORIZED, message);
    }

    public static Map<String, Object> internalError(String message) {
        return error(ResponseCodes.INTERNAL_ERROR, message);
    }

    public static Map<String, Object> conflict(String message) {
        return error(ResponseCodes.CONFLICT, message);
    }
}
