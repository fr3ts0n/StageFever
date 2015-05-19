package com.fr3ts0n.stagefever;

public class Song
{
	public String title = "";
	public String artist = "";
	public String settings = "";
	public String notes = "";
	public int bpm = 0;

	/**
	 * construct Song from array of elements
	 * @param elements array of elements
	 * in following order of fields:
	 * { title, artist, settings, String.valueOf(bpm), notes }
	 */
	public Song(String[] elements)
	{
		if (elements.length >= 5)
		{
			title  = elements[0];
			artist = elements[1];
			settings = elements[2];
			notes = elements[4];
			try
			{
				bpm = Integer.valueOf(elements[3]).intValue();
			} catch (NumberFormatException ex)
			{
				bpm = 0;
			}
		}
	}

	/**
	 * get elements as array of strings
	 * in following order of fields:
	 * { title, artist, settings, String.valueOf(bpm), notes }
	 * @return
	 */
	public String[] getElements()
	{
		String[] result = { title, artist, settings, String.valueOf(bpm), notes };
		return result;
	}

	@Override
	public String toString()
	{
		return title + "\n" + artist;
	}
}
