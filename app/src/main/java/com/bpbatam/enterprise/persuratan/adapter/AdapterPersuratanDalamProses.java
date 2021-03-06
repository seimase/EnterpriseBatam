package com.bpbatam.enterprise.persuratan.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bpbatam.AppConstant;
import com.bpbatam.enterprise.R;
import com.bpbatam.enterprise.disposisi.disposisi_detail;
import com.bpbatam.enterprise.model.ListData;
import com.bpbatam.enterprise.model.Persuratan_Attachment;
import com.bpbatam.enterprise.model.Persuratan_Detail;
import com.bpbatam.enterprise.model.Persuratan_List_Folder;
import com.bpbatam.enterprise.model.net.NetworkManager;
import com.bpbatam.enterprise.persuratan.persuratan_detail;

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by User on 9/19/2016.
 */
public class AdapterPersuratanDalamProses extends  RecyclerView.Adapter<AdapterPersuratanDalamProses.ViewHolder>{
    Persuratan_Attachment persuratanAttachment;
    Persuratan_List_Folder persuratanListFolder;
    private Context context;

    public AdapterPersuratanDalamProses(Context context, Persuratan_List_Folder persuratanListFolder, OnDownloadClicked listener) {
        this.context = context;
        this.persuratanListFolder = persuratanListFolder;
        this.listener = listener;
        if (persuratanListFolder == null) {
            throw new IllegalArgumentException("courses ArrayList must not be null");
        }
    }

    public interface OnDownloadClicked {
        public void OnDownloadClicked(String sUrl, boolean bStatus);
    }

    private OnDownloadClicked listener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate layout
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_persuratan_dalamproses, null);

        return new ViewHolder(itemView, context, this);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Persuratan_List_Folder.Datum listData = persuratanListFolder.data.get(position);
        //Set text
        holder.txtDate.setText(listData.mail_date);
        holder.txtTime.setText(listData.read_date);
        holder.txtJudul.setText(listData.title);
        holder.lbl_Attach.setText(listData.title);
        holder.lbl_Size.setText("");


        holder.layoutAttc.setVisibility(View.GONE);
        holder.imgRead.setVisibility(View.GONE);
        if(listData.is_read.equals("N")) holder.imgRead.setVisibility(View.VISIBLE);
        //holder.txtStatus.setText(listData.getAtr2());

        //AppController.getInstance().displayImage(context,listData.getAtr3(), holder.imgCover);
        if (listData.flag != null){
            if (listData.flag.equals(AppConstant.TIDAK_PESAN)){
                holder.imgChecklist.setVisibility(View.GONE);
            }else if (listData.flag.equals(AppConstant.PILIH_PESAN)){
                holder.imgChecklist.setVisibility(View.VISIBLE);
            }else if (listData.flag.equals(AppConstant.SEMUA_PESAN)){
                holder.imgChecklist.setImageDrawable(context.getResources().getDrawable(R.drawable.check32));
                holder.imgChecklist.setVisibility(View.VISIBLE);
            }
        }

        holder.btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppConstant.EMAIL_ID = listData.mail_id;
                Intent intent = new Intent(context, persuratan_detail.class);
                v.getContext().startActivity(intent);
            }
        });

        final DecimalFormat precision = new DecimalFormat("0.00");
        try{
            Persuratan_Attachment param = new Persuratan_Attachment(AppConstant.HASHID, AppConstant.USER,
                    AppConstant.REQID, Integer.toString(listData.mail_id));

            Call<Persuratan_Attachment> call = NetworkManager.getNetworkService(context).getMailAttachment(param);
            call.enqueue(new Callback<Persuratan_Attachment>() {
                @Override
                public void onResponse(Call<Persuratan_Attachment> call, Response<Persuratan_Attachment> response) {
                    int code = response.code();
                    if (code == 200){
                        persuratanAttachment = response.body();
                        if (persuratanAttachment.code.equals("00")){
                            for(Persuratan_Attachment.Datum dat : persuratanAttachment.data){
                                listData.attach_link = dat.attcLink;
                                listData.file_size = dat.fileSize;
                                listData.file_type = dat.fileType;
                            }

                            if (listData.file_size != null ){
                                String fileName = listData.attach_link.substring(listData.attach_link.lastIndexOf('/') + 1);
                                //double dFileSize = Double.parseDouble(listData.file_size) / 1024;
                                holder.lbl_Attach.setText(fileName);
                                //holder.lbl_Size.setText("(" + precision.format(dFileSize) + " kb)" );
                                holder.lbl_Size.setText(listData.file_size);
                                holder.layoutAttc.setVisibility(View.VISIBLE);
                            }
                        }

                    }
                }

                @Override
                public void onFailure(Call<Persuratan_Attachment> call, Throwable t) {

                }
            });
        }catch (Exception e){

        }

        holder.btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppConstant.DISPO_ID = Integer.toString(listData.mail_id);
                AppConstant.EMAIL_ID = listData.mail_id;
                //https://www.dropbox.com/s/jadu92w71vnku3o/Wireframe.pdf?dl=0

                /*if (listData.file_size != null && !listData.file_size.equals("")){

                    listener.OnDownloadClicked(listData.attach_link, true);
                }*/

                listener.OnDownloadClicked("http://unec.edu.az/application/uploads/2014/12/pdf-sample.pdf", true);
            }
        });
        holder.listData = listData;
    }

    @Override
    public int getItemCount() {
        return persuratanListFolder.data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        TextView txtDate,
                txtTime,
                lbl_Attach,
                lbl_Size,
                txtJudul,
                txtStatus;
        ;

        RelativeLayout btnDownload,
                btnPrint, layoutAttc;
        ImageView imgStatus,  imgChecklist, imgRead;

        Persuratan_List_Folder.Datum listData;
        public ViewHolder(View itemView,
                          Context context,
                          final AdapterPersuratanDalamProses mCourseAdapter) {
            super(itemView);
            txtJudul = (TextView)itemView.findViewById(R.id.lbl_Judul);
            layoutAttc = (RelativeLayout) itemView.findViewById(R.id.layout_attachment1);

            txtDate = (TextView)itemView.findViewById(R.id.text_Date);
            txtStatus = (TextView)itemView.findViewById(R.id.text_status);
            txtTime = (TextView)itemView.findViewById(R.id.text_time);
            lbl_Attach = (TextView)itemView.findViewById(R.id.lbl_attach);
            lbl_Size = (TextView)itemView.findViewById(R.id.lbl_size);
            imgStatus = (ImageView) itemView.findViewById(R.id.imageView5);
            btnDownload = (RelativeLayout) itemView.findViewById(R.id.btnDownload);
            btnPrint = (RelativeLayout) itemView.findViewById(R.id.btnPrint);
            imgChecklist = (ImageView)itemView.findViewById(R.id.imageView15);
            imgRead = (ImageView)itemView.findViewById(R.id.imgRead);
        }

        @Override
        public void onClick(View v) {

        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }
}
