package com.lfq.learnfactsquick;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Helpers {
	private static String results;
	public static Context myContext;
	private static Boolean logged_in = false;
	private static String myUsername = "";
	private static String myPassword = "";
	private static Cursor c;
	public static String db_prefix = "psy6ms3b_";
	private static boolean is_dbprefix_set = false;

	public Helpers(Context context) {
		myContext = context;

	}

	public static String login(String username, String password) {

		if (password.length() < 4 || username.length() < 4) {
			results = "false@@@USERNAME AND PASSWORD LENGTH MUST BE GREATER THAN 3.";
			logged_in = false;
			return results;
		}
		c = MainLfqActivity.getUsersDb().rawQuery(
				"SELECT * FROM `userdata` WHERE UserName='" + username
						+ "' AND Password='" + password + "' LIMIT 1", null);
		if (c.moveToFirst()) {
			logged_in = true;
			myUsername = username;
			myPassword = password;
			results = "true@@@WELCOME " + username;
		} else {
			logged_in = false;
			results = "false@@@" + username + " WAS NOT FOUND.";
		}
		c.close();
		return results;
	}

	public static Boolean getLoginStatus() {
		return logged_in;
	}

	public static String getUsername() {
		return myUsername;
	}

	public static String getPassword() {
		return myPassword;
	}

	public static String joinListQuoted(List<String> list, String delimiter) {
		String str = "";
		for (int i = 0; i < list.size(); i++) {
			str += "'" + list.get(i) + "'";
			if (i != list.size() - 1) {
				str += delimiter;
			}
		}
		return str;
	}

	public static String joinList(List<String> list, String delimiter) {
		String str = "";
		for (int i = 0; i < list.size(); i++) {
			str += list.get(i);
			if (i != (list.size() - 1)) {
				str += delimiter;
			}
		}
		return str;
	}

	public static String joinList(String[] list, String delimiter) {
		String str = "";
		for (int i = 0; i < list.length; i++) {
			str += list[i];
			if (i != (list.length - 1)) {
				str += delimiter;
			}
		}
		return str;
	}

	public static String[] spliceString(String[] array, String find) {
		String[] new_array = new String[array.length - 1];
		int ctNewArr = 0;
		for (int ctArr = 0; ctArr < array.length; ctArr++) {
			if (!array[ctArr].equals(find)) {
				new_array[ctNewArr] = array[ctArr];
				ctNewArr++;
			}

		}
		return new_array;
	}

	public static String[] spliceArray(String[] arr, int ind) {
		String[] makarr = new String[arr.length - 1];
		int ct = 0;
		for (int i = 0; i < arr.length; i++) {
			if (i != ind) {
				makarr[ct] = arr[i];
				ct++;
			}
		}

		return makarr;
	}

	public static String[] explode(String str) {
		String[] arr = new String[str.length()];
		for (int i = 0; i < str.length(); i++) {
			arr[i] = String.valueOf(str.charAt(i));
		}
		return arr;
	}

	public static Boolean contains(String[] arr, String str) {
		Boolean is_contains = false;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i].equals(str)) {
				is_contains = true;
			}
		}
		return is_contains;
	}

	public static String arrToString(String[] arr, String delimiter) {
		String arrstr = "";
		for (int i = 0; i < arr.length; i++) {
			arrstr += arr[i];
			if (i != arr.length - 1) {
				arrstr += delimiter;
			}
		}
		return arrstr;
	}

	public static String[] splitBy(String str, String delimiter) {
		List<String> textlist = new ArrayList<String>();
		while (str.indexOf(delimiter) != -1) {
			textlist.add(str.substring(0, str.indexOf(delimiter)));
			str = str.replaceFirst(str.substring(0, str.indexOf(delimiter))
					+ delimiter, "");
		}
		String[] strspl = new String[textlist.size()];
		for (int i = 0; i < textlist.size(); i++) {
			strspl[i] = textlist.get(i);
		}
		return strspl;
	}

	public static boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) LfqApp
				.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		Boolean is_pinged = false;
		try {
			InetAddress.getByName("learnfactsquick.com").isReachable(500);
			is_pinged = true;
			if (!is_dbprefix_set){
			   setDBPrefix();
			   is_dbprefix_set=true;
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return activeNetworkInfo != null && activeNetworkInfo.isConnected()
				&& is_pinged;
	}

	public static void setDBPrefix() {
		String url = "http://www.learnfactsquick.com/lfq_app_php/set_db_prefix.php";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		JSONObject json = Synchronize.makeHttpRequest(url, "POST", params);
		if (json != null) {
			try {
				db_prefix = json.getString("DB_PREFIX");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

}
