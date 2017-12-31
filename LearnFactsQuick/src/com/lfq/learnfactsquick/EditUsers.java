package com.lfq.learnfactsquick;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.lfq.learnfactsquick.MultiSpinner.multispinnerListener;

public class EditUsers extends Activity implements multispinnerListener {
	private LinearLayout forgot_password_layout, change_password_layout,
			new_user_layout;
	private TextView prompt_not_loggedin;
	private TextView results, delete_reviewed_results, add_reviewed_results,
			show_complete_words, show_complete_tables;
	private TextView found_delete_words_results, found_delete_tables_results,
			found_add_words_results, found_add_tables_results,
			enter_new_user_results, forgot_password_results,
			change_password_results, forgot_question;
	private EditText username_input, password_input, forgot_answer_input,
			new_question_input, new_answer_input, edit_question_input,
			edit_answer_input, new_password_input;
	private Button do_edit_user, user_login, user_logout, do_add_reviewed,
			do_delete_reviewed, get_password, get_forgot_password,
			change_password, do_enter_new_user;
	private RadioButton check_add_user, check_delete_user, check_edit_user;
	private RadioButton check_delete_tables, check_delete_words,
			check_add_tables, check_add_words;
	private Spinner select_delete_reviewed_tables, select_add_reviewed_tables,
			select_complete_tables, select_complete_words;
	private MultiSpinner select_delete_reviewed_multi_tables,
			select_delete_reviewed_multi_words,
			select_add_reviewed_multi_tables, select_add_reviewed_multi_words;
	
	private ArrayAdapter<String> deleteTablesAdapter, addTablesAdapter,
			completeTablesAdapter, completeWordsAdapter;

	private List<String> tables_list;
	private String words = "";
	private String username, password, new_password, user_reviewedwords_table,
			user_newwords_table;
	private List<String> delete_tables_list = new ArrayList<String>();
	private List<String> add_tables_list = new ArrayList<String>();
	private List<String> delete_words_list = new ArrayList<String>();
	private List<String> add_words_list = new ArrayList<String>();
	private List<String> list = new ArrayList<String>();
	private List<Boolean> upd_results = new ArrayList<Boolean>();
	private List<Integer> upd_app_results = new ArrayList<Integer>();
	private boolean[] checked;
	private String text, result_text;
	private Cursor c = null, c2 = null;
	private ContentValues cv;
	private Boolean logged_in;
	private Helpers h;
	private Activity this_act;
	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;

	private MenuItem menu_item_autosync_on, menu_item_autosync_off;
	private String question, answer;
	private String autosync_text, sql;
	private static Boolean is_database_load;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this_act = this;
		is_database_load = false;
		h = new Helpers(this_act);
		sharedPref = getSharedPreferences(
				getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		editor = sharedPref.edit();
		text = "";
		answer = "";
		question = "";
		autosync_text = "";
		sql = "";
		tables_list = new ArrayList<String>();
		cv = new ContentValues();
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

		editor.putString("EDIT USERS USERNAME INPUT", username_input.getText()
				.toString());

		editor.putBoolean("EDIT USERS CHECK ADD USER",
				check_add_user.isChecked());
		editor.putBoolean("EDIT USERS CHECK DELETE USER",
				check_delete_user.isChecked());
		editor.putBoolean("EDIT USERS CHECK EDIT USER",
				check_edit_user.isChecked());
		editor.putBoolean("EDIT USERS CHECK DELETE TABLES",
				check_delete_tables.isChecked());
		editor.putBoolean("EDIT USERS CHECK DELETE WORDS",
				check_delete_words.isChecked());
		editor.putBoolean("EDIT USERS CHECK ADD TABLES",
				check_add_tables.isChecked());
		editor.putBoolean("EDIT USERS CHECK ADD WORDS",
				check_add_words.isChecked());

		editor.commit();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		saveChanges();
		super.onConfigurationChanged(newConfig);
	}

	public void setViews() {
		// LAYOUTS:
		forgot_password_layout = (LinearLayout) findViewById(R.id.forgot_password_layout);
		change_password_layout = (LinearLayout) findViewById(R.id.change_password_layout);
		new_user_layout = (LinearLayout) findViewById(R.id.new_user_layout);
		new_user_layout.setVisibility(View.GONE);
		forgot_password_layout.setVisibility(View.GONE);
		change_password_layout.setVisibility(View.GONE);

		// BUTTONS:
		do_edit_user = (Button) findViewById(R.id.do_edit_user);
		user_login = (Button) findViewById(R.id.user_login);
		user_logout = (Button) findViewById(R.id.user_logout);
		do_delete_reviewed = (Button) findViewById(R.id.do_delete_reviewed);
		do_add_reviewed = (Button) findViewById(R.id.do_add_reviewed);
		get_password = (Button) findViewById(R.id.get_password);
		get_forgot_password = (Button) findViewById(R.id.get_forgot_password);
		change_password = (Button) findViewById(R.id.do_change_password);
		do_enter_new_user = (Button) findViewById(R.id.do_enter_new_user);

		// TEXTVIEWS:
		prompt_not_loggedin = (TextView) findViewById(R.id.prompt_not_loggedin_edit_users);
		results = (TextView) findViewById(R.id.edit_user_results);
		delete_reviewed_results = (TextView) findViewById(R.id.delete_reviewed_results);
		add_reviewed_results = (TextView) findViewById(R.id.add_reviewed_results);
		show_complete_words = (TextView) findViewById(R.id.show_complete_words);
		show_complete_tables = (TextView) findViewById(R.id.show_complete_tables);
		found_delete_words_results = (TextView) findViewById(R.id.edit_users_found_delete_words_results);
		found_delete_tables_results = (TextView) findViewById(R.id.edit_users_found_delete_tables_results);
		found_add_words_results = (TextView) findViewById(R.id.edit_users_found_add_words_results);
		found_add_tables_results = (TextView) findViewById(R.id.edit_users_found_add_tables_results);
		enter_new_user_results = (TextView) findViewById(R.id.enter_new_user_results);
		forgot_password_results = (TextView) findViewById(R.id.forgot_password_results);
		change_password_results = (TextView) findViewById(R.id.change_password_results);

		// EDITTEXTS:
		username_input = (EditText) findViewById(R.id.users_username);
		password_input = (EditText) findViewById(R.id.users_password);
		forgot_answer_input = (EditText) findViewById(R.id.forgot_password_answer);
		edit_question_input = (EditText) findViewById(R.id.edit_password_question);
		edit_answer_input = (EditText) findViewById(R.id.edit_password_answer);
		new_question_input = (EditText) findViewById(R.id.edit_users_new_question_input);
		new_answer_input = (EditText) findViewById(R.id.edit_users_new_answer_input);
		new_password_input = (EditText) findViewById(R.id.input_new_password);

		// RADIOBUTTONS:
		check_add_user = (RadioButton) findViewById(R.id.check_add_user);
		check_delete_user = (RadioButton) findViewById(R.id.check_delete_user);
		check_edit_user = (RadioButton) findViewById(R.id.check_edit_user);
		check_delete_tables = (RadioButton) findViewById(R.id.check_delete_tables);
		check_delete_words = (RadioButton) findViewById(R.id.check_delete_words);
		check_add_tables = (RadioButton) findViewById(R.id.check_add_tables);
		check_add_words = (RadioButton) findViewById(R.id.check_add_words);

		// MULTISPINNERS & SPINNERS:
		select_delete_reviewed_multi_tables = (MultiSpinner) findViewById(R.id.select_delete_reviewed_multi_tables);
		select_delete_reviewed_tables = (Spinner) findViewById(R.id.select_delete_reviewed_tables);
		select_delete_reviewed_multi_words = (MultiSpinner) findViewById(R.id.select_delete_reviewed_multi_words);

		select_add_reviewed_multi_tables = (MultiSpinner) findViewById(R.id.select_add_reviewed_multi_tables);
		select_add_reviewed_tables = (Spinner) findViewById(R.id.select_add_reviewed_tables);
		select_add_reviewed_multi_words = (MultiSpinner) findViewById(R.id.select_add_reviewed_multi_words);

		select_complete_tables = (Spinner) findViewById(R.id.select_complete_tables);
		completeTablesAdapter = new ArrayAdapter<String>(this_act,
				android.R.layout.simple_spinner_item, new ArrayList<String>());
		completeTablesAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		select_complete_tables.setAdapter(completeTablesAdapter);
		select_complete_words = (Spinner) findViewById(R.id.select_complete_words);
		completeWordsAdapter = new ArrayAdapter<String>(this_act,
				android.R.layout.simple_spinner_item, new ArrayList<String>());
		completeWordsAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		select_complete_words.setAdapter(completeWordsAdapter);

		select_delete_reviewed_tables.setAdapter(deleteTablesAdapter);
		addTablesAdapter = new ArrayAdapter<String>(this_act,
				android.R.layout.simple_spinner_item, new ArrayList<String>());
		addTablesAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		select_add_reviewed_tables.setAdapter(addTablesAdapter);

		deleteTablesAdapter = new ArrayAdapter<String>(this_act,
				android.R.layout.simple_spinner_item, new ArrayList<String>());
		deleteTablesAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		select_delete_reviewed_tables.setAdapter(deleteTablesAdapter);

		words = "";

	}

	public void loadButtons() {
		do_edit_user.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		user_login.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		user_logout.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		do_delete_reviewed.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		do_add_reviewed.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		get_password.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		get_forgot_password.setBackgroundResource(sharedPref.getInt(
				"BG Button", R.drawable.button));
		change_password.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		do_enter_new_user.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
	}

	public void setListeners() {
		get_password.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new_user_layout.setVisibility(View.GONE);
				change_password_layout.setVisibility(View.GONE);
				forgot_password_layout.setVisibility(View.VISIBLE);
				forgot_password_results.setText("");
				username = username_input.getText().toString();
				if (username.equals("")) {
					results.setText(Html
							.fromHtml("<b>MUST ENTER A USERNAME.</b>"));
				}
				c = MainLfqActivity.getMiscDb().rawQuery(
						"SELECT * FROM `userdata` WHERE UserName='" + username
								+ "'", null);
				if (c.moveToFirst()) {
					question = c.getString(c.getColumnIndex("SecurityQuestion"));
				}
				c.close();
				forgot_question.setText(Html
						.fromHtml("<b>" + question + "</b>"));
				forgot_answer_input.setText("");
			}
		});
		get_forgot_password.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				username = username_input.getText().toString();
				if (username.equals("")) {
					forgot_password_results.setText(Html
							.fromHtml("<b>ENTER USERNAME.</b>"));
					return;
				}
				answer = forgot_answer_input.getText().toString();
				if (answer.equals("")) {
					forgot_password_results.setText(Html
							.fromHtml("<b>MUST ENTER SECURITY ANSWER.</b>"));
					return;
				}
				c = MainLfqActivity.getMiscDb().rawQuery(
						"SELECT * FROM `userdata` WHERE UserName='" + username
								+ "'", null);
				if (c.moveToFirst()) {
					System.out.println("answer="
							+ c.getString(c.getColumnIndex("SecurityAnswer")));
					if (c.getString(c.getColumnIndex("SecurityAnswer")).equals(
							answer)) {
						forgot_password_results.setText(Html
								.fromHtml("<b>YOUR PASSWORD IS:"
										+ c.getString(c
												.getColumnIndex("Password"))
										+ ".<br />PLEASE LOGIN AGAIN.</b>"));
					} else {
						forgot_password_results.setText(Html
								.fromHtml("<b>NOT CORRECT ANSWER.</b>"));
					}
				}
				c.close();
			}
		});

		change_password.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				question = edit_question_input.getText().toString();
				answer = edit_answer_input.getText().toString();
				password = password_input.getText().toString();
				new_password = new_password_input.getText().toString();
				String text_result = "";
				System.out.println("change answer=" + answer);
				if (question.equals("") || answer.equals("")) {
					results.setText(Html
							.fromHtml("<b>MUST ENTER A SECURITY QUESTION AND ANSWER.</b>"));
					return;
				}
				result_text = "";
				upd_results.clear();
				upd_results.addAll(OldSynchronize.updateLfqUsers(
						"update_password", username, password, new_password,
						question, answer));
				if (upd_results.get(0) || upd_results.get(1)
						|| !upd_results.get(2)) {
					if (upd_results.get(0)) {
						result_text += " NOT CONNECTED OR NULL RESULT.";
					}
					if (upd_results.get(1)) {
						result_text += "USERNMAME EXISTS ON LFQ WEBSITE ALREADY.";
					}
					if (!upd_results.get(2)) {
						result_text += " NOT SYNCED.";
					}
					results.setText(result_text);
					return;
				}
				change_password_layout.setVisibility(View.GONE);
				cv.clear();
				cv.put("UserName", username);
				cv.put("Password", password);
				cv.put("SecurityQuestion", question);
				cv.put("SecurityAnswer", answer);
				int number_changed = MainLfqActivity.getMiscDb().update("userdata", cv,
						"UserName=?", new String[] { username });
				if (number_changed > 0) {
					text_result = username + " PASSWORD UPDATED.";
				} else {
					text_result = username + " PASSWORD NOT UPDATED.";
				}
				change_password_results.setText(Html.fromHtml("<b>"
						+ text_result + "</b>"));

			}
		});
		do_enter_new_user.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				password = password_input.getText().toString();
				username = username_input.getText().toString();
				user_newwords_table = username + "_savednewwords";
				user_reviewedwords_table = username + "_reviewedwords";
				question = new_question_input.getText().toString();
				answer = new_answer_input.getText().toString();
				// System.out.println(telephonyManager.getDeviceId());
				if (question.equals("") || answer.equals("")) {
					results.setText(Html
							.fromHtml("<b>MUST ENTER A SECURITY QUESTION AND ANSWER.</b>"));
					return;
				}
				result_text = "";
				if (username.length() < 4 || password.length() < 7) {
					if (username.length() < 4) {
						result_text = "USERNAME LENGTH MUST BE GREATER THAN 3.";
					}
					if (password.length() < 7) {
						result_text += "PASSWORD LENGTH MUST BE GREATER THAN 3.";
					}
					results.setText(result_text);
					return;
				}
				upd_results.clear();
				upd_results.addAll(OldSynchronize.updateLfqUsers(
						"add_username", username, password, "", question,
						answer));
				if (upd_results.get(0) || upd_results.get(1)
						|| !upd_results.get(2)) {
					if (upd_results.get(0)) {
						result_text += " NOT CONNECTED OR NULL RESULT.";
					}
					if (upd_results.get(1)) {
						result_text += "USERNMAME EXISTS ON LFQ WEBSITE ALREADY.";
					}
					if (!upd_results.get(2)) {
						result_text += " NOT SYNCED.";
					}
					results.setText(result_text);
					return;
				}
				new_user_layout.setVisibility(View.GONE);
				cv.clear();
				cv.put("UserName", username);
				cv.put("Password", password);
				cv.put("SecurityQuestion", question);
				cv.put("SecurityAnswer", answer);
				MainLfqActivity.getMiscDb().insert("userdata", null, cv);
				cv.clear();
				cv.put("UserName", username);
				cv.put("Time1", 0);
				cv.put("Time2", 1);
				cv.put("Time3", 7);
				cv.put("Time4", 30);
				cv.put("Time5", 182);
				cv.put("Time6", "");
				cv.put("Time7", "");
				cv.put("Time8", "");
				cv.put("Time9", "");
				cv.put("Time10", "");
				MainLfqActivity.getMiscDb().insert("user_review_times", null, cv);
				MainLfqActivity.getMiscDb().execSQL("CREATE TABLE IF NOT EXISTS `"
						+ user_reviewedwords_table
						+ "` (`_id` integer PRIMARY KEY AUTOINCREMENT,`Table_name` tinytext,`Word` tinytext);");
				MainLfqActivity.getMiscDb().execSQL("CREATE TABLE IF NOT EXISTS `"
						+ user_newwords_table
						+ "` (`_id` integer PRIMARY KEY AUTOINCREMENT,`Table_name` tinytext,`Date` tinytext, `Word` tinytext, `Anagram`, `Completed_Acrostic_Words` text, `Completed_Anagram_Acrostic_Words` text);");
				MainLfqActivity.getMiscDb().execSQL("CREATE TABLE IF NOT EXISTS `"
						+ username
						+ "_numbertable"
						+ "` (`_id` integer PRIMARY KEY AUTOINCREMENT NOT NULL, `Number` tinytext, `NumInf` text, `NumWors` text, `Type` tinytext)");

				clearLists();
				setVisibilities();
				prompt_not_loggedin.setVisibility(View.GONE);
				result_text += "ADDED " + username + " AND SYNCED. WELCOME!";
				results.setText(result_text);
			}
		});

		user_login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				forgot_password_layout.setVisibility(View.GONE);
				change_password_layout.setVisibility(View.GONE);
				password = password_input.getText().toString();
				username = username_input.getText().toString();
				String login_results = Helpers.login(username, password);
				String[] login_spl = login_results.split("@@@");
				if (login_spl[0].equals("true")) {
					logged_in = true;
				}
				results.setText(login_spl[1]);
				if (logged_in == true) {
					upd_app_results.clear();
					upd_app_results.addAll(OldSynchronize.updateAppUsers(
							username, this_act));
					if (upd_app_results.get(0) == 0
							|| upd_app_results.get(1) == 0
							|| upd_app_results.get(2) == 0) {// if not connected
																// or no
																// username or
																// not updated
						result_text += " USERS NOT UPDATED.";
						if (upd_app_results.get(0) == 0) {
							result_text += " NOT CONNECTED OR NULL RESULT.";
						}
						if (upd_app_results.get(1) == 0) {
							result_text += "USERNMAME DOES NOT EXIST ON LFQ WEBSITE.";
						}
						results.setText(result_text);
						return;
					} else {
						result_text += " UPDATED USERS.";
					}
					prompt_not_loggedin.setVisibility(View.GONE);
					user_reviewedwords_table = username + "_reviewedwords";
					user_newwords_table = username + "_savednewwords";

					setDeleteTables();
					if (delete_tables_list.size() > 0) {
						setDeleteWords(delete_tables_list.get(0));
					}
					if (add_tables_list.size() > 0) {
						setAddWords(add_tables_list.get(0));
					}

					// SETTING VISIBILITY OF DROP BOXES:
					setVisibilities();

				}

			}
		});

		user_logout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				logged_in = false;
				password_input.setText("");
				forgot_password_layout.setVisibility(View.GONE);
				change_password_layout.setVisibility(View.GONE);
				new_user_layout.setVisibility(View.GONE);
				select_delete_reviewed_tables.setVisibility(View.GONE);
				forgot_password_results.setText("");
				change_password_results.setText("");
				enter_new_user_results.setText("");
				forgot_answer_input.setText("");
				edit_question_input.setText("");
				edit_answer_input.setText("");
				new_question_input.setText("");
				new_answer_input.setText("");
				new_password_input.setText("");
				results.setText("BYE BYE " + username + ". LOGGED OUT");
				prompt_not_loggedin.setVisibility(View.VISIBLE);
				clearLists();
				setVisibilities();
			}
		});

		do_edit_user.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (check_add_user.isChecked()) {
					forgot_password_layout.setVisibility(View.GONE);
					change_password_layout.setVisibility(View.GONE);
					new_user_layout.setVisibility(View.VISIBLE);
				}
				if (check_delete_user.isChecked()) {
					if (logged_in == false) {
						results.setText(Html.fromHtml("<b>NOT LOGGED IN!</b>"));
						return;
					}
					result_text = "";
					upd_results.clear();
					upd_results.addAll(OldSynchronize.updateLfqUsers(
							"delete_username", username, password, "", "", ""));
					if (upd_results.get(0) || !upd_results.get(2)) {
						if (upd_results.get(0)) {
							result_text += " NOT CONNECTED OR NULL RESULT.";
						}
						if (!upd_results.get(2)) {
							result_text += " NOT SYNCED.";
						}
						results.setText(result_text);
						return;
					}
					MainLfqActivity.getMiscDb().execSQL("DELETE FROM `userdata` WHERE UserName='"
							+ username + "';");
					MainLfqActivity.getMiscDb().execSQL("DROP TABLE IF EXISTS `"
							+ user_reviewedwords_table + "`;");
					MainLfqActivity.getMiscDb().execSQL("DROP TABLE IF EXISTS `"
							+ user_newwords_table + "`;");
					MainLfqActivity.getMiscDb().execSQL("DROP TABLE IF EXISTS `" + username
							+ "_historical`;");
					MainLfqActivity.getMiscDb().execSQL("DROP TABLE IF EXISTS `" + username
							+ "_personal`;");
					user_logout.performClick();
				}
				if (check_edit_user.isChecked()) {
					change_password_layout.setVisibility(View.GONE);
					forgot_password_layout.setVisibility(View.GONE);
					new_user_layout.setVisibility(View.GONE);
					if (logged_in == false) {
						results.setText(Html.fromHtml("<b>NOT LOGGED IN!</b>"));
						return;
					}
					change_password_results.setText("");
					change_password_layout.setVisibility(View.VISIBLE);
					c = MainLfqActivity.getMiscDb().rawQuery(
							"SELECT * FROM `userdata` WHERE UserName='"
									+ username + "'", null);
					if (c.moveToFirst()) {
						edit_question_input.setText(c.getString(c
								.getColumnIndex("SecurityQuestion")));
						edit_answer_input.setText(c.getString(c
								.getColumnIndex("SecurityAnswer")));
					}
				}

			}
		});

		do_delete_reviewed.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				delete_reviewed_results.setText("");
				add_reviewed_results.setText("");
				found_delete_tables_results.setText("");
				found_delete_words_results.setText("");
				found_add_tables_results.setText("");
				found_add_words_results.setText("");
				autosync_text = "";
				if (logged_in == false) {
					results.setText(Html.fromHtml("<b>NOT LOGGED IN.</b>"));
					return;
				}
				if (logged_in == true) {
					user_reviewedwords_table = username + "_reviewedwords";
					// FOR IF DELETE TABLE
					if (check_delete_tables.isChecked()) {
						checked = select_delete_reviewed_multi_tables
								.getChecked();
						list.clear();
						delete_words_list.clear();
						for (int i = 0; i < delete_tables_list.size(); i++) {
							if (checked[i] == true) {
								list.add(delete_tables_list.get(i));
							}

						}
						MainLfqActivity.getMiscDb().execSQL("DELETE FROM `"
								+ user_reviewedwords_table
								+ "` WHERE Table_name IN ("
								+ joinListQuoted(list, ",") + ");");
						sql = "DELETE FROM `" + Helpers.db_prefix + "newwords`.`"
								+ user_reviewedwords_table
								+ "` WHERE Table_name IN ("
								+ joinListQuoted(list, ",") + ")";
						// autoSync(sql, db, action, table, name, bool is_image,
						// byte[]
						// image)
						autosync_text += Synchronize.autoSync(sql, "nw_db",
								"delete_reviewed", user_reviewedwords_table,
								"", false, null);
						setDeleteTables();
						if (delete_tables_list.size() > 0) {
							setDeleteWords(delete_tables_list.get(0));
						}
						if (add_tables_list.size() > 0) {
							setAddWords(add_tables_list.get(0));
						}
						setVisibilities();
					}
					if (check_delete_words.isChecked()) {
						checked = select_delete_reviewed_multi_words
								.getChecked();
						list.clear();
						for (int i = 0; i < delete_words_list.size(); i++) {
							if (checked[i] == true) {
								list.add(delete_words_list.get(i));
							}

						}
						MainLfqActivity.getMiscDb().execSQL("DELETE FROM `"
								+ user_reviewedwords_table
								+ "` WHERE Word IN ("
								+ joinListQuoted(list, ",") + ");");
						sql = "DELETE FROM `" + Helpers.db_prefix + "newwords`.`"
								+ user_reviewedwords_table
								+ "` WHERE Word IN ("
								+ joinListQuoted(list, ",") + ")";
						// autoSync(sql, db, action, table, name, bool is_image,
						// byte[]
						// image)
						autosync_text += Synchronize.autoSync(sql, "nw_db",
								"delete_words", user_reviewedwords_table, "",
								false, null);
						setDeleteWords(select_delete_reviewed_tables
								.getSelectedItem().toString());
						setAddWords(select_delete_reviewed_tables
								.getSelectedItem().toString());
						setDeleteTables();
						setVisibilities();
					}
				} else {
					delete_reviewed_results.setText("NOT LOGGED IN.");
				}
			}
		});

		do_add_reviewed.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				delete_reviewed_results.setText("");
				add_reviewed_results.setText("");
				found_delete_tables_results.setText("");
				found_delete_words_results.setText("");
				found_add_tables_results.setText("");
				found_add_words_results.setText("");
				autosync_text = "";
				if (logged_in == false) {
					results.setText(Html.fromHtml("<b>NOT LOGGED IN.</b>"));
					return;
				}
				if (logged_in == true) {
					user_reviewedwords_table = username + "_reviewedwords";
					// FOR IF ADD TABLE(S)
					if (check_add_tables.isChecked()) {
						checked = select_add_reviewed_multi_tables.getChecked();
						list.clear();
						for (int i = 0; i < add_tables_list.size(); i++) {
							if (checked[i] == true) {
								list.add(add_tables_list.get(i));
							}
						}
						for (int i = 0; i < list.size(); i++) {
							c = MainLfqActivity.getAcrosticsDb().rawQuery(
									"SELECT Name FROM "
											+ list.get(i)
											+ " WHERE Information<>'' AND Acrostics<>'' ORDER BY Name",
									null);
							if (c.moveToFirst()) {
								do {
									c2 = MainLfqActivity.getMiscDb().rawQuery("SELECT Word FROM "
											+ user_reviewedwords_table
											+ " WHERE Word='"
											+ list.get(i)
											+ "' AND Table_name='"
											+ select_add_reviewed_tables
													.getSelectedItem()
													.toString() + "'", null);
									if (c2.getCount() == 0) {
										cv.clear();
										cv.put("Word", c.getString(0));
										cv.put("Table_name", list.get(i));
										MainLfqActivity.getMiscDb().insert(user_reviewedwords_table,
												null, cv);
										sql = "INSERT INTO `" + Helpers.db_prefix + "newwords`.`"
												+ user_reviewedwords_table
												+ "` (`Table_name`,`Word`) VALUES('"
												+ list.get(i)
												+ "','"
												+ c.getString(0) + "')";
										// autoSync(sql, db, action, table,
										// name, bool is_image,
										// byte[]
										// image)
										autosync_text += Synchronize.autoSync(
												sql, "nw_db", "insert_tables",
												user_reviewedwords_table, "",
												false, null);
									}
									c2.close();
								} while (c.moveToNext());
							}
							c.close();
						}
						setDeleteTables();
						setVisibilities();
					}

					if (check_add_words.isChecked()) {

						checked = select_add_reviewed_multi_words.getChecked();
						list.clear();
						for (int i = 0; i < add_words_list.size(); i++) {
							if (checked[i] == true) {
								list.add(add_words_list.get(i));
							}

						}
						for (int i = 0; i < list.size(); i++) {
							c = MainLfqActivity.getMiscDb().rawQuery(
									"SELECT Word FROM "
											+ user_reviewedwords_table
											+ " WHERE Word='"
											+ list.get(i)
											+ "' AND Table_name='"
											+ select_add_reviewed_tables
													.getSelectedItem()
													.toString() + "'", null);
							if (c.getCount() == 0) {
								cv.clear();
								cv.put("Word", list.get(i));
								cv.put("Table_name", select_add_reviewed_tables
										.getSelectedItem().toString());
								MainLfqActivity.getMiscDb().insert(user_reviewedwords_table, null, cv);
								sql = "INSERT INTO " + Helpers.db_prefix + "newwords."
										+ user_reviewedwords_table
										+ "(Word,Table_name) VALUES('"
										+ list.get(i)
										+ "','"
										+ select_add_reviewed_tables
												.getSelectedItem().toString()
										+ "')";
								// autoSync(sql, db, action, table, name, bool
								// is_image,
								// byte[]
								// image)
								autosync_text += Synchronize.autoSync(sql,
										"nw_db", "insert_words",
										user_reviewedwords_table, "", false,
										null);
							}
							c.close();
						}
						setDeleteTables();
						setDeleteWords(select_add_reviewed_tables
								.getSelectedItem().toString());
						setAddWords(select_add_reviewed_tables
								.getSelectedItem().toString());
						setVisibilities();
						add_reviewed_results.setText(Html.fromHtml("<b>Added "
								+ list.size() + " words to reviewed list."
								+ autosync_text + "</b>"));
					}
				}
			}
		});

		select_delete_reviewed_tables
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						if (delete_tables_list.size() > 0) {
							setDeleteWords(select_delete_reviewed_tables
									.getSelectedItem().toString());
						}
						setVisibilities();
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}

				});

		select_add_reviewed_tables
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {

						if (add_tables_list.size() > 0) {
							setAddWords(select_add_reviewed_tables
									.getSelectedItem().toString());
						}
						setVisibilities();
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}

				});

		check_delete_tables.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (delete_tables_list.size() > 0) {
					select_delete_reviewed_multi_tables
							.setVisibility(View.VISIBLE);
				} else {
					select_delete_reviewed_multi_tables
							.setVisibility(View.GONE);
				}
				select_delete_reviewed_multi_words.setVisibility(View.GONE);
				found_delete_words_results.setVisibility(View.GONE);
				select_delete_reviewed_tables.setVisibility(View.GONE);
			}
		});

		check_add_tables.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (add_tables_list.size() > 0) {
					select_add_reviewed_multi_tables
							.setVisibility(View.VISIBLE);
				} else {
					select_add_reviewed_multi_tables.setVisibility(View.GONE);
				}
				select_add_reviewed_multi_words.setVisibility(View.GONE);
				found_add_words_results.setVisibility(View.GONE);
				select_add_reviewed_tables.setVisibility(View.GONE);
			}
		});

		check_delete_words.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				select_delete_reviewed_multi_tables.setVisibility(View.GONE);
				if (deleteTablesAdapter.getCount() > 0) {
					select_delete_reviewed_tables.setVisibility(View.VISIBLE);
				}

				if (delete_tables_list.size() > 0) {
					setDeleteWords(delete_tables_list.get(0));
					if (delete_words_list.size() > 0) {
						select_delete_reviewed_multi_words
								.setVisibility(View.VISIBLE);
						found_delete_words_results.setVisibility(View.VISIBLE);
					} else {
						select_delete_reviewed_multi_words
								.setVisibility(View.GONE);
						found_delete_words_results.setVisibility(View.GONE);
					}
				}

			}

		});
		check_add_words.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (logged_in == false) {
					results.setText(Html.fromHtml("<b>NOT LOGGED IN.</b>"));
					return;
				}
				select_add_reviewed_multi_tables.setVisibility(View.GONE);
				if (addTablesAdapter.getCount() > 0) {
					select_add_reviewed_tables.setVisibility(View.VISIBLE);
				}

				if (add_tables_list.size() > 0) {
					if (add_words_list.size() > 0) {
						select_add_reviewed_multi_words
								.setVisibility(View.VISIBLE);
						found_add_words_results.setVisibility(View.VISIBLE);
					} else {
						select_add_reviewed_multi_words
								.setVisibility(View.GONE);
						found_delete_words_results.setVisibility(View.GONE);
					}
				}

			}

		});

		select_complete_tables
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						setCompleteWordsList(select_complete_tables
								.getSelectedItem().toString());
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});

	}

	public String joinListQuoted(List<String> list, String delimiter) {
		String str = "";
		for (int i = 0; i < list.size(); i++) {
			str += "'" + list.get(i) + "'";
			if (i != list.size() - 1) {
				str += delimiter;
			}
		}
		return str;
	}

	public String joinList(List<String> list, String delimiter) {
		String str = "";
		for (int i = 0; i < list.size(); i++) {
			str += list.get(i);
			if (i != (list.size() - 1)) {
				str += delimiter;
			}
		}
		return str;
	}

	public String joinList(String[] list, String delimiter) {
		String str = "";
		for (int i = 0; i < list.length; i++) {
			str += list[i];
			if (i != (list.length - 1)) {
				str += delimiter;
			}
		}
		return str;
	}

	public String[] spliceString(String[] array, String find) {
		String[] new_array = new String[array.length - 1];
		int ctNewArr = 0;
		for (int ctArr = 0; ctArr < array.length; ctArr++) {
			if (!array[ctArr].equals(find)) {
				new_array[ctNewArr] = array[ctArr];
				ctNewArr++;
			}

		}
		return new_array;
	}

	public void setAddTables() {
		addTablesAdapter.clear();
		addTablesAdapter.addAll(add_tables_list);
		if (add_tables_list.size() > 0) {
			select_add_reviewed_multi_tables.setItems(add_tables_list,
					add_tables_list.get(0), this_act);
		}
		found_add_tables_results.setText(Html.fromHtml("<b>Found "
				+ add_tables_list.size() + " tables.</b>"));
	}

	public void setDeleteTables() {
		c = MainLfqActivity.getMiscDb().rawQuery("SELECT DISTINCT Table_name FROM "
				+ user_reviewedwords_table + " ORDER BY Table_name", null);
		deleteTablesAdapter.clear();
		delete_tables_list.clear();
		if (c.moveToFirst()) {
			do {
				deleteTablesAdapter.add(c.getString(0));
				delete_tables_list.add(c.getString(0));
			} while (c.moveToNext());
			if (delete_tables_list.size() > 0) {
				select_delete_reviewed_multi_tables
						.setItems(delete_tables_list,
								delete_tables_list.get(0), this_act);
			}
		}
		c.close();
		found_delete_tables_results.setText(Html.fromHtml("<b>Found "
				+ delete_tables_list.size() + " tables.</b>"));
	}

	public void setAddWords(String this_table) {// FOR WORDS LEARNED YOU DON'T
												// WANT TO REVIEW
		if (add_tables_list.size() > 0) {
			select_add_reviewed_tables.setVisibility(View.VISIBLE);
			c = MainLfqActivity.getAcrosticsDb()
					.rawQuery(
							"SELECT Name FROM "
									+ this_table
									+ " WHERE Information<>'' AND Acrostics<>'' ORDER BY Name COLLATE NOCASE",
							null);
			add_words_list.clear();
			if (c.moveToFirst()) {
				do {
					add_words_list.add(c.getString(0));
				} while (c.moveToNext());
			}
			c.close();
			c = MainLfqActivity.getMiscDb().rawQuery("SELECT Word FROM " + user_reviewedwords_table
					+ " WHERE Table_name='" + this_table + "'", null);
			if (c.moveToFirst()) {
				do {
					words = c.getString(0);
					add_words_list.remove(words);
					System.out.println("adding " + words + " to list.");
				} while (c.moveToNext());
			}
			c.close();
			if (add_words_list.size() > 0) {
				System.out.println("SETTING ADD MULTI WORDS!!!!");
				select_add_reviewed_multi_words.setItems(add_words_list,
						add_words_list.get(0), this_act);
			} else {
				select_add_reviewed_multi_words.setVisibility(View.GONE);
				if (add_tables_list.size() == 0) {
					select_add_reviewed_tables.setVisibility(View.GONE);
				}
			}

		} else {
			select_add_reviewed_tables.setVisibility(View.GONE);
		}
		found_add_words_results.setText(Html.fromHtml("<b>Found "
				+ add_words_list.size() + " words.</b>"));

	}

	public void setDeleteWords(String this_table) {
		if (deleteTablesAdapter.getCount() > 0) {
			c = MainLfqActivity.getMiscDb().rawQuery("SELECT Word FROM " + user_reviewedwords_table
					+ " WHERE Table_name='" + this_table + "'", null);
			delete_words_list.clear();
			if (c.moveToFirst()) {
				do {
					words = c.getString(0);
					delete_words_list.add(words);
				} while (c.moveToNext());
				c.close();
				if (delete_words_list.size() > 0) {
					select_delete_reviewed_multi_words.setItems(
							delete_words_list, delete_words_list.get(0),
							this_act);
				}
			}
		}
		found_delete_words_results.setText(Html.fromHtml("<b>Found "
				+ delete_words_list.size() + " words.</b>"));
	}

	public void setVisibilities() {
		if (check_delete_tables.isChecked()) {
			if (delete_tables_list.size() > 0) {
				select_delete_reviewed_multi_tables.setVisibility(View.VISIBLE);
			} else {
				select_delete_reviewed_multi_tables.setVisibility(View.GONE);
			}
			select_delete_reviewed_tables.setVisibility(View.GONE);
			select_delete_reviewed_multi_words.setVisibility(View.GONE);
			found_delete_words_results.setVisibility(View.GONE);
		}

		if (check_delete_words.isChecked()) {
			select_delete_reviewed_multi_tables.setVisibility(View.GONE);

			if (delete_tables_list.size() > 0) {
				select_delete_reviewed_tables.setVisibility(View.VISIBLE);
			} else {
				select_delete_reviewed_tables.setVisibility(View.GONE);
			}

			if (delete_words_list.size() > 0) {
				found_delete_words_results.setVisibility(View.VISIBLE);
				select_delete_reviewed_multi_words.setVisibility(View.VISIBLE);
			} else {
				select_delete_reviewed_multi_words.setVisibility(View.GONE);
				found_delete_words_results.setVisibility(View.GONE);
			}
		}

		if (check_add_tables.isChecked()) {
			if (add_tables_list.size() > 0) {
				select_add_reviewed_multi_tables.setVisibility(View.VISIBLE);
			} else {
				select_add_reviewed_multi_tables.setVisibility(View.GONE);
			}
			found_add_words_results.setVisibility(View.GONE);
			select_add_reviewed_tables.setVisibility(View.GONE);
			select_add_reviewed_multi_words.setVisibility(View.GONE);
		}

		if (check_add_words.isChecked()) {
			select_add_reviewed_multi_tables.setVisibility(View.GONE);

			if (add_tables_list.size() > 0) {
				select_add_reviewed_tables.setVisibility(View.VISIBLE);
			} else {
				select_add_reviewed_tables.setVisibility(View.GONE);
			}

			if (add_words_list.size() > 0) {
				select_add_reviewed_multi_words.setVisibility(View.VISIBLE);
				found_add_words_results.setVisibility(View.VISIBLE);
			} else {
				select_add_reviewed_multi_words.setVisibility(View.GONE);
				found_add_words_results.setVisibility(View.GONE);
			}

		}
	}

	public void clearLists() {
		deleteTablesAdapter.clear();
		delete_tables_list.clear();
		delete_words_list.clear();
		addTablesAdapter.clear();
		add_tables_list.clear();
		add_words_list.clear();
		list.clear();
	}

	@Override
	public void onItemschecked(boolean[] checked) {
		// for (int i = 0; i < checked.length; i++) {
		// }

	}

	class doLoadDatabases extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setContentView(R.layout.edit_users);
			setTitle("EDIT USERS");
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

		@SuppressWarnings("static-access")
		@Override
		protected void onPostExecute(String file_url) {
			results.setText("");
			createTableLists();
			setCompleteTablesList();
			setCompleteWordsList(tables_list.get(0));
			username_input.setText(sharedPref.getString(
					"EDIT USERS USERNAME INPUT", ""));
			select_delete_reviewed_multi_tables.setVisibility(View.GONE);
			select_delete_reviewed_tables.setVisibility(View.GONE);
			select_delete_reviewed_multi_words.setVisibility(View.GONE);
			select_add_reviewed_multi_tables.setVisibility(View.GONE);
			select_add_reviewed_tables.setVisibility(View.GONE);
			select_add_reviewed_multi_words.setVisibility(View.GONE);
			setAddTables();
			if (h.getLoginStatus() == true) {
				System.out.println("IM LOGGED IN!");
				username = Helpers.getUsername();
				password = Helpers.getPassword();
				username_input.setText(username);
				logged_in = true;
				results.setText("WELCOME " + username + ".");
				prompt_not_loggedin.setVisibility(View.GONE);
				user_reviewedwords_table = username + "_reviewedwords";
				user_newwords_table = username + "_savednewwords";
				setDeleteTables();
				if (delete_tables_list.size() > 0) {
					setDeleteWords(delete_tables_list.get(0));
				}
				if (add_tables_list.size() > 0) {
					setAddWords(add_tables_list.get(0));
				}
				setVisibilities();
			}

			check_add_user.setChecked(sharedPref.getBoolean(
					"EDIT USERS CHECK ADD USER", false));
			check_delete_user.setChecked(sharedPref.getBoolean(
					"EDIT USERS CHECK DELETE USER", false));
			check_edit_user.setChecked(sharedPref.getBoolean(
					"EDIT USERS CHECK EDIT USER", false));
			check_delete_tables.setChecked(sharedPref.getBoolean(
					"EDIT USERS CHECK DELETE TABLES", false));
			check_delete_words.setChecked(sharedPref.getBoolean(
					"EDIT USERS CHECK DELETE WORDS", false));
			check_add_tables.setChecked(sharedPref.getBoolean(
					"EDIT USERS CHECK ADD TABLES", false));
			check_add_words.setChecked(sharedPref.getBoolean(
					"EDIT USERS CHECK ADD WORDS", false));
			setListeners();
		}

	}

	public void createTableLists() {
		// CREATE COMPLETE LIST OF TABLES
		Cursor c_acr_tables = MainLfqActivity.getAcrosticsDb().rawQuery(
				"SELECT name FROM sqlite_master "
						+ " WHERE type='table' ORDER BY name", null);
		tables_list = new ArrayList<String>();

		if (c_acr_tables.moveToFirst()) {
			do {
				if (!c_acr_tables.getString(0).equals("android_metadata")
						&& !c_acr_tables.getString(0).equals("sqlite_sequence")) {
					tables_list.add(c_acr_tables.getString(0));
				}
			} while (c_acr_tables.moveToNext());
		}
		c_acr_tables.close();
	}

	public void setCompleteTablesList() {
		completeTablesAdapter.clear();
		add_tables_list.clear();
		int ct_complete_tables = 0;
		for (int i = 0; i < tables_list.size(); i++) {
			c = MainLfqActivity.getAcrosticsDb().rawQuery("SELECT Name FROM " + tables_list.get(i)
					+ " WHERE Information<>'' AND Acrostics<>''ORDER BY Name",
					null);
			if (c.getCount() > 0) {
				add_tables_list.add(tables_list.get(i));
				completeTablesAdapter.add(tables_list.get(i));
				ct_complete_tables++;
			}
			c.close();
		}
		show_complete_tables.setText(Html.fromHtml("<b>Found "
				+ ct_complete_tables + " tables."));
	}

	public void setCompleteWordsList(String table) {
		completeWordsAdapter.clear();
		c = MainLfqActivity.getAcrosticsDb()
				.rawQuery(
						"SELECT Name FROM "
								+ table
								+ " WHERE Information<>'' AND Acrostics<>'' ORDER BY Name COLLATE NOCASE",
						null);
		show_complete_words.setText(Html.fromHtml("<b>Found " + c.getCount()
				+ " words.</b>"));
		if (c.moveToFirst()) {
			do {
				completeWordsAdapter.add(c.getString(0));
			} while (c.moveToNext());
		}
		c.close();
	}

}