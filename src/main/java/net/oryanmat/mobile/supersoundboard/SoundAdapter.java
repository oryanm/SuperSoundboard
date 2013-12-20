package net.oryanmat.mobile.supersoundboard;

import android.content.Context;
import android.text.InputFilter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SoundAdapter extends BaseAdapter {
	private Context context;
	private Soundboard soundboard;

	public SoundAdapter(Context context, Soundboard soundboard) {
		super();
		this.context = context;
		this.soundboard = soundboard;
	}

	public int getCount() {
		return 4*7;
	}

	public Object getItem(int position) {
		return soundboard.getData(position);
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		TextView view;
		if (convertView == null) {  // if it's not recycled, initialize some attributes
			view = new TextView(context);
//			view.setLayoutParams(new GridView.LayoutParams(200, 85));
//			view.setPadding(0,0,0,0);
			view.setGravity(0x10|0x01);
			view.setHeight(150);
			view.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
			view.setBackgroundColor(0x0633B5E5);
		} else {
			view = (TextView) convertView;
		}

		SoundData data = soundboard.getData(position);
		data = data != null ? data : SoundData.DEFAULT;
		view.setText(data.getText());

		return view;
	}
}
