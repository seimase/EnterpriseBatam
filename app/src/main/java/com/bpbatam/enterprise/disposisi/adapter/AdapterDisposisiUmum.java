package com.bpbatam.enterprise.disposisi.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bpbatam.AppConstant;
import com.bpbatam.AppController;
import com.bpbatam.enterprise.CC_Activity;
import com.bpbatam.enterprise.CC_DisposActivity;
import com.bpbatam.enterprise.DistribusiActivity;
import com.bpbatam.enterprise.R;
import com.bpbatam.enterprise.disposisi.disposisi_detail;
import com.bpbatam.enterprise.model.Diposisi_List_Folder;
import com.bpbatam.enterprise.model.Disposisi_Attachment;
import com.bpbatam.enterprise.model.Disposisi_Detail_CC;
import com.bpbatam.enterprise.model.ListData;
import com.bpbatam.enterprise.model.Persuratan_List_Folder;
import com.bpbatam.enterprise.model.net.NetworkManager;

import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by User on 9/19/2016.
 */
public class AdapterDisposisiUmum extends  RecyclerView.Adapter<AdapterDisposisiUmum.ViewHolder>{
    Diposisi_List_Folder persuratanListFolder;
    Disposisi_Detail_CC disposisiDetailCc;
    Disposisi_Attachment disposisiAttachment;
    private Context context;

    public AdapterDisposisiUmum(Context context, Diposisi_List_Folder persuratanListFolder, OnDownloadClicked listener) {
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
                R.layout.row_disposisi_umum, null);

        return new ViewHolder(itemView, context, this);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Diposisi_List_Folder.Datum listData = persuratanListFolder.data.get(position);
        //Set text
        holder.txtDate.setText(listData.dispo_date);
        holder.txtTime.setText(listData.read_date);
        holder.txtJudul.setText(listData.title);
        holder.lbl_Attach.setText(listData.title);
        holder.lbl_Size.setText("");


        holder.layoutAttc.setVisibility(View.GONE);
        holder.imgCC.setVisibility(View.GONE);
        final DecimalFormat precision = new DecimalFormat("0.00");
        try{
            Disposisi_Attachment param = new Disposisi_Attachment(AppConstant.HASHID, AppConstant.USER,
                    AppConstant.REQID, Integer.toString(listData.dispo_id));

            Call<Disposisi_Attachment> call = NetworkManager.getNetworkService(context).getDisposisiAttachment(param);
            call.enqueue(new Callback<Disposisi_Attachment>() {
                @Override
                public void onResponse(Call<Disposisi_Attachment> call, Response<Disposisi_Attachment> response) {
                    int code = response.code();
                    if (code == 200){
                        disposisiAttachment = response.body();
                        if (disposisiAttachment.code.equals("00")){
                            for(Disposisi_Attachment.Datum dat : disposisiAttachment.data){
                                listData.attach_link = dat.attc_link;
                                listData.file_size = dat.file_size;
                                listData.file_type = dat.file_type;
                            }

                            if (listData.file_size != null ){
                                String fileName = listData.attach_link.substring(listData.attach_link.lastIndexOf('/') + 1);
                                double dFileSize = Double.parseDouble(listData.file_size) / 1024;
                                holder.lbl_Attach.setText(fileName);
                                holder.lbl_Size.setText("(" + precision.format(dFileSize) + " kb)" );

                                holder.layoutAttc.setVisibility(View.VISIBLE);
                            }
                        }

                    }
                }

                @Override
                public void onFailure(Call<Disposisi_Attachment> call, Throwable t) {

                }
            });
        }catch (Exception e){

        }

        if (listData.flag != null){
            if (listData.flag.equals(AppConstant.SEMUA_PESAN)){
                holder.imgChecklist.setVisibility(View.VISIBLE);
                holder.imgChecklist.setImageDrawable(context.getResources().getDrawable(R.drawable.check32));
                ButtonSelected(holder);
            }else if (listData.flag.equals(AppConstant.PILIH_PESAN)){
                holder.imgChecklist.setVisibility(View.VISIBLE);
                ButtonNotSelected(holder);
                holder.imgChecklist.setImageDrawable(context.getResources().getDrawable(R.drawable.circle));
            }else{
                holder.imgChecklist.setVisibility(View.GONE);
            }
        }

        try {
            AppConstant.HASHID = AppController.getInstance().getHashId(AppConstant.USER);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        try{
            Disposisi_Detail_CC param = new Disposisi_Detail_CC(AppConstant.HASHID,
                    AppConstant.USER,
                    AppConstant.REQID,
                    Integer.toString(listData.dispo_id));
            Call<Disposisi_Detail_CC> call = NetworkManager.getNetworkService(context).getDispoCC(param);
            call.enqueue(new Callback<Disposisi_Detail_CC>() {
                @Override
                public void onResponse(Call<Disposisi_Detail_CC> call, Response<Disposisi_Detail_CC> response) {
                    int code = response.code();
                    if (code == 200){
                        disposisiDetailCc = response.body();
                        int iIndex = 0;
                        if (disposisiDetailCc.code.equals("00")){
                            holder.imgCC.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<Disposisi_Detail_CC> call, Throwable t) {

                }
            });
        }catch (Exception e){

        }


        holder.imgChecklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listData.flag.equals("1")){
                    listData.flag = "2";
                    ButtonSelected(holder);
                    listener.OnDownloadClicked("", false);
                }else{
                    listData.flag = "1";
                    ButtonNotSelected(holder);
                    listener.OnDownloadClicked("", false);
                }
            }
        });

        holder.imgCC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppConstant.EMAIL_ID = listData.dispo_id;
                AppConstant.DISPO_ID = Integer.toString(listData.dispo_id);
                Intent intent = new Intent(context, CC_DisposActivity.class);
                context.startActivity(intent);
            }
        });

        holder.btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppConstant.EMAIL_ID = listData.dispo_id;
                AppConstant.DISPO_ID = Integer.toString(listData.dispo_id);
                Intent intent = new Intent(context, disposisi_detail.class);
                v.getContext().startActivity(intent);
            }
        });

        holder.btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //https://www.dropbox.com/s/jadu92w71vnku3o/Wireframe.pdf?dl=0
                AppConstant.EMAIL_ID = listData.dispo_id;
                AppConstant.DISPO_ID = Integer.toString(listData.dispo_id);
                /*if (listData.file_size != null && !listData.file_size.equals("")){
                    listener.OnDownloadClicked(listData.attach_link, true);
                }*/
                listener.OnDownloadClicked("http://unec.edu.az/application/uploads/2014/12/pdf-sample.pdf", true);
            }
        });
        holder.listData = listData;
    }

    void ButtonSelected(AdapterDisposisiUmum.ViewHolder holder){
        holder.imgChecklist.setImageDrawable(context.getResources().getDrawable(R.drawable.check32));
        holder.imgInfo.setColorFilter(context.getResources().getColor(R.color.white));
        holder.imgDownload.setColorFilter(context.getResources().getColor(R.color.white));
        holder.textDownload.setTextColor(context.getResources().getColor(R.color.white));
        holder.textInfo.setTextColor(context.getResources().getColor(R.color.white));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            holder.btnDownload.setBackground(context.getResources().getDrawable(R.drawable.btn_shape_all_blue));
            holder.btnPrint.setBackground(context.getResources().getDrawable(R.drawable.btn_shape_facebook));
        }
    }

    void ButtonNotSelected(AdapterDisposisiUmum.ViewHolder holder){
        holder.imgChecklist.setImageDrawable(context.getResources().getDrawable(R.drawable.circle));
        holder.imgInfo.setColorFilter(context.getResources().getColor(R.color.grey));
        holder.imgDownload.setColorFilter(context.getResources().getColor(R.color.colorAccept));
        holder.textDownload.setTextColor(context.getResources().getColor(R.color.grey));
        holder.textInfo.setTextColor(context.getResources().getColor(R.color.grey));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            holder.btnDownload.setBackground(context.getResources().getDrawable(R.drawable.btn_shape_all_transparant_blue));
            holder.btnPrint.setBackground(context.getResources().getDrawable(R.drawable.btn_shape_all_transparant_blue));
        }
    }

    @Override
    public int getItemCount() {
        return persuratanListFolder.data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        TextView txtDate,
                txtTime,
                txtJudul,
                lbl_Attach,
                lbl_Size,
                txtStatus
                        ;

        RelativeLayout btnDownload, layoutAttc,
                btnPrint;
        ImageView imgStatus, imgCC, imgChecklist;

        ImageView imgInfo,imgDownload;
        TextView textInfo, textDownload;

        Diposisi_List_Folder.Datum listData;
        public ViewHolder(View itemView,
                          Context context,
                          final AdapterDisposisiUmum mCourseAdapter) {
            super(itemView);
            txtJudul = (TextView)itemView.findViewById(R.id.lbl_Judul);
            layoutAttc = (RelativeLayout) itemView.findViewById(R.id.layout_attachment1);

            imgInfo = (ImageView) itemView.findViewById(R.id.imgInfo);
            imgDownload = (ImageView) itemView.findViewById(R.id.imgDownload);
            textInfo = (TextView)itemView.findViewById(R.id.textInfo);
            textDownload = (TextView)itemView.findViewById(R.id.textDownload);

            txtDate = (TextView)itemView.findViewById(R.id.text_Date);
            txtStatus = (TextView)itemView.findViewById(R.id.text_status);
            txtTime = (TextView)itemView.findViewById(R.id.text_time);
            lbl_Attach = (TextView)itemView.findViewById(R.id.lbl_attach);
            lbl_Size = (TextView)itemView.findViewById(R.id.lbl_size);
            imgStatus = (ImageView) itemView.findViewById(R.id.imageView5);
            btnDownload = (RelativeLayout) itemView.findViewById(R.id.btnDownload);
            btnPrint = (RelativeLayout) itemView.findViewById(R.id.btnPrint);
            imgCC = (ImageView)itemView.findViewById(R.id.imageView5);
            imgChecklist = (ImageView)itemView.findViewById(R.id.imageView15);
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
