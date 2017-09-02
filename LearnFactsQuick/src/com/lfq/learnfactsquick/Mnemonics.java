package com.lfq.learnfactsquick;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.lfq.learnfactsquick.Constants.cols.mnemonics;
import com.lfq.learnfactsquick.Constants.tables;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Mnemonics extends Activity {
	private TextView load_results;
	private LinearLayout results;
	private String text;
	private String[] peglist;
	private String cat;
	private List<Boolean> is_expands;
	private int ct_cats;
	private Activity this_act;
	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;
	private static Boolean is_database_load, is_mne_load;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this_act = this;
		is_database_load = true;
		is_mne_load = true;
		sharedPref = getSharedPreferences(
				getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		editor = sharedPref.edit();

		is_expands = new ArrayList<Boolean>();
		peglist = new String[] { "Tea", "New", "Me", "Ear", "Owl", "Gay",
				"Cow", "UFO", "Bee", "Dash", "Dead", "Tuna", "Atom", "Deer",
				"Tale", "Dog", "Duke", "TV", "Tuba", "NASA", "Ant", "Noon",
				"Enemy", "Honor", "Noel", "Wing", "Ink", "Navy", "Newbie",
				"Mouse", "Myth", "Moon", "Memo", "Humor", "Email", "Image",
				"Macho", "Movie", "Amoeba", "Horse", "Rat", "Rain", "Arm",
				"Arrow", "Rail", "Rage", "Rich", "Review", "Robe", "Loose",
				"Old", "Lion", "Lama", "Liar", "Hello", "Leg", "Lake", "Wolf",
				"Loop", "Goose", "Goat", "Gun", "Game", "Gray", "Galaxy",
				"Egg", "Joke", "Goofy", "Jeep", "Cheese", "Cat", "Knee",
				"Coma", "Car", "Cola", "Cage", "Cake", "Cafe", "Chip", "Fish",
				"Fat", "Fun", "Fame", "Fairy", "Fly", "Fog", "Fake", "FIFO",
				"FBI", "Bus", "Bat", "PIN", "Beam", "Bear", "Pool", "Pig",
				"Bike", "Beef", "Babe", "Disease", "Test", "Disney", "Autism",
				"Tzar", "Diesel", "White sage", "Disc", "Satisfy", "Hat shop",
				"Odds", "Daddy", "Titan", "Stadium", "Dexter", "Total",
				"Hot dog", "Attic", "HDTV", "HTTP", "Adonis", "Stunt",
				"Estonian", "Autonomy", "Diner", "Denial", "Stone Age",
				"Dance", "TNF", "Danube", "Times", "Time-out", "Domine",
				"Dummy", "Tumor", "HTML", "Damage", "Stomach", "TMV", "Thumb",
				"Tears", "Druid", "Darwin", "Storm", "Adorer", "Australia",
				"Storage", "Dark", "Dwarf", "Trophy", "Atlas", "Athlete",
				"Italian", "Soda lime", "Hitler", "Dolly", "Dialogue",
				"Italic", "Tea leaf", "Toolbox", "Doghouse", "Widget",
				"Shotgun", "Dogma", "Tiger", "Stagily", "Hedgehog", "Dog hook",
				"Deja vu", "Doughboy", "Steakhouse", "Woodcut", "Technique",
				"Sitcom", "Teacher", "Stokehole", "The Cage", "Duck",
				"Deceive", "Teacup", "TV show", "DVD", "Divine", "The Fame",
				"Stover", "Devil", "Defog", "Device", "Day off", "The F.B.I.",
				"Oedipus", "Tibet", "Headphone", "Tie beam", "Sidebar",
				"Duplex", "Debug", "Topic", "Top-heavy", "Tippy", "News show" };
		new doLoadDatabases().execute();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (is_database_load == false && is_mne_load == false) {
			new doLoadDatabases().execute();
		}
	}

	@Override
	public void onBackPressed() {
		if (is_database_load == false && is_mne_load == false) {
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

	class doLoadDatabases extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setContentView(R.layout.synchronize);
			setContentView(R.layout.show_mnemonics);
			setTitle("MNEMONICS!");
			results = (LinearLayout) findViewById(R.id.show_mnemonic_layout);
			load_results = (TextView) findViewById(R.id.show_mnemonics_load_results);

			text = "Loading databases. Please wait...";
			load_results.setText(Html.fromHtml("<b>" + text + "<b>"));

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
			load_results.setText(Html.fromHtml(values[0]));
		}

		@Override
		protected void onPostExecute(String file_url) {
			load_results.setText("");
			text = "";
			is_database_load = false;
			new doLoadMnemonics().execute();
		}

	}

	class doLoadMnemonics extends AsyncTask<String, TextView, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			results.removeAllViews();
			load_results.setText(Html.fromHtml("<b>NOW LOADING...</b>"));
			ct_cats = 0;
		}

		@Override
		protected String doInBackground(String... params) {

			Cursor c_mne_cats = MainLfqActivity
					.getMneDb()
					.rawQuery(
							"SELECT DISTINCT " + mnemonics.Category + " FROM " + tables.mnemonics + " ORDER BY " + mnemonics.Category,
							null);

			String ent = "";
			String dis_cat;
			int ct_entrees;
			String type, title;
			String wordstr = "";
			String mnestr = "";
			String infostr = "";
			String number_mne = "";
			String number_entrs = "";
			String number_info = "";
			boolean first = true;
			int ind=0,ct=1;
			

			if (c_mne_cats.moveToFirst()) {
				do {
					TextView cat_tv = new TextView(this_act);
					cat = c_mne_cats.getString(c_mne_cats
							.getColumnIndex("Category"));
					Cursor c_mne = MainLfqActivity
							.getMneDb()
							.rawQuery(
									"SELECT * FROM " + tables.mnemonics + " WHERE " + mnemonics.Category + "='"
											+ cat
											+ "' GROUP BY " + mnemonics.Entry_Number + " ORDER BY " + mnemonics.Entry_Number,
									null);
					ct_cats++;
					dis_cat = "+ " + ct_cats + ")" + cat.toUpperCase(Locale.US);
					Spannable spantext = Spannable.Factory.getInstance()
							.newSpannable(dis_cat);
					spantext.setSpan(new BackgroundColorSpan(Color.YELLOW), 0,
							dis_cat.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					cat_tv.setTextSize(20);
					cat_tv.setText(spantext);
					TextView mne_tv = new TextView(this_act);
					is_expands.add(false);
					text = "";
					if (c_mne.moveToFirst()) {
						ct_entrees = 0;
						do {
							type = c_mne.getString(c_mne
									.getColumnIndex(mnemonics.Mnemonic_Type));
							title = c_mne.getString(c_mne
									.getColumnIndex(mnemonics.Title));
							ct_entrees++;
							text += "<b><u>&nbsp;&nbsp;&nbsp;" + ct_entrees
									+ ". " + title + "(" + type
									+ ")</u></b><br />";// set
														// title
														// and
														// type
							wordstr = "";
							mnestr = "";
							infostr = "";
							Cursor c_ent = MainLfqActivity
									.getMneDb()
									.rawQuery(
											"SELECT * FROM " + tables.mnemonics + " WHERE " + mnemonics.Category + "='"
													+ cat
													+ "' AND " + mnemonics.Entry_Number + "='"
													+ c_mne.getString(c_mne.getColumnIndex(mnemonics.Entry_Number))
													+ "' ORDER BY " + mnemonics.Entry_Index,
											null);

							if (c_ent.moveToFirst()) {
								int num_entries = c_ent.getCount();
								ind=0;//i
								ct=1;//j=i+1								
								if (type.equals("anagram")) {
									String[] anaspl = c_ent
											.getString(
													c_ent.getColumnIndex(mnemonics.Entry_Mnemonic))
											.split("");
									String anagram = "";
									for (int i = 0; i < anaspl.length; i++) {
										if (anaspl[i].matches("[A-Z]")) {
											anagram += "<b>" + anaspl[i]
													+ "</b>";
										} else {
											anagram += anaspl[i];
										}
									}
									text += anagram + "<br />";
								}
								do {									
									if (type.equals("mnemonic")) {
										if (c_ent.getString(
												c_ent.getColumnIndex(mnemonics.Entry_Mnemonic)).length()>0){
										text += "<b>"
												+ ct
												+ ". "
												+ c_ent.getString(
														c_ent.getColumnIndex(mnemonics.Entry_Mnemonic))
														.substring(0, 1)
														.toUpperCase(Locale.US)
												+ "</b>";
										}
										if (c_ent.getString(
												c_ent.getColumnIndex(mnemonics.Entry_Mnemonic)).length()>1){										
												text += c_ent.getString(
														c_ent.getColumnIndex(mnemonics.Entry_Mnemonic))
														.substring(1);
										}
										if (c_ent.getString(
												c_ent.getColumnIndex(mnemonics.Entry))
												.length()>0) {
											text += "(<b>"
													+ c_ent.getString(
															c_ent.getColumnIndex(mnemonics.Entry))
															.substring(0, 1)
															.toUpperCase(
																	Locale.US)
													+ "</b>";
											if (c_ent.getString(
													c_ent.getColumnIndex(mnemonics.Entry))
													.length()>1) {											
													
													text += c_ent.getString(
															c_ent.getColumnIndex(mnemonics.Entry))
															.substring(1);
											}
										}
										if (c_ent
												.getString(
														c_ent.getColumnIndex(mnemonics.Entry_Info))
												.length() > 0) {
											text += "("
													+ c_ent.getString(c_ent
															.getColumnIndex(mnemonics.Entry_Info))
													+ ")  ";
										}

										text += ")";
									}
									if (type.equals("anagram")) {
										if (c_ent.getString(
												c_ent.getColumnIndex(mnemonics.Entry))
												.length()>0) {
											text += "<b>"
													+ ct
													+ ". "
													+ c_ent.getString(
															c_ent.getColumnIndex(mnemonics.Entry))
															.substring(0, 1)
															.toUpperCase(
																	Locale.US)
													+ "</b>";
											if (c_ent.getString(
													c_ent.getColumnIndex(mnemonics.Entry))
													.length() >1) {											
													text += c_ent.getString(
															c_ent.getColumnIndex(mnemonics.Entry))
															.substring(1);
											}
										}
										if (c_ent
												.getString(
														c_ent.getColumnIndex(mnemonics.Entry_Info))
												.length() > 0) {
											text += "("
													+ c_ent.getString(c_ent
															.getColumnIndex(mnemonics.Entry_Info))
													+ ")  ";
										}

									}
									if (type.equals("number_mnemonic")) {
										text += "<b>"
												+ c_ent.getString(c_ent
														.getColumnIndex(mnemonics.Entry_Index))
												+ ". "
												+ c_ent.getString(c_ent
														.getColumnIndex(mnemonics.Entry_Mnemonic))
												+ ":</b> ";
										String[] majwordspl = c_ent.getString(
												c_ent.getColumnIndex(mnemonics.Entry))
												.split("");
										String maj_word = "";
										for (int k = 0; k < majwordspl.length; k++) {
											if (majwordspl[k].matches("[A-Z]")) {
												maj_word += "<b>"
														+ majwordspl[k]
														+ "</b>";
											} else {
												maj_word += majwordspl[k];
											}
										}
										text += maj_word;
										if (c_ent
												.getString(
														c_ent.getColumnIndex(mnemonics.Entry_Info))
												.length() > 0) {
											text += "("
													+ c_ent.getString(c_ent
															.getColumnIndex(mnemonics.Entry_Info))
													+ ")   ";
										}

									}
									if (type.equals("peglist")) {
										text += "<b>"
												+ ct
												+ ".</b> "
												+ c_ent.getString(c_ent
														.getColumnIndex(mnemonics.Entry));
										if (c_ent
												.getString(
														c_ent.getColumnIndex(mnemonics.Entry_Info))
												.length() > 0) {
											text += "("
													+ c_ent.getString(c_ent
															.getColumnIndex(mnemonics.Entry_Info))
													+ ")  ";
										}

										text += "((#"
												+ c_ent.getString(c_ent
														.getColumnIndex(mnemonics.Entry_Index))
												+ ":"
												+ peglist[ind]
														.toUpperCase(Locale.US)
												+ ")"
												+ c_ent.getString(c_ent
														.getColumnIndex(mnemonics.Entry_Mnemonic))
												+ ")   ";

									}
									if (c_ent
											.getString(
													c_ent.getColumnIndex(mnemonics.Is_Linebreak))
											.equals("1")) {
										text += "<br />";
									}									
									ind++;
									ct++;									
								} while (c_ent.moveToNext());//END LOOP EACH MNEMONIC
								text += "<br />";
							}
						} while (c_mne.moveToNext());//END LOOP EACH CATEEGORY
					}// END IF c_mne.moveToFirst()
					text += "<br />";
					mne_tv.setTextSize(20);
					mne_tv.setText(Html.fromHtml(text));
					mne_tv.setVisibility(View.GONE);
					publishProgress(cat_tv, mne_tv);
				} while (c_mne_cats.moveToNext());

			}
			return null;
		}

		@Override
		protected void onProgressUpdate(TextView... tvs) {
			load_results
					.setText(Html.fromHtml("<b>Loading " + cat + "...</b>"));
			results.addView(tvs[0]);// cat
			final int this_cat_ind = ct_cats - 1;
			final TextView this_cat_tv = tvs[0];
			final TextView this_cat_info_tv = tvs[1];
			tvs[0].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String cat_text_now = this_cat_tv.getText().toString()
							.substring(1);
					if (is_expands.get(this_cat_ind) == false) {
						is_expands.set(this_cat_ind, true);
						this_cat_info_tv.setVisibility(View.VISIBLE);
						cat_text_now = "-" + cat_text_now;
						Spannable spantext = Spannable.Factory.getInstance()
								.newSpannable(cat_text_now);
						spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
								0, cat_text_now.length(),
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						this_cat_tv.setText(spantext);
					} else {
						is_expands.set(this_cat_ind, false);
						this_cat_info_tv.setVisibility(View.GONE);
						cat_text_now = "+" + cat_text_now;
						Spannable spantext = Spannable.Factory.getInstance()
								.newSpannable(cat_text_now);
						spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
								0, cat_text_now.length(),
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						this_cat_tv.setText(spantext);
					}
				}
			});
			results.addView(tvs[1]);// cat_info
		}

		@Override
		protected void onPostExecute(String file_url) {
			load_results.setText(Html.fromHtml("<b>ALL LOADED!</b>"));
			is_mne_load = false;
		}
	}

}