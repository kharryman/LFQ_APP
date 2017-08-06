package com.lfq.learnfactsquick;

import com.lfq.learnfactsquick.R.color;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.style.BackgroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;


public class HelpMenu extends Activity{
	TextView general_prompt, edit_prompt,show_prompt,tools_prompt,users_prompt;
	//EDIT TVs:
	TextView edit_acr, edit_alp, edit_dic, edit_events, edit_mne, edit_num, edit_tables, edit_nws;
	//SHOW TVs:
	TextView show_acr, show_nws, show_mne, show_num, show_timeline;
	//TOOL TVs:
	TextView tools_maj, tools_cel, tools_ana, tools_mne_gen, tools_dic;    	
	
	//HELP TVS:
	//EDIT TVs:
	TextView general_help;
	TextView edit_acr_help, edit_alp_help, edit_dic_help, edit_events_help, edit_mne_help, edit_num_help, edit_tables_help, edit_nws_help;
	//SHOW TVs:
	TextView show_acr_help, show_nws_help, show_mne_help, show_num_help, show_timeline_help;
	//TOOL TVs:
	TextView tools_maj_help, tools_cel_help, tools_ana_help, tools_mne_gen_help, tools_dic_help;
	//USERS TV:
	TextView users_help;
	
	Boolean is_general=false, is_edit=false, is_show=false, is_tools=false;
	Boolean is_edit_acr=false, is_edit_alp=false, is_edit_dic=false, is_edit_events=false, is_edit_mne=false, is_edit_num=false, is_edit_tables=false, is_edit_nws=false;
    Boolean is_show_acr=false, is_show_nws=false, is_show_mne=false, is_show_num=false, is_show_timeline=false;
    Boolean is_tools_maj=false, is_tools_cel=false, is_tools_ana=false, is_tools_mne_gen=false, is_tools_dic=false;
    //USERS TV:
	Boolean is_users=false;
	
	ScrollView help_menu_scroll;
	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sharedPref = getSharedPreferences(
				getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		//editor = sharedPref.edit();
		setContentView(R.layout.help_menu);
		setViews();		
		setListeners();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		help_menu_scroll = (ScrollView) findViewById(R.id.help_menu_scroll);
		help_menu_scroll.setBackgroundColor(sharedPref.getInt("BG Color", color.light_green));
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
		return true;
	}
	
	public void setViews() {
		String text = "";
		setTitle("HELP MENU!");
		Spannable spantext;
		
		general_prompt = (TextView) findViewById(R.id.help_menu_general_prompt);
		text = "+GENERAL:";
		spantext = Spannable.Factory.getInstance()
				.newSpannable(text);
		spantext.setSpan(new BackgroundColorSpan(sharedPref.getInt("BUTTON Color", color.light_green)), 0,
				text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);		
		general_prompt.setText(spantext);
		
		general_help = (TextView) findViewById(R.id.help_menu_general_help);		
		general_help.setVisibility(View.GONE);
        text = "<b>Acrostics:</b> Like acronyms of words, but give word meanings to help you remember.<br />";
        text += "<b>Anagrams:</b> A word combination of the first letters of a list of words you want to remember.<br />";
        text += "<b>Celebrity Numbers:</b> A way to remember a sequence of 2 numbers by having letters representing numbers a=1,b=2...i=9,j=10 and having those letters represent the first letters of a celebrity's first and last names.<br />";
        text += "<b>Mnemonics:</b> A list remembered as a sentence where, the first letters of the words of the sentence are the same as the first letters of the words to remember.<br />";
        text += "<b>Peglist:</b> A list remember by associating the index of the list(a number) with a word associated with the number(usual using major system) to help you remember.<br />";        
		general_help.setText(Html.fromHtml(text));
		
		
		//TextView edit_acr, edit_alp, edit_dic, edit_events, edit_mne, edit_num, edit_tables, edit_nws;
		//EDIT TVs:		
		//acrostics:
		edit_prompt = (TextView) findViewById(R.id.help_menu_edit_prompt);
		text = "-EDIT:";
		spantext = Spannable.Factory.getInstance()
				.newSpannable(text);
		spantext.setSpan(new BackgroundColorSpan(sharedPref.getInt("BUTTON Color", color.light_green)), 0,
				text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);		
		edit_prompt.setText(spantext);	
	
		
		edit_acr = (TextView) findViewById(R.id.help_menu_edit_acrostics);
		text = "+EDIT ACROSTICS";
		spantext = Spannable.Factory.getInstance()
				.newSpannable(text);
		spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
				0, text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		edit_acr.setText(spantext);
		edit_acr_help = (TextView) findViewById(R.id.help_menu_edit_acrostics_help);
		edit_acr_help.setVisibility(View.GONE);
        text = "<b>Vocabulary switch:</b> Switches between whatever table is currently selected and the general vocabulary table.<br />";
        text += "<b>Use Dictionary:</b> Uses dictionary to select words you can input yourself as acrostics.<br />";
        text += "<b>Use All Acrostics:</b> Searches all acrostics tables to find a words(to help review a word)<br />";        
		edit_acr_help.setText(Html.fromHtml(text));

		//alphabet:
		edit_alp = (TextView) findViewById(R.id.help_menu_edit_alphabet);
		text = "+EDIT ALPHABET";
		spantext = Spannable.Factory.getInstance()
				.newSpannable(text);
		spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
				0, text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		edit_alp.setText(spantext);
		edit_alp_help = (TextView) findViewById(R.id.help_menu_edit_alphabet_help);
		edit_alp_help.setVisibility(View.GONE);
		text = "<b>Select a table and a letter(A to Z) to find words beginning with that letter associated with the table or category.</b>(helps you form acrostics)<br />";
		edit_alp_help.setText(Html.fromHtml(text));
		
		//dictionary:
		edit_dic = (TextView) findViewById(R.id.help_menu_edit_dictionary);
		text = "+EDIT DICTIONARY";
		spantext = Spannable.Factory.getInstance()
				.newSpannable(text);
		spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
				0, text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		edit_dic.setText(spantext);
		edit_dic_help = (TextView) findViewById(R.id.help_menu_edit_dictionary_help);
		edit_dic_help.setVisibility(View.GONE);
		text += "<b>Helps you find and input words.</b>(includes part of speech)<br />";		
		edit_dic_help.setText(Html.fromHtml(text));
		
		//events:
		edit_events = (TextView) findViewById(R.id.help_menu_edit_events);
		text = "+EDIT EVENTS";
		spantext = Spannable.Factory.getInstance()
				.newSpannable(text);
		spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
				0, text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		edit_events.setText(spantext);
		edit_events_help = (TextView) findViewById(R.id.help_menu_edit_events_help);
		edit_events_help.setVisibility(View.GONE);
		text = "<b>By Date?:</b>Finds all events of a particular date.<br />";
		text += "<b>By Year?:</b>Finds all events of a particular year.<br />";
		text += "<b>SHARED(CHECK)/USER TABLE?:</b>If checked, uses the shared events table that everyone uses. If not checked, it uses your own events table, but yo need to be logged in.<br />";
		text += "<b>Historical/Personal?:</b>Shows either historical events or personal events of a users table. Must be logged in.<br />";
		edit_events_help.setText(Html.fromHtml(text));
		
		//mnemonics:
		edit_mne = (TextView) findViewById(R.id.help_menu_edit_mnemonics);
		text = "+EDIT MNEMONICS";
		spantext = Spannable.Factory.getInstance()
				.newSpannable(text);
		spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
				0, text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		edit_mne.setText(spantext);
		edit_mne_help = (TextView) findViewById(R.id.help_menu_edit_mnemonics_help);
		edit_mne_help.setVisibility(View.GONE);
		text = "<b>Standard:</b>Uses standard first letters of a sentence mnemonic notation.<br />";
		text += "<b>Number:</b>Input major words as mnemonics.<br />";
		text += "<b>Anagram:</b>Input a mnemonic word as an acronym(anagram) to remember a set of words.<br />";
		text += "<b>Peglist:</b>Uses Peglist(using major words)system to remember a list of words.<br />";
		text = "<b>Insert linebreaks:</b>If checked, enters a new line(more readable) for each mnemonic.<br />";
		edit_mne_help.setText(Html.fromHtml(text));
		
		//numbers:
		edit_num = (TextView) findViewById(R.id.help_menu_edit_numbers);
		text = "+EDIT NUMBERS";
		spantext = Spannable.Factory.getInstance()
				.newSpannable(text);
		spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
				0, text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		edit_num.setText(spantext);
		edit_num_help = (TextView) findViewById(R.id.help_menu_edit_numbers_help);
		edit_num_help.setVisibility(View.GONE);
		text = "<b>Shared:</b>Uses a shared number table(global number table).<br />";
		text += "<b>User:</b>Uses the user number table. Must be logged in.<br />";
		edit_num_help.setText(Html.fromHtml(text));
		
		//tables:
		edit_tables = (TextView) findViewById(R.id.help_menu_edit_tables);
		text = "+EDIT TABLES";
		spantext = Spannable.Factory.getInstance()
				.newSpannable(text);
		spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
				0, text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		edit_tables.setText(spantext);
		edit_tables_help = (TextView) findViewById(R.id.help_menu_edit_tables_help);
		edit_tables_help.setVisibility(View.GONE);
		text = "<b>Data Type:</b>Select the kind of data to be input(TINYINT=whole numbers,TINYTEXT is short text, LONGTEXT is long text.<br />";
		text += "<b>Change name:</b>Only changes name of table.<br />";
		text += "<b>Include Mnemonics:</b>Inserts a mnemonics and a peglist column into the table.<br />";
		text += "<b>Clear Table:</b>Clears all inputted values.<br />";
		edit_tables_help.setText(Html.fromHtml(text));
		
		//newwords:
		edit_nws = (TextView) findViewById(R.id.help_menu_edit_newwords);
		text = "+EDIT NEWWORDS";
		spantext = Spannable.Factory.getInstance()
				.newSpannable(text);
		spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
				0, text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		edit_nws.setText(spantext);
		edit_nws_help = (TextView) findViewById(R.id.help_menu_edit_newwords_help);
		edit_nws_help.setVisibility(View.GONE);
		text = "<b>Change Date:</b>Go back a number of review days to see words from last review.<br />";
		text += "<b>How Many Words:</b>Select how many words you want to review for this day.<br />";
		text += "<b>Change Review Times:</b>Input new times(how many days between) for each review.<br />";
		text += "<b>ALL 1 TABLE:</b>Use words automatically from same table.<br />";
		edit_nws_help.setText(Html.fromHtml(text));
		
		//SHOW TVs
		//TextView show_acr, show_nws, show_mne, show_num, show_timeline;
		//show acrostics tables:
		text = "-SHOW:";
		spantext = Spannable.Factory.getInstance()
				.newSpannable(text);
		spantext.setSpan(new BackgroundColorSpan(sharedPref.getInt("BUTTON Color", color.light_green)), 0,
				text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);		
		show_prompt = (TextView) findViewById(R.id.help_menu_show_prompt);		
		show_prompt.setText(spantext);
		
		show_acr = (TextView) findViewById(R.id.help_menu_show_acrostics_tables);
		text = "+ACROSTIC TABLES";
		spantext = Spannable.Factory.getInstance()
				.newSpannable(text);
		spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
				0, text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		show_acr.setText(spantext);
		show_acr_help = (TextView) findViewById(R.id.help_menu_show_acrostics_tables_help);
		show_acr_help.setVisibility(View.GONE);
		text = "<b>check boxes(in slide pane):</b>Use to select which records you want when you show the table.<br />";
		text += "<b>Read?:</b>If checked, allows you to read the information by highlighting each setence of a record by tapping on its information cell.<br />";
		text += "<b>Show all categories:</b>Shows category values along with the name in the name cell.<br />";
		text += "<b>How many so far?:</b>Shows numbers of completed(entered) acrostics for each table.<br />";
		show_acr_help.setText(Html.fromHtml(text));
		
		//show newwords tables:
		show_nws = (TextView) findViewById(R.id.help_menu_show_newwords);
		text = "+SHOW NEWWORDS";
		spantext = Spannable.Factory.getInstance()
				.newSpannable(text);
		spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
				0, text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		show_nws.setText(spantext);
		show_nws_help = (TextView) findViewById(R.id.help_menu_show_newwords_help);
		show_nws_help.setVisibility(View.GONE);
		text = "<b>#Days Before?:</b>Select how many days back you want to review(if you fall behind by not reviewing everyday).<br />";
		text += "<b>+Information(-Information):</b>Click to expand and allow to edit information for each word(defaulted to be collapsed, because information uses a lot of space).<br />";
		show_nws_help.setText(Html.fromHtml(text));
		
		//show mnemonics:
		show_mne = (TextView) findViewById(R.id.help_menu_show_mnemonics);
		text = "+SHOW MNEMONICS";
		spantext = Spannable.Factory.getInstance()
				.newSpannable(text);
		spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
				0, text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		show_mne.setText(spantext);
		show_mne_help = (TextView) findViewById(R.id.help_menu_show_mnemonics_help);
		show_mne_help.setVisibility(View.GONE);
		text = "<b>Click Mnemonic Table to expand or collapse mnemonics of that table.<br />";		
		show_mne_help.setText(Html.fromHtml(text));
		
		//show numbers:
		show_num = (TextView) findViewById(R.id.help_menu_show_numbers);
		text = "+SHOW NUMBERS";
		spantext = Spannable.Factory.getInstance()
				.newSpannable(text);
		spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
				0, text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		show_num.setText(spantext);
		show_num_help = (TextView) findViewById(R.id.help_menu_show_numbers_help);
		show_num_help.setVisibility(View.GONE);
		text = "<b>Get Shared Numbers:</b>Use to get shared(global number table) everyone uses with common numbers.<br />";
		text += "<b>Get Users Numbers:</b>Use to get user numbers. Must be logged in.<br />";
		show_num_help.setText(Html.fromHtml(text));
		
		//show timeline
		show_timeline = (TextView) findViewById(R.id.help_menu_timeline);
		text = "+TIMELINE";
		spantext = Spannable.Factory.getInstance()
				.newSpannable(text);
		spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
				0, text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		show_timeline.setText(spantext);
		show_timeline_help = (TextView) findViewById(R.id.help_menu_timeline_help);
		show_timeline_help.setVisibility(View.GONE);
		text = "<b>COMBINE YEAR AND DATE?</b>Check to combine year and date (YYYYMMDD) to search for single major words of an event date.<br />";
		text += "<b>GET(SHOW)?:</b>If checked, gets both events to edit, and saved major words. If not checked only shows saved major words.<br />";
		text += "<b>SHARED(USER)EVENTS?:</b>If checked, shows events from common(complete) events table.(all historical events). If not checked, shows events of users event table, but must be logged in.<br />";
		text += "<b>GET DATE EVENTS?:</b>Gets all events of a particular date.<br />";
		text += "<b>GET YEAR EVENTS?:</b>Gets all events of a particular year.<br />";
		text += "<b>GET ALL EVENTS?:</b>Gets all saved events of the shared events table.<br />";
		show_timeline_help.setText(Html.fromHtml(text));
		
		//TOOL TVs:
		//TextView show_maj, show_cel, show_ana, show_mne_gen, show_dic;
		//major system:
		text = "-TOOLS:";
		spantext = Spannable.Factory.getInstance()
				.newSpannable(text);
		spantext.setSpan(new BackgroundColorSpan(sharedPref.getInt("BUTTON Color", color.light_green)), 0,
				text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);		
		tools_prompt = (TextView) findViewById(R.id.help_menu_tools_prompt);		
		tools_prompt.setText(spantext);
		
		tools_maj = (TextView) findViewById(R.id.help_menu_major_system);
		text = "+MAJOR SYSTEM";
		spantext = Spannable.Factory.getInstance()
				.newSpannable(text);
		spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
				0, text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tools_maj.setText(spantext);
		tools_maj_help = (TextView) findViewById(R.id.help_menu_major_system_help);
		tools_maj_help.setVisibility(View.GONE);
		text = "<b>Use beginning letters?:</b>input a beginning letter you want your major word to start with first, then numbers(use to help find major words to use when forming your acrostics of a word.<br />";
		text += "<b>find words for each letter?:</b>for inputting more than one beginning letter(for example, your entire word) you want your major word to start with. Enter all these first followed by the numbers(use togther with 'Use beginning letters?').<br />";
		tools_maj_help.setText(Html.fromHtml(text));
		
		//celebrities:
		tools_cel = (TextView) findViewById(R.id.help_menu_celebrity);
		text = "+CELEBRITY NUMBERS";
		spantext = Spannable.Factory.getInstance()
				.newSpannable(text);
		spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
				0, text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tools_cel.setText(spantext);
		tools_cel_help = (TextView) findViewById(R.id.help_menu_celebrity_help);
		tools_cel_help.setVisibility(View.GONE);
		text = "<b>STUDY ENTIRE LIST?:</b>Check if you want to see all celebrities representing all numbers from 00 to 99.<br />";
		text += "<b>ABREVIATE RESULTS?:</b>Check if you only want names(not associated information).<br />";		
		tools_cel_help.setText(Html.fromHtml(text));
		
		//anagrams:
		tools_ana = (TextView) findViewById(R.id.help_menu_anagrams);
		text = "+ANAGRAMS";
		spantext = Spannable.Factory.getInstance()
				.newSpannable(text);
		spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
				0, text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tools_ana.setText(spantext);
		tools_ana_help = (TextView) findViewById(R.id.help_menu_anagrams_help);
		tools_ana_help.setVisibility(View.GONE);
		text = "<b>Find all combinations?:</b>Check to find all anagrams, basically.<br />";
		text += "<b>Treat consonants like vowels?:</b>Skip by consonants if too many consonants.<br />";
		text += "<b>Find a limited number of letters?:</b>Check if you only need some letters of the anagram, or if you can't find any anagrams.<br />";
		tools_ana_help.setText(Html.fromHtml(text));
		
		//mnemonic generator:
		tools_mne_gen = (TextView) findViewById(R.id.help_menu_mnemonic_generator);
		text = "+MNEMONIC GENERATOR";
		spantext = Spannable.Factory.getInstance()
				.newSpannable(text);
		spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
				0, text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tools_mne_gen.setText(spantext);
		tools_mne_gen_help = (TextView) findViewById(R.id.help_menu_mnemonic_generator_help);
		tools_mne_gen_help.setVisibility(View.GONE);
		text = "<b>Choose Theme?:</b>Select the kinds of nouns you want associated with your mnemonic.<br />";
		text += "<b>Choose Adjective Type?:</b>Select the kinds of adjectives you want associated with your mnemonic.<br />";
		tools_mne_gen_help.setText(Html.fromHtml(text));
		
		//dictionary:
		tools_dic = (TextView) findViewById(R.id.help_menu_dictionary);
		text = "+DICTIONARY";
		spantext = Spannable.Factory.getInstance()
				.newSpannable(text);
		spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
				0, text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tools_dic.setText(spantext);
		tools_dic_help = (TextView) findViewById(R.id.help_menu_dictionary_help);
		tools_dic_help.setVisibility(View.GONE);
		text = "<b>Find one word?:</b>Check if you want to search for only one word. '_' can select one character and '%' can select 1 or more characters.<br />";
		tools_dic_help.setText(Html.fromHtml(text));
		
		
		//USER TVs
		users_prompt = (TextView) findViewById(R.id.help_menu_users_prompt);
		text = "+USERS";
		spantext = Spannable.Factory.getInstance()
				.newSpannable(text);
		spantext.setSpan(new BackgroundColorSpan(sharedPref.getInt("BUTTON Color", color.light_green)),
				0, text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		users_prompt.setText(spantext);

		users_help = (TextView) findViewById(R.id.help_menu_users_help);
		users_help.setVisibility(View.GONE);
		text = "<b>DELETE USER'S REVIEWED TABLES/WORDS:</b>Use for clearing words you reviewed in the past that you want to review again.<br />";
		text += "<b>ADD TO USER'S REVIEWED TABLES/WORDS:</b>Use to add words or tables, you already know before using the system.<br />";
		text += "<b>COMPLETE WORDS LIST:</b>Shows you all the words that are ready to be reviewed because they have complete information and acrostics.<br />";
		users_help.setText(Html.fromHtml(text));
		
	}
	
	public void setListeners(){
		edit_prompt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = "";
				Spannable spantext;
				if (is_edit == true) {
					is_edit = false;
					showEdit();					
					text = "-EDIT";
					spantext = Spannable.Factory.getInstance()
							.newSpannable(text);
					spantext.setSpan(new BackgroundColorSpan(sharedPref.getInt("BUTTON Color", color.light_green)),
							0, text.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					edit_prompt.setText(spantext);
				} else {
					is_edit = true;
					hideEdit();					
					text = "+EDIT";
					spantext = Spannable.Factory.getInstance()
							.newSpannable(text);
					spantext.setSpan(new BackgroundColorSpan(sharedPref.getInt("BUTTON Color", color.light_green)),
							0, text.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					edit_prompt.setText(spantext);
				}
			}
		});
		show_prompt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = "";
				if (is_show == true) {
					is_show = false;
					showShow();					
					text = "-SHOW";
					Spannable spantext = Spannable.Factory.getInstance()
							.newSpannable(text);
					spantext.setSpan(new BackgroundColorSpan(sharedPref.getInt("BUTTON Color", color.light_green)),
							0, text.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					show_prompt.setText(spantext);
				} else {
					is_show = true;
					hideShow();					
					text = "+SHOW";
					Spannable spantext = Spannable.Factory.getInstance()
							.newSpannable(text);
					spantext.setSpan(new BackgroundColorSpan(sharedPref.getInt("BUTTON Color", color.light_green)),
							0, text.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					show_prompt.setText(spantext);
				}
			}
		});
		tools_prompt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = "";
				if (is_tools == true) {
					is_tools = false;
					showTools();					
					text = "-TOOLS";
					Spannable spantext = Spannable.Factory.getInstance()
							.newSpannable(text);
					spantext.setSpan(new BackgroundColorSpan(sharedPref.getInt("BUTTON Color", color.light_green)),
							0, text.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					tools_prompt.setText(spantext);
				} else {
					is_tools = true;
					hideTools();					
					text = "+TOOLS";
					Spannable spantext = Spannable.Factory.getInstance()
							.newSpannable(text);
					spantext.setSpan(new BackgroundColorSpan(sharedPref.getInt("BUTTON Color", color.light_green)),
							0, text.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					tools_prompt.setText(spantext);
				}
			}
		});
		

		//---------------------------------------------------------
		//EDIT LISTENERS:
		edit_acr.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = "";
				if (is_edit_acr == false) {
					is_edit_acr = true;
					edit_acr_help.setVisibility(View.VISIBLE);					
					text = "-" + edit_acr.getText().toString().substring(1);
				} else {
					is_edit_acr = false;
					edit_acr_help.setVisibility(View.GONE);					
					text = "+" + edit_acr.getText().toString().substring(1);
				}
				Spannable spantext = Spannable.Factory.getInstance()
						.newSpannable(text);
				spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
						0, text.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				edit_acr.setText(spantext);
			}
		});
		edit_alp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = "";
				if (is_edit_alp == false) {
					is_edit_alp = true;
					edit_alp_help.setVisibility(View.VISIBLE);					
					text = "-" + edit_alp.getText().toString().substring(1);
				} else {
					is_edit_alp = false;
					edit_alp_help.setVisibility(View.GONE);					
					text = "+" + edit_alp.getText().toString().substring(1);
				}
				Spannable spantext = Spannable.Factory.getInstance()
						.newSpannable(text);
				spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
						0, text.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				edit_alp.setText(spantext);
			}
		});
		edit_acr.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = "";
				if (is_edit_acr == false) {
					is_edit_acr = true;
					edit_acr_help.setVisibility(View.VISIBLE);					
					text = "-" + edit_acr.getText().toString().substring(1);
				} else {
					is_edit_acr = false;
					edit_acr_help.setVisibility(View.GONE);					
					text = "+" + edit_acr.getText().toString().substring(1);
				}
				Spannable spantext = Spannable.Factory.getInstance()
						.newSpannable(text);
				spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
						0, text.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				edit_acr.setText(spantext);
			}
		});
		edit_dic.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = "";
				if (is_edit_dic == false) {
					is_edit_dic = true;
					edit_dic_help.setVisibility(View.VISIBLE);					
					text = "-" + edit_dic.getText().toString().substring(1);
				} else {
					is_edit_dic = false;
					edit_dic_help.setVisibility(View.GONE);					
					text = "+" + edit_dic.getText().toString().substring(1);
				}
				Spannable spantext = Spannable.Factory.getInstance()
						.newSpannable(text);
				spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
						0, text.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				edit_dic.setText(spantext);
			}
		});
		edit_acr.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = "";
				if (is_edit_acr == false) {
					is_edit_acr = true;
					edit_acr_help.setVisibility(View.VISIBLE);					
					text = "-" + edit_acr.getText().toString().substring(1);
					} else {
					is_edit_acr = false;
					edit_acr_help.setVisibility(View.GONE);					
					text = "+" + edit_acr.getText().toString().substring(1);
				}
				Spannable spantext = Spannable.Factory.getInstance()
						.newSpannable(text);
				spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
						0, text.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				edit_acr.setText(spantext);
			}
		});
		edit_events.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = "";
				if (is_edit_events == false) {
					is_edit_events = true;
					edit_events_help.setVisibility(View.VISIBLE);					
					text = "-" + edit_events.getText().toString().substring(1);
				} else {
					is_edit_events = false;
					edit_events_help.setVisibility(View.GONE);					
					text = "+" + edit_events.getText().toString().substring(1);
				}
				Spannable spantext = Spannable.Factory.getInstance()
						.newSpannable(text);
				spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
						0, text.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				edit_events.setText(spantext);
			}
		});
		edit_mne.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = "";
				if (is_edit_mne == false) {
					is_edit_mne = true;
					edit_mne_help.setVisibility(View.VISIBLE);					
					text = "-" + edit_mne.getText().toString().substring(1);
				} else {
					is_edit_mne = false;
					edit_mne_help.setVisibility(View.GONE);					
					text = "+" + edit_acr.getText().toString().substring(1);
				}
				Spannable spantext = Spannable.Factory.getInstance()
						.newSpannable(text);
				spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
						0, text.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				edit_mne.setText(spantext);
			}
		});
		edit_num.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = "";
				if (is_edit_num == false) {
					is_edit_num = true;
					edit_num_help.setVisibility(View.VISIBLE);					
					text = "-" + edit_num.getText().toString().substring(1);
				} else {
					is_edit_num = false;
					edit_num_help.setVisibility(View.GONE);					
					text = "+" + edit_num.getText().toString().substring(1);
				}
				Spannable spantext = Spannable.Factory.getInstance()
						.newSpannable(text);
				spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
						0, text.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				edit_num.setText(spantext);
			}
		});
		edit_tables.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = "";
				if (is_edit_tables == false) {
					is_edit_tables = true;
					edit_tables_help.setVisibility(View.VISIBLE);					
					text = "-" + edit_acr.getText().toString().substring(1);
				} else {
					is_edit_tables = false;
					edit_tables_help.setVisibility(View.GONE);					
					text = "+" + edit_tables.getText().toString().substring(1);
				}
				Spannable spantext = Spannable.Factory.getInstance()
						.newSpannable(text);
				spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
						0, text.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				edit_tables.setText(spantext);
			}
		});
		edit_nws.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = "";
				if (is_edit_nws == false) {
					is_edit_nws = true;
					edit_nws_help.setVisibility(View.VISIBLE);					
					text = "-" + edit_acr.getText().toString().substring(1);
				} else {
					is_edit_nws = false;
					edit_nws_help.setVisibility(View.GONE);					
					text = "+" + edit_acr.getText().toString().substring(1);
				}
				Spannable spantext = Spannable.Factory.getInstance()
						.newSpannable(text);
				spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
						0, text.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				edit_nws.setText(spantext);
			}
		});
		
		//---------------------------------------------------------
		//SHOW LISTENERS:
		
		show_acr.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = "";
				if (is_show_acr == false) {
					is_show_acr = true;
					show_acr_help.setVisibility(View.VISIBLE);					
					text = "-" + show_acr.getText().toString().substring(1);
					Spannable spantext = Spannable.Factory.getInstance()
							.newSpannable(text);
					spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
							0, text.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					show_acr.setText(spantext);
				} else {
					is_show_acr = false;
					show_acr_help.setVisibility(View.GONE);					
					text = "+" + show_acr.getText().toString().substring(1);
					Spannable spantext = Spannable.Factory.getInstance()
							.newSpannable(text);
					spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
							0, text.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					show_acr.setText(spantext);
				}
			}
		});
		show_nws.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = "";
				if (is_show_nws == false) {
					is_show_nws = true;
					show_nws_help.setVisibility(View.VISIBLE);					
					text = "-" + show_nws.getText().toString().substring(1);
					Spannable spantext = Spannable.Factory.getInstance()
							.newSpannable(text);
					spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
							0, text.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					show_nws.setText(spantext);
				} else {
					is_show_nws = false;
					show_nws_help.setVisibility(View.GONE);					
					text = "+" + show_nws.getText().toString().substring(1);
					Spannable spantext = Spannable.Factory.getInstance()
							.newSpannable(text);
					spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
							0, text.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					show_nws.setText(spantext);
				}
			}
		});
		show_mne.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = "";
				if (is_show_mne == false) {
					is_show_mne = true;
					show_mne_help.setVisibility(View.VISIBLE);					
					text = "-" + show_mne.getText().toString().substring(1);
					Spannable spantext = Spannable.Factory.getInstance()
							.newSpannable(text);
					spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
							0, text.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					show_mne.setText(spantext);
				} else {
					is_show_mne = false;
					show_mne_help.setVisibility(View.GONE);					
					text = "+" + show_mne.getText().toString().substring(1);
					Spannable spantext = Spannable.Factory.getInstance()
							.newSpannable(text);
					spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
							0, text.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					show_mne.setText(spantext);
				}
			}
		});
		show_num.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = "";
				if (is_show_num == false) {
					is_show_num = true;
					show_num_help.setVisibility(View.VISIBLE);					
					text = "-" + show_num.getText().toString().substring(1);

				} else {
					is_show_num = false;
					show_num_help.setVisibility(View.GONE);					
					text = "+" + show_num.getText().toString().substring(1);
				}
				Spannable spantext = Spannable.Factory.getInstance()
						.newSpannable(text);
				spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
						0, text.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				show_num.setText(spantext);
			}
		});
		show_timeline.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = "";
				if (is_show_timeline == false) {
					is_show_timeline = true;
					show_timeline_help.setVisibility(View.VISIBLE);					
					text = "-" + show_timeline.getText().toString().substring(1);
				} else {
					is_show_timeline = false;
					show_timeline_help.setVisibility(View.GONE);					
					text = "+" + show_timeline.getText().toString().substring(1);
				}
				Spannable spantext = Spannable.Factory.getInstance()
						.newSpannable(text);
				spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
						0, text.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				show_timeline.setText(spantext);
			}
		});
		//---------------------------------------------------------
		//TOOL LISTENERS:
//		//TOOL TVs:
//		TextView tools_maj, tools_cel, tools_ana, tools_mne_gen, tools_dic;
//	    //USERS TV:
//		TextView users;
		tools_maj.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = "";
				if (is_tools_maj == false) {
					is_tools_maj = true;
					tools_maj_help.setVisibility(View.VISIBLE);					
					text = "-" + tools_maj.getText().toString().substring(1);
				} else {
					is_tools_maj = false;
					tools_maj_help.setVisibility(View.GONE);					
					text = "+" + tools_maj.getText().toString().substring(1);
				}
				Spannable spantext = Spannable.Factory.getInstance()
						.newSpannable(text);
				spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
						0, text.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				tools_maj.setText(spantext);
			}
		});
		tools_cel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = "";
				if (is_tools_cel == false) {
					is_tools_cel = true;
					tools_cel_help.setVisibility(View.VISIBLE);					
					text = "-" + tools_cel.getText().toString().substring(1);
				} else {
					is_tools_cel = false;
					tools_cel_help.setVisibility(View.GONE);					
					text = "+" + tools_cel.getText().toString().substring(1);
				}
				Spannable spantext = Spannable.Factory.getInstance()
						.newSpannable(text);
				spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
						0, text.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				tools_cel.setText(spantext);
			}
		});
		tools_ana.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = "";
				if (is_tools_ana == false) {
					is_tools_ana = true;
					tools_ana_help.setVisibility(View.VISIBLE);					
					text = "-" + tools_ana.getText().toString().substring(1);
				} else {
					is_tools_ana = false;
					tools_ana_help.setVisibility(View.GONE);					
					text = "+" + tools_ana.getText().toString().substring(1);
				}
				Spannable spantext = Spannable.Factory.getInstance()
						.newSpannable(text);
				spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
						0, text.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				tools_ana.setText(spantext);
			}
		});
		tools_mne_gen.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = "";
				if (is_tools_mne_gen == false) {
					is_tools_mne_gen = true;
					tools_mne_gen_help.setVisibility(View.VISIBLE);					
					text = "-" + tools_mne_gen.getText().toString().substring(1);
				} else {
					is_tools_mne_gen = false;
					tools_mne_gen_help.setVisibility(View.GONE);					
					text = "+" + tools_mne_gen.getText().toString().substring(1);
				}
				Spannable spantext = Spannable.Factory.getInstance()
						.newSpannable(text);
				spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
						0, text.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				tools_mne_gen.setText(spantext);
			}
		});
		tools_dic.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = "";
				if (is_tools_dic == false) {
					is_tools_dic = true;
					tools_dic_help.setVisibility(View.VISIBLE);					
					text = "-" + tools_dic.getText().toString().substring(1);
				} else {
					is_tools_dic = false;
					tools_dic_help.setVisibility(View.GONE);					
					text = "+" + tools_dic.getText().toString().substring(1);
				}
				Spannable spantext = Spannable.Factory.getInstance()
						.newSpannable(text);
				spantext.setSpan(new BackgroundColorSpan(Color.YELLOW),
						0, text.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				tools_dic.setText(spantext);
			}
		});
		users_prompt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = "";
				if (is_users == false) {
					is_users = true;
					users_help.setVisibility(View.VISIBLE);					
					text = "-" + users_prompt.getText().toString().substring(1);
				} else {
					is_users = false;
					users_help.setVisibility(View.GONE);					
					text = "+" + users_prompt.getText().toString().substring(1);
				}
				Spannable spantext = Spannable.Factory.getInstance()
						.newSpannable(text);
				spantext.setSpan(new BackgroundColorSpan(sharedPref.getInt("BUTTON Color", color.light_green)),
						0, text.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				users_prompt.setText(spantext);
			}
		});
		general_prompt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = "";
				if (is_general == false) {
					is_general = true;
					general_help.setVisibility(View.VISIBLE);					
					text = "-" + general_prompt.getText().toString().substring(1);
				} else {
					is_general = false;
					general_help.setVisibility(View.GONE);					
					text = "+" + general_prompt.getText().toString().substring(1);
				}
				Spannable spantext = Spannable.Factory.getInstance()
						.newSpannable(text);
				spantext.setSpan(new BackgroundColorSpan(sharedPref.getInt("BUTTON Color", color.light_green)),
						0, text.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				general_prompt.setText(spantext);
			}
		});
		
	}
	
	
	public void showEdit(){
		//EDIT TVs:
		edit_acr.setVisibility(View.VISIBLE);
		edit_alp.setVisibility(View.VISIBLE);
		edit_dic.setVisibility(View.VISIBLE);
		edit_events.setVisibility(View.VISIBLE);
		edit_mne.setVisibility(View.VISIBLE);
		edit_num.setVisibility(View.VISIBLE);
		edit_tables.setVisibility(View.VISIBLE);
		edit_nws.setVisibility(View.VISIBLE);
	}	
	public void hideEdit(){
		//EDIT TVs:
		edit_acr.setVisibility(View.GONE);
		edit_alp.setVisibility(View.GONE);
		edit_dic.setVisibility(View.GONE);
		edit_events.setVisibility(View.GONE);
		edit_mne.setVisibility(View.GONE);
		edit_num.setVisibility(View.GONE);
		edit_tables.setVisibility(View.GONE);
		edit_nws.setVisibility(View.GONE);
		
		//HIDE HELPS::
		edit_acr_help.setVisibility(View.GONE);
		edit_alp_help.setVisibility(View.GONE);
		edit_dic_help.setVisibility(View.GONE);
		edit_events_help.setVisibility(View.GONE);
		edit_mne_help.setVisibility(View.GONE);
		edit_num_help.setVisibility(View.GONE);
		edit_tables_help.setVisibility(View.GONE);
		edit_nws_help.setVisibility(View.GONE);;
	}
	
	public void showShow(){
		//SHOW TVs:
		show_acr.setVisibility(View.VISIBLE);
		show_nws.setVisibility(View.VISIBLE);
		show_mne.setVisibility(View.VISIBLE);
		show_num.setVisibility(View.VISIBLE);
		show_timeline.setVisibility(View.VISIBLE);
	}
	public void hideShow(){
		show_acr.setVisibility(View.GONE);
		show_nws.setVisibility(View.GONE);
		show_mne.setVisibility(View.GONE);
		show_num.setVisibility(View.GONE);
		show_timeline.setVisibility(View.GONE);
		
		//HIDE HELPS::
		show_acr_help.setVisibility(View.GONE);
		show_nws_help.setVisibility(View.GONE);
		show_mne_help.setVisibility(View.GONE);
		show_num_help.setVisibility(View.GONE);
		show_timeline_help.setVisibility(View.GONE);
	}
	
	public void showTools(){
		tools_maj.setVisibility(View.VISIBLE);
		tools_cel.setVisibility(View.VISIBLE);
		tools_ana.setVisibility(View.VISIBLE);
		tools_mne_gen.setVisibility(View.VISIBLE);
		tools_dic.setVisibility(View.VISIBLE);
	}
	public void hideTools(){
		tools_maj.setVisibility(View.GONE);
		tools_cel.setVisibility(View.GONE);
		tools_ana.setVisibility(View.GONE);
		tools_mne_gen.setVisibility(View.GONE);
		tools_dic.setVisibility(View.GONE);
		
		//HIDE HELPS::
		tools_maj_help.setVisibility(View.GONE);
		tools_cel_help.setVisibility(View.GONE);
		tools_ana_help.setVisibility(View.GONE);
		tools_mne_gen_help.setVisibility(View.GONE);
		tools_dic_help.setVisibility(View.GONE);
	}

}
