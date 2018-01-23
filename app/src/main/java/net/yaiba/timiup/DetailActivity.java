package net.yaiba.timiup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.yaiba.timiup.db.TimiUpDB;

import java.text.ParseException;

import static net.yaiba.timiup.utils.Custom.*;


public class DetailActivity extends Activity {
	private TimiUpDB TimiUpDB;
	private Cursor mCursor;

	private TextView GoodName;
	private TextView ProductDate;
	private TextView EndDate;
	private TextView BuyDate;
	private TextView LaveDays;
	private TextView HP;
	private TextView Status;
	private TextView Remark;

	private int RECORD_ID = 0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		TimiUpDB = new TimiUpDB(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_detail);
		RECORD_ID = this.getIntent().getIntExtra("INT", RECORD_ID);
		Log.v("debug","(onCreate)RECORD_ID:"+RECORD_ID);
		setUpViews();

		Button bn_go_edit = (Button)findViewById(R.id.go_edit);
		bn_go_edit.setOnClickListener(new View.OnClickListener(){
			public void  onClick(View v)
			{
				//画面迁移到edit画面
				Intent mainIntent = new Intent(DetailActivity.this,EditActivity.class);
				mainIntent.putExtra("INT", RECORD_ID);
				startActivity(mainIntent);
				setResult(RESULT_OK, mainIntent);
				finish();
			}
		});


		Button bn_go_del = (Button)findViewById(R.id.go_del);
		bn_go_del.setOnClickListener(new View.OnClickListener(){
			public void  onClick(View v)
			{
				AlertDialog.Builder builder= new AlertDialog.Builder(DetailActivity.this);
				builder.setIcon(android.R.drawable.ic_dialog_info);
				builder.setTitle("确认");
				builder.setMessage("确定要删除这条记录吗？");
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						delete();
						Intent mainIntent = new Intent(DetailActivity.this,MainActivity.class);
						startActivity(mainIntent);
						setResult(RESULT_OK, mainIntent);
						finish();

					}
				});
				builder.setNegativeButton("取消", null);
				builder.create().show();
			}
		});

		Button bn_go_status_used = (Button)findViewById(R.id.go_status_used);
		bn_go_status_used.setOnClickListener(new View.OnClickListener(){
			public void  onClick(View v)
			{
				AlertDialog.Builder builder= new AlertDialog.Builder(DetailActivity.this);
				builder.setIcon(android.R.drawable.ic_dialog_info);
				builder.setTitle("确认");
				builder.setMessage("确定这个东东已经使用了吗？");
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						update_status("1");
						Intent mainIntent = new Intent(DetailActivity.this,MainActivity.class);
						startActivity(mainIntent);
						setResult(RESULT_OK, mainIntent);
						finish();

					}
				});
				builder.setNegativeButton("取消", null);
				builder.create().show();
			}
		});




	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Intent mainIntent = new Intent(DetailActivity.this,MainActivity.class);
			startActivity(mainIntent);
			overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
			setResult(RESULT_OK, mainIntent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
 	public void setUpViews(){
		Log.v("debug","(setUpViews)RECORD_ID:"+RECORD_ID);
		TimiUpDB = new TimiUpDB(this);
		mCursor = TimiUpDB.getRecordInfo(RECORD_ID);

		GoodName = (TextView)findViewById(R.id.good_name);
		ProductDate = (TextView) findViewById(R.id.product_date);
		EndDate = (TextView) findViewById(R.id.end_date);
		BuyDate = (TextView) findViewById(R.id.buy_date);
		LaveDays = (TextView) findViewById(R.id.lave_days);
		HP = (TextView) findViewById(R.id.hp);
		Status = (TextView) findViewById(R.id.status);
		Remark = (TextView)findViewById(R.id.remark);

		String id = mCursor.getString(mCursor.getColumnIndex("id"));
		String goodName = mCursor.getString(mCursor.getColumnIndex("good_name"));
		String productDate = mCursor.getString(mCursor.getColumnIndex("product_date"));
		String endDate = mCursor.getString(mCursor.getColumnIndex("end_date"));
		String buyDate = mCursor.getString(mCursor.getColumnIndex("buy_date"));
		String status = mCursor.getString(mCursor.getColumnIndex("status"));
		String remark = mCursor.getString(mCursor.getColumnIndex("remark"));

		//Log.v("v_debug_detail_init",id+"/"+goodName+"/"+productDate+"/"+endDate+"/"+buyDate+"/"+status+"/"+remark);

		// 当当前物品是已经使用的状态时，设置状态的按钮隐藏
		if ("1".equals(status)){
			Button bn_go_status_used = (Button)findViewById(R.id.go_status_used);
			bn_go_status_used.setVisibility(View.GONE);
		}


		String  laveDays = "0";//剩余天数，， _剩余天数=今日-到期日 的天数
		double laveDaysDoub = 0;
		try {
			laveDaysDoub = getDiffDays(getStringToDate(getNowStringDate()),getStringToDate(endDate));
			laveDays = Double.toString(laveDaysDoub).split("\\.")[0];
		} catch (ParseException e) {
			e.printStackTrace();
		}

		String hp = "";//HP,表示商品的生命周期百分比，， _HP =（剩余天数/（生产日期~到期日的天数））x100
		try {
			double dhp = 0;
			if( "0".equals(laveDays)){
				hp = "-";
			} else {
				dhp = laveDaysDoub/getDiffDays(getStringToDate(productDate),getStringToDate(endDate))*100;
				hp = Double.toString(dhp).split("\\.")[0];
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}

		GoodName.setText(mCursor.getString(mCursor.getColumnIndex("good_name")));
		ProductDate.setText(mCursor.getString(mCursor.getColumnIndex("product_date")));
		EndDate.setText(mCursor.getString(mCursor.getColumnIndex("end_date")));
		BuyDate.setText(mCursor.getString(mCursor.getColumnIndex("buy_date")));
		LaveDays.setText(laveDays+"天");
		HP.setText(hp+"%");
		Status.setText(transStatus(mCursor.getString(mCursor.getColumnIndex("status"))));
		Remark.setText(mCursor.getString(mCursor.getColumnIndex("remark")));

	}

	public void delete() {
		if (RECORD_ID == 0) {
			return;
		}
		TimiUpDB.delete(RECORD_ID);
		//mCursor.requery();
		Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
	}

	public void update_status(String status) {
		if (RECORD_ID == 0) {
			return;
		}
		TimiUpDB.update_status(RECORD_ID,status);
		//mCursor.requery();
		Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
	}



	public String transStatus(String status){

		//根据status值，从arrayxml中动态调用返回值
		Resources res =getResources();
		String[] statusArr = res.getStringArray(R.array.status);

		if ("0".equals(status)){
			return statusArr[0];
		}
		if ("1".equals(status)){
			return statusArr[1];
		}
		return "";
	}

}
