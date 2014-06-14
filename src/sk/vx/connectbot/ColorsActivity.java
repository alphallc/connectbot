/*
 * ConnectBot: simple, powerful, open-source SSH client for Android
 * Copyright 2007 Kenny Root, Jeffrey Sharkey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sk.vx.connectbot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;

import sk.vx.connectbot.util.Colors;
import sk.vx.connectbot.util.FileChooser;
import sk.vx.connectbot.util.FileChooserCallback;
import sk.vx.connectbot.util.HostDatabase;
import sk.vx.connectbot.util.UberColorPickerDialog;
import sk.vx.connectbot.util.UberColorPickerDialog.OnColorChangedListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * @author Kenny Root
 *
 */
public class ColorsActivity extends Activity implements OnItemClickListener, OnColorChangedListener, OnItemSelectedListener, FileChooserCallback {
    public final static String TAG = "ConnectBot.ColorsActivity";

	private GridView mColorGrid;
	private Spinner mFgSpinner;
	private Spinner mBgSpinner;

	private int mColorScheme;

	private List<Integer> mColorList;
	private HostDatabase hostdb;

	private int mCurrentColor = 0;

	private int[] mDefaultColors;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.act_colors);

		this.setTitle(String.format("%s: %s",
				getResources().getText(R.string.app_name),
				getResources().getText(R.string.title_colors)));

		mColorScheme = HostDatabase.DEFAULT_COLOR_SCHEME;

		hostdb = new HostDatabase(this);

		mColorList = Arrays.asList(hostdb.getColorsForScheme(mColorScheme));
		mDefaultColors = hostdb.getDefaultColorsForScheme(mColorScheme);

		mColorGrid = (GridView) findViewById(R.id.color_grid);
		mColorGrid.setAdapter(new ColorsAdapter(true));
		mColorGrid.setOnItemClickListener(this);
		mColorGrid.setSelection(0);

		mFgSpinner = (Spinner) findViewById(R.id.fg);
		mFgSpinner.setAdapter(new ColorsAdapter(false));
		mFgSpinner.setSelection(mDefaultColors[0]);
		mFgSpinner.setOnItemSelectedListener(this);

		mBgSpinner = (Spinner) findViewById(R.id.bg);
		mBgSpinner.setAdapter(new ColorsAdapter(false));
		mBgSpinner.setSelection(mDefaultColors[1]);
		mBgSpinner.setOnItemSelectedListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (hostdb != null) {
			hostdb.close();
			hostdb = null;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (hostdb == null)
			hostdb = new HostDatabase(this);
	}

	private class ColorsAdapter extends BaseAdapter {
		private boolean mSquareViews;

		public ColorsAdapter(boolean squareViews) {
			mSquareViews = squareViews;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ColorView c;

			if (convertView == null) {
				c = new ColorView(ColorsActivity.this, mSquareViews);
			} else {
				c = (ColorView) convertView;
			}

			c.setColor(mColorList.get(position));
			c.setNumber(position + 1);

			return c;
		}

		public int getCount() {
			return mColorList.size();
		}

		public Object getItem(int position) {
			return mColorList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}
	}

	private class ColorView extends View {
		private boolean mSquare;

		private Paint mTextPaint;
		private Paint mShadowPaint;

		// Things we paint
		private int mBackgroundColor;
		private String mText;

		private int mAscent;
		private int mWidthCenter;
		private int mHeightCenter;

		public ColorView(Context context, boolean square) {
			super(context);

			mSquare = square;

			mTextPaint = new Paint();
			mTextPaint.setAntiAlias(true);
			mTextPaint.setTextSize(16);
			mTextPaint.setColor(0xFFFFFFFF);
			mTextPaint.setTextAlign(Paint.Align.CENTER);

			mShadowPaint = new Paint(mTextPaint);
			mShadowPaint.setStyle(Paint.Style.STROKE);
			mShadowPaint.setStrokeCap(Paint.Cap.ROUND);
			mShadowPaint.setStrokeJoin(Paint.Join.ROUND);
			mShadowPaint.setStrokeWidth(4f);
			mShadowPaint.setColor(0xFF000000);

			setPadding(10, 10, 10, 10);
		}

		public void setColor(int color) {
			mBackgroundColor = color;
		}

		public void setNumber(int number) {
			mText = Integer.toString(number);
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			int width = measureWidth(widthMeasureSpec);

			int height;
			if (mSquare)
				height = width;
			else
				height = measureHeight(heightMeasureSpec);

			mAscent = (int) mTextPaint.ascent();
			mWidthCenter = width / 2;
			mHeightCenter = height / 2 - mAscent / 2;

			setMeasuredDimension(width, height);
		}

		private int measureWidth(int measureSpec) {
			int result = 0;
			int specMode = MeasureSpec.getMode(measureSpec);
			int specSize = MeasureSpec.getSize(measureSpec);

			if (specMode == MeasureSpec.EXACTLY) {
				// We were told how big to be
				result = specSize;
			} else {
				// Measure the text
				result = (int) mTextPaint.measureText(mText) + getPaddingLeft()
						+ getPaddingRight();
				if (specMode == MeasureSpec.AT_MOST) {
					// Respect AT_MOST value if that was what is called for by
					// measureSpec
					result = Math.min(result, specSize);
				}
			}

			return result;
		}

		private int measureHeight(int measureSpec) {
			int result = 0;
			int specMode = MeasureSpec.getMode(measureSpec);
			int specSize = MeasureSpec.getSize(measureSpec);

			mAscent = (int) mTextPaint.ascent();
			if (specMode == MeasureSpec.EXACTLY) {
				// We were told how big to be
				result = specSize;
			} else {
				// Measure the text (beware: ascent is a negative number)
				result = (int) (-mAscent + mTextPaint.descent())
						+ getPaddingTop() + getPaddingBottom();
				if (specMode == MeasureSpec.AT_MOST) {
					// Respect AT_MOST value if that was what is called for by
					// measureSpec
					result = Math.min(result, specSize);
				}
			}
			return result;
		}


		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			canvas.drawColor(mBackgroundColor);

			canvas.drawText(mText, mWidthCenter, mHeightCenter, mShadowPaint);
			canvas.drawText(mText, mWidthCenter, mHeightCenter, mTextPaint);
		}
	}

	private void editColor(int colorNumber) {
		mCurrentColor = colorNumber;
		new UberColorPickerDialog(this, this, mColorList.get(colorNumber)).show();
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		editColor(position);
	}

	public void onNothingSelected(AdapterView<?> arg0) { }

	public void colorChanged(int value) {
		hostdb.setGlobalColor(mCurrentColor, value);
		mColorList.set(mCurrentColor, value);
		mColorGrid.invalidateViews();
	}

	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		boolean needUpdate = false;
		if (parent == mFgSpinner) {
			if (position != mDefaultColors[0]) {
				mDefaultColors[0] = position;
				needUpdate = true;
			}
		} else if (parent == mBgSpinner) {
			if (position != mDefaultColors[1]) {
				mDefaultColors[1] = position;
				needUpdate = true;
			}
		}

		if (needUpdate)
			hostdb.setDefaultColorsForScheme(mColorScheme, mDefaultColors[0], mDefaultColors[1]);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuItem load = menu.add(R.string.menu_colors_load);
		load.setAlphabeticShortcut('i');
		load.setNumericShortcut('1');
		load.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				FileChooser.selectFile(ColorsActivity.this, ColorsActivity.this,
						FileChooser.REQUEST_CODE_SELECT_FILE,
						getString(R.string.file_chooser_select_file, getString(R.string.select_for_upload)));
				return true;
			}
		});


		MenuItem save = menu.add(R.string.menu_colors_save);
		save.setAlphabeticShortcut('e');
		save.setNumericShortcut('2');
		save.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				final EditText textField = new EditText(ColorsActivity.this);

				new AlertDialog.Builder(ColorsActivity.this)
				.setTitle(R.string.colors_save_string)
				.setMessage(R.string.colors_save_string_summary)
				.setView(textField)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						int msg = R.string.colors_save_failure;
						if (ColorsActivity.this.saveColors(textField.getText().toString()))
							msg = R.string.colors_save_success;

						Toast.makeText(ColorsActivity.this, msg, Toast.LENGTH_LONG).show();
					}
				}).setNegativeButton(android.R.string.cancel, null).create().show();

				return true;
			}
		});


		MenuItem reset = menu.add(R.string.menu_colors_reset);
		reset.setAlphabeticShortcut('r');
		reset.setNumericShortcut('1');
		reset.setIcon(android.R.drawable.ic_menu_revert);
		reset.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem arg0) {
				// Reset each individual color to defaults.
				for (int i = 0; i < Colors.defaults.length; i++) {
					if (mColorList.get(i) != Colors.defaults[i]) {
						hostdb.setGlobalColor(i, Colors.defaults[i]);
						mColorList.set(i, Colors.defaults[i]);
					}
				}
				mColorGrid.invalidateViews();

				// Reset the default FG/BG colors as well.
				mFgSpinner.setSelection(HostDatabase.DEFAULT_FG_COLOR);
				mBgSpinner.setSelection(HostDatabase.DEFAULT_BG_COLOR);
				hostdb.setDefaultColorsForScheme(HostDatabase.DEFAULT_COLOR_SCHEME,
						HostDatabase.DEFAULT_FG_COLOR, HostDatabase.DEFAULT_BG_COLOR);

				return true;
			}
		});



		return true;
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {
            case FileChooser.REQUEST_CODE_SELECT_FILE:
                if (resultCode == RESULT_OK && intent != null) {
                    Uri uri = intent.getData();
                    try {
                        if (uri != null) {
                            fileSelected(new File(URI.create(uri.toString())));
                        } else {
                            String filename = intent.getDataString();
                            if (filename != null) {
                                fileSelected(new File(URI.create(filename)));
                            }
                        }
                    } catch (IllegalArgumentException e) {
                        Log.e(TAG, "Couldn't read from selected file", e);
                    }
                }
                break;
        }
    }

    protected String readFile(File file) throws IOException {
		FileInputStream input = new FileInputStream(file);
		StringBuffer content = new StringBuffer("");
		byte[] buffer = new byte[1024];

		while (input.read(buffer) != -1) {
			content.append(new String(buffer));
		}

		return content.toString();
    }

    protected Integer parseColor(final String color) {
    	final Pattern p = Pattern.compile("^#?(?:ff)?([0-9A-Fa-f]{6})$");
    	final Matcher m = p.matcher(color);
    	String rgb;

    	if  (m.matches()) {
    		rgb = m.group(1);
    	}
    	else {
    		Log.e(TAG, "invalid color: " + color);
    		return (0xFF000000 | 0);
    	}

		try {
			Integer i = Integer.parseInt(rgb, 16);
			Log.e(TAG, "color " + color + " = " + Integer.toHexString(i));
			return (0xFF000000 | i);
		} catch (java.lang.NumberFormatException e) {
			Log.e(TAG, "unable to parse color: " + rgb);
			return (0xFF000000 | 0);
		}
    }

	protected boolean loadColors(File file) {
		try {
			String json = readFile(file);
			JSONObject config = (JSONObject) new JSONTokener(json).nextValue();
			JSONArray colors = config.getJSONArray("colors");

			for (int i = 0; i < colors.length(); i++) {
				Integer color = parseColor(colors.getString(i));

				hostdb.setGlobalColor(i, color);
				mColorList.set(i, color);
			}

			Integer fgColor = config.getInt("fg");
			Integer bgColor = config.getInt("bg");

			mColorGrid.invalidateViews();

			// Reset the default FG/BG colors as well.
			mFgSpinner.setSelection(fgColor);
			mBgSpinner.setSelection(bgColor);
			hostdb.setDefaultColorsForScheme(HostDatabase.DEFAULT_COLOR_SCHEME,
					fgColor, bgColor);

		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		} catch (JSONException e) {
			return false;
		}

		return true;
	}

	/**
	 * @param name
	 */
	protected boolean saveColors(String name) {
		try {
			String storageDir = Environment.getExternalStorageDirectory().getPath();
			File file = new File(storageDir + "/" + name + ".json");
			JSONStringer json = new JSONStringer().object();

			json.key("colors").array();

			for (int i = 0; i < 15; i++) {
				Integer color = mColorList.get(i);
				json.value(String.format("#%06x", color));
			}

			json.endArray();

			json.key("fg");
			json.value(Integer.toHexString( mDefaultColors[0] ));

			json.key("bg");
			json.value(Integer.toHexString( mDefaultColors[1] ));

			json.endObject();

			OutputStream out = new FileOutputStream(file);
			out.write(json.toString().getBytes());
			out.flush();
			out.close();
		} catch (JSONException e) {
			Log.w("save", e);
			return false;
		} catch (FileNotFoundException e) {
			Log.w("save", e);

			return false;
		} catch (IOException e) {
			Log.w("save", e);

			return false;
		}

		return true;

	}

	/* (non-Javadoc)
	 * @see sk.vx.connectbot.util.FileChooserCallback#fileSelected(java.io.File)
	 */
	public void fileSelected(File f) {
		loadColors(f);

	}
}
