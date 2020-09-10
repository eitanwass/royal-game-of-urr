package com.stadio.urr;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;

public class SendMessageDialog extends Dialog implements View.OnClickListener {

    private Activity activity;
    private EditText messageEditText;
    private ImageView sendButton;

    public SendMessageDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_send_message);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendMessageButton);
        sendButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sendMessageButton:
                String message = messageEditText.getText().toString().trim();
                if (message.equals(""))
                    return;
                MessageDialogListener mActivity = (MessageDialogListener) this.activity;
                mActivity.onReturnValue(message, this);

                break;
        }
    }

    public interface MessageDialogListener {
        public void onReturnValue(String foo, Dialog dialog);
    }
}
