package com.fr3ts0n.stagefever;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

public class SongAdapter extends ArrayAdapter<Song>
{

	public SongAdapter(Context context, int resource, int textViewResourceId,
			List<Song> objects)
	{
		super(context, resource, textViewResourceId, objects);
	}

	/**
	 * Import model data from CSV file
	 *
	 * @param fileName
	 *          name of CSV file
	 * @param fieldDelimiter
	 *          delimiter sequence between data items
	 */
	public void importFromCsvFile(InputStream inStr, String fieldDelimiter)
	{
		String line;
		String[] elements;
		BufferedReader rdr;
		// clear song list
		clear();
		try
		{
			rdr = new BufferedReader(new InputStreamReader(inStr));

			while ((line = rdr.readLine()) != null)
			{
				elements = line.split(fieldDelimiter);
				add(new Song(elements));
			}
			rdr.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
