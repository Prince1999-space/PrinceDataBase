package com.prince.database;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity {
    LinearLayout root, content;
    android.content.SharedPreferences p;
    int purple = Color.rgb(108,99,255), dark = Color.rgb(16,19,26);
    TextView balance, income, savings, target, percent, streak;

    public void onCreate(Bundle b){
        super.onCreate(b); p=getSharedPreferences("data",0); build();
    }
    TextView tv(String s,int sp){
        TextView t=new TextView(this); t.setText(s); t.setTextSize(sp); t.setTextColor(Color.WHITE);
        t.setPadding(18,14,18,14); return t;
    }
    Button btn(String s){ Button b=new Button(this); b.setText(s); b.setTextColor(Color.WHITE); b.setBackgroundColor(purple); return b; }
    void build(){
        ScrollView sv=new ScrollView(this);
        root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(18,18,18,30); root.setBackgroundColor(dark);
        TextView title=tv("♛  Prince DataBase",26); root.addView(title);
        TextView sub=tv("Personal finance & progress dashboard",14); sub.setTextColor(Color.LTGRAY); root.addView(sub);
        content=new LinearLayout(this); content.setOrientation(LinearLayout.VERTICAL); root.addView(content);
        sv.addView(root); setContentView(sv); refresh();
    }
    TextView card(String label,String value){
        LinearLayout c=new LinearLayout(this); c.setOrientation(LinearLayout.VERTICAL); c.setPadding(10,10,10,10);
        c.setBackgroundColor(Color.rgb(27,31,41));
        TextView a=tv(label,14); a.setTextColor(Color.LTGRAY); c.addView(a);
        TextView v=tv(value,22); c.addView(v);
        content.addView(c,new LinearLayout.LayoutParams(-1,110){ {setMargins(0,8,0,8);} });
        return v;
    }
    void refresh(){
        content.removeAllViews();
        balance=card("CURRENT BALANCE","TSh "+money(p.getLong("balance",0)));
        income=card("TODAY'S INCOME","TSh "+money(p.getLong("income",0)));
        savings=card("TODAY'S SAVINGS","TSh "+money(p.getLong("savings",0)));

        long wi=p.getLong("wincome",0), wt=p.getLong("wtarget",100000);
        int pc=wt==0?0:(int)Math.min(100,wi*100/wt);
        target=card("WEEKLY TARGET","TSh "+money(wi)+" / TSh "+money(wt));
        percent=card("TARGET ACHIEVED",pc+"%");
        ProgressBar pb=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);
        pb.setMax(100); pb.setProgress(pc); content.addView(pb,new LinearLayout.LayoutParams(-1,30));

        card("MONTHLY NEEDS","TSh "+money(p.getLong("needs",0))+" planned");

        streak=card("PROGRESS TRACKER",p.getInt("streak",0)+" days completed");
        Button add=btn("+ Add Income"); add.setOnClickListener(v->amount("Income",0)); content.addView(add);
        Button save=btn("+ Add Savings"); save.setOnClickListener(v->amount("Savings",1)); content.addView(save);
        Button exp=btn("+ Add Expense"); exp.setOnClickListener(v->amount("Expense",2)); content.addView(exp);
        Button need=btn("Set Monthly Needs"); need.setOnClickListener(v->amount("Monthly Needs",3)); content.addView(need);
        Button tar=btn("Set Weekly Target"); tar.setOnClickListener(v->amount("Weekly Target",4)); content.addView(tar);
        Button prog=btn("Mark Progress Day"); prog.setOnClickListener(v->{p.edit().putInt("streak",p.getInt("streak",0)+1).apply();refresh();}); content.addView(prog);
        Button report=btn("View Report"); report.setOnClickListener(v->report()); content.addView(report);
    }
    String money(long n){ return String.format(Locale.US,"%,d",n); }
    void amount(String title,int type){
        final EditText e=new EditText(this); e.setInputType(2); e.setHint("Enter amount (TSh)");
        new AlertDialog.Builder(this).setTitle(title).setView(e).setNegativeButton("Cancel",null)
        .setPositiveButton("Save",(d,w)->{long x=0;try{x=Long.parseLong(e.getText().toString());}catch(Exception z){}
            long bal=p.getLong("balance",0), inc=p.getLong("income",0), sav=p.getLong("savings",0), wi=p.getLong("wincome",0);
            android.content.SharedPreferences.Editor ed=p.edit();
            if(type==0){ed.putLong("balance",bal+x).putLong("income",inc+x).putLong("wincome",wi+x);}
            if(type==1){ed.putLong("balance",bal+x).putLong("savings",sav+x);}
            if(type==2){ed.putLong("balance",Math.max(0,bal-x));}
            if(type==3)ed.putLong("needs",x);
            if(type==4)ed.putLong("wtarget",x);
            ed.apply();refresh();
        }).show();
    }
    void report(){
        long inc=p.getLong("wincome",0), sav=p.getLong("savings",0), bal=p.getLong("balance",0), tar=p.getLong("wtarget",100000);
        int pc=tar==0?0:(int)Math.min(100,inc*100/tar);
        new AlertDialog.Builder(this).setTitle("Prince DataBase Report")
        .setMessage("Balance: TSh "+money(bal)+"\nWeekly income: TSh "+money(inc)+"\nSavings: TSh "+money(sav)+"\nTarget: TSh "+money(tar)+"\nTarget achieved: "+pc+"%\nProgress streak: "+p.getInt("streak",0)+" days")
        .setPositiveButton("OK",null).show();
    }
}