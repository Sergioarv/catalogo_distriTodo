package co.com.sergio.catalogodistritodo.categoryAdmin.drinksAdmin;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import co.com.sergio.catalogodistritodo.R;

;

public class ViewHolderDrinks extends RecyclerView.ViewHolder {

    View mView;

    private ViewHolderDrinks.ClickListener mClickListener;

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
    public void setOnClickListener(ViewHolderDrinks.ClickListener clickListener) {
        mClickListener = clickListener;
    }

    /**
     * Metodo constructor para los click
     */
    public ViewHolderDrinks(@NonNull View itemView) {
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

    public void setterDrinks(Context context, String image, String name, String price, String description){
        ImageView imageDrink;
        TextView nameImgDrinksItem;
        TextView priceDrinksItem;
        TextView descriptionDrinksItem;

        /** Conexion con el item */
        imageDrink = mView.findViewById(R.id.imageDrinks);
        nameImgDrinksItem = mView.findViewById(R.id.nameImgDrinksItem);
        priceDrinksItem = mView.findViewById(R.id.priceDrinksItem);
        descriptionDrinksItem = mView.findViewById(R.id.descriptionDrinksItem);

        nameImgDrinksItem.setText(name);
        priceDrinksItem.setText(price);
        descriptionDrinksItem.setText(description);

        /** Controlar posibles errores */
        try{
            /** Si la imagen fue traida correctamente */
            Picasso.get().load(image).into(imageDrink);
        }catch (Exception e){
            /** Si la imagen no fue traida correctamente */
            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
