package com.lfq.learnfactsquick;

public class Constants {
	static public class tables{
		static public String acrostics = "acrostics";
		static public String alphabet = "alphabet";
		static public String alphabet_tables = "alphabet_tables";
		static public String dictionarya = "dictionarya";
		static public String events_table = "events_table";
		static public String global_number_table = "global_number_table";
		static public String mnemonics = "mnemonics";
		static public String user_events_table = "user_events_table";
		static public String user_new_words = "user_new_words";
		static public String user_numbers_table = "user_numbers_table";
		static public String user_review_times = "user_review_times";
		static public String sync_table = "sync_table";
	}
	static public class cols{
		static public class acrostics{
			static public String _id = "_id";
			static public String Name = "Name";
			static public String Information = "Information";
			static public String Acrostics = "Acrostics";
			static public String Mnemonics = "Mnemonics";
			static public String Peglist = "Peglist";
			static public String Image = "Image";
			static public String Has_Image = "Has_Image";
		}		
		static public class alphabet{
			static public String _id = "_id";
			static public String Entry = "Entry";
			static public String Letter = "Letter";
			static public String Table_name = "Table_name";
		}
		static public class alphabet_tables{
			static public String _id = "_id";
			static public String Category = "Category";
			static public String Table_name = "Table_name";
		}
		static public class dictionarya{
			static public String _id = "_id";
			static public String Word = "Word";
			static public String PartSpeech = "PartSpeech";
			static public String Definition = "Definition";
			static public String Number = "Number";			
		}
		static public class events_table{
			static public String _id = "_id";
			static public String Year = "Year";			
			static public String Date = "Date";
			static public String Event = "Event";
			static public String Acrostics = "Acrostics";
			static public String SaveWords = "SaveWords";
			static public String Type = "Type";
		}		
		static public class global_number_table{
			static public String _id = "_id";
			static public String Number = "Number";
			static public String NumInf = "NumInf";
			static public String NumWors = "NumWors";
			static public String Type = "Type";
		}		
		static public class mnemonics{
			static public String _id = "_id";
			static public String  Category = "Category";
			static public String  Entry_Number = "Entry_Number";
			static public String  Entry_Index = "Entry_Index";
			static public String  Entry = "Entry";
			static public String  Entry_Mnemonic = "Entry_Mnemonic";
			static public String  Entry_Info = "Entry_Info";
			static public String  Mnemonic_Type = "Mnemonic_Type";
			static public String  Title = "Title";
			static public String  Is_Linebreak = "Is_Linebreak";
		}
		static public class user_events_table{
			static public String _id = "_id"; 
			static public String Year = "Year";
			static public String Date = "Date";			
			static public String Event = "Event";
			static public String Acrostics = "Acrostics";
			static public String SaveWords = "SaveWords";
			static public String Type = "Type";
		}
		static public class user_new_words{
			static public String _id = "_id";
			static public String Username = "Username";
			static public String Table_name = "Table_name";
			static public String Date = "Date";
			static public String Word = "Word";
		}		
		static public class user_numbers_table{
			static public String _id = "_id";
			static public String Number = "Number";
			static public String NumInf = "NumInf";
			static public String NumWors = "NumWors";
			static public String Type = "Type";
		}		
		static public class user_review_times{
			static public String _id = "_id";
			static public String UserName = "UserName";
			static public String Time1 = "Time1";
			static public String Time2 = "Time2";
			static public String Time3 = "Time3";
			static public String Time4 = "Time4";
			static public String Time5 = "Time5";
			static public String Time6 = "Time6";
			static public String Time7 = "Time7";
			static public String Time8 = "Time8";
			static public String Time9 = "Time9";
			static public String Time10 = "Time10";
		}
		static public class sync_table{		
			static public String _id = "_id";
			static public String id = "id";
			static public String Password = "Password";
			static public String SQL = "SQL";
			static public String Action = "Action";
			static public String Device_Id = "Device_Id";
			static public String DB = "DB";
			static public String Username = "Username";
			static public String Is_Image = "Is_Image";
			static public String Image = "Image";
			static public String Table_name = "Table_name";
			static public String Name = "Name";
		}
	}	
}


