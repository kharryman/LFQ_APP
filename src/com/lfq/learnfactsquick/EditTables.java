package com.lfq.learnfactsquick;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class EditTables extends Activity {
	private EditText table_input;
	private Spinner select_edit_table;
	private Spinner select_data_type1, select_data_type2, select_data_type3,
			select_data_type4, select_data_type5, select_data_type6,
			select_data_type7;
	private TextView results;
	private EditText edit_cat1, edit_cat2, edit_cat3, edit_cat4, edit_cat5,
			edit_cat6, edit_cat7;
	private EditText[] edit_cat_arr;
	private EditText edit_dat_typ1, edit_dat_typ2, edit_dat_typ3,
			edit_dat_typ4, edit_dat_typ5, edit_dat_typ6, edit_dat_typ7;
	private EditText[] edit_dat_typ_arr;
	private RadioButton rad_get_table, rad_edit_table, rad_create_table,
			rad_change_table_name;
	private Button edit_table, clear_table;
	private CheckBox check_inc_mne_col, check_table_done;
	private Cursor c;
	ArrayAdapter<String> tablesAdapter;	
	private String text, sql;
	private ContentValues cv;
	private Activity this_act;
	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;

	private Helpers h;
	private MenuItem menu_item_autosync_on, menu_item_autosync_off;
	private String autosync_text;
	private static Boolean is_database_load;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this_act = this;
		is_database_load = false;
		sharedPref = getSharedPreferences(
				getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		editor = sharedPref.edit();
		text = "";
		sql = "";
		autosync_text = "";
		c = null;
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
		editor.putString("EDIT TABLES TABLE INPUT", table_input.getText()
				.toString());
		editor.putString("EDIT TABLES SELECT TABLE", select_edit_table
				.getSelectedItem().toString());

		editor.putString("EDIT TABLES CAT 1", edit_cat1.getText().toString());
		editor.putString("EDIT TABLES CAT 2", edit_cat2.getText().toString());
		editor.putString("EDIT TABLES CAT 3", edit_cat3.getText().toString());
		editor.putString("EDIT TABLES CAT 4", edit_cat4.getText().toString());
		editor.putString("EDIT TABLES CAT 5", edit_cat5.getText().toString());
		editor.putString("EDIT TABLES CAT 6", edit_cat6.getText().toString());
		editor.putString("EDIT TABLES CAT 7", edit_cat7.getText().toString());

		editor.putString("EDIT TABLES DATA TYPE 1", edit_dat_typ1.getText()
				.toString());
		editor.putString("EDIT TABLES DATA TYPE 2", edit_dat_typ2.getText()
				.toString());
		editor.putString("EDIT TABLES DATA TYPE 3", edit_dat_typ3.getText()
				.toString());
		editor.putString("EDIT TABLES DATA TYPE 4", edit_dat_typ4.getText()
				.toString());
		editor.putString("EDIT TABLES DATA TYPE 5", edit_dat_typ5.getText()
				.toString());
		editor.putString("EDIT TABLES DATA TYPE 6", edit_dat_typ6.getText()
				.toString());
		editor.putString("EDIT TABLES DATA TYPE 7", edit_dat_typ7.getText()
				.toString());

		editor.putBoolean("EDIT TABLES GET TABLE", rad_get_table.isChecked());
		editor.putBoolean("EDIT TABLES EDIT TABLE", rad_edit_table.isChecked());
		editor.putBoolean("EDIT TABLES CREATE TABLE",
				rad_create_table.isChecked());
		//editor.putBoolean("EDIT TABLES DELETE TABLE",rad_delete_table.isChecked());
		editor.putBoolean("EDIT TABLES CHANGE TABLE",
				rad_change_table_name.isChecked());

		editor.putBoolean("EDIT TABLES CHECK INCLUDE MNE COLUMN",
				check_inc_mne_col.isChecked());
		editor.putBoolean("EDIT TABLES CHECK TABLE DONE",
				check_table_done.isChecked());

		editor.commit();

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		saveChanges();
		super.onConfigurationChanged(newConfig);
	}

	public void setViews() {
		setTitle("EDIT TABLES");
		results = (TextView) findViewById(R.id.results_edit_table);
		table_input = (EditText) findViewById(R.id.table_input);
		select_edit_table = (Spinner) findViewById(R.id.select_edit_table);
		tablesAdapter = new ArrayAdapter<String>(this_act,
				android.R.layout.simple_spinner_item, new ArrayList<String>());
		tablesAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		select_edit_table.setAdapter(tablesAdapter);
		select_data_type1 = (Spinner) findViewById(R.id.select_data_type1);
		select_data_type2 = (Spinner) findViewById(R.id.select_data_type2);
		select_data_type3 = (Spinner) findViewById(R.id.select_data_type3);
		select_data_type4 = (Spinner) findViewById(R.id.select_data_type4);
		select_data_type5 = (Spinner) findViewById(R.id.select_data_type5);
		select_data_type6 = (Spinner) findViewById(R.id.select_data_type6);
		select_data_type7 = (Spinner) findViewById(R.id.select_data_type7);
		edit_cat1 = (EditText) findViewById(R.id.edit_cat1);
		edit_cat2 = (EditText) findViewById(R.id.edit_cat2);
		edit_cat3 = (EditText) findViewById(R.id.edit_cat3);
		edit_cat4 = (EditText) findViewById(R.id.edit_cat4);
		edit_cat5 = (EditText) findViewById(R.id.edit_cat5);
		edit_cat6 = (EditText) findViewById(R.id.edit_cat6);
		edit_cat7 = (EditText) findViewById(R.id.edit_cat7);
		edit_cat_arr = new EditText[] { edit_cat1, edit_cat2, edit_cat3,
				edit_cat4, edit_cat5, edit_cat6, edit_cat7 };
		edit_dat_typ1 = (EditText) findViewById(R.id.edit_dat_typ1);
		edit_dat_typ2 = (EditText) findViewById(R.id.edit_dat_typ2);
		edit_dat_typ3 = (EditText) findViewById(R.id.edit_dat_typ3);
		edit_dat_typ4 = (EditText) findViewById(R.id.edit_dat_typ4);
		edit_dat_typ5 = (EditText) findViewById(R.id.edit_dat_typ5);
		edit_dat_typ6 = (EditText) findViewById(R.id.edit_dat_typ6);
		edit_dat_typ7 = (EditText) findViewById(R.id.edit_dat_typ7);
		edit_dat_typ_arr = new EditText[] { edit_dat_typ1, edit_dat_typ2,
				edit_dat_typ3, edit_dat_typ4, edit_dat_typ5, edit_dat_typ6,
				edit_dat_typ7 };

		rad_get_table = (RadioButton) findViewById(R.id.rad_get_table);
		rad_edit_table = (RadioButton) findViewById(R.id.rad_edit_table);
		rad_create_table = (RadioButton) findViewById(R.id.rad_create_table);
		//rad_delete_table = (RadioButton) findViewById(R.id.rad_delete_table);
		rad_change_table_name = (RadioButton) findViewById(R.id.rad_change_table_name);

		edit_table = (Button) findViewById(R.id.edit_table);
		clear_table = (Button) findViewById(R.id.clear_table);
		check_inc_mne_col = (CheckBox) findViewById(R.id.check_inc_mne_col);
		check_table_done = (CheckBox) findViewById(R.id.check_table_done);

	}

	public void loadButtons() {
		edit_table.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		clear_table.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
	}

	public void setListeners() {
		edit_table.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (rad_get_table.isChecked()) {
					getTable(select_edit_table.getSelectedItem().toString());
				}
				if (rad_edit_table.isChecked()) {
					editTable();
				}
				if (rad_create_table.isChecked()) {
					createTable();
				}
//				if (rad_delete_table.isChecked()) {
//					deleteTable();
//				}
				if (rad_change_table_name.isChecked()) {
					changeTableName();
				}

			}
		});

		clear_table.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				clearAll();

			}
		});

		select_edit_table
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						getTable(select_edit_table.getSelectedItem().toString());

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}

				});

		select_data_type1
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						edit_dat_typ1.setText(select_data_type1
								.getSelectedItem().toString());

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}

				});
		select_data_type2
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						edit_dat_typ2.setText(select_data_type2
								.getSelectedItem().toString());

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}

				});
		select_data_type3
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						edit_dat_typ3.setText(select_data_type3
								.getSelectedItem().toString());

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}

				});
		select_data_type4
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						edit_dat_typ4.setText(select_data_type4
								.getSelectedItem().toString());

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}

				});
		select_data_type5
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						edit_dat_typ5.setText(select_data_type5
								.getSelectedItem().toString());

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}

				});
		select_data_type6
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						edit_dat_typ6.setText(select_data_type6
								.getSelectedItem().toString());

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}

				});
		select_data_type7
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						edit_dat_typ7.setText(select_data_type7
								.getSelectedItem().toString());

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}

				});

	}

	public void clearAll() {
		for (int i = 0; i < 7; i++) {
			edit_cat_arr[i].setText("");
			edit_dat_typ_arr[i].setText("");
		}
	}

	private void getTable(String sel_table) {
		clearAll();
		table_input.setText(sel_table);
		Cursor c_get = MainLfqActivity.getAcrosticsDb().query(sel_table, null, null, null, null, null,
				null);
		Cursor c_dat = null;
		String[] cols = c_get.getColumnNames();
		int ct_ind = 0;
		String col = "";
		for (int i = 0; i < cols.length; i++) {
			if (!cols[i].equals("_id") && !cols[i].equals("Name")
					&& !cols[i].equals("Information")
					&& !cols[i].equals("Acrostics")
					&& !cols[i].equals("Mnemonics")
					&& !cols[i].equals("Peglist") && !cols[i].equals("Image")) {
				col = cols[i];
				System.out.println("OTH COLS=" + col);
				c_dat = MainLfqActivity.getAcrosticsDb().rawQuery("select typeof (" + col + ") from "
						+ sel_table, null);
				c_dat.moveToFirst();
				edit_cat_arr[ct_ind].setText(col);
				edit_dat_typ_arr[ct_ind].setText(c_dat.getString(0));
				ct_ind++;
			}
		}
	}

	private void createTable() {
		MainLfqActivity.getAcrosticsDb().execSQL("CREATE TABLE "
				+ table_input.getText().toString()
				+ " (_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,Name nvarchar(255),Information nvarchar(4000),Acrostics TEXT,Image nvarchar(255))");
		String sql_add_text = "";
		if (check_inc_mne_col.isChecked()) {
			MainLfqActivity.getAcrosticsDb().execSQL("ALTER TABLE " + table_input.getText()
					+ " ADD Mnemonics TEXT");
			MainLfqActivity.getAcrosticsDb().execSQL("ALTER TABLE " + table_input.getText()
					+ " ADD Peglist TEXT");
			text += "WITH MNE and PEG";
			sql_add_text += ",Mnemonics TEXT,Peglist TEXT";
		}
		for (int i = 0; i < 7; i++) {
			if (!edit_cat_arr[i].getText().equals("")) {
				MainLfqActivity.getAcrosticsDb().execSQL("ALTER TABLE " + table_input.getText() + " ADD "
						+ edit_cat_arr[i].getText().toString() + " "
						+ edit_dat_typ_arr[i].getText().toString());
				text += edit_cat_arr[i].getText().toString() + " "
						+ edit_dat_typ_arr[i].getText().toString() + ",";
				sql_add_text += ","
						+ edit_cat_arr[i].getText().toString()
						+ " "
						+ edit_dat_typ_arr[i].getText().toString()
								.toUpperCase(Locale.US);
			}
		}
		cv.clear();
		sql = "CREATE TABLE "
				+ table_input.getText().toString()
				+ " (_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,Name nvarchar(255),Information nvarchar(4000),Acrostics TEXT,Image nvarchar(255)"
				+ sql_add_text + ")";
		// autoSync(sql, db, action, table, name, bool is_image, byte[]
		// image)
		autosync_text = Synchronize.autoSync(sql, "acr_db", "insert_table",
				table_input.getText().toString(), "", false, null);
		results.setText("INSERTED " + table_input.getText().toString() + "."
				+ autosync_text);
	}

//	private void deleteTable() {
//		MainLfqActivity.getAcrosticsDb().execSQL("DROP TABLE "
//				+ select_edit_table.getSelectedItem().toString());
//		sql = "DROP TABLE " + Helpers.db_prefix + "acrostics."
//				+ select_edit_table.getSelectedItem().toString();
//		// autoSync(sql, db, action, table, name, bool is_image, byte[]
//		// image)
//		autosync_text = Synchronize
//				.autoSync(sql, "acr_db", "delete_table", select_edit_table
//						.getSelectedItem().toString(), "", false, null);
//		results.setText("DELETED "
//				+ select_edit_table.getSelectedItem().toString()
//				+ " FROM ACROSTIC TABLES." + autosync_text);
//
//	}

	private void editTable() {
		{
			autosync_text = "";
			Cursor c_cols = MainLfqActivity.getAcrosticsDb().rawQuery("SHOW COLUMNS FROM "
					+ table_input.getText().toString(), null);
			c_cols.moveToFirst();
			List<String> cat_arr = new ArrayList<String>();
			int ct = 0;
			while (c_cols.moveToNext()) {
				if (!c_cols.getString(0).equals("_id")
						&& !c_cols.getString(0).equals("Name")
						&& !c_cols.getString(0).equals("Information")
						&& !c_cols.getString(0).equals("Acrostics")
						&& !c_cols.getString(0).equals("Mnemonics")
						&& !c_cols.getString(0).equals("Peglist")
						&& !c_cols.getString(0).equals("Image")) {
					cat_arr.add(ct, c_cols.getString(0));
					ct++;
				}
			}
			String texchacol = "", texdrocol = "", texaddcol = "", texdone = "";
			if (check_inc_mne_col.isChecked()) {
				MainLfqActivity.getAcrosticsDb().execSQL("ALTER TABLE "
						+ table_input.getText().toString()
						+ " ADD Mnemonics TEXT,Peglist TEXT");
				cv.clear();
				sql = "ALTER TABLE " + Helpers.db_prefix + "acrostics."
						+ table_input.getText().toString()
						+ " ADD Mnemonics TEXT,Peglist TEXT";
				// autoSync(sql, db, action, table, name, bool is_image, byte[]
				// image)
				autosync_text += Synchronize.autoSync(sql, "acr_db", "alter",
						table_input.getText().toString(), "", false, null);

			}
			for (int i = 0; i < 7; i++) {
				if (!edit_cat_arr[i].getText().equals("")
						&& !cat_arr.get(i).equals("")
						&& !edit_cat_arr[i].getText().toString()
								.equals(cat_arr.get(i)))// change
				// column
				// name
				{
					MainLfqActivity.getAcrosticsDb().execSQL("ALTER TABLE " + table_input.getText()
							+ " CHANGE " + cat_arr.get(i) + " "
							+ edit_cat_arr[i].getText().toString() + " "
							+ edit_dat_typ_arr[i].getText().toString());
					texchacol += "CHANGED " + cat_arr.get(i) + " TO "
							+ edit_cat_arr[i].getText().toString() + " "
							+ edit_dat_typ_arr[i].getText().toString() + ",";
					cv.clear();
					sql = "ALTER TABLE " + Helpers.db_prefix + "acrostics."
							+ table_input.getText()
							+ " CHANGE "
							+ cat_arr.get(i)
							+ " "
							+ edit_cat_arr[i].getText().toString()
							+ " "
							+ edit_dat_typ_arr[i].getText().toString()
									.toUpperCase(Locale.US);
					// autoSync(sql, db, action, table, name, bool is_image,
					// byte[]
					// image)
					autosync_text += Synchronize.autoSync(sql, "acr_db",
							"alter", table_input.getText().toString(), "",
							false, null);
					autosync_text += cat_arr.get(i)
							+ " "
							+ edit_cat_arr[i].getText().toString()
							+ " "
							+ edit_dat_typ_arr[i].getText().toString()
									.toUpperCase(Locale.US);
				}
				if (edit_cat_arr[i].getText().equals("")
						&& !cat_arr.get(i).equals("")) {
					MainLfqActivity.getAcrosticsDb().execSQL("ALTER TABLE " + table_input
							+ " DROP COLUMN " + cat_arr.get(i));
					texdrocol += "DROPPED COLUMN " + cat_arr.get(i) + ",";
					sql = "ALTER TABLE " + Helpers.db_prefix + "acrostics."
							+ table_input.getText().toString()
							+ " DROP COLUMN " + cat_arr.get(i);
					// autoSync(sql, db, action, table, name, bool is_image,
					// byte[]
					// image)
					autosync_text += Synchronize.autoSync(sql, "acr_db",
							"alter", table_input.getText().toString(), "",
							false, null);
				}
				if (!edit_cat_arr[i].getText().equals("")
						&& cat_arr.get(i).equals("")) {
					MainLfqActivity.getAcrosticsDb().execSQL("ALTER TABLE "
							+ table_input.getText().toString() + " ADD "
							+ edit_cat_arr[i].getText().toString() + " "
							+ edit_dat_typ_arr[i].getText().toString());
					texaddcol += "ADDED COLUMN "
							+ edit_cat_arr[i].getText().toString() + " "
							+ edit_dat_typ_arr[i].getText().toString() + ",";
					sql = "ALTER TABLE " + Helpers.db_prefix + "acrostics."
							+ table_input.getText().toString()
							+ " ADD "
							+ edit_cat_arr[i].getText().toString()
							+ " "
							+ edit_dat_typ_arr[i].getText().toString()
									.toUpperCase(Locale.US);
					autosync_text += Synchronize.autoSync(sql, "acr_db",
							"alter", table_input.getText().toString(), "",
							false, null);

				}
			}
			if (check_table_done.isChecked()) {
				ContentValues values = new ContentValues();
				values.put("Done", "Y");
				MainLfqActivity.getAcrosticsDb().update("user_table", values, "WHERE Table='"
						+ table_input.getText() + "'", null);
				texdone += " DONE.";
			}
			if (!check_table_done.isChecked()) {
				ContentValues values = new ContentValues();
				values.put("Done", "N");
				MainLfqActivity.getAcrosticsDb().update("user_table", values, "WHERE Table='"
						+ table_input.getText() + "'", null);
				texdone += "NOT DONE.";
			}
			results.setText(texchacol + texdrocol + texaddcol + texdone
					+ autosync_text);
		}

	}

	private void changeTableName() {
		MainLfqActivity.getAcrosticsDb().execSQL("RENAME TABLE "
				+ select_edit_table.getSelectedItem().toString() + " TO "
				+ table_input.getText());
		autosync_text = "";
		sql = "RENAME TABLE " + Helpers.db_prefix + "acrostics."
				+ select_edit_table.getSelectedItem().toString()
				+ " TO " + Helpers.db_prefix + "acrostics." + table_input.getText();
		// autoSync(sql, db, action, table, name, bool is_image, byte[]
		// image)
		autosync_text += Synchronize.autoSync(sql, "acr_db",
				"change_table_name", select_edit_table.getSelectedItem()
						.toString(), "ACROSTIC_TABLE_NAME_UPDATE", false, null);
		results.setText("RENAMED TABLE "
				+ select_edit_table.getSelectedItem().toString() + " TO "
				+ table_input.getText() + "." + autosync_text);

	}

	class doLoadDatabases extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setContentView(R.layout.edit_acrostics_tables);
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
			loadTablesAdapter();
			results.setText("");

			table_input.setText(sharedPref.getString("EDIT TABLES TABLE INPUT",
					""));
			select_edit_table.setSelection(tablesAdapter.getPosition(sharedPref
					.getString("EDIT TABLES SELECT TABLE", select_edit_table
							.getItemAtPosition(0).toString())));

			edit_cat1.setText(sharedPref.getString("EDIT TABLES CAT 1", ""));
			edit_cat2.setText(sharedPref.getString("EDIT TABLES CAT 2", ""));
			edit_cat3.setText(sharedPref.getString("EDIT TABLES CAT 3", ""));
			edit_cat4.setText(sharedPref.getString("EDIT TABLES CAT 4", ""));
			edit_cat5.setText(sharedPref.getString("EDIT TABLES CAT 5", ""));
			edit_cat6.setText(sharedPref.getString("EDIT TABLES CAT 6", ""));
			edit_cat7.setText(sharedPref.getString("EDIT TABLES CAT 7", ""));

			edit_dat_typ1.setText(sharedPref.getString(
					"EDIT TABLES DATA TYPE 1", ""));
			edit_dat_typ2.setText(sharedPref.getString(
					"EDIT TABLES DATA TYPE 2", ""));
			edit_dat_typ3.setText(sharedPref.getString(
					"EDIT TABLES DATA TYPE 3", ""));
			edit_dat_typ4.setText(sharedPref.getString(
					"EDIT TABLES DATA TYPE 4", ""));
			edit_dat_typ5.setText(sharedPref.getString(
					"EDIT TABLES DATA TYPE 5", ""));
			edit_dat_typ6.setText(sharedPref.getString(
					"EDIT TABLES DATA TYPE 6", ""));
			edit_dat_typ7.setText(sharedPref.getString(
					"EDIT TABLES DATA TYPE 7", ""));

			rad_get_table.setChecked(sharedPref.getBoolean(
					"EDIT TABLES GET TABLE", false));
			rad_edit_table.setChecked(sharedPref.getBoolean(
					"EDIT TABLES EDIT TABLE", false));
			rad_create_table.setChecked(sharedPref.getBoolean(
					"EDIT TABLES CREATE TABLE", false));
			//rad_delete_table.setChecked(sharedPref.getBoolean("EDIT TABLES DELETE TABLE", false));
			rad_change_table_name.setChecked(sharedPref.getBoolean(
					"EDIT TABLES CHANGE TABLE", false));

			check_inc_mne_col.setChecked(sharedPref.getBoolean(
					"EDIT TABLES CHECK INCLUDE MNE COLUMN", false));
			check_table_done.setChecked(sharedPref.getBoolean(
					"EDIT TABLES CHECK TABLE DONE", false));

		}

	}

	public void loadTablesAdapter() {
		c = MainLfqActivity.getAcrosticsDb().rawQuery(" SELECT name FROM sqlite_master "
				+ " WHERE type='table' ORDER BY name", null);
		if (c.moveToFirst()) {
			do {
				if (!c.getString(0).equals("android_metadata")
						&& !c.getString(0).equals("sqlite_sequence"))
					tablesAdapter.add(c.getString(0));
			} while (c.moveToNext());
		}
		c.close();
	}

}
