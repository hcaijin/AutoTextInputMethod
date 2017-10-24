package cn.queshw.autotextsetting;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import cn.queshw.autotextinputmethod.R;

public class FilePickerActivity extends Activity {
	// ������Ҫ����������һ��Ҫ�����Ŀ¼������Ҫѡȡ����Ŀ¼�����ļ�
	public static final int EXPORT = 0;
	public static final int IMPORT = 1;
	private String relativeRoot = "/";
	private int purpose = IMPORT;
	private String result;// ��������Ӧ��ʹ��startActivityForResult���������ǽ��

	// �����е�Ԫ��
	ListView fileListView;
	ArrayList<File> fileList = new ArrayList<File>();
	FilePickerAdapter fileListAdapter;
	EditText resultEditText;
	Button yesButton;
	Button noButton;
	Button parentButton;
	Spinner pathSpinner;
	ArrayList<String> pathList = new ArrayList<String>();
	ArrayAdapter<String> spinnerAdapter;

	// Ҫ�õ�����������

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_filepickeractivity);

		// Log.d("Here", "getDataDirectory()=" +
		// Environment.getDataDirectory().toString());
		// Log.d("Here", "getDownloadCacheDirectory()=" +
		// Environment.getDownloadCacheDirectory().toString());
		// Log.d("Here", "getExternalStorageDirectory()=" +
		// Environment.getExternalStorageDirectory().toString());
		// Log.d("Here", "getExternalStoragePublicDirectory()=" +
		// Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString());
		// Log.d("Here", "getExternalStorageState()=" +
		// Environment.getExternalStorageState());
		// Log.d("Here", "getRootDirectory()=" +
		// Environment.getRootDirectory().toString());
		// Log.d("Here", "isExternalStorageRemovable()=" +
		// Environment.isExternalStorageRemovable());
		// ��ȡ����������������
		Intent intent = this.getIntent();
		relativeRoot = intent.getStringExtra("relativeRoot");
		// if(!new File(relativeRoot).exists()) relativeRoot = "/";
		purpose = intent.getIntExtra("purpose", IMPORT);
		// ��ȡ�����е�Ԫ��
		fileListView = (ListView) findViewById(R.id.file_listView_layout_filepickeractivity);
		resultEditText = (EditText) findViewById(R.id.result_editText_layout_filepickeractivity);
		yesButton = (Button) findViewById(R.id.yes_button_layout_filepickeractivity);
		noButton = (Button) findViewById(R.id.no_button_layout_filepickeractivity);
		parentButton = (Button) findViewById(R.id.parent_button_layout_filepickeractivity);
		pathSpinner = (Spinner) findViewById(R.id.path_spinner_layout_filepickeractivity);

		// �����ļ��б��������
		fileListAdapter = new FilePickerAdapter(this, R.layout.filelist,
				fileList);
		fileListAdapter.setNotifyOnChange(false);
		fileListView.setAdapter(fileListAdapter);

		// ���������б��������
		spinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, pathList);
		spinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerAdapter.setNotifyOnChange(false);
		pathSpinner.setAdapter(spinnerAdapter);

		// �������ݹ��캯��
		updateData(relativeRoot, purpose);

		// ���������б�ĵ��������
		pathSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				// Log.d("Here", "position=" + String.valueOf(position));
				updateData(pathList.get(position), purpose);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		// ���á����ϡ���Ŧ�ĵ��������
		parentButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (new File(relativeRoot).getParent() != null)
					updateData(new File(relativeRoot).getParent(), purpose);
			}
		});

		// �����ļ��б�ĵ���¼�������
		fileListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				// TODO Auto-generated method stub

				if (fileList.get(pos).isDirectory()) {// ��������һ��Ŀ¼����򿪽���
					updateData(fileList.get(pos).getPath(), purpose);
					// resultEditText.setText("");
				} else {// ��������һ���ļ������ȡ�ļ���
					resultEditText.setText(fileList.get(pos).getName());
				}
			}
		});

		// ���á��񡱰�Ŧ�ļ�����
		noButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FilePickerActivity.this.setResult(RESULT_CANCELED);
				finish();
			}
		});

		// ���á��ǡ���Ŧ�ļ�����
		yesButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (relativeRoot.equals("/"))
					result = relativeRoot + resultEditText.getText().toString();
				else
					result = relativeRoot + "/"
							+ resultEditText.getText().toString();
				// Log.d("Here", result);
				// ���������Ч��
				// ���purpose��FILE�������ִ�������Ĳ�����һ��Ŀ¼������һ��Ҫ���ڣ�ͬʱ�ɶ�
				// ���purpost��DIR�������ִ�������Ĳ�����һ��Ŀ¼�����Ҳ��ܴ��ڣ����������ᱻ���ǣ�ͬʱ��д
				if (new File(result).isDirectory()) {
					Toast.makeText(FilePickerActivity.this,
							FilePickerActivity.this.getString(R.string.msg1),
							Toast.LENGTH_LONG).show();
					return;
				} else if (purpose == IMPORT) {
					if (!new File(result).exists()) {
						Toast.makeText(
								FilePickerActivity.this,
								FilePickerActivity.this
										.getString(R.string.msg5),
								Toast.LENGTH_LONG).show();
						return;
					} else if (!new File(result).canRead()) {
						Toast.makeText(
								FilePickerActivity.this,
								FilePickerActivity.this
										.getString(R.string.msg3),
								Toast.LENGTH_LONG).show();
						return;
					}
				} else {
					if (new File(result).exists()) {
						if (!new File(result).canWrite()) {
							Toast.makeText(
									FilePickerActivity.this,
									FilePickerActivity.this
											.getString(R.string.msg4),
									Toast.LENGTH_LONG).show();
							return;
						}
						new AlertDialog.Builder(FilePickerActivity.this)
								.setTitle(R.string.msg2)
								.setNegativeButton(R.string.no, null)
								.setPositiveButton(R.string.yes,
										new Dialog.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// TODO Auto-generated method
												// stub
												Intent intent = new Intent();
												intent.putExtra("result",
														result);
												FilePickerActivity.this
														.setResult(RESULT_OK,
																intent);
												finish();
											}
										}).show();

						return;
					}else{
						if (!new File(result).getParentFile().canWrite()) {
							Toast.makeText(
									FilePickerActivity.this,
									FilePickerActivity.this
											.getString(R.string.msg4),
									Toast.LENGTH_LONG).show();
							return;
						}
					}
				}

				Intent intent = new Intent();
				intent.putExtra("result", result);
				FilePickerActivity.this.setResult(RESULT_OK, intent);
				finish();
			}
		});

	}

	// �������ݵĺ������Ӷ���������·���б���ļ��б�
	private void updateData(String path, int purpose) {
		// Log.d("Here", "path = " + path);
		// Log.d("Here", "purpose = " + String.valueOf(purpose));

		// �Ȱ�ԭ���������
		fileList.clear();
		pathList.clear();

		// �����������path�����ڣ�������һ���ļ���������root·������
		File root = new File(path);
		if (!root.exists())
			path = "/";

		// �����ļ��б������
		for (File tempFile : root.listFiles()) {
			fileList.add(tempFile);
		}

		// ���������б������
		while (root != null) {
			pathList.add(root.getPath());
			root = root.getParentFile();
		}

		// ���������б���ļ��б�
		fileListAdapter.notifyDataSetChanged();
		spinnerAdapter.notifyDataSetChanged();
		pathSpinner.setSelection(0);
		relativeRoot = path;
	}

}
