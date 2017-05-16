package com.okapi.stalker.fragment.adapters;

        import android.app.Activity;
        import android.content.Intent;
        import android.net.Uri;
        import android.support.v7.widget.RecyclerView;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.TextView;

        import com.okapi.stalker.R;
        import com.okapi.stalker.data.storage.model.BusSchedule;

        import java.util.List;

/**
 * Created by burak on 5/16/2017.
 */

public class DistrictServiceAdapter extends RecyclerView.Adapter<DistrictServiceAdapter.MyViewHolder> {

    private List<BusSchedule> busses;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView district;
        public ImageView mapIcon;

        public MyViewHolder(View view) {
            super(view);
            district = (TextView) view.findViewById(R.id.bus_distinct);
            mapIcon = (ImageView) view.findViewById(R.id.map_icon);
        }
    }


    public DistrictServiceAdapter(List<BusSchedule> busses) {
        this.busses = busses;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.district_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        BusSchedule bus = busses.get(position);
        holder.district.setText(bus.getFrom());
        if(bus.getRouteURL() == null || bus.getRouteURL().isEmpty())
            holder.mapIcon.setVisibility(View.INVISIBLE);
        else
            holder.mapIcon.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return busses.size();
    }

    public BusSchedule getItem(int index){
        return busses.get(index);
    }
}
