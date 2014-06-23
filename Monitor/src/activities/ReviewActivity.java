package activities;


import io.Output;

import java.util.List;

import notifier.Mail;

import com.Server.camerapreview.R;

import database.LogHelper;
import database.LogModel;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

public class ReviewActivity extends Activity {

	private TextView logRecord;
	private List<LogModel> log;
	public static final String PREF_NAME = "user_settings";
	private String email;
	private String password;
	StringBuilder sb;
	private Output output = new Output();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        
        logRecord = (TextView) findViewById(R.id.textView1);
        
        LogHelper helper = new LogHelper(this);
        log = helper.getAllEvents();
        
        sb = new StringBuilder();
        for(LogModel mod : log){
        	sb.append(mod.toString()).append("\n");
        }
        
        logRecord.setText(sb.toString());
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.add("Upload").setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				SharedPreferences sPref = getSharedPreferences(PREF_NAME, 0);
				email = sPref.getString("email", "nil");
				password = sPref.getString("password", "nil");
				
				if(email == "nil" || password == "nil"){
					Toast.makeText(getApplicationContext(), "Settings not set", Toast.LENGTH_LONG).show();
				}else{
					output.writeToFile(sb.toString());
					sendMail(email, password);
				}
				
				return true;
        }
		});
        
        menu.add("Clean DB").setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				LogHelper logClean = new LogHelper(getApplicationContext());
				logClean.CleanDB();
				finish();
				startActivity(getIntent());
				return false;
			}
		});
    	return true;
}
    
    // Handling the back key press.
 	@Override
 	public void onBackPressed(){
 		finish();
 		startActivity(new Intent(this, MainActivity.class));
 	}
 	
 	public void sendMail(String email, String password){
        Mail mail = new Mail(email, password);
        String[] to = {email};
        mail.setTo(to);
        mail.setSubject("log");
        mail.setFrom(email);
        mail.setBody("Just a weekly log report");
        String root = Environment.getExternalStorageDirectory().toString();
        try{
        	mail.addAttachment(root+"/log.txt");
        	if(mail.send()){
        		Toast.makeText(getApplicationContext(), "Mail sent",
						Toast.LENGTH_SHORT).show();
        	}else
        		Toast.makeText(getApplicationContext(), "mail failed",
						Toast.LENGTH_SHORT).show();
        }catch(Exception x){
        	Log.e(ReviewActivity.class.getName(), "Exception: "+x.getMessage());
        }
 	}
}