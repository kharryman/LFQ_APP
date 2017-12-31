package com.lfq.learnfactsquick;

import java.util.HashMap;

import com.lfq.learnfactsquick.Constants.cols.user_numbers_table;
import com.lfq.learnfactsquick.Constants.tables;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class EditNumbers extends Activity {
	private RelativeLayout entries_layout;
	private LinearLayout top_layout;
	private TextView results, login_status, prompt_input_number,
			prompt_input_number_info;
	private EditText username_input, password_input,
			input_number_numbers_entries, input_number, input_number_info;
	private RadioButton check_edit_shared_numbers, check_edit_user_numbers;
	private Button do_login, do_logout, do_edit_numbers, do_fullscreen,
			do_backup, add_number, remove_number;
	private RadioButton check_update_numbers, check_delete_numbers,
			check_insert_numbers;
	private Spinner select_number_title;

	private RelativeLayout.LayoutParams params;
	private String text, numbers_table, username, password;
	private String[] textspl;
	private int num_entries;
	private TextView[] prompt_num_ent;
	private TextView[] prompt_mne_ent;
	private EditText[] num_ent;
	private EditText[] mne_ent;
	private static String status;
	private int id, sav_id;
	private HashMap<String, String> text_list;
	private Boolean logged_in;
	private Helpers h;
	private Activity this_act;
	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;
	private String autosync_text, sql;
	private ContentValues cv;

	private MenuItem menu_item_autosync_on, menu_item_autosync_off;
	private static Boolean is_database_load;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this_act = this;
		is_database_load = false;
		sharedPref = getSharedPreferences(
				getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		editor = sharedPref.edit();
		autosync_text = "";
		sql = "";
		cv = new ContentValues();
		h = new Helpers(this_act);
		text_list = new HashMap<String, String>();
		status = "";
		logged_in = false;
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

	@SuppressWarnings("static-access")
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
				&& h.isNetworkAvailable()) {
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

	@SuppressWarnings("static-access")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.autosync_on:
			if (h.isNetworkAvailable()) {
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

	public void saveChanges() {
		editor.putString("EDIT NUMBERS STATUS", status);
		if (status.equals("begin insert") || status.equals("begin update")) {
			editor.putString("EDIT NUMBERS INPUT NUMBER", input_number
					.getText().toString());
			editor.putString("EDIT NUMBERS INPUT NUMBER INFORMATION",
					input_number_info.getText().toString());
			for (int i = 0; i < num_ent.length; i++) {
				if (num_ent[i] != null) {
					editor.putString("EDIT NUMBERS NUMBER ENTRY " + i,
							num_ent[i].getText().toString());
				}
			}
			for (int i = 0; i < mne_ent.length; i++) {
				if (mne_ent[i] != null) {
					editor.putString("EDIT NUMBERS MNEMONIC ENTRY " + i,
							mne_ent[i].getText().toString());
				}
			}
		}
		editor.putString("EDIT NUMBERS NUMBER NUMBERS",
				input_number_numbers_entries.getText().toString());
		editor.putBoolean("EDIT NUMBERS CHECK SHARED NUMBERS",
				check_edit_shared_numbers.isChecked());
		editor.putBoolean("EDIT NUMBERS CHECK USER NUMBERS",
				check_edit_user_numbers.isChecked());
		editor.commit();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		saveChanges();
		super.onConfigurationChanged(newConfig);
	}

	@SuppressWarnings("static-access")
	public void setViews() {
		// LAYOUT:
		setTitle("EDIT NUMBERS");
		entries_layout = (RelativeLayout) findViewById(R.id.numbers_mnemonic_entries_layout);
		top_layout = (LinearLayout) findViewById(R.id.edit_numbers_top_layout);

		// BUTTONS:
		do_login = (Button) findViewById(R.id.do_login_edit_numbers);
		do_logout = (Button) findViewById(R.id.do_logout_edit_numbers);
		do_edit_numbers = (Button) findViewById(R.id.do_edit_numbers_table);
		do_fullscreen = (Button) findViewById(R.id.do_edit_numbers_fullscreen);
		do_backup = (Button) findViewById(R.id.do_edit_numbers_backup);
		
		

		// EDITTEXTS:
		username_input = (EditText) findViewById(R.id.edit_numbers_username);
		password_input = (EditText) findViewById(R.id.edit_numbers_password);
		input_number_numbers_entries = (EditText) findViewById(R.id.input_number_numbers_entries);

		// TEXTVIEWS:
		login_status = (TextView) findViewById(R.id.edit_numbers_login_status);
		results = (TextView) findViewById(R.id.edit_numbers_results);

		if (h.getLoginStatus() == true) {
			username = Helpers.getUsername();
			password = Helpers.getPassword();
			username_input.setText(username);
			logged_in = true;
			login_status.setText("WELCOME " + username + ".");
		}

		// RADIOBUTTONS:
		check_edit_shared_numbers = (RadioButton) findViewById(R.id.check_edit_shared_numbers);
		check_edit_user_numbers = (RadioButton) findViewById(R.id.check_edit_user_numbers);
		check_update_numbers = (RadioButton) findViewById(R.id.check_update_numbers);
		check_delete_numbers = (RadioButton) findViewById(R.id.check_delete_numbers);
		check_insert_numbers = (RadioButton) findViewById(R.id.check_insert_numbers);

		//SPINNERS:
		select_number_title = (Spinner) findViewById(R.id.select_edit_numbers_title);
	}

	public void loadButtons() {
		do_login.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		do_logout.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		do_edit_numbers.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		do_fullscreen.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		do_backup.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
	}

	public void setListeners() {
		do_fullscreen.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				top_layout.setVisibility(View.GONE);
				do_backup.setVisibility(View.VISIBLE);
				do_fullscreen.setVisibility(View.GONE);
			}
		});
		do_backup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				top_layout.setVisibility(View.VISIBLE);
				do_backup.setVisibility(View.GONE);
				do_fullscreen.setVisibility(View.VISIBLE);
			}
		});		

		do_login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				password = password_input.getText().toString();
				username = username_input.getText().toString();
				text = Helpers.login(username, password);
				textspl = text.split("@@@");
				if (textspl[0].equals("true")) {
					logged_in = true;
				}
				login_status.setText(textspl[1]);
			}
		});

		do_logout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				logged_in = false;
				password_input.setText("");
				login_status.setText("LOGGED OUT. BYE BYE " + username);
			}
		});
		check_update_numbers.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				results.setText("");
				setVisibilities();
				getEntry();
			}
		});
		check_insert_numbers.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				results.setText("");
				setVisibilities();
				startInsert();
			}
		});
		check_delete_numbers.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				results.setText("");
				setVisibilities();
			}
		});
		add_number.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				num_entries++;
				//wordspl.add("");
				//infospl.add("");
				//doAddEntries(type, word.size(), word.size() + 1);
			}
		});
		//Number,NumInf,NumWors,Type
		
		remove_number.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*
				System.out.println("infospl size=" + infospl.size());
				System.out.println("wordspl size=" + wordspl.size());
				System.out.println("mnespl size=" + mnespl.size());
				if (num_entries > 0) {
					num_entries--;
				}
				if (type != "anagram") {
					scroll.removeView(tv_mnemonic.get(tv_mnemonic.size() - 1));
					tv_mnemonic.remove(tv_mnemonic.size() - 1);
					scroll.removeView(mnemonic.get(mnemonic.size() - 1));
					mnemonic.remove(mnemonic.size() - 1);
				}
				scroll.removeView(tv_word.get(tv_word.size() - 1));
				tv_word.remove(tv_word.size() - 1);
				scroll.removeView(word.get(word.size() - 1));
				word.remove(word.size() - 1);
				scroll.removeView(tv_info.get(tv_info.size() - 1));
				tv_info.remove(tv_info.size() - 1);
				scroll.removeView(info.get(info.size() - 1));
				info.remove(info.size() - 1);
				if (!type.equals("anagram")) {
					if (mnespl.size() > 0) {
						mnespl.remove(mnespl.size() - 1);
					}
				}
				if (wordspl.size() > 0) {
					wordspl.remove(wordspl.size() - 1);
				}
				if (infospl.size() > 0) {
					infospl.remove(infospl.size() - 1);
				}
				*/
				
			}
			
		});
				
		select_number_title
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						getEntry();
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						getEntry();
					}

				});



		do_edit_numbers.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				results.setText("");
				if (check_delete_numbers.isChecked()) {
					deleteNumber();
				} else if (check_insert_numbers.isChecked()) {
					insertNumber();
				} else if (check_update_numbers.isChecked()) {
					updateTable();
				}
			}
		});		

	}

	public void addEditNumberInfo() {
		// ADD PROMPT INPUT NUMBER
		prompt_input_number = new TextView(this_act);
		params = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		prompt_input_number.setText("INPUT_NUMBER");
		prompt_input_number.setId(1);
		entries_layout.addView(prompt_input_number, params);

		// ADD INPUT NUMBER
		input_number = new EditText(this_act);
		params = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.BELOW, prompt_input_number.getId());
		input_number.setRawInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
		input_number.setMaxLines(3);
		input_number.setGravity(Gravity.TOP);
		input_number.setBackgroundResource(R.drawable.rounded_edittext_red);
		input_number.setId(2);
		entries_layout.addView(input_number, params);

		// ADD PROMPT NUMBER INFO
		prompt_input_number_info = new TextView(this_act);
		params = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.BELOW, input_number.getId());
		prompt_input_number_info.setText("INPUT NUMBER INFORMATION:");
		prompt_input_number_info.setId(3);
		entries_layout.addView(prompt_input_number_info, params);
		// ADD INPUT INFO
		input_number_info = new EditText(this_act);
		params = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.BELOW, prompt_input_number_info.getId());
		input_number_info
				.setBackgroundResource(R.drawable.rounded_edittext_red);
		input_number_info
				.setRawInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
		input_number_info.setMaxLines(3);
		input_number_info.setId(4);
		input_number_info.setGravity(Gravity.TOP);
		entries_layout.addView(input_number_info, params);
	}

	class doLoadDatabases extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setContentView(R.layout.edit_numbers);
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

		@Override
		protected void onPostExecute(String file_url) {
			setListeners();
			results.setText("");
			input_number_numbers_entries.setText(sharedPref.getString(
					"EDIT NUMBERS NUMBER NUMBERS", ""));

			check_edit_shared_numbers.setChecked(sharedPref.getBoolean(
					"EDIT NUMBERS CHECK SHARED NUMBERS", false));
			check_edit_user_numbers.setChecked(sharedPref.getBoolean(
					"EDIT NUMBERS CHECK USER NUMBERS", false));
			status = sharedPref.getString("EDIT NUMBERS STATUS", "");
			if (status.equals("begin insert") || status.equals("begin update")) {
				if (status.equals("begin insert")) {
					startInsert();
				}
				if (status.equals("begin update")) {
					do_begin_update_numbers();
				}
				input_number.setText(sharedPref.getString(
						"EDIT NUMBERS INPUT NUMBER", ""));
				input_number_info.setText(sharedPref.getString(
						"EDIT NUMBERS INPUT NUMBER INFORMATION", ""));
				for (int i = 0; i < num_ent.length; i++) {
					if (num_ent[i] != null) {
						num_ent[i].setText(sharedPref.getString(
								"EDIT NUMBERS NUMBER ENTRY " + i, ""));
					}
				}
				for (int i = 0; i < mne_ent.length; i++) {
					if (mne_ent[i] != null) {
						mne_ent[i].setText(sharedPref.getString(
								"EDIT NUMBERS MNEMONIC ENTRY " + i, ""));
					}
				}
			}

		}

	}
	
	public void getTitles() {
		
	}

	public void getEntry() {
		status = "get entries";
		numbers_table = "";
		username = username_input.getText().toString();
		if (check_edit_shared_numbers.isChecked()) {
			numbers_table = tables.global_number_table;
		}
		if (check_edit_user_numbers.isChecked()) {
			if (logged_in == false) {
				results.setText("NOT LOGGED IN");
				return;
			}
			numbers_table = username + "_numbertable";
		}
		text = "";
		entries_layout.removeAllViews();
		RadioGroup rad_ents = new RadioGroup(this_act);
		id = -1;
		sav_id = -1;

		Cursor c_get = MainLfqActivity.getMiscDb().rawQuery(
				"SELECT * FROM " + numbers_table + " ORDER BY "
						+ user_numbers_table.Type, null);
		if (c_get.moveToFirst()) {
			text_list.clear();
			rad_ents.setOrientation(1);
			do {
				RadioButton rad = new RadioButton(this_act);
				params = new RelativeLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				rad.setText(c_get.getString(c_get.getColumnIndex("Number"))
						+ ": "
						+ c_get.getString(c_get.getColumnIndex("NumInf")));
				id = Integer.parseInt(c_get.getString(c_get
						.getColumnIndex("_id")));
				rad.setId(id);
				rad.setBackgroundResource(R.drawable.radio_button_divider);
				text_list.put(String.valueOf(id),
						c_get.getString(c_get.getColumnIndex("Number")));
				rad.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						sav_id = v.getId();
						results.setText("SELECTED "
								+ text_list.get(String.valueOf(sav_id)) + ".");
					}
				});
				rad_ents.addView(rad, params);
			} while (c_get.moveToNext());
			params = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			entries_layout.addView(rad_ents, params);
			results.setText("Got entries for " + numbers_table + ".");
		}
	}

	public void do_begin_update_numbers() {
		if (sav_id == -1 || !status.equals("get entries")) {
			results.setText("NOTHING SELECTED");
			return;
		}
		status = "begin update";
		numbers_table = "";
		username = username_input.getText().toString();
		if (check_edit_shared_numbers.isChecked()) {
			numbers_table = tables.global_number_table;
		}
		if (check_edit_user_numbers.isChecked()) {
			numbers_table = username + "_numbertable";
		}
		text = "";
		entries_layout.removeAllViews();

		Cursor c_get1 = MainLfqActivity.getMiscDb()
				.rawQuery(
						"SELECT * FROM " + numbers_table + " WHERE _id="
								+ sav_id, null);
		String[] mnespl = null;
		String[] mne1spl = null;
		if (c_get1.moveToFirst()) {
			mnespl = c_get1.getString(
					c_get1.getColumnIndex(user_numbers_table.NumWors)).split(
					">>>");
			num_entries = mnespl.length;
			input_number_numbers_entries.setText(String.valueOf(num_entries));
			startInsert();
			input_number.setText(c_get1.getString(c_get1
					.getColumnIndex(user_numbers_table.Number)));
			input_number_info.setText(c_get1.getString(c_get1
					.getColumnIndex(user_numbers_table.NumInf)));

			for (int i = 0; i < num_entries; i++) {
				mne1spl = mnespl[i].split("@@@");
				num_ent[i].setText(mne1spl[0]);
				if (mne1spl.length > 1) {
					mne_ent[i].setText(mne1spl[1]);
				}
			}
			results.setText("BEGIN UPDATE FOR "
					+ c_get1.getString(c_get1
							.getColumnIndex(user_numbers_table.Number)) + ".");
		}
	}

	public void startInsert() {

		if (input_number_numbers_entries.getText().toString().equals("")) {
			results.setText("MUST ENTER HOW MANY MNEMONIC ENTRIES.");
			return;
		}
		num_entries = Integer.parseInt(input_number_numbers_entries.getText()
				.toString());
		entries_layout.removeAllViews();
		int j;
		status = "begin insert";
		addEditNumberInfo();

		prompt_num_ent = new TextView[num_entries];
		prompt_mne_ent = new TextView[num_entries];
		num_ent = new EditText[num_entries];
		mne_ent = new EditText[num_entries];
		// ADD MAIN PROMPT:
		TextView prompt_ents = new TextView(this_act);
		prompt_ents.setText("INPUT EACH NUMBER'S MNEMONIC:");
		prompt_ents.setId(5);
		params = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.BELOW, input_number_info.getId());
		entries_layout.addView(prompt_ents, params);
		for (int i = 0; i < num_entries; i++) {
			j = i + 1;
			// ADD PROMPT NUMBER:
			prompt_num_ent[i] = new TextView(this_act);
			prompt_num_ent[i].setText(j + ") NUMBER:");
			prompt_num_ent[i].setId(j + 5);
			params = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			if (j > 1) {
				params.addRule(RelativeLayout.BELOW, mne_ent[i - 1].getId());
			} else {
				params.addRule(RelativeLayout.BELOW, prompt_ents.getId());
			}
			entries_layout.addView(prompt_num_ent[i], params);
			// ADD NUMBER INPUT:
			num_ent[i] = new EditText(this_act);
			num_ent[i].setRawInputType(InputType.TYPE_CLASS_NUMBER);
			num_ent[i].setId(num_entries + j + 5);
			num_ent[i].setMaxLines(1);
			params = new RelativeLayout.LayoutParams(200,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.BELOW, prompt_num_ent[i].getId());
			num_ent[i].setBackgroundResource(R.drawable.rounded_edittext_red);
			entries_layout.addView(num_ent[i], params);
			// ADD PROMPT MNEMONIC:
			prompt_mne_ent[i] = new TextView(this_act);
			prompt_mne_ent[i].setText(j + ") MNEMONIC:");
			prompt_mne_ent[i].setId((2 * num_entries) + j + 5);
			params = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.BELOW, num_ent[i].getId());
			entries_layout.addView(prompt_mne_ent[i], params);
			// ADD MNEMONIC INPUT:
			mne_ent[i] = new EditText(this_act);
			mne_ent[i].setId((3 * num_entries) + j + 5);
			mne_ent[i].setMaxLines(2);
			params = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.BELOW, prompt_mne_ent[i].getId());
			mne_ent[i].setBackgroundResource(R.drawable.rounded_edittext_red);
			entries_layout.addView(mne_ent[i], params);
		}
	}

	public void updateTable() {
		numbers_table = "";
		username = username_input.getText().toString();
		if (check_edit_shared_numbers.isChecked()) {
			numbers_table = tables.global_number_table;
			username = "";
			if (Helpers.getLoginStatus() == true) {
				username = Helpers.getUsername();
			}
		}
		if (check_edit_user_numbers.isChecked()) {
			numbers_table = username + "_numbertable";
		}
		num_entries = Integer.parseInt(input_number_numbers_entries.getText()
				.toString());
		cv.clear();
		cv.put(user_numbers_table.Number, input_number.getText().toString());
		cv.put(user_numbers_table.NumInf, input_number_info.getText()
				.toString());
		text = "";
		for (int i = 0; i < num_entries; i++) {
			text += num_ent[i].getText().toString() + "@@@"
					+ mne_ent[i].getText().toString();
			if (i != (num_entries - 1)) {
				text += ">>>";
			}
		}
		cv.put(user_numbers_table.NumWors, text);
		MainLfqActivity.getMiscDb().update(numbers_table, cv,
				"_id=" + sav_id, null);
		sql = "UPDATE " + Helpers.db_prefix + "newwords." + numbers_table
				+ " SET " + user_numbers_table.Number + "='"
				+ input_number.getText().toString() + ", "
				+ user_numbers_table.NumInf + "='"
				+ input_number_info.getText().toString() + "', "
				+ user_numbers_table.NumWors + "='" + text + "' WHERE ID='"
				+ sav_id + "'";
		// autoSync(sql, db, action, table, name, bool is_image, byte[]
		// image)
		autosync_text += Synchronize.autoSync(sql, "num_db", "update",
				numbers_table, String.valueOf(sav_id), false, null);
		results.setText("UPDATED NUMBER " + input_number.getText().toString()
				+ ".");
	}

	public void insertNumber() {
		text = "";
		if (input_number_numbers_entries.getText().toString().equals("")) {
			results.setText("MUST ENTER NUMBER OF ENTRIES.");
			return;
		}
		numbers_table = "";
		username = username_input.getText().toString();
		if (check_edit_shared_numbers.isChecked()) {
			numbers_table = tables.global_number_table;
		}
		if (check_edit_user_numbers.isChecked()) {
			numbers_table = username + "_numbertable";
		}
		text = "";
		num_entries = Integer.parseInt(input_number_numbers_entries.getText()
				.toString());
		cv.clear();
		cv.put(user_numbers_table.Number, input_number.getText().toString());
		cv.put(user_numbers_table.NumInf, input_number_info.getText()
				.toString());
		for (int i = 0; i < num_entries; i++) {
			text += num_ent[i].getText().toString() + "@@@"
					+ mne_ent[i].getText().toString();
			if (i != (num_entries - 1)) {
				text += ">>>";
			}
		}
		cv.put(user_numbers_table.NumWors, text);
		MainLfqActivity.getMiscDb().insert(numbers_table, null, cv);
		sql = "INSERT INTO " + Helpers.db_prefix + "newwords." + numbers_table
				+ "(" + user_numbers_table.Number + ","
				+ user_numbers_table.NumInf + "," + user_numbers_table.NumWors
				+ ") VALUES('" + input_number.getText().toString() + "','"
				+ input_number_info.getText().toString() + "','" + text + "')";
		// autoSync(sql, db, action, table, name, bool is_image, byte[]
		// image)
		autosync_text += Synchronize.autoSync(sql, "nu,_db", "insert",
				numbers_table, "", false, null);
		results.setText("INSERTED NUMBER " + input_number.getText().toString()
				+ "." + autosync_text);

	}

	public void deleteNumber() {
		if (sav_id == -1 || !status.equals("get entries")) {
			results.setText("NOTHING SELECTED");
			return;
		}
		numbers_table = "";
		username = username_input.getText().toString();
		if (check_edit_shared_numbers.isChecked()) {
			numbers_table = tables.global_number_table;
			username = "";
			if (Helpers.getLoginStatus() == true) {
				username = Helpers.getUsername();
			}
		}
		if (check_edit_user_numbers.isChecked()) {
			numbers_table = username + "_numbertable";
		}
		Cursor c_get1 = MainLfqActivity.getMiscDb()
				.rawQuery(
						"SELECT * FROM " + numbers_table + " WHERE _id="
								+ sav_id, null);
		if (c_get1.moveToFirst()) {
			text = c_get1.getString(c_get1
					.getColumnIndex(user_numbers_table.Number));
		}
		MainLfqActivity.getMiscDb().execSQL(
				"DELETE FROM " + numbers_table + " WHERE _id=" + sav_id);
		sql = "DELETE FROM " + Helpers.db_prefix + "newwords." + numbers_table
				+ " WHERE _id=" + sav_id;
		// autoSync(sql, db, action, table, name, bool is_image, byte[]
		// image)
		autosync_text += Synchronize.autoSync(sql, "num_db", "delete",
				numbers_table, String.valueOf(sav_id), false, null);
		results.setText("DELETED " + text + "." + autosync_text);
		getEntry();
	}
	
	public void setVisibilities(){
		
	}

}
