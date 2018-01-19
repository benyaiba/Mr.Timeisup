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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.yaiba.timiup.db.TimiUpDB;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static net.yaiba.timiup.utils.Custom.isValidDate;


public class EditActivity extends Activity {
	private TimiUpDB TimiUpDB;
	private Cursor mCursor;

	private EditText GoodName;
	private EditText ProductDate;
	private EditText EndDate;
	private EditText BuyDate;
	private EditText Remark;
	private RadioGroup StatusGroup;

	private int mYear;
	private int mMonth;
	private int mDay;

	private int RECORD_ID = 0;

	private String statusValue = "0";


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		TimiUpDB = new TimiUpDB(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_edit);
		RECORD_ID = this.getIntent().getIntExtra("INT", RECORD_ID);

		setUpViews();

		Button bn_product_date = (Button)findViewById(R.id.bn_product_date);
		bn_product_date.setOnClickListener(new OnClickListener(){
			public void  onClick(View v)
			{
				getDate(ProductDate);
			}
		});

		Button bn_end_date = (Button)findViewById(R.id.bn_end_date);
		bn_end_date.setOnClickListener(new OnClickListener(){
			public void  onClick(View v)
			{
				getDate(EndDate);
			}
		});


		Button bn_buy_date = (Button)findViewById(R.id.bn_buy_date);
		bn_buy_date.setOnClickListener(new OnClickListener(){
			public void  onClick(View v)
			{
				getDate(BuyDate);
			}
		});

		StatusGroup = (RadioGroup) findViewById(R.id.ra_status_group);
	    StatusGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
					case R.id.ra_unuse:
						statusValue = "0";
						break;
					case R.id.ra_used:
						statusValue = "1";
						break;
				}
			}
		});



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





	}

	private void setDateTime(Boolean flag,EditText editText){
		if(flag){
			final Calendar c = Calendar.getInstance();
			mYear = c.get(Calendar.YEAR);
			mMonth = c.get(Calendar.MONTH);
			mDay = c.get(Calendar.DAY_OF_MONTH);
		}
		editText.setText(new StringBuilder().append(mYear).append("-")
				.append((mMonth+1) < 10 ? "0" + (mMonth+1) : (mMonth+1)).append("-")
				.append((mDay < 10) ? "0" + mDay : mDay));
	}


	public void getDate(final EditText editText) {

		String[] data = editText.getText().toString().split("-");
		Log.v("v_debug_data",editText.getText().toString());

		mYear = Integer.parseInt(data[0]);
		mMonth = Integer.parseInt(data[1]);
		mDay = Integer.parseInt(data[2]);

		Log.v("v_debug_mYear",mYear+"");
		Log.v("v_debug_mMonth",mMonth+"");
		Log.v("v_debug_mDay",mDay+"");

		new DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT,new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				EditActivity.this.mYear = year;
				mMonth = monthOfYear;
				mDay = dayOfMonth;

				Log.v("v_debug_Year",year+"");
				Log.v("v_debug_monthOfYear",monthOfYear+"");
				Log.v("v_debug_dayOfMonth",dayOfMonth+"");

				setDateTime(false, editText);
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
	

	public Boolean update(){

		GoodName = (EditText)findViewById(R.id.good_name);
		ProductDate = (EditText)findViewById(R.id.product_date);
		EndDate = (EditText) findViewById(R.id.end_date);
		BuyDate = (EditText) findViewById(R.id.buy_date);
		Remark = (EditText)findViewById(R.id.remark);

		String goodname = GoodName.getText().toString().replace("\n","");
		String productdate = ProductDate.getText().toString().replace("\n","");
		String enddate = EndDate.getText().toString().replace("\n","");
		String buydate = BuyDate.getText().toString().replace("\n","");
		String remark = Remark.getText().toString();

		if (goodname.equals("")){
			Toast.makeText(this, "[名称]没填", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (goodname.length() >100){
			Toast.makeText(this, "[名称]不能超过100个文字", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (productdate.equals("")){
			Toast.makeText(this, "[生产日期]没填", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (!isValidDate(productdate)){
			Toast.makeText(this, "[生产日期]无效", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (enddate.equals("")){
			Toast.makeText(this, "[到期日]没填", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (!isValidDate(enddate)){
			Toast.makeText(this, "[到期日]无效", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (productdate.equals(enddate)){
			Toast.makeText(this, "[生产日期][到期日]不能相同", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (buydate.equals("")){
			Toast.makeText(this, "[购买日期]没填", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (!isValidDate(buydate)){
			Toast.makeText(this, "[购买日期]无效", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (remark.length() >200){
			Toast.makeText(this, "[备注]长度不能超过200个文字", Toast.LENGTH_SHORT).show();
			return false;
		}

		try {
			TimiUpDB.update(RECORD_ID, goodname, productdate, enddate, buydate, statusValue, remark);
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

		GoodName = (EditText)findViewById(R.id.good_name);
		ProductDate = (EditText) findViewById(R.id.product_date);
		EndDate = (EditText) findViewById(R.id.end_date);
		BuyDate = (EditText) findViewById(R.id.buy_date);
		StatusGroup = (RadioGroup) findViewById(R.id.ra_status_group);

		Remark = (EditText)findViewById(R.id.remark);


		GoodName.setText(mCursor.getString(mCursor.getColumnIndex("good_name")));
		ProductDate.setText(mCursor.getString(mCursor.getColumnIndex("product_date")));
		EndDate.setText(mCursor.getString(mCursor.getColumnIndex("end_date")));
		BuyDate.setText(mCursor.getString(mCursor.getColumnIndex("buy_date")));

		String status = mCursor.getString(mCursor.getColumnIndex("status"));
		if("0".equals(status)){
			StatusGroup.check(R.id.ra_unuse);
		} else {
			StatusGroup.check(R.id.ra_used);
		}

		Remark.setText(mCursor.getString(mCursor.getColumnIndex("remark")));




	}
}
