package com.lfq.learnfactsquick;

import java.util.ArrayList;
import com.lfq.learnfactsquick.Constants.cols.acrostics;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.style.BackgroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ShowAcrosticsTables extends Activity {
	private RelativeLayout top_layout;
	private LinearLayout sort_layout;
	private TextView results;
	private TextView header_info, header_acr, header_mne, header_peg;
	private Spinner select_table, see_entered;
	private CheckBox check_information, check_complete_information,
			check_incomplete_information;
	private CheckBox check_acrostics, check_complete_acrostics,
			check_incomplete_acrostics;
	private CheckBox check_mnemonics, check_complete_mnemonics,
			check_incomplete_mnemonics;
	private CheckBox check_images, check_complete_images,
			check_incomplete_images;
	private CheckBox check_peglist, check_complete_peglist,
			check_incomplete_peglist;
	private CheckBox check_read_acr, check_show_all_categories;
	private Button make_list, backup;
	private TableLayout table;
	
	private Cursor c = null;
	private ArrayAdapter<String> tablesAdapter, seeEnteredAdapter, catsAdapter,
			typesAdapter;

	private android.widget.TableRow.LayoutParams rowParams;
	private String text;

	private CheckBox check_one_type, check_sort_cat;
	private Spinner select_cat, select_cat2, select_type;
	private String selected_table;
	private List<String> totcat;
	private Boolean hasImg, hasMne, hasPeg, hasColImg;
	private String tabtext;
	private HashMap<Integer, Integer> info_sentence_map;
	private HashMap<Integer, Boolean> info_sentence_isread;
	private HashMap<Integer, List<Integer>> info_period_arrays;
	private List<Integer> period_array;
	private Activity this_act;
	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;
	private static Boolean is_database_load;

	@SuppressLint("UseSparseArrays")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		is_database_load = false;
		this_act = this;
		sharedPref = getSharedPreferences(
				getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		editor = sharedPref.edit();
		text = "";
		selected_table = "";
		tabtext = "";
		info_sentence_map = new HashMap<Integer, Integer>();
		info_sentence_isread = new HashMap<Integer, Boolean>();
		info_period_arrays = new HashMap<Integer, List<Integer>>();
		totcat = new ArrayList<String>();
		period_array = new ArrayList<Integer>();
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

		editor.putBoolean("ACROSTICS TABLES CHECK INFORMATION",
				check_information.isChecked());
		editor.putBoolean("ACROSTICS TABLES CHECK COMPLETE INFORMATION",
				check_complete_information.isChecked());
		editor.putBoolean("ACROSTICS TABLES CHECK INCOMPLETE INFORMATION",
				check_incomplete_information.isChecked());
		editor.putBoolean("ACROSTICS TABLES CHECK ACROSTIC",
				check_acrostics.isChecked());
		editor.putBoolean("ACROSTICS TABLES CHECK COMPLETE ACROSTIC",
				check_complete_acrostics.isChecked());
		editor.putBoolean("ACROSTICS TABLES CHECK INCOMPLETE ACROSTIC",
				check_incomplete_acrostics.isChecked());
		editor.putBoolean("ACROSTICS TABLES CHECK MNEMONICS",
				check_mnemonics.isChecked());
		editor.putBoolean("ACROSTICS TABLES CHECK COMPLETE MNEMONICS",
				check_complete_mnemonics.isChecked());
		editor.putBoolean("ACROSTICS TABLES CHECK INCOMPLETE MNEMONICS",
				check_incomplete_mnemonics.isChecked());
		editor.putBoolean("ACROSTICS TABLES CHECK IMAGES",
				check_images.isChecked());
		editor.putBoolean("ACROSTICS TABLES CHECK COMPLETE IMAGES",
				check_complete_images.isChecked());
		editor.putBoolean("ACROSTICS TABLES CHECK INCOMPLETE IMAGES",
				check_incomplete_images.isChecked());
		editor.putBoolean("ACROSTICS TABLES CHECK PEGLIST",
				check_peglist.isChecked());
		editor.putBoolean("ACROSTICS TABLES CHECK COMPLETE PEGLIST",
				check_complete_peglist.isChecked());
		editor.putBoolean("ACROSTICS TABLES CHECK INCOMPLETE PEGLIST",
				check_incomplete_peglist.isChecked());

		editor.putBoolean("ACROSTICS TABLES CHECK READ",
				check_read_acr.isChecked());
		editor.putBoolean("ACROSTICS TABLES CHECK SHOW ALL CATEGORIES",
				check_show_all_categories.isChecked());
		editor.putBoolean("ACROSTICS TABLES CHECK ONE TYPE",
				check_one_type.isChecked());
		editor.putBoolean("ACROSTICS TABLES CHECK SORT CATEGORIES",
				check_sort_cat.isChecked());

		editor.putString("ACROSTICS TABLES SELECT TABLE", select_table
				.getSelectedItem().toString());
		editor.putString("ACROSTICS TABLES SELECT SEE ENTERED", see_entered
				.getSelectedItem().toString());

		editor.commit();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		saveChanges();
		super.onConfigurationChanged(newConfig);
	}

	public void setViews() {
		// LAYOUTS:
		setTitle("ACROSTIC TABLES!");
		top_layout = (RelativeLayout) findViewById(R.id.acr_tables_top_layout);
		table = (TableLayout) findViewById(R.id.acrostics_table);
		sort_layout = (LinearLayout) findViewById(R.id.show_acr_sort_layout);

		// BUTTONS:
		make_list = (Button) findViewById(R.id.make_list);
		backup = (Button) findViewById(R.id.acr_tables_backup);
		backup.setVisibility(View.GONE);

		// CHECKBOXES:
		check_information = (CheckBox) findViewById(R.id.check_information);
		check_information.setChecked(true);
		check_complete_information = (CheckBox) findViewById(R.id.check_complete_information);
		check_incomplete_information = (CheckBox) findViewById(R.id.check_incomplete_information);
		check_acrostics = (CheckBox) findViewById(R.id.check_acrostics);
		check_acrostics.setChecked(true);
		check_complete_acrostics = (CheckBox) findViewById(R.id.check_complete_acrostics);
		check_incomplete_acrostics = (CheckBox) findViewById(R.id.check_incomplete_acrostics);
		check_mnemonics = (CheckBox) findViewById(R.id.check_mnemonics);
		check_complete_mnemonics = (CheckBox) findViewById(R.id.check_complete_mnemonics);
		check_incomplete_mnemonics = (CheckBox) findViewById(R.id.check_incomplete_mnemonics);
		check_images = (CheckBox) findViewById(R.id.check_images);
		check_images.setChecked(true);
		check_complete_images = (CheckBox) findViewById(R.id.check_complete_images);
		check_incomplete_images = (CheckBox) findViewById(R.id.check_incomplete_images);
		check_peglist = (CheckBox) findViewById(R.id.check_peglist);
		check_complete_peglist = (CheckBox) findViewById(R.id.check_complete_peglist);
		check_incomplete_peglist = (CheckBox) findViewById(R.id.check_incomplete_peglist);
		check_read_acr = (CheckBox) findViewById(R.id.check_read_acr);
		check_show_all_categories = (CheckBox) findViewById(R.id.check_show_all_categories);
		check_one_type = (CheckBox) findViewById(R.id.check_one_type);
		check_sort_cat = (CheckBox) findViewById(R.id.check_sort_cat);

		// SCROLLVIEWS:

		// SPINNERS:
		select_table = (Spinner) findViewById(R.id.select_table);
		tablesAdapter = new ArrayAdapter<String>(this_act,
				android.R.layout.simple_spinner_item, new ArrayList<String>());
		tablesAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		select_table.setAdapter(tablesAdapter);
		see_entered = (Spinner) findViewById(R.id.see_entered);
		seeEnteredAdapter = new ArrayAdapter<String>(this_act,
				android.R.layout.simple_spinner_item, new ArrayList<String>());
		seeEnteredAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		see_entered.setAdapter(seeEnteredAdapter);
		catsAdapter = new ArrayAdapter<String>(this_act,
				android.R.layout.simple_spinner_item, new ArrayList<String>());
		catsAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		typesAdapter = new ArrayAdapter<String>(this_act,
				android.R.layout.simple_spinner_item, new ArrayList<String>());
		typesAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		select_cat = (Spinner) findViewById(R.id.select_cat);
		select_cat.setAdapter(catsAdapter);
		select_cat2 = (Spinner) findViewById(R.id.select_cat2);
		select_cat2.setAdapter(catsAdapter);
		select_type = (Spinner) findViewById(R.id.select_type);
		select_type.setAdapter(typesAdapter);

		// TEXTVIEWS:
		results = (TextView) findViewById(R.id.acrostics_results);
		header_info = (TextView) findViewById(R.id.show_acr_table_header_info);
		header_acr = (TextView) findViewById(R.id.show_acr_table_header_acr);
		header_mne = (TextView) findViewById(R.id.show_acr_table_header_mne);
		header_peg = (TextView) findViewById(R.id.show_acr_table_header_peg);

		check_information.setText(Html.fromHtml("<b><u>Information?</u></b>"));
		check_acrostics.setText(Html.fromHtml("<b><u>Acrostics?</u></b>"));
		check_mnemonics.setText(Html.fromHtml("<b><u>Mnemonics?</u></b>"));
		check_images.setText(Html.fromHtml("<b><u>Images?</u></b>"));
		check_peglist.setText(Html.fromHtml("<b><u>Peglist?</u></b>"));

	}

	public void loadButtons() {
		backup.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
		make_list.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
	}

	public void setListeners() {
		backup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				backup.setVisibility(View.GONE);
				top_layout.setVisibility(View.VISIBLE);
			}
		});

		select_table.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				catsAdapter.clear();
				typesAdapter.clear();
				check_one_type.setChecked(false);
				check_sort_cat.setChecked(false);
				selected_table = select_table.getSelectedItem().toString();
				c = MainLfqActivity.getAcrosticsDb().rawQuery("SELECT * FROM " + selected_table, null);
				String cols[] = c.getColumnNames();
				List<String> sort_cats = new ArrayList<String>();
				for (int i = 0; i < cols.length; i++) {
					if (!cols[i].equals("_id") && !cols[i].equals(acrostics.Name)
							&& !cols[i].equals(acrostics.Information)
							&& !cols[i].equals(acrostics.Acrostics)
							&& !cols[i].equals(acrostics.Mnemonics)
							&& !cols[i].equals(acrostics.Image)
							&& !cols[i].equals(acrostics.Has_Image)
							&& !cols[i].equals(acrostics.Peglist)) {
						sort_cats.add(cols[i]);
					}
				}
				c.close();
				if (sort_cats.size() == 0) {
					sort_layout.setVisibility(View.GONE);
					return;
				}
				sort_layout.setVisibility(View.VISIBLE);
				// ADD CHECK SHOW ONLY TYPE OF CATEGORY:
				catsAdapter.addAll(sort_cats);

				check_one_type.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (check_one_type.isChecked()) {
							select_type.setVisibility(View.VISIBLE);
						} else {
							select_type.setVisibility(View.GONE);
						}
					}
				});

				select_cat
						.setOnItemSelectedListener(new OnItemSelectedListener() {
							@Override
							public void onItemSelected(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								String cat = select_cat.getSelectedItem()
										.toString();
								c = MainLfqActivity.getAcrosticsDb().rawQuery("SELECT DISTINCT " + cat
										+ " FROM " + selected_table
										+ " ORDER BY " + cat, null);
								typesAdapter.clear();
								if (c.moveToFirst()) {
									do {
										if (c.getString(0) != null) {
											if (!c.getString(0).equals("")) {
												typesAdapter.add(c.getString(0));
											}
										}
									} while (c.moveToNext());
									if (check_one_type.isChecked()) {
										select_type.setVisibility(View.VISIBLE);
									}
								}
							}

							@Override
							public void onNothingSelected(AdapterView<?> arg0) {
							}
						});
				select_type.setVisibility(View.GONE);

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		make_list.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// SET VISIBILITIES:
				hasMne = false;
				hasPeg = false;
				hasImg = true;
				hasColImg = false;
				Cursor c_check = MainLfqActivity.getAcrosticsDb().rawQuery("SELECT * FROM "
						+ selected_table + " LIMIT 1", null);
				String[] cols = c_check.getColumnNames();
				c_check.close();
				if (check_information.isChecked()) {
					header_info.setVisibility(View.VISIBLE);
				} else {
					header_info.setVisibility(View.GONE);
				}
				if (check_acrostics.isChecked()) {
					header_acr.setVisibility(View.VISIBLE);
				} else {
					header_acr.setVisibility(View.GONE);
				}
				if (Helpers.contains(cols, acrostics.Mnemonics)
						&& check_mnemonics.isChecked()) {
					header_mne.setVisibility(View.VISIBLE);
					hasMne = true;
				} else {
					header_mne.setVisibility(View.GONE);
				}
				if (Helpers.contains(cols, acrostics.Peglist)
						&& check_peglist.isChecked()) {
					header_peg.setVisibility(View.VISIBLE);
					hasPeg = true;
				} else {
					header_peg.setVisibility(View.GONE);
				}
				if (Helpers.contains(cols, acrostics.Image)) {
					hasImg = true;
				}

				new doLoadTable().execute();

			}// end onclick

		});// end make major listener

		check_information.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!check_information.isChecked()) {
					check_complete_information.setChecked(false);
					check_incomplete_information.setChecked(false);
				}
			}
		});
		check_acrostics.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!check_acrostics.isChecked()) {
					check_complete_acrostics.setChecked(false);
					check_incomplete_acrostics.setChecked(false);
				}
			}
		});
		check_mnemonics.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!check_mnemonics.isChecked()) {
					check_complete_mnemonics.setChecked(false);
					check_incomplete_mnemonics.setChecked(false);
				}
			}
		});
		check_images.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!check_images.isChecked()) {
					check_complete_images.setChecked(false);
					check_incomplete_images.setChecked(false);
				}
			}
		});
		check_peglist.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!check_peglist.isChecked()) {
					check_complete_peglist.setChecked(false);
					check_incomplete_peglist.setChecked(false);
				}
			}
		});

		// SET COMPLETE/INCOMPLETE CHECKBOX LISTENERS
		check_complete_information
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (check_complete_information.isChecked()) {
							check_information.setChecked(true);
						}
					}
				});
		check_incomplete_information
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (check_incomplete_information.isChecked()) {
							check_information.setChecked(true);
						}
					}
				});

		check_complete_acrostics.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (check_complete_acrostics.isChecked()) {
					check_acrostics.setChecked(true);
				}
			}
		});
		check_incomplete_acrostics
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (check_incomplete_acrostics.isChecked()) {
							check_acrostics.setChecked(true);
						}
					}
				});

		check_complete_mnemonics.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (check_complete_mnemonics.isChecked()) {
					check_mnemonics.setChecked(true);
				}
			}
		});
		check_incomplete_mnemonics
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (check_incomplete_mnemonics.isChecked()) {
							check_mnemonics.setChecked(true);
						}
					}
				});

		check_complete_images.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (check_complete_images.isChecked()) {
					check_images.setChecked(true);
				}
			}
		});
		check_incomplete_images.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (check_incomplete_images.isChecked()) {
					check_images.setChecked(true);
				}
			}
		});

		check_complete_peglist.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (check_complete_peglist.isChecked()) {
					check_peglist.setChecked(true);
				}
			}
		});
		check_incomplete_peglist.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (check_incomplete_peglist.isChecked()) {
					check_peglist.setChecked(true);
				}
			}
		});
	}// end set listeners

	public void loadSpinners() {
		Cursor cursor = MainLfqActivity.getAcrosticsDb().rawQuery(" SELECT name FROM sqlite_master "
				+ " WHERE type='table' ORDER BY name", null);
		tablesAdapter.clear();
		seeEnteredAdapter.clear();
		int ct_tables = 0;
		int ct_records = 0;
		int ct_acrs = 0;
		Vector<String> seeEntered = new Vector<String>();
		Cursor c_recs, c_acrs;
		if (cursor != null) {
			cursor.moveToFirst();
			do {
				if (cursor.getString(0).equals("android_metadata")
						|| cursor.getString(0).equals("sqlite_sequence")) {
					continue;
				}
				tablesAdapter.add(cursor.getString(0));
				c_recs = MainLfqActivity.getAcrosticsDb().rawQuery(
						"SELECT COUNT(*) FROM " + cursor.getString(0), null);
				c_recs.moveToFirst();
				c_acrs = MainLfqActivity.getAcrosticsDb().rawQuery(
						"SELECT COUNT(*) FROM " + cursor.getString(0)
								+ " WHERE " + acrostics.Acrostics + "<>''", null);
				c_acrs.moveToFirst();
				seeEntered.add(c_recs.getString(0) + "-->"
						+ cursor.getString(0));
				ct_records += c_recs.getInt(0);
				ct_acrs += c_acrs.getInt(0);
				ct_tables++;
				c_recs.close();
				c_acrs.close();
			} while (cursor.moveToNext());
			cursor.close();
			seeEnteredAdapter.add(ct_acrs + "-->ACROSTICS ENTERED");
			seeEnteredAdapter.add(ct_records + "-->TOTAL ENTERED");
			seeEnteredAdapter.add(ct_tables + "-->TABLES");
			for (int i = 0; i < seeEntered.size(); i++) {
				seeEnteredAdapter.add(seeEntered.get(i));
			}

		} else {
			results.setText("nothing found");
		}

	}

	public String join(String[] spl_str, String delimiter) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < spl_str.length; i++) {
			if (i != spl_str.length - 1) {
				sb.append(spl_str[i]).append(delimiter).toString();
			} else {
				sb.append(spl_str[i]).toString();
			}
		}
		return sb.toString();
	}

	class doLoadDatabases extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setContentView(R.layout.show_acrostics_tables);
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
			loadSpinners();
			results.setText("");
			check_information.setChecked(sharedPref.getBoolean(
					"ACROSTICS TABLES CHECK INFORMATION", true));
			check_complete_information.setChecked(sharedPref.getBoolean(
					"ACROSTICS TABLES CHECK COMPLETE INFORMATION", false));
			check_incomplete_information.setChecked(sharedPref.getBoolean(
					"ACROSTICS TABLES CHECK INCOMPLETE INFORMATION", false));
			check_acrostics.setChecked(sharedPref.getBoolean(
					"ACROSTICS TABLES CHECK ACROSTIC", true));
			check_complete_acrostics.setChecked(sharedPref.getBoolean(
					"ACROSTICS TABLES CHECK COMPLETE ACROSTIC", false));
			check_incomplete_acrostics.setChecked(sharedPref.getBoolean(
					"ACROSTICS TABLES CHECK INCOMPLETE ACROSTIC", false));
			check_mnemonics.setChecked(sharedPref.getBoolean(
					"ACROSTICS TABLES CHECK MNEMONICS", false));
			check_complete_mnemonics.setChecked(sharedPref.getBoolean(
					"ACROSTICS TABLES CHECK COMPLETE MNEMONICS", false));
			check_incomplete_mnemonics.setChecked(sharedPref.getBoolean(
					"ACROSTICS TABLES CHECK INCOMPLETE MNEMONICS", false));
			check_images.setChecked(sharedPref.getBoolean(
					"ACROSTICS TABLES CHECK IMAGES", true));
			check_complete_images.setChecked(sharedPref.getBoolean(
					"ACROSTICS TABLES CHECK COMPLETE IMAGES", false));
			check_incomplete_images.setChecked(sharedPref.getBoolean(
					"ACROSTICS TABLES CHECK INCOMPLETE IMAGES", false));
			check_peglist.setChecked(sharedPref.getBoolean(
					"ACROSTICS TABLES CHECK PEGLIST", false));
			check_complete_peglist.setChecked(sharedPref.getBoolean(
					"ACROSTICS TABLES CHECK COMPLETE PEGLIST", false));
			check_incomplete_peglist.setChecked(sharedPref.getBoolean(
					"ACROSTICS TABLES CHECK INCOMPLETE PEGLIST", false));

			check_read_acr.setChecked(sharedPref.getBoolean(
					"ACROSTICS TABLES CHECK READ", false));
			check_show_all_categories.setChecked(sharedPref.getBoolean(
					"ACROSTICS TABLES CHECK SHOW ALL CATEGORIES", false));
			check_one_type.setChecked(sharedPref.getBoolean(
					"ACROSTICS TABLES CHECK ONE TYPE", false));
			check_sort_cat.setChecked(sharedPref.getBoolean(
					"ACROSTICS TABLES CHECK SORT CATEGORIES", false));

			select_table.setSelection(tablesAdapter.getPosition(sharedPref
					.getString("ACROSTICS TABLES SELECT TABLE", select_table
							.getItemAtPosition(0).toString())));
			see_entered.setSelection(seeEnteredAdapter.getPosition(sharedPref
					.getString("ACROSTICS TABLES SELECT SEE ENTERED",
							see_entered.getItemAtPosition(0).toString())));
			setListeners();

		}

	}// END doLoadDatabases

	class doLoadTable extends AsyncTask<String, TableRow, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			table.removeAllViews();
			top_layout.setVisibility(View.GONE);
			backup.setVisibility(View.VISIBLE);
			results.setText(Html.fromHtml("<b>Loading " + selected_table
					+ ".<b>"));
			info_sentence_map.clear();
			info_sentence_isread.clear();
			info_period_arrays.clear();
			period_array.clear();

		}

		@SuppressWarnings("resource")
		@Override
		protected String doInBackground(String... params) {
			selected_table = select_table.getSelectedItem().toString();

			// QUERY CONDITIONS:
			String category = "Name";
			Cursor c_sel = null;
			tabtext = "";
			String orderby = "Name COLLATE NOCASE";

			List<String> col_list = new ArrayList<String>();
			c = MainLfqActivity.getAcrosticsDb().rawQuery("SELECT * FROM " + selected_table + " LIMIT 1",
					null);
			String[] col_names = c.getColumnNames();
			c.close();
			for (int i = 0; i < col_names.length; i++) {
				if (!col_names[i].equals("android_metadata")
						&& !col_names[i].equals("sqlite_sequence")
						&& !col_names[i].equals(acrostics.Image)) {
					col_list.add(col_names[i]);
				}
			}
			String cols_str = Helpers.joinList(col_list, ",");
			if (check_sort_cat.isChecked()) {
				category = select_cat2.getSelectedItem().toString();
				orderby = category + ",Name COLLATE NOCASE";
			}

			if (hasMne || hasPeg) {
				c_sel = MainLfqActivity.getAcrosticsDb().rawQuery("SELECT " + cols_str + " FROM "
						+ selected_table, null);
			}
			if (!(hasMne || hasPeg)) {
				if (!check_one_type.isChecked()) {
					c_sel = MainLfqActivity.getAcrosticsDb().rawQuery("SELECT " + cols_str + " FROM "
							+ selected_table + " ORDER BY " + orderby, null);
				} else {
					c_sel = MainLfqActivity.getAcrosticsDb().rawQuery("SELECT " + cols_str + " FROM "
							+ selected_table + " WHERE "
							+ select_cat.getSelectedItem().toString() + "='"
							+ select_type.getSelectedItem().toString()
							+ "' ORDER BY " + orderby, null);
				}

			}

			totcat.clear();
			if (check_show_all_categories.isChecked()) {
				for (int i = 0; i < col_list.size(); i++) {
					if (!col_list.get(i).equals("_id")
							&& !col_list.get(i).equals(acrostics.Name)
							&& !col_list.get(i).equals(acrostics.Information)
							&& !col_list.get(i).equals(acrostics.Acrostics)
							&& !col_list.get(i).equals(acrostics.Mnemonics)
							&& !col_list.get(i).equals(acrostics.Peglist)
							&& !col_list.get(i).equals(acrostics.Has_Image)) {
						totcat.add(col_list.get(i));
					}
				}
			}
			int ct = 0;
			String mnevar;
			boolean is_found_image = false;
			Cursor c_get_img = null;
			if (c_sel.moveToFirst()) {
				do {

					mnevar = "";
					String name = c_sel.getString(c_sel.getColumnIndex(acrostics.Name));
					if (hasMne) {
						mnevar = c_sel.getString(c_sel
								.getColumnIndex(acrostics.Mnemonics));
					}
					if (check_images.isChecked()) {
						// GET IMAGE:
						is_found_image = false;
						name = name.replaceAll("'", "`");
						c_get_img = MainLfqActivity.getAcrosticsDb().rawQuery(
								"SELECT " + acrostics.Image + " FROM " + selected_table
										+ " WHERE " + acrostics.Name + "='" + name + "'", null);
						try {
							if (c_get_img.moveToFirst()) {
								if (c_get_img.getBlob(0) != null) {
									if (c_get_img.getBlob(0).length > 0) {
										is_found_image = true;
									}
								}
							}
						} catch (Exception e) {
							System.out.println(e.getMessage());
						}

					}

					Boolean isComplete = !(check_complete_information
							.isChecked() && c_sel.getString(
							c_sel.getColumnIndex(acrostics.Information)).equals(""))
							&& !(check_complete_acrostics.isChecked() && c_sel
									.getString(
											c_sel.getColumnIndex(acrostics.Acrostics))
									.equals(""))
							&& !(check_complete_mnemonics.isChecked() && mnevar
									.equals(""))
							&& !(check_complete_images.isChecked() && is_found_image == false)
							&& !(check_complete_peglist.isChecked() && !c_sel
									.getString(c_sel.getColumnIndex(acrostics.Peglist))
									.equals(""));
					Boolean isIncomplete = !(check_incomplete_information
							.isChecked() && !c_sel.getString(
							c_sel.getColumnIndex(acrostics.Information)).equals(""))
							&& !(check_incomplete_acrostics.isChecked() && !c_sel
									.getString(
											c_sel.getColumnIndex(acrostics.Acrostics))
									.equals(""))
							&& !(check_incomplete_mnemonics.isChecked() && !mnevar
									.equals(""))
							&& !(check_incomplete_images.isChecked() && is_found_image == true)
							&& !(check_incomplete_peglist.isChecked() && !c_sel
									.getString(c_sel.getColumnIndex(acrostics.Peglist))
									.equals(""));
					if (isComplete && isIncomplete) {

						ct++;
						TableRow newRow = new TableRow(this_act);

						TextView firColCell = new TextView(this_act);// NAME
																		// CELL
						String[] aliased_name_spl = name.split(";");
						String aliased_name = join(aliased_name_spl, "\nOR... ");
						if (hasImg) {
						}
						if (hasColImg) {
						}

						String result = "<b>" + ct + ".<u>" + aliased_name
								+ "</u></b>";
						if (check_show_all_categories.isChecked()) {

							for (int i = 0; i < totcat.size(); i++) {
								result += "<br /><b>"
										+ totcat.get(i).toString() + "</b>: ";
								if (c_sel.getString(c_sel.getColumnIndex(totcat
										.get(i))) != null) {
									result += c_sel.getString(c_sel
											.getColumnIndex(totcat.get(i)));
								}
							}
						}
						if (!check_show_all_categories.isChecked()) {
							if (!category.equals(acrostics.Name)) {
								result += "<br /><b><u>" + category
										+ ": </u></b>";
								if (c_sel.getString(c_sel
										.getColumnIndex(category)) != null) {
									result += "<b>"
											+ c_sel.getString(c_sel
													.getColumnIndex(category))
											+ "</b>";
								}
							}

						}

						firColCell.setText(Html.fromHtml(result));
						LinearLayout wrap_first = new LinearLayout(this_act);
						rowParams = new TableRow.LayoutParams(200,
								TableRow.LayoutParams.MATCH_PARENT);
						wrap_first.setLayoutParams(rowParams);
						wrap_first
								.setBackgroundResource(R.drawable.header_shape);
						wrap_first.setOrientation(LinearLayout.VERTICAL);
						wrap_first.setVisibility(View.VISIBLE);
						wrap_first.addView(firColCell);
						if (check_images.isChecked()) {
							ImageView img = new ImageView(this_act);
							if (is_found_image == false) {
								img.setImageResource(0);
							} else {
								byte[] imageByteArray = c_get_img.getBlob(0);
								Bitmap bitMapImage = BitmapFactory
										.decodeByteArray(imageByteArray, 0,
												imageByteArray.length);
								img.setImageBitmap(bitMapImage);
								c_get_img.close();
								rowParams = new TableRow.LayoutParams(200, 150);
								img.setLayoutParams(rowParams);
								wrap_first.addView(img);
							}
						}
						newRow.addView(wrap_first);

						if (check_information.isChecked()) {
							LinearLayout wrap_inf = new LinearLayout(this_act);
							rowParams = new TableRow.LayoutParams(300,
									TableRow.LayoutParams.MATCH_PARENT);
							wrap_inf.setLayoutParams(rowParams);
							wrap_inf.setOrientation(LinearLayout.VERTICAL);
							wrap_inf.setBackgroundResource(R.drawable.header_shape);
							final TextView infoColTex = new TextView(this_act);
							infoColTex.setText(c_sel.getString(c_sel
									.getColumnIndex(acrostics.Information)));
							if (!c_sel.getString(
									c_sel.getColumnIndex(acrostics.Information))
									.equals("")) {
								if (check_read_acr.isChecked()) {
									Button readInfoButton = new Button(
											this_act, null,
											android.R.attr.buttonStyleSmall);
									rowParams = new TableRow.LayoutParams(300,
											50);
									info_sentence_isread.put(ct, false);
									final int info_index = ct;
									readInfoButton.setLayoutParams(rowParams);
									readInfoButton
											.setOnClickListener(new View.OnClickListener() {

												@Override
												public void onClick(View arg0) {
													String text_now = infoColTex
															.getText()
															.toString();
													if (info_sentence_isread
															.get(info_index) == false) {
														String dummy_text = text_now;
														period_array.clear();
														int strind = 0;
														while (dummy_text
																.indexOf(".") != -1) {
															int strlen = dummy_text
																	.indexOf(".") + 1;
															strind += strlen;
															period_array
																	.add(strind);
															dummy_text = dummy_text
																	.substring(strlen);
															System.out
																	.println("dummy_text="
																			+ dummy_text);
														}
														info_period_arrays.put(
																info_index,
																period_array);
														info_sentence_map.put(
																info_index, 0);
														int first_period_index = text_now
																.indexOf(".");
														if (first_period_index == -1) {
															return;
														}
														Spannable spantext = Spannable.Factory
																.getInstance()
																.newSpannable(
																		text_now);
														spantext.setSpan(
																new BackgroundColorSpan(
																		Color.YELLOW),
																0,
																first_period_index,
																Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
														infoColTex
																.setText(spantext);
														info_sentence_isread
																.put(info_index,
																		true);
													} else {
														infoColTex
																.setText(text_now);
														info_sentence_isread
																.put(info_index,
																		false);
													}
												}
											});
									readInfoButton.setText("Read");
									wrap_inf.addView(readInfoButton);
									infoColTex
											.setOnClickListener(new View.OnClickListener() {
												@Override
												public void onClick(View arg0) {
													System.out
															.println("IN INFO EDIT!!");
													System.out
															.println("info_index="
																	+ info_index);
													if (info_sentence_isread
															.get(info_index) == true) {
														System.out
																.println("IN INFO EDIT (IS READ)!!");
														int senct = info_sentence_map
																.get(info_index);
														System.out
																.println("senct="
																		+ senct);
														if (senct < ((info_period_arrays
																.get(info_index))
																.size() - 1)) {
															System.out
																	.println("IN INFO EDIT (IS READ) SIZE LESS THAN!!");
															senct++;
															info_sentence_map
																	.put(info_index,
																			senct);
															String text_now = infoColTex
																	.getText()
																	.toString();
															Spannable spantext = Spannable.Factory
																	.getInstance()
																	.newSpannable(
																			text_now);
															spantext.setSpan(
																	new BackgroundColorSpan(
																			Color.YELLOW),
																	0,
																	info_period_arrays
																			.get(info_index)
																			.get(senct),
																	Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
															infoColTex
																	.setText(spantext);
														}
													}
												}
											});
								}
							}// END IF HAVE INFORMATION
							wrap_inf.addView(infoColTex);
							newRow.addView(wrap_inf);
						}
						if (check_acrostics.isChecked()
								|| (check_complete_acrostics.isChecked() && !c_sel
										.getString(
												c_sel.getColumnIndex(acrostics.Acrostics))
										.equals(""))) {
							LinearLayout wrap_acr = new LinearLayout(this_act);
							rowParams = new TableRow.LayoutParams(300,
									TableRow.LayoutParams.MATCH_PARENT);
							wrap_acr.setLayoutParams(rowParams);
							wrap_acr.setOrientation(LinearLayout.VERTICAL);
							wrap_acr.setBackgroundResource(R.drawable.header_shape);
							TextView acrTex = new TextView(this_act);
							acrTex.setText(c_sel.getString(c_sel
									.getColumnIndex(acrostics.Acrostics)));
							wrap_acr.addView(acrTex);
							newRow.addView(wrap_acr);
						}

						if (hasMne) {
							if (check_mnemonics.isChecked()
									&& !c_sel.getString(
											c_sel.getColumnIndex(acrostics.Mnemonics))
											.equals("")) {
								LinearLayout wrap_mne = new LinearLayout(
										this_act);
								rowParams = new TableRow.LayoutParams(300,
										TableRow.LayoutParams.MATCH_PARENT);
								wrap_mne.setLayoutParams(rowParams);
								wrap_mne.setBackgroundResource(R.drawable.header_shape);
								wrap_mne.setOrientation(LinearLayout.VERTICAL);
								TextView mneTex = new TextView(this_act);
								mneTex.setText(c_sel.getString(c_sel
										.getColumnIndex(acrostics.Mnemonics)));
								wrap_mne.addView(mneTex);
								newRow.addView(wrap_mne);
							}
						}
						if (hasPeg) {
							if (check_mnemonics.isChecked()
									&& !c_sel.getString(
											c_sel.getColumnIndex(acrostics.Peglist))
											.equals("")) {
								LinearLayout wrap_peg = new LinearLayout(
										this_act);
								rowParams = new TableRow.LayoutParams(300,
										TableRow.LayoutParams.MATCH_PARENT);
								wrap_peg.setLayoutParams(rowParams);
								wrap_peg.setOrientation(LinearLayout.VERTICAL);
								wrap_peg.setBackgroundResource(R.drawable.header_shape);
								TextView pegTex = new TextView(this_act);
								pegTex.setText(c_sel.getString(c_sel
										.getColumnIndex(acrostics.Peglist)));
								wrap_peg.addView(pegTex);
								newRow.addView(wrap_peg);
							}
						}

						tabtext = "LISTED ARE " + ct + " "
								+ selected_table.toUpperCase(Locale.US) + "`S:";
						publishProgress(newRow);
					}// if information exists...

				} while (c_sel.moveToNext());
			}
			c_sel.close();

			return null;
		}

		@Override
		protected void onProgressUpdate(TableRow... values) {
			table.addView(values[0]);
			results.setText(Html.fromHtml(tabtext));
		}

		@Override
		protected void onPostExecute(String file_url) {
		}
	}

}