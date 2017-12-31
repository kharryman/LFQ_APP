package com.lfq.learnfactsquick;

import com.lfq.learnfactsquick.Constants.cols.global_number_table;
import com.lfq.learnfactsquick.Constants.tables;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Numbers extends Activity {
	private RelativeLayout numbers_above_layout;
	private TextView results, login_status;
	private TableLayout numbers_table, numbers_header_table;
	private EditText username_input, password_input;
	private Button do_get_numbers, do_login, do_logout, backup;
	private RadioButton check_user_numbers, check_shared_numbers;
		
	private String text;// to be used as a buffer(for debugging too)
	private String[] textspl;
	private String username, password;

	private Boolean logged_in;
	private android.widget.TableRow.LayoutParams rowParams, cellParams;
	private Helpers h;
	private Activity this_act;
	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;
	private static Boolean is_database_load;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this_act = this;
		is_database_load = false;
		sharedPref = getSharedPreferences(
				getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		editor = sharedPref.edit();
		username = "";
		password = "";
		text = "";
		logged_in = false;
		h = new Helpers(this_act);

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
		MenuItem autosync = menu.findItem(R.id.autosync);
		autosync.setVisible(false);
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
		return true;
	}

	public void saveChanges() {
		editor.putString("NUMBERS USERNAME INPUT", username_input.getText()
				.toString());

		editor.putBoolean("NUMBERS CHECK USER NUMBERS",
				check_user_numbers.isChecked());
		editor.putBoolean("NUMBERS CHECK SHARED NUMBERS",
				check_shared_numbers.isChecked());

		editor.commit();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		saveChanges();
		super.onConfigurationChanged(newConfig);
	}

	public void setViews() {
		// LAYOUTS:
		numbers_above_layout = (RelativeLayout) findViewById(R.id.numbers_above_layout);

		// BUTTONS:
		do_login = (Button) findViewById(R.id.do_login_numbers);
		do_logout = (Button) findViewById(R.id.do_logout_numbers);
		do_get_numbers = (Button) findViewById(R.id.do_get_numbers);
		backup = (Button) findViewById(R.id.numbers_backup);
		backup.setVisibility(View.GONE);

		// EDITTEXTS:
		username_input = (EditText) findViewById(R.id.numbers_username);
		password_input = (EditText) findViewById(R.id.numbers_password);

		// RADIOBUTTONS:
		check_user_numbers = (RadioButton) findViewById(R.id.check_user_numbers);
		check_shared_numbers = (RadioButton) findViewById(R.id.check_shared_numbers);

		// RADIOGROUPS:

		// TABLELAYOUTS:
		numbers_table = (TableLayout) findViewById(R.id.numbers_table);
		numbers_header_table = (TableLayout) findViewById(R.id.numbers_header_table);
		numbers_header_table.setVisibility(View.GONE);
		rowParams = new TableRow.LayoutParams(
				TableRow.LayoutParams.MATCH_PARENT,
				TableRow.LayoutParams.WRAP_CONTENT);
		// cellParams.weight=1;

		// TEXTVIEWS:
		results = (TextView) findViewById(R.id.numbers_results);
		login_status = (TextView) findViewById(R.id.numbers_login_status);
	}

	public void loadButtons() {
		do_login.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		do_logout.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		do_get_numbers.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		backup.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
	}

	public void setListeners() {

		backup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				numbers_above_layout.setVisibility(View.VISIBLE);
				backup.setVisibility(View.GONE);
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

		do_get_numbers.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				numbers_header_table.setVisibility(View.VISIBLE);
				Cursor c_get = null;
				String table = "";
				if (check_shared_numbers.isChecked()) {
					table = tables.global_number_table;
				}
				if (check_user_numbers.isChecked()) {
					if (logged_in == false) {
						results.setText("NOT LOGGED IN.");
						return;
					}
					table = username + "_numbertable";
				}
				numbers_above_layout.setVisibility(View.GONE);
				backup.setVisibility(View.VISIBLE);
				c_get = MainLfqActivity.getMiscDb().rawQuery("SELECT * FROM " + table
						+ " ORDER BY " + global_number_table.Type, null);
				if (c_get.moveToFirst()) {
					numbers_table.removeAllViews();

					do {
						TableRow row = new TableRow(this_act);
						// row.setBackgroundColor(Color.WHITE);
						// row.setLayoutParams(rowParams);
						numbers_table.addView(row, rowParams);

						cellParams = new TableRow.LayoutParams(150,
								TableRow.LayoutParams.MATCH_PARENT);
						TextView tv_type = new TextView(this_act);
						tv_type.setText(c_get.getString(c_get
								.getColumnIndex(global_number_table.Type)));
						tv_type.setPadding(1, 1, 1, 1);
						tv_type.setBackgroundResource(R.drawable.cell_shape);
						row.addView(tv_type, cellParams);

						cellParams = new TableRow.LayoutParams(150,
								TableRow.LayoutParams.MATCH_PARENT);
						TextView tv_num = new TextView(this_act);
						tv_num.setText(c_get.getString(c_get
								.getColumnIndex("Number")));
						tv_num.setPadding(1, 1, 1, 1);
						tv_num.setBackgroundResource(R.drawable.cell_shape);
						row.addView(tv_num, cellParams);

						cellParams = new TableRow.LayoutParams(400,
								TableRow.LayoutParams.MATCH_PARENT);
						TextView tv_info = new TextView(this_act);
						tv_info.setPadding(1, 1, 1, 1);
						tv_info.setText(c_get.getString(c_get
								.getColumnIndex("NumInf")));
						tv_info.setBackgroundResource(R.drawable.cell_shape);
						row.addView(tv_info, cellParams);

						cellParams = new TableRow.LayoutParams(400,
								TableRow.LayoutParams.MATCH_PARENT);
						TextView tv_words = new TextView(this_act);
						tv_words.setPadding(1, 1, 1, 1);
						text = c_get.getString(c_get.getColumnIndex(global_number_table.NumWors));
						textspl = text.split("@@@");
						text = "";
						for (int i = 0; i < textspl.length; i++) {
							text += textspl[i] + "<br />";
						}
						tv_words.setText(Html.fromHtml(text));
						tv_words.setBackgroundResource(R.drawable.cell_shape);
						row.addView(tv_words, cellParams);

					} while (c_get.moveToNext());

				}
			}
		});

		username_input.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				username_input.setText(username_input.getText().toString()
						.trim());
			}
		});
		password_input.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				password_input.setText(password_input.getText().toString()
						.trim());
			}
		});

	}

	class doLoadDatabases extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setContentView(R.layout.numbers);
			setTitle("YOUR NUMBERS!");
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
			text = "";
			results.setText("");
			username_input.setText(sharedPref.getString(
					"NUMBERS USERNAME INPUT", ""));
			check_user_numbers.setChecked(sharedPref.getBoolean(
					"NUMBERS CHECK USER NUMBERS", false));
			check_shared_numbers.setChecked(sharedPref.getBoolean(
					"NUMBERS CHECK SHARED NUMBERS", false));
			if (h.getLoginStatus() == true) {
				username = Helpers.getUsername();
				password = Helpers.getPassword();
				username_input.setText(username);
				logged_in = true;
				login_status.setText("WELCOME " + username + ".");
			}
			setListeners();

		}

	}

}
