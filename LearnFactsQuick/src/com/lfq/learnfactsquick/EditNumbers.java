package com.lfq.learnfactsquick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.lfq.learnfactsquick.Constants.cols.global_numbers;
import com.lfq.learnfactsquick.Constants.cols.user_numbers;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout.LayoutParams;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class EditNumbers extends Activity {
	private RelativeLayout entries_layout;
	private List<RelativeLayout> entries;
	private LinearLayout top_layout;
	private TextView results, login_status, prompt_total_number_tv,
			total_number_tv, prompt_input_number, prompt_input_mnemonic,
			prompt_input_number_info;
	private EditText username_input, password_input,
			input_number_numbers_entries, input_number, input_mnemonic,
			input_number_info, input_number_title;
	private RadioButton check_edit_shared_numbers, check_edit_user_numbers;
	private Button do_login, do_logout, do_edit_numbers, do_backup, add_after;
	private RadioButton check_update_numbers, check_delete_numbers,
			check_insert_numbers;
	private Spinner select_number_title;
	private ArrayAdapter titlesAdapter;

	private RelativeLayout.LayoutParams params;
	private LinearLayout.LayoutParams button_params;

	private String text, numbers_table, username, password;
	private String[] textspl;
	private int num_entries;
	private TextView[] prompt_num_ent, prompt_mne_ent, prompt_mne_inf;
	private EditText[] num_ent, mne_ent, inf_ent;
	private Button[] delete_entry, insert_above_entry;
	private int id, sav_id;
	private HashMap<String, String> text_list;
	private Boolean logged_in;
	private Helpers h;
	private Activity this_act;
	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;
	private String autosync_text, sql;
	private ContentValues cv;
	private int view_id;

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
		view_id = 1;
		cv = new ContentValues();
		h = new Helpers(this_act);
		text_list = new HashMap<String, String>();
		logged_in = false;
		entries = new ArrayList<RelativeLayout>();
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
		if (check_update_numbers.isChecked()
				|| (check_insert_numbers.isChecked() && input_number_numbers_entries
						.getText().toString().length() > 0)) {
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
		do_backup = (Button) findViewById(R.id.do_edit_numbers_backup);
		add_after = (Button) findViewById(R.id.edit_numbers_add_after);

		// EDITTEXTS:
		username_input = (EditText) findViewById(R.id.edit_numbers_username);
		password_input = (EditText) findViewById(R.id.edit_numbers_password);
		input_number_title = (EditText) findViewById(R.id.input_numbers_table);
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

		// SPINNERS:
		select_number_title = (Spinner) findViewById(R.id.select_edit_numbers_title);
		titlesAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, new ArrayList<String>());
		titlesAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		select_number_title.setAdapter(titlesAdapter);
	}

	public void loadButtons() {
		do_login.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		do_logout.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		do_edit_numbers.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		do_backup.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		add_after.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
	}

	public void setListeners() {
		do_backup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				top_layout.setVisibility(View.VISIBLE);
				do_backup.setVisibility(View.GONE);
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

		check_edit_shared_numbers
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						getTitles(tables.global_numbers);
					}
				});
		check_edit_user_numbers.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (Helpers.getLoginStatus() == true) {
					getTitles(tables.user_numbers);
				} else {
					check_edit_user_numbers.setChecked(false);
				}
			}
		});

		check_update_numbers.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				results.setText("");
				setVisibilities();
				do_begin_update_numbers();
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
		// add_number.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// num_entries++;
		// wordspl.add("");
		// infospl.add("");
		// doAddEntries(type, word.size(), word.size() + 1);
		// }
		// });
		// Number,NumInf,NumWors,Type

		// remove_number.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		/*
		 * System.out.println("infospl size=" + infospl.size());
		 * System.out.println("wordspl size=" + wordspl.size());
		 * System.out.println("mnespl size=" + mnespl.size()); if (num_entries >
		 * 0) { num_entries--; } if (type != "anagram") {
		 * scroll.removeView(tv_mnemonic.get(tv_mnemonic.size() - 1));
		 * tv_mnemonic.remove(tv_mnemonic.size() - 1);
		 * scroll.removeView(mnemonic.get(mnemonic.size() - 1));
		 * mnemonic.remove(mnemonic.size() - 1); }
		 * scroll.removeView(tv_word.get(tv_word.size() - 1));
		 * tv_word.remove(tv_word.size() - 1);
		 * scroll.removeView(word.get(word.size() - 1)); word.remove(word.size()
		 * - 1); scroll.removeView(tv_info.get(tv_info.size() - 1));
		 * tv_info.remove(tv_info.size() - 1);
		 * scroll.removeView(info.get(info.size() - 1)); info.remove(info.size()
		 * - 1); if (!type.equals("anagram")) { if (mnespl.size() > 0) {
		 * mnespl.remove(mnespl.size() - 1); } } if (wordspl.size() > 0) {
		 * wordspl.remove(wordspl.size() - 1); } if (infospl.size() > 0) {
		 * infospl.remove(infospl.size() - 1); }
		 */

		// }

		// });

		select_number_title
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						if (check_update_numbers.isChecked()) {
							do_begin_update_numbers();
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						if (check_update_numbers.isChecked()) {
							do_begin_update_numbers();
						}
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
		add_after.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				addEditNumberInfo();
			}
		});
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
			check_update_numbers.setChecked(sharedPref.getBoolean(
					"EDIT NUMBERS CHECK UPDATE NUMBERS", false));
			check_insert_numbers.setChecked(sharedPref.getBoolean(
					"EDIT NUMBERS CHECK INSERT NUMBERS", false));
			if (check_update_numbers.isChecked()
					|| check_insert_numbers.isChecked()) {
				if (check_insert_numbers.isChecked()) {
					startInsert();
				}
				if (check_update_numbers.isChecked()) {
					do_begin_update_numbers();
				}
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
			do_backup.setVisibility(View.GONE);
			setVisibilities();
		}

	}

	public void getTitles(String table) {
		System.out.println("getTitles called");
		titlesAdapter.clear();
		Cursor c_tits = null;
		if (table.equals(tables.global_numbers)) {
			c_tits = MainLfqActivity.getMiscDb()
					.rawQuery(
							"SELECT DISTINCT " + global_numbers.Title
									+ " FROM " + table + " ORDER BY "
									+ global_numbers.Title, null);
		} else {
			c_tits = MainLfqActivity.getMiscDb().rawQuery(
					"SELECT DISTINCT " + user_numbers.Title + " FROM " + table
							+ " WHERE " + user_numbers.Username
							+ "=? ORDER BY " + user_numbers.Title,
					new String[] { username });
		}
		if (c_tits.moveToFirst()) {
			do {
				titlesAdapter.add(c_tits.getString(0));
			} while (c_tits.moveToNext());
		}
	}

	public void addEditNumberInfo() {
		num_entries++;
		System.out.println("num_entries = " + num_entries);
		// ADD PROMPT INPUT NUMBER
		prompt_input_number = new TextView(this_act);
		params = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		if (num_entries > 0) {
			params.addRule(RelativeLayout.BELOW,
					inf_ent[num_entries - 1].getId());
		} else {
			params.addRule(RelativeLayout.BELOW, total_number_tv.getId());
		}
		prompt_input_number.setText(num_entries + ") NUMBER:");
		prompt_input_number.setId(view_id++);
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
		input_number.setId(view_id++);
		entries_layout.addView(input_number, params);

		// ADD PROMPT INPUT MNEMONIC
		prompt_input_mnemonic = new TextView(this_act);
		params = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.BELOW, input_number.getId());
		prompt_input_mnemonic.setText(num_entries + ") MNEMONIC");
		prompt_input_mnemonic.setId(view_id++);
		entries_layout.addView(prompt_input_mnemonic, params);

		// ADD INPUT MNEMONIC
		input_mnemonic = new EditText(this_act);
		params = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.BELOW, prompt_input_mnemonic.getId());
		input_mnemonic.setRawInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
		input_mnemonic.setMaxLines(3);
		input_mnemonic.setGravity(Gravity.TOP);
		input_mnemonic.setBackgroundResource(R.drawable.rounded_edittext_red);
		input_mnemonic.setId(view_id++);
		entries_layout.addView(input_mnemonic, params);

		// ADD PROMPT NUMBER INFO
		prompt_input_number_info = new TextView(this_act);
		params = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.BELOW, input_mnemonic.getId());
		prompt_input_number_info.setText(num_entries + ") INFO:");
		prompt_input_number_info.setId(view_id++);
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
		input_number_info.setId(view_id++);
		input_number_info.setGravity(Gravity.TOP);
		entries_layout.addView(input_number_info, params);
	}

	public void resetEntries() {
		entries_layout.removeAllViews();
		System.out.println("entries.size()="+entries.size());
		for (int i = 0; i < entries.size(); i++) {
			// ADD THE RELATIVE LAYOUT:
			params = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			if (i > 0) {
				params.addRule(RelativeLayout.BELOW, entries.get(i - 1).getId());
			}
			entries_layout.addView(entries.get(i));
		}
	}

	public void addRemovetNumber(boolean is_add, int ent_num) {
		System.out.println("is_add = " + is_add);
		System.out.println("ent_num = " + ent_num);
		// ADD PROMPT INPUT NUMBER
		if (is_add == false) {
			entries.remove(ent_num - 1);
			resetEntries();
		} else {
			RelativeLayout rl = new RelativeLayout(this_act);
			rl.setId(view_id++);
			prompt_input_number = new TextView(this_act);
			params = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			prompt_input_number.setText(ent_num + ") NUMBER:");
			prompt_input_number.setId(view_id++);
			rl.addView(prompt_input_number, params);
			// ADD INPUT NUMBER
			input_number = new EditText(this_act);
			params = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.BELOW, prompt_input_number.getId());
			input_number
					.setRawInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
			input_number.setMaxLines(3);
			input_number.setGravity(Gravity.TOP);
			input_number.setBackgroundResource(R.drawable.rounded_edittext_red);
			input_number.setId(view_id++);
			rl.addView(input_number, params);
			
			// ADD INSERT ABOVE ENTRY BUTTON:
			Button insert_above = new Button(this_act);
			params = new RelativeLayout.LayoutParams(100,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			insert_above.setId(view_id++);
			insert_above.setText("+");
			insert_above.setTextSize(24);
			insert_above.setBackgroundResource(sharedPref.getInt(
					"BG Button", R.drawable.button));
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			final int final_ent_num = ent_num;
			insert_above
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							addRemovetNumber(true, final_ent_num - 1);
						}
					});
			rl.addView(insert_above, params);
			// ADD DELETE ENTRY BUTTON:
			Button delete_entry = new Button(this_act);
			params = new RelativeLayout.LayoutParams(100,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			delete_entry.setId(view_id++);
			delete_entry.setText("-");
			delete_entry.setTextSize(24);
			delete_entry.setBackgroundResource(sharedPref.getInt(
					"BG Button", R.drawable.button));
			params.addRule(RelativeLayout.LEFT_OF,
					insert_above.getId());
			delete_entry.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					addRemovetNumber(false, final_ent_num);
				}
			});
			rl.addView(delete_entry, params);			

			// ADD PROMPT INPUT MNEMONIC
			prompt_input_mnemonic = new TextView(this_act);
			params = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.BELOW, input_number.getId());
			prompt_input_mnemonic.setText(ent_num + ") MNEMONIC");
			prompt_input_mnemonic.setId(view_id++);
			rl.addView(prompt_input_mnemonic, params);

			// ADD INPUT MNEMONIC
			input_mnemonic = new EditText(this_act);
			params = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.BELOW, prompt_input_mnemonic.getId());
			input_mnemonic
					.setRawInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
			input_mnemonic.setMaxLines(3);
			input_mnemonic.setGravity(Gravity.TOP);
			input_mnemonic
					.setBackgroundResource(R.drawable.rounded_edittext_red);
			input_mnemonic.setId(view_id++);
			rl.addView(input_mnemonic, params);

			// ADD PROMPT NUMBER INFO
			prompt_input_number_info = new TextView(this_act);
			params = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.BELOW, input_mnemonic.getId());
			prompt_input_number_info.setText(ent_num + ") INFO:");
			prompt_input_number_info.setId(view_id++);
			rl.addView(prompt_input_number_info, params);
			// ADD INPUT INFO
			input_number_info = new EditText(this_act);
			params = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.BELOW,
					prompt_input_number_info.getId());
			input_number_info
					.setBackgroundResource(R.drawable.rounded_edittext_red);
			input_number_info
					.setRawInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
			input_number_info.setMaxLines(3);
			input_number_info.setId(view_id++);
			input_number_info.setGravity(Gravity.TOP);
			rl.addView(input_number_info, params);
			entries.add((ent_num - 1), rl);
			resetEntries();
			setVisibilities();
		}
	}

	public void do_begin_update_numbers() {
		// SHOW FULL SCREEN:
		view_id = 1;
		top_layout.setVisibility(View.GONE);
		do_backup.setVisibility(View.VISIBLE);
		// ----------------------------------------
		numbers_table = "";
		username = username_input.getText().toString();
		boolean is_date = false;
		if (check_edit_shared_numbers.isChecked()) {
			numbers_table = tables.global_numbers;
			is_date = true;
		}
		if (check_edit_user_numbers.isChecked()) {
			numbers_table = tables.user_numbers;
		}
		text = "";
		entries_layout.removeAllViews();

		// ADD PROMPT INPUT NUMBER
		prompt_total_number_tv = new TextView(this_act);
		params = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		prompt_total_number_tv.setText("TOTAL NUMBER:");
		prompt_total_number_tv.setId(view_id++);
		entries_layout.addView(prompt_total_number_tv, params);
		// ADD INPUT NUMBER
		total_number_tv = new TextView(this_act);
		params = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.BELOW, prompt_total_number_tv.getId());
		total_number_tv.setBackgroundResource(R.drawable.rounded_edittext_red);
		total_number_tv.setId(view_id++);
		entries_layout.addView(total_number_tv, params);

		String total_number = "";
		Cursor c_get1 = MainLfqActivity.getMiscDb()
				.rawQuery(
						"SELECT * FROM " + numbers_table + " WHERE "
								+ global_numbers.Title + "=?",
						new String[] { select_number_title.getSelectedItem()
								.toString() });
		if (c_get1.moveToFirst()) {
			results.setText("BEGIN UPDATE FOR "
					+ c_get1.getString(c_get1
							.getColumnIndex(global_numbers.Title)) + ".");
			num_entries = c_get1.getCount();
			System.out.println("num_entries=" + num_entries);
			input_number_numbers_entries.setText(String.valueOf(num_entries));
			startInsert();
			int i = 0;
			if (numbers_table.equals(tables.user_numbers)
					&& c_get1.getString(
							c_get1.getColumnIndex(user_numbers.Type)).equals(
							"HISTORICAL_NUMBERS")) {
				is_date = true;
			}
			do {
				total_number += String.valueOf(c_get1.getString(c_get1
						.getColumnIndex(user_numbers.Entry)));
				num_ent[i].setText(c_get1.getString(c_get1
						.getColumnIndex(user_numbers.Entry)));
				System.out.println("SETTING mne_ent["
						+ i
						+ "] TO="
						+ c_get1.getString(c_get1
								.getColumnIndex(user_numbers.Entry_Mnemonic)));
				mne_ent[i].setText(c_get1.getString(c_get1
						.getColumnIndex(user_numbers.Entry_Mnemonic)));
				inf_ent[i].setText(c_get1.getString(c_get1
						.getColumnIndex(user_numbers.Entry_Info)));
				i++;
			} while (c_get1.moveToNext());
			if (is_date == true && total_number.length() == 8) {
				total_number = total_number.substring(0, 4) + "/"
						+ total_number.substring(4, 6) + "/"
						+ total_number.substring(6, 8);
			}
			total_number_tv.setText(total_number);
		}
		setVisibilities();
	}

	public void startInsert() {
		if (input_number_numbers_entries.getText().toString().equals("")) {
			results.setText("MUST ENTER HOW MANY MNEMONIC ENTRIES.");
			return;
		}
		num_entries = Integer.parseInt(input_number_numbers_entries.getText()
				.toString());
		System.out.println("startInsert num_entries=" + num_entries);
		entries.clear();
		prompt_num_ent = new TextView[num_entries];
		prompt_mne_ent = new TextView[num_entries];
		prompt_mne_inf = new TextView[num_entries];
		num_ent = new EditText[num_entries];
		mne_ent = new EditText[num_entries];
		inf_ent = new EditText[num_entries];
		delete_entry = new Button[num_entries];
		insert_above_entry = new Button[num_entries];

		final int num_eles = 8;
		RelativeLayout rl;
		for (int i = 0; i < num_entries; i++) {
			final int final_ent_num = i;
			rl = new RelativeLayout(this_act);
			rl.setId(view_id++);
			// ADD PROMPT NUMBER:
			prompt_num_ent[i] = new TextView(this_act);
			prompt_num_ent[i].setText((i + 1) + ") NUMBER:");
			prompt_num_ent[i].setId(view_id++);
			params = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			rl.addView(prompt_num_ent[i], params);
			// ADD NUMBER INPUT:
			num_ent[i] = new EditText(this_act);
			num_ent[i].setRawInputType(InputType.TYPE_CLASS_NUMBER);
			num_ent[i].setId(view_id++);
			num_ent[i].setMaxLines(1);
			params = new RelativeLayout.LayoutParams(200,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.BELOW, prompt_num_ent[i].getId());
			num_ent[i].setBackgroundResource(R.drawable.rounded_edittext_red);
			rl.addView(num_ent[i], params);
			// ADD INSERT ABOVE ENTRY BUTTON:
			insert_above_entry[i] = new Button(this_act);
			params = new RelativeLayout.LayoutParams(100,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			insert_above_entry[i].setId(view_id++);
			insert_above_entry[i].setText("+");
			insert_above_entry[i].setTextSize(24);
			insert_above_entry[i].setBackgroundResource(sharedPref.getInt(
					"BG Button", R.drawable.button));
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			insert_above_entry[i]
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							addRemovetNumber(true, final_ent_num + 1);
						}
					});
			rl.addView(insert_above_entry[i], params);
			// ADD DELETE ENTRY BUTTON:
			delete_entry[i] = new Button(this_act);
			params = new RelativeLayout.LayoutParams(100,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			delete_entry[i].setId(view_id++);
			delete_entry[i].setText("-");
			delete_entry[i].setTextSize(24);
			// delete_entry[i].setBackgroundResource(R.drawable.minus);
			delete_entry[i].setBackgroundResource(sharedPref.getInt(
					"BG Button", R.drawable.button));
			params.addRule(RelativeLayout.LEFT_OF,
					insert_above_entry[i].getId());
			delete_entry[i].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					addRemovetNumber(false, final_ent_num);
				}
			});
			rl.addView(delete_entry[i], params);

			// ADD PROMPT MNEMONIC:
			prompt_mne_ent[i] = new TextView(this_act);
			prompt_mne_ent[i].setText((i + 1) + ") MNEMONIC:");
			prompt_mne_ent[i].setId(view_id++);
			params = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.BELOW, num_ent[i].getId());
			rl.addView(prompt_mne_ent[i], params);
			// ADD MNEMONIC INPUT:
			System.out.println("Creating new mne_ent!!!!!!!!!!!11 IN LOOP");
			mne_ent[i] = new EditText(this_act);
			mne_ent[i].setId(view_id++);
			mne_ent[i].setMaxLines(2);
			params = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.BELOW, prompt_mne_ent[i].getId());
			mne_ent[i].setBackgroundResource(R.drawable.rounded_edittext_red);
			rl.addView(mne_ent[i], params);
			// ADD PROMPT MNEMONIC INFORMATION:
			prompt_mne_inf[i] = new TextView(this_act);
			prompt_mne_inf[i].setText((i + 1) + ") INFO:");
			prompt_mne_inf[i].setId(view_id++);
			params = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.BELOW, mne_ent[i].getId());
			rl.addView(prompt_mne_inf[i], params);
			// ADD MNEMONIC INFORMATION:
			inf_ent[i] = new EditText(this_act);
			inf_ent[i].setId(view_id++);
			inf_ent[i].setMaxLines(2);
			params = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.BELOW, prompt_mne_inf[i].getId());
			inf_ent[i].setBackgroundResource(R.drawable.rounded_edittext_red);
			rl.addView(inf_ent[i], params);
			// ADD THE RELATIVE LAYOUT:
			params = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			if (i > 0) {
				params.addRule(RelativeLayout.BELOW, entries.get(i - 1).getId());
			}else{
				params.addRule(RelativeLayout.BELOW, total_number_tv.getId());
			}
			entries_layout.addView(rl, params);
			entries.add(rl);
		}
	}

	public void updateTable() {
		numbers_table = "";
		username = username_input.getText().toString();
		cv.clear();
		String where_username = "";
		if (check_edit_shared_numbers.isChecked()) {
			numbers_table = tables.global_numbers;
		}
		if (check_edit_user_numbers.isChecked()) {
			numbers_table = tables.user_numbers;
			username = "";
			if (Helpers.getLoginStatus() == true) {
				username = Helpers.getUsername();
			}
			cv.put(user_numbers.Username, username);
			where_username = " AND " + user_numbers.Username + "='" + username
					+ "'";
		}
		num_entries = Integer.parseInt(input_number_numbers_entries.getText()
				.toString());
		cv.put(user_numbers.Title, input_number.getText().toString());
		cv.put(user_numbers.Entry_Number, input_number_info.getText()
				.toString());
		text = "";
		for (int i = 0; i < num_entries; i++) {
			cv.put(global_numbers.Entry, num_ent[i].getText().toString());
			cv.put(global_numbers.Entry_Mnemonic, mne_ent[i].getText()
					.toString());
			cv.put(global_numbers.Entry_Index, (i + 1));
			MainLfqActivity.getMiscDb().update(
					numbers_table,
					cv,
					global_numbers.Title + "=? AND "
							+ global_numbers.Entry_Index + "=?",
					new String[] {
							select_number_title.getSelectedItem().toString(),
							String.valueOf((i + 1)) });
			sql = "UPDATE " + Helpers.db_prefix + "misc." + numbers_table
					+ " SET " + user_numbers.Title + "='"
					+ input_number.getText().toString() + ", "
					+ user_numbers.Entry + "='"
					+ num_ent[i].getText().toString() + user_numbers.Entry_Info
					+ "='" + input_number_info.getText().toString() + "', "
					+ user_numbers.Entry_Mnemonic + "='"
					+ mne_ent[i].getText().toString() + "' WHERE "
					+ global_numbers.Title + "='"
					+ select_number_title.getSelectedItem().toString()
					+ "' AND " + global_numbers.Entry_Index + "='" + (i + 1)
					+ "'" + where_username;
			// autoSync(sql, db, action, table, name, bool is_image, byte[]
			// image)
			autosync_text += Synchronize.autoSync(sql, "misc_db", "update",
					numbers_table, select_number_title.getSelectedItem()
							.toString(), false, null);
		}
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
		String username_value = "", username_column = "";
		if (check_edit_shared_numbers.isChecked()) {
			numbers_table = tables.global_numbers;
		}
		if (check_edit_user_numbers.isChecked()) {
			numbers_table = tables.user_numbers;
			if (Helpers.getLoginStatus() == true) {
				username = Helpers.getUsername();
			}
			cv.put(user_numbers.Username, username);
			String type = "";
			cv.put(user_numbers.Type, type);
			username_value = username + "','";
			username_column = "," + user_numbers.Username;
		}
		text = "";
		num_entries = Integer.parseInt(input_number_numbers_entries.getText()
				.toString());
		// GET Entry_Number:---------------------------------------
		int entry_number = 0;
		Cursor c_get_ent_num = MainLfqActivity.getMiscDb().rawQuery(
				"SELECT MAX(" + global_numbers.Entry_Number + ") FROM "
						+ numbers_table, null);
		if (c_get_ent_num.moveToFirst()) {
			entry_number = c_get_ent_num.getInt(0) + 1;
		}
		cv.clear();
		cv.put(global_numbers.Title, input_number.getText().toString());
		cv.put(global_numbers.Entry_Number, entry_number);

		for (int i = 0; i < num_entries; i++) {
			cv.put(global_numbers.Entry, num_ent[i].getText().toString());
			cv.put(global_numbers.Entry_Mnemonic, mne_ent[i].getText()
					.toString());
			cv.put(global_numbers.Entry_Index, (i + 1));
			MainLfqActivity.getMiscDb().insert(numbers_table, null, cv);
			sql = "INSERT INTO  " + Helpers.db_prefix + "misc." + numbers_table
					+ "(" + username_column + global_numbers.Title + ","
					+ global_numbers.Entry_Number + ","
					+ global_numbers.Entry_Index + "," + global_numbers.Entry
					+ "," + global_numbers.Entry_Mnemonic + ") VALUES('"
					+ username_value + input_number.getText().toString()
					+ "','" + entry_number + "','" + (i + 1) + "','"
					+ num_ent[i].getText().toString() + "','"
					+ mne_ent[i].getText().toString() + "')";
			// autoSync(sql, db, action, table, name, bool is_image, byte[]
			// image)
			autosync_text += Synchronize.autoSync(sql, "misc_db", "insert",
					numbers_table, String.valueOf(sav_id), false, null);
		}
		results.setText("INSERTED NUMBER " + input_number.getText().toString()
				+ "." + autosync_text);

	}

	public void deleteNumber() {
		numbers_table = "";
		username = username_input.getText().toString();
		if (check_edit_shared_numbers.isChecked()) {
			numbers_table = tables.global_numbers;
		}
		if (check_edit_user_numbers.isChecked()) {
			numbers_table = tables.user_numbers;
			if (Helpers.getLoginStatus() == true) {
				username = Helpers.getUsername();
			}
		}
		Cursor c_get1 = MainLfqActivity.getMiscDb()
				.rawQuery(
						"SELECT * FROM " + numbers_table + " WHERE "
								+ global_numbers.Title + "=?",
						new String[] { select_number_title.getSelectedItem()
								.toString() });
		if (c_get1.moveToFirst()) {
			text = c_get1
					.getString(c_get1.getColumnIndex(global_numbers.Title));
		}
		MainLfqActivity.getMiscDb().execSQL(
				"DELETE FROM " + numbers_table + " WHERE "
						+ global_numbers.Title + "='"
						+ select_number_title.getSelectedItem().toString()
						+ "'");
		sql = "DELETE FROM " + Helpers.db_prefix + "misc." + numbers_table
				+ " WHERE " + global_numbers.Title + "='"
				+ select_number_title.getSelectedItem().toString() + "'";
		// autoSync(sql, db, action, table, name, bool is_image, byte[]
		// image)
		autosync_text += Synchronize.autoSync(sql, "misc_db", "delete",
				numbers_table, String.valueOf(sav_id), false, null);
		results.setText("DELETED " + text + "." + autosync_text);
	}

	public void setVisibilities() {
		if (check_insert_numbers.isChecked()) {
			input_number_title.setVisibility(View.VISIBLE);
			select_number_title.setVisibility(View.GONE);
		} else {
			input_number_title.setVisibility(View.GONE);
			select_number_title.setVisibility(View.VISIBLE);
		}
		if (!check_delete_numbers.isChecked()) {
			if (num_entries > 0) {
				add_after.setVisibility(View.VISIBLE);
			} else {
				add_after.setVisibility(View.GONE);
			}
		}
	}

}
