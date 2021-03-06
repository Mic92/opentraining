/**
 * 
 * This is OpenTraining, an Android application for planning your your fitness training.
 * Copyright (C) 2012-2013 Christian Skubich
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package de.skubware.opentraining.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.actionbarsherlock.widget.ShareActionProvider;

import de.skubware.opentraining.basic.ExerciseTag;
import de.skubware.opentraining.basic.ExerciseType;
import de.skubware.opentraining.basic.IExercise;
import de.skubware.opentraining.basic.Muscle;
import de.skubware.opentraining.basic.SportsEquipment;
import de.skubware.opentraining.basic.Workout;
import de.skubware.opentraining.db.parser.ExerciseTagJSONParser;
import de.skubware.opentraining.db.parser.ExerciseTypeXMLParser;
import de.skubware.opentraining.db.parser.IParser;
import de.skubware.opentraining.db.parser.MuscleJSONParser;
import de.skubware.opentraining.db.parser.SportsEquipmentJSONParser;
import de.skubware.opentraining.db.parser.WorkoutXMLParser;
import de.skubware.opentraining.db.parser.XMLSaver;
import android.content.Context;
import android.util.Log;

/**
 * Implementation of {@link IDataProvider}.
 * 
 * 
 */
public class DataProvider implements IDataProvider {
	/** Tag for logging */
	public static final String TAG = "DataProvider";

	private Context mContext;

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            The applications context.
	 */
	public DataProvider(Context context) {
		mContext = context;
	}

	
	

	@Override
	public List<ExerciseType> getExercises() {
		if (Cache.INSTANCE.getExercises() == null)
			Cache.INSTANCE.updateCache(mContext);

		return new ArrayList<ExerciseType>(Cache.INSTANCE.getExercises());
	}

	/**
	 * Loads the .xml exercise files from the filesystem.
	 * 
	 * @return The loaded {@link ExerciseType}s.
	 * 
	 */
	List<ExerciseType> loadExercises() {
		List<ExerciseType> list = new ArrayList<ExerciseType>();

		try {
			String[] files = mContext.getAssets().list(IDataProvider.EXERCISE_FOLDER);

			for (String f : files) {
				if (f.endsWith(".xml")) {
					ExerciseTypeXMLParser parser = new ExerciseTypeXMLParser(mContext);
					ExerciseType ex = parser.read(mContext.getAssets().open(IDataProvider.EXERCISE_FOLDER + "/" + f));
					list.add(ex);
				}
			}

			Collections.sort(list);
		} catch (IOException ioEx) {
			Log.e(TAG, "Error during parsing exercises.", ioEx);
		}

		return list;
	}

	@Override
	public boolean saveExercise(IExercise ex) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ExerciseType getExerciseByName(String name) {
		for (ExerciseType ex : this.getExercises()) {
			if (name.equals(ex.getUnlocalizedName()) || name.equals(ex.getLocalizedName()))
				return ex;
		}

		return null;
	}

	@Override
	public boolean exerciseExists(String name) {
		return getExerciseByName(name) != null;
	}

	@Override
	public List<Muscle> getMuscles() {
		if (Cache.INSTANCE.getMuscles() == null)
			Cache.INSTANCE.updateCache(mContext);

		return new ArrayList<Muscle>(Cache.INSTANCE.getMuscles());
	}

	/**
	 * Loads the {@link Muscle}s from the filesytem.
	 * 
	 * @return The loaded {@link Muscle}s
	 */
	List<Muscle> loadMuscles() {
		List<Muscle> list = new ArrayList<Muscle>();

		try {
			IParser<List<Muscle>> muscleParser = new MuscleJSONParser();
			list = muscleParser.parse(mContext.getAssets().open(IDataProvider.MUSCLE_FILE));
		} catch (IOException ioEx) {
			Log.e(TAG, "Error during parsing muscles.", ioEx);
		}

		return list;

	}

	@Override
	public Muscle getMuscleByName(String name) {
		for (Muscle m : getMuscles()) {
			if (m.isAlternativeName(name))
				return m;
		}

		return null;
	}

	@Override
	public List<SportsEquipment> getEquipment() {
		if (Cache.INSTANCE.getEquipment() == null)
			Cache.INSTANCE.updateCache(mContext);

		return new ArrayList<SportsEquipment>(Cache.INSTANCE.getEquipment());
	}

	/**
	 * Loads the {@link SportsEquipment}s from the filesytem.
	 * 
	 * @return The loaded {@link SportsEquipment}s
	 */
	List<SportsEquipment> loadEquipment() {
		List<SportsEquipment> list = new ArrayList<SportsEquipment>();

		try {
			IParser<List<SportsEquipment>> equipmentParser = new SportsEquipmentJSONParser();
			list = equipmentParser.parse(mContext.getAssets().open(IDataProvider.EQUIPMENT_FILE));
		} catch (IOException ioEx) {
			Log.e(TAG, "Error during parsing SportsEquipment.", ioEx);
		}

		return list;

	}

	@Override
	public SportsEquipment getEquipmentByName(String name) {
		for (SportsEquipment m : getEquipment()) {
			if (m.isAlternativeName(name))
				return m;
		}

		return null;
	}
	
	@Override
	public List<ExerciseTag> getExerciseTags() {
		if (Cache.INSTANCE.getExerciseTags() == null)
			Cache.INSTANCE.updateCache(mContext);

		return new ArrayList<ExerciseTag>(Cache.INSTANCE.getExerciseTags());
	}

	/**
	 * Loads the {@link ExerciseTag}s from the filesytem.
	 * 
	 * @return The loaded {@link ExerciseTag}s
	 */
	List<ExerciseTag> loadExerciseTags() {
		List<ExerciseTag> list = new ArrayList<ExerciseTag>();

		try {
			IParser<List<ExerciseTag>> equipmentParser = new ExerciseTagJSONParser();
			list = equipmentParser.parse(mContext.getAssets().open(IDataProvider.EXERCISE_TAG_FILE));
		} catch (IOException ioEx) {
			Log.e(TAG, "Error during parsing muscles.", ioEx);
		}

		return list;

	}

	@Override
	public ExerciseTag getExerciseTagByName(String name) {
		for (ExerciseTag m : getExerciseTags()) {
			if (m.isAlternativeName(name))
				return m;
		}
		
		Log.w(TAG, "Did not find ExerciseTag: " + name + ".\n Will create new ExerciseTag.");
		ArrayList<String> nameList = new ArrayList<String> ();
		return new ExerciseTag(Locale.getDefault(), nameList, "");

	}

	@Override
	public List<Workout> getWorkouts() {
		if (Cache.INSTANCE.getWorkouts() == null)
			Cache.INSTANCE.updateCache(mContext);

		return new ArrayList<Workout>(Cache.INSTANCE.getWorkouts());
	}
	

	public List<Workout> loadWorkouts() {
		List<Workout> workoutList = new ArrayList<Workout>();

		// list files in directory that end with ".xml"
		String files[] = mContext.getFilesDir().list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				if (filename.endsWith(".xml") && !filename.equals(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME))
					return true;
				else
					return false;
			}
		});
		
		if(files.length == 0){		
			Log.d(TAG, "No workouts found, will copy example Workouts");
			copyExampleWorkouts();
			return getWorkouts();
		}

		// parse each file
		for (String file : files) {
			Workout w = this.loadWorkout(mContext.getFilesDir().toString() + "/" + file);
			
			if(w != null){
				workoutList.add(w);
			}else{
				Log.e(TAG, "Read Workout and parser returned null. This should not happen. Either the Workout XML-Parser or the XML-Saver is buggy.");
			}	
		}

		Log.v(TAG, "Read " + files.length + " Workouts. workoutList.size()= " + workoutList.size());

		return workoutList;
	}
	
	/**
	 * Copies the example Workouts to the file system.
	 */
	private void copyExampleWorkouts() {
		try {
			String[] exampleWorkouts = mContext.getAssets().list(
					IDataProvider.EXAMPLE_WORKOUT_FOLDER);
			for (String file : exampleWorkouts) {
				InputStream in = null;
				OutputStream out = null;
				try {
					in = mContext.getAssets().open(IDataProvider.EXAMPLE_WORKOUT_FOLDER + "/" + file);
					out = new FileOutputStream(mContext.getFilesDir().toString() + "/" + file);

					// copy file
					byte[] buffer = new byte[1024];
					int read;
					while ((read = in.read(buffer)) != -1) {
						out.write(buffer, 0, read);
					}

					in.close();
					in = null;
					out.flush();
					out.close();
					out = null;
				} catch (IOException e) {
					Log.e("tag", "Failed to copy asset file: " + file, e);
				}
			}

		} catch (IOException e) {
			Log.e(TAG, "Copying example workouts failed", e);
		}
	}
	

	/**
	 * Tries to load and parse a {@link Workout} .xml file.
	 * 
	 * @param path
	 *            The path of the .xml file
	 * 
	 * @return The {@link Workout} or null if the file could not be read
	 */
	private Workout loadWorkout(String path) {
		String xmlData;
		try {
			DataHelper helper = new DataHelper(mContext);
			xmlData = helper.loadFileFromFileSystem(path);

			// write file again ...
			FileOutputStream fos = mContext.openFileOutput("my_xml", Context.MODE_PRIVATE);
			fos.write(xmlData.getBytes());
			fos.close();

			// ... to read it
			WorkoutXMLParser parser = new WorkoutXMLParser();
			Workout w = parser.read(mContext.getFileStreamPath("my_xml"), mContext);

			if (w == null) {
				Log.e(TAG, "Read Workout and parser returned null. This should not happen");
			}
			return w;
		} catch (IOException e) {
			Log.i(TAG, "Could not read training plan \n" + e.getMessage());
			return null;
		}

	}

	@Override
	public boolean saveWorkout(Workout w) {
		Log.d(TAG, "w==null: " + (w==null) + "  mContext==null: " + (mContext==null));
		boolean succ = XMLSaver.writeTrainingPlan(w, mContext.getFilesDir());
		
		// update Cache, as Workout has changed
		new Thread() {
			@Override
			public void run() {
				Cache.INSTANCE.updateWorkoutCache(mContext);
			}
		}.start();

		return succ;
	}
	
	/**
	 * Does the same as #saveWorkout(Workout), but the task is executed in a new
	 * Thread.
	 * 
	 * @param w
	 *            The {@link Workout} to save
	 */
	public void saveWorkoutAsync(final Workout w){
		new Thread() {
			@Override
			public void run() {
				boolean succ = saveWorkout(w);
				
				if(!succ)
					Log.e(TAG, "Could not save Workout: " + w.toDebugString());
			}
		}.start();
	}

	@Override
	public boolean deleteWorkout(Workout w) {
		File directory = mContext.getFilesDir();
		File workout_file = new File(directory.toString() + "/" + w.getName() + ".xml");
		if (!workout_file.exists())
			throw new IllegalArgumentException("The workout that should be deleted does not exist.");

		boolean succ = workout_file.delete();
		
		// update Cache, as Workout has changed
		new Thread() {
			@Override
			public void run() {
				Cache.INSTANCE.updateWorkoutCache(mContext);
			}
		}.start();

		return succ;
		
	}


}
