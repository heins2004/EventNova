package com.example.eventnova;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OrgAdapter extends RecyclerView.Adapter<OrgAdapter.OrgViewHolder> {

    private List<Organization> orgList;

    public OrgAdapter(List<Organization> orgList) {
        this.orgList = orgList;
    }

    @NonNull
    @Override
    public OrgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_org, parent, false);
        return new OrgViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrgViewHolder holder, int position) {
        Organization org = orgList.get(position);
        holder.tvName.setText(org.getName());
        holder.tvEmail.setText(org.getEmail());
        holder.tvCategory.setText(org.getCategory());
    }

    @Override
    public int getItemCount() {
        return orgList.size();
    }

    static class OrgViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvCategory;

        public OrgViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvAdminOrgName);
            tvEmail = itemView.findViewById(R.id.tvAdminOrgEmail);
            tvCategory = itemView.findViewById(R.id.tvAdminOrgCategory);
        }
    }
}
