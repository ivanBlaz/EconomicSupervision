package com.devivan.economicsupervision.Adapters.VoiceAssistantSentenceAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devivan.economicsupervision.R;
import com.devivan.economicsupervision.System.System;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VoiceAssistantSentencesAdapter extends RecyclerView.Adapter<VoiceAssistantSentencesAdapter.ViewHolder> {

    Context context;
    boolean movements;
    String[] sentences;
    Map<String, ArrayList<String>> mapExamples = new HashMap<>();

    public VoiceAssistantSentencesAdapter(Context context, boolean movements) {
        this.context = context;
        this.movements = movements;
        sentences = context.getResources().getStringArray(movements ? R.array.movements_voice_assistant_statements : R.array.transactions_voice_assistant_statements);
        for (int i = 0; i < sentences.length; i++) mapExamples.put(sentences[i], System.getExamples(context, i, movements));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.line_voice_assistant_statement, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String statement = sentences[position];
        holder.txtvStatement.setText(statement);
        holder.dividerStatement.setVisibility(position == sentences.length - 1 ? View.GONE : View.VISIBLE);
        System.setAdapter(holder.rvExamples, new VoiceAssistantExamplesAdapter(mapExamples.get(statement)), true, false, new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
    }

    @Override
    public int getItemCount() {
        return sentences != null ? sentences.length : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtvStatement;
        View dividerStatement;
        RecyclerView rvExamples;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // txtvStatement
            txtvStatement = itemView.findViewById(R.id.txtvStatement);

            // dividerStatement
            dividerStatement = itemView.findViewById(R.id.dividerStatement);

            // rvExamples
            rvExamples = itemView.findViewById(R.id.rvExamples);
        }
    }
}
