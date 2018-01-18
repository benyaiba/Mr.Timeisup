package net.yaiba.timiup;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.Toast;
import net.yaiba.timiup.db.TimiUpDB;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;



public class AddActivity extends Activity {

	private TimiUpDB TimiUpDB;

	private EditText GoodName;
	private EditText ProductDate;
	private EditText EndDate;
	private EditText BuyDate;
	private EditText Remark;

	private int mYear;
	private int mMonth;
	private int mDay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		TimiUpDB = new TimiUpDB(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_add);

//		final Calendar c = Calendar.getInstance();
//		mYear = c.get(Calendar.YEAR);
//		mMonth = c.get(Calendar.MONTH);
//		mDay = c.get(Calendar.DAY_OF_MONTH);


		GoodName = (EditText)findViewById(R.id.good_name);
		ProductDate = (EditText) findViewById(R.id.product_date);
		EndDate = (EditText) findViewById(R.id.end_date);
		BuyDate = (EditText) findViewById(R.id.buy_date);

		setDateTime(true,BuyDate);

		Button bn_add = (Button)findViewById(R.id.add);
		bn_add.setOnClickListener(new OnClickListener(){
			   public void  onClick(View v)
			   {  
				   if(add()){
					   Intent mainIntent = new Intent(AddActivity.this,MainActivity.class);
					   startActivity(mainIntent);
					   overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
					   setResult(RESULT_OK, mainIntent);  
					   finish();  
				   }
			   }  
			  });


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




	}






	private void setDateTime(Boolean flag,EditText editText){
		if(flag){
			final Calendar c = Calendar.getInstance();
			mYear = c.get(Calendar.YEAR);
			mMonth = c.get(Calendar.MONTH);
			mDay = c.get(Calendar.DAY_OF_MONTH);
		}
		editText.setText(new StringBuilder().append(mYear).append("-")
				.append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1)).append("-")
				.append((mDay < 10) ? "0" + mDay : mDay));
	}


	public void getDate(final EditText editText) {
		new DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT,new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				AddActivity.this.mYear = year;
				mMonth = monthOfYear;
				mDay = dayOfMonth;

				setDateTime(false, editText);
			}
		}, mYear, mMonth, mDay).show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Intent myIntent = new Intent();
            myIntent = new Intent(AddActivity.this,MainActivity.class);
            startActivity(myIntent);
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	public static boolean isValidDate(String str) {
		boolean convertSuccess=true;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			format.setLenient(false);
			format.parse(str);
		} catch (ParseException e) {
			convertSuccess=false;
		}
		return convertSuccess;
	}
	

	protected Boolean add(){
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
		String status = "0";//未使用
		try {
			TimiUpDB.insert(goodname, productdate , enddate, buydate, status, remark );
			Log.v("insert_addAct",goodname+"/"+ productdate+"/"+  enddate+"/"+  buydate+"/"+  status+"/"+  remark);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
		return true;
	}
}
