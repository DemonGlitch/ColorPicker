package com.github.demon.glitch;
 
import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.view.View.*;
import android.view.*;
import com.github.demon.glitch.mixer.*;

public class MainActivity extends Activity
{

	private Button a; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		a=findViewById(R.id.act);
		a.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View p1)
				{
					ColorMixer cm=new ColorMixer(MainActivity.this,true);
					cm.setOnPickListener("Select Color",new ColorMixer.OnPickListener()
						{
							@Override
							public void onPickColor(int color, String hex)
							{
								a.setTextColor(color);
								a.setText(hex);
							}
					});
				}
		});
    }
	
} 
