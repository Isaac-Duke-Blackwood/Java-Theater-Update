//Name: Isaac Blackwood
//Net ID: idb170030
package Tickets;

public class InvalidTicketTypeException extends TicketsPackageDataMemberException 
{
	private static final long serialVersionUID = -786920500990637373L;
	private int row = 0;
	private char seat = '\0';

	public InvalidTicketTypeException() {}
	public InvalidTicketTypeException(int row, char seat)
	{
		this.row = row;
		this.seat = seat;
	}

	public String row()
	{
		if (0 == row)
		{
			return "_unknown_row_";
		}
		return Integer.toString(row);
	}
	public String seat() 
	{
		if ('\0' == seat)
		{
			return "_unknown_seat_";
		}
		return Character.toString(seat);
	}
}
