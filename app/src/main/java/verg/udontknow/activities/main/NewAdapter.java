package verg.udontknow.activities.main;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import verg.udontknow.R;
import verg.udontknow.entity.AccountEntity;

/**
 * 主列表适配器
 * Created by verg on 15/11/22.
 */
public class NewAdapter extends RecyclerView.Adapter<NewAdapter.AccountViewHolder> {

    private ItemClickListener mItemClickListener;
    private List<AccountEntity> mModels;
    private ItemLongClickListener mItemLongClickListener;
    private List<AccountEntity> mFilteredModels;
    private String mCurFilter;

    public NewAdapter(List<AccountEntity> models) {
        setModels(models);
    }

    public void setModels(List<AccountEntity> models) {
        if (models != null) {
            if (!TextUtils.isEmpty(mCurFilter)) {
                mFilteredModels = filter(models, mCurFilter);
            } else {
                mFilteredModels = new ArrayList<>(models);
            }
            mModels = models;
        }
        notifyDataSetChanged();
    }

    public AccountEntity getItemEntity(int position) {
        return mFilteredModels.get(position);
    }

    @Override
    public AccountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new AccountViewHolder(view, mItemClickListener, mItemLongClickListener);
    }

    @Override
    public void onBindViewHolder(AccountViewHolder holder, int position) {
        holder.bind(getItemEntity(position));
    }

    @Override
    public int getItemCount() {
        return mFilteredModels != null ? mFilteredModels.size() : 0;
    }

    public interface ItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    public void setOnItemLongClickListener(ItemLongClickListener listener) {
        this.mItemLongClickListener = listener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public AccountEntity removeItem(int position) {
        final AccountEntity model = mFilteredModels.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, AccountEntity model) {
        mFilteredModels.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final AccountEntity model = mFilteredModels.remove(fromPosition);
        mFilteredModels.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void animateTo(List<AccountEntity> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateAdditions(List<AccountEntity> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final AccountEntity model = newModels.get(i);
            if (!mFilteredModels.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateRemovals(List<AccountEntity> newModels) {
        for (int i = mFilteredModels.size() - 1; i >= 0; i--) {
            final AccountEntity model = mFilteredModels.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<AccountEntity> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final AccountEntity model = newModels.get(toPosition);
            final int fromPosition = mFilteredModels.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public void remove(int position) {
        mModels.remove(removeItem(position));
    }

    public void setFilter(String filter) {
        mCurFilter = filter;
        final List<AccountEntity> filteredModelList = filter(mModels, filter);
        animateTo(filteredModelList);
    }

    private List<AccountEntity> filter(List<AccountEntity> models, String query) {
        query = query.toLowerCase();

        final List<AccountEntity> filteredModelList = new ArrayList<>();
        for (AccountEntity model : models) {
            final String text = model.get(AccountEntity.StringType.LABEL).toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    static class AccountViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ItemClickListener mListener;
        ItemLongClickListener mLongListener;
        TextView title;
        TextView subTitle;

        public AccountViewHolder(View itemView, ItemClickListener listener, ItemLongClickListener longListener) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tvTitle);
            subTitle = (TextView) itemView.findViewById(R.id.tvSubTitle);
            mListener = listener;
            mLongListener = longListener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(AccountEntity model) {
            title.setText(model.get(AccountEntity.StringType.LABEL));
            if (!TextUtils.isEmpty(model.get(AccountEntity.StringType.USERNAME))) {
                subTitle.setText(model.get(AccountEntity.StringType.USERNAME));
            } else if (!TextUtils.isEmpty(model.get(AccountEntity.StringType.USERID))) {
                subTitle.setText(model.get(AccountEntity.StringType.USERID));
            } else if (!TextUtils.isEmpty(model.get(AccountEntity.StringType.EMAIL))) {
                subTitle.setText(model.get(AccountEntity.StringType.EMAIL));
            } else if (!TextUtils.isEmpty(model.get(AccountEntity.StringType.PHONE))) {
                subTitle.setText(model.get(AccountEntity.StringType.PHONE));
            } else {
                subTitle.setText("");
            }
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClick(v, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mLongListener != null) {
                mLongListener.onItemLongClick(v, getAdapterPosition());
            }
            return true;
        }
    }
}
