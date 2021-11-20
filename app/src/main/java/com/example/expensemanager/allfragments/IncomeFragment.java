package com.example.expensemanager.allfragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.expensemanager.R;
import com.example.expensemanager.model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Tag;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.zip.Inflater;


public class IncomeFragment extends Fragment {

    //Console
    private static final String TAG = "Demo";

    //Firebase Database
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;

    //Reycler view
    private RecyclerView recyclerView;

    //Text View
    private TextView mIncomeTotalSum;


    //Update edit text
    private EditText edtAmount;
    private EditText HoleedtAmount;
    private EditText edtType;
    private EditText edtNote;

    //update and delete button
    private Button btnUpdate;
    private Button btnDelete;

    //data item and value
    private String type;
    private String note;
    private int amount;

    private String post_kay;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View myView = inflater.inflate(R.layout.fragment_income, container, false);


        //income total sum
        mIncomeTotalSum = myView.findViewById(R.id.xIncome_txt_resultf);

        //find user id if they are logged
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();

        String uid = mUser.getUid();

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);

        //now adding recyler view
        recyclerView = (RecyclerView) myView.findViewById(R.id.xRecylerIncomeViewID);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalvalue = 0;

                for (DataSnapshot mysnapshot : snapshot.getChildren()) {

                    Data d = mysnapshot.getValue(Data.class);
                    totalvalue += d.getAmount();

                    String stTotalValue = String.valueOf(totalvalue);
                    mIncomeTotalSum.setText("INR " + stTotalValue + ".00");

                }
                ;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        HoleedtAmount = myView.findViewById(R.id.xAmount_edit);


        return myView;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Data, myViewHolderIncome> IncomeAdapter = new FirebaseRecyclerAdapter<Data, myViewHolderIncome>(
                Data.class,
                R.layout.income_recycler_data,
                myViewHolderIncome.class,
                mIncomeDatabase
        ) {
            @Override
            protected void populateViewHolder(myViewHolderIncome myViewHolder, Data data, final int i) {
                myViewHolder.setDate(data.getDate());
                myViewHolder.setType(data.getType());
                myViewHolder.setNote(data.getNote());
                myViewHolder.setAmount(data.getAmount());

                myViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        post_kay = getRef(i).getKey();

                        type = data.getType();
                        note = data.getNote();
                        amount = data.getAmount();
                        UpdateDataItem();


                    }
                });

            }
        };

        recyclerView.setAdapter(IncomeAdapter);
    }


    public static class myViewHolderIncome extends RecyclerView.ViewHolder {

        View mView;

        public myViewHolderIncome(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }


        private void setType(String type) {
            TextView mType = mView.findViewById(R.id.xRecyclerTypeSetIncome);
            mType.setText(type);
        }

        private void setNote(String note) {
            TextView mNote = mView.findViewById(R.id.xRecyclerNoteSetIncome);
            mNote.setText(note);
        }

        private void setDate(String date) {
            TextView mDate = mView.findViewById(R.id.xRecyclerDateSetIncome);
            mDate.setText(date);
        }

        private void setAmount(int amount) {
            TextView mAmount = mView.findViewById(R.id.xRecyclerAmountSetIncome);
            String stAmount = String.valueOf(amount);// convert int to string
            mAmount.setText(stAmount + ".00");
        }

    }


    private void UpdateDataItem() {

        AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.update_data_item, null);
        myDialog.setView(myview);


        edtAmount = myview.findViewById(R.id.xAmount_edit);
        edtType = myview.findViewById(R.id.xType_edit);
        edtNote = myview.findViewById(R.id.xNote_edit);



        //Set data to edit text
        edtType.setText(type);
        edtType.setSelection(type.length());

        edtNote.setText(note);
        edtNote.setSelection(note.length());

        edtAmount.setText(String.valueOf(amount));
        edtAmount.setSelection(String.valueOf(amount).length());


        btnUpdate = myview.findViewById(R.id.xUpdate_btn);
        btnDelete = myview.findViewById(R.id.xDelete_btn);

        AlertDialog dialog = myDialog.create();


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = edtType.getText().toString().trim();
                note = edtNote.getText().toString().trim();

                String mdamount = String.valueOf(edtAmount);
                Log.d(TAG, mdamount);
                mdamount = edtAmount.getText().toString().trim();
                Log.d(TAG, mdamount);

                if (TextUtils.isEmpty(mdamount)) {
                    edtAmount.setError("Required Filed..");
                    return;
                }

                if (TextUtils.isEmpty(type)) {
                    edtType.setError("Required Filed..");
                    return;
                }
                if (TextUtils.isEmpty(note)) {
                    edtNote.setError("Required Filed..");
                    return;
                }


                //just cheking entered number is string or number

                try
                {
//                    NumberFormat.getInstance().in(mdamount);
                    double dIAmount = Double.parseDouble(mdamount);
                    int iIAmount = (int) Math.round(dIAmount);
                    Log.d(TAG, " a number");

                }
                catch(Exception e)
                {
                    Log.d(TAG, "Not a number");
                    edtAmount.setError("Wrong values..");
                    return;
                }


                // First we convert String to double then double to integer
                double dIAmount = Double.parseDouble(mdamount);
                int iIAmount = (int) Math.round(dIAmount);






                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(iIAmount, type, note, post_kay, mDate);
                mIncomeDatabase.child(post_kay).setValue(data);
                dialog.dismiss();

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mIncomeDatabase.child(post_kay).removeValue();
                dialog.dismiss();
            }
        });

        dialog.show();
    }





}