package com.india.boa.billonairdata;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class CustomDialog extends BottomSheetDialogFragment {

    TextView title;
    ProgressBar progressBar;
    Button upload;
    FirebaseFirestore db;
    TextInputEditText name_text,company_text,unit_text,weight_text,desc_text,cost_text;
    String code,name,company,unit,weight,description,cost;
    public CustomDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static CustomDialog newInstance(String title) {
        CustomDialog frag = new CustomDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_Dialog_Alert);
        return inflater.inflate(R.layout.custom_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        title = view.findViewById(R.id.textView);
        progressBar = view.findViewById(R.id.progressBar);
        upload = view.findViewById(R.id.upload);
        name_text = view.findViewById(R.id.product_name);
        company_text = view.findViewById(R.id.company_name);
        unit_text = view.findViewById(R.id.product_unit);
        weight_text = view.findViewById(R.id.product_weight);
        desc_text = view.findViewById(R.id.product_description);
        cost_text = view.findViewById(R.id.product_cost);

        progressBar.setVisibility(View.GONE);

        try {
            title.setText(getArguments().getString("title"));
        }catch (Exception e){}

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onclick();
            }
        });

        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public void onclick(){
        code = getArguments().getString("title").trim();
        name = name_text.getText().toString().trim();
        if (name.trim().equals(""))
        {
            Toast.makeText(getContext(),"Name not entered",Toast.LENGTH_SHORT).show();
            return;
        }
        company = company_text.getText().toString().trim();
        if (company.trim().equals(""))
        {
            Toast.makeText(getContext(),"Company not entered",Toast.LENGTH_SHORT).show();
            return;
        }
        unit = unit_text.getText().toString().trim();
        if (unit.trim().equals(""))
        {
            Toast.makeText(getContext(),"Unit not entered",Toast.LENGTH_SHORT).show();
            return;
        }
        weight = weight_text.getText().toString().trim();
        if (weight.trim().equals(""))
        {
            Toast.makeText(getContext(),"Weight not entered",Toast.LENGTH_SHORT).show();
            return;
        }
        description = desc_text.getText().toString().trim();
        cost = cost_text.getText().toString().trim();
        if(cost.trim().equals(""))
        {
            Toast.makeText(getContext(), "Cost not entered", Toast.LENGTH_SHORT).show();
            return;
        }
        db = FirebaseFirestore.getInstance();

        db.collection("products")
                .whereEqualTo("code",code)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            try {
                                if (task.getResult().getDocuments().size() == 0) {
                                    // Create a new user with a first and last name
                                    Map<String, Object> product = new HashMap<>();
                                    product.put("code", code);
                                    product.put("name", name);
                                    product.put("company", company);
                                    product.put("unit",unit);
                                    product.put("weight",weight);
                                    product.put("description",description);
                                    product.put("cost",cost);

                                    progressBar.setVisibility(View.VISIBLE);
                                    // Add a new document with a generated ID
                                    db.collection("products")
                                            .add(product)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Log.d("success", "DocumentSnapshot added with ID: " + documentReference.getId());
                                                    Toast.makeText(getContext(), "Uploaded successfully", Toast.LENGTH_SHORT).show();
                                                    progressBar.setVisibility(View.GONE);
                                                    dismiss();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("fail", "Error adding document", e);
                                                    Toast.makeText(getContext(), "Failed to upload", Toast.LENGTH_SHORT).show();
                                                    progressBar.setVisibility(View.GONE);
                                                    dismiss();
                                                }
                                            });

                                }else {
                                    Toast.makeText(getContext(), "Product Already Added", Toast.LENGTH_SHORT).show();
                                    dismiss();
                                }
                            }catch (Exception e){}
                        }else {
                            Toast.makeText(getContext(), "Try Again", Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),"Try Again !",Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                });
    }

}
