package com.bpbatam.enterprise;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bpbatam.AppConstant;
import com.bpbatam.AppController;
import com.bpbatam.enterprise.model.Persuratan_proses;
import com.bpbatam.enterprise.model.net.NetworkManager;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

import java.io.File;
import java.security.NoSuchAlgorithmException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by User on 9/24/2016.
 */
public class PDFViewActivitySimpanKirim extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener {
    TextView txtLabel;
    Integer pageNumber = 0;
    Toolbar toolbar;
    String pdfFileName;
    PDFView pdfView;

    LinearLayout btnSimpan, btnKirim;
    Persuratan_proses persuratanProses;

    ImageView imgView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfview_simpan_kirim);
        imgView = (ImageView)findViewById(R.id.imgView);
        txtLabel = (TextView)findViewById(R.id.textLabel);
        pdfView = (PDFView)findViewById(R.id.pdfView);
        toolbar = (Toolbar)findViewById(R.id.tool_bar);
        btnSimpan = (LinearLayout)findViewById(R.id.btnSimpan);
        btnKirim = (LinearLayout)findViewById(R.id.btnKirim);

        txtLabel.setText(AppConstant.PDF_FILENAME);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.back_24);
        if (AppConstant.PDFVIEW_FROM != null) txtLabel.setText(AppConstant.PDFVIEW_FROM);

        File file = new File(AppConstant.STORAGE_CARD + "/Download/" + AppConstant.PDF_FILENAME);
        if (file.exists()){
            pdfView.fromFile(file)
                    .defaultPage(pageNumber)
                    .onPageChange(this)
                    .enableAnnotationRendering(true)
                    .onLoad(this)
                    .scrollHandle(new DefaultScrollHandle(this))
                    .load();
        }

        String sFileExtension = AppController.getInstance().getFileExtension(AppConstant.PDF_FILENAME);
        if (!sFileExtension.toUpperCase().equals("PDF") && !sFileExtension.toUpperCase().equals("TXT")){
            pdfView.setVisibility(View.GONE);
            imgView.setVisibility(View.VISIBLE);
            Uri uri = Uri.fromFile(new File(AppConstant.STORAGE_CARD + "/Download/" + AppConstant.PDF_FILENAME));

            AppController.getInstance().displayImageSDCard(getBaseContext(), uri, imgView);
        }

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    finish();
            }
        });

        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    void vSimpan(){
        String sPassword = "";
        try {
            sPassword = AppController.getInstance().md5("admin");
            AppConstant.HASHID = AppController.getInstance().getHashId(AppConstant.USER, sPassword);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        try{
            Persuratan_proses param = new Persuratan_proses(AppConstant.HASHID,
                    AppConstant.USER,
                    AppConstant.REQID,
                    sPassword,
                    Integer.toString(AppConstant.EMAIL_ID));

            Call<Persuratan_proses> call = NetworkManager.getNetworkService(this).postDisetejui(param);
            call.enqueue(new Callback<Persuratan_proses>() {
                @Override
                public void onResponse(Call<Persuratan_proses> call, Response<Persuratan_proses> response) {
                    if (response.code() == 200){
                        persuratanProses = response.body();
                        //AppController.getInstance().CustomeDialog(PDFViewActivityDitolakDisetujui.this, persuratanProses.info);
                    }
                }

                @Override
                public void onFailure(Call<Persuratan_proses> call, Throwable t) {

                }
            });
        }catch (Exception e){

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.clear();
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void loadComplete(int nbPages) {

    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
    }
}
