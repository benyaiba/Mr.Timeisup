package net.yaiba.timiup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.yaiba.timiup.utils.Custom;

import java.util.Timer;
import java.util.TimerTask;

public class LoginActivity extends Activity {

	private ImageView indexLogo;
	private int indexLogoClickCount = 1;

	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.login);

        //设置首页版本
        TextView textView = (TextView) findViewById(R.id.version_id);
        textView.setText(Custom.getVersion(this)+"@2018");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                /**
                 * 延时执行的代码
                 */
                //Toast.makeText(LoginActivity.this, "欢迎回来" , Toast.LENGTH_SHORT).show();
                Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(mainIntent);
                setResult(RESULT_OK, mainIntent);
                finish();

            }
        },1500);




		// 小彩蛋，以后再弄
//		indexLogo = (ImageView) findViewById(R.id.index_logo);
//		indexLogo.setOnClickListener(new OnClickListener(){
//			public void  onClick(View v)
//			{
//
//				if(indexLogoClickCount >= 16 && indexLogoClickCount < 88){
//					indexLogo.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.index_logo_egg1));
//				}
//
//				if(indexLogoClickCount >= 88){
//					indexLogo.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.index_logo_egg2));
//				}
//
//				indexLogoClickCount++;
//
//			}
//		});








	}
	
	



	

}
