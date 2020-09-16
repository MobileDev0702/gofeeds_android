package com.stackrage.gofeds;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class ForgotPwdActivity extends AppCompatActivity {

    private EditText et_email;
    private ImageView iv_back;
    private TextView tv_send_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pwd);

        initComponent();
        onClickBackBtn();
        onClickSendBtn();
    }

    private void initComponent() {
        et_email = findViewById(R.id.et_email);
        iv_back = findViewById(R.id.iv_back_btn);
        tv_send_btn = findViewById(R.id.tv_send_btn);
    }

    private void onClickBackBtn() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void onClickSendBtn() {
        tv_send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
            }
        });
    }

    private void sendEmail() {
        String email = et_email.getText().toString().trim();
        String subject = "Reset Password";
        String message = "Follow this link to reset password.\nhttp://stackrage.com/gofeeds/reset_password.php?email=" + email;
        SendMail sm = new SendMail(this, email, subject, message);
        sm.execute();
    }

}