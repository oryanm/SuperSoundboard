package net.oryanmat.mobile.supersoundboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Switch;

public class MainActivity extends ActionBarActivity {
	private static final int EDIT_BUTTON_CODE = 1;
	public static final String EXTRA_EDIT_BUTTON = "net.oryanmat.mobile.supersoundboard.editButton";
	public static final String EXTRA_EDIT_POSITION = "net.oryanmat.mobile.supersoundboard.editPos";

	private Soundboard soundboard;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
				.add(R.id.container, new PlaceholderFragment())
				.commit();
		}

		soundboard = new Soundboard(this);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(new SoundAdapter(this, soundboard));
		gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				buttonPressed(position);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		soundboard.load();
	}

	@Override
	protected void onPause() {
		super.onPause();
		soundboard.save();
	}

	public void buttonPressed(int position) {
		if (((Switch) findViewById(R.id.edit_switch)).isChecked()) {
			editSound(position);
		} else {
			soundboard.playSound(position);
		}
	}

	private void editSound(int position) {
		Intent intent = new Intent(this, EditSoundActivity.class);
		intent.putExtra(EXTRA_EDIT_BUTTON, soundboard.getData(position));
		intent.putExtra(EXTRA_EDIT_POSITION, position);
		startActivityForResult(intent, EDIT_BUTTON_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		switch (requestCode) {
			case EDIT_BUTTON_CODE:
				if (resultCode == RESULT_OK) {
					SoundData data = intent.getParcelableExtra(MainActivity.EXTRA_EDIT_BUTTON);
					int position = intent.getIntExtra(MainActivity.EXTRA_EDIT_POSITION, -1);
					soundboard.assignSound(position, data);
					soundboard.save();
					GridView gridview = (GridView) findViewById(R.id.gridview);
					gridview.invalidateViews();
				}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
														 Bundle savedInstanceState) {
			return inflater.inflate(R.layout.fragment_main, container, false);
		}
	}
}
