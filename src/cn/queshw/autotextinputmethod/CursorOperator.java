package cn.queshw.autotextinputmethod;

import android.view.inputmethod.InputConnection;

public class CursorOperator {

	private int FROMEND = 1;
	private int FROMSTART = 0;
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
		if (charSequence == "" || charSequence == null)
			return 0;
		for (int i = 1; i <= charSequence.length(); ++i) {
			if (charSequence.charAt(charSequence.length() - i) == '\n' || charSequence.charAt(charSequence.length() - i) == '\r') {
				result++;
			}
		}
		return result;
	}

	// ��ȡ���й��ǰ�ִ�
	public CharSequence getToLineStart(int mFromWhichEnd) {
		CharSequence result = "";
		if (mFromWhichEnd == FROMEND) {// ����ӽ����Ĺ������
			result = mConnection.getSelectedText(0);
			if (result.equals(""))
				result = "";// ���ʲô��û��ȡ������ѽ����Ϊ���ִ�
		}
		int len = result.length();
		int len2 = 0;
		for (int i = 1; true; i++) {
			if (i <= len) {// ���ѭ���Ĵ�����С���Ѿ�ѡ����ַ�����������������ִ���ȡ�ַ�
				result = result.subSequence(len - i, len);
			} else {// Ҫ�ڹ��ǰȡ�µ��ַ���
				result = mConnection.getTextBeforeCursor(i - len, 0).toString() + result.toString();
			}
			// ���������˳�����
			len2 = result.length();// �ٴμ��㳤�ȣ���Ϊ result��ֵ�����Ѿ�����
			if (len2 < i)
				break;// ��ʾ�Ѿ�ȡ��ͷ��
			if (result.charAt(0) == '\n') {// ��ʾ�Ѿ�ȡ����һ�еĻ��з��ˣ���ȥ����ͷ�ϵ�������з����ǽ��
				result = result.subSequence(1, len2);
				break;
			}
		}

		// if (mFromWhichEnd == FROMSTART) {// ����ӿ�ʼ�Ĺ������
		// for (int i = 1; true; i++) {
		// result = mConnection.getTextBeforeCursor(i, 0);
		// if (result.equals(""))
		// return "";// ���һ���ַ���ȡ������˵��������ͷ�ϣ�ֱ�ӷ��ؿ��ִ�
		// // ���������˳�����
		// int len = result.length();
		// if (len < i)
		// break;// �Ѿ�ȡ��ͷ����û��ȡ��һ�����з���˵�������ڵ�һ����
		// if (result.charAt(0) == '\n') {// ���ȡ���˻��з���˵���Ѿ�ȡ����һ�е�β��
		// result = result.subSequence(1, len);
		// break;
		// }
		// }
		// } else if (mFromWhichEnd == FROMEND) {// ����ӽ����Ĺ������
		// result = mConnection.getSelectedText(0);
		// if (result.equals(""))
		// result = "";// ���ʲô��û��ȡ������ѽ����Ϊ���ִ�
		// int len = result.length();
		// for (int i = 1; true; i++) {
		// if (i <= len) {// ���ѭ���Ĵ�����С���Ѿ�ѡ����ַ�����������������ִ���ȡ�ַ�
		// result = result.subSequence(len - i, len);
		// } else {// Ҫ�ڹ��ǰȡ�µ��ַ���
		// result = mConnection.getTextBeforeCursor(i - len, 0).toString() +
		// result.toString();
		// }
		// // ���������˳�����
		// len = result.length();// �ٴμ��㳤�ȣ���Ϊ result��ֵ�����Ѿ�����
		// if (len < i)
		// break;// ��ʾ�Ѿ�ȡ��ͷ��
		// if (result.charAt(0) == '\n') {// ��ʾ�Ѿ�ȡ����һ�еĻ��з��ˣ���ȥ����ͷ�ϵ�������з����ǽ��
		// result = result.subSequence(1, len);
		// break;
		// }
		// }
		// }
		return result;
	}

	// ��ȡ���й����ִ�
	public CharSequence getToLineEnd(int mFromWhichEnd) {
		CharSequence result = "";
		if (mFromWhichEnd == FROMSTART) {// ����ӽ����Ĺ������
			result = mConnection.getSelectedText(0);
			if (result.equals(""))
				result = "";// ���ʲô��û��ȡ������ѽ����Ϊ���ִ�
		}
		int len = result.length();
		int len2 = 0;
		for (int i = 1; true; i++) {
			if (i <= len) {// ���ѭ���Ĵ�����С���Ѿ�ѡ����ַ�����������������ִ���ȡ�ַ�
				result = result.subSequence(0, i);
			} else {// Ҫ�ڹ��ǰȡ�µ��ַ���
				result = result.toString() + mConnection.getTextAfterCursor(i - len, 0).toString();
			}
			// ���������˳�����
			len2 = result.length();// �ٴμ��㳤�ȣ���Ϊ result��ֵ�����Ѿ�����
			if (len2 < i)
				break;// ��ʾ�Ѿ�ȡ��ͷ��
			if (result.charAt(0) == '\n') {// ��ʾ�Ѿ�ȡ����һ�еĻ��з��ˣ���ȥ����ͷ�ϵ�������з����ǽ��
				result = result.subSequence(0, len2 - 1);
				break;
			}
		}
		return result;
	}

	// ��ȡ���ǰһ�е��ִ�
	CharSequence getPreLine(int mFromWhichEnd) {
		CharSequence result = "";
		int lineBreaks = 0;// ���ڼ�¼ȡ�����з��ĸ���

		if (mFromWhichEnd == FROMEND) {// ����ӽ����Ĺ������
			result = mConnection.getSelectedText(0);
			if (result.equals(""))
				result = "";// ���ʲô��û��ȡ������ѽ����Ϊ���ִ�
		}

		int len = result.length();
		int len2 = 0;
		for (int i = 1; true; i++) {
			if (i <= len) {// ���ѭ���Ĵ�����С���Ѿ�ѡ����ַ�����������������ִ���ȡ�ַ�
				result = result.subSequence(len - i, len);
			} else {// Ҫ�ڹ��ǰȡ�µ��ַ���
				result = mConnection.getTextBeforeCursor(i - len, 0).toString() + result.toString();
			}

			if (result.charAt(0) == '\n') // ��ʾ�Ѿ�ȡ����һ�еĻ��з��ˣ���ȥ����ͷ�ϵ�������з����ǽ��
				lineBreaks++;
			// ���������˳�����
			len2 = result.length();// �ٴμ��㳤�ȣ���Ϊ result��ֵ�����Ѿ�����
			if (len2 < i) {// ��ʾ�Ѿ�ȡ��ͷ��
				if (lineBreaks == 0) {//�Ѿ��˵�ͷ������û��һ�����з�����ʾ�����ڵ�һ���ϣ�����һ�в�����
					return "";
				} else if (lineBreaks == 1) {//�Ѿ���ͷ�ˣ�����ֻȡ��һ�����з���˵�����ǰ��ֻ��һ��
					result = result.subSequence(0, len2 - getToLineStart(mFromWhichEnd).length());
					break;
				}
			} else if (lineBreaks == 2) {//���ȡ�����������з���˵�����ǰ������������
				result = result.subSequence(1, len2 - getToLineStart(mFromWhichEnd).length());
				break;
			}
		}
		return result;
	}

	// ��ȡ�����һ�е��ִ�
	CharSequence getNextLine(int mFromWhichEnd) {
		CharSequence result = "";
		int lineBreaks = 0;// ���ڼ�¼ȡ�����з��ĸ���

		if (mFromWhichEnd == FROMSTART) {// ����ӽ����Ĺ������
			result = mConnection.getSelectedText(0);
			if (result.equals(""))
				result = "";// ���ʲô��û��ȡ������ѽ����Ϊ���ִ�
		}

		int len = result.length();
		int len2 = 0;
		for (int i = 1; true; i++) {
			if (i <= len) {// ���ѭ���Ĵ�����С���Ѿ�ѡ����ַ�����������������ִ���ȡ�ַ�
				result = result.subSequence(0, i);
			} else {// Ҫ�ڹ��ǰȡ�µ��ַ���
				result = result.toString() + mConnection.getTextAfterCursor(i - len, 0).toString();
			}

			len2 = result.length();// �ٴμ��㳤�ȣ���Ϊ result��ֵ�����Ѿ�����
			if (result.charAt(len2 -1) == '\n') // ��ʾ�Ѿ�ȡ����һ�еĻ��з��ˣ���ȥ����ͷ�ϵ�������з����ǽ��
				lineBreaks++;
			// ���������˳�����			
			if (len2 < i) {// ��ʾ�Ѿ�ȡ��β��
				if (lineBreaks == 0) {//�Ѿ��˵�β������û��һ�����з�����ʾ���������һ���ϣ�������һ�в�����
					return "";
				} else if (lineBreaks == 1) {//�Ѿ���β�ˣ�����ֻȡ��һ�����з���˵��������ֻ��һ��
					result = result.subSequence(getToLineEnd(mFromWhichEnd).length(), len2);
					break;
				}
			} else if (lineBreaks == 2) {//���ȡ�����������з���˵������������������
				result = result.subSequence(getToLineEnd(mFromWhichEnd).length(), len2 - 1);
				break;
			}
		}
		return result;
	}

}
