package com.example.drushti.views;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.drushti.model.ContactVO;
import com.example.drushti.R;
import com.example.drushti.utils.LocalTextToSpeech;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Shekahar.Shrivastava on 27-Nov-17.
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ContactViewHolder> {

    private List<ContactVO> contactVOList;
    private Context mContext;
    private ArrayList<ContactVO> arraylist;

    public ContactListAdapter(List<ContactVO> contactVOList, Context mContext) {
        this.contactVOList = contactVOList;
        this.mContext = mContext;
        this.arraylist = new ArrayList<ContactVO>();
        this.arraylist.addAll(contactVOList);

    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_contact_view, null);
        ContactViewHolder contactViewHolder = new ContactViewHolder(view);
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        ContactVO contactVO = contactVOList.get(position);
        holder.tvContactName.setText(contactVO.getContactName());
        holder.tvPhoneNumber.setText(contactVO.getContactNumber());
    }

    @Override
    public int getItemCount() {
        return contactVOList.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        ImageView ivContactImage;
        TextView tvContactName;
        TextView tvPhoneNumber;

        public ContactViewHolder(View itemView) {
            super(itemView);
            ivContactImage = (ImageView) itemView.findViewById(R.id.ivContactImage);
            tvContactName = (TextView) itemView.findViewById(R.id.tvContactName);
            tvPhoneNumber = (TextView) itemView.findViewById(R.id.tvPhoneNumber);
        }
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        contactVOList.clear();
        if (charText.length() == 0) {
            contactVOList.addAll(arraylist);
        } else {
            for (ContactVO contactVO : arraylist) {
                if (contactVO.getContactName().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    contactVOList.add(contactVO);
                    LocalTextToSpeech._INSTANCE.speak("found" + contactVO.getContactName() + "swipe to call" , TextToSpeech.SUCCESS, null);
                }
                else {
                    if(contactVOList.isEmpty()) {
                        LocalTextToSpeech._INSTANCE.speak("No result found", TextToSpeech.SUCCESS, null);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

}

