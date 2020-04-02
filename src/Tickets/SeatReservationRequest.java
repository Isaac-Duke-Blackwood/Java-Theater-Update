//Name: Isaac Blackwood
//Net ID: idb170030
package Tickets;

import java.util.InputMismatchException;
import java.util.Scanner;
import static java.lang.System.out;

import java.io.FileNotFoundException;

import Tickets.YesOrNo;

class SeatReservationRequest 
{
	private int row;
	private char startingSeat; //the leftmost seat of the selection
	private int adultTickets;
	private int childTickets;
	private int seniorTickets;
	private Auditorium auditorium;
	private static final double ADULT_PRICE = 10, CHILD_PRICE = 5, SENIOR_PRICE = 7.5;
	private static final char ADULT = 'A', CHILD = 'C', SENIOR = 'S';
	
	//constructors including a default for expansion purposes
	protected SeatReservationRequest(){}
	public SeatReservationRequest(int row, char startingSeat, int adultTickets, int childTickets, int seniorTickets, Auditorium auditorium) throws InvalidRowNumberException, InvalidSeatException, InvalidTicketNumberException
	{
		if (null == auditorium)
		{
			throw new NullPointerException();
		}
		auditorium(auditorium);
		row(row);
		startingSeat(startingSeat);
		adultTickets(adultTickets);
		childTickets(childTickets);
		seniorTickets(seniorTickets);
	}
	public SeatReservationRequest(Scanner userInput, Auditorium auditorium)
	{
		auditorium(auditorium);
		row(userInput);
		startingSeat(userInput);
		adultTickets(userInput);
		childTickets(userInput);
		seniorTickets(userInput);
	}
	public SeatReservationRequest(SeatReservationRequest seatReservationRequest, int row, char startingSeat) throws InvalidRowNumberException, InvalidSeatException, InvalidTicketNumberException //copy constructor that changes the row and starting seat
	{
			this(row, startingSeat, seatReservationRequest.adultTickets(), seatReservationRequest.childTickets(), seatReservationRequest.seniorTickets(), seatReservationRequest.auditorium());
	}
	
	//validate the seating request
	public boolean valid() throws EmptyOrderException
	{
		if (0 == totalTickets())
		{
			throw new EmptyOrderException();
		}
		try
		{	
			//move right over the selection and check if each seat in the user selection is available
			char seat = startingSeat;
			char lastSeatInSelection = endingSeat();
			while (seat <= lastSeatInSelection)
			{
				//get the current theaterSeat object and check if its reserved
				TheaterSeat theaterSeat = auditorium.get(row, seat);
				if (theaterSeat.reserved())
				{
					//one of the seats in the selection is occupied
					return false;
				}
				seat++;
			}
			//all the seats in the selection were empty and existed
			return true;
		}
		catch (TheaterSeatNotFoundException e)
		{
			//the selection went out of bounds
			return false;
		}
	}
	private boolean valid(TheaterSeat startingSeat)
	{
		try
		{	
			//move right over the selection and check if each seat in the user selection is available
			char seat = startingSeat.seat();
			int row = startingSeat.row();
			char lastSeatInSelection = endingSeat();
			while (seat <= lastSeatInSelection)
			{
				//get the current theaterSeat object and check if its reserved
				TheaterSeat theaterSeat = auditorium.get(row, seat);
				if (theaterSeat.reserved())
				{
					//one of the seats in the selection is occupied
					return false;
				}
				seat++;
			}
			//all the seats in the selection were empty and existed
			return true;
		}
		catch (TheaterSeatNotFoundException e)
		{
			//the selection went out of bounds
			return false;
		}
	}

	//change the seating request to the best available valid seats
	protected void changeToBest() throws InvalidRowNumberException, InvalidSeatException
	{
			//Get best seats in the theater
			SeatReservationRequest bestSeats = auditorium.bestSeats(this);
			if (null != bestSeats)
			{
				try
				{
					row(bestSeats.row());
					startingSeat(bestSeats.startingSeat());
				}
				catch (InvalidRowNumberException e)
				{
					//something went wrong while determining the best seats and an invalid row was passed back
					e.printStackTrace();
					throw e;
				}
				catch (InvalidSeatException e) 
				{
					//something went wrong while determining the best seats and an invalid starting seat was passed back
					e.printStackTrace();
					throw e;
				}
			}
		
	}
	protected boolean alternativeSeatsAvailable() //returns true if there are seats available
	{
		//starting at the first seat in the theater, iterate through the theater and check if any of the seats could be the starting seat
		TheaterSeat startingSeat = auditorium.first();
		while (null != startingSeat)
		{
			if (valid(startingSeat))
			{
				//there exists at least one set of alternative seats
				return true;
			}
			else
			{
				startingSeat = startingSeat.next();
			}
		}
		//all the seats have been tried and the theater is too full
		return false;
	}
	
	//reserve the seats
	private void reserve() throws InvalidTicketTypeException, TheaterSeatNotFoundException, FileNotFoundException, InvalidSelectionException, EmptyOrderException //used to make the reserve(Scanner) function shorter and more readable
	{
		if(!valid())
		{
			throw new InvalidSelectionException();
		}
		
		int adultTickets = this.adultTickets;
		int childTickets = this.childTickets;
		int seniorTickets = this.seniorTickets;
		char seat = startingSeat;
		char endingSeat = endingSeat();
		while (seat <= endingSeat)
		{
			TheaterSeat theaterSeat = auditorium.get(row, seat);
			if (adultTickets > 0)
			{
				theaterSeat.ticketType(ADULT);
				adultTickets--;
			}
			else if (childTickets > 0)
			{
				theaterSeat.ticketType(CHILD);
				childTickets--;
			}
			else if (seniorTickets > 0) 
			{
				theaterSeat.ticketType(SENIOR);
				seniorTickets--;
			}
			seat++;
		}
		//write the auditorium back to the file
		auditorium.writeToFile();
	}
	public void reserve(Scanner userInput) throws FileNotFoundException, TheaterSeatNotFoundException, InvalidTicketTypeException, InvalidRowNumberException, InvalidSeatException
	{
		try 
		{
			//reserve the seats in auditorium and write the auditorium back to the file
			reserve();
			
			//display confirmation
			out.println("Your seats have been reserved. Thank you, and enjoy the film!\n");
			out.print("ORDER SUMMARY: \n" + this);
		} 
		catch (EmptyOrderException e)
		{
			out.println("You requested 0 tickets.");
		}
		catch (InvalidSelectionException invalidSelectionException) 
		{
			out.println("We're sorry! One or more of the seats in your selection is unavailable.");
			if (alternativeSeatsAvailable())
			{
				//ask user to reserve best available and validate the input
				boolean inputValid = false;
				SeatReservationRequest bestSeats = auditorium.bestSeats(this);
				out.println("These seats are the best available:");
				out.println(bestSeats);
				out.println("Reserve best available? (Y/N)");
				YesOrNo choice = null;
				while (!inputValid)
				try 
				{
					choice = YesOrNo.toYesOrNo(userInput.next());
					inputValid = true;
				}
				catch (CannotConvertException e)
				{
					out.println("Please enter \'Y\' or \'N\'.");
				}
				catch (NullPointerException e)
				{
					//should be impossible as the input comes from console
					e.printStackTrace();
				}
				if (inputValid && choice.toBoolean())
				{
					//the user wants to reserve best available
					changeToBest();
					reserve(userInput);
				}
			}
			else
			{
				out.println("There are no alternative seats available in the theater for a group of size " + totalTickets());
			}
		}
		catch (TheaterSeatNotFoundException e)
		{
			//if the selection was valid this should never be thrown
			e.printStackTrace();
			throw e;
		} catch (InvalidTicketTypeException e) {
			//This should be impossible because the functions that throw this are being passed constants that should not cause this to be thrown.
			e.printStackTrace();
			throw e;
		}
	}

	//display the request
	@Override
	public String toString()
	{
		String string = null;
		if (totalTickets() > 1)
		{
			string =
				"Seats:                     " + row + startingSeat + "-" + row + endingSeat() + "\n" +
				"Number of adult tickets:   " + adultTickets + "\n" +
				"Number of child tickets:   " + childTickets + "\n" +
				"Number of senior tickets:  " + seniorTickets + "\n" +
				"Total Tickets:             " + totalTickets() + "\n" +
				"Price:                     $" + String.format("%.2f",price()) + "\n";
		}
		else
		{
			string =
					"Seat:                      " + row + startingSeat + "\n" +
					"Number of adult tickets:   " + adultTickets + "\n" +
					"Number of child tickets:   " + childTickets + "\n" +
					"Number of senior tickets:  " + seniorTickets + "\n" +
					"Total Tickets:             " + totalTickets() + "\n" +
					"Price:                     $" + String.format("%.2f",price()) + "\n";
		}
		return string;
	}
	
	//setters and getters for data members, including setters that prompt the user for keyboard input and verify it.
	public int row()
	{
		return row;
	}
	protected int row(int row) throws InvalidRowNumberException
	{
		if(auditorium.contains(row))
		{
			return this.row = row;
		}
		else 
		{
			throw new InvalidRowNumberException();
		}
	}
	private int row(Scanner userInput)
	{
		boolean validInput = false;
		int row = 0; //initialization is required for compilation but otherwise unnecessary 
		out.println("Select your row.");
		while(!validInput)
		{
			try 
			{
				row = userInput.nextInt();
				if(auditorium.contains(row))
				{
					validInput = true;
				}
				else
				{
					throw new InputMismatchException();
				}
			} 
			catch (InputMismatchException e) 
			{
				userInput.nextLine(); //advance past bad input
				out.println("You did not enter a valid row number.\nPlease try again.");
			}
		}
		return row;
	}
	public char startingSeat()
	{
		return startingSeat;
	}
	protected char startingSeat(char startingSeat) throws InvalidSeatException
	{
		if(auditorium.contains(startingSeat))
		{
			return this.startingSeat = startingSeat;
		}
		throw new InvalidSeatException();
	}
	private char startingSeat(Scanner userInput)
	{
		boolean validInput = false;
		char startingSeat = '\0'; //initialization is required for compilation but otherwise unnecessary 
		out.println("Select the letter of your starting seat. (That is the left-most seat)");
		while(!validInput)
		{
			try 
			{
				//check if user entered a character
				String string = userInput.next();
				if (string.length() != 1)
				{
					throw new InputMismatchException();
				}
				//check if the character is a valid one
				string = string.toUpperCase();
				startingSeat = string.charAt(0);
				if(auditorium.contains(startingSeat))
				{
					validInput = true;
				}
				else
				{
					throw new InputMismatchException();
				}
			} 
			catch (InputMismatchException e) 
			{
				userInput.nextLine(); //advance past bad input
				out.println("You did not enter a valid seat letter.\nPlease try again.");
			}
		}
		return startingSeat;
	}
	public char endingSeat() //returns letter of the rightmost seat in the selection
	{
		return (char)((int)startingSeat + totalTickets() - 1);
	}
	public int adultTickets()
	{
		return adultTickets;
	}
	protected int adultTickets(int adultTickets) throws InvalidTicketNumberException
	{
		if(adultTickets >= 0)
		{
			return this.adultTickets = adultTickets;
		}
		else 
		{
			throw new InvalidTicketNumberException();
		}
	}
	private int adultTickets(Scanner userInput)
	{
		boolean validInput = false;
		int adultTickets = -1; //initialization is required for compilation but otherwise unnecessary 
		out.println("How many adult tickets would you like?");
		while(!validInput)
		{
			try 
			{
				adultTickets = userInput.nextInt();
				if(adultTickets >= 0)
				{
					validInput = true;
					this.adultTickets = adultTickets;
				}
				else
				{
					throw new InputMismatchException();
				}
			} 
			catch (InputMismatchException e) 
			{
				userInput.nextLine(); //advance past bad input
				out.println("You did not enter a valid number of adult tickets.\nPlease try again.");
			}
		}
		return adultTickets;
	}
	public int childTickets() throws InvalidTicketNumberException
	{
		if(childTickets >= 0)
		{
			return childTickets;
		}
		else 
		{
			throw new InvalidTicketNumberException();
		}
	}
	protected int childTickets(int childTickets)
	{
		return this.childTickets = childTickets;
	}
	private int childTickets(Scanner userInput)
	{
		boolean validInput = false;
		int childTickets = -1; //initialization is required for compilation but otherwise unnecessary 
		out.println("How many child tickets would you like?");
		while(!validInput)
		{
			try 
			{
				childTickets = userInput.nextInt();
				if(childTickets >= 0)
				{
					validInput = true;
					this.childTickets = childTickets;
				}
				else
				{
					throw new InputMismatchException();
				}
			} 
			catch (InputMismatchException e) 
			{
				userInput.nextLine(); //advance past bad input
				out.println("You did not enter a valid number of child tickets.\nPlease try again.");
			}
		}
		return childTickets;
	}
	public int seniorTickets()
	{
		return seniorTickets;
	}
	protected int seniorTickets(int seniorTickets) throws InvalidTicketNumberException
	{
		if(seniorTickets >= 0)
		{
			return this.seniorTickets = seniorTickets;
		}
		else 
		{
			throw new InvalidTicketNumberException();
		}	
	}
	private int seniorTickets(Scanner userInput)
	{
		boolean validInput = false;
		int seniorTickets = -1; //initialization is required for compilation but otherwise unnecessary 
		out.println("How many senior tickets would you like?");
		while(!validInput)
		{
			try 
			{
				seniorTickets = userInput.nextInt();
				if(seniorTickets >= 0)
				{
					validInput = true;
					this.seniorTickets = seniorTickets;
				}
				else
				{
					throw new InputMismatchException();
				}
			} 
			catch (InputMismatchException e) 
			{
				userInput.nextLine(); //advance past bad input
				out.println("You did not enter a valid number of senior tickets.\nPlease try again.");
			}
		}
		return seniorTickets;
	}
	public int totalTickets()
	{
		return adultTickets + childTickets + seniorTickets;
	}
	public double price()
	{
		return (adultTickets * ADULT_PRICE) + (childTickets * CHILD_PRICE) + (seniorTickets * SENIOR_PRICE);
	}
	public Auditorium auditorium()
	{
		return auditorium;
	}
	protected Auditorium auditorium(Auditorium auditorium)
	{
		if (null == auditorium)
		{
			throw new NullPointerException();
		}
		return this.auditorium = auditorium;
	}
}
