//Name: Isaac Blackwood
//Net ID: idb170030
package Tickets;

public enum YesOrNo 
{
	YES
	{
		public boolean toBoolean()
		{
			return true;
		}
	}, 
	NO
	{
		public boolean toBoolean()
		{
			return false;
		}
	};
	
	abstract public boolean toBoolean();
	public static YesOrNo toYesOrNo(String string) throws CannotConvertException, NullPointerException
	{
		if (null == string)
		{
			throw new NullPointerException();
		}
		string = string.toUpperCase();
		if (string.equals("YES") || string.equals("Y"))
		{
			return YES;
		}
		else if (string.equals("NO") || string.equals("N"))
		{
			return NO;
		}
		throw new CannotConvertException();
	}
	public static YesOrNo toYesOrNo(char ch) throws CannotConvertException
	{
		ch = Character.toUpperCase(ch);
		if ('Y' == ch)
		{
			return YES;
		}
		else if ('N' == ch)
		{
			return NO;
		}
		throw new CannotConvertException();
	}
}
