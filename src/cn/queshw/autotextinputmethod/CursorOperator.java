package cn.queshw.autotextinputmethod;

import android.view.inputmethod.InputConnection;

public class CursorOperator {

	private int FROMEND;
	private int FROMSTART;
	InputConnection mConnection;

	public CursorOperator(InputConnection mConnection) {
		this.mConnection = mConnection;
	}

	// ȡ�õ�ǰ�����ж����ı�
	public int getAfterLength() {
		int step = 50;
		int result = 0;
		for (int i = 1; true; i++) {
			CharSequence tSequence = mConnection.getTextAfterCursor(step * i, 0);
			if (tSequence.length() < step * i) {
				result = tSequence.length();
				break;
			}
		}
		return result;
	}

	// ��ȡ�ִ����ж��ٻس����з�
	int getInvisibleCharsNumber(CharSequence charSequence) {
		int result = 0;
		if (charSequence == "" || charSequence == null) {
			return 0;
		}
		for (int i = 1; i <= charSequence.length(); ++i) {
			if (charSequence.charAt(charSequence.length() - i) == '\n' || charSequence.charAt(charSequence.length() - i) == '\r') {
				result++;
			}
		}
		return result;
	}
	
	//��ȡ���й����ִ�
	public CharSequence getToLineEnd(int mFromWhichEnd) {
		CharSequence result = "";
		return result;
	}

	//��ȡ���й��ǰ�ִ�
	public CharSequence getToLineStart(int mFromWhichEnd) {
		CharSequence result = "";
		return result;
	}
	
	//��ȡ���ǰһ�е��ִ�
	CharSequence getPreLine(int mFromWhichEnd) {
		CharSequence result = "";
		return result;
	}
	
	//��ȡ�����һ�е��ִ�
	CharSequence getNextLine(int mFromWhichEnd) {
		CharSequence result = "";
		return result;
	}

}
