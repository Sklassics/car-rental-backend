package com.sklassics.cars.exceptions;

public class CustomExceptions {
		
	

	public static class CarNotFoundException extends RuntimeException {
 	private static final long serialVersionUID = 1L;
     public CarNotFoundException(String message) {
         super(message);
     }
 }
   
    

    public static class CarAlreadyReservedException extends RuntimeException {
    	private static final long serialVersionUID = 1L;
        public CarAlreadyReservedException(String message) {
            super(message);
        }
    }
    
    
    public static class UserNotFoundException extends RuntimeException {
    	private static final long serialVersionUID = 1L;
        public UserNotFoundException(String message) {
            super(message);
        }
    }
    
    

}
