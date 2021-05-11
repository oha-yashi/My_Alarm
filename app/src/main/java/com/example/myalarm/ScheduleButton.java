package com.example.myalarm;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Button;

import androidx.annotation.Nullable;

import java.util.Calendar;

public class ScheduleButton extends androidx.appcompat.widget.AppCompatButton {

    private String ring_time;
    private int _id;

    public ScheduleButton(Context context, String calendar, int _id) {
        super(context);
        setRing_time(calendar);
        set_id(_id);
        this.setOnClickListener(view -> {
            new AlertDialog.Builder(context)
                    .setMessage(DatabaseHelper.toString(context,_id))
                    .show();
        });
    }



    public void setRing_time(String ring_time) {
        this.ring_time = ring_time;
        this.setText(Tools.toOnlyTimeStamp(ring_time));
    }

    public String getRing_time() {
        return ring_time;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int get_id() {
        return _id;
    }
}
