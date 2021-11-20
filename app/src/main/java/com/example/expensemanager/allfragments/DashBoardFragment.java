package com.example.expensemanager.allfragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensemanager.R;
import com.example.expensemanager.model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;


public class DashBoardFragment extends Fragment {

    //Console
    private static final String TAG = "Demo";

    //Floating button
    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;
    //Floating button textView..
    private TextView fab_income_text;
    private TextView fab_expense_text;

    //boolean
    private Boolean isOpen = false;

    //Animation..
    private Animation FadeOpen, FadeClose;

    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;

    //Set Total Income and Total Expense
    private TextView mIncomeSetResult, mExpenseSetResult;

    int Current_balance =0;


    //Recyler View Dashboard
    private RecyclerView mIncomeRecyler;
    private RecyclerView mExpenseRecyler;

    //close activity
    private long pressedTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View myView = inflater.inflate(R.layout.fragment_dash_board, container, false);

        //Connect floating button to layout
        fab_main_btn = myView.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn = myView.findViewById(R.id.xIncome_Ft_btn);
        fab_expense_btn = myView.findViewById(R.id.xExpense_Ft_btn);

        //Firebase..
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);


        // Connect floating text to layout
        fab_income_text = myView.findViewById(R.id.xIncome_ft_text);
        fab_expense_text = myView.findViewById(R.id.xExpense_ft_text);

        // ANimation connect..
        FadeOpen = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_open);
        FadeClose = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_close);

        //Dashboard text view
        mIncomeSetResult = myView.findViewById(R.id.xIncomeSetResult);
        mExpenseSetResult = myView.findViewById(R.id.xExpenseSetResult);



        //        fab_main_btn set On CLickListner
        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addData();

                if (isOpen) {

                    fab_income_btn.startAnimation(FadeClose);
                    fab_expense_btn.startAnimation(FadeClose);
                    fab_income_btn.setClickable(false);
                    fab_expense_btn.setClickable(false);
                    fab_income_text.startAnimation(FadeClose);
                    fab_expense_text.startAnimation(FadeClose);
                    fab_income_text.setClickable(false);
                    fab_expense_text.setClickable(false);
                    isOpen = false;
                } else {

                    fab_income_btn.startAnimation(FadeOpen);
                    fab_expense_btn.startAnimation(FadeOpen);
                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);

                    fab_income_text.startAnimation(FadeOpen);
                    fab_expense_text.startAnimation(FadeOpen);
                    fab_income_text.setClickable(true);
                    fab_expense_text.setClickable(true);
                    isOpen = true;
                }
            }
        });


        //Recyler view for dashboard
        mIncomeRecyler = myView.findViewById(R.id.recylerIncomeDashboard);
        mExpenseRecyler = myView.findViewById(R.id.recylerExpenseDashboard);

        //income reycler for dashboard
        LinearLayoutManager linearLayoutIncome = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        linearLayoutIncome.setStackFromEnd(true);
        linearLayoutIncome.setReverseLayout(true);
        mIncomeRecyler.setHasFixedSize(true);
        mIncomeRecyler.setLayoutManager(linearLayoutIncome);
        //Expense recyler for dashboard
        LinearLayoutManager linearLayoutExepnse = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        linearLayoutExepnse.setStackFromEnd(true);
        linearLayoutExepnse.setReverseLayout(true);
        mExpenseRecyler.setHasFixedSize(true);
        mExpenseRecyler.setLayoutManager(linearLayoutExepnse);


        return myView;
    }


   

    @Override
    public void onStart() {
        super.onStart();
        totalIncomeSum();
        totalExpenseSum();




//        this for income
        FirebaseRecyclerAdapter<Data, IncomedashboardVHolder> dsIncomeAdapter = new FirebaseRecyclerAdapter<Data, IncomedashboardVHolder>(
                Data.class,
                R.layout.dashboard_income,
                DashBoardFragment.IncomedashboardVHolder.class,
                mIncomeDatabase
        ) {
            @Override
            protected void populateViewHolder(IncomedashboardVHolder incomedashboardVHolder, Data data, int i) {

                incomedashboardVHolder.setIncomeType(data.getType());
                incomedashboardVHolder.setIncomeAmount(data.getAmount());
                incomedashboardVHolder.setIncomeDate(data.getDate());
            }
        };

        mIncomeRecyler.setAdapter(dsIncomeAdapter);
//        this is for expense


        FirebaseRecyclerAdapter<Data, ExpensedashboardVHolder> dsExepnseAdapter = new FirebaseRecyclerAdapter<Data, ExpensedashboardVHolder>(
                Data.class,
                R.layout.dashboard_exepnse,
                ExpensedashboardVHolder.class,
                mExpenseDatabase
        ) {
            @Override
            protected void populateViewHolder(ExpensedashboardVHolder expensedashboardVHolder, Data data, int i) {
                expensedashboardVHolder.setExpenseType(data.getType());
                expensedashboardVHolder.setExepnseAmount(data.getAmount());
                expensedashboardVHolder.setExpenseDate(data.getDate());
            }
        };

        mExpenseRecyler.setAdapter(dsExepnseAdapter);
    }

    public void ftAnimation() {
        if (isOpen) {

            fab_income_btn.startAnimation(FadeClose);
            fab_expense_btn.startAnimation(FadeClose);
            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);
            fab_income_text.startAnimation(FadeClose);
            fab_expense_text.startAnimation(FadeClose);
            fab_income_text.setClickable(false);
            fab_expense_text.setClickable(false);
            isOpen = false;
        } else {

            fab_income_btn.startAnimation(FadeOpen);
            fab_expense_btn.startAnimation(FadeOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_income_text.startAnimation(FadeOpen);
            fab_expense_text.startAnimation(FadeOpen);
            fab_income_text.setClickable(true);
            fab_expense_text.setClickable(true);
            isOpen = true;
        }
    }

    private void addData() {
        // Fab Button income..
        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incomeDataInsert();
            }
        });
        // Fab Button expense..
        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expenseDataInsert();
            }
        });
    }


    public void incomeDataInsert() {

        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myviewm = inflater.inflate(R.layout.custom_layout_for_insertdata, null);
        mydialog.setView(myviewm);

        final AlertDialog dialog = mydialog.create();

        dialog.setCancelable(false);

        EditText edtAmount = myviewm.findViewById(R.id.xAmount_edit);
        EditText edtType = myviewm.findViewById(R.id.xType_edit);
        EditText edtNote = myviewm.findViewById(R.id.xNote_edit);

        Button btnSave = myviewm.findViewById(R.id.xSave_btn);
        Button btnCancel = myviewm.findViewById(R.id.xCancel_btn);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = edtType.getText().toString().trim();
                String amount = edtAmount.getText().toString().trim();
                String note = edtNote.getText().toString().trim();


//                Required filed
                if (TextUtils.isEmpty(type)) {
                    edtType.setError("Required Filed..");
                    return;
                }
                if (TextUtils.isEmpty(amount)) {
                    edtAmount.setError("Required Filed..");
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
                    double dIncomeAmount = Double.parseDouble(amount);
                    int iIAmount = (int) Math.round(dIncomeAmount);
                    Log.d(TAG, " a number");

                }
                catch(Exception e)
                {
                    Log.d(TAG, "Not a number");
                    edtAmount.setError("Wrong values..");
                    return;
                }






                // First we convert String to double then double to integer
                double dAmount = Double.parseDouble(amount);
                int iAmount = (int) Math.round(dAmount);


//                push data to in firebase database
                String id = mIncomeDatabase.push().getKey();
                String mData = DateFormat.getDateInstance().format(new Date());

                Data modelData = new Data(iAmount, type, note, id, mData);

                mIncomeDatabase.child(id).setValue(modelData);

                Toast.makeText(getActivity(), "Data ADDED", Toast.LENGTH_SHORT).show();
                ftAnimation();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftAnimation();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void expenseDataInsert() {

        AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myViewmm = inflater.inflate(R.layout.custom_layout_for_insertdata, null);
        myDialog.setView(myViewmm);


        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        EditText edtAmountExp = myViewmm.findViewById(R.id.xAmount_edit);
        EditText edtTypeExp = myViewmm.findViewById(R.id.xType_edit);
        EditText edtNoteExp = myViewmm.findViewById(R.id.xNote_edit);

        Button btnSaveExp = myViewmm.findViewById(R.id.xSave_btn);
        Button btnCancelExp = myViewmm.findViewById(R.id.xCancel_btn);

        btnSaveExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmAmount = edtAmountExp.getText().toString().trim();
                String tmType = edtTypeExp.getText().toString().trim();
                String tmNote = edtNoteExp.getText().toString().trim();


                if (TextUtils.isEmpty(tmAmount)) {
                    edtAmountExp.setError("Required Filed..");
                    return;
                }


                if (TextUtils.isEmpty(tmType)) {
                    edtTypeExp.setError("Required Filed..");
                    return;
                }
                if (TextUtils.isEmpty(tmNote)) {
                    edtNoteExp.setError("Required Filed..");
                    return;
                }

                //just cheking entered number is string or number

                try
                {
//                    NumberFormat.getInstance().in(mdamount);
                    double dIncomeAmount = Double.parseDouble(tmAmount);
                    int iIAmount = (int) Math.round(dIncomeAmount);
                    Log.d(TAG, " a number");

                }
                catch(Exception e)
                {
                    Log.d(TAG, "Not a number");
                    edtAmountExp.setError("Wrong values..");
                    return;
                }






                // First we convert String to double then double to integer
                double dEAmount = Double.parseDouble(tmAmount);
                int iEAmount = (int) Math.round(dEAmount);


                //push data in firebase

                String id = mExpenseDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());
                Data modelData = new Data(iEAmount, tmType, tmNote, id, mDate);
                mExpenseDatabase.child(id).setValue(modelData);

                Toast.makeText(getActivity(), "Data added", Toast.LENGTH_SHORT).show();


                ftAnimation();
                dialog.dismiss();
            }
        });

        btnCancelExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftAnimation();
                dialog.dismiss();
            }
        });

        dialog.show();


    }

    //Calculate total income method
    public void totalIncomeSum() {

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {



            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalIncome = 0;


                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Data d = dataSnapshot.getValue(Data.class);
                    totalIncome += d.getAmount();
                    Current_balance += d.getAmount();
                    String stIncome = String.valueOf(totalIncome);
                    mIncomeSetResult.setText("INR " + stIncome + ".00");


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    //Calculate total income method
    public void totalExpenseSum() {

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalExpense = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Data d = dataSnapshot.getValue(Data.class);
                    totalExpense += d.getAmount();
                    String stExpense = String.valueOf(totalExpense);
                    mExpenseSetResult.setText("INR " + stExpense + ".00");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    //for income data view holder
    public static class IncomedashboardVHolder extends RecyclerView.ViewHolder {

        View myViewVH;

        public IncomedashboardVHolder(@NonNull View itemView) {
            super(itemView);
            myViewVH = itemView;
        }


        public void setIncomeType(String type) {
            TextView mtype = myViewVH.findViewById(R.id.xType_Income_Dashbaord);
            mtype.setText(type);
        }

        public void setIncomeAmount(int amount) {
            TextView mAmount = myViewVH.findViewById(R.id.xAmount_Income_Dashboard);
            String stAmount = String.valueOf(amount);
            mAmount.setText(stAmount);
        }

        public void setIncomeDate(String date) {
            TextView mDate = myViewVH.findViewById(R.id.xDate_Income_Dashboard);
            mDate.setText(date);
        }


    }

    //for expense data view holder
    public static class ExpensedashboardVHolder extends RecyclerView.ViewHolder {

        View mmmView;

        public ExpensedashboardVHolder(@NonNull View itemView) {
            super(itemView);
            mmmView = itemView;
        }

        public void setExpenseType(String type1) {
            TextView mExpenseType = mmmView.findViewById(R.id.xType_Expense_Dashbaord);
            mExpenseType.setText(type1);
        }

        public void setExepnseAmount(int amount1) {
            TextView mExpenseAmount = mmmView.findViewById(R.id.xAmount_Expense_Dashboard);
            String stAmount = String.valueOf(amount1);
            mExpenseAmount.setText(stAmount);
        }

        public void setExpenseDate(String date1) {
            TextView mExDate = mmmView.findViewById(R.id.xDate_Expense_Dashboard);
            mExDate.setText(date1);
        }

    }







}