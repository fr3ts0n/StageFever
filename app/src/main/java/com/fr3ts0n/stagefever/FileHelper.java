/*
 * Copyright (C) 2014 Erwin Scheuch-Heilig
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fr3ts0n.stagefever;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/**
 * Task to save measurements
 *
 * @author Erwin Scheuch-Heilig
 *
 */
class FileHelper
{
	/** Date Formatter used to generate file name */
	private static final SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss");
	private static ProgressDialog progress;

	/**
	 * get default path for load/store operation
	 * * path is based on configured <user data location>/<package name>
	 * @return default path for current app context
	 */
	public static String getPath(Context context)
	{
		// generate file name
		return Environment.getExternalStorageDirectory()
				+ File.separator
				+ context.getPackageName();
	}

	/**
	 * get filename (w/o extension) based on current date & time
	 * @return file name
	 */
	public static String getFileName()
	{
		return dateFmt.format(System.currentTimeMillis());
	}


	/**
	 * Save all data in a independent thread
	 */
	public static void saveDataThreaded( final Context context,
			                                 final Object objToSave,
			                                 String fileExtension)
	{
		// generate file name
		final String mPath = getPath(context);
		final String mFileName = mPath
					+ File.separator
					+ getFileName()
					+ fileExtension;

		// create progress dialog
		progress = ProgressDialog.show(	context,
																		context.getString(R.string.saving_data),
																		mFileName,
																		true);

		Thread saveTask = new Thread()
		{
			public void run()
			{
				Looper.prepare();
				saveData(context, mPath, mFileName, objToSave);
				progress.dismiss();
				Looper.loop();
			}
		};
		saveTask.start();
	}

	/**
	 * Save all data
	 */
	public static synchronized void saveData(final Context context,
			                                     String mPath,
			                                     String mFileName,
			                                     final Object objToSave)
	{
		File   	outFile;

		// ensure the path is created
		new File(mPath).mkdirs();
		outFile = new File(mFileName);

		try
		{
			outFile.createNewFile();
			FileOutputStream fStr = new FileOutputStream(outFile);
			ObjectOutputStream oStr = new ObjectOutputStream(fStr);
			oStr.writeObject(objToSave);

			oStr.close();
			fStr.close();

			String msg = String.format("%s %d Bytes to %s",
										context.getString(R.string.saved),
										outFile.length(),
										mPath);
			Log.i(context.getString(R.string.saved), msg);
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		}
		catch (Exception e)
		{
			Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	/**
	 * Load data from file into data sructures
	 * @param inStr Input stream
	 */
	public static synchronized Object loadData(Context context, InputStream inStr)
	{
		int numBytesLoaded = 0;
		Object result = null;

		String msg;
		try
		{
			numBytesLoaded = inStr.available();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		msg = String.format("%d Bytes", numBytesLoaded );

		try
		{
			ObjectInputStream oIn = new ObjectInputStream(inStr);
			/* ensure that measurement page is activated
			   to avoid deletion of loaded data afterwards */
			result = oIn.readObject();
			oIn.close();

			Log.i(context.getString(R.string.load), context.getString(R.string.loaded).concat(" ").concat(msg));
			Toast.makeText(context,context.getString(R.string.loaded).concat(" ").concat(msg), Toast.LENGTH_SHORT).show();
		} catch (Exception ex)
		{
			Toast.makeText(context, ex.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
			Log.e(context.getString(R.string.load),ex.getMessage());
		}
		return result;
	}
}
