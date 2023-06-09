package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public static final double REMISE =0.95;
 
    public void calculateFare(Ticket ticket, boolean discount){
                
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        double inHour = ticket.getInTime().getTime();
        double outHour = ticket.getOutTime().getTime();
       

        //TODO: Some tests are failing here. Need to check if this logic is correct
        
        double duration = (outHour - inHour)/(60*60*1000);
        

        
        if(duration<=(0.5)){
            ticket.setPrice(0);
            return;
        } 
        


        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: 
            
                ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
           break;

           case BIKE: 
                ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
         
             break;
            
          default: throw new IllegalArgumentException("Unkown Parking Type"); 
        }
        if(discount){
            ticket.setPrice(ticket.getPrice()*REMISE);
        }
    }
    
    
      public void calculateFare(Ticket ticket){
  calculateFare(ticket,false);
   }
}
  



 
   

