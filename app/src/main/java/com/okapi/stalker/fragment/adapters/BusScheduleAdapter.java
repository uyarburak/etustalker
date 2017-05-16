package com.okapi.stalker.fragment.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.okapi.stalker.R;
import com.okapi.stalker.data.storage.model.BusSchedule;

import java.util.List;

/**
 * Created by burak on 5/16/2017.
 */

public class BusScheduleAdapter extends RecyclerView.Adapter<BusScheduleAdapter.MyViewHolder> {

    private List<BusSchedule> busses;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView from, to, time;

        public MyViewHolder(View view) {
            super(view);
            from = (TextView) view.findViewById(R.id.bus_from);
            to = (TextView) view.findViewById(R.id.bus_to);
            time = (TextView) view.findViewById(R.id.bus_time);
        }
    }


    public BusScheduleAdapter(List<BusSchedule> busses) {
        this.busses = busses;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bus_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        BusSchedule bus = busses.get(position);
        holder.from.setText(bus.getFrom());
        holder.to.setText(bus.getTo());
        holder.time.setText(bus.getTime());
        Log.i("Adapter", bus.toString());
    }

    @Override
    public int getItemCount() {
        return busses.size();
    }
}
