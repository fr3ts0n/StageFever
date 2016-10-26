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
     * CSV field ids
     */
    public enum FID
    {
        TITLE,
        ARTIST,
        SETTINGS,
        BPM,
        NOTES
    }

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
            title = elements[FID.TITLE.ordinal()];
            artist = elements[FID.ARTIST.ordinal()];
            settings = elements[FID.SETTINGS.ordinal()];
            try
            {
                bpm = Integer.valueOf(elements[FID.BPM.ordinal()]);
            }
            catch (NumberFormatException ex)
            {
                bpm = 0;
            }
            notes = elements[FID.NOTES.ordinal()];
        }
        catch (Exception ex)
        {
            Log.e(getClass().getName(), ex.getMessage() + Arrays.toString(elements));
        }
    }

    /**
     * Construct song from a CSV record
     * @param record CSV data record
     */
    public Song(CSVRecord record)
    {
        try
        {
            title = record.get(FID.TITLE.ordinal());
            artist = record.get(FID.ARTIST.ordinal());
            settings = record.get(FID.SETTINGS.ordinal());
            try
            {
                bpm = Integer.valueOf(record.get(FID.BPM.ordinal()));
            }
            catch (NumberFormatException ex)
            {
                bpm = 0;
            }
            notes = record.get(FID.NOTES.ordinal());
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
