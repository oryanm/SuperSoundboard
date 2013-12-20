package net.oryanmat.mobile.supersoundboard;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class EditSoundActivity extends ActionBarActivity {
	public static final String RECORDINGS_PATH = "/SuperSoundboard/Recordings";
	public static final String RECORDINGS_FILETYPE = ".3gp";
	private static final int FILE_SELECT_CODE = 0;

	private SoundData data;
	private int position;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_sound);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
				.add(R.id.container, new PlaceholderFragment())
				.commit();
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		extractExtras(getIntent());
		((EditText)findViewById(R.id.uri_edit_text)).setText(data.getUri().toString());
		((EditText)findViewById(R.id.name_edit_text)).setText(data.getText());
	}

	private void extractExtras(Intent intent) {
		position = intent.getIntExtra(MainActivity.EXTRA_EDIT_POSITION, -1);
		data = intent.getParcelableExtra(MainActivity.EXTRA_EDIT_BUTTON);
		if (data == null) data = new SoundData(SoundData.DEFAULT_TEXT, SoundData.DEFAULT_SOUND);
	}

	public void playSound(View view) {
		MediaPlayer mediaPlayer = MediaPlayer.create(this, data.getUri());
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mediaPlayer) {
				mediaPlayer.release();
			}
		});
		mediaPlayer.start();
	}

	public void browseButtonPressed(View view) {
		showFileChooser();
	}

	private void showFileChooser() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("audio/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);

		try {
			startActivityForResult(Intent.createChooser(intent, "Select a File"), FILE_SELECT_CODE);
		} catch (android.content.ActivityNotFoundException ex) {
			// Potentially direct the user to the Market with a Dialog
			Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		switch (requestCode) {
			case FILE_SELECT_CODE: {
				if (resultCode == RESULT_OK) {
					setSelectedFile(intent.getData());
				}
				break;
			}
		}
	}

	private void setSelectedFile(Uri uri) {
		data.setUri(uri);
		((EditText) findViewById(R.id.uri_edit_text)).setText(data.getUri().toString());
		EditText nameEditText = (EditText) findViewById(R.id.name_edit_text);

		if (nameEditText.getText() == null ||
			SoundData.DEFAULT_TEXT.equals(nameEditText.getText().toString())) {
			nameEditText.setText(data.getUri().getLastPathSegment());
		}
	}

	public void onSubmit(View view) {
		Intent intent = new Intent();
		data.setText(((EditText)findViewById(R.id.name_edit_text)).getText());
		intent.putExtra(MainActivity.EXTRA_EDIT_BUTTON, data);
		intent.putExtra(MainActivity.EXTRA_EDIT_POSITION, position);
		setResult(RESULT_OK, intent);
		finish();
	}

	public void record(View view) throws IOException {
		if (!isExternalStorageWritable()) {
			Toast.makeText(this, "External storage not ready", Toast.LENGTH_SHORT).show();
			return;
		}

		File file = new File(getDir(), String.valueOf(Calendar.getInstance().getTimeInMillis()) + RECORDINGS_FILETYPE);
		MediaRecorder recorder = startRecording(file);
		sleep();
		stopRecording(recorder);
		setSelectedFile(Uri.parse(file.getAbsolutePath()));
		Toast.makeText(this, "Saved recording", Toast.LENGTH_SHORT).show();
	}

	private void sleep() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private MediaRecorder startRecording(File file) throws IOException {
		MediaRecorder recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setOutputFile(file.getAbsolutePath());
		recorder.prepare();
		recorder.start();
		return recorder;
	}

	private void stopRecording(MediaRecorder recorder) {
		recorder.stop();
		recorder.reset();
		recorder.release();
	}

	public boolean isExternalStorageWritable() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}

	public File getDir() {
		File file = new File(Environment.getExternalStorageDirectory().getPath() + RECORDINGS_PATH);
		if (!file.mkdirs() && !file.exists()) {
			Log.e(EditSoundActivity.class.getName(), "Directory not created");
		}
		return file;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_sound, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
			case R.id.action_settings:
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
														 Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_edit_sound, container, false);
			return rootView;
		}
	}
}
