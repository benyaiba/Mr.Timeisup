package net.yaiba.timiup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.yaiba.timiup.db.TimiUpDB;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class EditActivity extends Activity {
	private TimiUpDB TimiUpDB;
	private Cursor mCursor;
	private EditText FoodName;
	private Spinner EatTime;
	private EditText CreateTime;
	private EditText EatWhere;
	private EditText Remark;

	private TextView tv_qh;
	private TextView tv_qb;

	private EditText showDate = null;
	private int mYear;
	private int mMonth;
	private int mDay;

	private int RECORD_ID = 0;

	private final static int DIALOG=1;
	boolean[] foodNameClickFlags=null;//初始复选情况
	String[] foodNameitems=null;
	String foodNameSelectedResults = "";

	boolean[] eatWhereClickFlags=null;//初始复选情况
	String[] eatWhereitems=null;
	String eatWhereSelectedResults = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		TimiUpDB = new TimiUpDB(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_edit);
		RECORD_ID = this.getIntent().getIntExtra("INT", RECORD_ID);

		setUpViews();

		Button bn_add = (Button) findViewById(R.id.add);
		bn_add.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (update()) {
					Intent mainIntent = new Intent(EditActivity.this, MainActivity.class);
					startActivity(mainIntent);
					overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
					setResult(RESULT_OK, mainIntent);
					finish();
				}
			}
		});

		EatWhere = (EditText)findViewById(R.id.eat_where);

		Button bt_food_name_frequent = (Button) findViewById(R.id.food_name_frequent);
		bt_food_name_frequent.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			public void onClick(View v) {
				// 显示对话框
				showDialog(1);
			}
		});

		Button bt_eat_where_frequent = (Button) findViewById(R.id.eat_where_frequent);
		bt_eat_where_frequent.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			public void onClick(View v) {
				// 显示对话框
				showDialog(2);
			}
		});



	}


	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
			case 1:
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("我吃过以下食品...");

				Map<String, Integer> foodsStringMap = new HashMap<String, Integer>();
				TimiUpDB = new TimiUpDB(EditActivity.this);
				mCursor = TimiUpDB.getAllGoodName();
				for(mCursor.moveToFirst();!mCursor.isAfterLast();mCursor.moveToNext()) {
					String foodName = mCursor.getString(mCursor.getColumnIndex("food_name"));
					//Log.v("debug",foodName);

					foodName = foodName.replaceAll(" ","");//替换空格
					foodName = foodName.replaceAll("　","");//替换空格
					foodName = foodName.replaceAll("，",",");//全角逗号替换成半角逗号
					foodName = foodName.replaceAll("[',']+", ",");//将一个或多个半角逗号变成一个半角逗号
					String[] foodNamesTmp = foodName.split(",");

					for (int i = 0; i < foodNamesTmp.length; i++) {
						foodName = foodNamesTmp[i];
						if(foodsStringMap.containsKey(foodName)){
							foodsStringMap.put(foodName, foodsStringMap.get(foodName)+1);
						} else {
							foodsStringMap.put(foodName, 1);
						}
					}
				}

				List<Map.Entry<String, Integer>> foodInfosMap =	new ArrayList<Map.Entry<String, Integer>>(foodsStringMap.entrySet());

				//对map排序
				Collections.sort(foodInfosMap, new Comparator<Map.Entry<String, Integer>>() {
					public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
						//升序，按照名称升序排序
						//return (o1.getKey()).compareTo(o2.getKey());
						//降序，按照使用次数降序排序
						//return (o2.getValue()).compareTo(o1.getValue());
						String name1=o1.getKey();
						String name2=o2.getKey();
						Collator instance = Collator.getInstance(Locale.CHINA);
						return instance.compare(name1, name2);


					}
				});

				//每次点击时清空选择项
				foodNameClickFlags=null;
				foodNameSelectedResults = "";

				if(!foodInfosMap.equals(null)){
					foodNameitems = new String[foodInfosMap.size()];
					foodNameClickFlags = new boolean [foodInfosMap.size()];
				}

				//Log.v("debug","=====map info===sort===");
				int foodNameIndex = 0;
				for(Map.Entry<String,Integer> mapping:foodInfosMap){
					//Log.v("debug",mapping.getKey()+":"+mapping.getValue().toString());
					if(mapping.getValue()>=5){
						foodNameitems[foodNameIndex] = mapping.getKey()+" ("+mapping.getValue()+"次)";
					} else {
						foodNameitems[foodNameIndex] = mapping.getKey();
					}

					foodNameClickFlags[foodNameIndex] = false;//设置默认选中状态

					String fn = FoodName.getText().toString();
					foodNameSelectedResults = fn;

					fn = fn.replaceAll(" ","");//替换空格
					fn = fn.replaceAll("　","");//替换空格
					fn = fn.replaceAll("，",",");//全角逗号替换成半角逗号
					fn = fn.replaceAll("[',']+", ",");//将一个或多个半角逗号变成一个半角逗号
					String[] foodNamesTmp = fn.split(",");


					for(int i=0;i<foodNamesTmp.length;i++){
						if(foodNamesTmp[i].equals(mapping.getKey())){
							foodNameClickFlags[foodNameIndex] = true;
							break;
						}
					}


					//Log.v("debug","view->"+mapping.getKey()+":"+mapping.getValue().toString());
					foodNameIndex++;
				}
				//Log.v("debug","=====items===");
				//Log.v("debug",foodNameitems.length+"");
				builder.setMultiChoiceItems(foodNameitems, foodNameClickFlags, new DialogInterface.OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked) {
						foodNameClickFlags[which]=isChecked;
						foodNameSelectedResults = "";
						for (int i = 0; i < foodNameClickFlags.length; i++) {
							if(foodNameClickFlags[i])
							{
								String n = foodNameitems[i];
								String[] m = n.split(" ");
								foodNameSelectedResults=foodNameSelectedResults + m[0]+",";
							}
						}
						//去掉结尾的逗号
						if(!foodNameSelectedResults.isEmpty()){
							foodNameSelectedResults = foodNameSelectedResults.substring(0,foodNameSelectedResults.length()-1);
						}

						//Log.v("debug","我点了！which:"+which+",name:"+foodNameitems[which]);
					}
				});
				builder.setPositiveButton("就选这些（点击后，之前填写的将被清空）", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//选择的食物名称赋值到前台文本框中
						FoodName.setText(foodNameSelectedResults);
					}
				});
				dialog = builder.create();
				break;

			case 2:
				AlertDialog.Builder eatWhereBbuilder = new AlertDialog.Builder(this);
				eatWhereBbuilder.setTitle("我去过以下地方...");

				Map<String, Integer> whereStringMap = new HashMap<String, Integer>();
				TimiUpDB = new TimiUpDB(EditActivity.this);
				//mCursor = TimiUpDB.getAllEatWhere();
				for(mCursor.moveToFirst();!mCursor.isAfterLast();mCursor.moveToNext()) {
					String eatWhere = mCursor.getString(mCursor.getColumnIndex("eat_where"));
					//Log.v("debug",foodName);

					eatWhere = eatWhere.replaceAll(" ","");//替换空格
					eatWhere = eatWhere.replaceAll("　","");//替换空格
					eatWhere = eatWhere.replaceAll("，",",");//全角逗号替换成半角逗号
					eatWhere = eatWhere.replaceAll("[',']+", ",");//将一个或多个半角逗号变成一个半角逗号
					String[] foodNamesTmp = eatWhere.split(",");

					for (int i = 0; i < foodNamesTmp.length; i++) {
						eatWhere = foodNamesTmp[i];
						if(!eatWhere.isEmpty()){
							if(whereStringMap.containsKey(eatWhere)){
								whereStringMap.put(eatWhere, whereStringMap.get(eatWhere)+1);
							} else {
								whereStringMap.put(eatWhere, 1);
							}
						}

					}
				}

				List<Map.Entry<String, Integer>> eatWhereInfosMap =	new ArrayList<Map.Entry<String, Integer>>(whereStringMap.entrySet());

				//对map排序
				Collections.sort(eatWhereInfosMap, new Comparator<Map.Entry<String, Integer>>() {
					public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
						//升序，按照名称升序排序
						//return (o1.getKey()).compareTo(o2.getKey());
						//降序，按照使用次数降序排序
						//return (o2.getValue()).compareTo(o1.getValue());
						String name1=o1.getKey();
						String name2=o2.getKey();
						Collator instance = Collator.getInstance(Locale.CHINA);
						return instance.compare(name1, name2);


					}
				});

				//每次点击时清空选择项
				eatWhereClickFlags=null;

				if(!eatWhereInfosMap.equals(null)){
					eatWhereitems = new String[eatWhereInfosMap.size()];
					eatWhereClickFlags = new boolean [eatWhereInfosMap.size()];
				}

				//Log.v("debug","=====map info===sort===");
				int eatWhereIndex = 0;
				for(Map.Entry<String,Integer> mapping:eatWhereInfosMap){
					//Log.v("debug",mapping.getKey()+":"+mapping.getValue().toString());
					if(mapping.getValue()>=5){
						eatWhereitems[eatWhereIndex] = mapping.getKey()+" ("+mapping.getValue()+"次)";
					} else {
						eatWhereitems[eatWhereIndex] = mapping.getKey();
					}

					eatWhereClickFlags[eatWhereIndex] = false;//设置默认选中状态
					//Log.v("debug","view->"+mapping.getKey()+":"+mapping.getValue().toString());

                    String ew = EatWhere.getText().toString();

                    foodNameSelectedResults = ew;
                    ew = ew.replaceAll(" ","");//替换空格
                    ew = ew.replaceAll("　","");//替换空格
                    ew = ew.replaceAll("，",",");//全角逗号替换成半角逗号
                    ew = ew.replaceAll("[',']+", ",");//将一个或多个半角逗号变成一个半角逗号
                    String[] eatWhereTmp = ew.split(",");

                    for(int i=0;i<eatWhereTmp.length;i++){
                        if(eatWhereTmp[i].equals(mapping.getKey())){
                            eatWhereClickFlags[eatWhereIndex] = true;
                            break;
                        }
                    }

					eatWhereIndex++;
				}
				//Log.v("debug","=====items===");
				//Log.v("debug",foodNameitems.length+"");
				eatWhereBbuilder.setMultiChoiceItems(eatWhereitems, eatWhereClickFlags, new DialogInterface.OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked) {
						eatWhereClickFlags[which]=isChecked;
						eatWhereSelectedResults = "";
						for (int i = 0; i < eatWhereClickFlags.length; i++) {
							if(eatWhereClickFlags[i])
							{
								String n = eatWhereitems[i];
								String[] m = n.split(" ");
								eatWhereSelectedResults=eatWhereSelectedResults + m[0]+",";
							}
						}
						//去掉结尾的逗号
						if(!eatWhereSelectedResults.isEmpty()){
							eatWhereSelectedResults = eatWhereSelectedResults.substring(0,eatWhereSelectedResults.length()-1);
						}

						//Log.v("debug","我点了！which:"+which+",name:"+foodNameitems[which]);
					}
				});
				eatWhereBbuilder.setPositiveButton("就在这了！（点击后，之前填写的将被清空）", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//选择的地点赋值到前台文本框中
						EatWhere.setText(eatWhereSelectedResults);
					}
				});
				dialog = eatWhereBbuilder.create();
				break;

			default:
				break;
		}

		return dialog;
	}





	public void getDate(View v) {

		new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				mYear = year;
				mMonth = monthOfYear;
				mDay = dayOfMonth;

				CreateTime.
						setText(new StringBuilder().append(mYear).append("-")
						.append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1)).append("-")
						.append((mDay < 10) ? "0" + mDay : mDay));
			}
		}, mYear, mMonth-1, mDay).show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Intent mainIntent = new Intent(EditActivity.this,MainActivity.class);
			   startActivity(mainIntent);
			   overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
			   setResult(RESULT_OK, mainIntent);  
			   finish(); 
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	@SuppressWarnings("deprecation")
	public Boolean update(){
	
		String foodname = FoodName.getText().toString().replace("\n","");
		String eattime = EatTime.getSelectedItem().toString();
		String eatwhere = EatWhere.getText().toString().replace("\n","");
		String createTime = CreateTime.getText().toString().replace("\n","");
		String remark = Remark.getText().toString();

		if (foodname.equals("")){
			Toast.makeText(this, "[吃的啥]没填", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (foodname.length() >100){
			Toast.makeText(this, "[吃的啥]食物过多，不能超过100个文字", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (eattime.equals("")){
			Toast.makeText(this, "[饭点儿]没有选择，请选择", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (eatwhere.length() >100){
			Toast.makeText(this, "[享用地点]文字过多，不能超过100个文字", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (createTime.length() > 14){
			Toast.makeText(this, "[日期]长度不能超过14个文字，请检查格式是否正确", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (createTime.length() < 10){
			Toast.makeText(this, "[日期]长度不能小于10个文字，请检查格式是否正确", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (remark.length() >200){
			Toast.makeText(this, "[备注]长度不能超过200个文字", Toast.LENGTH_SHORT).show();
			return false;
		}


		try {
			TimiUpDB.update(RECORD_ID, foodname, eattime, eatwhere, remark ,createTime ,createTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mCursor.requery();
		Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
		return true;
	}
	
	public void setUpViews(){
		TimiUpDB = new TimiUpDB(this);
		mCursor = TimiUpDB.getRecordInfo(RECORD_ID);

		FoodName = (EditText)findViewById(R.id.food_name);

		CreateTime = (EditText)findViewById(R.id.create_time);
		EatWhere = (EditText)findViewById(R.id.eat_where);
		Remark = (EditText)findViewById(R.id.remark);

		FoodName.setText(mCursor.getString(1));
		Log.v("debug","mCursor.getString(2):"+mCursor.getString(2));
		Log.v("debug","getEatTimeIndex(mCursor.getString(2)):"+(mCursor.getString(2)));
		CreateTime.setText(mCursor.getString(5));
		EatWhere.setText(mCursor.getString(3));
		Remark.setText(mCursor.getString(4));


		String[] data = mCursor.getString(5).split("-");
        if(data.length==3){
            mYear = Integer.parseInt(data[0]);
            mMonth = Integer.parseInt(data[1]);
            mDay = Integer.parseInt(data[2]);
        } else {
            mYear = 0;
            mMonth = 0;
            mDay = 0;
        }

	}
}
