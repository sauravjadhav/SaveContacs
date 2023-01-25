package com.example.savecontacts;

import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText numberEditText;
    private EditText nameEditText;
    private EditText serviceEditText;
    private EditText descriptionEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter();
        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        registerReceiver(new PhoneCallReceiver(), filter);

        // Find views by ID
        numberEditText = findViewById(R.id.number);
        nameEditText = findViewById(R.id.name);
        serviceEditText = findViewById(R.id.service);
        descriptionEditText = findViewById(R.id.description);

        // Set last call number to the numberEditText
        String lastCallNumber = getLastCallNumber();
        numberEditText.setText(lastCallNumber);

        // Set onClickListener for save button
        findViewById(R.id.save).setOnClickListener(view -> saveContact());



    }

    private void saveContact() {
        // Get input values
        String number = numberEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();
        // Check if phone number is not empty
        if (number.isEmpty()) {
            Toast.makeText(this, "Phone number is required", Toast.LENGTH_SHORT).show();
            return;
        }
        // Check if contact name is not empty
        if (name.isEmpty()) {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
            return;
        }
        // Create a new intent to insert a contact
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                .build());

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                .build());
        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String service = serviceEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        String message = "Name: " + name + "\nNumber: " + number + "\nService: " + service + "\nDescription: " + description;
        String phoneNumber = "+919960227855";
        String url = "https://api.whatsapp.com/send?phone=" + phoneNumber + "&text=" + message;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private String getLastCallNumber() {
        String[] projection = new String[]{CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER};
        Uri calls = CallLog.Calls.CONTENT_URI;
        Cursor cursor = getContentResolver().query(calls, projection, null, null, CallLog.Calls.DATE + " DESC LIMIT 1");
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
            int numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            if(nameIndex == -1 || numberIndex == -1) return "";
            String name = cursor.getString(nameIndex);
            String number = cursor.getString(numberIndex);
            cursor.close();
            if(!name.isEmpty()) nameEditText.setText(name);
            return number;
        }
        return "";
    }
}


