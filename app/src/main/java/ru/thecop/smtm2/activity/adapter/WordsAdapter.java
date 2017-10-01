package ru.thecop.smtm2.activity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import ru.thecop.smtm2.R;

import java.util.List;

public class WordsAdapter extends RecyclerView.Adapter<WordsAdapter.WordViewHolder> {

    private Context mContext;
    private List<String> mData;
    private WordDeleteOnClickHandler mClickHandler;

    public WordsAdapter(Context mContext, WordDeleteOnClickHandler mClickHandler) {
        this.mContext = mContext;
        this.mClickHandler = mClickHandler;
    }

    @Override
    public WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.word_list_item, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WordViewHolder holder, int position) {
        String word = mData.get(position);
        holder.word.setText(word);
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public void setData(List<String> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    public List<String> getData() {
        return mData;
    }

    class WordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView word;
        ImageButton deleteButton;

        public WordViewHolder(View itemView) {
            super(itemView);
            word = (TextView) itemView.findViewById(R.id.textViewWord);
            deleteButton = (ImageButton) itemView.findViewById(R.id.imageButtonDelete);
            deleteButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            String clickedWord = mData.get(position);
            mClickHandler.onWordDeleteClick(clickedWord);
        }
    }

    public interface WordDeleteOnClickHandler {
        void onWordDeleteClick(String word);
    }
}
