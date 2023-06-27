package co.com.sergio.catalogodistritodo.categoryAdmin.candysAdmin;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import co.com.sergio.catalogodistritodo.R;

public class ViewHolderCandys extends RecyclerView.ViewHolder {
    View mView;

    private ViewHolderCandys.ClickListener mClickListener;

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
    public void setOnClickListener(ViewHolderCandys.ClickListener clickListener) {
        mClickListener = clickListener;
    }

    /**
     * Metodo constructor para los click
     */
    public ViewHolderCandys(@NonNull View itemView) {
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

    public void setterCandys(Context context, String image, String name, String price, String description){
        ImageView imageCandy;
        TextView nameImgCandysItem;
        TextView priceCandysItem;
        TextView descriptionCandysItem;

        /** Conexion con el item */
        imageCandy = mView.findViewById(R.id.imageCandys);
        nameImgCandysItem = mView.findViewById(R.id.nameImgCandysItem);
        priceCandysItem = mView.findViewById(R.id.priceCandysItem);
        descriptionCandysItem = mView.findViewById(R.id.descriptionCandysItem);

        nameImgCandysItem.setText(name);
        priceCandysItem.setText(price);
        descriptionCandysItem.setText(description);

        /** Controlar posibles errores */
        try{
            /** Si la imagen fue traida correctamente */
            Picasso.get().load(image).into(imageCandy);
        }catch (Exception e){
            /** Si la imagen no fue traida correctamente */
            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
