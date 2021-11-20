package com.example.expensemanager.allfragments;

import android.app.AlertDialog;
import android.app.Dialog;
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

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;


public class ExpenseFragment extends Fragment {
    //Console
    private static final String TAG = "Demo";

    //    Firebase Database
    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseDatabase;

    //Reycler View
    private RecyclerView recyclerView;

    //    Text View
    private TextView mExpnseTotalSum;

    //Edt data item
    private EditText edtAmount;
    private EditText edtType;
    private EditText edtNote;

    private Button updateButton;
    private Button deleteButton;

    //global veriable for getting data to hold
    private String type;
    private String note;
    private int amount;
    private String post_key;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View myView = inflater.inflate(R.layout.fragment_expense, container, false);


        //expnse total sum
        mExpnseTotalSum = myView.findViewById(R.id.xExpense_txt_resultf);

        //cheking our user is which use connted
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);
        //now adding recyler view

        recyclerView = (RecyclerView) myView.findViewById(R.id.xRecylerExpenseViewID);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalExpense = 0;
                for (DataSnapshot mysnapshot : snapshot.getChildren()) {
                    Data d = mysnapshot.getValue(Data.class);
                    totalExpense += d.getAmount();

                    String stTotalValue = String.valueOf(totalExpense);
                    mExpnseTotalSum.setText("INR " + stTotalValue + ".00");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return myView;
    }

    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerAdapter<Data, myViewHolderExpense> expenseAdapter = new FirebaseRecyclerAdapter<Data, myViewHolderExpense>(
                Data.class,
                R.layout.expense_recycler_data,
                myViewHolderExpense.class,
                mExpenseDatabase
        ) {
            @Override
            protected void populateViewHolder(myViewHolderExpense myViewHolderExpense, Data data, int i) {
                myViewHolderExpense.setDate(data.getDate());
                myViewHolderExpense.setAmmount(data.getAmount());
                myViewHolderExpense.setNote(data.getNote());
                myViewHolderExpense.setType(data.getType());


                myViewHolderExpense.mmView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        post_key = getRef(i).getKey();
                        type = data.getType();
                        note = data.getNote();
                        amount = data.getAmount();
                        updateDataItem();
                    }
                });

            }
        };

        recyclerView.setAdapter(expenseAdapter);

    }


    public static class myViewHolderExpense extends RecyclerView.ViewHolder {

        View mmView;

        public myViewHolderExpense(@NonNull View itemView) {
            super(itemView);
            mmView = itemView;
        }

        private void setType(String type) {
            TextView mType = mmView.findViewById(R.id.xRecyclerTypeSetExpense);
            mType.setText(type);
        }

        private void setNote(String note) {
            TextView mNote = mmView.findViewById(R.id.xRecyclerNoteSetExpense);
            mNote.setText(note);
        }

        private void setDate(String date) {
            TextView mDate = mmView.findViewById(R.id.xRecyclerDateSetExpense);
            mDate.setText(date);
        }

        private void setAmmount(int amount) {
            TextView mAmount = mmView.findViewById(R.id.xRecyclerAmountSetExpense);
            String stAmount = String.valueOf(amount);
            mAmount.setText(stAmount + ".00");
        }

    }


    public void updateDataItem() {

        //Custom dialog
        AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.update_data_item, null);
        myDialog.setView(myview);

        edtAmount = myview.findViewById(R.id.xAmount_edit);
        edtType = myview.findViewById(R.id.xType_edit);
        edtNote = myview.findViewById(R.id.xNote_edit);

        updateButton = myview.findViewById(R.id.xUpdate_btn);
        deleteButton = myview.findViewById(R.id.xDelete_btn);

        AlertDialog dialog = myDialog.create();

        //Data set
        edtType.setText(type);
        edtType.setSelection(type.length());

        edtNote.setText(note);
        edtNote.setSelection(note.length());

        edtAmount.setText(String.valueOf(amount));
        edtAmount.setSelection(String.valueOf(amount).length());

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                type = edtType.getText().toString().trim();
                note = edtNote.getText().toString().trim();

                String amountm = String.valueOf(amount);
                amountm = edtAmount.getText().toString().trim();


                if (TextUtils.isEmpty(amountm)) {
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

                try {
//                    NumberFormat.getInstance().in(mdamount);
                    double dEAmount = Double.parseDouble(amountm);
                    int iEAmount = (int) Math.round(dEAmount);
                    Log.d(TAG, " a number");

                } catch (Exception e) {
                    Log.d(TAG, "Not a number");
                    edtAmount.setError("Wrong values..");
                    return;
                }


                // First we convert String to double then double to integer
                double dEAmount = Double.parseDouble(amountm);
                int iEAmount = (int) Math.round(dEAmount);





                String mDate = DateFormat.getDateInstance().format(new Date());

                Data modalClass = new Data(iEAmount, type, note, post_key, mDate);
                mExpenseDatabase.child(post_key).setValue(modalClass);


                dialog.dismiss();
            }
        });


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpenseDatabase.child(post_key).removeValue();
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}