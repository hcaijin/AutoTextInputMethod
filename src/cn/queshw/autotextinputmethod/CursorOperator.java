package cn.queshw.autotextinputmethod;

import android.view.inputmethod.InputConnection;

public class CursorOperator {
	InputConnection mConnection;

	public CursorOperator(InputConnection mConnection) {
		this.mConnection = mConnection;
	}

/*	public void moveCursorTo(int pos, int mStart, int mEnd) {
		if (mStart == mEnd) {// ���ڲ���ģʽ
			mConnection.commitText(" ", 1);
			mConnection.deleteSurroundingText(1, 0);// ͷ�������д�������ȷ��ռ����Ļ����ִ����Ѿ���ʧ�����������ı༭ģʽ
			mConnection.setSelection(0, 0);
		}
		if (pos > 0) {// �����ƶ���ͷ
			moveToPos(pos);
		} else {// �ƶ���ͷ
			if (mStart != 0 || mEnd != 0)
				moveCursorToStart();
		}
	}

	// ////////////////////////////////////////////////////////////////////////////
	// �ѹ���ƶ���ͷ��
	private void moveCursorToStart() {
		// �Ȱ�ͷһ���ַ�����һ������������ҳ��������й��λ��selectionλ����һ��
		mConnection.setSelection(0, 1);
		SpannableStringBuilder tSpanable = new SpannableStringBuilder(mConnection.getSelectedText(InputConnection.GET_TEXT_WITH_STYLES));
		mConnection.commitText("", 1);
		mConnection.commitText(tSpanable.append(tSpanable), 0);
		// ���ƶ����
		mConnection.setSelection(0, 1);
		CharSequence tSequence = mConnection.getSelectedText(InputConnection.GET_TEXT_WITH_STYLES);
		mConnection.commitText("", 1);
		mConnection.commitText(tSequence, 0);
		mConnection.deleteSurroundingText(1, 0);
		mConnection.setSelection(0, 0);
	}

	// �ѹ���Ƶ�ĳ��λ�ã�����Ϊͷ�ϣ�Ҳ����˵Ҫȷ�����λ��֮ǰ����һ���ַ�����������ͬʱȷ�����������ı༭ģʽ�У�����ɫ��겻���֣���
	private void moveToPos(int pos) {
		mConnection.setSelection(pos - 1, pos);
		CharSequence tSequence = mConnection.getSelectedText(InputConnection.GET_TEXT_WITH_STYLES);
		mConnection.commitText("", 1);
		mConnection.commitText(tSequence, 0);
	}*/

	// ///////////////////////////////////////////////////////////////////////////
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

}
