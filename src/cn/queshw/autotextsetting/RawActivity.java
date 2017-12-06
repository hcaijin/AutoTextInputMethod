package cn.queshw.autotextsetting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import cn.queshw.autotextinputmethod.ConstantList;
import cn.queshw.autotextinputmethod.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RawActivity extends Activity {
	public static final int LIMIT = 50;// һ����ȡ5������¼
	private final int LOADED = -1;// ��һ��50���������Ѿ�����
	private final int NOTLOAD = -2;// ��һ��50��������û�м���
	private final int IMPORTED = -3;// �������
	private final int EXPORTED = -4;// �������

	private Handler handler;// ���������첽����
	private int loadtag = NOTLOAD;// ��һ��50�������Ƿ��Ѿ����صı�ǣ�Ĭ��Ϊû�м���

	private int offset = 0;// sql�����ĳ�ʼƫ����
	private int methodId;// ��������method��id

	private ArrayList<RawItem> listdata;// ���ڲ���listview������
	private RawAdapter adapter;// listview �õ�������
	private ListView rawListview;// listview���ڷ���autotext��Ŀ��
	private int totalItems = 0;// �б���������ܹ��м������ݣ������첽���ݼ���

	private EditText searchEditText;// EditText
	private String searchText = "";// ������������ʲô

	private ImageView deleteIcon;// ɾ����ͼ��
	private float downX;// ontouch�¼���x
	private float upX;// ontouch�¼���x
	private int position;// ��������ɾ��ͼ��ļ�����ʱ�õ�
	private Animation animation;// ɾ������
	private View view;// Ҫɾ������ͼ��������һ��list��Ŀ

	private DBOperations dboper;// �������ݿ����
	private String table;// Ҫ�����ı������

	// alertdialog����ͼ��Ԫ�أ��ֱ����ڽ���input��autotext����
	private EditText codeEditText;
	private EditText candidateEditText;
	private final int ADD = -1;// ����AutotextItem��id�����ڱ�ʾ����������Ŀ

	// ���ڵ��뵼������
	File resultFile;// ���ڵ����뵼������
	int lines = 0;// �Ѿ����뵼�������ж�����
	TextView statusTextView;// ������ʾ���뵼��״̬

	// �������õ�������activity�ķ���
	public static void actionStart(Context context, int methodId) {
		Intent intent = new Intent(context, RawActivity.class);
		intent.putExtra("methodId", methodId);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_rawactivity);

		// ��Intent�л��methodId��������Ҫ�����ı������
		Intent intent = getIntent();
		methodId = intent.getIntExtra("methodId", -1);
		//table = "autotext" + String.valueOf(methodId);
		table = "raw" + String.valueOf(methodId);
		animation = AnimationUtils.loadAnimation(this, R.anim.push_out);

		// �ӱ�����ȡ��¼��Ϊlistview��׼����һ����ȡ��������¼��Ȼ����ʾ����
		dboper = new DBOperations(this);
		listdata = dboper.searchRawItems(table, "", LIMIT, offset);
		adapter = new RawAdapter(this, R.layout.rawitem, listdata);
		rawListview = (ListView) findViewById(R.id.raws_listview_layout_rawactivity);
		rawListview.setAdapter(adapter);
		rawListview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				addOrEdit(listdata.get(position).getId());
			}

		});
		// Ϊlistview���ô��������������󻮳���ɾ��ͼ�꣬�����ɾ����Ӧ����Ŀ
		rawListview.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				// ������������ĸ���Ŀ
				position = rawListview.pointToPosition((int) event.getX(),
						(int) event.getY());
				if (position == AdapterView.INVALID_POSITION) {// �������Чλ��
					if (deleteIcon != null) {// ����Ѿ���ʾ��ɾ��ͼ��
						deleteIcon.setVisibility(View.GONE);
					}
					return false;
				}
				// �������Ч��λ�ã���ô�ͻ����Ӧ����Ŀ��View������һ�����ɾ��ͼ�꣬���ں������õ��������
				view = rawListview.getChildAt(position
						- rawListview.getFirstVisiblePosition());
				ImageView tempDeleteIcon = (ImageView) view
						.findViewById(R.id.delete_imageview_rawitem);
				// �������¼�
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					downX = event.getX();
					break;
				case MotionEvent.ACTION_UP:
					upX = event.getX();
					if (deleteIcon != null) {// ���deleteͼ���Ѿ���ʾ������������ͬʱ������touch�¼�
						deleteIcon.setVisibility(View.GONE);
						deleteIcon = null;
						return true;
					} else {// ���ɾ��ͼ��û����ʾ
						if (Math.abs(downX - upX) > 80) {// ���deleteͼ��û����ʾ������������󻮶��������35dp������ʾdeleteͼ��
							tempDeleteIcon.setVisibility(View.VISIBLE);
							deleteIcon = tempDeleteIcon;
							deleteIcon
									.setOnClickListener(new View.OnClickListener() {
										@Override
										public void onClick(View arg0) {
											// TODO Auto-generated method stub
											view.startAnimation(animation);// ���ö���
											animation
													.setAnimationListener(new AnimationListener() {
														@Override
														public void onAnimationEnd(
																Animation animation) {
															// TODO
															// Auto-generated
															// method stub
															dboper.deleteRawItem(
																	methodId,
																	listdata.get(
																			position));
															refreshListView();
															deleteIcon
																	.setVisibility(View.GONE);
															deleteIcon = null;
														}

														@Override
														public void onAnimationRepeat(
																Animation animation) {
															// TODO
															// Auto-generated
															// method stub
														}

														@Override
														public void onAnimationStart(
																Animation animation) {
															// TODO
															// Auto-generated
															// method stub
														}
													});

										}
									});
							return true;
						}
					}
					return false;
				}
				return false;
			}
		});

		// �����б�Ĺ����������������첽��������
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case LOADED:// ��������Ѿ���������
					adapter.notifyDataSetChanged();
					// Toast.makeText(AutotextActivity.this,
					// AutotextActivity.this.getString(R.string.loaded),
					// Toast.LENGTH_SHORT).show();
					break;
				case IMPORTED:
					refreshListView();
					Toast.makeText(RawActivity.this,
							RawActivity.this.getString(R.string.imported),
							Toast.LENGTH_LONG).show();
					statusTextView.setVisibility(View.GONE);
					break;
				case EXPORTED:
					Toast.makeText(RawActivity.this,
							RawActivity.this.getString(R.string.exported),
							Toast.LENGTH_LONG).show();
					statusTextView.setVisibility(View.GONE);
					break;
				case 1:
					statusTextView.setVisibility(View.VISIBLE);
				default:
					statusTextView.setText(String.valueOf(msg.what));
					break;
				}
				// ��ʱ�������б��е�����Ŀ����Ѿ��仯�ˣ��ı������Ƿ��Ѿ����صı�ǣ�������һ�μ���
				if (totalItems != listdata.size()) {
					loadtag = NOTLOAD;
				}
			}
		};
		rawListview.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScroll(AbsListView listview, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

				if (listview.getLastVisiblePosition() == totalItemCount - LIMIT
						/ 2
						&& loadtag == NOTLOAD) {
					loadtag = LOADED;
					totalItems = totalItemCount;// �Ѿֲ���������activity�ı�����

					// ��ʼ�첽��������
					Thread loaddata = new Thread(new Runnable() {
						@Override
						public synchronized void run() {
							// TODO Auto-generated method stub
							ArrayList<RawItem> data = dboper
									.searchRawItems(table, searchText,
											LIMIT, totalItems);
							for (RawItem item : data) {
								listdata.add(item);
							}

							// ���ݼ�����������̷߳�����Ϣ
							Message msg = new Message();
							msg.what = LOADED;
							handler.sendMessage(msg);
							// Log.d("Here", "Data loaded");
						}
					});
					loaddata.start();
				}
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
			}

		});

		// ����������ð���������
		searchEditText = (EditText) findViewById(R.id.search_edittext_layout_rawactivity);		
		searchEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				// �������ݣ�ˢ���б�����
				offset = 0;
				loadtag = NOTLOAD;
				searchText = s.toString();
				refreshListView();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
			}
		});

		// ��õ��뵼��״̬��ʾ��TextView
		statusTextView = (TextView) findViewById(R.id.status_textview_layout_rawactivity);		
	}

	// ////////////////////////////////////////
	// ˢ���б���
	private void refreshListView() {
		listdata.clear();
		for (RawItem item : dboper.searchRawItems(table, searchText,
				LIMIT, offset)) {
			listdata.add(item);
		}
		adapter.notifyDataSetChanged();
		rawListview.setSelection(0);
	}

	// ///////////////////////////////////////////
	// �˵�����
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		this.getMenuInflater().inflate(R.menu.menu_rawactivity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.add_menu_rawactivity:
			addOrEdit(ADD);
			break;
		case R.id.import_menu_rawactivity:
			startFilePickerActivity(
					String.valueOf(Environment.getExternalStorageDirectory()),
					FilePickerActivity.IMPORT);
			break;
		case R.id.export_menu_rawactivity:
			startFilePickerActivity(
					String.valueOf(Environment.getExternalStorageDirectory()),
					FilePickerActivity.EXPORT);
			break;
		}
		return true;
	}

	// ��FilePickerActivity�ĺ��������ڲ˵���
	private void startFilePickerActivity(String relativeRoot, int purpose) {// ��FilePickerActivity��Ӧ����Ҫ��������
		Intent intent = new Intent(this, FilePickerActivity.class);
		intent.putExtra("relativeRoot", relativeRoot);
		intent.putExtra("purpose", purpose);
		this.startActivityForResult(intent, purpose);
	}

	// FilePickerActivity�������ݺ�Ĵ�����
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == Activity.RESULT_OK) {
			String result = data.getStringExtra("result");
			// Log.d("Here", "RequestCode=" + String.valueOf(requestCode)
			// + " | resultCode=" + String.valueOf(resultCode)
			// + " | result=" + result);
			resultFile = new File(result);
			if (requestCode == FilePickerActivity.IMPORT) {// �������
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub

						FileReader fr;
						ArrayList<String[]> data = new ArrayList<String[]>();
						try {
							fr = new FileReader(resultFile);
							BufferedReader br = new BufferedReader(fr);
							String line = "";
							
							int twolevel = 0;//���ڱ�ʶ�����滻����
							boolean isTwoLevel = false;//���ڱ�ʶ��ǰ�Ƿ��ڶ����滻����
							
							while ((line = br.readLine()) != null) {
								line = line.trim();
								
								if(TextUtils.isEmpty(line)) continue;//���Ϊ����������
								if(line.equals("[twolevel]")){
									//�����滻�鿪ʼ
									isTwoLevel = true;
									twolevel--;
									continue;
								}
								else if(line.equals("[/twolevel]")){
									//�����滻�����
									isTwoLevel = false;
									continue;
								}
								
								String[] item = new String[3];
								item[0] = line.substring(0, line.indexOf(','));
								item[1] = line.substring(line.indexOf(',') + 1);
								if(isTwoLevel == true) item[2] = String.valueOf(twolevel);
								else item[2] = String.valueOf(0);
								if(TextUtils.isEmpty(item[0]) || TextUtils.isEmpty(item[1])) continue;//�����һ���յģ����Ƿǳ����滻�����
								
								data.add(item);
								// dboper.importAutotext(table, item[0],
								// item[1]);
								// ֪ͨ��ʾ�������								
								Message msg = new Message();
								msg.what = ++lines;
								if(lines % 500 == 0) handler.sendMessage(msg);
							}
							br.close();
							dboper.importData(methodId, data);

							// ��ɺ�֪ͨ�����Ѿ������
							Message msg = new Message();
							msg.what = IMPORTED;
							handler.sendMessage(msg);

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
				//importAutotexts(result);
			} else {// ��������
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub

						FileWriter fw;
						try {
							fw = new FileWriter(resultFile, false);
							BufferedWriter bw = new BufferedWriter(fw);
							ArrayList<RawItem> tempData = dboper.exportData(methodId);
							int preTwolevel = 0;
							int nextTwolevel = 0;
							RawItem item;
							for (int i = 0; i < tempData.size(); i++) {
								preTwolevel = 0;
								nextTwolevel = 0;
								if(i != 0) preTwolevel = tempData.get(i - 1).getTwolevel();
								if(i != tempData.size() - 1) nextTwolevel = tempData.get(i + 1).getTwolevel();
								item = tempData.get(i);
								
								
								if(item.getTwolevel() < 0 && item.getTwolevel() != preTwolevel) bw.write("[twolevel]\n");
								
								bw.write(ConstantList.escape(item.getCode()) + ","
										+ ConstantList.escape(item.getCandidate()) + "\n");	
								
								if(item.getTwolevel() < 0 && item.getTwolevel() != nextTwolevel) bw.write("[/twolevel]\n");
								// ֪ͨ��ʾ��������
								Message msg = new Message();
								msg.what = ++lines;
								if(lines % 500 == 0) handler.sendMessage(msg);
							}
							bw.close();

							// ��ɺ�֪ͨ�����Ѿ������
							Message msg = new Message();
							msg.what = EXPORTED;
							handler.sendMessage(msg);

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
				//exportAutotexts(result);
			}			
		} else {
			Log.d("Here", "set result cancel!");
		}
	}

	// addOrSave���������ڴ���˵��¼������ӻ����޸�raw��Ŀ
	@SuppressLint("InflateParams")
	private void addOrEdit(final int rawItemId) {
		// ��ȡ�Ի���Ҫ�õ�view��Ȼ��ȡ��view��Ԫ��
		View view = this.getLayoutInflater().inflate(
				R.layout.add_or_edit_rawitem, null);
		codeEditText = (EditText) view
				.findViewById(R.id.code_add_or_edit_raw);
		candidateEditText = (EditText) view
				.findViewById(R.id.candidate_add_or_edit_raw);
		if (rawItemId != ADD) {// ��������id�Ų���ADD����ʾ����Ҫ�޸���Ŀ������������
			RawItem item = dboper.getRawItem(table, rawItemId);
			codeEditText.setText(item.getCode());
			candidateEditText.setText(item.getCandidate());
		}

		// ����һ��AlertDialog�������û��޸Ļ���������
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setTitle(R.string.autotextitem)
				.setView(view)
				.setCancelable(true)
				.setPositiveButton(R.string.save, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dboper.addOrSaveRawItem(methodId, codeEditText
								.getText().toString(), candidateEditText
								.getText().toString(), rawItemId);
						refreshListView();
					}
				}).setNeutralButton(R.string.comma, null)
				.setNegativeButton(R.string.cancel, null).show();

		// Ȼ���ֶ�������altertdialog�����԰�Ŧ����������view.onClickListener�������Ļ�һ����Ŧ�Ͳ���Ĭ�ϹرնԻ�����
		dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						candidateEditText.setText(candidateEditText.getText() + "#COMMA# #SHARP#");
						candidateEditText.setSelection(candidateEditText
								.getText().length());
					}
				});
	}

}
