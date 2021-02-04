package com.example.recyclearview_selectitem_cab;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AdapterItem extends RecyclerView.Adapter<AdapterItem.ViewHolder> {
    private List<String> items;
    private Activity activity;
    private TextView emptyText;
    private ItemViewModel itemViewModel;
    boolean isEnable = false;
    boolean isSelectAll = false;
    private List<String> selectList = new ArrayList<>();
    private Context context;

    public AdapterItem(List<String> items, Activity activity, TextView emptyText, Context context) {
        this.items = items;
        this.activity = activity;
        this.emptyText = emptyText;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterItem.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
        //initialize viewModel
        itemViewModel = ViewModelProviders.of((FragmentActivity) activity).get(ItemViewModel.class);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterItem.ViewHolder holder, int position) {

        holder.itemText.setText(items.get(position));

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!isEnable) {
                    //when Action Mode isn't enable
                    ActionMode.Callback callback = new ActionMode.Callback() {
                        @Override
                        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                            //initialize menu inflater
                            MenuInflater menuInflater = mode.getMenuInflater();
                            menuInflater.inflate(R.menu.action_menu, menu);
                            return true;
                        }

                        @Override
                        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                            //when action mode is prepare
                            isEnable = true;
                            //create method for item selection
                            selectItem(holder);

                            //set observer on getText
                            itemViewModel.getText().observe((LifecycleOwner) activity, new Observer<String>() {
                                @Override
                                public void onChanged(String s) {
                                    //when select item size change
                                    //set text as selected item
                                    mode.setTitle(String.format("%s Selected", s));
                                }
                            });

                            return true;
                        }

                        @Override
                        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                            int menuId = item.getItemId();
                            switch (menuId) {
                                case R.id.action_delete:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setCancelable(false);
                                    builder.setTitle("Are you sure to Delete?");
                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            for (String s : selectList) {
                                                items.remove(s);
                                            }

                                            dialog.dismiss();
                                            mode.finish();

                                            if (items.size() == 0) {
                                                emptyText.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });

                                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        }
                                    });
                                    builder.show();






                                    break;
                                case R.id.action_selectAll:
                                    if (selectList.size() == items.size()) {
                                        isSelectAll = false;
                                        selectList.clear();
                                    } else {
                                        isSelectAll = true;

                                        selectList.clear();
                                        selectList.addAll(items);
                                    }
                                    itemViewModel.setText(String.valueOf(selectList.size()));
                                    notifyDataSetChanged();
                                    break;
                            }

                            return true;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode mode) {
                            isEnable = false;
                            isSelectAll = false;
                            selectList.clear();
                            notifyDataSetChanged();
                        }
                    };

                    //start action mode
                    ((AppCompatActivity) v.getContext()).startActionMode(callback);
                } else {
                    //when action mode is already visible
                    selectItem(holder);
                }
                return true;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEnable) {   //when action mode is visible
                    selectItem(holder);
                } else {
                    //normal click action
                    Toast.makeText(context, "You selected " + items.get(position), Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (isSelectAll) {
            //visible all check and change all item background
            holder.checkItem.setVisibility(View.VISIBLE);
            holder.itemCard.setBackgroundColor(context.getResources().getColor(R.color.selected_item_color));
        } else {
            holder.checkItem.setVisibility(View.GONE);
            holder.itemCard.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private void selectItem(ViewHolder holder) {
        String item = items.get(holder.getAdapterPosition());
        if (holder.checkItem.getVisibility() == View.GONE) {
            holder.checkItem.setVisibility(View.VISIBLE);
            holder.itemCard.setBackgroundColor(context.getResources().getColor(R.color.selected_item_color));
            selectList.add(item);
        } else {
            holder.checkItem.setVisibility(View.GONE);
            holder.itemCard.setBackgroundColor(Color.TRANSPARENT);
            selectList.remove(item);
        }
        itemViewModel.setText(String.valueOf(selectList.size()));  //set selected item size


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout itemCard;
        TextView itemText;
        ImageView checkItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemText = itemView.findViewById(R.id.item_text);
            checkItem = itemView.findViewById(R.id.item_check);
            itemCard = itemView.findViewById(R.id.item_card);
        }
    }
}
