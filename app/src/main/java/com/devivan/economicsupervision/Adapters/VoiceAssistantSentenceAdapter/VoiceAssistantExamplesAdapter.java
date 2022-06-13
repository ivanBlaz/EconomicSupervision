package com.devivan.economicsupervision.Adapters.VoiceAssistantSentenceAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devivan.economicsupervision.R;

import java.util.ArrayList;

public class VoiceAssistantExamplesAdapter extends RecyclerView.Adapter<VoiceAssistantExamplesAdapter.ViewHolder> {

    ArrayList<String> examples;

    public VoiceAssistantExamplesAdapter(ArrayList<String> examples) {
        this.examples = examples;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.line_voice_assistant_example, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtvExample.setText(examples.get(position));
    }

    @Override
    public int getItemCount() {
        return examples != null ? examples.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtvExample;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // txtvExample
            txtvExample = itemView.findViewById(R.id.txtvExample);
        }
    }
}
