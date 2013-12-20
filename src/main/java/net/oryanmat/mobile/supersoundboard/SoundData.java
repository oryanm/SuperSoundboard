package net.oryanmat.mobile.supersoundboard;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;

public class SoundData implements Parcelable {
	public static final String SEPARATOR = ":=:";
	public static final Uri DEFAULT_SOUND = Settings.System.DEFAULT_NOTIFICATION_URI;
	public static final String DEFAULT_TEXT = "untitled";
	public static final SoundData DEFAULT = new SoundData(DEFAULT_TEXT, DEFAULT_SOUND);

	private CharSequence text = "";
	private Uri uri;

	public SoundData(CharSequence text, Uri uri) {
		this.text = text;
		this.uri = uri;
	}

	public static String toString(SoundData data) {
		return data.getText() + SEPARATOR + data.getUri().toString();
	}

	public static SoundData fromString(String string) {
		String[] strings = string.split(SEPARATOR);
		return new SoundData(strings[0],	Uri.parse(strings[1]));
	}

	public CharSequence getText() {
		return text;
	}

	public Uri getUri() {
		return uri;
	}

	public void setText(CharSequence text) {
		this.text = text;
	}

	public void setUri(Uri uri) {
		this.uri = uri;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeString(text.toString());
		out.writeParcelable(uri, flags);
	}

	public static final Parcelable.Creator<SoundData> CREATOR = new Parcelable.Creator<SoundData>() {
		public SoundData createFromParcel(Parcel in) {
			return new SoundData(in);
		}

		public SoundData[] newArray(int size) {
			return new SoundData[size];
		}
	};

	private SoundData(Parcel in) {
		text = in.readString();
		uri = in.readParcelable(Uri.class.getClassLoader());
	}
}

