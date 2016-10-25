package com.fr3ts0n.stagefever;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class SongAdapter extends ArrayAdapter<Song>
{
	static int position = 0;

	public SongAdapter(Context context, int resource, int textViewResourceId,
			List<Song> objects)
	{
		super(context, resource, textViewResourceId, objects);
	}

	/**
	 * Import model data from CSV file
	 *
	 * @param inStr
	 *          input stream of CSV file
	 * @param fieldDelimiter
	 *          delimiter sequence between data items
	 */
	public void importFromCsvFile(InputStream inStr, String fieldDelimiter)
	{
		BufferedReader rdr;
		// clear song list
		clear();
		try
		{
			rdr = new BufferedReader(new InputStreamReader(inStr));
			for(CSVRecord record : CSVFormat.newFormat(fieldDelimiter.charAt(0)).parse(rdr))
			{
				add(new Song(record));
			}
			rdr.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
