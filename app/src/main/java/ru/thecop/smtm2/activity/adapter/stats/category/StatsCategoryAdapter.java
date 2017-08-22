package ru.thecop.smtm2.activity.adapter.stats.category;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import ru.thecop.smtm2.R;
import ru.thecop.smtm2.util.AmountFormatter;

public class StatsCategoryAdapter extends RecyclerView.Adapter<StatsCategoryAdapter.StatSpendingViewHolder> {

    private Context mContext;
    private StatsCategoryAdapterData mData;
    private StatsCategoryAdapterOnClickHandler mClickHandler;

    public StatsCategoryAdapter(Context mContext, StatsCategoryAdapterOnClickHandler mClickHandler) {
        this.mContext = mContext;
        this.mClickHandler = mClickHandler;
    }

    @Override
    public StatSpendingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.stats_category_item, parent, false);
        return new StatSpendingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StatSpendingViewHolder holder, int position) {
        //TODO optimise binding? Minor lags on scrolling
        StatsCategoryAdapterStatsInfo categoryInfo = mData.getCategoryInfos().get(position);

        holder.mTextViewCategoryName.setText(categoryInfo.getCategory().getName());
        holder.mTextViewTotal.setText(AmountFormatter.format(categoryInfo.getTotalAmount()));

        holder.mTextViewEntries.setText(Integer.toString(categoryInfo.getEntriesCount()));
        holder.mTextViewEntriesPerDay.setText(AmountFormatter.format(categoryInfo.getEntriesPerDay(mData.getPeriodDays())));
        holder.mTextViewPerDay.setText(AmountFormatter.format(categoryInfo.getPerDay(mData.getPeriodDays())));
        holder.mTextViewPerWeek.setText(AmountFormatter.format(categoryInfo.getPerWeek(mData.getPeriodDays())));
        holder.mTextViewPerMonth.setText(AmountFormatter.format(categoryInfo.getPerMonth(mData.getPeriodDays())));
        holder.mTextViewPerYear.setText(AmountFormatter.format(categoryInfo.getPerYear(mData.getPeriodDays())));

        holder.mAmountShareView.setLayoutParams(categoryInfo.getShareLayoutParams());
        holder.mAmountShareOppositeView.setLayoutParams(categoryInfo.getOppositeShareLayoutParams());
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.getCategoryInfos().size() : 0;
    }

    public void setData(StatsCategoryAdapterData data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    class StatSpendingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //TODO rename all members of all holders in all adapters with m-type-name
        TextView mTextViewEntries;
        TextView mTextViewEntriesPerDay;
        TextView mTextViewPerDay;
        TextView mTextViewPerWeek;
        TextView mTextViewPerMonth;
        TextView mTextViewPerYear;

        TextView mTextViewCategoryName;
        TextView mTextViewTotal;

        View mAmountShareView;
        View mAmountShareOppositeView;

        public StatSpendingViewHolder(View itemView) {
            super(itemView);
            mTextViewCategoryName = (TextView) itemView.findViewById(R.id.textViewCategory);
            mTextViewTotal = (TextView) itemView.findViewById(R.id.textViewTotal);
            mTextViewEntries = (TextView) itemView.findViewById(R.id.textViewEntries);
            mTextViewEntriesPerDay = (TextView) itemView.findViewById(R.id.textViewEntriesPerDay);
            mTextViewPerDay = (TextView) itemView.findViewById(R.id.textViewPerDay);
            mTextViewPerWeek = (TextView) itemView.findViewById(R.id.textViewPerWeek);
            mTextViewPerMonth = (TextView) itemView.findViewById(R.id.textViewPerMonth);
            mTextViewPerYear = (TextView) itemView.findViewById(R.id.textViewPerYear);
            mAmountShareView = itemView.findViewById(R.id.amountShareView);
            mAmountShareOppositeView = itemView.findViewById(R.id.amountShareOppositeView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //TODO open stats activity with spendings of this category
//            int position = getAdapterPosition();
//            Category category = mData.get(position);
//            mClickHandler.onCategoryClick(category.getId());
            Toast.makeText(mContext, "Woah..", Toast.LENGTH_SHORT);
        }
    }

    public interface StatsCategoryAdapterOnClickHandler {
        void onCategoryClick(long categoryId);
    }
}
