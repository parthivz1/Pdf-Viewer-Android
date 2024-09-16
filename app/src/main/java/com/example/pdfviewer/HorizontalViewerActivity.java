package com.example.pdfviewer;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import java.io.IOException;

public class HorizontalViewerActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private static final int REQUEST_PDF_PICKER = 1;
    private PdfRenderer pdfRenderer;
    private ParcelFileDescriptor parcelFileDescriptor;
    private PdfPagerAdapter pdfPagerAdapter;
    private TextView tvPageIndicator;
    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizontal_viewer);

        viewPager = findViewById(R.id.viewPager);
        tvPageIndicator = findViewById(R.id.pageInfo);
        seekBar = findViewById(R.id.seekBar);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updatePageIndicator(position);
                seekBar.setProgress(position);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    viewPager.setCurrentItem(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        pickPDF();
    }

    private void pickPDF() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, "Select PDF"), REQUEST_PDF_PICKER);
        } catch (ActivityNotFoundException ex) {
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
                    openPdfRenderer(selectedPdfUri);
                }
            }
        }
    }

    private void updatePageIndicator(int position) {
        int totalPages = pdfRenderer.getPageCount();
        tvPageIndicator.setText(String.format("Page %d/%d", position + 1, totalPages));
    }

    private void openPdfRenderer(Uri pdfUri) {
        try {
            parcelFileDescriptor = getContentResolver().openFileDescriptor(pdfUri, "r");
            if (parcelFileDescriptor != null) {
                pdfRenderer = new PdfRenderer(parcelFileDescriptor);
                pdfPagerAdapter = new PdfPagerAdapter(this, pdfRenderer);
                viewPager.setAdapter(pdfPagerAdapter);
                seekBar.setMax(pdfRenderer.getPageCount() - 1);
                updatePageIndicator(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pdfRenderer != null) {
            pdfRenderer.close();
        }
        if (parcelFileDescriptor != null) {
            try {
                parcelFileDescriptor.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}