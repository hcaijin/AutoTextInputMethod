package cn.queshw.autotextsetting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	private final String method_sql = "CREATE TABLE methods(" + "id INTEGER PRIMARY KEY AUTOINCREMENT," + "name TEXT NOT NULL,"
			+ "isDefault INTEGER NOT NULL)";
	private SQLiteDatabase mdb;

	public DBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		mdb = db;
		mdb.execSQL(method_sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		switch (oldVersion) {
		case 1:// ��autotext�в���Դ�ļ�
				// ��һ����ȡ��methods���е�id��
			ArrayList<Integer> methodIdList = new ArrayList<Integer>();
			Cursor cursor = db.rawQuery("select id from methods order by id", null);
			while (cursor.moveToNext()) {
				methodIdList.add(cursor.getInt(0));
				Log.d("Here", "methodid = " + String.valueOf(cursor.getInt(0)));
			}
			// �ڶ���������method��id�ţ�����rawϵ�еı����Ҵ�autotext��������raw�����Ŀ
			for (int i : methodIdList) {
				// 1������raw��
				String sql = "create table raw" + String.valueOf(i) + "(" + "id integer primary key autoincrement," + "code text not null,"
						+ "candidate text not null," + "twolevel int default 0" + ")";
				Log.d("Here", "create raw sql = " + sql);
				db.execSQL(sql);

				//2����autotext��������raw�����Ŀ
				cursor = db.rawQuery("select * from autotext" + String.valueOf(i) + " order by id", null);
				HashMap<String, String> autotextMap = new HashMap<String, String>();
				while (cursor.moveToNext()) {// ��autotext���е���Ŀ���ŵ�һ��hashmap��
					autotextMap.put(cursor.getString(1), cursor.getString(2));
				}
				ArrayList<String[]> rawList = new ArrayList<String[]>();
				String key, value;
				for (Entry<String, String> autotext : autotextMap.entrySet()) {
					key = autotext.getKey();
					value = autotext.getValue();
					if (!autotextMap.containsValue(key + "%B") && !autotextMap.containsValue(key.subSequence(0, key.length() - 1) + "%B")) {
						// ���key����key��ȥ���һ����ĸ�����Ǳ����Ŀ���滻���˵����ǰ��ĿΪ�����Ŀ�������������Ŀ������Ҳ���˵����������ѡ��ĵ�һ����Ŀ
						if (value.subSequence(0, 2).equals("%b")) {// ���ͷ����λ��%b
							value = value.substring(2);
						} else if (value.subSequence(value.length() - 2, value.length()).equals("%B")) {// ��������λ��%B
							value = (String) value.subSequence(0, value.length() - 2);
							value = getCandidate(value, value, autotextMap);
							if(value.subSequence(0, 1).equals(",")) value = value.substring(1);
						}
						//Log.d("Here", key + "," + value);
						rawList.add(new String[]{key, value});
					}
				}
			}
		default:
		}
	}

	private String getCandidate(CharSequence s, CharSequence start, HashMap<String, String> m) {
		// TODO Auto-generated method stub
		String candidate = "";
		// Log.d("Here", "s = " + s);
		// Log.d("Here", "m.get(s) = " + m.get(s));
		// Log.d("Here", "m.containsKey(s) = " +
		// String.valueOf(m.containsKey(s)));
		if ((m.containsKey(s) && m.get(s).subSequence(0, 2).equals("%b")))
			candidate = candidate + "," + m.get(s).substring(2);
		if ((m.containsKey(s + "w") && m.get(s + "w").subSequence(0, 2).equals("%b")))
			candidate = candidate + "," + m.get(s + "w").substring(2);
		if ((m.containsKey(s + "e") && m.get(s + "e").subSequence(0, 2).equals("%b")))
			candidate = candidate + "," + m.get(s + "e").substring(2);
		if ((m.containsKey(s + "r") && m.get(s + "r").subSequence(0, 2).equals("%b")))
			candidate = candidate + "," + m.get(s + "r").substring(2);
		if ((m.containsKey(s + "s") && m.get(s + "s").subSequence(0, 2).equals("%b")))
			candidate = candidate + "," + m.get(s + "s").substring(2);
		if ((m.containsKey(s + "d") && m.get(s + "d").subSequence(0, 2).equals("%b")))
			candidate = candidate + "," + m.get(s + "d").substring(2);
		if ((m.containsKey(s + "f") && m.get(s + "f").subSequence(0, 2).equals("%b")))
			candidate = candidate + "," + m.get(s + "f").substring(2);
		if ((m.containsKey(s + "z") && m.get(s + "z").subSequence(0, 2).equals("%b")))
			candidate = candidate + "," + m.get(s + "z").substring(2);
		if ((m.containsKey(s + "x") && m.get(s + "x").subSequence(0, 2).equals("%b")))
			candidate = candidate + "," + m.get(s + "x").substring(2);
		if ((m.containsKey(s + "c") && m.get(s + "c").subSequence(0, 2).equals("%b")))
			candidate = candidate + "," + m.get(s + "c").substring(2);

		if (m.containsKey(s) && m.get(s).subSequence(m.get(s).length() - 2, m.get(s).length()).equals("%B")
				&& !m.get(s).subSequence(0, m.get(s).length() - 2).equals(start)) {
			candidate = candidate + this.getCandidate(m.get(s).subSequence(0, m.get(s).length() - 2), start, m);
		}
		return candidate;
	}
}
