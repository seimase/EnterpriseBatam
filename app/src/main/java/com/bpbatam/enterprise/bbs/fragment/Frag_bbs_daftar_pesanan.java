package com.bpbatam.enterprise.bbs.fragment;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ayz4sci.androidfactory.DownloadProgressView;
import com.bpbatam.AppConstant;
import com.bpbatam.AppController;
import com.bpbatam.enterprise.PDFViewActivity;
import com.bpbatam.enterprise.PDFViewActivity_Edit;
import com.bpbatam.enterprise.R;
import com.bpbatam.enterprise.bbs.BBS_category_list;
import com.bpbatam.enterprise.bbs.adapter.AdapterBBSDaftarPesanan;
import com.bpbatam.enterprise.fragment.frag_bbs;
import com.bpbatam.enterprise.model.BBS_CATEGORY;
import com.bpbatam.enterprise.model.BBS_Insert;
import com.bpbatam.enterprise.model.BBS_LIST;
import com.bpbatam.enterprise.model.BBS_List_ByCategory;
import com.bpbatam.enterprise.model.ListData;
import com.bpbatam.enterprise.model.Persuratan_List_Folder;
import com.bpbatam.enterprise.model.net.NetworkManager;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by User on 9/22/2016.
 */
public class Frag_bbs_daftar_pesanan extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    int CODE_FILE = 45;
    RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    ArrayList<ListData> AryListData;
    ListData listData;

    RelativeLayout rLayoutDownload;
    //RelativeLayout layoutBtnLampiran, layoutAttachment;
    DownloadProgressView downloadProgressView;
    private long downloadID;
    private DownloadManager downloadManager;

    //TextView txtTulisPesan, text_publikasi, txtFileName, txtSize;
    //RelativeLayout layoutHeader, layout_spinner_category;

    ImageView imgCancel, imgSave, imgDelete;

    Spinner spnStatus;

    SimpleAdapter adpGridView;

    BBS_CATEGORY bbs_category;
    BBS_List_ByCategory bbs_list_byCategory, bbs_list_byCategoryFull, bbsListSearch;
    BBS_Insert bbs_insert;
    String[] lstCategory;

    EditText txtJudul, txtIsi;
    //LinearLayout layout_button_kembali;
    String sFile_Size, sFile_Type, sBBS_id, sFile_Path;
    Uri uri;
    int iMin, iMax;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView textKategori;
    RelativeLayout layout_kategori;

    ProgressDialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bbs_daftarpesanan, container, false);
        inflater = inflater;
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        InitControl(view);
        //layoutAttachment.setVisibility(View.GONE);

        AppConstant.sCategoryID = "PDK";
        AppConstant.sCategoryName = "";
        bbs_list_byCategoryFull = new BBS_List_ByCategory();
        FillGrid("PDK");
        /*FillSpinner();
        FillSpinnerCategory();*/
    }

    void InitControl(View v){
        textKategori = (TextView)v.findViewById(R.id.text_kategori);
        layout_kategori = (RelativeLayout)v.findViewById(R.id.layout_kategori);
        layout_kategori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(getActivity(), BBS_category_list.class);
                startActivity(mIntent);
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getActivity().getResources().getColor(R.color.colorSearch),
                getActivity().getResources().getColor(R.color.Green),
                getActivity().getResources().getColor(R.color.b7_orange),
                getActivity().getResources().getColor(R.color.red));

        /*txtFileName = (TextView)v.findViewById(R.id.text_attachment);
        txtSize = (TextView)v.findViewById(R.id.text_size);*/
        imgDelete = (ImageView)v.findViewById(R.id.img_delete);
        //layoutAttachment = (RelativeLayout)v.findViewById(R.id.layout_attachment);
        txtJudul = (EditText)v.findViewById(R.id.text_judul);
        txtIsi = (EditText)v.findViewById(R.id.text_isi);
        //spnBuletin = (Spinner)v.findViewById(R.id.spinner_buletinboard);
        spnStatus = (Spinner)v.findViewById(R.id.spinner_status);
        //spnCategory = (Spinner)v.findViewById(R.id.spinner_caetogory);
        //layout_button_kembali = (LinearLayout) v.findViewById(R.id.layout_button_kembali);
        //layoutBtnLampiran = (RelativeLayout) v.findViewById(R.id.layout_btn_lampiran);
        //text_publikasi = (TextView) v.findViewById(R.id.text_publikasi);
        //txtTulisPesan = (TextView)v.findViewById(R.id.text_tulis_pesan);
       /* layoutHeader = (RelativeLayout)v.findViewById(R.id.layout_header);
        txtTulisPesan.setVisibility(View.VISIBLE);
        layoutHeader.setVisibility(View.GONE);*/

        /*imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sFile_Path = "";
                sFile_Size = "";
                sFile_Type = "";
                layoutAttachment.setVisibility(View.GONE);
            }
        });*/

     /*   txtTulisPesan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtTulisPesan.setVisibility(View.GONE);
                layoutHeader.setVisibility(View.VISIBLE);
            }
        });*/

        /*layout_button_kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtTulisPesan.setVisibility(View.VISIBLE);
                layoutHeader.setVisibility(View.GONE);
            }
        });*/

        /*text_publikasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ValidInputan();
                txtTulisPesan.setVisibility(View.VISIBLE);
                layoutHeader.setVisibility(View.GONE);
            }
        });


        layoutBtnLampiran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sFile_Size = "";
                sFile_Type = "";
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file*//*");
                startActivityForResult(intent, CODE_FILE);
            }
        });*/

        mRecyclerView = (RecyclerView)v.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(v.getContext());
        rLayoutDownload = (RelativeLayout)v.findViewById(R.id.layout_download);
        // use a linear layout manager
        mRecyclerView.setLayoutManager(mLayoutManager);

        downloadProgressView = (DownloadProgressView) v.findViewById(R.id.downloadProgressView);
        downloadManager = (DownloadManager) v.getContext().getSystemService(v.getContext().DOWNLOAD_SERVICE);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(dy > 0) //check for scroll down
                {
                    int visibleItemCount = mLayoutManager.getChildCount();
                    int totalItemCount = mLayoutManager.getItemCount();
                    int pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                    {
                        if (iMax <= totalItemCount){
                            iMin = iMax +1;
                            iMax += 10;
                            FillGridMore(AppConstant.sCategoryID);
                        }
                        //loading = false;
                        Log.v("...", "Last Item Wow !");
                        //Do pagination.. i.e. fetch new data
                    }
                }
            }
        });
    }


    void FillGridMore(String sCategory_id){
/*        AryListData = new ArrayList<>();

        for(int i = 0; i < 10; i++){
            listData = new ListData();
            listData.setAtr1("Attachment " + i);
            listData.setAtr2("(5,88 mb)");
            listData.setAtr3("http://cottonsoft.co.nz/assets/img/our-company-history/history-2011-Paseo.jpg");
            AryListData.add(listData);

        }*/

        try {
            AppConstant.HASHID = AppController.getInstance().getHashId(AppConstant.USER);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        BBS_List_ByCategory paramBBBList = new BBS_List_ByCategory(AppConstant.HASHID,
                AppConstant.USER,
                AppConstant.REQID,
                sCategory_id,
                Integer.toString(iMin),
                Integer.toString(iMax));

        try{
            Call<BBS_List_ByCategory> call = NetworkManager.getNetworkService(getActivity()).getBBS_List_ByCat(paramBBBList);
            call.enqueue(new Callback<BBS_List_ByCategory>() {
                @Override
                public void onResponse(Call<BBS_List_ByCategory> call, Response<BBS_List_ByCategory> response) {
                    int code = response.code();
                    mRecyclerView.setVisibility(View.VISIBLE);
                    if (code == 200){
                        bbs_list_byCategory = response.body();

                        if (bbs_list_byCategory !=null){
                            if (bbs_list_byCategory.code.equals("00")){
                                for (BBS_List_ByCategory.Datum dat : bbs_list_byCategory.data){
                                    bbs_list_byCategoryFull.data.add(dat);
                                }



                                mAdapter.notifyItemInserted(bbs_list_byCategoryFull.data.size() - 1);
                                mAdapter.notifyDataSetChanged();
                                //FillAdapter();
                                /*mRecyclerView.setAdapter(mAdapter);
                                mRecyclerView.invalidate();*/
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<BBS_List_ByCategory> call, Throwable t) {
                    String a = t.getMessage();
                    a = a;
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
            });
        }catch (Exception e){
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            mRecyclerView.setVisibility(View.VISIBLE);
        }

    }

    void FillGrid(String sCategory_id){
        iMin = 1;
        iMax = 10;
/*        AryListData = new ArrayList<>();

        for(int i = 0; i < 10; i++){
            listData = new ListData();
            listData.setAtr1("Attachment " + i);
            listData.setAtr2("(5,88 mb)");
            listData.setAtr3("http://cottonsoft.co.nz/assets/img/our-company-history/history-2011-Paseo.jpg");
            AryListData.add(listData);

        }*/

        try {
            AppConstant.HASHID = AppController.getInstance().getHashId(AppConstant.USER);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        BBS_List_ByCategory paramBBBList = new BBS_List_ByCategory(AppConstant.HASHID,
                AppConstant.USER,
                AppConstant.REQID,
                sCategory_id,
                Integer.toString(iMin),
                Integer.toString(iMax));

        try{
            Call<BBS_List_ByCategory> call = NetworkManager.getNetworkService(getActivity()).getBBS_List_ByCat(paramBBBList);
            call.enqueue(new Callback<BBS_List_ByCategory>() {
                @Override
                public void onResponse(Call<BBS_List_ByCategory> call, Response<BBS_List_ByCategory> response) {
                    int code = response.code();
                    mRecyclerView.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                    if (code == 200){
                        bbs_list_byCategory = response.body();

                        if (bbs_list_byCategory !=null){
                            if (bbs_list_byCategory.code.equals("00")){
                                bbs_list_byCategoryFull = bbs_list_byCategory;
                                FillAdapter();
                                bbsListSearch = new BBS_List_ByCategory();
                                bbsListSearch.data = new ArrayList<BBS_List_ByCategory.Datum>();
                                for (BBS_List_ByCategory.Datum dat : bbs_list_byCategory.data){
                                    if ( dat.title != null){
                                        if ( dat.title.equals("Halo smua"))
                                            bbsListSearch.data.add(dat);
                                    }

                                }

                                //if(bbsListSearch != null) InitRecycle();
                            }else{
                                mRecyclerView.setAdapter(null);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<BBS_List_ByCategory> call, Throwable t) {
                    String a = t.getMessage();
                    a = a;
                    mRecyclerView.setAdapter(null);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }catch (Exception e){
            mRecyclerView.setAdapter(null);
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            mRecyclerView.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
        }

    }

    void FillAdapter(){
        mAdapter = new AdapterBBSDaftarPesanan(getActivity(), bbs_list_byCategoryFull, new AdapterBBSDaftarPesanan.OnDownloadClicked() {
            @Override
            public void OnDownloadClicked(final String sUrl, boolean bStatus) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(sUrl.replace("https://", "http://")));
                AppConstant.PDF_FILENAME = AppController.getInstance().getFileName(sUrl);
                AppConstant.PDF_FILENAME = AppConstant.PDF_FILENAME.replace("%20"," ");

                File file = new File(AppConstant.STORAGE_CARD + "/Download/" + AppConstant.PDF_FILENAME);
                if (file.exists()){
                    Intent intent = new Intent(getActivity(), PDFViewActivity.class);
                    getActivity().startActivity(intent);
                }else{
                    mRecyclerView.setVisibility(View.GONE);
                    rLayoutDownload.setVisibility(View.VISIBLE);

                    request.setTitle(AppConstant.PDF_FILENAME);

                    request.setDescription("DESCRIPTION");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    // request.setDestinationInExternalPublicDir(AppConstant.FOLDER_DOWNLOAD, "DOWNLOAD_FILE_NAME.pdf");

                    File root = new File(AppConstant.STORAGE_CARD + "/Download/");
                    Uri path = Uri.withAppendedPath(Uri.fromFile(root), AppConstant.PDF_FILENAME);
                    request.setDestinationUri(path);

                    downloadID = downloadManager.enqueue(request);
                }

                downloadProgressView.show(downloadID, new DownloadProgressView.DownloadStatusListener() {
                    @Override
                    public void downloadFailed(int reason) {
                        //Action to perform when download fails, reason as returned by DownloadManager.COLUMN_REASON
                        mRecyclerView.setVisibility(View.VISIBLE);
                        rLayoutDownload.setVisibility(View.GONE);
                    }

                    @Override
                    public void downloadSuccessful() {
                        //Action to perform on success
                        mRecyclerView.setVisibility(View.VISIBLE);
                        rLayoutDownload.setVisibility(View.GONE);
                        Intent intent = new Intent(getActivity(), PDFViewActivity.class);
                        getActivity().startActivity(intent);
                    }

                    @Override
                    public void downloadCancelled() {
                        //Action to perform when user press the cancel button
                        mRecyclerView.setVisibility(View.VISIBLE);
                        rLayoutDownload.setVisibility(View.GONE);
                    }
                });
            }
        });
        // set the adapter object to the Recyclerview

        mRecyclerView.setAdapter(mAdapter);
    }

    public synchronized void onActivityResult(final int requestCode, int resultCode, final Intent data) {
        if (requestCode == CODE_FILE){
            if (resultCode == Activity.RESULT_OK) {
                uri = data.getData();
                File f = new File(uri.getPath());
                sFile_Path = f.getPath();
                sFile_Size = Long.toString(f.length());
                sFile_Type = f.getPath().substring(f.getPath().lastIndexOf(".") + 1); // Without dot jpg, png

                DecimalFormat precision = new DecimalFormat("0.00");
                double dFileSize = Double.parseDouble(sFile_Size) / 1024;

                String sFileName = AppController.getInstance().getFileName(f.getPath());
                /*layoutAttachment.setVisibility(View.VISIBLE);
                txtFileName.setText(sFileName);
                txtSize.setText("(" + precision.format(dFileSize) + " kb)");*/
            }
        }

    }

    void sendBBSAttachment(){
        try {
            AppConstant.HASHID = AppController.getInstance().getHashId(AppConstant.USER);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        try{
            BBS_Insert param = new BBS_Insert(AppConstant.HASHID, AppConstant.USER, AppConstant.REQID,
                    sBBS_id,
                    sFile_Path,
                    sFile_Type,
                    sFile_Size
            );

            File file = new File(uri.getPath());
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);

            // MultipartBody.Part is used to send also the actual file name
            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("files", file.getName(), requestFile);

            RequestBody user_id =
                    RequestBody.create(
                            MediaType.parse("multipart/form-data"), AppConstant.USER);

            RequestBody id =
                    RequestBody.create(
                            MediaType.parse("multipart/form-data"), "bbs");


            RequestBody fileKey =
                    RequestBody.create(
                            MediaType.parse("multipart/form-data"), sBBS_id);

            Call<BBS_Insert> call = NetworkManager.getNetworkServiceUpload(getActivity()).postBBSInsertAttachmentOnly(
                    user_id,
                    id,
                    fileKey, body);
            call.enqueue(new Callback<BBS_Insert>() {
                @Override
                public void onResponse(Call<BBS_Insert> call, Response<BBS_Insert> response) {
                    int code = response.code();
                    bbs_insert = null;
                    if(code == 200){
                        bbs_insert = response.body();
                    }
                }

                @Override
                public void onFailure(Call<BBS_Insert> call, Throwable t) {

                }
            });
        }catch (Exception e){
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRefresh() {
        FillGrid(AppConstant.sCategoryID);
    }

    @Override
    public void onResume(){
        super.onResume();
        // put your code here...
        textKategori.setText("PESAN DARI KETUA");
        if (AppConstant.sCategoryName != null && !AppConstant.sCategoryName.equals("")) textKategori.setText(AppConstant.sCategoryName);
        FillGrid(AppConstant.sCategoryID);

    }

    public  void InitRecycle(String sKeyword){
        sKeyword = sKeyword.toLowerCase(Locale.getDefault());
        bbsListSearch = new BBS_List_ByCategory();
        bbsListSearch.data = new ArrayList<BBS_List_ByCategory.Datum>();
        for (BBS_List_ByCategory.Datum dat : bbs_list_byCategoryFull.data){
            if ( dat.title != null){
                if ( dat.title.toLowerCase(Locale.getDefault()).contains(sKeyword) ||
                        dat.name.toLowerCase(Locale.getDefault()).contains(sKeyword))
                    bbsListSearch.data.add(dat);
            }

        }

        mAdapter = new AdapterBBSDaftarPesanan(getActivity(), bbsListSearch, new AdapterBBSDaftarPesanan.OnDownloadClicked() {
            @Override
            public void OnDownloadClicked(final String sUrl, boolean bStatus) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(sUrl.replace("https://", "http://")));
                AppConstant.PDF_FILENAME = AppController.getInstance().getFileName(sUrl);
                AppConstant.PDF_FILENAME = AppConstant.PDF_FILENAME.replace("%20"," ");

                File file = new File(AppConstant.STORAGE_CARD + "/Download/" + AppConstant.PDF_FILENAME);
                if (file.exists()){
                    Intent intent = new Intent(getActivity(), PDFViewActivity.class);
                    getActivity().startActivity(intent);
                }else{
                    mRecyclerView.setVisibility(View.GONE);
                    rLayoutDownload.setVisibility(View.VISIBLE);

                    request.setTitle(AppConstant.PDF_FILENAME);

                    request.setDescription("DESCRIPTION");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    // request.setDestinationInExternalPublicDir(AppConstant.FOLDER_DOWNLOAD, "DOWNLOAD_FILE_NAME.pdf");

                    File root = new File(AppConstant.STORAGE_CARD + "/Download/");
                    Uri path = Uri.withAppendedPath(Uri.fromFile(root), AppConstant.PDF_FILENAME);
                    request.setDestinationUri(path);

                    downloadID = downloadManager.enqueue(request);
                }

                downloadProgressView.show(downloadID, new DownloadProgressView.DownloadStatusListener() {
                    @Override
                    public void downloadFailed(int reason) {
                        //Action to perform when download fails, reason as returned by DownloadManager.COLUMN_REASON
                        mRecyclerView.setVisibility(View.VISIBLE);
                        rLayoutDownload.setVisibility(View.GONE);
                    }

                    @Override
                    public void downloadSuccessful() {
                        //Action to perform on success
                        mRecyclerView.setVisibility(View.VISIBLE);
                        rLayoutDownload.setVisibility(View.GONE);
                        Intent intent = new Intent(getActivity(), PDFViewActivity.class);
                        getActivity().startActivity(intent);
                    }

                    @Override
                    public void downloadCancelled() {
                        //Action to perform when user press the cancel button
                        mRecyclerView.setVisibility(View.VISIBLE);
                        rLayoutDownload.setVisibility(View.GONE);
                    }
                });
            }
        });
        // set the adapter object to the Recyclerview

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView sv =(SearchView) menu.findItem(R.id.action_search).getActionView();
        sv.setQueryHint("Search Berita...");
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                System.out.println(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                System.out.println("tap");
                InitRecycle(newText);
                //FillGrid(newText);
                return false;
            }
        });

    }
}
