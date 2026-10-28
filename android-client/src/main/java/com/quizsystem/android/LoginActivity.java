package com.quizsystem.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class LoginActivity extends Activity {

    private QuizDataStore dataStore;
    private EditText etLoginId, etLoginPassword;
    private EditText etRegName, etRegPhone, etRegPassword;
    private LinearLayout layoutSignInForm, layoutRegisterForm;
    private Button btnTabLogin, btnTabRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dataStore = QuizDataStore.getInstance(this);

        // Auto login check
        String currentRole = dataStore.getCurrentUserRole();
        if ("ADMIN".equals(currentRole)) {
            startActivity(new Intent(this, AdminActivity.class));
            finish();
            return;
        } else if ("STUDENT".equals(currentRole)) {
            startActivity(new Intent(this, ExamListActivity.class));
            finish();
            return;
        }

        initViews();
    }

    private void initViews() {
        btnTabLogin = findViewById(R.id.btn_tab_login);
        btnTabRegister = findViewById(R.id.btn_tab_register);
        layoutSignInForm = findViewById(R.id.layout_signin_form);
        layoutRegisterForm = findViewById(R.id.layout_register_form);

        etLoginId = findViewById(R.id.et_login_id);
        etLoginPassword = findViewById(R.id.et_login_password);
        Button btnSubmitLogin = findViewById(R.id.btn_submit_login);

        etRegName = findViewById(R.id.et_reg_name);
        etRegPhone = findViewById(R.id.et_reg_phone);
        etRegPassword = findViewById(R.id.et_reg_password);
        Button btnSubmitRegister = findViewById(R.id.btn_submit_register);

        Button btnFillAdmin = findViewById(R.id.btn_fill_admin);
        Button btnFillStudent = findViewById(R.id.btn_fill_student);

        btnTabLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(true);
            }
        });

        btnTabRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(false);
            }
        });

        btnFillAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etLoginId.setText("Admin");
                etLoginPassword.setText("Admin123");
            }
        });

        btnFillStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etLoginId.setText("9876543210");
                etLoginPassword.setText("student123");
            }
        });

        btnSubmitLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });

        btnSubmitRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRegister();
            }
        });
    }

    private void switchTab(boolean isLogin) {
        if (isLogin) {
            btnTabLogin.setBackgroundColor(getResources().getColor(R.color.primary));
            btnTabLogin.setTextColor(0xFFFFFFFF);
            btnTabRegister.setBackgroundColor(getResources().getColor(R.color.card_dark));
            btnTabRegister.setTextColor(getResources().getColor(R.color.text_muted));
            layoutSignInForm.setVisibility(View.VISIBLE);
            layoutRegisterForm.setVisibility(View.GONE);
        } else {
            btnTabRegister.setBackgroundColor(getResources().getColor(R.color.primary));
            btnTabRegister.setTextColor(0xFFFFFFFF);
            btnTabLogin.setBackgroundColor(getResources().getColor(R.color.card_dark));
            btnTabLogin.setTextColor(getResources().getColor(R.color.text_muted));
            layoutRegisterForm.setVisibility(View.VISIBLE);
            layoutSignInForm.setVisibility(View.GONE);
        }
    }

    private void handleLogin() {
        String identifier = etLoginId.getText().toString().trim();
        String password = etLoginPassword.getText().toString().trim();

        if (identifier.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter your username/phone and password", Toast.LENGTH_SHORT).show();
            return;
        }

        String role = dataStore.authenticate(identifier, password);
        if ("ADMIN".equals(role)) {
            Toast.makeText(this, "🛡️ Welcome, Administrator!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AdminActivity.class));
            finish();
        } else if ("STUDENT".equals(role)) {
            Toast.makeText(this, "👋 Welcome, " + dataStore.getCurrentUserName() + "!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ExamListActivity.class));
            finish();
        } else {
            Toast.makeText(this, "❌ Invalid Credentials!\nUse Admin / Admin123 or register as a student.", Toast.LENGTH_LONG).show();
        }
    }

    private void handleRegister() {
        String name = etRegName.getText().toString().trim();
        String phone = etRegPhone.getText().toString().trim();
        String password = etRegPassword.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill out all registration fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phone.length() != 10) {
            Toast.makeText(this, "Please enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = dataStore.registerStudent(name, phone, password);
        if (success) {
            Toast.makeText(this, "🎉 Registration Successful! Welcome " + name, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ExamListActivity.class));
            finish();
        } else {
            Toast.makeText(this, "User with phone " + phone + " is already registered! Please sign in.", Toast.LENGTH_LONG).show();
            switchTab(true);
            etLoginId.setText(phone);
        }
    }
}
