package com.bizsoft.fmcgv2.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bizsoft.fmcgv2.DashboardActivity;
import com.bizsoft.fmcgv2.R;
import com.bizsoft.fmcgv2.dataobject.Product;
import com.bizsoft.fmcgv2.dataobject.Store;

import java.util.ArrayList;

/**
 * Created by shri on 10/8/17.
 */

public class AddedProductAdapter extends BaseAdapter {
    Context context;
    public ArrayList<Product> productList = new ArrayList<Product>();
    LayoutInflater layoutInflater = null;
    public Holder holder;
    public String from;
    double tender;
    double subTotal;
    public double gst;
    double fromCustomer;
    public double grandTotal;




    public AddedProductAdapter(Context context, ArrayList<Product> productList) {

        this.context = context;
        this.layoutInflater = (LayoutInflater.from(context));
        this.productList = productList;
        holder = new Holder();

    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    @Override
    public int getCount() {
        return productList.size();

    }

    @Override
    public Product getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return productList.get(position).getId();
    }

    public class Holder {
        TextView id, name, price;


        TextView calculatedAmount;
        TextView quantity;
        public ImageButton remove;
        TextView resale,reason;
        TextView discountAmount,finalPrice;
        TextView discountLabel;
        TextView productCode;


    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {



        convertView = layoutInflater.inflate(R.layout.added_product_single_item, null);

        final Product product = getItem(position);

        holder.id = (TextView) convertView.findViewById(R.id.sale_id);
        holder.name = (TextView) convertView.findViewById(R.id.customer_name);
        holder.price = (TextView) convertView.findViewById(R.id.price);
        holder.resale = (TextView) convertView.findViewById(R.id.is_resale);
        holder.reason= (TextView) convertView.findViewById(R.id.particulars);

        holder.calculatedAmount = (TextView) convertView.findViewById(R.id.calculated_amount);
        holder.quantity = (TextView) convertView.findViewById(R.id.quantity);
        holder.remove = (ImageButton) convertView.findViewById(R.id.remove);
        holder.discountAmount = (TextView) convertView.findViewById(R.id.discount);
        holder.finalPrice= (TextView) convertView.findViewById(R.id.final_price);
        holder.discountLabel = (TextView) convertView.findViewById(R.id.discount_label);
        holder.productCode= (TextView) convertView.findViewById(R.id.product_code);

        if(!DashboardActivity.currentSaleType.toLowerCase().contains("return"))
        {
            holder.resale.setVisibility(View.GONE);
            holder.reason.setVisibility(View.GONE);
        }
        holder.resale.setText(String.valueOf("Resale: "+product.isResale()));
        holder.reason.setText(String.valueOf("Reason: "+product.getParticulars()));


        if(getFrom()!=null)
        {
            if(getFrom().toLowerCase().contains("preview"))
            {
                holder.remove.setVisibility(View.INVISIBLE);
            }
            else
            {
                holder.remove.setVisibility(View.VISIBLE);
            }
        }
        holder.id.setText(String.valueOf(product.getId()));
        holder.name.setText(String.valueOf(product.getProductName()));
        holder.price.setText(String.valueOf(String.format("%.2f",product.getMRP())));
        holder.productCode.setText(String.valueOf(product.getItemCode()));

        holder.quantity.setText(String.valueOf(product.getQty()+" * "));
        double amount = product.getQty() * product.getMRP();
        holder.calculatedAmount.setText(" = "+String.valueOf(String.format("%.2f",amount)));
        holder.discountAmount.setText(String.valueOf(String.format("%.2f",product.getDiscountAmount())));

        double dp = 100/(amount/product.getDiscountAmount());


        holder.discountLabel.setText("Discount "+String.valueOf(dp) +" % = ");



        if(product.getFinalPrice()==0)
        {
            product.setFinalPrice(product.getQty()*product.getMRP());
            notifyDataSetChanged();
        }

        holder.finalPrice.setText(String.valueOf(String.format("%.2f",product.getFinalPrice())));

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(int i=0;i<productList.size();i++)
                {
                    if(position==i)
                    {
                        productList.get(i).setDiscountAmount(0);
                        productList.get(i).setFinalPrice(0);
                        productList.get(i).setQty(null);
                        productList.remove(i);
                        notifyDataSetChanged();
                    }
                }

            }
        });
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
         tender=0;
         subTotal=0;
         gst=0;
         grandTotal=0;

         fromCustomer = 0;


        if(Store.getInstance().fromCustomer.getText()!= null)
        {
            if(!Store.getInstance().fromCustomer.getText().toString().equals("") || TextUtils.isEmpty(Store.getInstance().fromCustomer.getText().toString()))
            {

                try
                {
                    fromCustomer = Double.parseDouble(Store.getInstance().fromCustomer.getText().toString());
                }
                catch (Exception e)
                {

                }



            }

        }

        System.out.println("PROD subtotal = "+subTotal);
        for(int i=0;i<this.productList.size();i++)
        {

            System.out.println("PROD INDEX = "+i);
            System.out.println("PROD ID = "+productList.get(i).getId());

            subTotal =  subTotal +productList.get(i).getFinalPrice();



        }
        System.out.println("PROD subtotal after notify changes= "+subTotal);
        if(DashboardActivity.isGstValue.toLowerCase().contains("no"))
        {
            gst = 0;
        }
        else
        {
            gst = subTotal*(0.06);
        }

        grandTotal = gst +subTotal;

        if(fromCustomer==0) {
            tender = 0;
        }
        else
        {
            tender =  fromCustomer  - grandTotal;
        }

        Store.getInstance().subTotal.setText(String.valueOf(String.format("%.2f",subTotal)));
        Store.getInstance().GST.setText(String.valueOf( String.format("%.2f", gst)));
        Store.getInstance().grandTotal.setText(String.valueOf( String.format("%.2f", grandTotal)));
        Store.getInstance().tenderAmount.setText(String.valueOf(String.format("%.2f", tender)));





    }
}
