package ru.thecop.smtm2.activity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import ru.thecop.smtm2.R;
import ru.thecop.smtm2.model.Spending;
import ru.thecop.smtm2.util.AmountFormatter;
import ru.thecop.smtm2.util.Constants;
import ru.thecop.smtm2.util.DateTimeUtils;

import java.util.List;

public class StatsSpendingAdapter extends RecyclerView.Adapter<StatsSpendingAdapter.StatSpendingViewHolder> {

    private Context mContext;
    private List<Spending> mData;
    private StatsSpendingAdapterOnClickHandler mClickHandler;

    public StatsSpendingAdapter(Context mContext, StatsSpendingAdapterOnClickHandler mClickHandler) {
        this.mContext = mContext;
        this.mClickHandler = mClickHandler;
    }

    @Override
    public StatSpendingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.stats_spending_item, parent, false);
        return new StatSpendingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StatSpendingViewHolder holder, int position) {
        Spending spending = mData.get(position);

        holder.categoryName.setText(spending.getCategory().getName());
        holder.date.setText(DateTimeUtils.convert(spending.getTimestamp()).toString(Constants.DATE_DISPLAY_FORMAT_PATTERN));
        holder.amount.setText(AmountFormatter.format(spending.getAmount()));
        holder.comment.setText(spending.getComment());
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public void setData(List<Spending> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    class StatSpendingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView categoryName;
        TextView date;
        TextView amount;
        TextView comment;

        public StatSpendingViewHolder(View itemView) {
            super(itemView);
            categoryName = (TextView) itemView.findViewById(R.id.textViewCategory);
            date = (TextView) itemView.findViewById(R.id.textViewDate);
            amount = (TextView) itemView.findViewById(R.id.textViewAmount);
            comment = (TextView) itemView.findViewById(R.id.textViewComment);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //TODO show menu or dialog on spending click
//            int position = getAdapterPosition();
//            Category category = mData.get(position);
//            mClickHandler.onCategoryClick(category.getId());
            Toast.makeText(mContext, "Woah..", Toast.LENGTH_SHORT);
        }
    }

    public interface StatsSpendingAdapterOnClickHandler {
        void onCategoryClick(long categoryId);
    }
}
