package com.example.messimusic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PersonAdapter extends ArrayAdapter<Person> implements Filterable {
    private  Context mContext;
    private int mResource;
    List<Person> modellist;
    ArrayList<Person> arrayList;
    ArrayList<Person> arrayListCopy;

    public PersonAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Person> objects) {
        super(context, resource, objects);
        this.mContext=context;
        this.mResource=resource;
        this.arrayList=objects;
        arrayListCopy=objects;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater=LayoutInflater.from(mContext);
        convertView=layoutInflater.inflate(mResource,parent,false);
        ImageView img=convertView.findViewById(R.id.myImg);
        TextView tvName=convertView.findViewById(R.id.tvName);
        TextView tvDes=convertView.findViewById(R.id.tvDes);
        img.setImageResource(getItem(position).getImg());
        tvName.setText(getItem(position).getName());
        tvDes.setText(getItem(position).getDes());
        return convertView;
    }
    public Filter getFilter() {
        return  new CustomFilter();
    }
    class CustomFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            arrayList = arrayListCopy;
            FilterResults objRes = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                objRes.values = arrayList;
            } else {
                ArrayList<Person> tempArrayList = new ArrayList<>();
                for (Person obj : arrayList) {
                    if (obj.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempArrayList.add(obj);
                    }
                }
                objRes.values = tempArrayList;
            }
            return objRes;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            arrayList = (ArrayList<Person>) results.values;
            notifyDataSetChanged();
        }
    }
}

