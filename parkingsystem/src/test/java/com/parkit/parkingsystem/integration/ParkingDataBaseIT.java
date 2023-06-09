package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.*;
import java.util.Date;
import java.lang.Thread;
import java.lang.InterruptedException;


@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    

    @Mock
    private static InputReaderUtil inputReaderUtil;
    

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
       
        
        
        
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        Mockito.lenient().when(inputReaderUtil.readSelection()).thenReturn(1);
        Mockito.lenient().when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
        

    }

    @AfterAll
    private static void tearDown(){

    }

    @Test
    public void testParkingACar(){
        
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        ParkingSpot parkingSpot = ticket.getParkingSpot();
        assertNotNull(ticket);
        assertTrue(parkingSpotDAO.updateParking(parkingSpot));
       
        //TODO: check that a ticket is actualy saved in DB and Parking table is updated with availability
    }

    @Test
    public void testParkingLotExit(){
        try {
            // Sleep for 1 seconds
            
        //testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        Thread.sleep(1000);
        parkingService.processExitingVehicle();
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        assertNotNull(ticket.getPrice());
        assertNotNull(ticket.getOutTime());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //TODO: check that the fare generated and out time are populated correctly in the database
    }


    @Test
    public void testParkingLotExitRecurringUser(){
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        // Ticket du jour 1
        parkingService.processIncomingVehicle();
        Ticket ticketBeforeDayOne = ticketDAO.getTicket("ABCDEF");
        ticketBeforeDayOne.setInTime(new Date(System.currentTimeMillis() - (4 * 60 * 60 * 1000)));
        ticketDAO.updateTicket(ticketBeforeDayOne);
        parkingService.processExitingVehicle();
        Ticket ticketDayOne = ticketDAO.getTicket("ABCDEF");
        // Ticket du jour 2
        parkingService.processIncomingVehicle();
        Ticket ticketBeforeDayTwo = ticketDAO.getTicket("ABCDEF");
        ticketBeforeDayTwo.setInTime(new Date(System.currentTimeMillis() - (4 * 60 * 60 * 1000)));
        ticketDAO.updateTicket(ticketBeforeDayTwo);
        parkingService.processExitingVehicle();
        Ticket ticketDayTwo = ticketDAO.getTicket("ABCDEF");
        //System.out.println("Ticket day one : " + ticketDayOne.getPrice()+ " - "+ "Ticket day two : "+ticketDayTwo.getPrice());
        assertEquals(ticketDayOne.getPrice()*FareCalculatorService.REMISE,ticketDayTwo.getPrice(), 0.001);      
    }
}
