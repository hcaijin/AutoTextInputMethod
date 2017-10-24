package cn.queshw.autotextsetting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.queshw.autotextinputmethod.R;

public class ImportDefaultActivity extends Activity {
	private TextView filenameTextview;
	private TextView statusTextview;
	private ProgressBar importProgressBar;
	private Handler handler;

	private DBOperations dboper;// �������ݿ����

	String[] list;// ���ڴ��assets/dicts/Ŀ¼����Ҫ������ļ����б�
	String[] fileName;//���ڴ��assets/dicts/Ŀ¼���ļ���Ӧ���������뷨����
	AssetManager assetManager;
	int lines = 0;

	static void startAction(Context context) {
		Intent intent = new Intent(context, ImportDefaultActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.importdefault);

		dboper = new DBOperations(this);

		filenameTextview = (TextView) findViewById(R.id.filename_textview_layout_importdefault);
		importProgressBar = (ProgressBar) findViewById(R.id.import_progressbar_layout_importdefault);
		statusTextview = (TextView) findViewById(R.id.status_textview_layout_importdefault);

		assetManager = ImportDefaultActivity.this.getAssets();
		try {
			list = assetManager.list("dicts");
			fileName = new String[list.length];
			for (int i = 0; i < list.length; i++) {
				if(list[i].equals("01wubi_pinyin")){
					fileName[i] = this.getString(R.string.wubi_pinyin);
				}else if(list[i].equals("02yinwen")){
					fileName[i] = this.getString(R.string.yinwen);
				}
				else if(list[i].equals("03code_search")){
					fileName[i] = this.getString(R.string.code_search);
				}
				else{
					fileName[i] = list[i];
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		handler = new Handler() {			
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub				
				if (msg.what != -1) {
					filenameTextview.setText(fileName[msg.what]);
					lines = msg.arg1;
					//Log.d("Here", list[msg.what] + "|" + String.valueOf(lines));
				} else {
					//Log.d("Here", "|" + String.valueOf(msg.arg2));
					importProgressBar.setProgress(msg.arg2 * 100 / lines );
					statusTextview.setText(String.valueOf(msg.arg2) + "/" + String.valueOf(lines));
				}
			}
		};

		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					for (int i = 0; i < list.length; i++) {
						// ȡ���ļ�������
						InputStream is = assetManager.open("dicts/" + list[i]);
						BufferedReader br = new BufferedReader(new InputStreamReader(is));
						br.mark(1000000);
						int lines = 0;
						while (br.readLine() != null) {
							lines++;
						}
						//Log.d("Here", list[i] + "|" + String.valueOf(lines));

						// ֪ͨ�����̸��µ���״̬
						Message msg = Message.obtain();
						msg.what = i;
						msg.arg1 = lines;
						handler.sendMessage(msg);

						// ��ʼ���룬����ʾ����
						int count = 0;
						String line;
						br.reset();
						int id = dboper.addOrSaveMethodItem(fileName[i], MethodItem.NOTDEFAULT, MethodsListActivity.ADD);
						ArrayList<String[]> data = new ArrayList<String[]>();
						while ((line = br.readLine()) != null) {
							count++;
							String[] item = new String[2];
							item = line.split(",");
							data.add(item);							
							if(count % 500 == 0) {
								Message msg2 = Message.obtain();
								msg2.what = -1;
								msg2.arg2 = count;
								handler.sendMessage(msg2);
							}
						}
						dboper.importData("autotext"+String.valueOf(id), data);
						br.close();
					}
					ImportDefaultActivity.this.finish();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}).start();

	}
}
