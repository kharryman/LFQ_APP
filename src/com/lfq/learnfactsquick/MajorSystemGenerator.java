package com.lfq.learnfactsquick;

import java.util.Locale;

import android.annotation.SuppressLint;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MajorSystemGenerator extends Activity {
	private TextView results, load_results;
	private EditText major_input;
	private CheckBox check_use_major_letters, check_all_major_letters;
	private Button make_major;
	
	private String input, text;
	private String ign_lets_str = "aeiouwxy";
	private String dbl_lets = "cgpst";
	private Boolean done;
	private String[] inpspl;
	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;
	private static Boolean is_database_load;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		is_database_load = false;
		sharedPref = getSharedPreferences(
				getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		editor = sharedPref.edit();
		done = false;
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
		editor.putString("MAJOR GENERATOR INPUT", major_input.getText()
				.toString());
		editor.putBoolean("MAJOR GENERATOR CHECK ALL",
				check_all_major_letters.isChecked());
		editor.putBoolean("MAJOR GENERATOR CHECK USE LETTERS",
				check_use_major_letters.isChecked());
		editor.commit();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		saveChanges();
		super.onConfigurationChanged(newConfig);
	}

	public void setViews() {
		setTitle("USE MAJOR SYSTEM!");
		major_input = (EditText) findViewById(R.id.major_input);

		make_major = (Button) findViewById(R.id.make_major);

		check_all_major_letters = (CheckBox) findViewById(R.id.check_all_major_letters);
		check_use_major_letters = (CheckBox) findViewById(R.id.check_use_major_letters);
		results = (TextView) findViewById(R.id.show_major_results);
		load_results = (TextView) findViewById(R.id.major_load_results);
	}

	public void loadButtons() {
		make_major.setBackgroundResource(sharedPref.getInt("BG Button",
				R.drawable.button));
	}

	public void setListeners() {
		make_major.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				new doLoadMajorWords().execute();
			}// end onclick
		});// end make major listener

	}// end set listeners

	class doLoadDatabases extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setContentView(R.layout.show_major);
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
			results.setText("");
			setListeners();
			major_input.setText(sharedPref.getString("MAJOR GENERATOR INPUT",
					""));
			check_all_major_letters.setChecked(sharedPref.getBoolean(
					"MAJOR GENERATOR CHECK ALL", false));
			check_use_major_letters.setChecked(sharedPref.getBoolean(
					"MAJOR GENERATOR CHECK USE LETTERS", false));
		}

	}

	class doLoadMajorWords extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			load_results.setText(Html
					.fromHtml("<b>Loading. Please wait...</b>"));
			results.setText("");
		}

		@SuppressLint("DefaultLocale")
		@Override
		protected String doInBackground(String... params) {
			input = major_input.getText().toString();
			done = false;
			boolean search_success = false;
			text = "According to the Stanislaus Mink von Wennsshein, of 17th Century, the Major Sytem is<br />";
			text += "0=s,z 1=d,t,th 2=n 3=m 4=r 5=l 6=ch,j,g,sh 7=c,gg,k,q 8=f,ph,v 9=b,p<br /><br />";
			String alp = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
			String num_str = "0123456789";
			int inpct = input.length();
			int numct = 0, ltct = 0;
			int size = 0;
			inpspl = Helpers.explode(input);
			String beglets, endnums, wriwor, num;
			int ct = 0, ct2 = 0;
			if (check_use_major_letters.isChecked()) {
				for (int i = 0; i < inpct; i++) {
					if (!alp.contains(inpspl[i])
							&& !num_str.contains(inpspl[i])) {
						publishProgress(
								"Must enter only alphanumeric letters.",
								"NOTHING LOADED");
						return null;
					}
				}
			}
			if (!check_use_major_letters.isChecked()) {
				for (int i = 0; i < inpct; i++) {
					if (!num_str.contains(inpspl[i])) {
						publishProgress("Must enter only numbers.",
								"NOTHING LOADED");
						return null;
					}
				}
			}
			if (check_use_major_letters.isChecked() == false) {// OPTION: uses
																// only numbers
				text += input.toString() + "<br /><br />";
				Cursor c1 = MainLfqActivity.getDictionaryDb().rawQuery(
						"SELECT * FROM dictionarya WHERE Number LIKE '" + input
								+ "%'", null);
				String[] worspl;
				done = false;
				numct = inpct;
				if (c1.moveToFirst()) {
					search_success = true;
					text = "NUMBERS TO WORDS:<br />";
					do {
						worspl = Helpers.explode(c1.getString(c1
								.getColumnIndex("Word")));
						wriwor = formatWord("", worspl, 0, numct);
						size = c1.getString(c1.getColumnIndex("Definition"))
								.length();
						ct++;
						text += ct + ") " + wriwor + " ";
						if (size != 0) {
							text += c1.getString(c1
									.getColumnIndex("Definition"));
						}
						text += "<br />";
						publishProgress(text, "Loading.. " + ct
								+ ". Please wait..");
					} while (c1.moveToNext());// end while loop
				}
				c1.close();
			}// END OPTION only numbers

			if (check_use_major_letters.isChecked()) {// for 2 OPTIONS: letters
														// at beginning, or find
														// all letters
				for (int ctalp = 0; ctalp < inpct; ctalp++) {
					if (alp.contains(inpspl[ctalp])) {
						ltct++;
					} else {
						break;
					}
				}
				for (int ctnum = ltct; ctnum < inpct; ctnum++) {
					if (num_str.contains(inpspl[ctnum])) {
						numct++;
					} else {
						break;
					}
				}
				int totct = ltct + numct;
				if (ltct == 0 || totct != inpct) {
					publishProgress(
							"Must enter letters first followed by only numbers.",
							"NOTHING LOADED");
					return null;
				}
				beglets = input.substring(0, ltct);
				endnums = input.substring(ltct, inpct);
				if (check_all_major_letters.isChecked()) {
					String begbegwor, endbegwor;
					for (int i = 0; i < ltct; i++) {// replaces redundant
													// letters
						begbegwor = beglets.substring(0, i + 1);
						endbegwor = beglets.substring(i + 1, ltct);
						beglets = begbegwor + endbegwor.replace(inpspl[i], "-");
					}
					beglets = beglets.replace("-", "");
				}
				ltct = beglets.length();
				int seawornumltct = 0;
				String begworstr;
				String seawor;
				String[] defspl, worspl;
				text += "<b>RESULTS:<b><br />";
				Boolean is_prompt = false;
				// FOR NUMBERS TO WORDS:
				for (int bkct = 0; bkct < ltct; bkct++) {
					begworstr = beglets.substring(bkct, bkct + 1);
					Cursor c_num_wors = MainLfqActivity.getDictionaryDb().rawQuery(
							"SELECT * FROM dictionarya WHERE Word LIKE '"
									+ begworstr + "%' AND Number LIKE '"
									+ endnums + "%'", null);
					if (c_num_wors.moveToFirst()) {
						if (is_prompt == false) {
							text += "NUMBERS TO WORDS:<br />";
							is_prompt = true;
						}
						search_success = true;
						publishProgress(text, "Loading.. " + ct
								+ " of numbers to words. Please wait..");
						do {
							done = false;
							worspl = Helpers.explode(c_num_wors
									.getString(c_num_wors
											.getColumnIndex("Word")));
							wriwor = formatWord("", worspl, 0, numct);
							size = c_num_wors.getString(
									c_num_wors.getColumnIndex("Definition"))
									.length();
							ct++;
							text += ct + ") " + wriwor + " ";
							if (size != 0) {
								text += c_num_wors.getString(c_num_wors
										.getColumnIndex("Definition"));
							}
							text += "<br />";
							publishProgress(text, "Loading.. " + ct
									+ " of numbers to words. Please wait..");
						} while (c_num_wors.moveToNext());
					}
					c_num_wors.close();
				}

				is_prompt = false;
				text += "<br />";

				for (int bkct = 0; bkct < ltct; bkct++) {
					seawornumltct = ltct - bkct;
					begworstr = beglets.substring(0, seawornumltct);
					if (check_all_major_letters.isChecked()) {
						begworstr = beglets.substring(bkct, bkct + 1);
						seawornumltct = 1;
					}
					String[] selectionArgs = { begworstr + "%" };
					Cursor c_let_maj = MainLfqActivity.getDictionaryDb().rawQuery(
							"SELECT * FROM dictionarya WHERE Word LIKE ?",
							selectionArgs);
					if (c_let_maj.moveToFirst()) {
						publishProgress(
								text,
								"Loading.. "
										+ ct2
										+ " of letters and numbers to words. Please wait..");
						do {

							done = false;
							seawor = c_let_maj.getString(c_let_maj
									.getColumnIndex("Word"));
							defspl = Helpers.explode(seawor);
							num = getNumber(defspl, seawornumltct);
							if (num.equals("")) {
								continue;
							}
							if (num.length() < numct) {
								continue;
							}
							if (endnums.equals(num.substring(0, numct))) {// if
																			// numbers
																			// in
																			// indicator=
																			// the
																			// num
								if (is_prompt == false) {
									text += "NUMBERS AND LETTERS TO WORDS<br />";
									text += "<br />-->" + beglets + endnums
											+ "<br />";
									is_prompt = true;
								}
								done = true;
								wriwor = begworstr.toLowerCase(Locale.US);
								worspl = Helpers.explode(c_let_maj
										.getString(c_let_maj
												.getColumnIndex("Word")));
								wriwor = formatWord(wriwor, worspl,
										seawornumltct, numct);
								if (done == true) {
									size = c_let_maj
											.getString(
													c_let_maj
															.getColumnIndex("Definition"))
											.length();
									ct2++;
									text += ct2 + ") " + wriwor + " ";
									if (size != 0) {
										text += c_let_maj.getString(c_let_maj
												.getColumnIndex("Definition"));
									}
									text += "<br />";
									publishProgress(
											text,
											"Loading.. "
													+ ct2
													+ " of letters and numbers to words. Please wait..");
									search_success = true;
								}
							}// end if endnums equals the found number
						} while (c_let_maj.moveToNext());
					}
					c_let_maj.close();
				}// end loop for each letter(s)
				ct += ct2;
			}// END IF OTHER 2 OPTIONS
			if (search_success) {
				publishProgress(text, "Loaded " + ct + " total words.");
			} else {
				publishProgress("RESULTS: SORRY TRY AGAIN", "NOTHING LOADED");
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			results.setText(Html.fromHtml("<b>" + values[0] + "</b>"));
			load_results.setText(Html.fromHtml("<b>" + values[1] + "</b>"));
		}

		@Override
		protected void onPostExecute(String file_url) {
		}

	}

	public String getNumber(String[] defspl, int seawornumltct) {
		int z;
		String num = "";
		int wl = defspl.length;
		for (int wornumst = seawornumltct; wornumst < (wl - 1); wornumst++) {
			z = wornumst + 1;
			if (z == wl) {
				z--;
			}
			if (defspl[wornumst].equals("s") && !defspl[z].equals("h")) {
				num += "0";
			}
			if (defspl[wornumst].equals("z")) {
				num += "0";
			}
			if (defspl[wornumst].equals("d") || defspl[wornumst].equals("t")) {
				num = num + "1";
			}
			if (defspl[wornumst].equals("n")) {
				num += "2";
			}
			if (defspl[wornumst].equals("m")) {
				num += "3";
			}
			if (defspl[wornumst].equals("r")) {
				num += "4";
			}
			if (defspl[wornumst].equals("l")) {
				num += "5";
			}
			if (defspl[wornumst].equals("j")) {
				num += "6";
			}
			if (defspl[wornumst].equals("g") && !defspl[z].equals("g")
					&& !defspl[z].equals("h")) {
				num += "6";
			}
			if (defspl[wornumst].equals("c") && defspl[z].equals("h")) {
				num += "6";
			}
			if (defspl[wornumst].equals("s") && defspl[z].equals("h")) {
				num += "6";
			}
			if (defspl[wornumst].equals("g") && defspl[z].equals("g")) {
				num += "7";
				wornumst++;
			}
			if (defspl[wornumst].equals("c") && !defspl[z].equals("h")) {
				num += "7";
			}
			if (defspl[wornumst].equals("k") || defspl[wornumst].equals("q")) {
				num += "7";
			}
			if (defspl[wornumst].equals("f") || defspl[wornumst].equals("v")) {
				num += "8";
			}
			if (defspl[wornumst].equals("p") && defspl[z].equals("h")) {
				num += "8";
			}
			if (defspl[wornumst].equals("b")) {
				num += "9";
			}
			if (defspl[wornumst].equals("p") && !defspl[z].equals("h")) {
				num += "9";
			}
		}
		return num;
	}

	public String formatWord(String wriwor, String[] worspl, int start,
			int numct) {
		int worct = worspl.length;
		int marletct = 0;
		for (int i = start; i < worct; i++) {
			if (ign_lets_str.contains(worspl[i]) || marletct >= numct) {
				wriwor += worspl[i].toLowerCase(Locale.US);
			} else {
				if (worspl[i].toLowerCase(Locale.US).equals("h") && i == start) {
					wriwor += worspl[i].toLowerCase(Locale.US);
					continue;
				}
				if (i > start) {
					if (worspl[i].toLowerCase(Locale.US).equals("h")
							&& !dbl_lets.contains(worspl[i - 1])) {
						wriwor += worspl[i].toLowerCase(Locale.US);
						continue;
					}
				}
				if (!ign_lets_str.contains(worspl[i])) {
					wriwor += worspl[i].toUpperCase(Locale.US);
					if (inpspl[0].equals("0")
							&& !worspl[i].toLowerCase(Locale.US).equals("s")
							&& !worspl[i].toLowerCase(Locale.US).equals("z")
							&& marletct == 0) {
						done = false;
					}
					if (inpspl[0].equals("0")
							&& !worspl[i].toLowerCase(Locale.US).equals("s")
							&& worspl[i + 1].toLowerCase(Locale.US).equals("h")
							&& marletct == 0) {
						done = false;
					}
					marletct++;
					if (i < (worct - 1) && dbl_lets.contains(worspl[i])) {
						if (worspl[i + 1].toLowerCase(Locale.US).equals("g")
								|| worspl[i + 1].toLowerCase(Locale.US).equals(
										"h")) {
							wriwor += worspl[i + 1].toUpperCase(Locale.US);
							i++;
						}
					}
				}
			}// end if marletct<numct
		}// end for loop format letters
		return wriwor;
	}
}// end class