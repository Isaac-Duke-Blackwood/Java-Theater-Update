//Name: Isaac Blackwood
//Net ID: idb170030
package Tickets;

import java.util.NoSuchElementException;
import java.util.Scanner;
import static java.lang.System.out;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.Math.abs;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Auditorium 
{
	private static final double ADULT_PRICE = 10, CHILD_PRICE = 5, SENIOR_PRICE = 7.5;
	private static final char ADULT = 'A', CHILD = 'C', SENIOR = 'S', EMPTY = '.', OCCUPIED = '#';
	private static final int ASCII_A = 65, ZERO = 0;
	private TheaterSeat first;
	private String fileName;
	
	//constructors
	protected Auditorium(){}//default constructor for future expansion
	public Auditorium(String fileName) throws FileNotFoundException, SecurityException, InvalidTicketTypeException, InvalidRowNumberException, InvalidSeatException
	{
		try 
		{
			//Try to open the file
			this.fileName = fileName;
			File file = new File(fileName);
		
			//scan the file and fill the auditorium with TheaterSeat objects
			
			Scanner fileScanner = new Scanner(file);
			try 
			{
				int row = 0; //stores the actual row number starting from 1
				while(fileScanner.hasNext())
				{
					row++;
					//read in the next line of the file
					String currentLine = fileScanner.nextLine();
					
					//iterate through each "seat" represented by a single character of the string and create a TheaterSeat for it (link as we go)
					for(int index = 0; index < currentLine.length(); index++)
					{
						//get the info about the seat
						char currentChar = currentLine.charAt(index);
						char seat = (char)(index + ASCII_A);
						char ticketType = checkTicketType(currentChar, row, seat);
						boolean reserved = (currentChar != EMPTY);
						
						//create the seat
						TheaterSeat currentTheaterSeat = new TheaterSeat(row, seat, reserved, ticketType);
					
						//set the pinter to left and up for each TheaterSeat and then doubly link it
						if(1 == row && 'A' == seat)
						{
							//if the seat is the first one, set it as first (all TheaterSeat pointers should be pointed to null)
							first = currentTheaterSeat;
						}
						else if ('A' == seat)
						{
							//if the seat is the first in the column, doubly link up and leave the rest as null
							TheaterSeat upFromCurrent = currentTheaterSeat.up(get(row - 1, seat));
							upFromCurrent.down(currentTheaterSeat);
						}
						else if (1 == row)
						{
							//if the seat is in the first row, set up as null
							TheaterSeat leftFromCurrent = currentTheaterSeat.left(get(row , (char)((int)seat - 1)));
							leftFromCurrent.right(currentTheaterSeat);
						}
						else
						{
							//if the seat is anywhere else
							TheaterSeat upFromCurrent = currentTheaterSeat.up(get(row - 1, seat));
							upFromCurrent.down(currentTheaterSeat);
							TheaterSeat leftFromCurrent = currentTheaterSeat.left(get(row , (char)((int)seat - 1)));
							leftFromCurrent.right(currentTheaterSeat);
						}			
					}
				}
			}
			catch (TheaterSeatNotFoundException e)
			{
				//this should never happen unless I messed up
				e.printStackTrace();
			}
			catch (NoSuchElementException e)
			{
				//the file has ended
			}
			catch (InvalidTicketTypeException e) 
			{
				//one of the seats was bad
				out.println("The character in the seating chart at row " + e.row() + " and seat " + e.seat() + " was invalid. Please fix the file and try again.");
				throw e;
			}
			catch (InvalidRowNumberException e)
			{
				//this should be impossible, and would likely mean the loop design is wrong
				e.printStackTrace();
				throw e;
			}
			catch (InvalidSeatException e)
			{
				//this should be impossible, and would likely mean the loop design is wrong
				e.printStackTrace();
				throw e;
			}
			
			//close resources
			fileScanner.close();
		}
		catch (FileNotFoundException e) 
		{
			//The file was not found and could'nt be opened for input
			out.println("Seating chart could not be found.\nPlease check that the file is named \"A1.txt\" and located in the proper directory.");
			throw e;
		}
		catch (SecurityException e)
		{
			//The file could not be opened for security reasons
			out.println("The seating chart could not be opened for security reasons.");
			throw e;
		}		
	}

	//create a seating request
	public SeatReservationRequest createSeatReservationRequest(int row, char startingSeat, int adultTickets, int childTickets, int seniorTickets) throws InvalidRowNumberException, InvalidSeatException, InvalidTicketNumberException
	{
		return new SeatReservationRequest(row, startingSeat, adultTickets, childTickets, seniorTickets, this);
	}
	public SeatReservationRequest createSeatReservationRequest(Scanner userInput)
	{
		return new SeatReservationRequest(userInput, this);
	}
	
	//Display functions
	@Override
	public String toString()
	{
		//Initialize the empty string
		StringBuilder stringBuilder = new StringBuilder();
		
		//add each line 
		TheaterSeat theaterSeat = first;
		while (null != theaterSeat)
		{
			stringBuilder.append(theaterSeat);
			if (theaterSeat.hasNext())
			{
				boolean newRow = (theaterSeat.row() != theaterSeat.next().row());
				if (newRow)
				{
					stringBuilder.append("\n");
				}
			}
			theaterSeat = theaterSeat.next();
		}
		return stringBuilder.toString();
	}
	public void display(boolean displayInternalInfo)
	{
		if (displayInternalInfo)
		{
			out.print(fullDisplay());
		}
		else
		{
			out.print(menuDisplay());
		}
	}
	protected String fullDisplay()
	{
		if (null == first)
		{
			return null;
		}
		//Initialize the empty string
		StringBuilder display = new StringBuilder();
		
		//add the spaces before the header
		int spaces = digitsInMaxRowNumber();
		for (int index = 0; index <= spaces; index++) 
		{
			display.append(' ');
		}
		
		//add the header
		int asciiLimit = seatsPerRow() + ASCII_A;
		for (int ascii = 65; ascii < asciiLimit; ascii++)
		{
			char letter = (char)ascii;
			display.append(letter);
		}
		display.append('\n');
		
		//add each line 
		int maxRow = rows();
		for (int row = 1; row <= maxRow; row++)
		{
			//add the number and spaces at the left of the row
			//add spaces
			spaces = digitsInMaxRowNumber() - Integer.toString(row).length();
			for (int index = 0; index < spaces - 1; index++)
			{
				display.append(' ');
			}
			//add row number and one more space
			display.append(row + " ");
			
			//add the actual theater seats now
			try 
			{
			TheaterSeat theaterSeat = get(row, 'A');
			while (theaterSeat != null)
			{
				display.append(theaterSeat.ticketType());
				theaterSeat = theaterSeat.right();
			}
			display.append('\n');
			}
			catch (TheaterSeatNotFoundException e)
			{
				//this should be impossible if the theater has any seats and the loop is correct
				e.printStackTrace();
			}
		}
		//append the total tickets sold of each type and the money earned
		display.append(	"Total Seats in Auditorium: " + totalSeats() + "\n" +
						"Total Tickets Sold:        " + totalTicketsSold() + "\n" +
						"Adult Tickets Sold:        " + adultTicketsSold() + "\n" +
						"Child Tickets Sold:        " + childTicketsSold() + "\n" +
						"Senior Tickets Sold:       " + seniorTicketsSold() + "\n" +
						"Total Ticket Sales:        $" + String.format("%.2f", totalEarnings()) + "\n");
		
		return display.toString();
	}
	protected String menuDisplay()
	{
		if (null == first)
		{
			return null;
		}
		//Initialize the empty string
		StringBuilder display = new StringBuilder();
		
		//add the spaces before the header
		int spaces = digitsInMaxRowNumber();
		for (int index = 0; index <= spaces; index++) 
		{
			display.append(' ');
		}
		
		//add the header
		int asciiLimit = seatsPerRow() + ASCII_A;
		for (int ascii = 65; ascii < asciiLimit; ascii++)
		{
			char letter = (char)ascii;
			display.append(letter);
		}
		display.append('\n');
		
		//add each line 
		int maxRow = rows();
		for (int row = 1; row <= maxRow; row++)
		{
			//add the number and spaces at the left of the row
			//add spaces
			spaces = digitsInMaxRowNumber() - Integer.toString(row).length();
			for (int index = 0; index < spaces - 1; index++)
			{
				display.append(' ');
			}
			//add row number and one more space
			display.append(row + " ");
			
			//add the actual theater seats now
			try 
			{
			TheaterSeat theaterSeat = get(row, 'A');
			while (theaterSeat != null)
			{
				if (!theaterSeat.reserved())
				{
					display.append(EMPTY);
				}
				else
				{
					display.append(OCCUPIED);
				}
				theaterSeat = theaterSeat.right();
			}
			display.append('\n');
			}
			catch (TheaterSeatNotFoundException e)
			{
				//this should be impossible if the theater has any seats and the loop is correct
				e.printStackTrace();
			}
		}
		
		//display a helpful key
		display.append("# = reserved, . = available\nLower row numbers are near the screen.\n\n");
		
		return display.toString();
	}
	protected double totalEarnings()
	{
		return (adultTicketsSold() * ADULT_PRICE) + (childTicketsSold() * CHILD_PRICE) + (seniorTicketsSold() * SENIOR_PRICE);
	}
	protected void writeToFile() throws FileNotFoundException
	{
		File file = new File(fileName);
		PrintWriter printWriter = new PrintWriter(file);
		printWriter.print(this);
		printWriter.close();
	}
	
	//check if the passed char represents a valid ticket type or an empty seat. Otherwise, throw an invalid ticket exception
	private char checkTicketType(char ticketType, int row, char seat) throws InvalidTicketTypeException
	{
		if (ADULT == ticketType || CHILD == ticketType || SENIOR == ticketType || EMPTY == ticketType)
		{
			return ticketType;
		}
		throw new InvalidTicketTypeException(row, seat);
	}
	
	//searching and sizing the list
	protected TheaterSeat first()
	{
		return first;
	}
	protected int totalTicketsSold()
	{
		return adultTicketsSold() + childTicketsSold() + seniorTicketsSold();
	}
	protected int unreservedSeats()
	{
		int unreservedSeats = 0;
		TheaterSeat theaterSeat = first;
		while (null != theaterSeat)
		{
			if (!theaterSeat.reserved())
			{
				unreservedSeats++;
			}
			theaterSeat = theaterSeat.next();
		}
		return unreservedSeats;
	}
	protected int adultTicketsSold()
	{
		int adultTickets = 0;
		TheaterSeat theaterSeat = first;
		while (null != theaterSeat)
		{
			if (ADULT == theaterSeat.ticketType())
			{
				adultTickets++;
			}
			theaterSeat = theaterSeat.next();
		}
		return adultTickets;
	}
	protected int childTicketsSold()
	{
		int childTickets = 0;
		TheaterSeat theaterSeat = first;
		while (null != theaterSeat)
		{
			if (CHILD == theaterSeat.ticketType())
			{
				childTickets++;
			}
			theaterSeat = theaterSeat.next();
		}
		return childTickets;
	}
	protected int seniorTicketsSold()
	{
		int seniorTickets = 0;
		TheaterSeat theaterSeat = first;
		while (null != theaterSeat)
		{
			if (SENIOR == theaterSeat.ticketType())
			{
				seniorTickets++;
			}
			theaterSeat = theaterSeat.next();
		}
		return seniorTickets;
	}
	public TheaterSeat get(int row, char seat) throws TheaterSeatNotFoundException
	{
		//check to see if the passed row and seat are indeed inbounds
		if (row < 1 || row > rows())
		{
			throw new TheaterSeatNotFoundException();
		}
		if (seat < 'A' || seat > lastSeatLetter())
		{
			throw new TheaterSeatNotFoundException();
		}
		
		//seat is inbounds, so traverse the auditorium and return a reference to the TheaterSeat
		TheaterSeat iterator = first;
		for (int currentRow = 1; currentRow < row; currentRow++)
		{
			iterator = iterator.down();
		}
		for (char currentSeat = ASCII_A; currentSeat < seat; currentSeat++)
		{
			iterator = iterator.right();
		}
		return iterator;
	}
	public int rows() //returns the number of rows in the auditorium by moving straight down from first
	{
		int rows = 0;
		TheaterSeat currentTheaterSeat = first;
		while (currentTheaterSeat != null)
		{
			rows++;
			currentTheaterSeat = currentTheaterSeat.down();
		}
		return rows;
	}
	public int seatsPerRow() //returns the number of seats in each row by moving straight down from first
	{
		int seatsPerRow = 0;
		TheaterSeat currentTheaterSeat = first;
		while (currentTheaterSeat != null)
		{
			seatsPerRow++;
			currentTheaterSeat = currentTheaterSeat.right();
		}
		return seatsPerRow;
	}
	public int totalSeats() //returns the number of seats in the auditorium by multiplying the number of seats in each row by the number of rows
	{
		return rows() * seatsPerRow();
	}
	public char lastSeatLetter() //returns the letter of the last seat in each row
	{
		return (char)(seatsPerRow() - 1 + ASCII_A);
	}
	private int digitsInMaxRowNumber() //returns the number of digits in the last row number in the theater
	{
		return Integer.toString(rows()).length();
	}
	public boolean contains(int row)
	{
		if (row >= 1 && rows() >= row)
		{
			return true;
		}
		return false;
	}
	public boolean contains(char seat)
	{
		if (seat >= ASCII_A && lastSeatLetter() >= seat)
		{
			return true;
		}
		return false;
	}
	public boolean contains(int row, char seat)
	{
		return (contains(row) && contains(seat));
	}
	public SeatReservationRequest bestSeats(SeatReservationRequest seatReservationRequest) //creates a new SeatReservationRequest with the best seats available for the number of tickets 
	{
		//determine the center of the auditorium
		final double CENTER_ROW = ((rows() + 1) / 2.0d); //this gets either the middle row if an odd number of rows, or the middle of the two middle rows if an even number of rows
		final double CENTER_SEAT = (((seatsPerRow() + 1) / 2.0d) + ASCII_A); //this gets either the number corresponding to the ascii character in the middle seat letter, or the number in between the two 
		
		//step through the auditorium and test every valid seating alternative to see which is the closest to the center
		SeatReservationRequest bestSeats = null;
		TheaterSeat startingSeat = first;
		double shortestDistanceFromCenter = Double.POSITIVE_INFINITY; //this is an impossible value for distance and is only here as a sentinel value. If the theater is absolutely huge it might make the program break idk.
		double shortestDistanceFromCenterRow = Double.POSITIVE_INFINITY; //this too
		while (null != startingSeat)
		{
			try
			{
				//create a seating request with the current starting seat
				SeatReservationRequest newSeats = new SeatReservationRequest(seatReservationRequest, startingSeat.row(), startingSeat.seat());
			
				//if the new request is valid, then calculate the distance between the center of the selection and the center of the auditorium
				if (newSeats.valid())
				{
					//calculate the center of the selection
					final double SELECTION_CENTER_ROW = newSeats.row();
					final double SELECTION_CENTER_SEAT = (((newSeats.totalTickets() + 1) / 2.0d) + (double)newSeats.startingSeat() - 1);
					
					//calculate the distance from the center of auditorium
					final double DISTANCE_FROM_CENTER = sqrt(pow(CENTER_ROW - SELECTION_CENTER_ROW, 2) + pow(CENTER_SEAT - SELECTION_CENTER_SEAT, 2));
					
					if (DISTANCE_FROM_CENTER < shortestDistanceFromCenter)
					{
						//these are the new best alternative seats
						bestSeats = newSeats;
						shortestDistanceFromCenter = DISTANCE_FROM_CENTER;
						final double DISTANCE_FROM_CENTER_ROW = abs(CENTER_ROW - SELECTION_CENTER_ROW);
						shortestDistanceFromCenterRow = DISTANCE_FROM_CENTER_ROW;
					}
					else if (DISTANCE_FROM_CENTER == shortestDistanceFromCenter)
					{
						final double DISTANCE_FROM_CENTER_ROW = abs(CENTER_ROW - SELECTION_CENTER_ROW);
						if (DISTANCE_FROM_CENTER_ROW < shortestDistanceFromCenterRow)
						{
							//these are the new best alternative seats (if there is a tie for the closest row, the loop will automatically keep the one with the smaller row number
							bestSeats = newSeats;
							shortestDistanceFromCenter = DISTANCE_FROM_CENTER;
							shortestDistanceFromCenterRow = DISTANCE_FROM_CENTER_ROW;
						}
					}
				}
			}
			catch (TicketsPackageDataMemberException e) 
			{
				//the current seat reservation request is bad so obviously the request wont be valid
			} 
			catch (EmptyOrderException e) 
			{
				//The order is empty so just return null
				return null;
			}
			finally 
			{
				startingSeat = startingSeat.next();
			}
		}
		return bestSeats;
	}
}
