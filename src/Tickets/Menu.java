//Name: Isaac Blackwood
//Net ID: idb170030
package Tickets;

import static java.lang.System.out;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Menu 
{
	private static final int RESERVE_SEATS = 1, EXIT = 2;

	public static void display(Scanner userInput, String fileName) 
	{
		boolean quit = false;
		
		while (!quit) //display main menu until user quits
		{
			//display choices
			out.println("1. Reserve Seats \n2. Exit");
			
			try
			{
				//get user input
				int userChoice = userInput.nextInt();
				
				switch (userChoice) 
				{
				//lets user reserve seats
				case RESERVE_SEATS:	
					reserveSeats(userInput, fileName);
					break;
				//lets user exit the program
				case EXIT: 				
					quit = true;
					exit(fileName);
					break;
				//conveys error message
				default: 				
					throw new InputMismatchException();
				}
			}
			catch (InputMismatchException e) 
			{
				//user didn't enter a number so ask again
				userInput.nextLine();
				out.println("Enter \'1\' or \'2\' to select your choice.\nPlease try again.");
			}
			catch (FileNotFoundException e)
			{
				//input file couldn't be openEd so go ahead and quit the program
				quit = true;
			}
			catch (TicketsPackageDataMemberException e)
			{
				//error message was displayed earlier
				quit = true;
			} 
			catch (TheaterSeatNotFoundException e) 
			{
				//Should be impossible if this comes from the reserve function
				quit = true;
			}
		}
	}
	
	//choices
	private static void reserveSeats(Scanner userInput, String fileName) throws FileNotFoundException, InvalidTicketTypeException, InvalidRowNumberException, InvalidSeatException, TheaterSeatNotFoundException
	{		
		//Initialize the auditorium
		Auditorium auditorium = new Auditorium(fileName);
		
		//display the theater on the screen
		boolean displayInternalInfo = false;
		auditorium.display(displayInternalInfo);
		
		//Create a SeatReservationRequest
		SeatReservationRequest seatReservationRequest = auditorium.createSeatReservationRequest(userInput);
		
		//Process the request
		seatReservationRequest.reserve(userInput);		
	}
	private static void exit(String fileName) throws FileNotFoundException, InvalidTicketTypeException, InvalidRowNumberException, InvalidSeatException
	{
		//initialize the auditorium
		Auditorium auditorium = new Auditorium(fileName);
		
		//display the report of the theater
		boolean displayInternalInfo = true;
		auditorium.display(displayInternalInfo);
	}

}
