package com.example.pdfviewer;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class VerticalViewerActivity extends AppCompatActivity {

    private static final int REQUEST_PDF_PICKER = 1;
    private RecyclerView recyclerView;
    private PdfViewerAdapter adapter;
    private List<Uri> pdfUris;
    private List<Bitmap> allPagesBitmaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertical_viewer);

        recyclerView = findViewById(R.id.recycler_view);
        pdfUris = new ArrayList<>();
        allPagesBitmaps = new ArrayList<>();
        adapter = new PdfViewerAdapter(this, allPagesBitmaps);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        pickPDF();
    }

    private void pickPDF() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, "Select PDF"), REQUEST_PDF_PICKER);
        } catch (ActivityNotFoundException ex) {
            // Handle error when no PDF viewer is installed
            Toast.makeText(this, "Please install a PDF viewer", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PDF_PICKER && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedPdfUri = data.getData();
                if (selectedPdfUri != null) {
                    pdfUris.add(selectedPdfUri);
                    loadAllPages(selectedPdfUri); // Load all pages of PDF
                }
            }
        }
    }

    private void loadAllPages(Uri pdfUri) {
        try {
            ParcelFileDescriptor fileDescriptor = getContentResolver().openFileDescriptor(pdfUri, "r");
            if (fileDescriptor != null) {
                PdfRenderer renderer = new PdfRenderer(fileDescriptor);
                int pageCount = renderer.getPageCount();
                for (int i = 0; i < pageCount; i++) {
                    PdfRenderer.Page page = renderer.openPage(i);
                    Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                    bitmap.eraseColor(Color.WHITE);
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                    allPagesBitmaps.add(bitmap);
                    page.close();
                }
                renderer.close();
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
