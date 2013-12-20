package net.oryanmat.mobile.supersoundboard;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Soundboard {
	public static final String DATA_FILE_NAME = "filename";

	private Map<Integer, SoundData> sounds = new HashMap<Integer, SoundData>();
	private Activity activity;

	public Soundboard(Activity activity) {
		this.activity = activity;
	}

	public void playSound(int id) {
		SoundData data = sounds.get(id);
		MediaPlayer mediaPlayer = MediaPlayer.create(activity,
			data != null ? data .getUri(): SoundData.DEFAULT_SOUND);
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mediaPlayer) {
				mediaPlayer.release();
			}
		});
		mediaPlayer.start();
	}

	public void assignSound(int id, SoundData data) {
		if (data.getUri() == null) data.setUri(SoundData.DEFAULT_SOUND);
		sounds.put(id, data);
	}

	public SoundData getData(int id) {
		return sounds.get(id);
	}

	public void load() {
		assignSounds(readDataFile(new HashMap<Integer, String>()));
	}

	@SuppressWarnings("unchecked")
	private HashMap<Integer, String> readDataFile(HashMap<Integer, String> strings) {
		try {
			FileInputStream fos = activity.openFileInput(DATA_FILE_NAME);
			ObjectInputStream in = new ObjectInputStream(fos);
			strings = (HashMap<Integer, String>) in.readObject();
			in.close();
			fos.close();
		} catch (IOException e) {
			Toast.makeText(activity, "load failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
		} catch (ClassNotFoundException e) {
			Toast.makeText(activity, "load failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
		}

		return strings;
	}

	private void assignSounds(HashMap<Integer, String> strings) {
		sounds.clear();

		for (Integer id : strings.keySet()) {
			assignSound(id, SoundData.fromString(strings.get(id)));
		}
	}

	public void save() {
		try {
			FileOutputStream fileOutputStream = activity.openFileOutput(DATA_FILE_NAME, Context.MODE_PRIVATE);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(convertToStrings(sounds));
			objectOutputStream.close();
			fileOutputStream.close();
		} catch (IOException e) {
			Toast.makeText(activity, "save failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	private Map<Integer, String> convertToStrings(Map<Integer, SoundData> sounds) {
		Map<Integer, String> strings = new HashMap<Integer, String>();

		for (Integer id : sounds.keySet()) {
			strings.put(id, SoundData.toString(sounds.get(id)));
		}

		return strings;
	}
}
