package com.example.lababsencesystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class DoctorLabAdapter extends RecyclerView.Adapter<StudentLabAdapter.StudentLabViewHolder> {
    ArrayList<Lab> labs;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    public DoctorLabAdapter(ArrayList<Lab> labs) {
        this.labs = labs;
    }

    @NonNull
    @Override
    public StudentLabAdapter.StudentLabViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.doctor_lab_card, viewGroup, false);
        StudentLabAdapter.StudentLabViewHolder dcvh = new StudentLabAdapter.StudentLabViewHolder(v);
        return dcvh;
    }

    @Override
    public void onBindViewHolder(@NonNull final StudentLabAdapter.StudentLabViewHolder holder, int i) {
        holder.labCourse.setText("Course: "+ labs.get(i).getCourse());
//        holder.labDoctor.setText("Title: "+ labs.get(i).doctor);
        holder.labDate.setText("Date: "+ labs.get(i).date.toString());
        holder.labTime.setText("Time: "+ labs.get(i).getTime());

    }

    @Override
    public int getItemCount() {
        return labs.size();
    }

    public static class DoctorLabViewHolder extends RecyclerView.ViewHolder {

        TextView labCourse;
        TextView labDate;
        TextView labTime;
        Button labEdit;

        DoctorLabViewHolder(View itemView) {
            super(itemView);
            labCourse = itemView.findViewById(R.id.doctorLabCourse);
            labDate = itemView.findViewById(R.id.doctorLabDate);
            labTime = itemView.findViewById(R.id.doctorLabTime);
             labEdit= itemView.findViewById(R.id.doctorLabEdit);

        }
    }
}