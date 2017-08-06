package com.lfq.learnfactsquick;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

@SuppressLint("ClickableViewAccessibility")
public class OldSynchronize extends Activity {	
	private ScrollView sync_scroll, sync_display_scroll;
	private android.widget.LinearLayout.LayoutParams linear_params;
	private static TextView sync_results, login_status;
	private TextView sync_display_results,sql_to_queries, sql_from_queries, results_from_lfq, show_no_my_tables, show_no_lfq_tables, prompt_sync_display; 
	private Button do_login, do_logout, do_sync_to, do_sync_from,
			do_sync_tables, sync_clear, sync_left, sync_right;
	private Spinner select_my_database, select_my_table, select_lfq_table,
			select_lfq_database;
	private static EditText username_input;
	private EditText password_input;
	private CheckBox check_only_databases, check_make_new_tables;
	private static String text, autosync_result;
	private static Boolean logged_in;

	private static SQLiteDatabase sync_db, acr_db, alp_db, dictionary_db,
			events_db, misc_db, mne_db, newwords_db, numbers_db, users_db;

	JSONArray response = null;
	private static InputStream is = null;
	private static JSONObject jObj = null;
	private static String json_str = "";
	private ArrayAdapter<String> myTablesAdapter, myDatabasesAdapter,
			lfqTablesAdapter, lfqDatabasesAdapter;
	private String myDatabase, myTable, lfqDatabase, lfqTable, is_database;
	private Cursor c = null;	
	private static String username;
	private static String password;
	private String textspl[] = null;
	private Boolean is_show_no_my_tables;

	private List<String[]> from_lfq_queries_list;	

	private static String url = "";

	ArrayList<HashMap<String, String>> row_params;

	private ContentValues cv;	
	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;
	private static Boolean is_database_load;
	private int display_index;
	//private int measuredWidth;
	private int measuredHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		is_database_load = false;
		sharedPref = getSharedPreferences(
				getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		editor = sharedPref.edit();
		logged_in = false;
		is_show_no_my_tables = false;
		row_params = new ArrayList<HashMap<String, String>>();
		myDatabase = "";
		myTable = "";
		lfqDatabase = "";
		lfqTable = "";
		is_database = "";
		username = "";
		password = "";
		display_index = 0;
		//measuredWidth = 0;
		measuredHeight = 0;
		cv = new ContentValues();
		from_lfq_queries_list = new ArrayList<String[]>();		
		setContentView(R.layout.synchronize);
		setViews();
		loadButtons();
		setScrollListeners();
		setLoginListeners();
		if (Helpers.getLoginStatus() == true) {
			username = Helpers.getUsername();
			password = Helpers.getPassword();
			username_input.setText(username);
			logged_in = true;
			login_status.setText("WELCOME " + username + ".");
			new doLoadDatabases().execute();
		} else {
			sync_results
					.setText(Html.fromHtml("<b>PLEASE LOGIN TO START.</b>"));
		}

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
		editor.putBoolean("SYNCHRONIZE CHECK ONLY DATABASES",
				check_only_databases.isChecked());
		editor.putBoolean("SYNCHRONIZE CHECK MAKE NEW TABLES",
				check_make_new_tables.isChecked());
		editor.putString("SYNCHRONIZE USERNAME INPUT", username_input.getText()
				.toString());
		editor.putString("SYNCHRONIZE SELECT MY DATABASE", select_my_database
				.getSelectedItem().toString());
		editor.putString("SYNCHRONIZE SELECT LFQ DATABASE", select_lfq_database
				.getSelectedItem().toString());
		editor.commit();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		saveChanges();
		super.onConfigurationChanged(newConfig);
	}	
	
	
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public void setViews() {
		setTitle("SYNCHRONIZE TO LFQ.com");
		// LAYOUTS:
		

		// BUTTONS:
		do_sync_to = (Button) findViewById(R.id.do_sync_to);
		do_sync_from = (Button) findViewById(R.id.do_sync_from);
		sync_clear = (Button) findViewById(R.id.sync_clear);
		do_login = (Button) findViewById(R.id.sync_login);
		do_logout = (Button) findViewById(R.id.sync_logout);
		do_sync_tables = (Button) findViewById(R.id.do_sync_tables);
		sync_left = (Button) findViewById(R.id.sync_left);
		sync_right = (Button) findViewById(R.id.sync_right);

		// CHECKBOXES:
		check_only_databases = (CheckBox) findViewById(R.id.check_sync_databases);
		check_make_new_tables = (CheckBox) findViewById(R.id.check_make_new_tables);

		// EDITTEXTS:
		username_input = (EditText) findViewById(R.id.sync_username);
		password_input = (EditText) findViewById(R.id.sync_password);
		
		
		//SCROLLVIEWS:
		sync_scroll = (ScrollView) findViewById(R.id.sync_scroll);
		sync_display_scroll = (ScrollView) findViewById(R.id.sync_display_scroll);
				
		WindowManager w = getWindowManager();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point size = new Point();
			w.getDefaultDisplay().getSize(size);
			//measuredWidth = size.x;
			measuredHeight = size.y;
		} else {
			Display d = w.getDefaultDisplay();
			//measuredWidth = d.getWidth();
			measuredHeight = d.getHeight();
		}


		// SPINNERS:
		select_my_table = (Spinner) findViewById(R.id.select_sync_my_tables);
		myTablesAdapter = new ArrayAdapter<String>(LfqApp.getInstance(),
				android.R.layout.simple_spinner_item, new ArrayList<String>());
		myTablesAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		select_my_table.setAdapter(myTablesAdapter);
		select_lfq_table = (Spinner) findViewById(R.id.select_sync_lfq_tables);
		lfqTablesAdapter = new ArrayAdapter<String>(LfqApp.getInstance(),
				android.R.layout.simple_spinner_item, new ArrayList<String>());
		lfqTablesAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		select_lfq_table.setAdapter(lfqTablesAdapter);

		// TEXTVIEWS:
		sync_results = (TextView) findViewById(R.id.sync_results);
		sync_display_results = (TextView) findViewById(R.id.sync_display_results);
		sql_to_queries = (TextView) findViewById(R.id.sync_to_queries);
		sql_from_queries = (TextView) findViewById(R.id.sync_from_queries);
		results_from_lfq = (TextView) findViewById(R.id.results_sync_from_lfq);
		login_status = (TextView) findViewById(R.id.sync_login_status);
		show_no_my_tables = (TextView) findViewById(R.id.show_no_my_tables);
		show_no_lfq_tables = (TextView) findViewById(R.id.show_no_lfq_tables);
		prompt_sync_display = (TextView) findViewById(R.id.prompt_sync_display);
		if (Helpers.getLoginStatus() == true) {
			username = Helpers.getUsername();
			password = Helpers.getPassword();
			username_input.setText(username);
			logged_in = true;
			login_status.setText("WELCOME " + username + ".");
		}
	}

	public void loadButtons() {
		sync_left.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		sync_right.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		sync_clear.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		do_sync_to.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		do_sync_from.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		do_login.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		do_logout.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		do_sync_tables.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
	}

	@SuppressLint("ClickableViewAccessibility")
	public void setLoginListeners() {
		do_login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (is_database_load == false) {
					password = password_input.getText().toString();
					username = username_input.getText().toString();
					System.out.println(username + " " + password);
					text = Helpers.login(username, password);
					textspl = text.split("@@@");
					if (textspl[0].equals("true")) {
						logged_in = true;
						new doLoadDatabases().execute();
					}
					login_status.setText(textspl[1]);
				}
			}
		});

		do_logout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (is_database_load == false) {
					logged_in = false;
					password_input.setText("");
					login_status.setText("LOGGED OUT. BYE BYE " + username);
				}
			}
		});
	}

	@SuppressLint("ClickableViewAccessibility")
	public void setListeners() {
		
		sync_left.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {				
				if (display_index == 0) {
					return;
				} else {
					display_index--;
					setDisplay();
				}
			}
		});
		sync_right.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				if (display_index >= 2) {
					return;
				} else {
					display_index++;
					setDisplay();					
				}
			}
		});

		check_only_databases.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (check_only_databases.isChecked()) {
					select_my_table.setEnabled(false);
				} else {
					select_my_table.setEnabled(true);
				}
			}
		});

		select_lfq_database
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						select_my_table.setVisibility(View.VISIBLE);
						select_lfq_table.setVisibility(View.VISIBLE);
						if (!check_only_databases.isChecked()) {
							lfqDatabase = select_lfq_database.getSelectedItem()
									.toString();
							doGetLFQTables(lfqDatabase);
							doGetMyTables(lfqDatabase);
							select_my_database.setSelection(select_lfq_database
									.getSelectedItemPosition());
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});

		select_my_database
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						if (!check_only_databases.isChecked()) {
							myDatabase = select_my_database.getSelectedItem()
									.toString();
							doGetMyTables(myDatabase);
							doGetLFQTables(myDatabase);
							select_lfq_database.setSelection(select_my_database
									.getSelectedItemPosition());
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});

		select_lfq_table
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						lfqDatabase = select_lfq_database.getSelectedItem()
								.toString();
						lfqTable = select_lfq_table.getSelectedItem()
								.toString();
						if (lfqDatabase.equals("events")
								|| lfqDatabase.equals("miscellaneous")) {
							select_my_table.setSelection(select_lfq_table
									.getSelectedItemPosition());
						} else {
							if (myTablesAdapter.getPosition(lfqTable) != -1) {
								select_my_table.setVisibility(View.VISIBLE);
								is_show_no_my_tables = false;
								show_no_my_tables.setVisibility(View.GONE);
								select_my_table.setSelection(myTablesAdapter
										.getPosition(lfqTable));
							} else {
								show_no_my_tables.setVisibility(View.VISIBLE);
								is_show_no_my_tables = true;
								select_my_table.setVisibility(View.GONE);
							}
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});
		select_my_table.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				myDatabase = select_my_database.getSelectedItem().toString();
				myTable = select_my_table.getSelectedItem().toString();
				if (myDatabase.equals("events")
						|| myDatabase.equals("miscellaneous")) {
					select_lfq_table.setSelection(select_my_table
							.getSelectedItemPosition());
				} else {
					if (lfqTablesAdapter.getPosition(myTable) != -1) {
						select_lfq_table.setVisibility(View.VISIBLE);
						show_no_lfq_tables.setVisibility(View.GONE);
						select_lfq_table.setSelection(lfqTablesAdapter
								.getPosition(myTable));
					} else {
						show_no_lfq_tables.setVisibility(View.VISIBLE);
						select_lfq_table.setVisibility(View.GONE);

					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		sync_clear.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				sync_db.execSQL("DELETE FROM `sync_table`");
				doShowQueries();

			}
		});

		do_sync_tables.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				myDatabase = select_my_database.getSelectedItem().toString();
				if (!check_only_databases.isChecked()) {// FOR ONE TABLE
					is_database = "no";
					myTable = select_my_table.getSelectedItem().toString();
					System.out.println("is_database=" + is_database
							+ ",myDatabase=" + myDatabase + ",myTable="
							+ myTable);
					new doLoadTable(is_database, "sync", myDatabase, myTable)
							.execute();

				} else {// FOR ALL DATABASE
					is_database = "yes";
					new doLoadTable(is_database, "sync", myDatabase, "")
							.execute();
				}
			}
		});

		do_sync_from.setOnClickListener(new View.OnClickListener() {// SYNCS
					// FROM LFQ
					// to APP:
					@Override
					public void onClick(View v) {
						c = null;
						SQLiteDatabase myDB = null;
						String results = "";						
						String db = "";
						for (int i = 0; i < from_lfq_queries_list.size(); i++) {
							db = from_lfq_queries_list.get(i)[0];
							// acr_db, alp_db,
							// dictionary_db,events_db,misc_db,mne_db,nw_db,num_db,users_db
							if (db.equals("acr_db")) {
								myDB = acr_db;
							}
							if (db.equals("alp_db")) {
								myDB = alp_db;
							}
							if (db.equals("dictionary_db")) {
								myDB = dictionary_db;
							}
							if (db.equals("events_db")) {
								myDB = events_db;
							}
							if (db.equals("misc_db")) {
								myDB = misc_db;
							}
							if (db.equals("mne_db")) {
								myDB = mne_db;
							}
							if (db.equals("nw_db")) {
								myDB = newwords_db;
							}
							if (db.equals("num_db")) {
								myDB = numbers_db;
							}
							if (db.equals("users_db")) {
								myDB = users_db;
							}
							// from_lfq_queries_list.add(new String[] {db, id,
							// is_image ,res, image, table, name, entry, action,
							// user});
							//String id = from_lfq_queries_list.get(i)[1];
							String is_image = from_lfq_queries_list.get(i)[2];
							String query = from_lfq_queries_list.get(i)[3];
							String table = from_lfq_queries_list.get(i)[5];
							String name = from_lfq_queries_list.get(i)[6];
							String entry = from_lfq_queries_list.get(i)[7];
							String old_entry = from_lfq_queries_list.get(i)[8];
							String action = from_lfq_queries_list.get(i)[9];
							String user = from_lfq_queries_list.get(i)[10];
							if (is_image.equals("false")) {// NOT IMAGE:
								if (user.equals("") || user.equals(username)) {
									try {
										if (!db.equals("alp_db")) {
											myDB.execSQL(query);
											results += (i + 1) + ". SYNCED: "
													+ query + ". ";
										} else {
											if (action.equals("sql")) {
												myDB.execSQL(query);
												results += (i + 1)
														+ ". SYNCED: " + query
														+ ". ";
											}
											if (action.equals("update")) {
												cv.clear();
												cv.put(name, entry);
												myDB.update(
														table,
														cv,
														name + "=?",
														new String[] { old_entry });
												results += (i + 1)
														+ ". SYNCED: " + db
														+ " table:" + table
														+ ", letter:" + name
														+ ". ";
											}
											if (action.equals("insert")) {
												c = myDB.rawQuery(
														"SELECT _id FROM "
																+ table
																+ " WHERE "
																+ name
																+ " IS NULL OR "
																+ name
																+ "='' LIMIT 1",
														null);
												if (c.moveToFirst()) {
													cv.clear();
													cv.put(name, entry);
													myDB.update(
															table,
															cv,
															"_id=?",
															new String[] { c
																	.getString(c
																			.getColumnIndex("_id")) });
												} else {// insert:
													cv.clear();
													cv.put(name, entry);
													myDB.insert(table, null, cv);
												}
												results += (i + 1)
														+ ". SYNCED: " + db
														+ " table:" + table
														+ ", letter:" + name
														+ ". ";
												c.close();
											}
											if (action.equals("delete")) {
												cv.put(name, "");
												myDB.update(table, cv, name
														+ "=?",
														new String[] { entry });
												results += (i + 1)
														+ ". SYNCED: " + db
														+ " table:" + table
														+ ", letter:" + name
														+ ". ";
											}
										}
									} catch (Exception e) {
										results += (i + 1) + ". NOT SYNCED "
												+ query + "." + e.getMessage();
									}
								} else {
									results += "<b>NOT SYNCED. NOT LOGGED IN!</b>";
								}
								results += "<br />";
							} else {// IS IMAGE? -> INSERT:
								String imageString = from_lfq_queries_list
										.get(i)[3];
								byte[] imageInByte = Base64.decode(imageString,
										0);
								cv.clear();
								cv.put("Image", imageInByte);
								cv.put("Has_Image", "yes");
								myDB.update(table, cv, "Name=?",
										new String[] { name });
								results += "Updated " + table + ", " + name
										+ ". ";
							}							
						}// END FOR LOOP
					    //RESET DATABASES SYNCED DATES:						
						setDatabaseDate("DATE_ACR_SYNCED");
						setDatabaseDate("DATE_ALP_SYNCED");
						setDatabaseDate("DATE_DIC_SYNCED");
						setDatabaseDate("DATE_EVT_SYNCED");
						setDatabaseDate("DATE_MNE_SYNCED");
						setDatabaseDate("DATE_MSC_SYNCED");
						setDatabaseDate("DATE_NWS_SYNCED");
						setDatabaseDate("DATE_NUM_SYNCED");
						setDatabaseDate("DATE_USR_SYNCED");

						sync_results.setText(Html.fromHtml("<b>" + results
								+ "</b>"));
						doShowQueries();
					}
				});

		do_sync_to.setOnClickListener(new View.OnClickListener() {// SYNCS TO
					// LFQ.com
					@Override
					public void onClick(View arg0) {
						new doSyncTo().execute();
					}
				});

	}

	@SuppressLint("ClickableViewAccessibility")
	public void setScrollListeners() {
		sync_scroll.setOnTouchListener(new View.OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				findViewById(R.id.sync_display_scroll).getParent()
						.requestDisallowInterceptTouchEvent(false);
				return false;
			}
		});

		sync_display_scroll.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});		
	}
	
	public void setScrollDisplayHeight(){
		if (sync_display_scroll.getHeight()>measuredHeight/2){
		linear_params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, measuredHeight / 2);		
		}
		else{
			linear_params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);	
		}
		sync_display_scroll.setLayoutParams(linear_params);
	}
	
	public void setDisplay(){
		switch (display_index) {
		case 0:// RESULTS
			prompt_sync_display.setText(Html.fromHtml("<b>RESULTS:</b>"));
			sync_results.setVisibility(View.VISIBLE);
			sql_to_queries.setVisibility(View.GONE);
			sql_from_queries.setVisibility(View.GONE);
			setScrollDisplayHeight();
			break;
		case 1:// TO QUERIES
			prompt_sync_display.setText(Html.fromHtml("<b>QUERIES TO LFQ:</b>"));
			sync_results.setVisibility(View.GONE);
			sql_to_queries.setVisibility(View.VISIBLE);
			sql_from_queries.setVisibility(View.GONE);
			setScrollDisplayHeight();
			break;
		case 2:// FROM QUERIES
			prompt_sync_display.setText(Html.fromHtml("<b>QUERIES FROM LFQ:</b>"));
			sync_results.setVisibility(View.GONE);
			sql_to_queries.setVisibility(View.GONE);
			sql_from_queries.setVisibility(View.VISIBLE);
			setScrollDisplayHeight();
			break;
		}
	}

	public static String autoSync(String user, String device_id, String mySQL,
			boolean is_image, byte[] image, String table, String name) {
		text = "";
		autosync_result = "";
		try {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("SQL", mySQL));
			params.add(new BasicNameValuePair("User", mySQL));
			params.add(new BasicNameValuePair("Device_Id", device_id));
			String is_image_str = "no";
			if (is_image == true) {
				is_image_str = "yes";
			} else {
				is_image_str = "no";
			}
			params.add(new BasicNameValuePair("Is_Image", is_image_str));
			if (is_image == true) {
				params.add(new BasicNameValuePair("table", table));
				params.add(new BasicNameValuePair("name", name));
				params.add(new BasicNameValuePair("image", Base64
						.encodeToString(image, 0)));
			}
			url = "http://www.learnfactsquick.com/lfq_app_php/synchronize_from_app_auto.php";
			if (!isConnected()) {
				return "";
			}
			JSONObject json = makeHttpRequest(url, "POST", params);
			if (json == null) {
				return "";
			}
			autosync_result = json.getString("result");
			String debug = json.getString("DEBUG");
			text += " SYNC TO LFQ " + autosync_result + "." + debug;
			if (mySQL != "") {
				text += "mySQL = " + mySQL + ". ";
			}
			text += debug;
		} catch (JSONException e) {
			e.printStackTrace();
			text += "...FAILED CONNECTION.";
		}
		return text;
	}

	public static boolean isConnected() {
		boolean ret = true;
		if (!Helpers.isNetworkAvailable()) {
			if (sync_results != null) {
				sync_results.setText(Html
						.fromHtml("<b>NETWORK UNAVAILABLE.</b>"));
			}
			ret = false;
		}
		return ret;
	}

	public static JSONObject makeHttpRequest(String url, String method,
			List<NameValuePair> params) {
		// Making HTTP request
		try {
			// check for request method
			if (method == "POST") {
				// request method is POST
				// defaultHttpClient
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);
				httpPost.setEntity(new UrlEncodedFormEntity(params));
				HttpResponse httpResponse = httpClient.execute(httpPost);
				httpResponse.addHeader("Cache-Control", "no-cache");
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();

			} else if (method == "GET") {
				// request method is GET
				DefaultHttpClient httpClient = new DefaultHttpClient();
				String paramString = URLEncodedUtils.format(params, "utf-8");
				url += "?" + paramString;
				HttpGet httpGet = new HttpGet(url);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");

			}
			is.close();
			json_str = sb.toString();
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		// try parse the string to a JSON object
		try {
			jObj = new JSONObject(json_str);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}

		// return JSON String
		return jObj;
	}

	static public String setDatabaseDate(String date_database_synced) {
		if (!isConnected()) {
			return "NOT CONNECTED";
		}
		url = "http://www.learnfactsquick.com/lfq_app_php/synchronize_database_date.php";
		List<NameValuePair> args = new ArrayList<NameValuePair>();
		//TelephonyManager telephonyManager = (TelephonyManager) LfqApp.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
		//String device_id = telephonyManager.getDeviceId();
		String device_id = "";
		args.add(new BasicNameValuePair("device_id", device_id));
		args.add(new BasicNameValuePair("date_database_synced",
				date_database_synced));//EG: DATE_ACR_SYNCHED
		String ret = "";
		try {
			JSONObject json = OldSynchronize.makeHttpRequest(url, "POST", args);
			if (json == null) {
				return "RETURNED NULL";
			}
			ret = json.getString("result");
		} catch (JSONException e) {
			return e.getMessage();
		}
		return ret;
	}

	public void doShowQueries() {
		// GET QUERIES FROM app:
		from_lfq_queries_list.clear();
		c = null;
		c = sync_db.rawQuery("SELECT `SQL` FROM `sync_table`", null);
		String queries = "";
		int ct = 1;
		if (c.moveToFirst()) {
			do {
				queries += ct + ". " + c.getString(c.getColumnIndex("SQL"))
						+ "<br /><br />";
				ct++;
			} while (c.moveToNext());
		}
		c.close();
		sql_to_queries.setText(Html.fromHtml("<b>" + queries + "</b>"));
		// GET QUERIES FROM lfq.com:
		url = "http://www.learnfactsquick.com/lfq_app_php/synchronize_from_lfq_sqls.php";
		try {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			TelephonyManager telephonyManager = (TelephonyManager) LfqApp.getInstance()
					.getSystemService(Context.TELEPHONY_SERVICE);
			String device_id = telephonyManager.getDeviceId();
			params.add(new BasicNameValuePair("device_id", device_id));
			if (!isConnected()) {
				return;
			}
			JSONObject json = makeHttpRequest(url, "POST", params);
			if (json == null) {
				return;
			}
			System.out.println("GETTING FROM QUERIES JSON NOT NULL");
			text = "";
			text += json.getString("DEBUG");
			int count = json.getInt("Count");
			String db, id, is_image = "false", res, image, table, name, entry, old_entry, action, user;
			for (int i = 1; i <= count; i++) {
				db = json.getString("DB" + i);
				id = json.getString("ID" + i);
				res = json.getString("SQL" + i);
				image = json.getString("Image" + i);
				table = json.getString("Table" + i);
				name = json.getString("Name" + i);
				entry = json.getString("Entry" + i);
				old_entry = json.getString("Old_Entry" + i);
				action = json.getString("Action" + i);
				user = json.getString("User" + i);
				// System.out.println(db + ", " + id + ", " + is_image + ", " +
				// res + ", " + image + ", " + table + ", " + name + ", " +
				// entry + ", " + old_entry + ", " + action + ", " + user +
				// ".");
				if (json.getBoolean("Is_Image" + i) == false
						&& !db.equals("alp_db")) {
					is_image = "false";
					text += i + ". " + res + "<br /><br />";
				} else if (json.getBoolean("Is_Image" + i) == false
						&& db.equals("alp_db") && !action.equals("sql")) {
					is_image = "false";
					text += i + action.toUpperCase(Locale.US) + " from table "
							+ table + ", letter " + name + ".<br /><br />";
				} else if (json.getBoolean("Is_Image" + i) == false
						&& db.equals("alp_db") && action.equals("sql")) {
					is_image = "false";
					text += i + ". " + res + "<br /><br />";
				} else if (json.getBoolean("Is_Image" + i) != false) {
					is_image = "true";
					text += i + ". Image from table " + table + ", name "
							+ name + ".<br /><br />";
				}

				from_lfq_queries_list.add(new String[] { db, id, is_image, res,
						image, table, name, entry, old_entry, action, user });

			}
			// text += "count=" + count;
			sql_from_queries.setText(Html.fromHtml("<b>" + text + "</b>"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void doGetLFQTables(String db) {
		lfqTablesAdapter.clear();
		List<NameValuePair> args = new ArrayList<NameValuePair>();
		if (!db.equals("events") && !db.equals("miscellaneous")) {
			if (db.equals("acrostics")) {
				url = "http://www.learnfactsquick.com/lfq_app_php/lfq_app_get_acrostics_tables.php";
			}
			if (db.equals("alphabet")) {
				url = "http://www.learnfactsquick.com/lfq_app_php/lfq_app_get_alphabet_tables.php";
			}
			if (db.equals("mnemonics")) {
				url = "http://www.learnfactsquick.com/lfq_app_php/lfq_app_get_mnemonic_tables.php";
			}
			if (db.equals("users")) {
				if (logged_in == false) {
					sync_results
							.setText(Html.fromHtml("<b>NOT LOGGED IN!</b>"));
					return;
				} else {
					url = "http://www.learnfactsquick.com/lfq_app_php/lfq_app_get_users_tables.php";
					args.add(new BasicNameValuePair("username", username));
				}
			}

			try {
				if (!isConnected()) {
					return;
				}
				JSONObject json = makeHttpRequest(url, "POST", args);
				if (json == null) {
					return;
				}
				for (int i = 0; i < json.length(); i++) {
					if (!json.getString("table" + i).equals("FAILED")) {
						lfqTablesAdapter.add(json.getString("table" + i));
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (db.equals("events")) {			
				lfqTablesAdapter.add("events_table");			
		}// END IF FOR DATABASE "events"

		if (db.equals("miscellaneous")) {
			lfqTablesAdapter.add("dictionarya");
			lfqTablesAdapter.add("global_number_table");
		}
	}

	public void doGetMyTables(String db) {
		c = null;
		myTablesAdapter.clear();
		// DATABASES
		// ARE:"acrostics","alphabet","events","miscellaneous", "users"
		myTablesAdapter.clear();
		if (db.equals("acrostics")) {
			c = acr_db.rawQuery(" SELECT name FROM sqlite_master "
					+ " WHERE type='table' ORDER BY name", null);
			if (c.moveToFirst()) {
				do {
					if (!c.getString(0).equals("android_metadata")
							&& !c.getString(0).equals("sqlite_sequence")) {
						myTablesAdapter.add(c.getString(0));
					}
				} while (c.moveToNext());
			} else {
				results_from_lfq.setText("nothing found");
			}
			c.close();
		}
		if (db.equals("alphabet")) {
			c = alp_db.rawQuery(" SELECT name FROM sqlite_master "
					+ " WHERE type='table' ORDER BY name", null);
			if (c.moveToFirst()) {
				do {
					if (!c.getString(0).equals("android_metadata")
							&& !c.getString(0).equals("sqlite_sequence")) {
						myTablesAdapter.add(c.getString(0));
					}
				} while (c.moveToNext());
			} else {
				results_from_lfq.setText("nothing found");
			}
			c.close();
		}
		if (db.equals("events")) {
		 	myTablesAdapter.add("events_table");			
		}// END IF FOR DATABASE "events"
		if (db.equals("mnemonics")) {
			c = mne_db
					.rawQuery(
							"SELECT Categories FROM mnemonics WHERE Categories<>'' ORDER BY Categories",
							null);
			if (c.moveToFirst()) {
				do {
					myTablesAdapter.add(c.getString(0));
				} while (c.moveToNext());
			} else {
				results_from_lfq.setText("nothing found");
			}
			c.close();
		}
		if (db.equals("users")) {
			if (logged_in == false) {
				sync_results.setText(Html.fromHtml("<b>NOT LOGGED IN!</b>"));
			} else {
				myTablesAdapter.add(username + "_numbertable");
				myTablesAdapter.add(username + "_reviewedwords");
				myTablesAdapter.add(username + "_savednewwords");
			}
		}
		if (db.equals("miscellaneous")) {
			myTablesAdapter.add("dictionarya");
			myTablesAdapter.add("global_number_table");
		}

	}

	public static List<Boolean> updateLfqUsers(String action, String username,
			String password, String new_password, String question, String answer) {
		url = "http://www.learnfactsquick.com/lfq_app_php/update_lfq_users.php";
		List<Boolean> upd_results = new ArrayList<Boolean>();
		upd_results.add(false);// add false to is_connected
		upd_results.add(false);// add is_username_exists
		upd_results.add(false);// add is_success
		List<NameValuePair> args = new ArrayList<NameValuePair>();
		args.add(new BasicNameValuePair("action", action));
		args.add(new BasicNameValuePair("username", username));
		args.add(new BasicNameValuePair("password", password));
		if (action.equals("update_password")) {
			args.add(new BasicNameValuePair("new_password", new_password));
			args.add(new BasicNameValuePair("question", question));
			args.add(new BasicNameValuePair("answer", answer));
		}
		try {
			if (!isConnected()) {
				return upd_results;
			}
			JSONObject json = makeHttpRequest(url, "POST", args);
			if (json == null) {
				return upd_results;
			}
			upd_results.set(0, true);// set is_connected and not_null to true
			if (json.getBoolean("username_exists")) {
				upd_results.set(1, true);
				return upd_results;
			}
			if (json.getBoolean("result")) {
				upd_results.set(1, true);
				return upd_results;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return upd_results;
	}

	public static List<Integer> updateAppUsers(String username, Context context) {
		url = "http://www.learnfactsquick.com/lfq_app_php/update_app_users.php";
		List<Integer> upd_results = new ArrayList<Integer>();
		upd_results.add(0);// initialize is_connected to false
		upd_results.add(0);// initialize is_username_exists to false
		upd_results.add(0);// initialize is_updated to false
		upd_results.add(0);// initialize count_queries to 0
		List<NameValuePair> args = new ArrayList<NameValuePair>();
		args.add(new BasicNameValuePair("username", username));
		try {
			if (!isConnected()) {
				return upd_results;
			}
			JSONObject json = makeHttpRequest(url, "POST", args);
			if (json == null) {
				return upd_results;
			}
			upd_results.set(0, 1);// set is_connected and not_null to true
			if (!json.getBoolean("username_exists")) {
				return upd_results;
			}
			upd_results.set(1, 1);// set is_username_exists to true
			if (!json.getBoolean("is_updated")) {
				return upd_results;
			}
			upd_results.set(2, 1);// set is_updated to true
			int count_queries = json.getInt("Count");
			upd_results.set(3, count_queries);
			for (int i = 0; i < count_queries; i++) {
				users_db.execSQL(json.getString("Query" + i));
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return upd_results;
	}

	class doSyncTo extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			text = "Loading. Please wait...";
			sync_results.setText(Html.fromHtml("<b>" + text + "<b>"));
		}

		@Override
		protected String doInBackground(String... params) {
			c = null;
			int ct = 1;
			c = sync_db.rawQuery("SELECT * FROM `sync_table` ORDER BY _id",
					null);
			int tot_ct = c.getCount();
			publishProgress("", "<b>Syncing " + tot_ct + " updates to Lfq</b>");
			c.close();
			c = null;
			int sav_id = 0;
			text = "";
			do {
				c = sync_db.rawQuery("SELECT * FROM `sync_table` WHERE _id>'"
						+ sav_id + "' ORDER BY _id LIMIT 10", null);
				if (c.moveToLast()) {
					sav_id = c.getInt(c.getColumnIndex("_id"));
				}
				boolean is_image;				

				if (c.moveToFirst()) {

					do {
						// autoSync(String mySQL, boolean is_image,
						// byte[] image, String table, String name)
						is_image = false;
						if (c.getString(c.getColumnIndex("Is_Image")) != null) {
							if (c.getString(c.getColumnIndex("Is_Image"))
									.equals("yes")) {
								is_image = true;
							}
						}
						text += ct + ") ";		

						text += autoSync(
								c.getString(c.getColumnIndex("Username")),
								c.getString(c.getColumnIndex("Device_Id")),
								c.getString(c.getColumnIndex("SQL")), is_image,
								c.getBlob(c.getColumnIndex("Image")),
								c.getString(c.getColumnIndex("Table_name")),
								c.getString(c.getColumnIndex("Name")));

						text += "<br />";
						publishProgress(
								text,
								"Loading sync # " + ct + " of " + tot_ct
										+ " to Lfq." + " _id="
										+ c.getInt(c.getColumnIndex("_id")));
						if (autosync_result.equals("SUCCESS.")) {// DELETE
																	// FROM
																	// sync_table
							if (sync_db.delete("sync_table", "_id=?",
									new String[] { c.getString(c
											.getColumnIndex("_id")) }) != 0) {
								System.out.println("DELETED sync row.");
							} else {
								System.out.println("NOT DELETED sync row.");
							}
						}
						ct++;
					} while (c.moveToNext());
					c.close();
				}
			} while (c.getCount() > 0);
			text = "SYNC TABLE EMPTY...";

			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			sync_results.setText(Html.fromHtml(values[0]));
			sync_display_results.setText(Html.fromHtml("<b>" + values[1]
					+ "</b>"));
		}

		@Override
		protected void onPostExecute(String file_url) {
			doShowQueries();
		}

	}

	class doLoadDatabases extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			text = "Loading databases. Please wait...";
			sync_results.setText(Html.fromHtml("<b>" + text + "<b>"));
			is_database_load = true;
		}

		@Override
		protected String doInBackground(String... params) {
			text += "<br />Loading synchronize database...";
			publishProgress(text);
			sync_db = MainLfqActivity.getSyncDb();
			text += "LOADED.<br />";
			publishProgress(text);

			text += "Loading acrostics database...";
			publishProgress(text);
			acr_db = MainLfqActivity.getAcrosticsDb();
			text += "LOADED.<br />";
			publishProgress(text);

			text += "Loading alphabet database...";
			publishProgress(text);
			alp_db = MainLfqActivity.getAlphabetDb();
			text += "LOADED.<br />";
			publishProgress(text);

			text += "Loading dictionary database...";
			publishProgress(text);
			dictionary_db = MainLfqActivity.getDictionaryDb();
			text += "LOADED.<br />";
			publishProgress(text);

			text += "Loading events database...";
			publishProgress(text);
			events_db = MainLfqActivity.getEventsDb();
			text += "LOADED.<br />";
			publishProgress(text);

			text += "Loading miscellaneous database...";
			publishProgress(text);
			misc_db = MainLfqActivity.getMiscDb();
			text += "LOADED.<br />";
			publishProgress(text);

			text += "Loading mnemonics database...";
			publishProgress(text);
			mne_db = MainLfqActivity.getMneDb();
			text += "LOADED.<br />";
			publishProgress(text);

			text += "Loading newwords database...";
			publishProgress(text);
			newwords_db = MainLfqActivity.getNewwordsDb();
			text += "LOADED.<br />";
			publishProgress(text);

			text += "Loading numbers database...";
			publishProgress(text);
			numbers_db = MainLfqActivity.getNumbersDb();
			text += "LOADED.<br />";
			publishProgress(text);

			text += "Loading users database...";
			publishProgress(text);
			users_db = MainLfqActivity.getUsersDb();
			text += "LOADED.<br />";
			publishProgress(text);
			is_database_load = false;
			return null;
		}

		public void doProgress(String value) {
			publishProgress(text + " " + value);
		}

		@Override
		protected void onProgressUpdate(String... values) {
			sync_results.setText(Html.fromHtml(values[0]));
		}

		@Override
		protected void onPostExecute(String file_url) {
			sync_results.setText("");
			select_my_database = (Spinner) findViewById(R.id.select_sync_my_databases);
			myDatabasesAdapter = new ArrayAdapter<String>(LfqApp.getInstance(),
					android.R.layout.simple_spinner_item,
					new ArrayList<String>());
			myDatabasesAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			select_my_database.setAdapter(myDatabasesAdapter);
			myDatabasesAdapter.addAll(new String[] { "acrostics", "alphabet",
					"events", "users", "mnemonics", "miscellaneous" });

			select_lfq_database = (Spinner) findViewById(R.id.select_sync_lfq_databases);
			lfqDatabasesAdapter = new ArrayAdapter<String>(LfqApp.getInstance(),
					android.R.layout.simple_spinner_item,
					new ArrayList<String>());
			lfqDatabasesAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			select_lfq_database.setAdapter(lfqDatabasesAdapter);
			lfqDatabasesAdapter.addAll(new String[] { "acrostics", "alphabet",
					"events", "users", "mnemonics", "miscellaneous" });
			doShowQueries();
			check_only_databases.setChecked(sharedPref.getBoolean(
					"SYNCHRONIZE CHECK ONLY DATABASES", false));
			check_make_new_tables.setChecked(sharedPref.getBoolean(
					"SYNCHRONIZE CHECK MAKE NEW TABLES", false));
			username_input.setText(sharedPref.getString(
					"SYNCHRONIZE USERNAME INPUT", ""));
			select_my_database
					.setSelection(myDatabasesAdapter.getPosition(sharedPref
							.getString("SYNCHRONIZE SELECT MY DATABASE",
									select_my_database.getItemAtPosition(0)
											.toString())));
			select_lfq_database.setSelection(lfqDatabasesAdapter
					.getPosition(sharedPref
							.getString("SYNCHRONIZE SELECT LFQ DATABASE",
									select_lfq_database.getItemAtPosition(0)
											.toString())));
			setListeners();
			setDisplay();
		}

	}

	// s.new doLoadTable("yes", "events", "event_db", "").execute();
	class doLoadTable extends AsyncTask<String, String, String> {
		public String activity, db_str, table, is_db;

		public doLoadTable(String my_is_db, String my_activity, String my_db,
				String my_table) {
			super();
			is_db = my_is_db;
			activity = my_activity;
			db_str = my_db;
			table = my_table;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			text = "<u>BEGIN...</u><br />";
			text += "Loading database:" + myDatabase.toUpperCase(Locale.US);
			if (is_database.equals("no")) {
				text += ", table:" + table;
			}
			if (is_database.equals("yes")) {
				text += "with " + select_my_table.getCount() + " tables.";
			}
			text += "...<br />";
			if (activity.equals("sync")) {
				sync_results.setText(Html.fromHtml("<b>" + text + "</b>"));
				sync_display_results.setText(Html.fromHtml("<b>" + text
						+ "</b>"));
			}
			// if (activity.equals("events")){
			// EditEvents.setText("<b>" + "STARTING" + "</b>");
			// }

		}

		@Override
		protected String doInBackground(String... params) {
			SQLiteDatabase db = null;
			if (db_str.equals("acrostics")) {
				db = acr_db;
			}
			if (db_str.equals("alphabet")) {
				db = alp_db;
			}
			if (db_str.equals("events")) {
				db = events_db;
			}
			if (db_str.equals("mnemonics")) {
				db = mne_db;
			}
			if (db_str.equals("users")) {
				if (logged_in == true) {
					if (myTable.equals(username + "_savednewwords")
							|| myTable.equals(username + "_reviewedwords")) {
						db = newwords_db;
					}
					if (myTable.equals(username + "_numbertable")) {
						db = numbers_db;
					}

				} else {
					sync_results
							.setText(Html.fromHtml("<b>NOT LOGGED IN!</b>"));
					return null;
				}
			}
			if (db_str.equals("miscellaneous")) {
				if (table.equals("dictionarya")) {
					db = dictionary_db;
				}
				if (table.equals("global_number_table")) {
					db = numbers_db;
				}
			}
			url = "http://www.learnfactsquick.com/lfq_app_php/synchronize_from_lfq_app.php";
			List<NameValuePair> args = new ArrayList<NameValuePair>();
			args.add(new BasicNameValuePair("database", db_str));
			args.add(new BasicNameValuePair("table", table));

			if (check_make_new_tables.isChecked() && is_db.equals("no")
					&& is_show_no_my_tables == true) {
				publishProgress("TABLE DOESN'T EXIST!");
				return null;
			}
			if (check_make_new_tables.isChecked()) {
				args.add(new BasicNameValuePair("is_new_tables", "yes"));
			} else {
				args.add(new BasicNameValuePair("is_new_tables", "no"));
			}

			if (is_db.equals("no")) {// DO ONE TABLE:
				if (db == mne_db) {
					cv.clear();
					cv.put(myTable, "");
					db.update("mnemonics", cv, null, null);
				} else {
					db.execSQL("DELETE FROM " + table);
					// db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE name='" +
					// myTable
					// + "'");
				}
				text += "Loading table " + table + "...";
				publishProgress(text);
				try {
					if (!isConnected()) {
						return "";
					}
					JSONObject json = makeHttpRequest(url, "POST", args);
					if (json == null) {
						return "";
					}
					int num_records = json.getInt("Count");
					for (int i = 0; i < num_records; i++) {
						String sql_get = json.getString("sql" + i);
						if (sql_get != null && !sql_get.equals("")
								&& !sql_get.equals("null")) {
							db.execSQL(sql_get);
							// text += sql_get + "<br />";
						}
						if (db == acr_db) {
							String hasImage = json.getString("Has_Image" + i);
							cv.clear();
							cv.put("Has_Image", hasImage);
							if (hasImage.equals("yes")) {
								String imageString = json
										.getString("Image" + i);
								byte[] imageInByte = Base64.decode(imageString,
										0);
								cv.put("Image", imageInByte);
								// text+=imageString + "<br />";
							}
							// text += "name="+json.getString("Name" + i) +
							// "<br />";
							db.update(table, cv, "Name=?",
									new String[] { json.getString("Name" + i) });
						}
						publishProgress(text + " loaded " + (i + 1) + " of "
								+ num_records + "records...");
					}
					text += "DONE. Loaded " + num_records + " records.<br />";
					publishProgress(text);
					text += "ALL COMPLETE!!!    LOADED TABLE " + table + "!";
					publishProgress(text);

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
			if (is_db.equals("yes")) {// DO ALL DATABASE:
				int ct_tables = 1;
				if (db == mne_db) {
					c = db.rawQuery(
							"SELECT Categories FROM mnemonics WHERE Categories<>'' ORDER BY Categories",
							null);
					db.execSQL("DELETE FROM mnemonics");
				} else {
					c = db.rawQuery(" SELECT name FROM sqlite_master "
							+ " WHERE type='table' ORDER BY name", null);
				}

				if (c.moveToFirst()) {
					do {
						table = c.getString(0);
						args.set(1, new BasicNameValuePair("table", table));
						if (!myTable.equals("android_metadata")
								&& !c.getString(0).equals("sqlite_sequence")) {
							if (!db.equals("mnemonics")) {
								db.execSQL("DELETE FROM " + myTable);
							}
							// db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE name='"
							// + myTable + "'");
							text += ct_tables + ". Loading table " + table
									+ "...";
							publishProgress(text);
							try {
								if (!isConnected()) {
									return "";
								}
								JSONObject json = makeHttpRequest(url, "POST",
										args);
								if (json == null) {
									return "";
								}
								for (int i = 0; i < json.length(); i++) {
									String sql_get = json.getString("sql" + i);
									if (sql_get != null && !sql_get.equals("")
											&& !sql_get.equals("null")) {
										db.execSQL(sql_get);
									}
								}
								text += "DONE. Loaded " + json.length()
										+ " records.<br />";
								ct_tables++;
								publishProgress(text);
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					} while (c.moveToNext());
					text += "ALL COMPLETE!!!    LOADED " + ct_tables
							+ " TABLES!";
				}

				c.close();
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			sync_results.setText(Html.fromHtml("<b>" + values[0] + "</b>"));

		}

		@Override
		protected void onPostExecute(String file_url) {
			sync_display_results.setText(Html.fromHtml(""));
		}

	}

}