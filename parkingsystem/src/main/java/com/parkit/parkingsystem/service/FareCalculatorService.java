package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

 


    public void calculateFare(Ticket ticket, boolean discount){
        
        
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        double inHour = ticket.getInTime().getTime();
        double outHour = ticket.getOutTime().getTime();
       

        //TODO: Some tests are failing here. Need to check if this logic is correct
        double duration = (outHour - inHour)/(60*60*1000);
        double remise = 1;
        
         if(duration<=(0.5)){
            remise =0;} 
         else{
                if((duration > (0.50)) && (discount == true)){
                    remise=0.95;}}

 
     
        
     


        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: 
            
                ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR*remise);
           
        
            
            break;
            
            
            case BIKE: 
               
         
                ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR*remise);
         
             break;
            
          default: throw new IllegalArgumentException("Unkown Parking Type"); 
        }
        }
    
    
      public void calculateFare(Ticket ticket){
  calculateFare(ticket,false);
   }
    }
  



 
   

