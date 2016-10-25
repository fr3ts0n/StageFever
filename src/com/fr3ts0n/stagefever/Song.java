package com.fr3ts0n.stagefever;

import android.util.Log;

import org.apache.commons.csv.CSVRecord;

import java.util.Arrays;

public class Song {
    String title = "";
    String artist = "";
    String settings = "";
    String notes = "";
    int bpm = 0;

    /**
     * construct Song from array of elements
     *
     * @param elements array of elements
     *                 in following order of fields:
     *                 { title, artist, settings, String.valueOf(bpm), notes }
     */
    public Song(String[] elements)
    {
        try
        {
            title = elements[0];
            artist = elements[1];
            settings = elements[2];
            try
            {
                bpm = Integer.valueOf(elements[3]);
            }
            catch (NumberFormatException ex)
            {
                bpm = 0;
            }
            notes = elements[4];
        }
        catch (Exception ex)
        {
            Log.e(getClass().getName(), ex.getMessage() + Arrays.toString(elements));
        }
    }

    public Song(CSVRecord record)
    {
        try
        {
            title = record.get(0);
            artist = record.get(1);
            settings = record.get(2);
            try
            {
                bpm = Integer.valueOf(record.get(3));
            }
            catch (NumberFormatException ex)
            {
                bpm = 0;
            }
            notes = record.get(4);
        }
        catch (Exception ex)
        {
            Log.e(getClass().getName(), ex.getMessage() + record.toString());
        }
    }

    /**
     * get elements as array of strings
     * in following order of fields:
     * { title, artist, settings, String.valueOf(bpm), notes }
     *
     * @return array of elements
     */
    public String[] getElements() {
        return new String[]{title, artist, settings, String.valueOf(bpm), notes};
    }

    @Override
    public String toString() {
        return title + "\n" + artist;
    }
}
