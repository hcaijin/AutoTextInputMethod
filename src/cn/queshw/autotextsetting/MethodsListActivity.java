package cn.queshw.autotextsetting;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import cn.queshw.autotextinputmethod.R;

public class MethodsListActivity extends Activity {
	public final static int ADD = -1;// ��װ��¼
	private ListView methodsListview;
	private ArrayList<MethodItem> methodsItemList;
	private MethodsAdapter adapter;
	private DBOperations dboper;

	// Ϊ�󻮳��Ĳ˵����õı���
	private RelativeLayout slideMenu;
	private ImageView editImageView;
	private ImageView deleteImageView;
	private ImageView isdefaultImageView;
	private int position;// �������listview�е��ĸ�ͼ��Ŀ
	private Animation animation;
	private View view;// Ҫʹ�ö�������ͼ��������һ��list��Ŀ
	float downX;
	float upX;

	// for AlertDialog view
	String name;
	int isDefault = MethodItem.NOTDEFAULT;
	EditText ed;
	Switch sw;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.methods_list);

		dboper = new DBOperations(this);
		animation = AnimationUtils.loadAnimation(this, R.anim.push_out);// ������������
		methodsListview = (ListView) findViewById(R.id.methods_listview_activity_main);

		methodsItemList = dboper.loadMethodsData();
		this.registerForContextMenu(methodsListview);
		adapter = new MethodsAdapter(this, R.layout.methoditem, methodsItemList);
		methodsListview.setAdapter(adapter);
		methodsListview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				// TODO Auto-generated method stub
				AutotextActivity.actionStart(MethodsListActivity.this, methodsItemList.get(position).getId());
				// Log.d("Here", "Position=" + String.valueOf(position) +
				// " clicked");
			}
		});

		// ����listview�Ĵ����¼�
		methodsListview.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				// ȷ���㵽������
				position = methodsListview.pointToPosition((int) event.getX(), (int) event.getY());
				if (position == AdapterView.INVALID_POSITION) {// ������������Чλ��
					if (slideMenu != null) {// �����󻮲˵��Ѿ���ʾ
						slideMenu.setVisibility(View.GONE);
						slideMenu = null;
					}
					return false;
				}
				// �������Чλ��
				view = methodsListview.getChildAt(position - methodsListview.getFirstVisiblePosition());// ����λ�û��Ҫ��������ͼ
				RelativeLayout tempslideMenu = (RelativeLayout) view.findViewById(R.id.slidemenu_linearlayout_methoditem);
				isdefaultImageView = (ImageView) view.findViewById(R.id.isdefalut_imageview_methoditem);// ����ID�����Ӧ���󻮲˵��ϵ���Ŀ��Ϊ�Ժ����õ����������׼��
				editImageView = (ImageView) view.findViewById(R.id.edit_imageview_methoditem);
				deleteImageView = (ImageView) view.findViewById(R.id.delete_imageview_methoditem);

				// ���������������¼�
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					downX = event.getX();
					break;
				case MotionEvent.ACTION_UP:
					upX = event.getX();
					// ����󻮲˵��Ѿ���ʾ����ô�����أ�ͬʱ���ĵ��¼�
					if (slideMenu != null) {
						slideMenu.setVisibility(View.GONE);
						slideMenu = null;
						return true;
					} else {// ����󻮲˵���û����ʾ
						if (Math.abs(downX - upX) > 35) {// ������󻮶��������35dp������ʾ�󻮲˵�
							tempslideMenu.setVisibility(View.VISIBLE);
							slideMenu = tempslideMenu;
							// ���������ø����˵���ĵ�������¼�
							// ����ΪĬ�ϡ��ļ�����
							isdefaultImageView.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									dboper.addOrSaveMethodItem(methodsItemList.get(position).getName(), MethodItem.DEFAULT,
											methodsItemList.get(position).getId());
									slideMenu.setVisibility(View.GONE);
									slideMenu = null;
									refresh();
								}
							});
							// ���޸ġ��ļ�����
							editImageView.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									addOrEdit(methodsItemList.get(position).getId());
									slideMenu.setVisibility(View.GONE);
									slideMenu = null;
									refresh();
								}
							});
							// ��ɾ�����ļ�����
							deleteImageView.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									view.startAnimation(animation);
									animation.setAnimationListener(new AnimationListener() {
										@Override
										public void onAnimationEnd(Animation animation) {
											// TODO
											// Auto-generated
											// method stub
											dboper.deleteMethodItem("methods", methodsItemList.get(position).getId());
											refresh();
											slideMenu.setVisibility(View.GONE);
											slideMenu = null;
										}

										@Override
										public void onAnimationRepeat(Animation animation) {
											// TODO
											// Auto-generated
											// method stub
										}

										@Override
										public void onAnimationStart(Animation animation) {
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
				}
				return false;
			}

		});

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		refresh();
	}

	// ����ˢ���б�
	private void refresh() {
		// TODO Auto-generated method stub
		// Log.d("Here", "refresh");
		methodsItemList.clear();
		for (MethodItem item : dboper.loadMethodsData()) {
			methodsItemList.add(item);
		}
		adapter.notifyDataSetChanged();
	}

	// @Override
	// public void onCreateContextMenu(ContextMenu menu, View v,
	// ContextMenuInfo menuInfo) {
	// // TODO Auto-generated method stub
	// super.onCreateContextMenu(menu, v, menuInfo);
	// menu.add(0, 0, 0, R.string.edit);
	// menu.add(0, 2, 0, R.string.setdefault);
	// menu.add(0, 1, 0, R.string.delete);
	// }
	//
	// @Override
	// public boolean onContextItemSelected(MenuItem item) {
	// // TODO Auto-generated method stub
	// AdapterView.AdapterContextMenuInfo info = (AdapterContextMenuInfo)
	// item.getMenuInfo();
	// MethodItem methodItem = methodsItemList.get(info.position);
	// int id = methodItem.getId();
	//
	// switch(item.getItemId()){
	// case 0://�޸�
	// //Log.d("Here", "0 click");
	// addOrEdit(id);
	// break;
	// case 1://ɾ��
	// dboper.deleteMethodItem("methods", id);
	// //Log.d("Here", "1 click");
	// refresh();
	// break;
	// case 2://��ΪĬ��
	// dboper.addOrSaveMethodItem(methodItem.getName(), MethodItem.DEFAULT, id);
	// refresh();
	// break;
	// }
	// return true;
	// }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_methodsactivity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.add_menu_methodactivity:
			addOrEdit(ADD);
			break;
		case R.id.loaddefault_menu_methodactivity:
			loadDefault();
			break;
		case R.id.help_menu_methodactivity:
			help();
			break;
		}
		return true;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case KeyEvent.KEYCODE_C:
			addOrEdit(ADD);
			break;
		case KeyEvent.KEYCODE_L:
			loadDefault();
			break;
		case KeyEvent.KEYCODE_H:
			help();
			break;
		default:
			return super.onKeyUp(keyCode, event);
		}
		return true;
	}

	// �˵����
	//help�˵���
	private void help() {
		Intent intent = new Intent(this, HelpActivity.class);
		startActivity(intent);
	}
	
	//�����Դ��ʿ�Ĳ˵���
	private void loadDefault() {
		try {
			final AssetManager assetManager = this.getAssets();
			final String[] list = assetManager.list("dicts");
			StringBuilder s = new StringBuilder();
			for (int i = 0; i < list.length; i++) {
				if(list[i].equals("01wubi_pinyin")){
					s.append("1��" + this.getString(R.string.wubi_pinyin) + "\n");
				}else if(list[i].equals("02yinwen")){
					s.append("2��" + this.getString(R.string.yinwen) + "\n");
				}
				else if(list[i].equals("03code_search")){
					s.append("3��" + this.getString(R.string.code_search) + "\n");
				}else{
					s.append(list[i] + "\n");
				}				
			}

			AlertDialog dialog = new AlertDialog.Builder(this).setTitle(getString(R.string.importdefault)).setMessage(s)
					.setNegativeButton(R.string.no, null).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							ImportDefaultActivity.startAction(MethodsListActivity.this);
						}
					}).show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	

	
	//���ӻ��޸Ĵʿ�Ĳ˵���
	private void addOrEdit(final int id) {
		View view = this.getLayoutInflater().inflate(R.layout.add_or_edit_method, null);
		ed = (EditText) view.findViewById(R.id.name_add_or_edit_method);
		sw = (Switch) view.findViewById(R.id.isdefault_add_or_edit_method);

		if (id != ADD) {
			MethodItem item = dboper.getMethodItem(id);
			ed.setText(item.getName());
			isDefault = item.getIsDefault();
			if (item.getIsDefault() == MethodItem.DEFAULT) {
				sw.setChecked(true);
			} else {
				sw.setChecked(false);
			}
		}

		sw.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				isDefault = isChecked ? MethodItem.DEFAULT : MethodItem.NOTDEFAULT;
				// Log.d("Here", "isDefault=" + String.valueOf(isDefault));
			}
		});

		AlertDialog dialog = new AlertDialog.Builder(this).setTitle(getString(R.string.title)).setView(view).setCancelable(true)
				.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						// TODO Auto-generated method stub
						isDefault = MethodItem.NOTDEFAULT;
						refresh();
						// Log.d("Here", "cancel isDefault=" +
						// String.valueOf(isDefault));
					}
				})

				.setNegativeButton(R.string.cancel, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						isDefault = MethodItem.NOTDEFAULT;
						refresh();
						// Log.d("Here", "Dismiss isDefault=" +
						// String.valueOf(isDefault));
					}
				})

				.setPositiveButton(R.string.save, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						name = ed.getText().toString();
						dboper.addOrSaveMethodItem(name, isDefault, id);
						isDefault = MethodItem.NOTDEFAULT;
						refresh();
					}
				}).create();
		dialog.show();
	}
}