package co.com.sergio.catalogodistritodo.categoryAdmin.liquorsAdmin;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import co.com.sergio.catalogodistritodo.R;

public class ViewHolderLiquors extends RecyclerView.ViewHolder {

    View mView;

    private ViewHolderLiquors.ClickListener mClickListener;

    /**
     * Interfaz listener para detectar click
     */
    public interface ClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    /**
     * setea el tipo de click normal o largo
     */
    public void setOnClickListener(ViewHolderLiquors.ClickListener clickListener) {
        mClickListener = clickListener;
    }

    /**
     * Metodo constructor para los click
     */
    public ViewHolderLiquors(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
        /**     Click normal*/
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v, getBindingAdapterPosition());
            }
        });
        /**    Click largo*/
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mClickListener.onItemLongClick(v, getBindingAdapterPosition());
                return true;
            }
        });
    }

    /**
     *  Setear imformacion leida desdes db
     */

    public void setterLiquors(Context context, String image, String name, String price, String description){
        ImageView imageLiquor;
        TextView nameImgLiquorsItem;
        TextView priceLiquorsItem;
        TextView descriptionLiquorsItem;

        /** Conexion con el item */
        imageLiquor = mView.findViewById(R.id.imageLiquor);
        nameImgLiquorsItem = mView.findViewById(R.id.nameImgLiquorsItem);
        priceLiquorsItem = mView.findViewById(R.id.priceLiquorsItem);
        descriptionLiquorsItem = mView.findViewById(R.id.descriptionLiquorsItem);

        nameImgLiquorsItem.setText(name);
        priceLiquorsItem.setText(price);
        descriptionLiquorsItem.setText(description);

        /** Controlar posibles errores */
        try{
            /** Si la imagen fue traida correctamente */
            Picasso.get().load(image).into(imageLiquor);
        }catch (Exception e){
            /** Si la imagen no fue traida correctamente */
            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
