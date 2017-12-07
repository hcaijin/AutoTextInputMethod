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
	private GenAutotext ga = new GenAutotext();

	// ////////////////////////////////////////////////////
	// ���캯��
	public DBOperations(Context context) {
		// TODO Auto-generated constructor stub
		helper = new DBHelper(context, "methods.db", null, 2);
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
				String rawTableName = "raw" + String.valueOf(id);
				String autotextTableName = "autotext" + String.valueOf(id);
				// 1������raw��
				String sql = "create table " + rawTableName + "(" + "id integer primary key autoincrement," + "code text not null,"
						+ "candidate text not null," + "twolevel int default 0)";
				// Log.d("Here", "create raw sql = " + sql);
				db.execSQL(sql);

				// 2 ������autotext��Ľṹ
				sql = "create table " + autotextTableName + "(id integer primary key autoincrement," + "input text not null,"
						+ "autotext text not null," + "rawid integer default 0)";
				db.execSQL(sql);

				// String tableName = "autotext" + String.valueOf(id);
				// String sql = "create table " + tableName +
				// "(id integer primary key autoincrement," +
				// "input text not null,"
				// + "autotext text not null)";
				// Log.d("Here", sql);
				// db.execSQL(sql);
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
		String rawTableName = "raw" + String.valueOf(id);
		String autotextTableName = "autotext" + String.valueOf(id);

		// ������ɾ����Ӧ��raw����autotext��
		String sql = "drop table if exists " + rawTableName;
		// Log.d("Here", sql);
		db.execSQL(sql);

		sql = "drop table if exists " + autotextTableName;
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
	// ����raw��ϵ�б�
	// ������ز�����ȡ��¼
	public ArrayList<RawItem> searchRawItems(String table, String searchText, int limit, int offset) {
		// searchText.toLowerCase();
		searchText = ConstantList.escape(searchText);
		ArrayList<RawItem> data = new ArrayList<RawItem>();
		// String sql = "select * from " + table + " where input like '" +
		// searchText + "%' order by input limit " + String.valueOf(limit) +
		// " offset "
		// + String.valueOf(offset);
		String sql;
		if (searchText.equals("twolevel"))
			sql = "select * from " + table + " where twolevel < 0 order by twolevel,code";
		else
			sql = "select * from " + table + " where code like '" + searchText + "%' order by code limit " + String.valueOf(limit) + " offset "
					+ String.valueOf(offset);
		// Log.d("Here", sql);
		Cursor cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			RawItem item;
			item = constructRawItem(cursor);
			data.add(item);
		}
		cursor.close();
		return data;
	}

	// ����id��ȡ������¼
	public RawItem getRawItem(String table, int id) {
		RawItem item;
		String sql = "select * from " + table + " where id = " + String.valueOf(id);
		// Log.d("Here", sql);
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToNext();
		item = constructRawItem(cursor);
		cursor.close();
		return item;
	}

	// ����rawitem
	private RawItem constructRawItem(Cursor cursor) {
		// TODO Auto-generated method stub
		RawItem item = new RawItem();
		item.setId(cursor.getInt(cursor.getColumnIndex("id")));
		item.setCode(ConstantList.recover(cursor.getString(cursor.getColumnIndex("code"))));
		item.setCandidate(ConstantList.recover(cursor.getString(cursor.getColumnIndex("candidate"))));
		item.setTwolevel(cursor.getInt(cursor.getColumnIndex("twolevel")));
		return item;
	}

	// ��ӻ����޸ĵ�������
	public void addOrSaveRawItem(int methodId, String code, String candidate, int id) {
		String rawTableName = "raw" + String.valueOf(methodId);

		code = ConstantList.escape(code);
		candidate = ConstantList.escape(candidate);

		if (!TextUtils.isEmpty(code) && !TextUtils.isEmpty(candidate)) {// �����һ��Ϊ�գ���ʲô������
			// ���ж���Ӧid�ŵļ�¼�Ƿ���ڣ��Դ���ȷ����������¼�����޸ļ�¼
			String sql = "select id from " + rawTableName + " where id = " + String.valueOf(id);
			Cursor cursor = db.rawQuery(sql, null);

			if (cursor.getCount() == 0) {// ˵��Ϊ������¼
				ContentValues cv = new ContentValues();
				cv.put("code", code);
				cv.put("candidate", candidate);
				cv.put("twolevel", 0);
				int tempId = (int) db.insert(rawTableName, null, cv);
				// sql = "insert into " + rawTableName + " values(null, '" +
				// code + "', '" + candidate + "', null)";
				// db.execSQL(sql);
				regenAutotext(methodId, tempId);
			} else {// ˵��Ϊ�޸�ԭ�м�¼
				sql = "update " + rawTableName + " set code = '" + code + "', candidate='" + candidate + "' where id = " + String.valueOf(id);
				db.execSQL(sql);
				RawItem item = getRawItem(rawTableName, id);
				// ������Ӧ�ø��¶�Ӧ��autotext���еļ�¼
				if (item.getTwolevel() < 0) {// �Ƕ����滻��Ŀ
					regenAutotext(methodId, item.getTwolevel());
				} else {// ���Ƕ����滻��Ŀ
					regenAutotext(methodId, item.getId());
				}
			}
			// Log.d("Here", sql);
			cursor.close();
		}
	}

	// ���������������
	// public void importData(int methodId, ArrayList<String[]> data) {
	// // String sql;
	// String rawTableName = "raw" + String.valueOf(methodId);
	// boolean isTwolevel = false;
	// int last_insert_id = 0;
	// int twolevelid = 0;
	// ContentValues cv = new ContentValues();
	// // db.beginTransaction();
	// for (String[] item : data) {
	// cv.clear();
	// if (item[0].equals("[twolevel]") && isTwolevel == false) {// �ж��Ƿ��Ƕ����滻��Ŀ
	// isTwolevel = true;
	// twolevelid = last_insert_id;// ������һ�β����id��
	// cv.put("twolevel", twolevelid);
	// // ��������Ҫ�Ѹղ����raw��һ�е�twolevel�ֶ�ֵ ���и���
	// db.update(rawTableName, cv, "id = ? ", new String[] {
	// String.valueOf(last_insert_id) });
	// continue;
	// } else if (item[0].equals("[twolevel]") && isTwolevel == true) {
	// isTwolevel = false;
	// twolevelid = 0;
	// continue;
	// }
	// cv.put("code", item[0]);
	// cv.put("candidate", item[1]);
	// cv.put("twolevel", twolevelid);
	// last_insert_id = (int) db.insert(rawTableName, null, cv);
	// // sql = "insert into " + table + " values(null, '" + item[0] +
	// // "', '" + item[1] + "', "+ String.valueOf(last_insert_id) +")";
	// // db.execSQL(sql);
	// // ������Ӧ�����ɶ�Ӧ��autotext��Ŀ���������Ӧ�ı���
	// //genAutotext(new RawItem(last_insert_id, item[0], item[1], twolevelid),
	// methodId);
	//
	// }
	// // db.setTransactionSuccessful();
	// // db.endTransaction();
	// }
	// �˰汾Ч�ʴ�����
	public void importData(int methodId, ArrayList<String[]> data) {
		String rawTableName = "raw" + String.valueOf(methodId);
		String autotextTableName = "autotext" + String.valueOf(methodId);
		// ��ѯraw���У���С��twolevelֵ�����ں�����������Ҫ�õ�twolevelֵ
		int preTwolevel = 0;
		Cursor cursor = db.rawQuery("select min(twolevel) from " + rawTableName, null);
		if (cursor.getCount() != 0) {
			cursor.moveToNext();
			preTwolevel = cursor.getInt(0);
			cursor.close();
		}

		// Log.d("Here", "importData()");
		String sql = "insert into " + rawTableName + " values(null, ?, ?, ?)";
		SQLiteStatement statement = db.compileStatement(sql);

		db.beginTransaction();
		for (String[] item : data) {
			statement.bindString(1, item[0]);
			statement.bindString(2, item[1]);
			if (Integer.parseInt(item[2]) < 0)
				statement.bindLong(3, Integer.parseInt(item[2]) + preTwolevel);
			else
				statement.bindLong(3, 0);
			statement.executeInsert();
		}
		db.setTransactionSuccessful();
		db.endTransaction();

		// ��������������autotext��Ŀ������
		// 1����raw���е����ݶ�ȡ����������Ҫ�����autotext������
		ArrayList<String> input = new ArrayList<String>();
		ArrayList<String> autotext = new ArrayList<String>();
		ArrayList<Integer> rawid = new ArrayList<Integer>();

		ArrayList<String> tempInput = new ArrayList<String>();
		ArrayList<String> tempAutotext = new ArrayList<String>();
		cursor = db.rawQuery("select * from " + rawTableName + " order by id", null);
		while (cursor.moveToNext()) {
			tempInput.clear();
			tempAutotext.clear();
			ga.gen(cursor.getString(cursor.getColumnIndex("code")) + "," + cursor.getString(cursor.getColumnIndex("candidate")));
			tempInput = ga.getInputList();
			tempAutotext = ga.getAutotextList();
			// ��������滻�������
			if (cursor.getInt(cursor.getColumnIndex("twolevel")) < 0) {// �����ǰ��Ϊ�����滻��Ŀ
				// �鿴��һ������滻�У�˭�ǵ�һ��
				Cursor tempCursor = db.rawQuery("select min(id) from " + rawTableName + " where twolevel=?",
						new String[] { String.valueOf(cursor.getInt(cursor.getColumnIndex("twolevel"))) });
				tempCursor.moveToNext();

				if (cursor.getInt(cursor.getColumnIndex("id")) == tempCursor.getInt(0)) {// ��ǰΪ��������滻�ĵ�һ��
					for (int i = 0; i < tempInput.size(); i++) {
						input.add(tempInput.get(i));
						autotext.add(tempAutotext.get(i));
						rawid.add(cursor.getInt(cursor.getColumnIndex("twolevel")));
					}
				} else {// ��ǰΪ��������滻��������
					int j = -1;
					for (int i = 0; i < autotext.size(); i++) {
						if (rawid.get(i) == cursor.getInt(cursor.getColumnIndex("twolevel")) && autotext.get(i).equals("%b" + tempInput.get(0))) {
							j = i;
							break;
						}
					}
					if (j == -1) {// ���û���ҵ�
						input.add(tempInput.get(0));
						autotext.add(tempAutotext.get(0));
						rawid.add(cursor.getInt(cursor.getColumnIndex("twolevel")));
					} else {// ����ҵ���
						autotext.set(j, tempAutotext.get(0));
					}
					for (int i = 1; i < tempInput.size(); i++) {
						input.add(tempInput.get(i));
						autotext.add(tempAutotext.get(i));
						rawid.add(cursor.getInt(cursor.getColumnIndex("twolevel")));
					}
				}
				tempCursor.close();
			} else {// ���в��Ƕ����滻��Ŀ
				for (int i = 0; i < tempInput.size(); i++) {
					input.add(tempInput.get(i));
					autotext.add(tempAutotext.get(i));
					rawid.add(cursor.getInt(cursor.getColumnIndex("id")));
				}
			}
		}
		cursor.close();// �����Ѿ��������ڵ���autotext�������

		// 2����������autotext����
		sql = "insert into " + autotextTableName + " values(null, ?, ?, ?)";
		statement = db.compileStatement(sql);

		db.beginTransaction();
		for (int i = 0; i < input.size(); i++) {
			statement.bindString(1, input.get(i));
			statement.bindString(2, autotext.get(i));
			statement.bindLong(3, rawid.get(i));
			statement.executeInsert();
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	public ArrayList<RawItem> exportData(int methodId) {
		String rawTableName = "raw" + String.valueOf(methodId);
		ArrayList<RawItem> data = new ArrayList<RawItem>();
		// �ȶ������Ƕ����滻����Ŀ
		String sql = "select * from " + rawTableName + " where twolevel=0 order by id";
		Cursor cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			RawItem item;
			item = constructRawItem(cursor);
			data.add(item);
		}
		cursor.close();

		// �ٶ��������滻����Ŀ
		sql = "select * from " + rawTableName + " where twolevel<0 order by id,twolevel desc";
		cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			RawItem item;
			item = constructRawItem(cursor);
			data.add(item);
		}
		cursor.close();

		return data;
	}

	// ɾ��һ������
	public void deleteRawItem(int methodId, RawItem item) {
		// TODO Auto-generated method stub
		String rawTable = "raw" + String.valueOf(methodId);
		String sql = "delete from " + rawTable + " where id = " + String.valueOf(item.getId());
		db.execSQL(sql);
		// ������Ӧ�ø���autotext���еĶ�Ӧ����
		if (item.getTwolevel() < 0) {// �Ƕ����滻��Ŀ
			regenAutotext(methodId, item.getTwolevel());
		} else {// ���Ƕ����滻��Ŀ
			regenAutotext(methodId, item.getId());
		}
	}

	private void regenAutotext(int methodId, int id) {
		// ע������Ƕ����滻��Ŀ��idӦ�ô���raw���е�twolevel�ţ����������ֱ����raw���е�id�ż���
		String rawTableName = "raw" + String.valueOf(methodId);
		String autotextTableName = "autotext" + String.valueOf(methodId);
		String sql;

		// ��ɾ�����еĶ�Ӧ����Ŀ
		sql = "delete from " + autotextTableName + " where rawid=" + String.valueOf(id);
		db.execSQL(sql);

		// ��raw���ж�Ӧ������ȡ����������Ҫ�����autotext������
		ArrayList<String> input = new ArrayList<String>();
		ArrayList<String> autotext = new ArrayList<String>();
		ArrayList<Integer> rawid = new ArrayList<Integer>();

		ArrayList<String> tempInput = new ArrayList<String>();
		ArrayList<String> tempAutotext = new ArrayList<String>();
		if (id < 0) {// ��Ҫ���µ�Ϊ�����滻��
			sql = "select * from " + rawTableName + " where twolevel = " + String.valueOf(id);
		} else {// ��Ҫ���µĲ��Ƕ����滻��
			sql = "select * from " + rawTableName + " where id = " + String.valueOf(id);
		}
		Cursor cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			tempInput.clear();
			tempAutotext.clear();
			ga.gen(cursor.getString(cursor.getColumnIndex("code")) + "," + cursor.getString(cursor.getColumnIndex("candidate")));
			tempInput = ga.getInputList();
			tempAutotext = ga.getAutotextList();
			// ��������滻�������
			if (cursor.getInt(cursor.getColumnIndex("twolevel")) < 0) {// �����ǰ��Ϊ�����滻��Ŀ
				// �鿴��һ������滻�У�˭�ǵ�һ��
				Cursor tempCursor = db.rawQuery("select min(id) from " + rawTableName + " where twolevel=?",
						new String[] { String.valueOf(cursor.getInt(cursor.getColumnIndex("twolevel"))) });
				tempCursor.moveToNext();

				if (cursor.getInt(cursor.getColumnIndex("id")) == tempCursor.getInt(0)) {// ��ǰΪ��������滻�ĵ�һ��
					for (int i = 0; i < tempInput.size(); i++) {
						input.add(tempInput.get(i));
						autotext.add(tempAutotext.get(i));
						rawid.add(cursor.getInt(cursor.getColumnIndex("twolevel")));
					}
				} else {// ��ǰΪ��������滻��������
					int j = autotext.lastIndexOf("%b" + tempInput.get(0));
					if (j == -1) {// ���û���ҵ�
						input.add(tempInput.get(0));
						autotext.add(tempAutotext.get(0));
						rawid.add(cursor.getInt(cursor.getColumnIndex("twolevel")));
					} else {// ����ҵ���
						autotext.set(j, tempAutotext.get(0));
					}
					for (int i = 1; i < tempInput.size(); i++) {
						input.add(tempInput.get(i));
						autotext.add(tempAutotext.get(i));
						rawid.add(cursor.getInt(cursor.getColumnIndex("twolevel")));
					}
				}
				tempCursor.close();
			} else {// ���в��Ƕ����滻��Ŀ
				for (int i = 0; i < tempInput.size(); i++) {
					input.add(tempInput.get(i));
					autotext.add(tempAutotext.get(i));
					rawid.add(cursor.getInt(cursor.getColumnIndex("id")));
				}
			}
		}
		cursor.close();// �����Ѿ��������ڵ���autotext�������

		// 2����������autotext����
		sql = "insert into " + autotextTableName + " values(null, ?, ?, ?)";
		SQLiteStatement statement = db.compileStatement(sql);

		db.beginTransaction();
		for (int i = 0; i < input.size(); i++) {
			statement.bindString(1, input.get(i));
			statement.bindString(2, autotext.get(i));
			statement.bindLong(3, rawid.get(i));
			statement.executeInsert();
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	// ////////////////////////////////////////////////////////////////////////////
	// �������뷨�еĲ�ѯ���滻
	public String searchRaw(String table, String input) {
		// input.toLowerCase();
		input = ConstantList.newescape(input);
		String result;
		String sql = "select autotext from " + table + " where input = '" + input + "' order by id limit 1";
		// Log.d("Here", sql + "|");
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.getCount() == 0) {// ���û���ҵ����򷵻�һ���յ�SpannableStringBuilder����
			result = null;
		} else {// ����ҵ��ˣ���ô�ͷ��ض�Ӧ��autotext��SpannableStringBuilder����
			cursor.moveToNext();
			result = ConstantList.newrecover(cursor.getString(cursor.getColumnIndex("autotext")));
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
