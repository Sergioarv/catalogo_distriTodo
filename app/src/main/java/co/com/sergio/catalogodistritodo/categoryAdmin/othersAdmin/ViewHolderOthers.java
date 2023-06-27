package co.com.sergio.catalogodistritodo.categoryAdmin.othersAdmin;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import co.com.sergio.catalogodistritodo.R;

public class ViewHolderOthers extends RecyclerView.ViewHolder {

    View mView;

    private ViewHolderOthers.ClickListener mClickListener;

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
    public void setOnClickListener(ViewHolderOthers.ClickListener clickListener) {
        mClickListener = clickListener;
    }

    /**
     * Metodo constructor para los click
     */
    public ViewHolderOthers(@NonNull View itemView) {
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

    public void setterOthers(Context context, String image, String name, String price, String description){
        ImageView imageOther;
        TextView nameImgOthersItem;
        TextView priceOthersItem;
        TextView descriptionOthersItem;

        /** Conexion con el item */
        imageOther = mView.findViewById(R.id.imageOthers);
        nameImgOthersItem = mView.findViewById(R.id.nameImgOthersItem);
        priceOthersItem = mView.findViewById(R.id.priceOthersItem);
        descriptionOthersItem = mView.findViewById(R.id.descriptionOthersItem);

        nameImgOthersItem.setText(name);
        priceOthersItem.setText(price);
        descriptionOthersItem.setText(description);

        /** Controlar posibles errores */
        try{
            /** Si la imagen fue traida correctamente */
            Picasso.get().load(image).into(imageOther);
        }catch (Exception e){
            /** Si la imagen no fue traida correctamente */
            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
