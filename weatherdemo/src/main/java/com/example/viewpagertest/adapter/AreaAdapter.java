package com.example.viewpagertest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.viewpagertest.R;
import com.example.viewpagertest.db.AddCounty;
import com.example.viewpagertest.util.LogUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by Administrator on 2017/2/12.
 */

public class AreaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<String> dataList;
    private OnItemClickListener onItemClickListener;
    private boolean isvisiable = false;
    private List<AddCounty> addCounties;
    private static final int FIRSTITEMTYPE = 1;
    private static final int SECONDITEMTYPE = 2;

    public boolean isIsvisiable() {
        return isvisiable;
    }

    public void setIsvisiable(boolean isvisiable) {
        this.isvisiable = isvisiable;
    }

    public AreaAdapter(Context context, List<String> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == FIRSTITEMTYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.area_item, parent, false);
            return new AddCountyList(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.area_item, parent, false);
            return new ItemHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof AddCountyList) {
            ((AddCountyList) holder).deleteBtn.setVisibility(View.VISIBLE);
            ((AddCountyList) holder).countyListNameTextView.setText(dataList.get(position));
            ((AddCountyList) holder).getItemposition(position);
            ((AddCountyList) holder).deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DataSupport.deleteAll(AddCounty.class, "countyName = ?", dataList.get(position));
                    dataList.clear();
                    addCounties = DataSupport.findAll(AddCounty.class);
                    if (null != addCounties && addCounties.size() > 0) {
                        for (AddCounty addCounty : addCounties) {
                            dataList.add(addCounty.getCountyName());
                        }
                    }
                    notifyDataSetChanged();
                    onItemClickListener.onClick(position,view);
                }
            });
        } else if (holder instanceof ItemHolder) {
            ((ItemHolder) holder).setData(dataList.get(position), position);
        }
    }

    @Override
    public int getItemCount() {
        return null != dataList ? dataList.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (isIsvisiable()) {
            return FIRSTITEMTYPE;
        } else
            return SECONDITEMTYPE;
    }

    public class AddCountyList extends RecyclerView.ViewHolder {
        private TextView countyListNameTextView;
        private Button deleteBtn;
        private int mPosition;

        public void getItemposition(int position) {
            this.mPosition = position;
        }

        public AddCountyList(View itemView) {
            super(itemView);
            countyListNameTextView = (TextView) itemView.findViewById(R.id.area_name);
            deleteBtn = (Button) itemView.findViewById(R.id.delete_btn);
        }
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        private TextView areaText;
        private Button deleteBtn;
        private int mPostion;

        public ItemHolder(View itemView) {
            super(itemView);
            areaText = (TextView) itemView.findViewById(R.id.area_name);
            deleteBtn = (Button) itemView.findViewById(R.id.delete_btn);
            initEvent(itemView);
        }

        private void initEvent(View itemView) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onClick(mPostion, v);
                }
            });
        }

        public void setData(String name, int position) {
            areaText.setText(name);
            deleteBtn.setVisibility(View.GONE);
            this.mPostion = position;
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    //创建一个接口
    public interface OnItemClickListener {
        void onClick(int position, View v);
    }
}
