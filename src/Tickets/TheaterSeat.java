//Name: Isaac Blackwood
//Net ID: idb170030
package Tickets;

class TheaterSeat extends BaseNode
{
	private TheaterSeat up;
	private TheaterSeat down;
	private TheaterSeat left;
	private TheaterSeat right;

	//constructors
	protected TheaterSeat(){}//default constructor for future expansion
	protected TheaterSeat(int row, char seat, boolean reserved, char ticketType) throws InvalidRowNumberException, InvalidSeatException, InvalidTicketTypeException 
	{
		super(row, seat, reserved, ticketType);
	}
	TheaterSeat(int row, char seat, boolean reserved, char ticketType, TheaterSeat up, TheaterSeat down, TheaterSeat left, TheaterSeat right) throws InvalidRowNumberException, InvalidSeatException, InvalidTicketTypeException
	{
		this(row, seat, reserved, ticketType);
		this.up = up;
		this.down = down;
		this.left = left;
		this.right = right;
	}

	//Accessors
	protected TheaterSeat up()
	{
		return up;
	}
	protected TheaterSeat down()
	{
		return down;
	}
	protected TheaterSeat left()
	{
		return left;
	}
	protected TheaterSeat right()
	{
		return right;
	}
	protected TheaterSeat next() //returns the node to the right of the seat or the seat at the beginning of the next row (wraps to next row) returns null if neither exist
	{
		if (null != right)
		{
			return right;
		}
		TheaterSeat next = this;
		while (null != next.left())
		{
			//back up to the start of the row
			next = next.left();
		}
		//return the start of the next row of null if it doesn't exist
		next = next.down();
		return next;
	}
	protected boolean hasNext()
	{
		if (null != next())
		{
			return true;
		}
		return false;
	}
	protected TheaterSeat prev() //returns the node to the left of the seat or at the end of the previous row or null if neither exist
	{
		if (null != left)
		{
			return left;
		}
		//step up to the previous row
		TheaterSeat next = this.up;
		if (null != next)
		{
			while (null != next.right())
			{
				// advance to the end of the row
				next = next.right();
			}
		}
		return next;
	}
	protected boolean hasPrev()
	{
		if (null != prev())
		{
			return true;
		}
		return false;
	}
	
	//Mutators
	protected TheaterSeat up(TheaterSeat up)
	{
		return this.up = up;
	}
	protected TheaterSeat down(TheaterSeat down)
	{
		return this.down = down;
	}
	protected TheaterSeat left(TheaterSeat left)
	{
		return this.left = left;
	}
	protected TheaterSeat right(TheaterSeat right)
	{
		return this.right = right;
	}
}
