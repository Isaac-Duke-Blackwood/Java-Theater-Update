//Name: Isaac Blackwood
//Net ID: idb170030
package Tickets;

import java.util.Scanner;

public class Main 
{
	private static final Scanner USER_INPUT = new Scanner(System.in);
	private static final String FILE_NAME = "A1.txt";

	public static void main(String[] args) 
	{		
		Menu.display(USER_INPUT, FILE_NAME);
	}
}
