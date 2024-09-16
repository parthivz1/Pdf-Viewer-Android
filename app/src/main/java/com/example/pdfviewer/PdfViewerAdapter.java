package com.example.pdfviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PdfViewerAdapter extends RecyclerView.Adapter<PdfViewerAdapter.ViewHolder> {

    private Context context;
    private List<Bitmap> allPagesBitmaps;

    public PdfViewerAdapter(Context context, List<Bitmap> allPagesBitmaps) {
        this.context = context;
        this.allPagesBitmaps = allPagesBitmaps;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pdf_page, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bitmap bitmap = allPagesBitmaps.get(position);
        holder.pdfImageView.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return allPagesBitmaps.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView pdfImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pdfImageView = itemView.findViewById(R.id.pdfPageImage);
        }
    }
}
