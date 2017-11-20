package cn.queshw.autotextsetting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import cn.queshw.autotextinputmethod.ConstantList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;

public class DBOperations {
	public static final int NOOFFSET = -1;// �������ȡȫ������
	public static final int NOLIMIt = -1;// �������ȡȫ������

	private DBHelper helper;
	private SQLiteDatabase db;

	// ////////////////////////////////////////////////////
	// ���캯��
	public DBOperations(Context context) {
		// TODO Auto-generated constructor stub
		helper = new DBHelper(context, "methods.db", null, 1);
		db = helper.getWritableDatabase();
	}

	// /////////////////////////////////////////////////////
	// ��ȡmethods�����ݣ������һ��ArrayList<MethodItem>����
	public ArrayList<MethodItem> loadMethodsData() {
		ArrayList<MethodItem> itemList = new ArrayList<MethodItem>();
		Cursor cursor;
		cursor = db.rawQuery("select * from methods order by id", null);
		while (cursor.moveToNext()) {
			MethodItem item = constructMethodItem(cursor);
			itemList.add(item);
		}
		cursor.close();
		return itemList;
	}

	// ����id��ȡ��¼������MethodItem
	public MethodItem getMethodItem(int id) {
		Cursor cursor = db.rawQuery("select * from methods where id = ?", new String[] { String.valueOf(id) });
		cursor.moveToNext();
		MethodItem item = constructMethodItem(cursor);
		cursor.close();
		return item;
	}

	// ����һ����¼����һ������
	private MethodItem constructMethodItem(Cursor cursor) {
		MethodItem item = new MethodItem();
		item.setId(cursor.getInt(cursor.getColumnIndex("id")));
		item.setName(ConstantList.recover(cursor.getString(cursor.getColumnIndex("name"))));
		// Log.d("Here", "name=" +
		// cursor.getString(cursor.getColumnIndex("name")));
		item.setIsDefault(cursor.getInt(cursor.getColumnIndex("isDefault")));
		return item;
	}

	// ////////////////////////////////////////////////////////

	// ////////////////////////////////////////////////////////
	// ���һ����¼������id��
	public int addOrSaveMethodItem(String name, int isDefault, int id) {
		name = ConstantList.escape(name);
		// Log.d("Here", "name=" + name);
		ContentValues values = new ContentValues();
		values.put("name", name);
		values.put("isDefault", isDefault);

		if (!TextUtils.isEmpty(name)) {// �����Ϊ��
			Cursor cursor = db.rawQuery("select isDefault from methods where id = ?", new String[] { String.valueOf(id) });

			if (cursor.getCount() == 0) {// ���ԭ��û�д�����¼����������¼
				if (isDefault == MethodItem.DEFAULT) {
					db.execSQL("update methods set isDefault = ?", new String[] { String.valueOf(MethodItem.NOTDEFAULT) });
				}
				id = (int) db.insert("methods", null, values);

				// �������������뷨�������Ĵʿ��
				String tableName = "autotext" + String.valueOf(id);
				String sql = "create table " + tableName + "(id integer primary key autoincrement," + "input text not null,"
						+ "autotext text not null)";
				// Log.d("Here", sql);
				db.execSQL(sql);
			} else {// ���ԭ���Ѿ���������¼�����޸ļ�¼
				if (isDefault == MethodItem.DEFAULT) {// �Ȱ�ԭ��Ĭ�����뷨���������
					db.execSQL("update methods set isDefault = ?", new String[] { String.valueOf(MethodItem.NOTDEFAULT) });
				}
				db.update("methods", values, "id = ?", new String[] { String.valueOf(id) });
			}
			cursor.close();
			return id;
		}
		return -1;
	}

	// ɾ��һ����¼
	public void deleteMethodItem(String table, int id) {// ??
		// TODO Auto-generated method stub
		Cursor cursor = db.rawQuery("select isDefault from methods where id = ?", new String[] { String.valueOf(id) });
		cursor.moveToNext();
		// int isDefault = cursor.getInt(cursor.getColumnIndex("isDefault"));//
		// ����Ҫɾ���ļ�¼�Ƿ���Ĭ�ϵ����뷨

		db.delete("methods", "id=?", new String[] { String.valueOf(id) });
		String tableName = "autotext" + String.valueOf(id);
		String sql = "drop table if exists " + tableName;
		// Log.d("Here", sql);
		db.execSQL(sql);

		// if(isDefault == MethodItem.DEFAULT){//����Ѿ���Ĭ�����뷨ɾ���ˣ��ǾͰѵ�һ����¼��
		// cursor = db.rawQuery("select min(id) from methods", null);
		// cursor.moveToNext();
		// id = cursor.getInt(0);
		// db.execSQL("update methods set isDefault=? where id =?", new
		// String[]{String.valueOf(MethodItem.DEFAULT), String.valueOf(id)});
		// }
		cursor.close();

	}

	// //////////////////////////////////////////////////////////////////
	// ����autotext��ϵ�б�
	// ������ز�����ȡ��¼
	public ArrayList<AutotextItem> searchAutotextItems(String table, String searchText, int limit, int offset) {
		// searchText.toLowerCase();
		searchText = ConstantList.escape(searchText);
		ArrayList<AutotextItem> data = new ArrayList<AutotextItem>();
		String sql = "select * from " + table + " where input like '" + searchText + "%' order by input limit " + String.valueOf(limit) + " offset "
				+ String.valueOf(offset);
		// Log.d("Here", sql);
		Cursor cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			AutotextItem item;
			item = constructAutotextItem(cursor);
			data.add(item);
		}
		cursor.close();
		return data;
	}

	// ����id��ȡ������¼
	public AutotextItem getAutotextItem(String table, int id) {
		AutotextItem item;
		String sql = "select * from " + table + " where id = " + String.valueOf(id);
		// Log.d("Here", sql);
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToNext();
		item = constructAutotextItem(cursor);
		cursor.close();
		return item;
	}

	// ����autotextitem
	private AutotextItem constructAutotextItem(Cursor cursor) {
		// TODO Auto-generated method stub
		AutotextItem item = new AutotextItem();
		item.setId(cursor.getInt(cursor.getColumnIndex("id")));
		item.setInput(ConstantList.recover(cursor.getString(cursor.getColumnIndex("input"))));
		item.setAutotext(ConstantList.recover(cursor.getString(cursor.getColumnIndex("autotext"))));
		return item;
	}

	// ��ӻ����޸ĵ�������
	public void addOrSaveAutotextItem(String table, String input, String autotext, int id) {
		input = ConstantList.escape(input);
		autotext = ConstantList.escape(autotext);

		if (!TextUtils.isEmpty(input) && !TextUtils.isEmpty(autotext)) {// �����һ��Ϊ�գ���ʲô������
			// ���ж���Ӧid�ŵļ�¼�Ƿ���ڣ��Դ���ȷ����������¼�����޸ļ�¼
			String sql = "select id from " + table + " where id = " + String.valueOf(id);
			Cursor cursor = db.rawQuery(sql, null);

			if (cursor.getCount() == 0) {// ˵��Ϊ������¼
				sql = "insert into " + table + " values(null, '" + input + "', '" + autotext + "')";
			} else {// ˵��Ϊ�޸�ԭ�м�¼
				sql = "update " + table + " set input = '" + input + "', autotext='" + autotext + "' where id = " + String.valueOf(id);
			}
			// Log.d("Here", sql);
			db.execSQL(sql);
			cursor.close();
		}
	}

	// ���ڿ��������������
	// public void importData(String table, ArrayList<String[]> data) {
	// String sql;
	// db.beginTransaction();
	// for(String[] item : data){
	// sql = "insert into " + table + " values(null, '" + item[0] + "', '"
	// + item[1] + "')";
	// db.execSQL(sql);
	// }
	// db.setTransactionSuccessful();
	// db.endTransaction();
	// }
	// �˰汾Ч�ʴ�Ϊ���
	public void importData(String table, ArrayList<String[]> data) {
		String sql = "insert into " + table + " values(null, ?, ?)";
		SQLiteStatement statement = db.compileStatement(sql);
		db.beginTransaction();
		for (String[] item : data) {
			statement.bindString(1, item[0]);
			statement.bindString(2, item[1]);
			statement.executeInsert();
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	// ɾ��һ������
	public void deleteAutotextItem(String table, int id) {
		// TODO Auto-generated method stub
		String sql = "delete from " + table + " where id = " + String.valueOf(id);
		db.execSQL(sql);
	}

	// ////////////////////////////////////////////////////////////////////////////
	// �������뷨�еĲ�ѯ���滻
	public String searchAutotext(String table, String input) {
		// input.toLowerCase();
		input = ConstantList.escape(input);
		String result;
		String sql = "select autotext from " + table + " where input = '" + input + "' order by id limit 1";
		// Log.d("Here", sql + "|");
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.getCount() == 0) {// ���û���ҵ����򷵻�һ���յ�SpannableStringBuilder����
			result = null;
		} else {// ����ҵ��ˣ���ô�ͷ��ض�Ӧ��autotext��SpannableStringBuilder����
			cursor.moveToNext();
			result = ConstantList.recover(cursor.getString(cursor.getColumnIndex("autotext")));
		}
		cursor.close();
		return result;
	}

	// /////////////////////////////////////////////////////////////////////////////
	// ����ȡ�����input�еĳ���
	public int getMaxInputLength(int methodId) {
		String sql = "select max(length(input)) from autotext" + String.valueOf(methodId);
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToNext();
		int max = cursor.getInt(0);
		cursor.close();
		return max;
	}

}
