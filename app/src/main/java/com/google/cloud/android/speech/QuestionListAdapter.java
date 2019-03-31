package com.google.cloud.android.speech;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class QuestionListAdapter extends ArrayAdapter<SampleObject> {
    List<SampleObject> filteredSampleObjects;
    final List<SampleObject> questionList;

    public QuestionListAdapter(Context context, List<SampleObject> questionList) {
        super(context, 0, questionList);
        this.questionList = questionList;
        this.filteredSampleObjects = questionList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SampleObject sampleObj = getItem(position);
        Gson gson=new Gson();
        String questionString=null;


        Question question=gson.fromJson(sampleObj.getQuestion(),Question.class);
        questionString=question.getQuestion();

        ViewHolder holder = null;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.question_list_item, parent, false);
            holder = new ViewHolder();

            holder.tvQuestion =  convertView.findViewById(R.id.tvQuestion);
            holder.tvCount =  convertView.findViewById(R.id.tvMachinWords);
            convertView.setTag(holder);
            convertView.setTag(R.id.tvQuestion, holder.tvQuestion);
            convertView.setTag(R.id.tvMachinWords, holder.tvCount);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvQuestion.setText(questionString);
        holder.tvCount.setText("Matching words : ("+String.valueOf(sampleObj.getCount())+")");
//        holder.tvSampleObjectLocation.setText(courtHouseObj.getCity());
        return convertView;
    }

    static class ViewHolder {
        protected TextView tvQuestion;
        TextView tvCount;
    }

    @Override
    public int getCount() {
        int size;
        if (filteredSampleObjects != null) {
            size = filteredSampleObjects.size();
        } else {
            size = 0;
        }
        return size;
    }

    @Nullable
    @Override
    public SampleObject getItem(int position) {
        return filteredSampleObjects.get(position);
    }
}
