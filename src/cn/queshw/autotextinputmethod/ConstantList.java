package cn.queshw.autotextinputmethod;

import android.view.KeyEvent;

public class ConstantList {
	//�����뷨��ϵͳ�е�id
	static final String METHODID = "cn.queshw.autotextinputmethod/.AutotextInputMethod";
	
	// �����ַ�
	static final int SUBSTITUTION_TRIGGER = KeyEvent.KEYCODE_SPACE;// �����滻�����ַ�
	static final int SUBSTITUTION_TRIGGER_REVERSE = KeyEvent.KEYCODE_DEL;// �����滻�����ַ�
	static final int SUBSTITUTION_ENTER = KeyEvent.KEYCODE_ENTER;//�س��ַ�
	static final int SUBSTITUTION_NUMPAD_ENTER = KeyEvent.KEYCODE_NUMPAD_ENTER;
	static final char SUBSTITUTION_SEPERRATOR = ' ';// �滻�ָ���

	// ������
	static final char MACRO_DELETEBACK = 'b';
	static final char MACRO_DELETEFORWARD = 'B';
	static final char MACRO_DELETEWORD = 'w';
	static final char MACRO_DATE = 'd';
	static final char MACRO_LONGDATE = 'D';
	static final char MACRO_TIME = 't';
	static final char MACRO_ESCAPECHARACTER = '%';

	// �༭������
	static final int EDIT_COPY = KeyEvent.KEYCODE_C;
	static final int EDIT_PASTE = KeyEvent.KEYCODE_V;
	static final int EDIT_CUT = KeyEvent.KEYCODE_X;
	static final int EDIT_UNDO = KeyEvent.KEYCODE_Z;
	
	static final int EDIT_SELECTMODEL = KeyEvent.KEYCODE_S;
	static final int EDIT_SELECTALL = KeyEvent.KEYCODE_A;
	static final int EDIT_SELECTLINE = KeyEvent.KEYCODE_H;
	
	static final int EDIT_UP = KeyEvent.KEYCODE_I;
    static final int EDIT_DOWN = KeyEvent.KEYCODE_K;
    static final int EDIT_BACK = KeyEvent.KEYCODE_J;
    static final int EDIT_FORWARD = KeyEvent.KEYCODE_L;    

    static final int EDIT_TOLINESTART = KeyEvent.KEYCODE_U;
    static final int EDIT_TOLINEEND = KeyEvent.KEYCODE_O;    
    static final int EDIT_TOSTART = KeyEvent.KEYCODE_Y;
    static final int EDIT_TOEND = KeyEvent.KEYCODE_P;
	
	static final int EDIT_DELETEALL = KeyEvent.KEYCODE_D;
	static final int EDIT_DELETEFORWARD = KeyEvent.KEYCODE_N;
	static final int EDIT_DELETELINE = KeyEvent.KEYCODE_M;
    	
	//static final int EDIT_REDO = KeyEvent.KEYCODE_R;

	static final int SWITCH_INPUTMETHOD = KeyEvent.KEYCODE_ENTER;// �����t�����������л����뷨

	// //////////////////////////////////////////////////////////////////////////
	/*
	 * ��������ת������������ֿ��ʱ���� #SINGLE_QUOTATION# #S# # #C#�����к���������Դ�ļ��ʹ��ڵġ�
	 */
	public static String escape(String str) {//���е��붼��ҪӦ�ô˺���
		str = str.trim();
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c == '\'')
				s.append("#SINGLE_QUOTATION#");
			else
				s.append(c);
		}
		return s.toString();
	}
	
	public static String recover(String str) {//���д����ݿ���ȡֵ����ҪӦ�ô˺���
		StringBuilder s = new StringBuilder();
		String[] item = str.split("#");
		for (int i = 0; i < item.length; i++) {
			if (item[i].equals("SINGLE_QUOTATION"))
				s.append("'");
			else if (item[i].equals("SHARP"))
				s.append("#SHARP#");
			else if (item[i].equals("COMMA"))
				s.append("#COMMA#");
			else
				s.append(item[i]);
		}
		return s.toString();
	}

	///////////////////////////////////////////////////////////////////////
	//�������������������뷨���������
	public static String newescape(String str) {//������ʱ��Ҫת�壬��Ϊ�����ݿ��У����š����š�����������ת��ķ�ʽ�洢��
		str = str.trim();
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c == '\'')
				s.append("#SINGLE_QUOTATION#");
			else if (c == '#')
				s.append("#SHARP#");
			else if (c == ',')
				s.append("#COMMA#");
			else
				s.append(c);
		}
		return s.toString();
	}
	
	public static String newrecover(String str) {//���д����ݿ���ȡֵ����ҪӦ�ô˺���
		StringBuilder s = new StringBuilder();
		String[] item = str.split("#");
		for (int i = 0; i < item.length; i++) {
			if (item[i].equals("SINGLE_QUOTATION"))
				s.append("'");
			else if (item[i].equals("SHARP"))
				s.append("#");
			else if (item[i].equals("COMMA"))
				s.append(",");
			else
				s.append(item[i]);
		}
		return s.toString();
	}
}

