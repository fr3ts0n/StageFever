package com.fr3ts0n.stagefever;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends Activity
		implements
		NavigationDrawerFragment.NavigationDrawerCallbacks
{
	// activity request responses
	static final int REQUEST_SELECT_FILE = 1;
	static final int REQUEST_SETTINGS = 2;

	/** Timeout for exiting via BACK key */
	private static final int EXIT_TIMEOUT = 2500;
	/** last time of back key pressed */
	private static long lastBackPressTime = 0;
	/** toast for showing exit message */
	static Toast exitToast = null;

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/** song display handler */
	static final SongItemFragment songItemFragment = new SongItemFragment();

	/** Song data adapter */
	static SongAdapter songs;

	/**
	 * Used to store the last screen title. For use in {@link #restoreActionBar()}
	 * .
	 */
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_main);

		mTitle = getTitle();

		mNavigationDrawerFragment = (NavigationDrawerFragment)
				getFragmentManager().findFragmentById(R.id.navigation_drawer);

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(
				R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));

		// initialize song data
		if(songs == null)
		{
			songs = new SongAdapter(getApplicationContext(),
															android.R.layout.simple_selectable_list_item,
															0,
															new ArrayList<Song>());
			try
			{
				songs.importFromCsvFile(getApplicationContext().getAssets().open("SetList.csv"), ";");
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		songItemFragment.setSongs(songs);
		mNavigationDrawerFragment.setAdapter(songs);
	}

	@Override
	public void onNavigationDrawerItemSelected(int position)
	{
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.container, songItemFragment).commit();

		songItemFragment.setPosition(position);
	}

	@SuppressWarnings("deprecation")
	public void restoreActionBar()
	{
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		if (!mNavigationDrawerFragment.isDrawerOpen())
		{
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id)
		{
			case R.id.import_csv:
				selectFileToLoad();
				break;

			case R.id.action_settings:
				// Launch the BtDeviceListActivity to see devices and do scan
				Intent settingsIntent = new Intent(this, SettingsActivity.class);
				startActivityForResult(settingsIntent, REQUEST_SETTINGS);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Select file to be loaded
	 */
	public void selectFileToLoad()
	{
		File file = new File(FileHelper.getPath(this));
		if (!file.exists())
		{
			file.mkdirs();
			file.mkdir();
		}
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		Uri data = Uri.fromFile(file);
		intent.setDataAndType(data, "text/*");
		startActivityForResult(intent, REQUEST_SELECT_FILE);
	}

	/**
	 * Handler for result messages from other activities
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode)
		{
			case REQUEST_SELECT_FILE:
				if (resultCode == RESULT_OK)
				{
					// Get the Uri of the selected file
					Uri uri = data.getData();
					Log.d("CSV import", "Load content: " + uri);
					// load data ...
					try
					{
						songs.importFromCsvFile(getContentResolver().openInputStream(uri),";");
					} catch (FileNotFoundException e)
					{
						Log.e("CSV import", e.getLocalizedMessage(), e);
					}
					songItemFragment.setSongs(songs);
				}
				break;
		}
	}

	/**
	 * handle pressing of the BACK-KEY
	 */
	@Override
	public void onBackPressed()
	{
		if (lastBackPressTime < System.currentTimeMillis() - EXIT_TIMEOUT)
		{
			exitToast = Toast.makeText(this, R.string.back_again_to_exit, Toast.LENGTH_SHORT);
			exitToast.show();
			lastBackPressTime = System.currentTimeMillis();
		} else
		{
			if (exitToast != null)
			{
				exitToast.cancel();
			}
			super.onBackPressed();
		}
	}
}
