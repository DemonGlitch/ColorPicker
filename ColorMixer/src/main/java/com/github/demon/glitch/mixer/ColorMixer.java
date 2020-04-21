package com.github.demon.glitch.mixer;
/*               
         /\         || ||   //          /\
        //\\        || ||  //          //\\
       //  \\       || || //          //  \\
      //    \\      || ||//          //    \\
     //      \\     || ||/          //      \\
    //________\\    || ||\         //________\\
   //__________\\   || ||\\       //__________\\
  //            \\  || || \\     //            \\
 //              \\ || ||  \\   //              \\
//                \\|| ||   \\ //                \\
*/
import android.content.Context;
import android.app.AlertDialog;
import android.widget.SeekBar;
import android.widget.LinearLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.view.*;
import android.widget.*;
import android.preference.*;
import android.content.*;
import android.view.View.*;
import com.github.demon.glitch.mixer.ColorMixer.*;
import android.graphics.*;

public class ColorMixer
{
	
	private int ai=255,ri=255,gi=255,bi=255;
	
	private int as,rs,gs,bs;
	
	private Context c;

	private AlertDialog dialog;

	private CircularSeekBar aw,rw,gw,bw;
	
	private TextView tv,at,rt,gt,bt;
	
	private ProgressBar pb;

	private SharedPreferences sp;
	
	private Button cc,dd;

	private ColorMixer.OnPickListener pick;
	
	private boolean alpha;

	private int zz;
	
	public ColorMixer(Context c,boolean enableAlpha)
	{
		this.c=c;
		initialize(c,enableAlpha);
	}

	private void initialize(Context c,boolean b)
	{
		sp=PreferenceManager.getDefaultSharedPreferences(c);
		AlertDialog.Builder picker=new AlertDialog.Builder(c,R.style.ColorMixerTheme);
		
		//cast
		
		View v=LayoutInflater.from(c).inflate(R.layout.color_mixer,null);
		pb=v.findViewById(R.id.colorP);
		tv=v.findViewById(R.id.color);
		aw=v.findViewById(R.id.alphaSb);
		rw=v.findViewById(R.id.redSb);
		gw=v.findViewById(R.id.greenSb);
		bw=v.findViewById(R.id.blueSh);
		at=v.findViewById(R.id.alphaTv);
		rt=v.findViewById(R.id.redTv);
		gt=v.findViewById(R.id.greenTv);
		bt=v.findViewById(R.id.blueTv);
		cc=v.findViewById(R.id.close);
		dd=v.findViewById(R.id.done);
		
		//sp
		
		as=sp.getInt("alpha",255);
		rs=sp.getInt("red",255);
		gs=sp.getInt("green",255);
		bs=sp.getInt("blue",255);
		
		//
		
		at.setText(String.valueOf(as));
		rt.setText(String.valueOf(rs));
		gt.setText(String.valueOf(gs));
		bt.setText(String.valueOf(bs));
		aw.setProgress(as);
		rw.setProgress(rs);
		gw.setProgress(gs);
		bw.setProgress(bs);
		
		//alpha
		
		at.setTextColor(Color.WHITE);
		aw.setPointerColor(Color.WHITE);
		aw.setPointerHaloColor(Color.WHITE);
		aw.setCircleProgressColor(Color.WHITE);
		
		//red
		
		rt.setTextColor(Color.RED);
		rw.setPointerColor(Color.RED);
		rw.setPointerHaloColor(Color.RED);
		rw.setCircleProgressColor(Color.RED);
		
		//green
		
		gt.setTextColor(Color.GREEN);
		gw.setPointerColor(Color.GREEN);
		gw.setPointerHaloColor(Color.GREEN);
		gw.setCircleProgressColor(Color.GREEN);
		
		//blue
		
		bt.setTextColor(Color.BLUE);
		bw.setPointerColor(Color.BLUE);
		bw.setPointerHaloColor(Color.BLUE);
		bw.setCircleProgressColor(Color.BLUE);
		
		
		
		zz=Color.argb(b?as:255,rs,gs,bs);
		pb.getIndeterminateDrawable().setColorFilter(zz,PorterDuff.Mode.MULTIPLY);
		tv.setTextColor(zz);
		tv.setText("#"+Integer.toHexString(zz));
		picker.setView(v);
		dialog=picker.create();
		dialog.show();
		alpha=b;
		aw.setLockEnabled(b);
		at.setEnabled(b);
		cc.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View p1)
				{
					dialog.dismiss();
				}
		});
	}
	public void setOnPickListener(String name,final OnPickListener pick)
	{
		this.pick=pick;
		dd.setText(name);
		aw.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener()
		{
				@Override
				public void onProgressChanged(CircularSeekBar circularSeekBar, int p, boolean fromUser)
				{
					ai=p;
					at.setText(String.valueOf(ai));
					sp.edit().putInt("alpha",ai).apply();
					int color=Color.argb(alpha ?ai: 255, rw.getProgress(), gw.getProgress(), bw. getProgress());
					pb.getIndeterminateDrawable().setColorFilter(color,PorterDuff.Mode.MULTIPLY);
					tv.setTextColor(color);
					tv.setText("#"+Integer.toHexString(color));
				}
				@Override
				public void onStopTrackingTouch(CircularSeekBar seekBar){}
				@Override
				public void onStartTrackingTouch(CircularSeekBar seekBar){}
				
		});
		rw.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener()
			{
				@Override
				public void onProgressChanged(CircularSeekBar circularSeekBar, int p, boolean fromUser)
				{
					ri=p;
					rt.setText(String.valueOf(ri));
					sp.edit().putInt("red",ri).apply();
					int color=Color.argb(alpha ?aw.getProgress(): 255, ri, gw.getProgress(), bw. getProgress());
					pb.getIndeterminateDrawable().setColorFilter(color,PorterDuff.Mode.MULTIPLY);
					tv.setTextColor(color);
					tv.setText("#"+Integer.toHexString(color));
				}
				@Override
				public void onStopTrackingTouch(CircularSeekBar seekBar){}
				@Override
				public void onStartTrackingTouch(CircularSeekBar seekBar){}
			});
		gw.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener()
			{
				@Override
				public void onProgressChanged(CircularSeekBar circularSeekBar, int p, boolean fromUser)
				{
					gi=p;
					gt.setText(String.valueOf(gi));
					sp.edit().putInt("green",gi).apply();
					int color=Color.argb(alpha ?aw.getProgress(): 255, rw.getProgress(), gi, bw. getProgress());
					pb.getIndeterminateDrawable().setColorFilter(color,PorterDuff.Mode.MULTIPLY);
					tv.setTextColor(color);
					tv.setText("#"+Integer.toHexString(color));
				}
				@Override
				public void onStopTrackingTouch(CircularSeekBar seekBar){}
				@Override
				public void onStartTrackingTouch(CircularSeekBar seekBar){}
			});
		bw.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener()
			{
				@Override
				public void onProgressChanged(CircularSeekBar circularSeekBar, int p, boolean fromUser)
				{
					bi=p;
					bt.setText(String.valueOf(bi));
					sp.edit().putInt("blue",bi).apply();
					int color=Color.argb(alpha ?aw.getProgress(): 255, rw.getProgress(), gw.getProgress(), bi);
					pb.getIndeterminateDrawable().setColorFilter(color,PorterDuff.Mode.MULTIPLY);
					tv.setTextColor(color);
					tv.setText("#"+Integer.toHexString(color));
				}
				@Override
				public void onStopTrackingTouch(CircularSeekBar seekBar){}
				@Override
				public void onStartTrackingTouch(CircularSeekBar seekBar){}
			});
		dd.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View p1)
				{
					dialog.dismiss();
					int color=Color.argb(alpha?aw.getProgress():255,rw.getProgress(),gw.getProgress(),bw.getProgress());
					String hex="#"+Integer.toHexString(color);
					pick.onPickColor(color,hex);
				}
		});
	}
	
	public interface OnPickListener
	{
		void onPickColor(int color,String hex);
	}
}
