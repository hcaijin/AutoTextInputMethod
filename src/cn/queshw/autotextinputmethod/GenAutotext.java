package cn.queshw.autotextinputmethod;

import java.util.HashMap;

public class GenAutotext {
	public final char CHINESE_QUOTATION_LEFT = '��';
    public final char CHINESE_QUOTATION_RIGHT = '��';
    
    private HashMap<String, String> result = new HashMap<String, String>();
    
	public GenAutotext() {
		// TODO Auto-generated constructor stub
	}
	
	//��һ���ִ�ת��Ϊautotext����Ŀ
	HashMap<String, String> gen(String line){
		result.clear();
		line = line.trim();
        if (line.isEmpty()) {//���������һ�����ִ�
            return null;
        }
        
        //ȥ���յ���
        line = this.escape(line);
        final String[] tempitem = line.split("[,]");
        int realItemNumber = 0;
        for (int i = 0; i < tempitem.length; ++i) {
            tempitem[i] = tempitem[i].trim();
            if (!tempitem[i].isEmpty()) {
                ++realItemNumber;
            }
        }
        
        //��������������
        final String[] item = new String[realItemNumber];
        realItemNumber = 0;
        for (int j = 0; j < tempitem.length; ++j) {
            tempitem[j] = tempitem[j].trim();
            if (!tempitem[j].isEmpty()) {
                item[realItemNumber] = tempitem[j];
                ++realItemNumber;
            }
        }
        if(realItemNumber <= 1) return null;//�������һ��������autotext��Ŀ
        
        //�����滻���м�ҳ
        final int candiWordNumber = item.length - 1;
        int candiPageNumber;      
        if (candiWordNumber % 9 > 0) {
            candiPageNumber = candiWordNumber / 9 + 1;
        }
        else {
            candiPageNumber = candiWordNumber / 9;
        }
        
        //���찴ҳ�ֵ����飬��ÿһ����ǰ��������
        final String[] pages = new String[candiPageNumber + 1];
        pages[0] = item[0];//��һ��Ϊ����
        int itemIndex = 1;
        for (int k = 1; k <= candiPageNumber; ++k) {
            final StringBuilder candiPage = new StringBuilder();
            for (int i = 0; i < 9 && itemIndex <= candiWordNumber; ++itemIndex, ++i) {
                if (itemIndex == candiWordNumber && i == 0) {
                    candiPage.append(item[itemIndex]);//���ֻ��һ��
                }
                else {
                    candiPage.append(String.valueOf(String.valueOf(i + 1)) + item[itemIndex]);//��ÿ����Ŀǰ��������
                }
            }
            pages[k] = candiPage.toString();
        }//����Ϊֹ���Ѿ���һ����ҳ����õ����飬����ж���滻�ÿ����֮ǰ�Ѿ��������
        
        //��������׼������autotext��Ŀ
        if (candiPageNumber > 1) {//�������һҳ�����ڿ�ͷ���β�������ĵ�������
            pages[1] = String.valueOf(CHINESE_QUOTATION_LEFT) + pages[1];
            pages[pages.length - 1] = pages[pages.length - 1] + CHINESE_QUOTATION_RIGHT;
        }
        
        if (candiPageNumber == 1) {//���ֻ��һҳ
            final String[] nextitem = pages[1].split("[1-9]");//���滻�������ַֿ�
            if (nextitem.length == 1) {//˵��ֻ��һ���滻��
            	result.put(this.recover(pages[0]), pages[1]);
            }
            else {//����ж���滻��
            	result.put(this.recover(pages[0]), pages[1]);
            	result.put(this.recover(pages[1]) + "a", "%b");
                this.writeEntries(pages[1]);
            }
        }
        else {//�����ֹһҳ
            for (int k = 0; k < pages.length; ++k) {
            	//�����ҳ������
            	if (k == pages.length - 1) result.put(recover(pages[k]), recover(pages[1]) + "%B");    //���Ϊ���һҳ���򷭻ص�һҳ   
            	else result.put(recover(pages[k]), recover(pages[k + 1]) + "%B");//��������
                
            	//������������ǰ��
            	if(k == 1) result.put(recover(pages[1]) + "0", recover(pages[1]) + "%B");
            	else if(k > 1) result.put(pages[k] + "0", recover(pages[k - 1]) + "%B");
            	
            	//����������ɾ��
            	if(k != 0) result.put(recover(pages[k]) + "a", "%b");//page[0]�Ǳ���
            	
            	//�����������ҳ�е��滻��
            	this.writeEntries(pages[k]);   	
            }
        }
		return result;
	}
	
	
	private void writeEntries(final String s) {
		// TODO Auto-generated method stub
        final String[] item = s.split("[1-9]");
            for (int i = 1; i < item.length; ++i) {//֮���Դ�1��ʼ������Ϊ��һ��ҪôΪ�գ�ҪôΪ�����š�
            	if (item[i].charAt(item[i].length() - 1) == CHINESE_QUOTATION_RIGHT) {//������һ���ַ����������ţ�˵�������Ƕ�ҳ�е����һҳ
            			result.put(this.recover(s) + getchar(i), "%b" + item[i].substring(0, item[i].length() - 1));
                 }else{//����Ƕ�ҳ�е�����ҳ
                    	result.put(this.recover(s) + getchar(i), "%b" + item[i]);
                 }
            }
	}

	//��ѡ��ʱ�������ֶ�Ӧ����ĸ��ʲô
    String getchar(final int i) {
        String c = "";
        switch (i) {
            case 0: {
                c = "0";
                break;
            }
          case 1: {
                c = "";
                break;
            }
            case 2: {
                c = "e";
                break;
            }
            case 3: {
                c = "r";
                break;
            }
            case 4: {
                c = "s";
                break;
            }
            case 5: {
                c = "d";
                break;
            }
            case 6: {
                c = "f";
                break;
            }
            case 7: {
                c = "z";
                break;
            }
            case 8: {
                c = "x";
                break;
            }
            case 9: {
                c = "c";
                break;
            }
        }
        return c;
    }
	//��ת���ַ��ָ���ԭ״
	private String recover(final String str) {
        final StringBuilder s = new StringBuilder();
        final String[] item = str.split("#");
        for (int i = 0; i < item.length; ++i) {
            if (item[i].equals("NUMBER_ZERO")) {
                s.append("0");
            }
            else if (item[i].equals("NUMBER_ONE")) {
                s.append("1");
            }
            else if (item[i].equals("NUMBER_TWO")) {
                s.append("2");
            }
            else if (item[i].equals("NUMBER_THREE")) {
                s.append("3");
            }
            else if (item[i].equals("NUMBER_FOUR")) {
                s.append("4");
            }
            else if (item[i].equals("NUMBER_FIVE")) {
                s.append("5");
            }
            else if (item[i].equals("NUMBER_SIX")) {
                s.append("6");
            }
            else if (item[i].equals("NUMBER_SEVEN")) {
                s.append("7");
            }
            else if (item[i].equals("NUMBER_EIGHT")) {
                s.append("8");
            }
            else if (item[i].equals("NUMBER_NINE")) {
                s.append("9");
            }
            else if (item[i].equals("CHINESE_QUOTATION_LEFT")) {
                s.append(CHINESE_QUOTATION_LEFT);
            }
            else if (item[i].equals("CHINESE_QUOTATION_RIGHT")) {
                s.append(CHINESE_QUOTATION_RIGHT);
            }
            else if (item[i].equals("SINGLE_QUOTATION")) {
                s.append("#SINGLE_QUOTATION#");
            }
            else if (item[i].equals("SHARP")) {
                s.append("#SHARP#");
            }
            else if (item[i].equals("COMMA")) {
                s.append("#COMMA#");
            }
            else {
                s.append(item[i]);
            }
        }
        return s.toString();
    }
    
	//������ַ�����ת���ַ�
    private String escape(final String str) {
        final StringBuilder s = new StringBuilder();
        for (int i = 0; i < str.length(); ++i) {
            final char c = str.charAt(i);
            if (c == '0') {
                s.append("#NUMBER_ZERO#");
            }
            else if (c == '1') {
                s.append("#NUMBER_ONE#");
            }
            else if (c == '2') {
                s.append("#NUMBER_TWO#");
            }
            else if (c == '3') {
                s.append("#NUMBER_THREE#");
            }
            else if (c == '4') {
                s.append("#NUMBER_FOUR#");
            }
            else if (c == '5') {
                s.append("#NUMBER_FIVE#");
            }
            else if (c == '6') {
                s.append("#NUMBER_SIX#");
            }
            else if (c == '7') {
                s.append("#NUMBER_SEVEN#");
            }
            else if (c == '8') {
                s.append("#NUMBER_EIGHT#");
            }
            else if (c == '9') {
                s.append("#NUMBER_NINE#");
            }
            else if (c == CHINESE_QUOTATION_LEFT) {
                s.append("#CHINESE_QUOTATION_LEFT#");
            }
            else if (c == CHINESE_QUOTATION_RIGHT) {
                s.append("#CHINESE_QUOTATION_RIGHT#");
            }
            else if (c == '\'') {
                s.append("#SINGLE_QUOTATION#");
            }
            else {
                s.append(c);
            }
        }
        return s.toString();
    }

}
