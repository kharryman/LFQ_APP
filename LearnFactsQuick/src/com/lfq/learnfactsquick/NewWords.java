package com.lfq.learnfactsquick;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.lfq.learnfactsquick.Constants.cols.acrostics;
import com.lfq.learnfactsquick.Constants.cols.user_review_times;
import com.lfq.learnfactsquick.Constants.cols.user_saved_newwords;
import com.lfq.learnfactsquick.Constants.tables;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.style.BackgroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class NewWords extends Activity {
	private RelativeLayout show_newwords_above_layout;
	private Button do_login, do_logout;
	private Button get_user_newwords, go_left, go_right;
	private Button do_show_newwords_backup;
	private EditText username_input, password_input;
	private Spinner select_newword_days_before;
	private TextView results;
	private LinearLayout newwords_table;
	private Calendar today, cal_days_before;
	private String usertable;
	int year, month, day;
	int ctcells = 0, ct_days_wors = 0;
	private String date_before, month_display_number, day_display_number, acro,
			info, text;
	private List<Integer> review_times;
	private int review_index;
	private String username, password;
	private Boolean logged_in;	
	private String textspl[] = null;
	private String word, table;
	private List<Boolean> is_edit_acro_list;
	private List<Boolean> is_edit_info_list;
	private ContentValues cv;
	private String sql;
	private Activity this_act;
	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;
	private String my_date;
	Cursor c = null;
	private List<Boolean> is_expands;
	private List<String> saved_acro;
	private List<String> saved_info;

	private MenuItem menu_item_autosync_on, menu_item_autosync_off;
	private static Boolean is_database_load;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		is_database_load = false;
		is_expands = new ArrayList<Boolean>();
		saved_acro = new ArrayList<String>();
		saved_info = new ArrayList<String>();
		this_act = this;
		sharedPref = getSharedPreferences(
				getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		editor = sharedPref.edit();
		text = "";
		sql = "";
		logged_in = false;
		review_index = 0;
		username = "";
		password = "";
		word = "";
		table = "";
		day_display_number = "";
		month_display_number = "";
		is_edit_acro_list = new ArrayList<Boolean>();
		is_edit_info_list = new ArrayList<Boolean>();
		review_times = new ArrayList<Integer>();
		cv = new ContentValues();
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

	public void saveChanges() {
		editor.putString("NEWORDS SELECT DAYS BEFORE",
				select_newword_days_before.getSelectedItem().toString());
		editor.putString("NEWORDS USERNAME INPUT", username_input.getText()
				.toString());
		if (logged_in == true) {
			editor.putInt("REVIEW INDEX", review_index);
			editor.putString("MY DATE", my_date);
		}
		editor.commit();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		saveChanges();
		super.onConfigurationChanged(newConfig);
	}

	public void setViews() {
		setTitle("NEW WORDS");
		// LAYOUTS:
		newwords_table = (LinearLayout) findViewById(R.id.newwords_table);
		show_newwords_above_layout = (RelativeLayout) findViewById(R.id.show_newwords_above_layout);

		// BUTTONS:
		get_user_newwords = (Button) findViewById(R.id.get_user_newwords);
		go_left = (Button) findViewById(R.id.newwords_left);
		go_right = (Button) findViewById(R.id.newwords_right);
		do_login = (Button) findViewById(R.id.show_newwords_login);
		do_logout = (Button) findViewById(R.id.show_newwords_logout);
		do_show_newwords_backup = (Button) findViewById(R.id.do_show_newwords_backup);

		// EDITTEXTS:
		password_input = (EditText) findViewById(R.id.show_newwords_password);
		username_input = (EditText) findViewById(R.id.show_newwords_username);

		// SPINNERS:
		select_newword_days_before = (Spinner) findViewById(R.id.select_newword_days_before);

		// TEXTVIEWS:
		results = (TextView) findViewById(R.id.newwords_results);

		today = Calendar.getInstance();
		year = today.get(Calendar.YEAR);
		month = today.get(Calendar.MONTH);
		day = today.get(Calendar.DAY_OF_MONTH);
		month_display_number = String.valueOf(month);
		day_display_number = String.valueOf(day);
		if (month < 10) {
			month_display_number = "0" + month;
		}
		if (day < 10) {
			day_display_number = "0" + day;
		}
		my_date = year + "/" + month_display_number + "/" + day_display_number;
		if (month < 10) {
			month_display_number = "0" + month;
		}
		do_show_newwords_backup.setVisibility(View.GONE);

	}

	public void loadButtons() {
		get_user_newwords.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		go_left.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		go_right.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		do_login.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		do_logout.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		do_show_newwords_backup.setBackgroundResource(sharedPref.getInt(
				"BG Button", R.drawable.button));
	}

	public void setListeners() {

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
				results.setText(textspl[1]);
			}
		});

		do_logout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				logged_in = false;
				password_input.setText("");
				results.setText("LOGGED OUT. BYE BYE " + username);
			}
		});

		do_show_newwords_backup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				do_show_newwords_backup.setVisibility(View.GONE);
				show_newwords_above_layout.setVisibility(View.VISIBLE);
			}
		});

		go_left.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (logged_in == false) {
					results.setText(Html.fromHtml("<b>NOT LOGGED IN!</b>"));
					return;
				}
				if (review_index == 0) {
					results.setText(Html
							.fromHtml("<b><u>NO PREVIOUS REVIEW.</u></b>"));
					return;
				}
				review_index--;
				newwords_table.removeAllViews();
				usertable = username + "_savednewwords";
				int days_before = Integer.parseInt(select_newword_days_before
						.getSelectedItem().toString());

				// GET DATE:
				cal_days_before = Calendar.getInstance();
				cal_days_before.add(Calendar.DATE,
						(-days_before - review_times.get(review_index) + 1));
				year = cal_days_before.get(Calendar.YEAR);
				month = cal_days_before.get(Calendar.MONTH) + 1;
				day = cal_days_before.get(Calendar.DAY_OF_MONTH);
				month_display_number = String.valueOf(month);
				day_display_number = String.valueOf(day);
				if (month < 10) {
					month_display_number = "0" + month;
				}
				if (day < 10) {
					day_display_number = "0" + day;
				}
				my_date = year + "/" + month_display_number + "/"
						+ day_display_number;

				getWords(my_date);

			}
		});
		go_right.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (logged_in == false) {
					results.setText(Html.fromHtml("<b>NOT LOGGED IN!</b>"));
					return;
				}
				if (review_index >= (review_times.size() - 1)) {
					results.setText(Html
							.fromHtml("<b><u>NO NEXT REVIEW.</u></b>"));
					return;
				}
				newwords_table.removeAllViews();
				review_index++;
				usertable = username + "_savednewwords";
				int days_before = Integer.parseInt(select_newword_days_before
						.getSelectedItem().toString());

				// GET DATE:
				cal_days_before = Calendar.getInstance();
				cal_days_before.add(Calendar.DATE, (0 - days_before
						- review_times.get(review_index) + 1));
				System.out.println("rev_ind=" + review_times.get(review_index));
				year = cal_days_before.get(Calendar.YEAR);
				month = cal_days_before.get(Calendar.MONTH) + 1;
				day = cal_days_before.get(Calendar.DAY_OF_MONTH);
				month_display_number = String.valueOf(month);
				day_display_number = String.valueOf(day);
				if (month < 10) {
					month_display_number = "0" + month;
				}
				if (day < 10) {
					day_display_number = "0" + day;
				}
				my_date = year + "/" + month_display_number + "/"
						+ day_display_number;

				getWords(my_date);
			}
		});

		get_user_newwords.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (logged_in == false) {
					results.setText(Html.fromHtml("<b>NOT LOGGED IN!</b>"));
					return;
				}
				do_show_newwords_backup.setVisibility(View.VISIBLE);
				show_newwords_above_layout.setVisibility(View.GONE);
				newwords_table.removeAllViews();
				usertable = username + "_savednewwords";
				int days_before = Integer.parseInt(select_newword_days_before
						.getSelectedItem().toString());
				setReviewTimes();

				review_index = 0;
				cal_days_before = Calendar.getInstance();
				cal_days_before.add(Calendar.DATE, (0 - days_before
						- review_times.get(0) + 1));// GET
													// YESTERDAYS
				year = cal_days_before.get(Calendar.YEAR);
				month = cal_days_before.get(Calendar.MONTH) + 1;
				day = cal_days_before.get(Calendar.DAY_OF_MONTH);
				month_display_number = String.valueOf(month);
				day_display_number = String.valueOf(day);
				if (month < 10) {
					month_display_number = "0" + month;
				}
				if (day < 10) {
					day_display_number = "0" + day;
				}
				my_date = year + "/" + month_display_number + "/"
						+ day_display_number;
				getWords(my_date);

			}// end onclick event
		});

	}

	public void setReviewTimes() {
		c = MainLfqActivity.getNewwordsDb().rawQuery(
				"SELECT * FROM " + tables.user_review_times + " WHERE " + user_review_times.UserName+ "='" + username
						+ "'", null);
		review_times.clear();
		if (c.moveToFirst()) {
			for (int i = 1; i <= 10; i++) {
				if (!c.getString(c.getColumnIndex("Time" + i)).equals("")) {
					review_times.add(Integer.parseInt(c.getString(c
							.getColumnIndex("Time" + i))));
				}
			}
		}
		c.close();
	}

	class doLoadDatabases extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setContentView(R.layout.newwords);
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

		@SuppressWarnings("unchecked")
		@Override
		protected void onPostExecute(String file_url) {
			if (Helpers.getLoginStatus() == true) {
				username = Helpers.getUsername();
				password = Helpers.getPassword();
				username_input.setText(username);
				logged_in = true;
				results.setText("WELCOME " + username + ".");
				usertable = username + "_savednewwords";
				review_index = sharedPref.getInt("REVIEW INDEX", 0);
				setReviewTimes();
				my_date = sharedPref.getString("MY DATE", my_date);
				do_show_newwords_backup.setVisibility(View.VISIBLE);
				show_newwords_above_layout.setVisibility(View.GONE);
				getWords(my_date);
			} else {
				results.setText("WELCOME GUEST.");
			}
			username_input.setText(sharedPref.getString(
					"NEWORDS USERNAME INPUT", ""));
			setListeners();
			select_newword_days_before
					.setSelection(((ArrayAdapter<String>) select_newword_days_before
							.getAdapter()).getPosition(sharedPref.getString(
							"NEWORDS SELECT DAYS BEFORE",
							select_newword_days_before.getItemAtPosition(0)
									.toString())));
		}

	}

	private void getWords(String date) {
		// SET VARIABLES:
		date_before = date;

		// INITIATE VARIABLES:
		newwords_table.removeAllViews();
		is_edit_acro_list.clear();
		is_edit_info_list.clear();
		saved_acro.clear();
		saved_info.clear();

		Cursor c_getword = MainLfqActivity.getNewwordsDb().rawQuery("SELECT * FROM " + usertable
				+ " WHERE " + user_saved_newwords.MyDate + "='" + date_before + "'", null);
		ct_days_wors = 0;
		TextView prompt_tv = new TextView(this_act);
		prompt_tv.setText(Html.fromHtml("<b>" + review_times.get(review_index)
				+ " DAYS BEFORE:" + date_before + "</b><br />"));
		prompt_tv.setTextSize(24);
		newwords_table.addView(prompt_tv);
		if (c_getword.moveToFirst()) {
			do {
				word = c_getword.getString(c_getword.getColumnIndex(user_saved_newwords.Word));
				table = c_getword.getString(c_getword
						.getColumnIndex(user_saved_newwords.Table_name));
				Cursor c_getacro = MainLfqActivity.getAcrosticsDb().rawQuery(
						"SELECT " + acrostics.Information + "," + acrostics.Acrostics + " FROM " + table
								+ " WHERE " + acrostics.Name + "='" + word + "'", null);
//YES, ...
				if (c_getacro.moveToFirst()) {
					// SET VARIABLES:
					info = c_getacro.getString(c_getacro
							.getColumnIndex(acrostics.Information));
					acro = c_getacro.getString(c_getacro
							.getColumnIndex(acrostics.Acrostics));
					final int this_ct = ct_days_wors;
					is_expands.add(false);
					is_edit_info_list.add(false);
					is_edit_acro_list.add(false);
					final String this_word = word;
					final String this_table = table;
					saved_info.add(info);
					saved_acro.add(acro);

					// INITIATE VARIABLES:
					ct_days_wors++;

					final TextView first_tv = new TextView(this_act);
					first_tv.setTextSize(24);
					first_tv.setText(Html.fromHtml("<b>" + ct_days_wors + ")"
							+ word + "</b>"));
					newwords_table.addView(first_tv);

					// ADD PROMPT_INFO AND EDIT BUTTON LAYOUT:
					LinearLayout prompt_info_layout = new LinearLayout(this_act);
					prompt_info_layout.setOrientation(LinearLayout.HORIZONTAL);
					final TextView prompt_info_tv = new TextView(this_act);
					String prompt_info = "+Information";
					Spannable spantext = Spannable.Factory.getInstance()
							.newSpannable(prompt_info);
					spantext.setSpan(
							new BackgroundColorSpan(sharedPref.getInt(
									"BUTTON Color", Color.GREEN)), 0,
							prompt_info.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					prompt_info_tv.setTextSize(24);
					prompt_info_tv.setText(spantext);
					prompt_info_layout.addView(prompt_info_tv);
					final Button edit_info_button = new Button(this_act);
					edit_info_button.setText("EDIT");
					prompt_info_layout.addView(edit_info_button);
					edit_info_button.setVisibility(View.GONE);
					newwords_table.addView(prompt_info_layout);

					// ADD INFO TEXTVIEW AND INFO EDIT TEXT:
					final TextView info_tv = new TextView(this_act);
					info_tv.setTextSize(24);
					info_tv.setText(info);
					newwords_table.addView(info_tv);
					final EditText info_et = new EditText(this_act);
					info_et.setMaxLines(5);
					info_et.setTextSize(24);
					info_et.setText(info);
					info_et.setBackgroundResource(R.drawable.rounded_edittext);
					info_et.setVisibility(View.GONE);
					newwords_table.addView(info_et);

					// ADD INFO RESULTS TEXT VIEW
					final TextView info_results = new TextView(this_act);
					info_results.setVisibility(View.GONE);
					info_tv.setVisibility(View.GONE);
					newwords_table.addView(info_results);

					// ADD PROMPT_ACRO AND EDIT BUTTON LAYOUT:
					LinearLayout prompt_acro_layout = new LinearLayout(this_act);
					prompt_acro_layout.setOrientation(LinearLayout.HORIZONTAL);
					TextView prompt_acro = new TextView(this_act);
					prompt_acro.setTextSize(24);
					prompt_acro.setText(Html.fromHtml("<b>Acrostics:</b>"));
					prompt_acro_layout.addView(prompt_acro);
					Button edit_acro_button = new Button(this_act);
					edit_acro_button.setText("EDIT");
					prompt_acro_layout.addView(edit_acro_button);
					newwords_table.addView(prompt_acro_layout);

					// ADD ACROSTICS TEXT VIEW AND ACRO EDIT TEXT:
					final TextView acro_tv = new TextView(this_act);
					acro_tv.setTextSize(24);
					acro_tv.setText(acro);
					newwords_table.addView(acro_tv);
					final EditText acro_et = new EditText(this_act);
					acro_et.setMaxLines(5);
					acro_et.setTextSize(24);
					acro_et.setText(acro);
					acro_et.setBackgroundResource(R.drawable.rounded_edittext);
					acro_et.setVisibility(View.GONE);
					newwords_table.addView(acro_et);

					// ADD ACRO RESULTS TEXT VIEW:
					final TextView acro_results = new TextView(this_act);
					acro_results.setVisibility(View.GONE);
					newwords_table.addView(acro_results);

					prompt_info_tv
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									String info_text_now = "Information:";
									if (is_expands.get(this_ct) == false) {
										is_expands.set(this_ct, true);
										if (is_edit_info_list.get(this_ct) == false) {
											info_tv.setVisibility(View.VISIBLE);
											info_tv.setText(saved_info
													.get(this_ct));
											info_et.setVisibility(View.GONE);
										} else {
											info_tv.setVisibility(View.GONE);
											info_et.setVisibility(View.VISIBLE);
											info_et.setText(saved_info
													.get(this_ct));
										}
										edit_info_button
												.setVisibility(View.VISIBLE);
										info_text_now = "-" + info_text_now;
										Spannable spantext = Spannable.Factory
												.getInstance().newSpannable(
														info_text_now);
										spantext.setSpan(
												new BackgroundColorSpan(
														sharedPref.getInt(
																"BUTTON Color",
																Color.GREEN)),
												0,
												info_text_now.length(),
												Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
										prompt_info_tv.setText(spantext);
									} else {
										is_expands.set(this_ct, false);
										info_tv.setVisibility(View.GONE);
										edit_info_button
												.setVisibility(View.GONE);
										info_et.setVisibility(View.GONE);
										saved_info.set(this_ct, info_et
												.getText().toString());
										info_text_now = "+" + info_text_now;
										Spannable spantext = Spannable.Factory
												.getInstance().newSpannable(
														info_text_now);
										spantext.setSpan(
												new BackgroundColorSpan(
														sharedPref.getInt(
																"BUTTON Color",
																Color.GREEN)),
												0,
												info_text_now.length(),
												Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
										prompt_info_tv.setText(spantext);
									}
								}
							});

					edit_info_button
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									if (is_edit_info_list.get(this_ct) == false) {
										info_tv.setVisibility(View.GONE);
										info_et.setVisibility(View.VISIBLE);
										info_et.setText(saved_info.get(this_ct));
										is_edit_info_list.set(this_ct, true);
									} else {
										info_et.setVisibility(View.GONE);
										info_tv.setVisibility(View.VISIBLE);
										info_results
												.setVisibility(View.VISIBLE);
										info_tv.setText(info_et.getText()
												.toString());
										cv.clear();
										cv.put(acrostics.Information, info_et.getText()
												.toString());
										saved_info.set(this_ct, info_et
												.getText().toString());
										MainLfqActivity.getAcrosticsDb().update(this_table, cv, acrostics.Name+"=?",
												new String[] { this_word });
										cv.clear();
										sql = "UPDATE " + Helpers.db_prefix + tables.acrostics + "."
												+ this_table
												+ " SET " + acrostics.Information + "='"
												+ info_et.getText().toString()
												+ "' WHERE " + acrostics.Name + "='" + this_word
												+ "'";
										// autoSync(sql, db, action, table,
										// name, bool is_image,
										// byte[]
										// image)
										Synchronize.autoSync(sql, "acr_db",
												"update", this_table,
												this_word, false, null);
										is_edit_info_list.set(this_ct, false);
										info_results.setText(Html
												.fromHtml("<b>UPDATED "
														+ this_word + ".</b>"));
										info_results.setTextSize(24);
									}
								}
							});
					edit_acro_button
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									if (is_edit_acro_list.get(this_ct) == false) {
										acro_tv.setVisibility(View.GONE);
										acro_et.setVisibility(View.VISIBLE);
										acro_et.setText(saved_acro.get(this_ct));
										is_edit_acro_list.set(this_ct, true);
									} else {
										acro_et.setVisibility(View.GONE);
										acro_tv.setVisibility(View.VISIBLE);
										acro_results
												.setVisibility(View.VISIBLE);
										acro_tv.setText(acro_et.getText()
												.toString());
										cv.clear();
										cv.put("Acrostics", acro_et.getText()
												.toString());
										saved_acro.set(this_ct, acro_et
												.getText().toString());
										MainLfqActivity.getAcrosticsDb().update(this_table, cv, acrostics.Name + "=?",
												new String[] { this_word });

										cv.clear();
										sql = "UPDATE " + Helpers.db_prefix + tables.acrostics + "."
												+ this_table
												+ " SET " + acrostics.Acrostics + "='"
												+ acro_et.getText().toString()
												+ "' WHERE " + acrostics.Name + "='" + this_word
												+ "'";
										// autoSync(sql, db, action, table,
										// name, bool is_image,
										// byte[]
										// image)
										Synchronize.autoSync(sql, "acr_db",
												"update", this_table,
												this_word, false, null);
										is_edit_acro_list.set(this_ct, false);
										acro_results.setText(Html
												.fromHtml("<b>UPDATED "
														+ this_word + ".</b>"));
										acro_results.setTextSize(24);
									}
								}
							});
				}
			} while (c_getword.moveToNext());
		} else {
			results.setText(Html.fromHtml("<b><u>NO NEWWORDS FOR "
					+ date_before + "</u></b>"));
		}
		c_getword.close();
	}
}