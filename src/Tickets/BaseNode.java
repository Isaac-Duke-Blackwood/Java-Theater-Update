//Name: Isaac Blackwood
//Net ID: idb170030
package Tickets;

abstract class BaseNode 
{
	//data members to house the location of the seat in the auditorium, and its status
	private int row;
	private char seat;
	private boolean reserved;
	private char ticketType; //project description requires this to be a char, but it would be better as an enum
	private static final char ADULT = 'A', CHILD = 'C', SENIOR = 'S', EMPTY = '.';
	private static final int ASCII_A = 65, ASCII_Z = 90;
	
	protected BaseNode() //default constructor for future expansion
	{
		ticketType = EMPTY;
	}
	BaseNode(int row, char seat, boolean reserved, char ticketType) throws InvalidRowNumberException, InvalidSeatException, InvalidTicketTypeException
	{
		row(row);
		seat(seat);
		reserved(reserved);
		ticketType(ticketType);
	}
	//Object method overrides
	@Override
	public boolean equals(Object object) //return true if the row and seat are the same
	{
		if (null == object)
		{
			return false;
		}
		if (!(object instanceof BaseNode))
		{
			return false;
		}
		//the passed object is indeed a non-null instance of the correct class.
		BaseNode passedBaseNode = (BaseNode) object;
		if (this.row != passedBaseNode.row())
		{
			return false;
		}
		if (this.seat != passedBaseNode.seat())
		{
			return false;
		}
		if (this.reserved != passedBaseNode.reserved())
		{
			return false;
		}
		if (this.ticketType != passedBaseNode.ticketType())
		{
			return false;
		}
		//the passed node is equal in value to this one
		return true;
	}	
	
	public String toString()
	{
		if (validTicketType())
		{
			return Character.toString(ticketType);
		}
		return "[Invalid Ticket Type]";
	}
	
	protected boolean validTicketType()
	{
		if (ADULT == ticketType || CHILD == ticketType || SENIOR == ticketType || EMPTY == ticketType)
		{
			return true;
		}
		return false;
	}
	
	//Accessors
	public int row()
	{
		return row;
	}
	public char seat() 
	{
		return seat;
	}
	public boolean reserved()
	{
		return reserved;
	}
	public char ticketType() 
	{
		return ticketType;
	}
	
	//Mutators
	protected int row(int row) throws InvalidRowNumberException
	{
		if (row < 1)
		{
			throw new InvalidRowNumberException();
		}
		return this.row = row;
	}
	protected char seat(char seat) throws InvalidSeatException
	{
		if (seat < ASCII_A || seat > ASCII_Z)
		{
			throw new InvalidSeatException();
		}
		return this.seat = seat;
	}
	protected boolean reserved(boolean reserved)
	{
		return this.reserved = reserved;
	}
	protected char ticketType(char ticketType) throws InvalidTicketTypeException //if the passed value is not 'A', 'C', 'S' or '.'
	{
		if (ADULT == ticketType || CHILD == ticketType || SENIOR == ticketType || EMPTY == ticketType)
		{
			return this.ticketType = ticketType;
		}
		else
		{
			throw new InvalidTicketTypeException();
		}
	}
}
