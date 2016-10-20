/**
 * Fragment to display a song
 */
package com.fr3ts0n.stagefever;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * @author erwin
 *
 */
public class SongItemFragment extends Fragment
{
	public static final int MSG_BPM_TICK = 1;

	private SongAdapter songs;

	private View thisView;
	private TextView tvDescr;
	private TextView tvArtist;
	private TextView tvSettings;
	private TextView tvNotes;
	private ToggleButton btnBpm;
	private Button btnNext;
	private Button btnPrev;
	private ProgressBar progBar;

	/** Timer for display updates */
	private static Timer updateTimer = new Timer(true);

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
					break;
			}
		}
	};

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
			}
		});

		updateFields();

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

}
