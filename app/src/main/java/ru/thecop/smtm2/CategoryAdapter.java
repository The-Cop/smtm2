package ru.thecop.smtm2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import ru.thecop.smtm2.model.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context mContext;
    private List<Category> mData;
    private CategoryAdapterOnClickHandler mClickHandler;

    public CategoryAdapter(Context mContext, CategoryAdapterOnClickHandler mClickHandler) {
        this.mContext = mContext;
        this.mClickHandler = mClickHandler;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        Category category = mData.get(position);

        holder.categoryName.setText(category.getName());
        //set plus image invisible
        holder.imageAddCategory.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public void setData(List<Category> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView categoryName;
        ImageView imageAddCategory;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            categoryName = (TextView) itemView.findViewById(R.id.categoryName);
            imageAddCategory = (ImageView) itemView.findViewById(R.id.imageViewCategoryAdd);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Category category = mData.get(position);
            mClickHandler.onCategoryClick(category.getId());
        }
    }

    public interface CategoryAdapterOnClickHandler {
        void onCategoryClick(long categoryId);
    }
}
