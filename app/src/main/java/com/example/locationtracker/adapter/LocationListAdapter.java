package com.example.locationtracker.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.locationtracker.Database.LocationDBHelper;
import com.example.locationtracker.Model.RouteIdListModel;
import com.example.locationtracker.R;
import com.example.locationtracker.activities.DistanceActivity;
import com.example.locationtracker.activities.MapsActivity;

import java.util.ArrayList;
import java.util.List;

public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.ViewHolder> {
    private List<RouteIdListModel> routeIdModalArrayList;
    private Context context;
    LocationDBHelper locationDBHelper;

    public LocationListAdapter(List<RouteIdListModel> routeIdModalArrayList, Context context,LocationDBHelper locationDBHelper) {
        this.routeIdModalArrayList = routeIdModalArrayList;
        this.context = context;
        this.locationDBHelper=locationDBHelper;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RouteIdListModel routeIdListModel = routeIdModalArrayList.get(position);
//        holder.totalDistance_tv.setText(routeIdListModel.getToatalDistance());
        holder.routeId_tv.setText(routeIdListModel.getRouteId());

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, DistanceActivity.class);
//                intent.putExtra("ROUTEID", routeIdListModel.getRouteId());
//                context.startActivity(intent);
//            }
//        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapsActivity.class);
                intent.putExtra("ROUTEID", routeIdListModel.getRouteId());
                context.startActivity(intent);
            }
        });

        holder.delete_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogue = new AlertDialog.Builder(context);
                alertDialogue
                        .setTitle("Delete RouteDAta")
                        .setMessage("Are you sure?")
                        .setIcon(R.drawable.ic_delete)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                              deleteRouteData(routeIdListModel.getRouteId(),position);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
                AlertDialog alertDialog = alertDialogue.create();
                alertDialog.show();
            }
        });
    }

    public void deleteRouteData(String routeId,int position){
        locationDBHelper.deleteDataFromLocationMaster(routeId);
        routeIdModalArrayList.remove(position);
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return routeIdModalArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView routeId_tv,delete_tv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            totalDistance_tv=itemView.findViewById(R.id.toatalDistance_tv);
            routeId_tv = itemView.findViewById(R.id.routeId_tv);
            delete_tv=itemView.findViewById(R.id.delete_tv);
        }
    }
}
