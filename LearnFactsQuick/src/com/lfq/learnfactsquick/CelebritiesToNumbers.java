package com.lfq.learnfactsquick;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CelebritiesToNumbers extends Activity{	
	private TextView celebrity_results;
	private EditText celebrities_input;
	private CheckBox check_study_celebrities,
			check_study_celebrities_abbreviated;
	private Button make_celebrities;
	
	private String text, ind, pos, spos, sspos, sav1a, sav1b, sav2a, sav2b,
			ssav2a, ssav2b;
	private String[] numsspl, names, namenums, namenumsspl, actions;
	private int len, x, rem, alt, db, dbp, last, prev, iind;

	private String onlynums;	
	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sharedPref = getSharedPreferences(
				getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		editor = sharedPref.edit();		
		setContentView(R.layout.numbers_to_celebrities);				
		setViews();
		loadButtons();
		setListeners();
		onlynums = "0123456789";
		celebrities_input.setText(sharedPref.getString("CELEBRITIES INPUT", ""));
		check_study_celebrities.setChecked(sharedPref.getBoolean("CELEBRITIES CHECK STUDY", false));
		check_study_celebrities_abbreviated.setChecked(sharedPref.getBoolean("CELEBRITIES CHECK STUDY ABBREVIATED", false));
		make_celebrities.performClick();
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
	
	@Override
	protected void onPause(){
		super.onPause();
		saveChanges();		
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();		
		saveChanges();
	}
	
	@Override
	public void onBackPressed(){
		saveChanges();
		super.onBackPressed();
	}
	
	public void saveChanges() {	
		editor.putString("CELEBRITIES INPUT", celebrities_input.getText().toString());
		editor.putBoolean("CELEBRITIES CHECK STUDY", check_study_celebrities.isChecked());
		editor.putBoolean("CELEBRITIES CHECK STUDY ABBREVIATED", check_study_celebrities_abbreviated.isChecked());
		editor.commit();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		saveChanges();
		super.onConfigurationChanged(newConfig);
	}

	public void setViews() {		
		setTitle("CELEBRITY NUMBER MATCH");		
		celebrities_input = (EditText) findViewById(R.id.celebrities_input);
		make_celebrities = (Button) findViewById(R.id.make_celebrities);
		check_study_celebrities = (CheckBox) findViewById(R.id.check_study_celebrities);
		check_study_celebrities_abbreviated = (CheckBox) findViewById(R.id.check_study_celebrities_abbreviated);		
		celebrity_results = (TextView) findViewById(R.id.celebrity_results);		
	}
	public void loadButtons() {	
			make_celebrities.setBackgroundResource(sharedPref.getInt("BG Button", R.drawable.button));
	}

	public void setListeners() {
		namenums = new String[] {
				"Antonio>(Agitating, an Iraqi actor, was Prince Tariq in 2005 movie Dreamer Inspired by a True Story, jailed in Iraq war)>Albadran>(Arabs):Agitating>(Antonio)>Arabs>(Albadran)",
				"Anne>(Abandoning)>Boleyn>(Babies):Abandoning>(Anne)>Babies>(Boleyn)",
				"Alice>(Acting)>Cooper>(Creepy):Acting>(Alice)>Creepy>(Cooper)",
				"Albert>(Articulation, an actor, artist, producer, poet and rapper, born in Bronx, is black and Puerto Rican)>Daniels>(Drumming):Articulation>(Albert)>Drumming>(rapping, Daniels)",
				"Albert>(Applying)>Einstein>(Equations):Applying>(Albert)>Equations>(Einstein)",
				"Aretha-Louise>(Aloud,named for 2 aunts, born in Memphis, TN, 3rd of 4 children)>Franklin>(Fluctuating):Aloud>(Aretha)>Fluctuating>(Franklin)",
				"Ava>(Around)>Gardner>(Gables):Around>(Ava, born in NC , youngest of 7 children, famous for The Killer, in The Hucksters with Clark Gable, in The Barefoot Contessa with Humphrey Bogart)>Gables>(Gardner)",
				"Audrey>(Amsterdam, born in Brussels, Belgium, studied ballet, a fashion icon, died from appendiceal cancer)>Hepburn>(Hopping):Amsterdam>(Audrey)>Hopping>(Hepburn)",
				"Akinori>(Air, is an infielder now with the Tohoku Rakuten Golden Eagles of the Japanese Pacific League, was with the Tampa Rays)>Iwamura-Jr>(Impelling):Air>(Akinori)>Impelling>(Iwamura)",
				"Angelina>(Acting)>Jolie>(Judiciously):Acting>(Angelina)>Judiciously>(Jolie)",
				"Blythe>(Beaten, born in 1985 in New York, played in Girl Next Door, about girl beaten to death by aunt)>Auffarth>(Around):Beaten>(Blythe)>Around>(Auffarth)",
				"Bobbi>(Busting, born in NC was Kiki in sitcom House of Payne)>Baker>(Blacks, wrote thesis on how black preaching is not black culture):Busting>(Bobbi, in movie Madea Goes to Jail)>Blacks>(Baker)",
				"Bill>(Bathing)>Clinton>(Calvins):Bathing>(Bill)>Calvins>(Clinton)",
				"Bette>(Bearing)>Davis>(Deterministically, payed unsympathetic characters):Bearing>(Bette, had breast cancer)>Deterministically>(Davis)",
				"Bill>(Baseball)>George Evans>(Evaluator):Baseball>(Bill, was youngest umpire at 22 in 1906)>Evaluator>(Bill Evans)",
				"Benjamin>(Building)>Franklin>(Furnaces):Building>(Benjamin)>Furnaces>(Franklin)",
				"Bill>(Broadcasting)>Gates>(Gigabytes):Broadcasting>(Bill)>Gigabytes>(Gates)",
				"Buddy>(Buoyantly)>Holly>(Halting):Buoyantly>(Buddy, died in plane crash 1 and a half years after performing)>Halting>(Holly)",
				"Burl>(Burly, 1909-1995 a US actor, folk music singer, comedian)>Ives>(Inhaling):Burly>(died of oral cancer from cigars at age 86)>Inhaling>(Ives)",
				"Bjorn>(Bibliographying)>Johnson>(Jousiting):Bibliographying>(Bjorn, Choreographs fighting scenes)>Jousting>(Johnson)",
				"Camille>(Crashing)>Anderson>(Assemblages):Crashing>(Camille, was in movie Wedding Crashers with Owen Wilson)>Assemblages>(Anderson)",
				"Coby>(Catching)>Bryant>(Basketballs):Catching>(Coby)>Basketballs>(Bryant)",
				"Christopher>(Cruising)>Colombus>(Carribean):Cruising>(Christopher)>Carribean>(Colombus)",
				"Charles>(Checking)>Dickens>(Development):Checking>(Charles)>Development>(Dickens)",
				"Clint>(Cigar)>Eastwood>(Eating):Cigar>(Clint)>Eating>(Eastwood)",
				"Corey>(Colloseums)>Fischer>(Flying):Colloseums>(Corey, as officer Hines in Brewster McCloud)>Flying>(Fischer)",
				"Clark>(Chasing)>Gable>(Gales):Chasing>(Clark, in movie Gone with the Wind)>Gales>(Gable)",
				"Curt>(Chess)>Hansen>(Hurtling):Chess>(Curt, 2nd best Danish chess champion)>Hurlting>(Hansen)",
				"Chris>(Chatting)>Isaac>(Infatuations):Chatting>(Chris, has own talk show for 4 years 2001-2004)>Infatuations>(Isaak)",
				"Christopher>(Conducting)>Jones>(Journeys):Conducting>(Christopher, was captain of the Mayflower)>Journeys>(Jones)",
				"Dan>(Drumming)>Akyroid>(Amusement):Drumming>(Dan)>Amusment>(Akyroyd)",
				"David>(Dramatizing)>Burns>(Boogeying):Dramatizing>(David, played in Music Man 1959)>Boogying>(Burns)",
				"Davy>(Deer)>Crocket>(Catching):Deer>(Davy, died in Alamo, rejected A Jacksons Indian removal, was Tenessee HR Rep)>Catching>(Crocket)",
				"Danny>(Despoiling)>Devito>(Deniro):Despoiling>(Danny)>Deniro>(Devito)",
				"Duke>(Debonairly)>Ellington>(Ensembling):Debonairly>(Duke)>Ensembling>(Ellington)",
				"Don>(Delivering, was a boxer, beat Tank Abbot, Ken Shamrock, Gary Goodridge, and Gilbert Yvel)>Frye>(Fights):Delivering>(Don)>Fights>(Frye)",
				"Danny>(Decidedly)>Glover>(Geriatric):Decidedly>(Danny)>Geriatric>(Glover, relating to the old)",
				"David>(Driving)>Hasselhoff>(Hastily):Driving>(David, Knight Rider)>Hastily>(Hasselhoff)",
				"Daryl>(Discharging)>Irvine>(Inflammation):Discharging>(Daryl, was a replacement pitcher for the Red Sox)>Inflammation>(Irvine)",
				"Dwane>(Desintegrating)>Johnson>(Jaggeds):Desintegrating>(Dwane)>Jaggeds>(Johnson)",
				"Eva>(Educating)>Arden>(Administration):Educating>(Eva, was Rydell school principal in Grease I and II)>Administration>(Arden)",
				"Eric>(Engineering)>Bana>(Barbarians):Engineering>(was the Hulk directed by Ang Lee in 2003)>Barbarians>(Bana)",
				"Eric>(Electrifying)>Clapton>(Cream):Electrifying>(Eric, Brittish played in Yardbirds and Cream)>Cream>(Clapton)",
				"Ellen>(Engaging)>Degeneres>(Dames):Engaging>(Ellen)>Dames>(Degeneres)",
				"Emilio>(Eying)>Estevez>(Eagles):Eying>(Emilio)>Eagles>(Estevez)",
				"Ella>(Expanding)>Fitzgerald>(Frequencies):Expanding>(Ella, had vocal range that spanned 3 frequencies)>Frequencies>(Fitzgerald)",
				"Evan>(Entertaining, made TV show with Terry Bradshaw)>Golden>(Giants):Entertaining>(Evan, producer of Today in America with Terry Bradshaw)>Giants>(Golden)",
				"Eddie-Van>(Electrifying)>Halen>(Holland):Electrifying>(Eddie, born in Holland)>Holland>(Halen)",
				"Ed>(Eyra, a tropical wildcat, he replaced Ty Cobbs of Detroit Tigers)>Irvin>(Interchanging):Eyra>(long-bodied long-tailed tropical US wildcat, Ed, replaced Ty Cobbs of Detroit Tigers)>Interchanging>(Irvin)",
				"Elizabeth>(Exemplifying, model)>Jagger>(Jaggedness, daughter of McJagger)):Exemplifying>(Elizabeth, model and daughter of Mick Jagger born in 1984)>Jaggedness>(Jagger)",
				"Fred>(Forever, 76 yrs in Broadway)>Astaire>(Acting):Forever>(Fred, a Broadway actor for 76 years)>Acting>(Astaire)",
				"Floyd>(Feigning)>Buckley>(Boatswains):Feigning>(Floyd,voice popeye on radio)>Boatswains>(Buckley, officer on a merchant ship who controls seamen)",
				"Frank>(Frankly 1901-1960)>Cooper>(Championing):Frankly>(Frank, 1901-1961 known to play strong silent heroes)>Championing>(Cooper)",
				"Frank>(Formerly)>Delfino>(Dwarfing):Formerly>(Frank, was in Planet of Apes, Willy Wonka, The Odd Couple with wife was stand in on Brady Bunch, he was a midget)>Dwarfing>(Delfino)",
				"Femi>(Flashing, in If Looks Could Kill)>Emiola>(Evil):Flashing>(Femi, in If Looks Could Kill, promotion for Toyota Camrys, has Filipinan mother and Nigerian father, both chemists)>Evil>(Emiola)",
				"Farrah>(Fabulously, in Charlie's Angels, advertised hair products)>Fawcett>(Fluffy):Fabulously>(Farrah)>Fluffy>(Fawcett)",
				"Francis>(Fixing)>Galton>(Genomes):Fixing>(Francis, was cousin of Charles Darwin, est eugenics or selective breeding esp of humans)>Genomes>(Galton)",
				"Frankie>(Funny, a gay UK comic in Sgt Peppers movie)>Howerd>(Homosexually):Funny>(Frankie, a gay UK comic man in Sgt Peppers Lonely Hearts Club band movie)>Homosexually>(Howerd, name means to be different)",
				"Frankie>(Following, of Sicilian, Italian origin, on many TV shows)>Ingrassia>(Italians):Following>(Frankie, of Sicilian Italian origin, on many TV shows)>Italians>(Ingrassia)",
				"Fran>(Frollicking, in 1963 Pink Panther movie)>Jeffries>(Jaguars):Frollicking>(Fran, was in 1963 Pink Panther movie)>Jaguars>(Jeffries)",
				"Gillian>(Grovelling, Scully in X files)>Anderson>(Arcane):Grovelling>(Gillian, was agent Scully in X-files)>Arcane>(Anderson)",
				"George>(Governing)>Bush>(Bullishly):Governing>(George)>Bullishly>(Bush)",
				"Gary>(Grueling, did over 100 movies)>Cooper>(Cowboys):Grueling>(Gary, did over 100 movies)>Cowboys>(Cooper)",
				"Glenn>(Gold, won 3 gold medals in 1956-1960, coached by Larry Snyder who coached Jesse Owens)>Davis>(Dashing):Gold>(Glenn, won 3 gold medals in 1956-1960, coached by Larry Snyder who also coached Jesse Owens)>Dashing>(Davis)",
				"George>(Guarding)>Eliot>(Estrogen):Guarding>(George, really shes called Mary, wrote Middlemarch)>Estrogen>(Eliot)",
				"Gustave>(Guilessly)>Flaubert>(Fornicating):Guilessly>(Gustave, a moustache Frenchman writer who never married but died at age 58 from venereal disease)>Fornicating>(Flaubert)",
				"Galileo>(Guessing)>Galilee>(Geophysics):Guessing>(Galileo)>Geophysics>(Galilee)",
				"Goldie>(Guiding)>Hawn>(Heterodox):Guiding>(Goldie, is a Jewish Buddhist, father descendant of Edward Rutelage, youngest signer of Dec of Ind)>Heterodox>(Hawn)",
				"Garth>(Guarding, 3rd baseman of Blue Jays, born in Arcata CA)>Iorg>(Isoceles):Guarding>(Garth, 3rd baseman of Toronto Blue Jays, born in Arcata, CA)>Isoceles>(Iorg=3)",
				"Gerry>(Gorilla, voice of Betty Rubble)>Johnson>(Jawing):Gorilla>(Gerry, was voice of Betty Rubble in final seasons of Flinstones)>Jawing>(Jerry)",
				"Hank>(Hersheys)>Aaron>(Astounding):Hersheys>(Hank, beat Babe Ruths record with 755 home runs, playing for Braves and Brewers)>Astounding>(Aaron)",
				"Humphrey>(Humans, in Cassablanca)>Bogart>(Bettering):Humans>(Humphrey, hero in Casablanca, city in Morocco 1942 Vichy France)>Bettering>(Bogart)",
				"Harriet>(Hydrating)>Creighton>(Crossovers):Hydrating>(Harriet, a botanist with Barbara McClintock discovered genetics of chromosomes)>Crossovers>(Creighton)",
				"Hugh>(Hastened, footballer of Scotland, retired in early 30s)>Davidson>(Dropkicks):Hastened>(Hugh, footballer of Scotland, retired)>Dropkicks>(Davidson)",
				"Hunter>(Horse, was QB for Cowboys, Chargers, Raiders and Broncos in early 60s)>Enis>(Ejecting):Horse>(Hunter, was QB for Dallas Texans in 60, Chargers in 61, Raiders in 62, and Broncos in 62)>Ejecting>(Enis)",
				"Harrison>(Hunting)>Ford>(Freaks):Hunting>(Harrison)>Freaks>(Ford)",
				"Humphrey>(Homed-in, UK navigator in 1583 found 1st UK colony in Newfoundland)>Gilbert>(Great Britain):Homed-in>(Humphrey, English navigator in 1583 set up 1st English colony in Newfoundland)>GreatBritain>(Gilbert)",
				"Heinrich>(Harmonicizing)>Hertz>(Hertz 1857-1894):Harmonicizing>(Heinrich, German physicist, 1st to make artificial electromagnetic waves 1857-1894)>Hertz>(Hertz)",
				"Henrik>(Honestly, Norwegian playwright, 2nd best to Shakespeare)>Ibsen>(Inscribing):Honestly>(Henrik, a Norwegian playwright, 2nd in ability to Shakespear)>Inscribing>(Ibsen)",
				"Herrick>(Hypothermically, cryogenic worker, discovered heavy isotopes of oxygen)>Johnston>(Joinging):Hypothermically>(Herrick, US man who founded heavy isotopes of oxygen and worked hard in cryogenics)>Joining>(Johnston)",
				"Ivy>(Intoxicating, pitcher for the Yankees in 31-38,Red Sox 32-33,Browns 34-36, Indians 47)>Andrews>(Arms):Intoxicating>(Ivy, a pitcher called poison ivy for the Yankees in 31-38,Red Sox 32-33,Browns 34-36, Indians 47)>Arms>(Andrews)",
				"Ibn>(Insistently)>Battuta>(Bestriding):Insistently>(Ibn)>Bestriding>(Battuta)",
				"Isis>(Impersonated, played younger Whoopi in SIster Act)>Carmen>(Jones, Choirists):Impersonated>(Isis, played younger version of Whoopi Goldberg in Sister Act, and as young Guinan in Star Trek epsisode, Rascals)>Choirist>(Carmen)",
				"Irene>(Immorally, played in silent films, violated Mann Act with Ray Owens)>Dalton>(Died):Immoraly>(Irene, was a US actress in silent films, violated Mann act by prostitution with Ray Owens, millionaire son of Mike Owens inventor of bottle machine, she died suddenly at 33 yrs age)>Died>(Dalton)",
				"Ike>(Frederick George, Indigenously, was outfielder 4 seasons for Indians in 25-27, and WHite Sox in 31)>Eichrodt>(Elemental):Indigenously>(Ike, played outfielder 4 seasons for Indians 25-27 and White Sox in 31)>Elemental>Eichrodt",
				"Ira>(James Pete, Impacting, played 13 seasons as batter and outfielder in American and NL with Tigers, 17-23, Red Sox 23-29, Washington Senators 29, and Pirates 29-30 with a high bat average and number of runs and catches)>Flagstead>(Frontier):Impacting>(Ira, played 13 seasons as batter and outfielder in American and NL with Tigers, 17-23, Red Sox 23-29, Washington Senators 29, and Pirates 29-30 with a high bat average and number of runs and catches)>Frontier>(Flagstead)",
				"Indira>(Isoceles, was 3rd PM of India, only child of Jawaharlal Nehru, assassinated after Op Blue against Amritsar Sikhs)>Gandhi>(Garroted):Isoceles>(was 3rd PM of India, only child of Jawaharlal Nehru, assassinated after Op Blue against Amritsar Sikhs)>Garroted>(Gandhi)",
				"Ike>(Inverting, a pinch hitter or replacement batter for Mets in 74 then traded to CA Angels 75-79, had a strong arm and .207 bat aver)>Hampton>(Heavily):Inverting>(Ike, was a pinch hitter or replacement batter for Mets in 74 then traded to CA Angels 75-79, had a strong arm and .207 bat average)>Heavily>(Hampton)",
				"Isabel>(Impersonating, a US actress who played the Schoolmistress, retired in 36 and married Will H Thompson until he died in 23, made 33 productions in 50 yrs)>Irving>(Intellectuals):Impersonating>(Isabel, a US actress who played the Schoolmistress, retired in 36 and married Will H Thompson until he died in 23, made 33 productions in 50 yrs)>Intellectuals>(Irving)",
				"Ivyann-Schwan>(Immaturely, Ivyann-Schwan, was in Miracle on 34th, Sound of Music, Parenthood, Problem Child 2, and Bill Nye Science Guy, JC Pennys and Kellogs commercials)>Jones>(Jingling):Immaturely>(Ivyann-Schwan, was in Miracle on 34th, Sound of Music, Parenthood, Problem Child 2, and Bill Nye Science Guy, JC Pennys and Kellogs commercials)>Jingling>(Jones)",
				"Jane>(Jotting, a writer 1775-1810 who wrote novels about landed gentry and womens dependence to man, posthumously famous)>Austen>(Actualities):Jotting>(Jane, a writer 1775-1810 who wrote novels about landed gentry and womens dependence to man, posthumously famous)>Actualities>(Austen)",
				"Jack>(Jerking, Jack 1894-1974, a Jewish violinist comedian known for his timing, played on CBS radio)>Benny>(Bowstrings):Jerking>(Jack 1894-1974, a Jewish violinist comedian known for his timing, played on CBS radio)>Bowstrings>(Benny)",
				"James>(Journeying)>Cook>(Ceaselessly):Journeying>(James)>Ceaselessly>(Cook)",
				"James>(Jockeying, was in John Steinbecks East of Eden and Rebel Without a Cause, died in a car crash age 24)>Dean>(Destruction):Jockeying>(James, was in John Steinbecks East of Eden and Rebel Without a Cause, died in a car crash age 24)>Destruction>(Dean)",
				"Jacob>(Junoesqueing, suggestive of a statue, 1880-1959 English born in US noted for busts and large controversial works)>Epstein>(Exotically):Junoesqueing>(suggestive of a statue, Jacob, 1880-1959 English born in US noted for busts and large controversial works)>Exotically>(Epstein)",
				"Jane>(Judging, actor, writer, fitness instructor, descendant of Lady Jane Seymore, Henry VIIIs 3rd wife)>Fonda>(Fitness):Judging>(Jane, actor, writer, fitness instructor, descendant of Lady Jane Seymore, Henry VIIIs 3rd wife)>Fitness>(Fonda)",
				"Judy>(Jadedly, born Frances Ethel Gumm, was in Wiz of Oz and Judgement at Nuremberg, had 5 marriages, execs told her she was unattractive and she owed 100000s in back taxes so she was an alcoholic and overdosed on drugs at age 47)>Garland>(Gone):Jadedly>(Judy, born Frances Ethel Gumm, was in Wiz of Oz and Judgement at Nuremberg, had 5 marriages, execs told her she was unattractive and she owed 100000s in back taxes so she was an alcoholic and overdosed on drugs at age 47)>Gone>(Garland)",
				"Jimmy>(Jamming)>Hendrix>(Hippocritically):Jamming>(Jimmy)>Hippocritically>(Hendrix)",
				"Judas>(Jesus)>Iscariot>(Insidiating or lie in ambush for):Jesus>(Judas)>Insidiating>(lie in ambush for, Iscariot)",
				"Jon-Bon>(Journeying)>Jovi>(Jaggedly):Journeying>(Jon)>Jaggedly>(Jovi)" };
		make_celebrities.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				celebrity_results.setText("");
				text = "";
				String str_nums = celebrities_input.getText().toString();
				numsspl = explode(str_nums);
				if (!check_study_celebrities.isChecked()) {
					text = "FOR NUMBER";
				}
				if (check_study_celebrities.isChecked()) {
					str_nums = "1111121213131414151516161717181819191010212122222323242425252626272728282929202031313232333334343535363637373838393930304141424243434444454546464747484849494040515152525353545455555656575758585959505061616262636364646565666667676868696960607171727273737474757576767777787879797070818182828383848485858686878788888989808091919292939394949595969697979898999990900101020203030404050506060707080809090000";
					numsspl = explode(str_nums);
					text = "STUDY LIST";
				}
				if (check_study_celebrities_abbreviated.isChecked()) {
					text += "(ABBREVIATED)";
				}
				int ct = 1, ct5 = 1, ct4 = 1;
				len = numsspl.length;
				text += ": ";
				if (!check_study_celebrities.isChecked()) {
					for (int i = 0; i < len; i++) {
						text += numsspl[i];
						if (ct == 4 && i != (len - 1)) {
							text += " - ";
							ct = 0;
						}
						ct++;
					}
				}
				text += "\n";

				// TEST IF INPUT IS NOT A LIST OF NUMBERS
				for (int i = 0; i < len; i++) {
					if (!onlynums.contains(numsspl[i])) {
						celebrity_results.setText("MUST ENTER ONLY NUMBERS.");
						return;
					}
				}

				rem = (int) (len - (4 * Math.floor(len / 4)));
				ct = 1;
				for (int i = 0; i < len; i++) {
					if (ct == 1) {
						text += ct4 + ": ";
					}
					text += numsspl[i];
					if ((rem == 1 || rem == 3) && i == (len - 2)) {
						text += numsspl[i];
					}
					if (ct == 4 && i != (len - 1)
							&& !(rem == 1 && i == (len - 2))
							|| (rem == 2 && i >= (len - 3))) {
						text += "  ";
						ct = 0;
						ct4++;
						if (ct5 == 5) {
							text += "\n";
							ct5 = 0;
						}
						ct5++;
					}
					ct++;
				}
				text += "\n\n";

				// BEGIN LOOPS

				int rem2 = (int) (len - (2 * Math.floor(len / 2)));
				x = (len / 2) + rem2;
				int ctprt = 1;
				if (rem == 0) {
					iind = 0;
					alt = 0;
					for (int i = 0; i < x; i++)// for every 2 numbers, x=len/2
					{
						ind = "";// indicator
						pos = "";// position
						db = i * 2;
						dbp = (i * 2) + 1;
						pos = pos.concat(numsspl[db]);
						pos = pos.concat(numsspl[dbp]);

						if (!numsspl[db].equals("0")
								&& !numsspl[db].equals("1")) {
							iind = Integer.parseInt(numsspl[db]) - 1;
							ind = ind.concat(String.valueOf(iind));

						}
						if (numsspl[db].equals("0")) {
							ind = ind.concat("9");
						}
						if (!numsspl[dbp].equals("0")) {
							iind = Integer.parseInt(numsspl[dbp]) - 1;
							ind = ind.concat(String.valueOf(iind));
						}
						if (numsspl[dbp].equals("0")) {
							ind = ind.concat("9");
						}

						namenumsspl = namenums[Integer.parseInt(ind)]
								.split(":");
						if (alt == 0) {
							names = namenumsspl[0].split(">");
							if (check_study_celebrities_abbreviated.isChecked()) {
								sav1a = names[0];
								sav1b = names[2];
							}
							if (!check_study_celebrities_abbreviated
									.isChecked()) {
								sav1a = names[0].concat(names[1]);
								sav1b = names[2].concat(names[3]);
							}
							spos = pos;
							alt = 1;
							continue;
						}
						if (alt == 1) {
							actions = namenumsspl[1].split(">");
							if (check_study_celebrities_abbreviated.isChecked()) {
								sav2a = actions[0];
								sav2b = actions[2];
								text += ctprt + ". " + spos + pos + ":" + "\n"
										+ sav1a + " " + sav1b + " " + sav2a
										+ " " + sav2b + "\n\n";
								ctprt++;
							}
							if (!check_study_celebrities_abbreviated
									.isChecked()) {
								sav2a = actions[0].concat(actions[1]);
								sav2b = actions[2].concat(actions[3]);
								text += ctprt + ". " + spos + pos + ":" + "\n"
										+ sav1a + "\n" + sav1b + "\n" + sav2a
										+ "\n" + sav2b + "\n\n";
								ctprt++;
							}
							alt = 0;
							continue;
						}
					}// end for loop
				}// end if rem is 0

				if (rem == 1 || rem == 3) {
					last = x - 1;
					prev = x - 2;
					alt = 0;
					for (int i = 0; i < x; i++) {
						ind = "";
						pos = "";
						if (i != last) {
							db = i * 2;
							dbp = (i * 2) + 1;
						}
						if (i == last) {
							db = len - 2;
							dbp = len - 1;
						}
						pos = pos.concat(numsspl[db]);
						pos = pos.concat(numsspl[dbp]);
						if (!numsspl[db].equals("0")
								&& !numsspl[db].equals("1")) {
							iind = Integer.parseInt(numsspl[db]) - 1;
							ind = ind.concat(String.valueOf(iind));

						}
						if (numsspl[db].equals("0")) {
							ind = ind.concat("9");
						}
						if (!numsspl[dbp].equals("0")) {
							iind = Integer.parseInt(numsspl[dbp]) - 1;
							ind = ind.concat(String.valueOf(iind));
						}
						if (numsspl[dbp].equals("0")) {
							ind = ind.concat("9");
						}

						namenumsspl = namenums[Integer.parseInt(ind)]
								.split(":");
						if (i < last) {
							if (alt == 0) {
								names = namenumsspl[0].split(">");
								if (check_study_celebrities_abbreviated
										.isChecked()) {
									sav1a = names[0];
									sav1b = names[2];
								}
								if (!check_study_celebrities_abbreviated
										.isChecked()) {
									sav1a = names[0].concat(names[1]);
									sav1b = names[2].concat(names[3]);
								}
								spos = pos;
								alt = 1;
								continue;
							}
							if (alt == 1) {
								actions = namenumsspl[1].split(">");
								if (check_study_celebrities_abbreviated
										.isChecked()) {
									sav2a = actions[0];
									sav2b = actions[2];
								}
								if (!check_study_celebrities_abbreviated
										.isChecked()) {
									sav2a = actions[0].concat(actions[1]);
									sav2b = actions[2].concat(actions[3]);
								}
								if (i != prev) {
									if (check_study_celebrities_abbreviated
											.isChecked()) {
										text += ctprt + ". " + spos + pos + ":"
												+ "\n" + sav1a + " " + sav1b
												+ " " + sav2a + " " + sav2b
												+ "\n\n";
										ctprt++;

									}
									if (!check_study_celebrities_abbreviated
											.isChecked()) {

										text += ctprt + ". " + spos + pos + ":"
												+ "\n" + sav1a + "\n" + sav1b
												+ "\n" + sav2a + "\n" + sav2b
												+ "\n\n";
										ctprt++;

									}
								}
								if (i == prev) {
									actions = namenumsspl[1].split(">");
									if (check_study_celebrities_abbreviated
											.isChecked()) {
										ssav2a = actions[0];
										ssav2b = actions[2];
									}
									if (!check_study_celebrities_abbreviated
											.isChecked()) {
										ssav2a = actions[0].concat(actions[1]);
										ssav2b = actions[2].concat(actions[3]);
									}
									sspos = pos;
								}
								alt = 0;
								continue;
							}// end alt==1
						}// end if i<last
						if (i == last && rem == 3) {
							actions = namenumsspl[1].split(">");
							if (check_study_celebrities_abbreviated.isChecked()) {
								sav2a = actions[0];
								sav2b = actions[2];
								text += ctprt + ". " + spos + pos + ":" + "\n"
										+ sav1a + " " + sav1b + " " + sav2a
										+ " " + sav2b + "\n\n";
								ctprt++;

							}
							if (!check_study_celebrities_abbreviated
									.isChecked()) {
								sav2a = actions[0].concat(actions[1]);
								sav2b = actions[2].concat(actions[3]);
								text += ctprt + ". " + spos + pos + ":" + "\n"
										+ sav1a + "\n" + sav1b + "\n" + sav2a
										+ "\n" + sav2b + "\n\n";
								ctprt++;
							}
						}
						if (i == last && rem == 1) {
							actions = namenumsspl[1].split(">");
							if (check_study_celebrities_abbreviated.isChecked()) {
								sav2a = actions[0];
								sav2b = actions[2];
								text += ctprt + ". " + spos + sspos + pos + ":"
										+ "\n" + sav1a + " " + sav1b + " "
										+ ssav2a + " " + ssav2b + " " + sav2a
										+ " " + sav2b + "\n\n";
								ctprt++;
							}
							if (!check_study_celebrities_abbreviated
									.isChecked()) {
								sav2a = actions[0].concat(actions[1]);
								sav2b = actions[2].concat(actions[3]);

								text += ctprt + ". " + spos + sspos + pos + ":"
										+ "\n" + sav1a + "\n" + sav1b + "\n"
										+ ssav2a + "\n" + ssav2b + "\n" + sav2a
										+ "\n" + sav2b + "\n\n";
								ctprt++;

							}
						}// end if ==last && rem==1

					}// end for loop

				}// end if remainder is 1 or 3

				if (rem == 2) {
					alt = 0;
					for (int i = 0; i < x; i++) {
						last = x - 1;
						prev = x - 2;
						ind = "";
						pos = "";
						db = i * 2;
						dbp = (i * 2) + 1;
						pos = pos.concat(numsspl[db]);
						pos = pos.concat(numsspl[dbp]);
						if (!numsspl[db].equals("0")
								&& !numsspl[db].equals("1")) {
							iind = Integer.parseInt(numsspl[db]) - 1;
							ind = ind.concat(String.valueOf(iind));

						}
						if (numsspl[db].equals("0")) {
							ind = ind.concat("9");
						}
						if (!numsspl[dbp].equals("0")) {
							iind = Integer.parseInt(numsspl[dbp]) - 1;
							ind = ind.concat(String.valueOf(iind));
						}
						if (numsspl[dbp].equals("0")) {
							ind = ind.concat("9");
						}
						namenumsspl = namenums[Integer.parseInt(ind)]
								.split(":");

						if (i < last && alt == 0) {
							spos = pos;
							names = namenumsspl[0].split(">");
							if (check_study_celebrities_abbreviated.isChecked()) {
								sav1a = names[0];
								sav1b = names[2];
							}
							if (!check_study_celebrities_abbreviated
									.isChecked()) {
								sav1a = names[0].concat(names[1]);
								sav1b = names[2].concat(names[3]);
							}
							alt = 1;
							continue;
						}
						if (i < last && alt == 1) {
							alt = 0;
							actions = namenumsspl[1].split(">");
							if (check_study_celebrities_abbreviated.isChecked()) {
								sav2a = actions[0];
								sav2b = actions[2];
							}
							if (!check_study_celebrities_abbreviated
									.isChecked()) {
								sav2a = actions[0].concat(actions[1]);
								sav2b = actions[2].concat(actions[3]);
							}
							if (i != prev) {
								if (check_study_celebrities_abbreviated
										.isChecked()) {
									text += ctprt + ". " + spos + pos + ":"
											+ "\n" + sav1a + " " + sav1b + " "
											+ sav2a + " " + sav2b + "\n\n";
									ctprt++;
								}
								if (!check_study_celebrities_abbreviated
										.isChecked()) {
									text += ctprt + ". " + spos + pos + ":"
											+ "\n" + sav1a + "\n" + sav1b
											+ "\n" + sav2a + "\n" + sav2b
											+ "\n\n";
									ctprt++;
								}
							}
							if (i == prev) {
								actions = namenumsspl[1].split(">");
								if (check_study_celebrities_abbreviated
										.isChecked()) {
									ssav2a = actions[0];
									ssav2b = actions[2];
								}
								if (!check_study_celebrities_abbreviated
										.isChecked()) {
									ssav2a = actions[0].concat(actions[1]);
									ssav2b = actions[2].concat(actions[3]);
								}
								sspos = pos;
							}
							continue;
						}
						if (i == last) {
							actions = namenumsspl[1].split(">");
							if (check_study_celebrities_abbreviated.isChecked()) {
								sav2a = actions[0];
								sav2b = actions[2];
								text += ctprt + ". " + spos + sspos + pos + ":"
										+ "\n" + sav1a + " " + sav1b + " "
										+ ssav2a + " " + ssav2b + " " + sav2a
										+ " " + sav2b + "\n\n";
								ctprt++;
							}
							if (!check_study_celebrities_abbreviated
									.isChecked()) {
								sav2a = actions[0].concat(actions[1]);
								sav2b = actions[2].concat(actions[3]);
								text += ctprt + ". " + spos + sspos + pos + ":"
										+ "\n" + sav1a + "\n" + sav1b + "\n"
										+ ssav2a + "\n" + ssav2b + "\n" + sav2a
										+ "\n" + sav2b + "\n\n";
								ctprt++;
							}
						}// end if i==last
					}// end loop
				}// end if rem is 2

				celebrity_results.setText(text);
			}// end method in make_celebrities listener
		});// end onclick in make_celebrities listener

	}

	String[] explode(String str) {
		String[] arr = new String[str.length()];
		for (int i = 0; i < str.length(); i++) {
			arr[i] = String.valueOf(str.charAt(i));
		}
		return arr;
	}

	int[] explodeInt(String str) {
		int[] arr = new int[str.length()];
		String[] str_arr = explode(str);
		for (int i = 0; i < str.length(); i++) {
			arr[i] = Integer.parseInt(str_arr[i]);
		}
		return arr;
	}

	String[] explode(String str, String del) {
		int count = 1;
		String[] strspl = explode(str);
		for (int i = 0; i < str.length(); i++) {
			if (strspl[i].equals(del)) {
				count++;
			}
		}
		String[] arr = new String[count];
		String str_elem = "";
		count = 0;
		for (int j = 0; j < str.length(); j++) {
			if (strspl[j].equals(del)) {
				str_elem += strspl[j];
			} else {
				arr[count] = str_elem;
				str_elem = "";
			}
		}
		count++;
		return arr;
	}
	

}