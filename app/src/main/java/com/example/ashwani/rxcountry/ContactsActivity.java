package com.example.ashwani.rxcountry;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.internal.schedulers.SingleScheduler;
import io.reactivex.schedulers.Schedulers;
import ir.mirrajabi.rxcontacts.Contact;
import ir.mirrajabi.rxcontacts.RxContacts;
import rx.functions.Action1;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public class ContactsActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    List<String[]> contactsArray = null;
    ArrayAdapter<String> arrayAdapter;
    Button contactsButton;

    Cursor cursor;

    String name, contactNumber;
    ListView listView;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_contacts);

        listView = (ListView) findViewById(R.id.listview);
        contactsArray = new ArrayList<>();
        contactsButton = (Button) findViewById(R.id.contacts);
        contactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRequiredpermission();


                Observable.fromCallable(new Callable<String>() {

                    @Override
                    public String call() throws Exception {
                        return contacts();
                    }
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(filePath -> Toast.makeText(getApplicationContext(),  " Zipped contact file at "+filePath, Toast.LENGTH_LONG).show());


            }

        });

    }
    public String contacts() {

        ContentResolver reslvr = getContentResolver();
        Cursor cursor = reslvr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        while (cursor.moveToNext()) {

            String str1[] = new String[2];

            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

            Cursor phoneCursor = reslvr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);

            str1[0] = name;
            int cnt = 0;
            String str = "";
            while (phoneCursor.moveToNext()) {
                String phoneNum = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if (cnt > 0)
                    str += ", " + phoneNum;
                else
                    str = phoneNum;
                cnt++;
            }

            str1[1] = str;
            contactsArray.add(str1);
        }

        return make_CSV();
    }
void getRequiredpermission(){
    if (Build.VERSION.SDK_INT >= 23) {
        if (checkPermission()) {
            Log.e("permission", "Permission already granted.");
        } else {
            requestPermission();
        }
    }
}
    String finalResult = "";

    private void fetchContacts() {

        BehaviorSubject<String> subject = BehaviorSubject.create("");
        subject.skip(1).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Toast.makeText(context, "Csv ka zip has been created at " + finalResult, Toast.LENGTH_LONG).show();

            }
        });
        RxContacts.fetch(this)
                .filter(m -> m.getInVisibleGroup() == 1)
                .toSortedList(Contact::compareTo)
                .observeOn(Schedulers.io())
                .subscribeOn(new SingleScheduler())
                .subscribe(contacts -> {
                    String[] strl = new String[2];
                    for (Contact c : contacts
                            ) {
                        Log.d("ContactsActivity", "fetchContacts: " + c.getPhoneNumbers());
                        strl[0] = c.getDisplayName();
                        if (c.getPhoneNumbers().size() > 1)
                            for (String s : c.getPhoneNumbers()) {
                                strl[1] += s + ",";
                            }
                        else strl[1] = c.getPhoneNumbers().toString();

                        contactsArray.add(strl);
                    }
                    System.out.println("something happeninh");
                    finalResult = make_CSV();
                    subject.onNext(finalResult);
                });

    }

    private String make_CSV() {

        String direct = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        File storageDir = new File(direct, "AshwaniContacts");
        storageDir.mkdir();
        String file_Name = "contact.csv";
        String filePath = storageDir.getPath() + File.separator + file_Name;
        File f = new File(filePath);

        if (f.exists()) {
            try {
                FileWriter mFileWriter = new FileWriter(filePath, true);
                CSVWriter writer = new CSVWriter(mFileWriter);
                writer.writeAll(contactsArray);
                writer.close();

            } catch (Exception e) {
                Log.i("Error", e.getMessage());
            }
        } else {
            try {
                CSVWriter writer = new CSVWriter(new FileWriter(filePath));
                writer.writeAll(contactsArray);
                writer.close();
            } catch (Exception e) {
                Log.i("Error@2", e.getMessage() );
            }
        }

        ZipConverter.zip(storageDir.getPath() + File.separator, storageDir.getPath() + File.separator, "contact.zip", false);

        return storageDir.getPath();
    }


    public boolean checkPermission() {

        int StoragePermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int ContactPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS);

        return StoragePermissionResult == PackageManager.PERMISSION_GRANTED &&
                ContactPermissionResult == PackageManager.PERMISSION_GRANTED;

    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(ContactsActivity.this, new String[]
                {
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, PERMISSION_REQUEST_CODE);

    }
}
