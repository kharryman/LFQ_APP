package com.lfq.learnfactsquick;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import com.lfq.learnfactsquick.Constants.cols.dictionarya;
import com.lfq.learnfactsquick.Constants.tables;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class EditDictionary extends Activity {
	private EditText dictionary_word_input;
	private Spinner select_speech, select_suggested_words;
	private TextView suggested_words, results;
	private EditText dictionary_definition;
	private RadioButton insert_definition, delete_definition, edit_definition;
	private Button get, edit;
	private String selwor, definition;
	private String sql, sql2, text;
	private ArrayAdapter<String> dataAdapter, speechAdapter;
	private Cursor c = null, c2 = null;
	private ContentValues values;
	private Activity this_act;
	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;
	private android.widget.RelativeLayout.LayoutParams params;

	private MenuItem menu_item_autosync_on, menu_item_autosync_off;
	private String autosync_text;
	private SelectWordListener select_word_listener;
	private static Boolean is_database_load;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this_act = this;
		is_database_load = false;
		sharedPref = getSharedPreferences(
				getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		editor = sharedPref.edit();
		values = new ContentValues();
		select_word_listener = new SelectWordListener();
		new doLoadDatabases().execute();

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (is_database_load == false) {
			saveChanges();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (is_database_load == false) {
			saveChanges();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (is_database_load == false) {
			new doLoadDatabases().execute();
		}
	}

	@Override
	public void onBackPressed() {
		if (is_database_load == false) {
			saveChanges();
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_lfq, menu);
		MenuItem bg_color = menu.findItem(R.id.background_color_settings);
		MenuItem button_color = menu.findItem(R.id.button_color_settings);
		MenuItem go_back_item = menu.findItem(R.id.action_bar_go_back);
		go_back_item.setVisible(true);
		bg_color.setVisible(false);
		button_color.setVisible(false);
		RelativeLayout back_text_layout = (RelativeLayout) go_back_item
				.getActionView();
		int buttonDrawable = sharedPref.getInt("BG Button", R.drawable.button);
		back_text_layout.setBackgroundResource(buttonDrawable);
		back_text_layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		menu_item_autosync_off = menu.findItem(R.id.autosync_off);
		menu_item_autosync_on = menu.findItem(R.id.autosync_on);
		if (sharedPref.getBoolean("AUTO SYNC", false) == true
				&& Helpers.isNetworkAvailable()) {
			menu_item_autosync_on.setChecked(true);
			menu_item_autosync_off.setChecked(false);
		} else {
			editor.putBoolean("AUTO SYNC", false);
			menu_item_autosync_on.setChecked(false);
			menu_item_autosync_off.setChecked(true);
		}
		editor.commit();
		return true;
	}

	public void saveChanges() {
		editor.putString("EDIT DICTIONARY WORD INPUT", dictionary_word_input
				.getText().toString());
		editor.putString("EDIT DICTIONARY DEFINITION INPUT",
				dictionary_definition.getText().toString());
		editor.putString("EDIT DICTIONARY SELECT SPEECH", select_speech
				.getSelectedItem().toString());

		editor.putBoolean("EDIT DICTIONARY CHECK INSERT",
				insert_definition.isChecked());
		editor.putBoolean("EDIT DICTIONARY CHECK DELETE",
				delete_definition.isChecked());
		editor.putBoolean("EDIT DICTIONARY CHECK EDIT",
				edit_definition.isChecked());

		editor.commit();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		saveChanges();
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.autosync_on:
			if (Helpers.isNetworkAvailable()) {
				editor.putBoolean("AUTO SYNC", true);
				menu_item_autosync_on.setChecked(true);
				menu_item_autosync_off.setChecked(false);
				editor.commit();
			}
			break;
		case R.id.autosync_off:
			editor.putBoolean("AUTO SYNC", false);
			menu_item_autosync_on.setChecked(false);
			menu_item_autosync_off.setChecked(true);
			editor.commit();
			break;
		}
		return true;
	}

	@SuppressWarnings({ "deprecation" })
	@SuppressLint("NewApi")
	public void setViews() {
		// LAYOUTS:
		setTitle("EDIT DICTIONARY");

		// BUTTONS:
		get = (Button) findViewById(R.id.get_definition);
		edit = (Button) findViewById(R.id.edit_specific_definition);

		// EDITEXTS:
		dictionary_word_input = (EditText) findViewById(R.id.dictionary_word_input);
		dictionary_definition = (EditText) findViewById(R.id.dictionary_definition);

		// SPINNERS:
		select_speech = (Spinner) findViewById(R.id.select_speech);
		speechAdapter = new ArrayAdapter<String>(this_act,
				android.R.layout.simple_spinner_item, new ArrayList<String>());
		speechAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		select_speech.setAdapter(speechAdapter);
		String[] speech_options = { "noun", "adj.", "verb", "adv.", "acr.",
				"pre.", "con.", "pro.", "aux.", "art." };
		speechAdapter.addAll(Arrays.asList(speech_options));
		select_suggested_words = (Spinner) findViewById(R.id.select_suggested_words);
		select_suggested_words.setOnTouchListener(select_word_listener);
		select_suggested_words.setOnItemSelectedListener(select_word_listener);
		dataAdapter = new ArrayAdapter<String>(this_act,
				android.R.layout.simple_spinner_item, new ArrayList<String>());
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		select_suggested_words.setAdapter(dataAdapter);

		// RADIOBUTTONS:
		insert_definition = (RadioButton) findViewById(R.id.insert_definition);
		delete_definition = (RadioButton) findViewById(R.id.delete_definition);
		edit_definition = (RadioButton) findViewById(R.id.edit_definition);

		// TEXTVIEWS:
		results = (TextView) findViewById(R.id.show_results);
		suggested_words = (TextView) findViewById(R.id.suggested_words);

		int measuredHeight = 0;
		WindowManager w = getWindowManager();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point size = new Point();
			w.getDefaultDisplay().getSize(size);
			measuredHeight = size.y;
		} else {
			Display d = w.getDefaultDisplay();
			measuredHeight = d.getHeight();
		}
		params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				(int) (measuredHeight * 0.3));
		params.addRule(RelativeLayout.BELOW, suggested_words.getId());
		dictionary_definition.setLayoutParams(params);

	}

	public void loadButtons() {
		get.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		edit.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));

	}

	public void setListeners() {
		dictionary_word_input.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				if (select_word_listener.userSelect == true) {
					return;
				}
				selwor = dictionary_word_input.getText().toString();
				if (selwor.equals("")) {
					dataAdapter.clear();
					return;
				} else {
					c = MainLfqActivity.getDictionaryDb().rawQuery(
							"SELECT * FROM " + tables.dictionarya + " WHERE "
									+ dictionarya.Word + " LIKE ?",
							new String[] { selwor + "%" });
					dataAdapter.clear();
					if (c.moveToFirst()) {
						while (!c.isAfterLast()) {
							dataAdapter.add(c.getString(c
									.getColumnIndex(dictionarya.Word)));
							c.moveToNext();
						}
					}
					c.close();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

		});

		edit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				selwor = dictionary_word_input.getText().toString();
				definition = dictionary_definition.getText().toString();
				String partspeech = select_speech.getSelectedItem().toString();
				if (insert_definition.isChecked()) {
					values.clear();
					values.put(dictionarya.Word, selwor);
					values.put(dictionarya.PartSpeech, partspeech);
					values.put(dictionarya.Definition, definition);
					int number = getNumber(selwor);
					values.put(dictionarya.Number, number);
					MainLfqActivity.getDictionaryDb().delete(
							tables.dictionarya, dictionarya.Word + "=?",
							new String[] { selwor });
					MainLfqActivity.getDictionaryDb().insert(
							tables.dictionarya, null, values);
					sql = "DELETE FROM " + Helpers.db_prefix + "dictionary."
							+ tables.dictionarya + " WHERE " + dictionarya.Word
							+ "='" + selwor + "'";
					sql2 = "INSERT INTO " + Helpers.db_prefix + "dictionary."
							+ tables.dictionarya + "(" + dictionarya.Word + ","
							+ dictionarya.PartSpeech + ","
							+ dictionarya.Definition + "," + dictionarya.Number
							+ ") VALUES('" + selwor + "','" + partspeech
							+ "','" + definition + "','" + number + "')";
					// autoSync(sql, db, action, table, name, bool is_image,
					// byte[] image)
					autosync_text = Synchronize.autoSync(sql, "dictionary_db",
							"delete", tables.dictionarya, selwor, false, null);
					autosync_text += Synchronize.autoSync(sql2,
							"dictionary_db", "insert", tables.dictionarya,
							selwor, false, null);
					results.setText(Html.fromHtml("RESULTS: Word is: " + selwor
							+ ". Definition is: " + definition
							+ ". Major number is: " + number + ".<br />"
							+ autosync_text));
					c.close();
				}
				if (edit_definition.isChecked()) {
					partspeech = select_speech.getSelectedItem().toString();
					values.clear();
					values.put(dictionarya.PartSpeech, partspeech);
					values.put(dictionarya.Definition, definition);
					int number = getNumber(selwor);
					values.put(dictionarya.Number, number);
					MainLfqActivity.getDictionaryDb().update(
							tables.dictionarya, values,
							dictionarya.Word + "=?", new String[] { selwor });
					sql = "UPDATE " + Helpers.db_prefix + "dictionary."
							+ tables.dictionarya + " SET "
							+ dictionarya.PartSpeech + "='" + partspeech
							+ "', " + dictionarya.Number + "='" + number
							+ "', " + dictionarya.Definition + "='"
							+ definition + "'  WHERE " + dictionarya.Word
							+ "='" + selwor + "'";
					autosync_text = Synchronize.autoSync(sql, "dictionary_db",
							"update", tables.dictionarya, selwor, false, null);
					c = MainLfqActivity.getDictionaryDb().rawQuery(
							"SELECT " + dictionarya.PartSpeech + ","
									+ dictionarya.Definition + " FROM "
									+ tables.dictionarya + " WHERE "
									+ dictionarya.Word + "=?",
							new String[] { selwor });
					if (!c.moveToFirst()) {
						results.setText("RESULTS: " + selwor
								+ " doesn't exist." + autosync_text);
					} else {
						results.setText("RESULTS: Updated "
								+ selwor
								+ ". Part of speech is "
								+ c.getString(c
										.getColumnIndex(dictionarya.PartSpeech))
								+ ". Definition is: "
								+ c.getString(c
										.getColumnIndex(dictionarya.Definition))
								+ ". Major number is: " + number + "."
								+ autosync_text);
					}
					c.close();
				}
				if (delete_definition.isChecked()) {
					MainLfqActivity.getDictionaryDb().delete(
							tables.dictionarya, dictionarya.Word + "=?",
							new String[] { selwor });
					sql = "DELETE FROM " + Helpers.db_prefix + "dictionary."
							+ tables.dictionarya + " WHERE " + dictionarya.Word
							+ "='" + selwor + "'";
					autosync_text = Synchronize.autoSync(sql, "dictionary_db",
							"delete", tables.dictionarya, selwor, false, null);
					results.setText(Html.fromHtml("RESULTS: Deleted word: "
							+ selwor + "<br />" + autosync_text));
				}

			}
		});

		get.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				selwor = dictionary_word_input.getText().toString();
				doGet(selwor);
			}
		});
	}

	@SuppressLint("ClickableViewAccessibility")
	public class SelectWordListener implements
			AdapterView.OnItemSelectedListener, View.OnTouchListener {
		public boolean userSelect = false;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			userSelect = true;
			if (dataAdapter.getCount() == 1
					|| select_suggested_words
							.getItemAtPosition(0)
							.toString()
							.equals(select_suggested_words.getSelectedItem()
									.toString())) {
				doSelected();
				userSelect = true;
			}
			return false;
		}

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			doSelected();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}

		public void doSelected() {
			if (userSelect) {
				selwor = select_suggested_words.getSelectedItem().toString();
				doGet(selwor);
				userSelect = false;
			}
		}
	}

	class doLoadDatabases extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setContentView(R.layout.edit_dictionary);
			setViews();
			loadButtons();
			text = "Loading databases. Please wait...";
			results.setText(Html.fromHtml("<b>" + text + "<b>"));

		}

		@Override
		protected String doInBackground(String... params) {
			return null;
		}

		public void doProgress(String value) {
			publishProgress(value);
		}

		@Override
		protected void onProgressUpdate(String... values) {
			results.setText(Html.fromHtml(values[0]));
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		protected void onPostExecute(String file_url) {
			setListeners();
			results.setText("");
			dictionary_word_input.setText(sharedPref.getString(
					"EDIT DICTIONARY WORD INPUT", ""));
			dictionary_definition.setText(sharedPref.getString(
					"EDIT DICTIONARY DEFINITION INPUT", ""));
			select_speech.setSelection(((ArrayAdapter) select_speech
					.getAdapter()).getPosition(sharedPref.getString(
					"EDIT DICTIONARY SELECT SPEECH", select_speech
							.getItemAtPosition(0).toString())));

			insert_definition.setChecked(sharedPref.getBoolean(
					"EDIT DICTIONARY CHECK INSERT", false));
			delete_definition.setChecked(sharedPref.getBoolean(
					"EDIT DICTIONARY CHECK DELETE", false));
			edit_definition.setChecked(sharedPref.getBoolean(
					"EDIT DICTIONARY CHECK EDIT", false));
		}

	}

	public void doGet(String myword) {
		selwor = myword;
		c = MainLfqActivity.getDictionaryDb().rawQuery(
				"SELECT " + dictionarya.Definition + " FROM "
						+ tables.dictionarya + " WHERE " + dictionarya.Word
						+ "=?", new String[] { selwor });
		c2 = MainLfqActivity.getDictionaryDb().rawQuery(
				"SELECT " + dictionarya.PartSpeech + " FROM "
						+ tables.dictionarya + " WHERE " + dictionarya.Word
						+ "=?", new String[] { selwor });

		if (c.moveToFirst() && c2.moveToFirst()) {
			dictionary_definition.setText(c.getString(0));
			dictionary_word_input.setText(selwor);
			select_speech.setSelection(speechAdapter.getPosition(c2
					.getString(0)));
			results.setText("RESULTS: got definition for " + selwor);
		} else {
			results.setText("RESULTS: " + selwor + " doesn't exist.");
		}
		c.close();
		c2.close();
	}

	public int getNumber(String input_word) {
		String[] defspl = input_word.toLowerCase(Locale.US).split("");
		int wl = defspl.length;
		int z;
		String num = "";
		for (int j = 0; j < wl; j++) {
			z = j + 1;
			if (defspl[j].equals("s")) {
				if (z >= wl) {
					num += "0";
				}
				if (z < wl) {
					if (!defspl[z].equals("h")) {
						num += "0";
					}
				}

			}
			if (defspl[j].equals("z")) {
				num += "0";
			}
			if (defspl[j].equals("d") || defspl[j].equals("t")) {
				num += "1";
			}
			if (defspl[j].equals("n")) {
				num += "2";
			}
			if (defspl[j].equals("m")) {
				num += "3";
			}
			if (defspl[j].equals("r")) {
				num += "4";
			}
			if (defspl[j].equals("l")) {
				num += "5";
			}
			if (defspl[j].equals("j")) {
				num += "6";
			}
			if (defspl[j].equals("g") && z < wl) {
				if (!defspl[z].equals("g") && !defspl[z].equals("h")) {
					num += "6";
				}
			}
			if (defspl[j].equals("c") && z < wl) {
				if (defspl[z].equals("h")) {
					num += "6";
				}
			}
			if (defspl[j].equals("s") && z < wl) {
				if (defspl[z].equals("h")) {
					num += "6";
				}
			}
			if (defspl[j].equals("g") && z < wl) {
				if (defspl[z].equals("g")) {
					num += "7";
					j++;
				}
			}
			if (defspl[j].equals("c")) {
				if (z >= wl) {
					num += "7";
				}
				if (z < wl) {
					if (!defspl[z].equals("h")) {
						num += "7";
					}
				}
			}
			if (defspl[j].equals("k") || defspl[j].equals("q")) {
				num += "7";
			}
			if (defspl[j].equals("f") || defspl[j].equals("v")) {
				num += "8";
			}
			if (defspl[j].equals("p") && z < wl) {
				if (defspl[z].equals("h")) {
					num += "8";
				}
			}
			if (defspl[j].equals("b")) {
				num += "9";
			}
			if (defspl[j].equals("p")) {
				if (z >= wl) {
					num += "9";
				}
				if (z < wl) {
					if (!defspl[z].equals("h")) {
						num += "9";
					}
				}
			}
		}
		return Integer.parseInt(num);
	}

}
