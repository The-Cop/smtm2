package ru.thecop.smtm2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ru.thecop.smtm2.db.FakeDb;
import ru.thecop.smtm2.model.Category;
import ru.thecop.smtm2.model.Spending;
import ru.thecop.smtm2.util.Constants;
import ru.thecop.smtm2.util.DateTimeConverter;

import java.util.List;

public class NonConfirmedAdapter extends RecyclerView.Adapter<NonConfirmedAdapter.NonConfirmedViewHolder> {

    private Context mContext;
    private List<Spending> mData;

    public NonConfirmedAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public NonConfirmedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.non_confirmed_item, parent, false);
        return new NonConfirmedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NonConfirmedViewHolder holder, int position) {
        Spending s = mData.get(position);
        //todo use spending dto or spending with eager loaded category
        Category c = FakeDb.findCategoryById(s.getCategoryId());

        holder.date.setText(DateTimeConverter.convert(s.getTimestamp()).toString(Constants.DATE_DISPLAY_FORMAT_PATTERN));
        holder.amount.setText(Double.toString(s.getAmount()));
        holder.smsFrom.setText("Tinkoff Bank");
        holder.category.setText(c.getName());
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public void setData(List<Spending> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    class NonConfirmedViewHolder extends RecyclerView.ViewHolder {

        TextView date;
        TextView amount;
        TextView smsFrom;
        TextView category;

        public NonConfirmedViewHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.textViewDate);
            amount = (TextView) itemView.findViewById(R.id.textViewAmount);
            smsFrom = (TextView) itemView.findViewById(R.id.textViewSmsFrom);
            category = (TextView) itemView.findViewById(R.id.textViewCategory);
        }

    }

}
