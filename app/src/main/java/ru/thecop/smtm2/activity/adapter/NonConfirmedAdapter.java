package ru.thecop.smtm2.activity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import ru.thecop.smtm2.R;
import ru.thecop.smtm2.model.Category;
import ru.thecop.smtm2.model.Spending;
import ru.thecop.smtm2.util.Constants;
import ru.thecop.smtm2.util.DateTimeUtils;

import java.util.List;

public class NonConfirmedAdapter extends RecyclerView.Adapter<NonConfirmedAdapter.NonConfirmedViewHolder> {

    private Context mContext;
    private List<Spending> mData;
    private NonConfirmedAdapterButtonsClickHandler buttonsClickHandler;

    public NonConfirmedAdapter(Context mContext,
                               NonConfirmedAdapterButtonsClickHandler buttonsClickHandler) {
        this.mContext = mContext;
        this.buttonsClickHandler = buttonsClickHandler;
    }

    @Override
    public NonConfirmedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.non_confirmed_item, parent, false);
        return new NonConfirmedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NonConfirmedViewHolder holder, int position) {
        Spending s = mData.get(position);
        Category c = s.getCategory();
        //todo add sms text textView
        holder.date.setText(DateTimeUtils.convert(s.getTimestamp()).toString(Constants.DATE_DISPLAY_FORMAT_PATTERN));
        holder.amount.setText(Double.toString(s.getAmount()));
        holder.smsFrom.setText(s.getSmsFrom());
        holder.category.setText(c != null ? c.getName() : mContext.getString(R.string.no_category));
        //todo show sms text
        if (c == null) {
            holder.confirmButton.setVisibility(View.GONE);
        } else {
            holder.confirmButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public void setData(List<Spending> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    public List<Spending> getData() {
        return mData;
    }

    class NonConfirmedViewHolder extends RecyclerView.ViewHolder {

        TextView date;
        TextView amount;
        TextView smsFrom;
        TextView category;
        ImageButton confirmButton;
        ImageButton editButton;

        public NonConfirmedViewHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.textViewDate);
            amount = (TextView) itemView.findViewById(R.id.textViewAmount);
            smsFrom = (TextView) itemView.findViewById(R.id.textViewSmsFrom);
            category = (TextView) itemView.findViewById(R.id.textViewCategory);
            confirmButton = (ImageButton) itemView.findViewById(R.id.imageButtonConfirm);
            editButton = (ImageButton) itemView.findViewById(R.id.imageButtonEdit);

            confirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Spending spending = mData.get(position);
                    buttonsClickHandler.confirmButtonClick(spending);
                }
            });

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Spending spending = mData.get(position);
                    buttonsClickHandler.editButtonClick(spending);
                }
            });
        }
    }

    public interface NonConfirmedAdapterButtonsClickHandler {
        void confirmButtonClick(Spending spending);

        void editButtonClick(Spending spending);
    }

}
