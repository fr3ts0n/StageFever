/**
 * Fragment to display a song
 */
package com.fr3ts0n.stagefever;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author erwin
 *
 */
public class SongItemFragment
        extends Fragment
        implements SharedPreferences.OnSharedPreferenceChangeListener
{
	public static final int MSG_BPM_TICK = 1;
    public static final int MAX_NUMBER_BEATS = 2;

    /** settings strings */
    static final String SETTINGS_FONT_SIZE = "font_size_notes";
    static final String SETTINGS_METRONOME_VISIBLE = "metronome_visible";

	private SongAdapter songs;

	View thisView;
	TextView tvDescr;
	TextView tvArtist;
	TextView tvSettings;
	TextView tvNotes;
	ToggleButton btnBpm;
	Button btnNext;
	Button btnPrev;
	ProgressBar progBar;
	Button[] metronome;
    View loMetronome;

	/** Timer for display updates */
	private static Timer updateTimer = new Timer(true);

	private int beat = 0;

	/**
	 * Timer Task to cyclically update data screen
	 */
	private final class BpmUpdateTask extends TimerTask
	{
		@Override
		public void run()
		{
			/* forward message to update the view */
			Message msg = mHandler.obtainMessage(MSG_BPM_TICK);
			mHandler.sendMessage(msg);
		}
	}

	/**
	 * Handle message requests
	 */
	private transient final Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{

			switch (msg.what)
			{
				case MSG_BPM_TICK:
					btnBpm.toggle();
                    updateMetronome(btnBpm.isChecked());
					break;
			}
		}
	};

    /**
     * Update display of metronome
     */
    void updateMetronome(boolean isActive)
    {
        if(isActive)
        {
            // set all lights of metronome
            for(int i=0; i<MAX_NUMBER_BEATS; i++)
            {
                metronome[i].setPressed(i==beat);
            }
        }
        else
        {
            // increase count to next beat
            beat++;
            // if beat is max, restart with 0
            beat %= MAX_NUMBER_BEATS;
        }
    }

    /**
     * set all segments of metronome display to specified state
     * @param state state to be set
     */
    void setAllMetronomeSegments(boolean state)
    {
        // set all lights of metronome
        for(int i=0; i<MAX_NUMBER_BEATS; i++)
        {
            metronome[i].setPressed(state);
        }
        // reset beat number
        beat=0;
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		thisView = inflater.inflate(R.layout.fragment_song_item, container, false);

		tvDescr = (TextView) thisView.findViewById(R.id.title);
		tvArtist = (TextView) thisView.findViewById(R.id.artist);
		tvSettings = (TextView) thisView.findViewById(R.id.settings);
		tvNotes = (TextView) thisView.findViewById(R.id.notes);
		btnBpm = (ToggleButton) thisView.findViewById(R.id.bpm);
		btnPrev = (Button) thisView.findViewById(R.id.previous);
		btnNext = (Button) thisView.findViewById(R.id.next);
		progBar = (ProgressBar) thisView.findViewById(R.id.progressBar1);

        loMetronome = thisView.findViewById(R.id.metronome);
        metronome = new Button[]{
				(Button) thisView.findViewById(R.id.metronome0),
				(Button) thisView.findViewById(R.id.metronome1)
		};

		btnNext.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				setPosition(SongAdapter.position+1);
			}
		});

		btnPrev.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				setPosition(SongAdapter.position-1);
			}
		});

		btnBpm.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// cleanup from last updates
				updateTimer.cancel();
				btnBpm.setChecked(false);
                setAllMetronomeSegments(false);
			}
		});
		// get preferences
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        // register for later changes
        prefs.registerOnSharedPreferenceChangeListener(this);
        // set values from shared preferences
        onSharedPreferenceChanged(prefs, null);

		return thisView;
	}

	/**
	 * Update all display data fields
	 */
	private void updateFields()
	{
		if( thisView != null && songs != null && songs.getCount() > 0 )
		{
			Song song = songs.getItem(SongAdapter.position);

			if (song != null)
			{
				tvDescr.setText(song.title);
				tvArtist.setText(song.artist);
				tvSettings.setText(song.settings);
				tvNotes.setText(song.notes);
				setBpm(song.bpm);
			}
			btnNext.setText(SongAdapter.position < songs.getCount()-1 ? songs.getItem(SongAdapter.position + 1).title : "-");
			progBar.setMax(songs.getCount());
			progBar.setProgress(SongAdapter.position+1);
		}
	}

	/**
	 * Set BPM rate for display toggle
	 *
	 * @param newBpmRate
	 */
	public void setBpm(long newBpmRate)
	{
		// cleanup from last updates
		updateTimer.cancel();
		btnBpm.setChecked(false);
        setAllMetronomeSegments(false);

		// set new BPM values
		btnBpm.setText(String.valueOf(newBpmRate));
		btnBpm.setTextOn(String.valueOf(newBpmRate));
		btnBpm.setTextOff(String.valueOf(newBpmRate));
		if (newBpmRate > 0)
		{
			updateTimer = new Timer();
			updateTimer.schedule(new BpmUpdateTask(), 0, 60000 / 2 / newBpmRate);
		}
	}

	/**
	 * @return the song
	 */
	public Song getSong()
	{
		return songs.getItem(SongAdapter.position);
	}

	/**
	 * @param songs
	 *          the song adapter to set
	 */
	public void setSongs(SongAdapter songs)
	{
		this.songs = songs;
		setPosition(SongAdapter.position);
	}

	/**
	 * @return the SongAdapter.position
	 */
	public int getPosition()
	{
		return SongAdapter.position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(int position)
	{
		SongAdapter.position =
				songs != null
				 ? java.lang.Math.max( java.lang.Math.min(position, songs.getCount()-1), 0)
			   : 0;
		updateFields();
	}

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        // Font size of notes?
        if (key==null || SETTINGS_FONT_SIZE.equals(key))
        {
            tvNotes.setTextSize(
                    Float.valueOf(sharedPreferences.getString(SETTINGS_FONT_SIZE, "30")));
        }

        // Metronome visible?
        if (key==null || SETTINGS_METRONOME_VISIBLE.equals(key)) {
            loMetronome.setVisibility(
                    sharedPreferences.getBoolean(SETTINGS_METRONOME_VISIBLE, false)
                            ? View.VISIBLE : View.GONE);
        }
    }
}
