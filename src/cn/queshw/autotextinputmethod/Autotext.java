package cn.queshw.autotextinputmethod;


//���ڹ������滻�����еļ�������
final class Autotext {

	int start;
	int end;
	String beforeString;
	String afterString;
	
	public Autotext() {
		start = -1;
		end = -1;
		beforeString = "";
		afterString = "";
	}

	public void clear() {
		start = -1;
		end = -1;
		beforeString = "";
		afterString = "";
	}
}
